import java.util.Iterator;
import java.util.Stack;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * This class extends RedBlackTree into a tree that supports iterating over the values it
 * stores in sorted, ascending order.
 */
public class RBTreeIterable<T extends Comparable<T>>
        extends RedBlackTree<T> implements IterableSortedCollection<T> {

    //Field that will be used to store the maximum for the iterator, or null if no maximum is set.
    private Comparable<T> maxIterator = null;

    //Field that will be used to stores the minimum for the iterator, or null if no minimum is set.
    private Comparable<T> minIterator = null;



    /**
     * Allows setting the start (minimum) value of the iterator. When this method is called,
     * every iterator created after it will use the minimum set by this method until this method
     * is called again to set a new minimum value.
     *
     * @param min the minimum for iterators created for this tree, or null for no minimum
     */
    public void setIteratorMin(Comparable<T> min) {
        this.minIterator = min;
    }

    /**
     * Allows setting the stop (maximum) value of the iterator. When this method is called,
     * every iterator created after it will use the maximum set by this method until this method
     * is called again to set a new maximum value.
     *
     * @param max the maximum for iterators created for this tree, or null for no maximum
     */
    public void setIteratorMax(Comparable<T> max) {
         this.maxIterator = max;

     }

    /**
     * Returns an iterator over the values stored in this tree. The iterator uses the
     * start (minimum) value set by a previous call to setIteratorMin, and the stop (maximum)
     * value set by a previous call to setIteratorMax. If setIteratorMin has not been called
     * before, or if it was called with a null argument, the iterator uses no minimum value
     * and starts with the lowest value that exists in the tree. If setIteratorMax has not been
     * called before, or if it was called with a null argument, the iterator uses no maximum
     * value and finishes with the highest value that exists in the tree.
     */
    public Iterator<T> iterator() {
        return new TreeIterator<T>(root, minIterator, maxIterator);
    }

    /**
     * Nested class for Iterator objects created for this tree and returned by the iterator method.
     * This iterator follows an in-order traversal of the tree and returns the values in sorted,
     * ascending order.
     */
    protected static class TreeIterator<R extends Comparable<R>> implements Iterator<R> {

        // stores the start point (minimum) for the iterator
        Comparable<R> min = null;
        // stores the stop point (maximum) for the iterator
        Comparable<R> max = null;
        // stores the stack that keeps track of the inorder traversal
        Stack<BinaryNode<R>> stack = null;

        /**
         * Constructor for a new iterator if the tree with root as its root node, and
         * min as the start (minimum) value (or null if no start value) and max as the
         * stop (maximum) value (or null if no stop value) of the new iterator.
         * Time complexity should be O(log n).
         *
         * @param root root node of the tree to traverse
         * @param min  the minimum value that the iterator will return
         * @param max  the maximum value that the iterator will return
         */
        public TreeIterator(BinaryNode<R> root, Comparable<R> min, Comparable<R> max) {
	        this.min = min;
	        this.max = max;
            this.stack = new Stack<>();
            updateStack(root);
        }


        /**
         * Helper method for initializing and updating the stack. This method both
         * - finds the next data value stored in the tree (or subtree) that is between
         * start(minimum) and stop(maximum) point (including start and stop points
         * themselves), and
         * - builds up the stack of ancestor nodes that contain values between
         * start(minimum) and stop(maximum) values (including start and stop values
         * themselves) so that those nodes can be visited in the future.
         *
         * @param node the root node of the subtree to process
         */
        private void updateStack(BinaryNode<R> node) {
            if (node == null) return;

             // if node is smaller than min, skip left side and go right
            if (min != null && min.compareTo(node.getData()) > 0) {
                updateStack(node.getRight());
                return;
            } else {
                // node is within range (or no bounds), so it might be returned
                stack.push(node);
                updateStack(node.getLeft());
            }

        }

        /**
         * Returns true if there is another value to visit.
         * The next value is at the top of the stack.
         * If a maximum bound is set, ensure the next value does not exceed it.
         */
        public boolean hasNext() {

            if (stack.isEmpty()) {
                return false;
            }
            if (this.max == null) {
                return true;
            }
            return this.max.compareTo(stack.peek().getData()) >= 0;
               
        }
        

        /**
         * Returns the next value of the iterator.
         * Amortized time complexity should be O(1).
         * Worst case time complexity should be O(log n).
         * Do not implement this method by linearly walking through the
         * entire tree from the smallest element until the start bound is reached.
         * That process should occur only once during construction of the
         * iterator object.
         *
         * @throws NoSuchElementException if the iterator has no more values to return
         */
        public R next() {

            if (!hasNext()) {
                throw new NoSuchElementException("There are no more values to visit in iterator.");
            }
            BinaryNode<R> nextNode = stack.pop();
            R value = nextNode.getData();

            // After visiting this node, process its right subtree
            // so the next smallest values are correctly added to the stack.
            updateStack(nextNode.getRight());

            return value;
        }
    }


    /**
     * Checks integer iteration with no duplicates, using BOTH min and max bounds. 
     * Ensures output is sorted and stays within [min,max].
     */
    @Test
    public void testIterable1() {
        RBTreeIterable<Integer> firstTester = new RBTreeIterable<>();

        // Insert values into the tree (some inside and outside the min/max range)
        firstTester.insert(10);
        firstTester.insert(20);
        firstTester.insert(15);
        firstTester.insert(8);
        firstTester.insert(9);

        // Set both minimum and maximum bounds, then create the iterator
        firstTester.setIteratorMax(20);
        firstTester.setIteratorMin(10);

        Iterator<Integer> firstIterate = firstTester.iterator();

        // Confirm values returned are within bounds and in sorted order
        Assertions.assertTrue(firstIterate.hasNext());

        Assertions.assertEquals(10, firstIterate.next());
        Assertions.assertEquals(15, firstIterate.next());
        Assertions.assertEquals(20, firstIterate.next());

        Assertions.assertFalse(firstIterate.hasNext());
    }


    /**
     * Checks integer iteration with duplicates allowed in inserts,
     * using ONLY a min bound (no max).
     * Confirms values returned are >= min and are in ascending order.
     */
    @Test
    public void testIterable2() {
        RBTreeIterable<Integer> secondTester = new RBTreeIterable<>();

        // Insert values including duplicates to test min-only behavior
        secondTester.insert(10);
        secondTester.insert(7);
        secondTester.insert(30);
        secondTester.insert(30);
        secondTester.insert(35);

        // Set only the minimum bound and create iterator
        secondTester.setIteratorMin(7);
        Iterator<Integer> secondIterator = secondTester.iterator();

        Assertions.assertTrue(secondIterator.hasNext());
        int checkValue = 7;

        // Walk through iterator and ensure values are >= min and sorted
        while (secondIterator.hasNext()) {
            int intValue = secondIterator.next();
            Assertions.assertTrue(intValue >= checkValue);
            checkValue = intValue;
        }

        Assertions.assertFalse(secondIterator.hasNext());


    }

    /**
     * Checks string iteration with no duplicates, using ONLY a max bound (no min).
     * Ensures iterator stops at max (inclusive) and is sorted.
     */
    @Test
    public void testIterable3() {
        RBTreeIterable<String> thirdTester = new RBTreeIterable<>();

        // Insert string values in non-sorted order
        thirdTester.insert("Apple");
        thirdTester.insert("Banana");
        thirdTester.insert("Zebra");
        thirdTester.insert("Cat");

        // Set only the maximum bound and create iterator
        thirdTester.setIteratorMax("Cat");
        Iterator<String> thirdIterator = thirdTester.iterator();

        // Ensure iterator returns values up to and including the max bound
        Assertions.assertTrue(thirdIterator.hasNext());

        Assertions.assertEquals("Apple", thirdIterator.next());
        Assertions.assertEquals("Banana", thirdIterator.next());
        Assertions.assertEquals("Cat", thirdIterator.next());

        Assertions.assertFalse(thirdIterator.hasNext());

    }
    


}
