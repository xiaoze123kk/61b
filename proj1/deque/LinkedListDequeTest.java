package deque;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Objects;
import java.util.Random;
import java.util.Iterator;


/**
 * Performs some basic linked list tests.
 */
public class LinkedListDequeTest {


    
    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double> lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

    /**
     * 随机测试get函数的迭代版本和递归版本
     * 测试两个版本是否返回相同的结果
     */
    @Test
    public void randomGetIterativeVsRecursiveTest() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        Random random = new Random(42); // 固定种子确保可重现

        // 随机添加一些元素
        int numOperations = 20;
        for (int i = 0; i < numOperations; i++) {
            int value = random.nextInt(100);
            if (random.nextBoolean()) {
                deque.addFirst(value);
            } else {
                deque.addLast(value);
            }
        }

        System.out.println("测试get迭代版本vs递归版本，deque大小: " + deque.size());

        // 测试所有有效索引
        for (int i = 0; i < deque.size(); i++) {
            Integer iterativeResult = deque.get(i);
            Integer recursiveResult = deque.getRecursive(i);

            if (!Objects.equals(iterativeResult, recursiveResult)) {
                System.out.println("错误: 在索引 " + i + " 处，迭代版本返回 " + iterativeResult +
                                 "，递归版本返回 " + recursiveResult);
                System.out.println("当前deque内容:");
                deque.printDeque();
                fail("get()和getRecursive()在索引" + i + "处返回不同结果");
            }
        }

        // 测试边界情况
        Integer invalidGet1 = deque.get(-1);
        Integer invalidRecursive1 = deque.getRecursive(-1);
        if (!Objects.equals(invalidGet1, invalidRecursive1)) {
            System.out.println("错误: 负索引测试，迭代版本返回 " + invalidGet1 +
                             "，递归版本返回 " + invalidRecursive1);
            fail("负索引时get()和getRecursive()返回不同结果");
        }

        Integer invalidGet2 = deque.get(deque.size());
        Integer invalidRecursive2 = deque.getRecursive(deque.size());
        if (!Objects.equals(invalidGet2, invalidRecursive2)) {
            System.out.println("错误: 超出范围索引测试，迭代版本返回 " + invalidGet2 +
                             "，递归版本返回 " + invalidRecursive2);
            fail("超出范围索引时get()和getRecursive()返回不同结果");
        }

