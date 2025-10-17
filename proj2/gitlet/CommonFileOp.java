package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class CommonFileOp {

    /**
     * 找.gitlet所在的目录,从start开始递归向上寻找
     * 存在返回.gitlet所在的文件目录，否则返回null
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
     * @param target (add文件夹中的目标文件)
     */
    public static void removeFrom(File target) {
        if (!target.exists()) {
            return;
        }
        if (target.isFile()){
            target.delete();
            return;
        }
        if (target.isDirectory()){
            File[] children = target.listFiles();
            if (children != null) {
                for (File child: children){
                    removeFrom(child);
                }
            }
            target.delete();
        }

    }


}
