package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;


/**
 * 这个类只是用于暂存
 */
public class MapFile implements Serializable {
    //工作目录中文件的文件名->Pair(文件的相对路径和哈希值)
    private Map<String, Pair> map;

    MapFile() {
        map = new TreeMap<>();
    }

    /**
     * 取出map
     *
     * @return
     */
    public Map<String, Pair> map() {
        return map;
    }

    /**
     * 删除map里所对应的文件
     */
    public void delFile(String filename) {
        map.remove(filename);
    }

    /**
     * 将文件信息（文件名，相对路径，文件哈希）写入map
     *
     * @param filename
     * @param relative
     * @param fileHash
     */
    public void putFile(String filename, String relative, String fileHash) {
        map.put(filename, new Pair(relative, fileHash));
    }

    static class Pair implements Serializable {
        String relativePath;
        String fileHash;

        Pair(String r, String f) {
            this.relativePath = r;
            this.fileHash = f;
        }

    }


}
