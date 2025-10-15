package gitlet;

public class Branch {
    /**分支名称*/
    private String name;
    /**分支头指针*/
    private String branchHead;

    Branch(String name , String branchHead){
        this.name = name;
        this.branchHead = branchHead;
    }

}