        System.out.println("get迭代版本vs递归版本测试通过");
    }

    /**
     * 随机测试iterator和equals方法
     */
    @Test
    public void randomIteratorAndEqualsTest() {
        LinkedListDeque<String> deque1 = new LinkedListDeque<>();
        LinkedListDeque<String> deque2 = new LinkedListDeque<>();
        Random random = new Random(123);

        // 向两个deque添加相同的元素
        String[] testData = new String[15];
        for (int i = 0; i < 15; i++) {
            testData[i] = "item" + random.nextInt(50);
            if (random.nextBoolean()) {
                deque1.addFirst(testData[i]);
                deque2.addFirst(testData[i]);
            } else {
                deque1.addLast(testData[i]);
                deque2.addLast(testData[i]);
            }
        }

        System.out.println("测试iterator和equals，deque大小: " + deque1.size());

        // 测试equals方法
        if (!deque1.equals(deque2)) {
            System.out.println("错误: 相同内容的deque应该相等");
            System.out.println("deque1内容:");
            deque1.printDeque();
            System.out.println("deque2内容:");
            deque2.printDeque();
            fail("相同内容的deque equals()返回false");
        }

        // 测试iterator
        Iterator<String> iter1 = deque1.iterator();
        Iterator<String> iter2 = deque2.iterator();
        int position = 0;

        while (iter1.hasNext() && iter2.hasNext()) {
            String item1 = iter1.next();
            String item2 = iter2.next();
            String getItem = deque1.get(position);

            if (!Objects.equals(item1, item2)) {
                System.out.println("错误: 位置 " + position + " 处，iter1返回 " + item1 +
                                 "，iter2返回 " + item2);
                fail("相同deque的iterator在位置" + position + "返回不同值");
            }

            if (!Objects.equals(item1, getItem)) {
                System.out.println("错误: 位置 " + position + " 处，iterator返回 " + item1 +
                                 "，get()返回 " + getItem);
                fail("iterator和get()在位置" + position + "返回不同值");
            }
            position++;
        }

        if (iter1.hasNext() || iter2.hasNext()) {
            System.out.println("错误: iterator长度不匹配，应该都遍历完成");
            fail("iterator长度不匹配");
        }

        // 修改一个deque，测试不相等情况
        deque2.addFirst("different");
        if (deque1.equals(deque2)) {
            System.out.println("错误: 不同内容的deque不应该相等");
            System.out.println("deque1大小: " + deque1.size() + ", deque2大小: " + deque2.size());
            fail("不同内容的deque equals()返回true");
        }

        System.out.println("iterator和equals测试通过");
    }

    /**
     * 综合随机测试：结合add、remove和size方法
     */
    @Test
    public void randomComprehensiveTest() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        Random random = new Random(456);
        int expectedSize = 0;

        System.out.println("开始综合随机测试");

        for (int operation = 0; operation < 100; operation++) {
            int choice = random.nextInt(6); // 6种操作

            if (choice == 0) { // addFirst
                int value = random.nextInt(1000);
                deque.addFirst(value);
                expectedSize++;

                if (deque.size() != expectedSize) {
                    System.out.println("错误: addFirst后，期望大小 " + expectedSize +
                                     "，实际大小 " + deque.size());
                    System.out.println("刚添加的值: " + value + "，操作序号: " + operation);
                    fail("addFirst后size()不正确");
                }

            } else if (choice == 1) { // addLast
                int value = random.nextInt(1000);
                deque.addLast(value);
                expectedSize++;

                if (deque.size() != expectedSize) {
                    System.out.println("错误: addLast后，期望大小 " + expectedSize +
                                     "，实际大小 " + deque.size());
                    System.out.println("刚添加的值: " + value + "，操作序号: " + operation);
                    fail("addLast后size()不正确");
                }

            } else if (choice == 2 && !deque.isEmpty()) { // removeFirst
                Integer removed = deque.removeFirst();
                expectedSize--;

                if (deque.size() != expectedSize) {
                    System.out.println("错误: removeFirst后，期望大小 " + expectedSize +
                                     "，实际大小 " + deque.size());
                    System.out.println("刚移除的值: " + removed + "，操作序号: " + operation);
                    fail("removeFirst后size()不正确");
                }

                if (removed == null) {
                    System.out.println("错误: removeFirst从非空deque返回null，操作序号: " + operation);
                    fail("removeFirst从非空deque返回null");
                }

            } else if (choice == 3 && !deque.isEmpty()) { // removeLast
                Integer removed = deque.removeLast();
                expectedSize--;

                if (deque.size() != expectedSize) {
                    System.out.println("错误: removeLast后，期望大小 " + expectedSize +
                                     "，实际大小 " + deque.size());
                    System.out.println("刚移除的值: " + removed + "，操作序号: " + operation);
                    fail("removeLast后size()不正确");
                }

                if (removed == null) {
                    System.out.println("错误: removeLast从非空deque返回null，操作序号: " + operation);
                    fail("removeLast从非空deque返回null");
                }

            } else if (choice == 4) { // 测试isEmpty
                boolean actualEmpty = deque.isEmpty();
                boolean expectedEmpty = (expectedSize == 0);

                if (actualEmpty != expectedEmpty) {
                    System.out.println("错误: isEmpty()返回 " + actualEmpty +
                                     "，但期望 " + expectedEmpty);
                    System.out.println("当前大小: " + deque.size() + "，期望大小: " + expectedSize +
                                     "，操作序号: " + operation);
                    fail("isEmpty()返回错误结果");
                }

            } else if (choice == 5 && !deque.isEmpty()) { // 测试get
                int index = random.nextInt(deque.size());
                Integer value = deque.get(index);

                if (value == null) {
                    System.out.println("错误: get(" + index + ")从非空deque返回null");
                    System.out.println("deque大小: " + deque.size() + "，操作序号: " + operation);
                    fail("get()从有效索引返回null");
                }
            }

            // 每10次操作检查一次size一致性
            if (operation % 10 == 0) {
                if (deque.size() != expectedSize) {
                    System.out.println("错误: 操作 " + operation + " 后size不一致");
                    System.out.println("期望大小: " + expectedSize + "，实际大小: " + deque.size());
                    fail("size()与预期不符");
                }
            }
        }

        System.out.println("综合测试完成，最终大小: " + deque.size());
        System.out.println("综合随机测试通过");
    }

}
