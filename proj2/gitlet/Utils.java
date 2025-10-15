package gitlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;


/** 各种实用工具。
 *
 * 仔细阅读本文件，它提供了若干有用的工具函数，可为你节省时间。
 *
 *  作者：P. N. Hilfinger
 */
class Utils {

    /** 以十六进制表示的完整 SHA-1 UID 的长度。 */
    static final int UID_LENGTH = 40;

    /* SHA-1 哈希值 */

    /** 返回将 VALS 连接后计算得到的 SHA-1 哈希。VALS 可以是字节数组和字符串的任意混合。 */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /** 返回将 VALS 中的字符串连接后得到的 SHA-1 哈希。 */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* 文件删除 */

    /** 如果 FILE 存在且不是目录，则删除之。若成功删除返回 true，否则返回 false。
     *  若 FILE 所在目录不包含名为 .gitlet 的目录，则拒绝删除并抛出 IllegalArgumentException。 */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /** 如果名为 FILE 的文件存在且不是目录，则删除之。
     *  若成功删除返回 true，否则返回 false。若 FILE 所在目录不包含名为 .gitlet 的目录，
     *  则拒绝删除并抛出 IllegalArgumentException。 */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* 读取与写入文件内容 */

    /** 以字节数组形式返回 FILE 的全部内容。FILE 必须是普通文件。
     *  如有问题则抛出 IllegalArgumentException。 */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** 以字符串形式返回 FILE 的全部内容。FILE 必须是普通文件。
     *  如有问题则抛出 IllegalArgumentException。 */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /** 将 CONTENTS 中的字节按顺序连接后的结果写入 FILE，必要时创建或覆盖。
     *  CONTENTS 中的每个对象可以是 String 或字节数组。如有问题则抛出 IllegalArgumentException。 */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** 从 FILE 读取对象，返回类型为 T，并强制转换为 EXPECTEDCLASS。
     *  如有问题则抛出 IllegalArgumentException。 */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** 将 OBJ 写入 FILE。 */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /* 目录 */

    /** 仅保留普通文件的过滤器。 */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /** 返回目录 DIR 中所有普通文件的文件名列表（Java 字符串），按字典序排序。
     *  如果 DIR 不是目录则返回 null。 */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /** 返回目录 DIR 中所有普通文件的文件名列表（Java 字符串），按字典序排序。
     *  如果 DIR 不是目录则返回 null。 */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* 其他文件工具 */

    /** 将 FIRST 与 OTHERS 拼接成一个 File 指示器，
     *  类似于 java.nio.file.Paths.get(String, String[]) 方法。 */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** 将 FIRST 与 OTHERS 拼接成一个 File 指示器，
     *  类似于 java.nio.file.Paths.get(String, String[]) 方法。 */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* 序列化工具 */

    /** 返回包含 OBJ 序列化内容的字节数组。 */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }



    /* 消息与错误报告 */

    /** 返回一个 GitletException，其消息由 MSG 和 ARGS 按照 String.format 的方式格式化而成。 */
    static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /** 按照 String.format 的方式用 MSG 和 ARGS 组合并打印消息，随后输出一个换行。 */
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }
}
