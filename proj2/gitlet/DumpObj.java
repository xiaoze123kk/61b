package gitlet;

import java.io.File;

/** 一个调试类，其主程序可如下调用：
 *      java gitlet.DumpObj FILE...
 *  其中每个 FILE 都是由 Utils.writeObject 生成的文件（或任意包含序列化对象的文件）。
 *  该程序将简单地读取 FILE、反序列化其内容，并对得到的对象调用 dump 方法。
 *  该对象必须实现 gitlet.Dumpable 接口才能工作。例如，你可以像下面这样定义你的类：
 *
 *        import java.io.Serializable;
 *        import java.util.TreeMap;
 *        class MyClass implements Serializeable, Dumpable {
 *            ...
 *            {@literal @}Override
 *            public void dump() {
 *               System.out.printf("size: %d%nmapping: %s%n", _size, _mapping);
 *            }
 *            ...
 *            int _size;
 *            TreeMap<String, String> _mapping = new TreeMap<>();
 *        }
 *
 *  如上所示，你的 dump 方法应当打印出该类对象的有用信息。
 *  作者：P. N. Hilfinger
 */
public class DumpObj {

    /** 反序列化并对 FILES 中每个文件的内容调用 dump。 */
    public static void main(String... files) {
        for (String fileName : files) {
            Dumpable obj = Utils.readObject(new File(fileName),
                                            Dumpable.class);
            obj.dump();
            System.out.println("---");
        }
    }
}
