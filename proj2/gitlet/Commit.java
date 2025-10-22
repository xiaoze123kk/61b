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
     * 将暂存区文件加入到当前提交的 Tree 中（file 指向暂存区 add 目录或其中的文件）。
     */
    public void addFile(File file){
        if (file == null || !file.exists()) return;
        if (file.isFile()){
            String blobHash = sha1(readContents(file));
            writeToBlobs(file);
            // 暂存区中只有文件名一层，直接使用文件名作为路径
            treeHash = Tree.putFile(treeHash, file.getName(), blobHash);
            return;
        }
        if (file.isDirectory()){
            File[] children = file.listFiles();
            if (children!= null){
                for (File child : children){
                    addFile(child);
                }
            }
        }
    }

    /**
     * 把对应文件从当前提交的 Tree 中删除（file 指向暂存区 remove 目录或其中的文件）。
     */
    public void removeFile(File file){
        if (file == null || !file.exists()) return;
        if (file.isFile()){
            treeHash = Tree.removePath(treeHash, file.getName());
            return;
        }
        if (file.isDirectory()){
            File[] children = file.listFiles();
            if (children!= null) {
                for (File child : children) {
                    removeFile(child);
                }
            }
        }
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
}
