package deque;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;

public class MaxArrayDequeTest {
    // 测试空队列返回 null
    @Test
    public void testMaxOnEmpty() {
        Comparator<Integer> natural = Integer::compareTo;
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(natural);
        assertNull(mad.max(), "空队列 max() 应返回 null");
        assertNull(mad.max(natural), "空队列 max(Comparator) 应返回 null");
    }

    // 测试整数自然顺序
    @Test
    public void testMaxIntegersNatural() {
        Comparator<Integer> natural = Integer::compareTo;
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(natural);
        int[] data = {3, 1, 7, 7, -2, 5, 6};
        for (int v : data) {
            mad.addLast(v);
        }
        assertEquals(7, mad.max(), "自然序最大值应为 7");
    }

    // 测试使用不同的比较器（逆序 -> 取得最小元素）
    @Test
    public void testMaxIntegersReverseComparator() {
        Comparator<Integer> natural = Integer::compareTo;
        Comparator<Integer> reverse = (a, b) -> Integer.compare(b, a); // 逆序
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(natural);
        int[] data = {10, 4, 9, -5, 0, 10};
        for (int v : data) mad.addLast(v);
        // 默认比较器（自然序）
        assertEquals(10, mad.max());
        // 传入逆序比较器时，应该返回最小值 -5
        assertEquals(-5, mad.max(reverse));
    }

    // 测试字符串长度比较
    @Test
    public void testMaxStringsLength() {
        Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(byLength);
        mad.addLast("a");
        mad.addLast("abcd"); // 第一个长度4
        mad.addLast("abc");
        mad.addLast("zzzz"); // 同长度4，但后出现
        mad.addLast("pq");
        // 比较器只按长度，遇到同长度不会替换（返回0 不 > 0）=> 结果应为第一个长度4: "abcd"
        assertEquals("abcd", mad.max(), "长度比较应返回第一个出现的最长字符串");

        // 传入一个按字典序的比较器
        Comparator<String> lexicographic = String::compareTo;
        assertEquals("zzzz", mad.max(lexicographic), "字典序最大应为 zzzz");
    }

    // 测试所有元素相等时
    @Test
    public void testAllEqual() {
        Comparator<Integer> natural = Integer::compareTo;
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(natural);
        for (int i = 0; i < 5; i++) mad.addLast(42);
        assertEquals(42, mad.max());
        Comparator<Integer> reverse = (a,b) -> Integer.compare(b,a);
        assertEquals(42, mad.max(reverse));
    }
}
