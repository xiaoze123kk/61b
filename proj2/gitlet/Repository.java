package gitlet;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static gitlet.CommonFileOp.*;
import static gitlet.Utils.*;


/**
 * 表示一个 gitlet 仓库。
 *
 */
public class Repository {

    /** 当前工作目录。 */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** .gitlet 目录。 */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** 当前分支 */
    private Branch curBranch;
    /** 分支map表 */
    private java.util.Map<String, Branch> BranchMap;
    /** 当前头指针 */
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
        /**
         * trees 目录
         */
        File TREES_DIR = join(GITLET_DIR, "trees");
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        REFS_DIR.mkdir();
        STAGING_DIR.mkdir();
        TREES_DIR.mkdir();

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
        writeObject(join(commitsDir, origin.getCommitId()), origin);

        writeContents(join(curRepo, "HEAD"), "master");
        File refsDir = join(curRepo, "refs");
        writeContents(join(refsDir, "master"), origin.getCommitId());
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
        String oldHash = curCommit.getBlobHash(filename);
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
        writeObject(join(getCOMMITS(), newCommit.getCommitId()),newCommit);
        //更改头指针朝向
        HeadChange(newCommit);
        //删除暂存区里的文件
        removeFrom(getSTAGINGADD());
        removeFrom(getSTAGINGREMOVE());
    }

    /**
     * remove命令
     */
    public void remove(String filename){
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        if (findGitlet(start) == null) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        //判断是否在add暂存区
        {
            File target = join(getSTAGINGADD(), filename);
            if (target.exists()) {
                removeFrom(target);
                return;
            }
        }
        //判断是否被当前的commit跟踪
        Commit curCommit = getCurCommit();
        if (curCommit.getBlobHash(filename) != null){
            // 在 staging/remove 放入以文件名命名的删除标记，提交时按名称移除跟踪
            File rmMarker = join(getSTAGINGREMOVE(), filename);
            File parentDir = rmMarker.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            writeContents(rmMarker, "");
            // 工作区存在该文件则删除
            File target = findTargetFile(filename, start);
            if (target != null){
                removeFrom(target);
            }
            return;
        }
        //既不在暂存区内，也不在当前commit中被跟踪
        System.out.println("No reason to remove the file.");
    }

    /**
     * log命令
     */
    public void log(){
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        if (findGitlet(start) == null) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        Commit cur = getCurCommit();
        //循环打印信息
        while (true){
            System.out.println("===");
            System.out.println("commit" + " " + cur.getCommitId());
            //如果存在合并的父提交,加一行
            if (cur.getMparentCommit()!=null){
                System.out.println("Merge: " + cur.getParentCommit().substring(0,7) + " " + cur.getMparentCommit().substring(0,7));
            }
            Date date = new Date(cur.getTimestamp());
            String formatted = String.format(Locale.US, "Date: %ta %tb %td %tT %tY %tz", date, date, date, date, date, date);
            System.out.println(formatted);
            System.out.println(cur.getMessage());
            System.out.println();
            //打印完后向前遍历
            if (cur.getParentCommit() == null){
                break;
            }
            cur = readObject(join(getCOMMITS(),cur.getParentCommit()),Commit.class);
        }

    }

    /**
     * global-log命令
     */
    public void global_log(){
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        if (findGitlet(start) == null) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        List<String> filenames = plainFilenamesIn(getCOMMITS());
        //遍历打印log信息
        for (int i = 0; i < filenames.size(); i++) {
            Commit cur = readObject(join(getCOMMITS(), filenames.get(i)), Commit.class);
            System.out.println("===");
            System.out.println("commit" + " " + cur.getCommitId());
            //如果存在合并的父提交,加一行
            if (cur.getMparentCommit()!=null){
                System.out.println("Merge: " + cur.getParentCommit().substring(0,7) + " " + cur.getMparentCommit().substring(0,7));
            }
            Date date = new Date(cur.getTimestamp());
            String formatted = String.format(Locale.US, "Date: %ta %tb %td %tT %tY %tz", date, date, date, date, date, date);
            System.out.println(formatted);
            System.out.println(cur.getMessage());
            System.out.println();
        }

    }

    /**
     * find命令
     * @param msg
     */
    public void find(String msg){
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        if (findGitlet(start) == null) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        boolean hasId = false;
        List<String> filenames = plainFilenamesIn(getCOMMITS());
        for (int i = 0; i < filenames.size(); i++) {
            Commit cur = readObject(join(getCOMMITS(), filenames.get(i)), Commit.class);
            if (cur.getMessage().equals(msg)){
                System.out.println(cur.getCommitId());
                hasId = true;
            }
        }
        //如果没有这样的提交信息，输出错误信息
        if (!hasId){
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }

    }

}
