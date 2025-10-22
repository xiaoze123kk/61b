package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.CommonFileOp.getTREES_DIR;
import static gitlet.CommonFileOp.gitletDirOrDie;
import static gitlet.Utils.join;
import static gitlet.Utils.readObject;
import static gitlet.Utils.serialize;
import static gitlet.Utils.sha1;
import static gitlet.Utils.writeObject;

/**
 * Tree 表示一层目录快照：名称 -> blob 或子 tree 的哈希。
 */
public class Tree implements Serializable {
    /** 名称 -> 文件(blob)哈希 */
    private Map<String, String> files = new TreeMap<>();
    /** 名称 -> 子目录(Tree)哈希 */
    private Map<String, String> dirs = new TreeMap<>();
    /** 当前节点哈希（由内容计算） */
    private String hash;

    public Map<String, String> getFiles() { return files; }
    public Map<String, String> getDirs() { return dirs; }
    public String getHash() { return hash; }

    public void computeHash() { this.hash = sha1(serialize(this)); }

    public void writeInTrees() {
        if (hash == null) computeHash();
        File out = join(getTREES_DIR(), hash);
        writeObject(out, this);
    }

    /** 生成并返回空树的哈希（若未存在则写入磁盘）。 */
    public static String emptyTreeHash() {
        Tree t = new Tree();
        t.computeHash();
        t.writeInTrees();
        return t.hash;
    }


    private static Tree readTree(String hash) {
        return readObject(join(getTREES_DIR(), hash), Tree.class);
    }

    private static Tree loadTreeOrEmpty(String hash) {
        if (hash == null || hash.isEmpty()) return new Tree();
        return readTree(hash);
    }

    private static String writeTree(Tree t) {
        t.computeHash();
        t.writeInTrees();
        return t.hash;
    }

    /** 读取指定路径对应的 blob 哈希，若不存在返回 null。 */
    public static String getBlobAt(String rootHash, String path) {
        if (rootHash == null || path == null || path.isEmpty()) return null;
        String[] segs = path.split("/");
        return getBlobAtRec(rootHash, segs, 0);
    }

    private static String getBlobAtRec(String hash, String[] segs, int i) {
        if (hash == null) return null;
        Tree t = readTree(hash);
        String name = segs[i];
        boolean last = (i == segs.length - 1);
        if (last) {
            return t.files.get(name);
        } else {
            String child = t.dirs.get(name);
            if (child == null) return null;
            return getBlobAtRec(child, segs, i + 1);
        }
    }

    /** 在树中写入/更新文件 path -> blobHash，返回新的根哈希。 */
    public static String putFile(String rootHash, String path, String blobHash) {
        String[] segs = path.split("/");
        String newHash = putRec(rootHash, segs, 0, blobHash);
        return newHash == null ? emptyTreeHash() : newHash;
    }

    private static String putRec(String hash, String[] segs, int i, String blobHash) {
        Tree t = loadTreeOrEmpty(hash);
        Tree nt = new Tree();
        nt.files.putAll(t.files);
        nt.dirs.putAll(t.dirs);
        String name = segs[i];
        boolean last = (i == segs.length - 1);
        if (last) {
            // 文件名冲突目录时，移除目录
            nt.dirs.remove(name);
            nt.files.put(name, blobHash);
            return writeTree(nt);
        } else {
            // 目录名冲突文件时，移除文件
            nt.files.remove(name);
            String child = nt.dirs.get(name);
            String newChild = putRec(child, segs, i + 1, blobHash);
            nt.dirs.put(name, newChild);
            return writeTree(nt);
        }
    }

    /** 从树中删除 path（文件或整个目录），返回新的根哈希（若树为空返回 null）。 */
    public static String removePath(String rootHash, String path) {
        if (rootHash == null || rootHash.isEmpty()) return null;
        String[] segs = path.split("/");
        return removeRec(rootHash, segs, 0);
    }

    private static String removeRec(String hash, String[] segs, int i) {
        if (hash == null) return null;
        Tree t = readTree(hash);
        Tree nt = new Tree();
        nt.files.putAll(t.files);
        nt.dirs.putAll(t.dirs);
        String name = segs[i];
        boolean last = (i == segs.length - 1);
        if (last) {
            if (nt.files.remove(name) == null) {
                // 若是目录名，删除整个目录
                nt.dirs.remove(name);
            }
        } else {
            String child = nt.dirs.get(name);
            if (child == null) {
                return hash; // 不存在，返回原树
            }
            String newChild = removeRec(child, segs, i + 1);
            if (newChild == null) nt.dirs.remove(name); else nt.dirs.put(name, newChild);
        }
        if (nt.files.isEmpty() && nt.dirs.isEmpty()) return null;
        return writeTree(nt);
    }


}
