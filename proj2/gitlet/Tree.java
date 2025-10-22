package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.CommonFileOp.getTREES_DIR;
import static gitlet.Utils.join;
import static gitlet.Utils.readObject;
import static gitlet.Utils.serialize;
import static gitlet.Utils.sha1;
import static gitlet.Utils.writeObject;

/**
 * Tree 表示一层目录快照：名称 -> blob 或子 tree 的哈希。
 */
public class Tree implements Serializable {
    /** 名称 -> 文件(blob)哈希（仅存储当前目录层级的文件，不含子目录） */
    private Map<String, String> files = new TreeMap<>();
    /** 名称 -> 子目录(Tree)哈希（子目录名称到对应子 Tree 根哈希） */
    private Map<String, String> dirs = new TreeMap<>();
    /** 当前节点哈希（由内容计算，作为落盘文件名） */
    private String hash;

    /** 返回当前层级的文件映射（不可跨目录）。 */
    public Map<String, String> getFiles() { return files; }
    /** 返回当前层级的子目录映射（目录名 -> 子 Tree 哈希）。 */
    public Map<String, String> getDirs() { return dirs; }
    /** 返回该 Tree 的哈希（由 computeHash 计算得到）。 */
    public String getHash() { return hash; }

    /**
     * 基于当前对象内容（files 与 dirs）计算哈希，并写入字段 hash。
     * 建议在落盘或作为值返回前调用，保证哈希与内容一致。
     */
    public void computeHash() { this.hash = sha1(serialize(this)); }

    /**
     * 将当前 Tree 对象按其哈希写入到 .gitlet/trees 目录下。
     * 若 hash 为空会先 computeHash。
     */
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

    /**
     * 从 trees 目录中读取指定哈希对应的 Tree 对象。
     * @param hash Tree 根哈希
     * @return 反序列化后的 Tree
     */
    private static Tree readTree(String hash) {
        return readObject(join(getTREES_DIR(), hash), Tree.class);
    }

    /**
     * 当 hash 为空或空串时返回“内存中的空 Tree”，否则从磁盘读取。
     * 注意：返回的新空 Tree 尚未写盘也未计算哈希。
     */
    private static Tree loadTreeOrEmpty(String hash) {
        if (hash == null || hash.isEmpty()) return new Tree();
        return readTree(hash);
    }

    /**
     * 为给定 Tree 计算哈希并写盘，返回写盘后的哈希值。
     * @param t 需要写入的 Tree
     * @return 该 Tree 的哈希
     */
    private static String writeTree(Tree t) {
        t.computeHash();
        t.writeInTrees();
        return t.hash;
    }

    /**
     * 读取指定相对路径对应的 blob 哈希，路径使用 '/' 作为分隔符。
     * @param rootHash 根 Tree 的哈希
     * @param path 相对路径（如 "a/b/c.txt" 或 "wug.txt"）
     * @return 目标文件对应的 blob 哈希，不存在时返回 null
     */
    public static String getBlobAt(String rootHash, String path) {
        if (rootHash == null || path == null || path.isEmpty()) return null;
        String[] segs = path.split("/");
        return getBlobAtRec(rootHash, segs, 0);
    }

    /**
     * getBlobAt 的递归实现：逐段向下遍历目录树。
     * @param hash 当前层 Tree 的哈希
     * @param segs 路径分段
     * @param i 当前处理到的分段索引
     * @return 命中的 blob 哈希或 null
     */
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

    /**
     * 在目录树中写入/更新一个文件的路径到指定 blob 哈希，返回新的根哈希。
     * 若中间目录不存在会按需创建；若路径上存在与目录同名的文件，会被替换为目录。
     * @param rootHash 根 Tree 哈希（可为 null 表示空树）
     * @param path 相对路径（使用 '/' 分隔）
     * @param blobHash 目标文件内容对应的 blob 哈希
     * @return 更新后的根 Tree 哈希（若结果为空树则返回空树哈希）
     */
    public static String putFile(String rootHash, String path, String blobHash) {
        String[] segs = path.split("/");
        String newHash = putRec(rootHash, segs, 0, blobHash);
        return newHash == null ? emptyTreeHash() : newHash;
    }

    /**
     * putFile 的递归实现：在当前层复制一份节点并进行持久化写入（持久化数据结构）。
     * @param hash 当前层 Tree 哈希
     * @param segs 路径分段
     * @param i 当前分段索引
     * @param blobHash 目标文件的 blob 哈希
     * @return 新的当前层 Tree 哈希
     */
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

    /**
     * 从树中删除给定路径（文件或整个目录）。
     * 若删除后树为空，返回 null；调用方可据此判断并替换为空树哈希。
     * @param rootHash 根 Tree 哈希
     * @param path 需要删除的相对路径
     * @return 新的根 Tree 哈希（可能为 null 表示空）
     */
    public static String removePath(String rootHash, String path) {
        if (rootHash == null || rootHash.isEmpty()) return null;
        String[] segs = path.split("/");
        return removeRec(rootHash, segs, 0);
    }

    /**
     * removePath 的递归实现：若命中文件则移除文件，若命中目录名则移除整个子树。
     * @param hash 当前层 Tree 哈希
     * @param segs 路径分段
     * @param i 当前分段索引
     * @return 新的当前层 Tree 哈希；若当前层也为空，返回 null
     */
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
