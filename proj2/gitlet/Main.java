package gitlet;

import java.util.Objects;

/** Gitlet 的驱动类，Git 版本控制系统的一个子集。
 *  @author TODO
 */
public class Main {

    /** 用法：java gitlet.Main ARGS，其中 ARGS 包含
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length==0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        // TODO：若用户输入的命令操作数数量或格式错误，打印 "Incorrect operands." 并退出。
        Repository repo = new Repository();
        // TODO：检测运行命令时所在目录是否存在 .gitlet，即在包含 .gitlet 的目录及其子目录下才能运行命令；
        //  否则打印 "Not in an initialized Gitlet directory."，然后直接返回。
        switch(firstArg) {
            case "init":
                repo.init();
                break;
            case "add":
                if (args.length != 2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.add(args[1]);
                break;
            case "commit":
                if (args.length == 2){
                    if (Objects.equals(args[1], "")){
                        System.out.println("Please enter a commit message.");
                        System.exit(0);
                    }
                }
                if (args.length != 2){
                    if (args.length == 1 ) {
                        System.out.println("Please enter a commit message.");
                        System.exit(0);
                    }
                    else {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                }
                repo.commit(args[1]);
                break;
            case "rm":
                if (args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.remove(args[1]);
                break;
            case "log":
                if (args.length!=1){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.log();
            // 该名称的命令不存在
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }

    }
}
