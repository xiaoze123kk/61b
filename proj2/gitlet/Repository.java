package gitlet;

import java.io.File;
import java.util.*;

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
        repoExist(start);
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
        // 确保 blob 已写入 .gitlet/blobs
        writeToBlobs(target);
        Commit curCommit = getCurCommit();
        String relative = getRelativePathWithRoot(target);
        String oldHash = curCommit.getBlobHash(relative);
        //取出暂存区的map
        MapFile mapAdd;
        MapFile mapRemove;
        File forAdd = join(getSTAGINGADD(), "forAdd");
        File forRemove = join(getSTAGINGREMOVE(), "forRemove");
        mapAdd = getMapAdd();
        //如果处于待删除状态
        if (forRemove.exists()) {
            mapRemove = readObject(forRemove, MapFile.class);
            mapRemove.delFile(filename);
            removeFrom(join(getSTAGINGREMOVE(), filename));
            writeObject(forRemove, mapRemove);
            return;
        }
        //版本一致就将该文件从暂存区删除
        if (targetHash.equals(oldHash)) {
            mapAdd.map().remove(filename);
            removeFrom(join(getSTAGINGADD(), filename));
            // 持久化更新后的 forAdd
            writeObject(forAdd, mapAdd);
            return;
        }
        //将更新后的mapAdd重新写入
        mapAdd.putFile(filename, relative, targetHash);
        writeObject(forAdd, mapAdd);
        //目标文件拷贝到暂存区
        copyTo(target, getSTAGINGADD());
    }

    /**
     * commit命令
     *
     * @param msg
     */
    public void commit(String msg) {
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        repoExist(start);
        // 读取暂存区元数据（forAdd/forRemove），不存在则视为为空
        MapFile addMeta = getMapAdd();
        MapFile rmMeta = getMapRemove();
        boolean addEmpty = addMeta.map().isEmpty();
        boolean rmEmpty = rmMeta.map().isEmpty();
        if (addEmpty && rmEmpty) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit base = getCurCommit();
        Commit newCommit = Commit.fromParent(base, msg);
        // 应用 add 暂存：按路径与 blobHash 写入，避免依赖工作区
        for (String k : addMeta.map().keySet()) {
            MapFile.Pair p = addMeta.map().get(k);
            // 确保 blobs 中存在该内容
            File blobFile = join(getBLOBS(), p.fileHash);
            if (!blobFile.exists()) {
                File stagedFile = join(getSTAGINGADD(), k);
                if (stagedFile.exists() && stagedFile.isFile()) {
                    byte[] content = readContents(stagedFile);
                    writeContents(blobFile, content);
                }
            }
            newCommit.addFileWithHash(p.relativePath, p.fileHash);
        }
        // 应用 remove 暂存：按路径从树中移除
        for (String k : rmMeta.map().keySet()) {
            MapFile.Pair p = rmMeta.map().get(k);
            newCommit.removeFile(p.relativePath);
        }
        // 正确设置父提交为当前 HEAD
        newCommit.setParentCommit(base.getCommitId());
        newCommit.computeMyHash();
        writeObject(join(getCOMMITS(), newCommit.getCommitId()), newCommit);
        //更改头指针朝向
        HeadChange(newCommit);
        // 清空暂存区内容并保留目录结构
        removeFrom(getSTAGINGADD());
        removeFrom(getSTAGINGREMOVE());
        // 重新创建 staging/add 与 staging/remove 目录
        getSTAGINGADD().mkdirs();
        getSTAGINGREMOVE().mkdirs();
    }

    /**
     * remove命令
     */
    public void remove(String filename) {
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        repoExist(start);
        //判断是否在add暂存区,在的话解除跟踪
        File targetInAdd = join(getSTAGINGADD(), filename);

        if (targetInAdd.exists()) {
            removeFrom(targetInAdd);
            MapFile mAdd = getMapAdd();
            mAdd.delFile(filename);
            // 持久化更新后的 forAdd
            writeObject(getFORADD(), mAdd);
            return;
        }
        File target = join(start, filename);
        String relative = getRelativePathWithRoot(target);
        Commit curCommit = getCurCommit();
        //如果该文件被当前的commit跟踪，则将其暂存以待删除
        String blobHash = curCommit.getBlobHash(relative);
        if (blobHash != null) {
            File wait4rm = join(getBLOBS(), blobHash);
            copyTo(wait4rm, getSTAGINGREMOVE());
            File forRemoveMeta = join(getSTAGINGREMOVE(), "forRemove");
            MapFile rmMeta = getMapRemove();
            rmMeta.putFile(filename, relative, blobHash);
            writeObject(forRemoveMeta, rmMeta);
            // 工作区存在该文件则删除
            target = findTargetFile(filename, start);
            if (target != null) {
                removeFrom(target);
            }
            return;
        }
        //既不在暂存区内，也不在当前commit中被跟踪
        System.out.println("No reason to remove the file.");
    }

    /**
     * 打印log信息
     */
    private void msgPrint(Commit cur) {
        System.out.println("===");
        System.out.println("commit" + " " + cur.getCommitId());
        //如果存在合并的父提交,加一行
        if (cur.getMparentCommit() != null) {
            System.out.println("Merge: " + cur.getParentCommit().substring(0, 7) + " " + cur.getMparentCommit().substring(0, 7));
        }
        Date date = new Date(cur.getTimestamp());
        String formatted = String.format(Locale.US, "Date: %ta %tb %td %tT %tY %tz", date, date, date, date, date, date);
        System.out.println(formatted);
        System.out.println(cur.getMessage());
        System.out.println();
    }


    /**
     * log命令
     */
    public void log() {
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        repoExist(start);

        Commit cur = getCurCommit();
        //循环打印信息
        while (true) {
            msgPrint(cur);
            //打印完后向前遍历
            if (cur.getParentCommit() == null) {
                break;
            }
            cur = readObject(join(getCOMMITS(), cur.getParentCommit()), Commit.class);
        }

    }

    /**
     * global-log命令
     */
    public void global_log() {
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        repoExist(start);

        List<String> filenames = plainFilenamesIn(getCOMMITS());
        //遍历打印log信息
        for (int i = 0; i < filenames.size(); i++) {
            Commit cur = readObject(join(getCOMMITS(), filenames.get(i)), Commit.class);
            msgPrint(cur);
        }

    }

    /**
     * find命令
     *
     * @param msg
     */
    public void find(String msg) {
        File start = new File(System.getProperty("user.dir"));
        //看当前的目录或父目录是否存在.gitlet仓库
        repoExist(start);
        boolean hasId = false;
        List<String> filenames = plainFilenamesIn(getCOMMITS());
        for (int i = 0; i < filenames.size(); i++) {
            Commit cur = readObject(join(getCOMMITS(), filenames.get(i)), Commit.class);
            if (cur.getMessage().equals(msg)) {
                System.out.println(cur.getCommitId());
                hasId = true;
            }
        }
        //如果没有这样的提交信息，输出错误信息
        if (!hasId) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }

    }

    /**
     * status命令
     */
    public void status() {
        File start = new File(System.getProperty("user.dir"));
        //看是否在当前仓库下
        repoExist(start);
        List<String> branchNames = plainFilenamesIn(getREFS());
        String curBranchName = readContentsAsString(getHEAD());
        System.out.println("=== Branches ===");
        if (branchNames != null) {
            for (String branchname : branchNames) {
                if (curBranchName.equals(branchname)) {
                    System.out.println("*" + branchname);
                } else {
                    System.out.println(branchname);
                }
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        // 从元数据 forAdd 读取已暂存的文件名并排序输出
        MapFile addMetaForStatus = getMapAdd();
        if (addMetaForStatus != null && !addMetaForStatus.map().isEmpty()) {
            List<String> addNames = new ArrayList<>(addMetaForStatus.map().keySet());
            Collections.sort(addNames);
            for (String name : addNames) {
                System.out.println(name);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        // 从元数据 forRemove 读取待删除文件名并排序输出
        MapFile rmMetaForStatus = getMapRemove();
        if (rmMetaForStatus != null && !rmMetaForStatus.map().isEmpty()) {
            List<String> rmNames = new ArrayList<>(rmMetaForStatus.map().keySet());
            Collections.sort(rmNames);
            for (String name : rmNames) {
                System.out.println(name);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");//先鸽一下这个部分
        System.out.println();
        System.out.println("=== Untracked Files ===");//这个部分也鸽一下
        System.out.println();
    }

    /**
     * 将文件在头提交中存在的版本放入工作目录中
     *
     * @param filename
     */
    public void checkout1(String filename) {
        File start = new File(System.getProperty("user.dir"));
        repoExist(start);
        Commit cur = getCurCommit();
        String[] s = cur.findPathAndHashByFilename(filename);
        if (s == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        // 将 blob 内容写回工作区同名文件（扁平结构）
        byte[] data = readContents(join(getBLOBS(), s[1]));
        writeContents(join(CWD, filename), data);
    }

    /**
     * 判断指定的提交id是否存在
     */
    public Commit isCommitExist(String commitId){
        File[] commits = getCOMMITS().listFiles();
        Commit targetCommit = null;
        if (commits != null) {
            int matches = 0;
            for (File f : commits) {
                String commitName = f.getName();
                if (commitName.startsWith(commitId)) {
                    matches++;
                    targetCommit = readObject(f, Commit.class);
                }
            }
            if (matches != 1) { // 0 或 >1 都视为不存在
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        } else {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return targetCommit;
    }




    /**
     * 获取指定 ID 提交的文件版本，并将其放入工作目录
     *
     * @param commitId
     * @param filename
     */
    public void checkout2(String commitId, String filename) {
        File start = new File(System.getProperty("user.dir"));
        repoExist(start);
        Commit targetCommit = isCommitExist(commitId);
        String[] s = targetCommit.findPathAndHashByFilename(filename);
        if (s == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        // 写回指定提交的该文件内容
        byte[] data = readContents(join(getBLOBS(), s[1]));
        writeContents(join(CWD, filename), data);
    }

    /**
     * 覆写
     * @param branchName
     */
    public boolean overWrite (String branchName){
        // 当前与目标提交
        File targetBranchRef = join(getREFS(), branchName);

        Commit curCommit = getCurCommit();
        String targetCommitId = readContentsAsString(targetBranchRef);
        Commit targetCommit = readObject(join(getCOMMITS(), targetCommitId), Commit.class);
        Tree curTree = readObject(join(getTREES_DIR(), curCommit.getTreeHash()), Tree.class);
        Tree targetTree = readObject(join(getTREES_DIR(), targetCommit.getTreeHash()), Tree.class);
        // 未跟踪文件冲突：CWD 中存在文件 X，且 X 不被当前提交跟踪，但在目标提交中存在，将被覆盖
        File[] files = CWD.listFiles();
        if (files != null) {
            for (File f : files) {
                String name = f.getName();
                boolean trackedInCur = curTree.getFiles().containsKey(name);
                boolean existsInTarget = targetTree.getFiles().containsKey(name);
                if (!trackedInCur && existsInTarget) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    return false;
                }
            }
        }
        // 删除当前分支跟踪但目标分支不包含的文件
        for (String f : curTree.getFiles().keySet()) {
            if (!targetTree.getFiles().containsKey(f)) {
                File wf = join(CWD, f);
                if (wf.exists()) wf.delete();
            }
        }
        // 写入目标分支的文件内容（覆盖）
        for (Map.Entry<String, String> e : targetTree.getFiles().entrySet()) {
            String name = e.getKey();
            String blob = e.getValue();
            byte[] data = readContents(join(getBLOBS(), blob));
            writeContents(join(CWD, name), data);
        }
        return true;
    }


    /**
     * 迁出当前目标分支的文件到工作目录
     *
     * @param branchName
     */
    public boolean checkout3(String branchName) {
        File start = new File(System.getProperty("user.dir"));
        repoExist(start);
        File targetBranchRef = join(getREFS(), branchName);
        if (!targetBranchRef.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String curBranchName = readContentsAsString(getHEAD());
        if (curBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        overWrite(branchName);
        // 更新 HEAD 指向目标分支名
        writeContents(getHEAD(), branchName);
        // 清空暂存区
        clearStagingArea();
        return true;
    }

    /**
     * 创建新分支：refs/<name> 指向当前提交。
     */
    public void branch(String name) {
        File start = new File(System.getProperty("user.dir"));
        repoExist(start);
        if (name == null || name.isEmpty()) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        File refFile = join(getREFS(), name);
        if (refFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Commit cur = getCurCommit();
        writeContents(refFile, cur.getCommitId());
    }

    /**
     *rm-branch命令
     * @param branchname
     */
    public void rmbranch(String branchname){
        File start = new File(System.getProperty("user.dir"));
        repoExist(start);
        File targetBranch = join(getREFS(),branchname);
        if (!targetBranch.exists()){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String curBranch = readContentsAsString(getHEAD());
        if (curBranch.equals(branchname)){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        //要是分支存在
        targetBranch.delete();

    }

    /**
     * reset命令
     */
    public void reset(String commitId){
        File start = new File(System.getProperty("user.dir"));
        repoExist(start);
        Commit c = isCommitExist(commitId);
        //已知存在这个commitId,把当前分支的头节点先保存（便于失败后重新写入）
        //然后指向当前commit
        File curBranch = join(getREFS(),readContentsAsString(getHEAD()));
        String curCommitId = readContentsAsString(curBranch);
        Commit backUp = readObject(join(getCOMMITS(),curCommitId), Commit.class);
        writeContents(curBranch, c.getCommitId());
        if (!overWrite(curBranch.getName())){
            writeContents(curBranch, backUp.getCommitId());
        }
    }



}
