package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * 这个类只是用于暂存
 */
public class MapFile implements Serializable {
    //工作目录中文件的相对路径->该文件的哈希值
    private Map<String,String> mapRandH;

    MapFile(){
        mapRandH = new TreeMap<>();
    }

    /**
     * 取出map
     * @return
     */
    public Map<String, String> getMapRandH() {
        return mapRandH;
    }
}
