package gitlet;

import java.io.File;
import java.util.Map;

import static gitlet.CommonFileOp.*;
import static gitlet.Utils.*;


/**
 * 表示一个 gitlet 仓库。
 *
 */
public class Repository {

    /**
     * 当前工作目录。
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * .gitlet 目录。
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * 当前分支
     */
    private Branch curBranch;
    /**
     * 分支map表
     */
    private Map<String, Branch> BranchMap;
    /**
     * 当前头指针
     */
    private Commit HEAD;


    /**
     * 对init文件进行创建。
     *
     * @param start
     */
    private void initDir(File start) {
        /**
         * .gitlet 目录。
         */
        File GITLET_DIR = join(start, ".gitlet");
        /**
         * blobs 目录
         */
        File BLOBS_DIR = join(GITLET_DIR, "blobs");
        /**
         * commits 目录
         */
        File COMMITS_DIR = join(GITLET_DIR, "commits");
        /**
         * refs 目录
         */
        File REFS_DIR = join(GITLET_DIR, "refs");
        /**
         * staging 目录
         */
        File STAGING_DIR = join(GITLET_DIR, "staging");
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        REFS_DIR.mkdir();
        STAGING_DIR.mkdir();

        File add = join(STAGING_DIR, "add");
        File remove = join(STAGING_DIR, "remove");

        add.mkdir();
        remove.mkdir();
    }


    /**
     * init命令。
     */
    public void init() {
        File start = new File(System.getProperty("user.dir"));
        //看当前目录及其父目录有没有.gitlet
        if (findGitlet(start) != null) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        //创建目录
        initDir(start);
        //开始做初始化
        File curRepo = join(start, ".gitlet");
        Commit origin = new Commit("initial commit");

        // 用动态的仓库根写入所有文件
        File commitsDir = join(curRepo, "commits");
        writeObject(join(commitsDir, origin.getCommitHash()), origin);

        writeContents(join(curRepo, "HEAD"), "master");
        File refsDir = join(curRepo, "refs");
        writeContents(join(refsDir, "master"), origin.getCommitHash());
    }

    /**
     * add命令
     */
    public void add(String filename) {
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        if (findGitlet(start) == null) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        //ok,在仓库下，开始找文件,得在当前的文件目录下找
        File target = findTargetFile(filename, start);
        if (target == null) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        //找到了文件，将这个文件的副本添加到暂存区
        //文件的当前版本跟当前commit的版本一致，则不添加，并且如果此文件若是在
        //staging area中，将其移除
        String targetHash = sha1(readContents(target));
        Commit curCommit = getCurCommit();
        String oldHash = curCommit.getBlobsMap().get(filename);
        //判断版本一致
        if (oldHash != null && oldHash.equals(targetHash)) {
            removeFrom(join(getSTAGING(), "add", target.getName()));
            System.exit(0);
        }
        //如果该文件在待删除状态，将该文件从rm中移除
        removeFrom(join(getSTAGING(), "remove", target.getName()));

        File dir = join(getSTAGING(), "add");
        copyTo(target, dir);
    }

    /**
     * commit命令
     * @param msg
     */
    public void commit(String msg){
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        if (findGitlet(start) == null) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        // 暂存区均为空时，不允许提交
        File[] addFiles = getSTAGINGADD().listFiles();
        File[] rmFiles = getSTAGINGREMOVE().listFiles();
        boolean addEmpty = (addFiles == null || addFiles.length == 0);
        boolean rmEmpty = (rmFiles == null || rmFiles.length == 0);
        if (addEmpty && rmEmpty){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit newCommit =  Commit.fromParent(getCurCommit(), msg);
        //找add文件夹里的内容并加入
        newCommit.addFile(getSTAGINGADD());
        //找remove文件夹里的内容并删除
        newCommit.removeFile(getSTAGINGREMOVE());
        //将commit写进commits
        newCommit.computeMyHash();
        writeObject(join(getCOMMITS(), newCommit.getCommitHash()),newCommit);
        //更改头指针朝向
        HeadChange(newCommit);
        //删除暂存区里的文件
        removeFrom(getSTAGINGADD());
        removeFrom(getSTAGINGREMOVE());

    }



}
