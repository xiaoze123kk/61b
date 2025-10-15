package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Repository.COMMITS_DIR;
import static gitlet.Utils.*;

/**
 * 表示一个 gitlet 提交（commit）对象。
 *  TODO：最好在此处简要描述该类在更高层面还做了什么。
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO：在此添加成员变量。
     *
     * 在这里列出 Commit 类的所有成员变量，并在其上方用有用的注释描述该变量代表什么、
     * 以及该变量如何被使用。我们已经为 `message` 提供了一个示例。
     */
    /**
     * 当前commit的哈希值
     */
    private String commitHash;
    /**
     * 本次提交的消息。
     */
    private String message;
    /**
     * 时间戳
     */
    private long timestamp;
    /**
     * 文件名到文件哈希值的映射
     */
    private Map<String, String> blobsMap;
    /**
     * 父指针
     */
    private String parentCommit;
    /**
     * 合并时使用的第二父指针
     */
    private String MparentCommit;

    Commit(String logMessage) {
        blobsMap = new TreeMap<>();
        timestamp = 0;
        message = logMessage;
        parentCommit = null;
        MparentCommit = null;
        commitHash = sha1(serialize(this));
    }

    /**
     * 写入commit
     */
    public void writeIn() {
        File hash_File = join(COMMITS_DIR, commitHash);
        writeObject(hash_File, this);
    }


}
