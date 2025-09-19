package deque;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;
import java.util.LinkedList;

public class ArrayDequeTest {

    // 添加调试帮助方法
    private void printDequeState(ArrayDeque<Integer> deque, String operation, int operationNum) {
        System.out.println("=== 操作 " + operationNum + ": " + operation + " ===");
        System.out.println("Size: " + deque.size());
        System.out.println("isEmpty: " + deque.isEmpty());
        // 如果你的ArrayDeque有getNextFirst和getNextLast方法，可以取消注释
        // System.out.println("NextFirst: " + deque.getNextFirst());
        // System.out.println("NextLast: " + deque.getNextLast());
        System.out.println("内容:");
        deque.printDeque();
        System.out.println();
    }

    private void validateState(ArrayDeque<Integer> actual, LinkedList<Integer> expected,
                              String operation, int operationNum) {
        if (actual.size() != expected.size()) {
            System.err.println("❌ SIZE 错误在操作 " + operationNum + " (" + operation + ")");
            System.err.println("期望 size: " + expected.size() + ", 实际 size: " + actual.size());
            printDequeState(actual, operation, operationNum);
            fail("Size不匹配");
        }

        if (actual.isEmpty() != expected.isEmpty()) {
            System.err.println("❌ isEmpty 错误在操作 " + operationNum + " (" + operation + ")");
            System.err.println("期望 isEmpty: " + expected.isEmpty() + ", 实际 isEmpty: " + actual.isEmpty());
            printDequeState(actual, operation, operationNum);
            fail("isEmpty状态不匹配");
        }
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        LinkedList<Integer> expectedDeque = new LinkedList<>();
        Random random = new Random(42); // 固定种子以便复现

        int numOperations = 1000; // 减少操作数量，便于调试
        System.out.println("🧪 开始随机测试，共 " + numOperations + " 次操作");

        for (int i = 0; i < numOperations; i++) {
            int operation = random.nextInt(6); // 0-5，六种操作

            try {
                switch (operation) {
                    case 0: // addFirst
                        int addFirstValue = random.nextInt(100);
                        System.out.println("操作 " + i + ": addFirst(" + addFirstValue + ")");
                        arrayDeque.addFirst(addFirstValue);
                        expectedDeque.addFirst(addFirstValue);
                        validateState(arrayDeque, expectedDeque, "addFirst(" + addFirstValue + ")", i);
                        break;

                    case 1: // addLast
                        int addLastValue = random.nextInt(100);
                        System.out.println("操作 " + i + ": addLast(" + addLastValue + ")");
                        arrayDeque.addLast(addLastValue);
                        expectedDeque.addLast(addLastValue);
                        validateState(arrayDeque, expectedDeque, "addLast(" + addLastValue + ")", i);
                        break;

                    case 2: // removeFirst
                        System.out.println("操作 " + i + ": removeFirst()");
                        if (!expectedDeque.isEmpty()) {
                            Integer expectedFirst = expectedDeque.removeFirst();
                            Integer actualFirst = arrayDeque.removeFirst();
                            System.out.println("  期望移除: " + expectedFirst + ", 实际移除: " + actualFirst);

                            if (!expectedFirst.equals(actualFirst)) {
                                System.err.println("❌ removeFirst 值错误在操作 " + i);
                                System.err.println("期望: " + expectedFirst + ", 实际: " + actualFirst);
                                printDequeState(arrayDeque, "removeFirst", i);
                                fail("removeFirst返回值不匹配");
                            }
                            validateState(arrayDeque, expectedDeque, "removeFirst", i);
                        } else {
                            // 测试空deque的removeFirst
                            Integer result = arrayDeque.removeFirst();
                            System.out.println("  空队列removeFirst结果: " + result);
                            if (result != null) {
                                System.err.println("❌ 空队列removeFirst应该返回null，但返回了: " + result);
                                printDequeState(arrayDeque, "removeFirst(empty)", i);
                                fail("空队列removeFirst应该返回null");
                            }
                            assertEquals(0, arrayDeque.size(), "空队列操作后size应该为0");
                        }
                        break;

                    case 3: // removeLast
                        System.out.println("操作 " + i + ": removeLast()");
                        if (!expectedDeque.isEmpty()) {
                            Integer expectedLast = expectedDeque.removeLast();
                            Integer actualLast = arrayDeque.removeLast();
                            System.out.println("  期望移除: " + expectedLast + ", 实际移除: " + actualLast);

                            if (!expectedLast.equals(actualLast)) {
                                System.err.println("❌ removeLast 值错误在操作 " + i);
                                System.err.println("期望: " + expectedLast + ", 实际: " + actualLast);
                                printDequeState(arrayDeque, "removeLast", i);
                                fail("removeLast返回值不匹配");
                            }
                            validateState(arrayDeque, expectedDeque, "removeLast", i);
                        } else {
                            // 测试空deque的removeLast
                            Integer result = arrayDeque.removeLast();
                            System.out.println("  空队列removeLast结果: " + result);
                            if (result != null) {
                                System.err.println("❌ 空队列removeLast应该返回null，但返回了: " + result);
                                printDequeState(arrayDeque, "removeLast(empty)", i);
                                fail("空队列removeLast应该返回null");
                            }
                            assertEquals(0, arrayDeque.size(), "空队列操作后size应该为0");
                        }
                        break;

                    case 4: // 检查isEmpty
                        System.out.println("操作 " + i + ": 检查isEmpty");
                        validateState(arrayDeque, expectedDeque, "isEmpty检查", i);
                        break;

                    case 5: // 检查size
                        System.out.println("操作 " + i + ": 检查size");
                        validateState(arrayDeque, expectedDeque, "size检查", i);
                        break;
                }

                // 每10次操作显示进度
                if (i % 10 == 0 && i > 0) {
                    System.out.println("✅ 已完成 " + i + " 次操作，当前size: " + arrayDeque.size());
                }

            } catch (Exception e) {
                System.err.println("💥 异常发生在操作 " + i);
                printDequeState(arrayDeque, "异常", i);
                System.err.println("期望队列size: " + expectedDeque.size());
                e.printStackTrace();
                throw e;
            }
        }

        System.out.println("🎉 随机测试完成！执行了 " + numOperations + " 次操作");
        System.out.println("最终size: " + arrayDeque.size());
        System.out.println("最终isEmpty: " + arrayDeque.isEmpty());
    }

