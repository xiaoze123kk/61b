package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.CommonFileOp.*;
import static gitlet.Utils.*;

/**
 * 表示一个 gitlet 提交（commit）对象。
 */
public class Commit implements Serializable {

    /** 当前commit的哈希值 */
    private String commitId;
    /** 本次提交的消息。 */
    private String message;
    /** 时间戳 */
    private long timestamp;
    /** 根目录树的哈希（Tree 的 root hash） */
    private String treeHash;
    /** 父指针 */
    private String parentCommit;
    /** 合并时使用的第二父指针 */
    private String MparentCommit;

    // 用于 initial commit
    Commit(String logMessage) {
        this.message = logMessage;
        this.timestamp = 0L;
        this.parentCommit = null;
        this.MparentCommit = null;
        this.treeHash = Tree.emptyTreeHash();
        this.commitId = sha1(serialize(this));
    }

    public Commit() { }

    /** 写入commit对象到 .gitlet/commits */
    public void writeInCommits() {
        File gitlet = gitletDirOrDie();
        File COMMIT_DIR = join(gitlet, "commits");
        File hash_File = join(COMMIT_DIR, commitId);
        writeObject(hash_File, this);
    }

    public String getCommitId() { return commitId; }
    public void setCommitId(String commitId) { this.commitId = commitId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    /** 返回根目录树哈希 */
    public String getTreeHash() { return treeHash; }
    public void setTreeHash(String treeHash){this.treeHash = treeHash;}

    /** 获取路径对应的 blob 哈希，不存在返回 null */
    public String getBlobHash(String path) {
        return Tree.getBlobAt(treeHash, path);
    }

    public String getParentCommit() { return parentCommit; }
    public void setParentCommit(String parentCommit) { this.parentCommit = parentCommit; }

    public String getMparentCommit() { return MparentCommit; }
    public void setMparentCommit(String MparentCommit) { this.MparentCommit = MparentCommit; }

    public String toString() {
        return "Commit{commitHash=" + commitId + ", message=" + message + ", timestamp=" + timestamp
                + ", treeHash=" + treeHash + ", parentCommit=" + parentCommit + ", MparentCommit=" + MparentCommit + "}";
    }

    /**
     * 将工作区中的某个“文件路径”加入到当前提交的 Tree 中。
     * 路径使用系统分隔符传入，内部会规范为 '/'
     * 注意：该方法会直接读取工作区文件并写入 blob；若文件不存在则忽略。
     * 更推荐在“提交构建”阶段调用 addFileWithHash，由上层（暂存区）提供 blobHash，避免直接访问文件系统。
     */
    public void addFile(String path){
        if (path == null || path.isEmpty()) return;
        String norm = normalizePath(path);
        File f = new File(norm);
        if (!f.exists() || !f.isFile()) return;
        String blobHash = sha1(readContents(f));
        writeToBlobs(f);
        // 使用规范化后的相对路径写入目录树
        treeHash = Tree.putFile(treeHash, norm, blobHash);
    }

    /**
     * 在不访问文件系统的前提下，把给定路径绑定到已存在的 blob 哈希。
     * 典型用法：由 add 命令计算并写入 blob 后，在构建提交时把暂存区中的 (path, blobHash) 合并进树。
     */
    public void addFileWithHash(String path, String blobHash) {
        if (path == null || path.isEmpty() || blobHash == null || blobHash.isEmpty()) return;
        String norm = normalizePath(path);
        treeHash = Tree.putFile(treeHash, norm, blobHash);
    }

    /**
     * 从当前提交的 Tree 中删除对应的“路径”（文件或整个目录）。
     * 路径使用系统分隔符传入，内部会规范为 '/'
     */
    public void removeFile(String path){
        if (path == null || path.isEmpty()) return;
        String norm = normalizePath(path);
        treeHash = Tree.removePath(treeHash, norm);
    }

    /** 重新计算并设置本次提交的哈希 */
    public void computeMyHash(){
        this.commitId = sha1(serialize(this));
    }

    /** 基于父提交创建新提交（不写盘），treeHash 继承父提交 */
    public static Commit fromParent(Commit parent, String msg) {
        Commit c = new Commit();
        c.message = msg;
        c.timestamp = System.currentTimeMillis();
        c.parentCommit = parent == null ? null : parent.commitId;
        c.MparentCommit = null;
        c.treeHash = parent == null ? Tree.emptyTreeHash() : parent.treeHash;
        c.commitId = null; // 待变更后再计算
        return c;
    }

    /**
     * 假设整棵目录树中没有重名文件：
     * 传入仅“文件名”（不含路径），返回该文件在本提交中的相对路径与 blob 哈希。
     * 若未找到则返回 null。
     * 返回数组格式：[0] = relativePath, [1] = blobHash
     */
    public String[] findPathAndHashByFilename(String filename) {
        if (filename == null || filename.isEmpty()) return null;
        String blobHash = Tree.findBlobHashByName(treeHash, filename);
        if (blobHash == null) return null;
        String relPath = Tree.findPathByName(treeHash, filename);
        if (relPath == null || relPath.isEmpty()) return null;
        return new String[]{relPath, blobHash};
    }

    /** 将任意平台的路径规范为以 '/' 分隔的相对路径，去除开头的 './' 或前导分隔符。 */
    private static String normalizePath(String path) {
        String p = path.replace("\\", "/");
        while (p.startsWith("./")) p = p.substring(2);
        // 合并重复分隔符
        p = p.replaceAll("/+", "/");
        // 去掉前导 '/'
        if (p.startsWith("/")) p = p.substring(1);
        return p;
    }
}
