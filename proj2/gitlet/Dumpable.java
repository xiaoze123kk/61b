package gitlet;

import java.io.Serializable;

/** 描述可转储（dump）对象的接口。
 *  @author P. N. Hilfinger
 */
interface Dumpable extends Serializable {
    /** 在 System.out 上打印该对象的有用信息。 */
    void dump();
}
