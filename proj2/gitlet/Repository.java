package gitlet;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.*;


/** 表示一个 gitlet 仓库。
 *
 */
public class Repository {

    /** 当前工作目录。 */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** .gitlet 目录。 */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** blobs 目录 */
    public static final File BLOBS_DIR = join(GITLET_DIR,"blobs");
    /** commits 目录 */
    public static final File COMMITS_DIR = join(GITLET_DIR,"commits");
    /** refs 目录*/
    public static final File REFS_DIR = join(GITLET_DIR,"refs");
    /** 当前分支 */
    private Branch curBranch;
    /**分支map表*/
    private Map<String , Branch> BranchMap;
    /**当前头指针*/
    private Commit HEAD;

    /**
     * 查找父目录是否存在.gitlet文件夹
     * @return
     */
    private boolean findGitlet(String name , File dir){
        //当前目录是根目录
        if (dir == null){return false;}
        //当前目录是.gitlet目录
        if (dir.getName().equals(name)){return true;}
        File parent = dir.getParentFile();
        //没找到，递归寻找
        return findGitlet(name,parent);
    }

    /**
     * 创建Init文件夹。
     */
    private void makeInitFile(){
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        REFS_DIR.mkdir();
    }


    /** 执行init命令。*/
    public void Init(){
        //先判断是否已经Init过了，即是找当前目录或者当前目录的父目录是否有.gitlet文件夹
        if (!findGitlet(".gitlet",CWD)){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        //未init过
        makeInitFile();
        Commit originCommit = new Commit("initial commit");
        HEAD = originCommit;
        String HEADHASH = sha1(serialize(HEAD));
        curBranch = new Branch("master",HEADHASH);
        BranchMap = new TreeMap<>();
        BranchMap.put("master",curBranch);
        //写入文件
        originCommit.writeIn();
        File HEAD_FILE = join(GITLET_DIR,"HEAD");
        File master_FILE = join(REFS_DIR,"master");
        //保存哈希值，分支名
        writeObject(master_FILE,HEADHASH);
        writeObject(HEAD_FILE,"master");
    }




}
