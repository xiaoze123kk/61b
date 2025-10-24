package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static gitlet.Utils.*;

public class CommonFileOp {

    /**
     * 找.gitlet所在的目录,从start开始递归向上寻找
     * 存在返回.gitlet所在的文件目录，否则返回null
     *
     * @param start
     * @return
     */
    static File findGitlet(File start) {
        if (start == null) {
            return null;
        }
        File g = join(start, ".gitlet");
        if (g.isDirectory()) {
            return g;
        }
        File parent = start.getParentFile();
        return findGitlet(parent);
    }

    /**
     * 找到最近的仓库根。
     *
     * @return
     */
    public static File gitletDirOrDie() {
        File g = findGitlet(new File(System.getProperty("user.dir")));
        if (g == null) {
            System.exit(0);
        }
        return g;
    }

    /**
     * 获取HEAD文件
     *
     * @return
     */
    public static File getHEAD() {
        return join(gitletDirOrDie(), "HEAD");
    }

    /**
     * 获取refs文件目录
     *
     * @return
     */
    public static File getREFS() {
        return join(gitletDirOrDie(), "refs");
    }

    /**
     * 获取commits文件目录
     *
     * @return
     */
    public static File getCOMMITS() {
        return join(gitletDirOrDie(), "commits");
    }

    /**
     * 获取blobs文件目录
     *
     * @return
     */
    public static File getBLOBS() {
        return join(gitletDirOrDie(), "blobs");
    }

    /**
     * 获取staging文件目录
     *
     * @return
     */
    public static File getSTAGING() {
        return join(gitletDirOrDie(), "staging");
    }

    /**
     * 获取staging下的add文件目录
     *
     * @return
     */
    public static File getSTAGINGADD() {
        return join(getSTAGING(), "add");
    }

    /**
     * 获取staging下的remove文件目录
     *
     * @return
     */
    public static File getSTAGINGREMOVE() {
        return join(getSTAGING(), "remove");
    }

    /**
     * 获取trees目录
     *
     * @return
     */
    public static File getTREES_DIR() {
        return join(gitletDirOrDie(), "trees");
    }

    /** 获取 staging/add 的元数据文件 forAdd */
    public static File getFORADD() {
        return join(getSTAGINGADD(), "forAdd");
    }

    /** 获取 staging/remove 的元数据文件 forRemove */
    public static File getFORREMOVE() {
        return join(getSTAGINGREMOVE(), "forRemove");
    }


    /**
     * 找到当前目录下的目标文件，找不到就返回null
     *
     * @param filename
     * @param start
     * @return
     */
    public static File findTargetFile(String filename, File start) {
        File target = join(start, filename);
        if (target.isFile()) {
            return target;
        }
        return null;
    }

    /**
     * 将一个文件（文件夹）复制到另外一个目录底下
     */
    public static void copyTo(File src, File dir) {
        if (src == null || dir == null) {
            return;
        }
        if (!src.exists()) {
            return;
        }
        if (src.exists() && !dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (src.isFile()) {
            byte[] data = readContents(src);
            File dst = join(dir, src.getName());
            writeContents(dst, data);
            return;
        }

        if (src.isDirectory()) {
            File dstDir = join(dir, src.getName());
            if (!dstDir.exists()) {
                dstDir.mkdirs();
            }
            File[] children = src.listFiles();
            if (children != null) {
                for (File child : children) {
                    copyTo(child, dstDir);
                }
            }
        }
    }

    /**
     * 获取当前头指针所指向分支所指的commit
     * 步骤：HEAD文件->取出分支名->在refs中找到当前分支->取出头指针的哈希值
     * ->在commits文件中取出commit
     *
     * @return
     */
    public static Commit getCurCommit() {
        String branchName = readContentsAsString(getHEAD());
        File branch = findTargetFile(branchName, getREFS());
        String headHash = readContentsAsString(branch);
        Commit curCommit = readObject(join(getCOMMITS(), headHash), Commit.class);
        return curCommit;
    }

    /**
     * 将文件(文件夹)从暂存区中的add或者rm目录下删除
     * 补充：发现这个函数很通用，纯粹的删除就完了
     *
     * @param target (add文件夹中的目标文件)
     */
    public static void removeFrom(File target) {
        if (!target.exists()) {
            return;
        }
        if (target.isFile()) {
            target.delete();
            return;
        }
        if (target.isDirectory()) {
            File[] children = target.listFiles();
            if (children != null) {
                for (File child : children) {
                    removeFrom(child);
                }
            }
            target.delete();
        }

    }

    /**
     * 把文件写入blobs文件里
     *
     * @param file
     */
    public static void writeToBlobs(File file) {
        String HashCode = sha1(readContents(file));
        File f = join(getBLOBS(), HashCode);
        if (f.exists()) {
            return;
        }
        byte[] data = readContents(file);
        writeContents(f, data);
    }

    /**
     * 更改当前分支的头指针
     */
    public static void HeadChange(Commit c) {
        String branchName = readContentsAsString(getHEAD()).trim();
        File branchFile = join(getREFS(), branchName);
        writeContents(branchFile, c.getCommitId());
    }

    /**
     * 获取blobs里面的某一个文件
     *
     * @param hashName
     */
    public static File getBlob(String hashName) {
        return join(getBLOBS(), hashName);
    }

    /**
     * 计算项目根目录和传入文件的相对路径
     *
     * @param base
     * @param file
     * @return
     */
    public static String getRelativePath(File base, File file) {
        try {
            // 1️⃣ 转为标准化的绝对路径（去掉 ..、.、符号链接等）
            Path basePath = base.getCanonicalFile().toPath();
            Path filePath = file.getCanonicalFile().toPath();

            // 2️⃣ 计算相对路径
            Path relativePath = basePath.relativize(filePath);

            // 3️⃣ 转为字符串（Path 会自动用系统文件分隔符）
            return relativePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get relative path from "
                    + base + " to " + file, e);
        }


    }

    /**
     * 计算.gitlet的父目录和传入文件的相对路径
     * @param file
     * @return
     */
    public static String getRelativePathWithRoot(File file){
        try {
            // 以 .gitlet 的父目录为基准（工作区根）
            File repoRoot = gitletDirOrDie().getParentFile().getCanonicalFile();
            Path basePath = repoRoot.toPath();
            Path filePath = file.getCanonicalFile().toPath();
            Path relativePath = basePath.relativize(filePath);
            return relativePath.toString().replace('\\', '/');
        } catch (IOException e) {
            throw new RuntimeException("Failed to get relative path from "
                    + ".gitlet's parent" + " to " + file, e);
        }
    }

    /**
     * 获取add暂存区的mapAdd（MapFile对象）
     * @return
     */
    public static MapFile getMapAdd(){
        File forAdd = join(getSTAGINGADD(), "forAdd");
        MapFile mapAdd;
        if (forAdd.exists()) {
            mapAdd = readObject(forAdd, MapFile.class);
        } else {
            mapAdd = new MapFile();
        }
        return mapAdd;
    }

    /**
     * 获取remove暂存区的mapRemove(MapFile对象)
     * @return
     */
    public static MapFile getMapRemove(){
        File forRemove = join(getSTAGINGREMOVE(),"forRemove");
        MapFile mapRemove;
        if (forRemove.exists()){
            mapRemove = readObject(forRemove, MapFile.class);
        }else {
            mapRemove = new MapFile();
        }
        return mapRemove;
    }

    /**
     * 判断当前目录下是否有.gitlet仓库
     */
    public static void repoExist(File start){
        //看当前的目录或父目录是否存在.gitlet仓库
        if (findGitlet(start) == null) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }





}
