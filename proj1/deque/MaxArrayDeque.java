package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> c;
    /**
     * creates a MaxArrayDeque with the given Comparator.
     * @param c
     */
    public MaxArrayDeque(Comparator<T> c){
        super();
        this.c = c;
    }

    /**
     * returns the maximum element in the deque as governed by the previously given Comparator.
     * If the MaxArrayDeque is empty, simply return null.
     * @return
     */
    public T max(){
        return max(c);
    }

    /**
     * returns the maximum element in the deque as governed by the parameter Comparator c.
     * If the MaxArrayDeque is empty, simply return null.
     * @param c
     * @return
     */
    public T max(Comparator<T> c){
        if (isEmpty()){
            return null;
        }
        T maxItem = get(0);
        for (int i = 0; i < size(); i++) {
            T currItem = get(i);
            int symbol = c.compare(currItem,maxItem);
            if (symbol > 0){
                maxItem = currItem;
            }
        }
        return maxItem;
    }


}
