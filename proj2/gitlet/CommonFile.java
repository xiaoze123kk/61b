package gitlet;

import java.io.File;
import java.lang.reflect.Field;

import static gitlet.Utils.*;

public class CommonFile {

    /**
     * 找.gitlet所在的目录,从start开始递归向上寻找
     * @param start
     * @return
     */
    static File findGitlet(File start){
        if (start == null){
            return null;
        }
        File g = join(start,".gitlet");
        if (g.isDirectory()){
            return g;
        }
        File parent = start.getParentFile();
        return findGitlet(parent);
    }

    /**
     * 找到最近的仓库根。
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
     * @return
     */
    public static File getHEAD(){
        return join(gitletDirOrDie(),"HEAD");
    }

    /**
     * 获取refs文件目录
     * @return
     */
    public static File getREFS(){
        return join(gitletDirOrDie(),"refs");
    }

    /**
     * 获取commits文件目录
     * @return
     */
    public static File getCOMMITS(){
        return join(gitletDirOrDie(),"commits");
    }

    /**
     * 获取blobs文件目录
     * @return
     */
    public static File getBLOBS(){
        return join(gitletDirOrDie(),"blobs");
    }

    /**
     * 获取staging文件目录
     * @return
     */
    public static File getSTAGING(){
        return join(gitletDirOrDie(),"staging");
    }


}
