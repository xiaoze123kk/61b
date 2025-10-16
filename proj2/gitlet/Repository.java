package gitlet;

import java.io.File;
import java.util.Map;

import static gitlet.CommonFile.findGitlet;
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
     * @param start
     */
    private void initDir(File start){
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

        File add = join(STAGING_DIR,"add");
        File remove = join(STAGING_DIR,"remove");

        add.mkdir();
        remove.mkdir();
    }



    /**
     * init命令。
     */
    public void init(){
        File start = new File(System.getProperty("user.dir"));
        //看当前目录及其父目录有没有.gitlet
        if (findGitlet(start) != null){
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

        writeContents(join(curRepo,"HEAD"), "master");
        File refsDir = join(curRepo, "refs");
        writeContents(join(refsDir,"master"), origin.getCommitHash());
    }





}
