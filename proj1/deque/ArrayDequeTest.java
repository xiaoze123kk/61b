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

    @Test
    public void randomizedGetTest() {
        System.out.println("🧪 开始 get 随机测试");
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        LinkedList<Integer> oracle = new LinkedList<>();
        Random r = new Random(114514); // 固定种子方便复现
        int operations = 2000;
        StringBuilder log = new StringBuilder();

        for (int i = 0; i < operations; i++) {
            int op = r.nextInt(7); // 增加一种 get 操作权重
            try {
                switch (op) {
                    case 0: { // addFirst
                        int v = r.nextInt(1000);
                        arrayDeque.addFirst(v);
                        oracle.addFirst(v);
                        log.append(i).append(": addFirst(").append(v).append(")\n");
                        break; }
                    case 1: { // addLast
                        int v = r.nextInt(1000);
                        arrayDeque.addLast(v);
                        oracle.addLast(v);
                        log.append(i).append(": addLast(").append(v).append(")\n");
                        break; }
                    case 2: { // removeFirst
                        Integer a = arrayDeque.removeFirst();
                        Integer b = oracle.isEmpty() ? null : oracle.removeFirst();
                        log.append(i).append(": removeFirst() -> ").append(a).append("\n");
                        assertEquals(b, a, errMsg("removeFirst 返回不一致", i, log, arrayDeque, oracle));
                        break; }
                    case 3: { // removeLast
                        Integer a = arrayDeque.removeLast();
                        Integer b = oracle.isEmpty() ? null : oracle.removeLast();
                        log.append(i).append(": removeLast() -> ").append(a).append("\n");
                        assertEquals(b, a, errMsg("removeLast 返回不一致", i, log, arrayDeque, oracle));
                        break; }
                    case 4: { // size 检查
                        log.append(i).append(": size() -> ").append(arrayDeque.size()).append("\n");
                        assertEquals(oracle.size(), arrayDeque.size(), errMsg("size 不一致", i, log, arrayDeque, oracle));
                        break; }
                    case 5: { // isEmpty 检查
                        log.append(i).append(": isEmpty() -> ").append(arrayDeque.isEmpty()).append("\n");
                        assertEquals(oracle.isEmpty(), arrayDeque.isEmpty(), errMsg("isEmpty 不一致", i, log, arrayDeque, oracle));
                        break; }
                    case 6: { // get 测试
                        if (arrayDeque.size() == 0) { // 空时测试非法索引
                            int badIdx = r.nextInt(3) - 1; // -1,0,1
                            Integer got = arrayDeque.get(badIdx);
                            log.append(i).append(": get(").append(badIdx).append(") -> ").append(got).append(" (空)\n");
                            assertNull(got, errMsg("空deque get 非法索引应为null", i, log, arrayDeque, oracle));
                        } else {
                            // 70% 测试合法索引, 30% 测试非法索引
                            if (r.nextDouble() < 0.7) {
                                int idx = r.nextInt(arrayDeque.size());
                                Integer expect = oracle.get(idx);
                                Integer actual = arrayDeque.get(idx);
                                log.append(i).append(": get(").append(idx).append(") -> ").append(actual).append("\n");
                                if ((expect == null && actual != null) || (expect != null && !expect.equals(actual))) {
                                    fail(errMsg("get 返回值错误 index=" + idx + " 期望=" + expect + " 实际=" + actual, i, log, arrayDeque, oracle));
                                }
                            } else {
                                int badIdx;
                                if (r.nextBoolean()) {
                                    badIdx = -1 - r.nextInt(3); // 负数
                                } else {
                                    badIdx = arrayDeque.size() + r.nextInt(3) + 1; // 超界
                                }
                                Integer actual = arrayDeque.get(badIdx);
                                log.append(i).append(": get(").append(badIdx).append(") -> ").append(actual).append(" (非法)\n");
                                assertNull(actual, errMsg("非法索引 get 应返回 null", i, log, arrayDeque, oracle));
                            }
                        }
                        break; }
                }
            } catch (AssertionError e) {
                System.err.println("❌ 在操作 " + i + " 发生断言失败: " + e.getMessage());
                System.err.println("---- 操作日志 (最近 50 条) ----");
                printLastLines(log.toString(), 50);
                System.err.println("---- 当前 Deque 内容 ----");
                printDequeContents(arrayDeque);
                System.err.println("---- Oracle (LinkedList) 内容 ----");
                System.err.println(oracle);
                throw e; // 继续抛出以让测试框架标红
            } catch (Exception ex) {
                System.err.println("💥 在操作 " + i + " 发生异常: " + ex);
                System.err.println("操作日志:");
                printLastLines(log.toString(), 50);
                throw ex;
            }
        }
        System.out.println("✅ get 随机测试完成, 最终 size=" + arrayDeque.size());
    }

    // 调试辅助: 打印最后 n 行日志
    private void printLastLines(String all, int n) {
        String[] lines = all.split("\n");
        int start = Math.max(0, lines.length - n);
        for (int i = start; i < lines.length; i++) {
            System.err.println(lines[i]);
        }
    }

    // 调试辅助: 打印当前 deque 内容(使用 get)
    private void printDequeContents(ArrayDeque<Integer> d) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < d.size(); i++) {
            sb.append(d.get(i));
            if (i + 1 < d.size()) sb.append(", ");
        }
        sb.append(']');
        System.err.println(sb);
    }

    // 统一错误信息构造
    private String errMsg(String msg, int opIndex, StringBuilder log, ArrayDeque<Integer> d, LinkedList<Integer> oracle) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg).append(" | op=").append(opIndex)
          .append(" | size=").append(d.size()).append('\n');
        sb.append("当前内容:");
        for (int i = 0; i < d.size(); i++) {
            sb.append(i == 0 ? " [" : "").append(d.get(i));
            if (i + 1 < d.size()) sb.append(", ");
            else sb.append("]\n");
        }
        sb.append("Oracle: ").append(oracle).append('\n');
        sb.append("最近日志: \n");
        String[] lines = log.toString().split("\n");
        int start = Math.max(0, lines.length - 20);
        for (int i = start; i < lines.length; i++) {
            sb.append(lines[i]).append('\n');
        }
        return sb.toString();
    }
}
