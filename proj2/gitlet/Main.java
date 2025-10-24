package gitlet;

import java.util.Objects;

/**
 * Gitlet 的驱动类，Git 版本控制系统的一个子集。
 * 这个gitlet项目只考虑的是一个扁平的文件夹，不考虑子目录，现在我是多实现了一些，写测试只考虑.gitlet仓库
 * 同级层次的目录下只存在文件即可
 *
 * @author
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        Repository repo = new Repository();
        switch (firstArg) {
            case "init":
                repo.init();
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.add(args[1]);
                break;
            case "commit":
                if (args.length == 2) {
                    if (Objects.equals(args[1], "")) {
                        System.out.println("Please enter a commit message.");
                        System.exit(0);
                    }
                }
                if (args.length != 2) {
                    if (args.length == 1) {
                        System.out.println("Please enter a commit message.");
                        System.exit(0);
                    } else {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                }
                repo.commit(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.remove(args[1]);
                break;
            case "log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.log();
                break;
            case "global-log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.global_log();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.find(args[1]);
                break;
            case "status":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.status();
                break;
            case "checkout":
                if (Objects.equals(args[1], "--") && args.length == 3) {
                    repo.checkout1(args[2]);
                } else if (args.length == 4 && Objects.equals(args[2], "--")) {
                    repo.checkout2(args[1], args[3]);
                } else if (args.length == 2) {
                    repo.checkout3(args[1]);
                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.branch(args[1]);
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.rmbranch(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }

    }
}
