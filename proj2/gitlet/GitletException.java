package gitlet;

/** 一般性异常，表示 Gitlet 发生错误。对于致命错误，
 *  调用 .getMessage() 的结果即需要打印的错误信息。
 *  作者：P. N. Hilfinger
 */
class GitletException extends RuntimeException {


    /** 无消息的 GitletException。 */
    GitletException() {
        super();
    }

    /** 以 MSG 作为消息的 GitletException。 */
    GitletException(String msg) {
        super(msg);
    }

}