    @Test
    public void testResizingWithDebug() {
        System.out.println("🧪 开始扩容缩容测试");
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        // 测试扩容
        System.out.println("📈 测试扩容 - 添加100个元素");
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
            if (i % 20 == 0 || i < 10) {
                System.out.println("  添加第 " + i + " 个元素，当前size: " + deque.size());
            }

            if (deque.size() != i + 1) {
                System.err.println("❌ Size错误在添加第 " + i + " 个元素时");
                System.err.println("期望: " + (i + 1) + ", 实际: " + deque.size());
                fail("扩容时size不正确");
            }
        }

        System.out.println("✅ 扩容测试完成，最终size: " + deque.size());

        // 测试缩容
        System.out.println("📉 测试缩容 - 删除80个元素");
        for (int i = 0; i < 80; i++) {
            Integer removed = deque.removeFirst();
            int expectedSize = 100 - i - 1;

            if (i % 20 == 0 || i < 10) {
                System.out.println("  删除第 " + i + " 个元素: " + removed + "，当前size: " + deque.size());
            }

            if (!removed.equals(i)) {
                System.err.println("❌ 删除的值错误在第 " + i + " 次删除时");
                System.err.println("期望: " + i + ", 实际: " + removed);
                fail("删除的值不正确");
            }

            if (deque.size() != expectedSize) {
                System.err.println("❌ Size错误在删除第 " + i + " 个元素后");
                System.err.println("期望: " + expectedSize + ", 实际: " + deque.size());
                fail("缩容时size不正确");
            }
        }

        System.out.println("✅ 缩容测试完成，剩余size: " + deque.size());
        assertEquals(20, deque.size());
        assertFalse(deque.isEmpty());
    }

    @Test
    public void testMixedOperations() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        LinkedList<String> expected = new LinkedList<>();

        // 混合前后添加删除操作
        String[] testData = {"A", "B", "C", "D", "E", "F", "G", "H"};

        for (int i = 0; i < testData.length; i++) {
            if (i % 2 == 0) {
                deque.addFirst(testData[i]);
                expected.addFirst(testData[i]);
            } else {
                deque.addLast(testData[i]);
                expected.addLast(testData[i]);
            }
            assertEquals(expected.size(), deque.size());
        }

        // 混合删除操作
        while (!expected.isEmpty()) {
            if (expected.size() % 2 == 0) {
                String expectedValue = expected.removeFirst();
                String actualValue = deque.removeFirst();
                assertEquals(expectedValue, actualValue);
            } else {
                String expectedValue = expected.removeLast();
                String actualValue = deque.removeLast();
                assertEquals(expectedValue, actualValue);
            }
            assertEquals(expected.size(), deque.size());
        }

        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }

    @Test
    public void testEdgeCases() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();

        // 测试空deque
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        assertNull(deque.removeFirst());
        assertNull(deque.removeLast());

        // 测试单个元素
        deque.addFirst(42);
        assertFalse(deque.isEmpty());
        assertEquals(1, deque.size());

        Integer removed = deque.removeFirst();
        assertEquals(Integer.valueOf(42), removed);
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());

        // 测试添加删除循环
        for (int i = 0; i < 10; i++) {
            deque.addLast(i);
            deque.addFirst(-i);
            assertEquals(2, deque.size());

            deque.removeFirst();
            deque.removeLast();
            assertEquals(0, deque.size());
            assertTrue(deque.isEmpty());
        }
    }
}

