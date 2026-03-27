import java.util.LinkedList;
import java.util.Queue;

/**
 * This class implements a Binary Search Tree (BST) that stores comparable data.
 * It implements the SortedCollection interface and uses BinaryNode objects
 * to store data.
 */
public class BinarySearchTree<T extends Comparable<T>> implements  SortedCollection<T> {

    /** Reference to the root node of this tree. */
    protected BinaryNode<T> root = null;

    /** No-argument constructor*/
    public BinarySearchTree() {

    }


    /**
     * Inserts a non-null data value into this BST.
     * Duplicates are inserted into the left subtree.
     *
     * @param data value to insert
     * @throws NullPointerException if data is null
     */
    @Override
    public void insert(T data) throws NullPointerException {
        if (data == null)
            throw new NullPointerException("Cannot insert null into the BST.");

        BinaryNode<T> newNode = new BinaryNode<>(data);


        if (root == null) {
            root = newNode;
            return;
        }

        insertHelper(newNode, root);
    }

    /**
     * Performs BST insert algorithm to recursively insert newNode into
     * the provided subtree. When subtree is null, this method does nothing.
     * <p>
     * Duplicates are inserted to the LEFT.
     */
    protected void insertHelper(BinaryNode<T> newNode, BinaryNode<T> subtree) {
        if (subtree == null) {
            return;
        }

        int cmp = newNode.getData().compareTo(subtree.getData());

        // if cmp <= 0 goes left, otherwise goes right
        if (cmp <= 0) {
            if (subtree.getLeft() == null) {
                subtree.setLeft(newNode);
                newNode.setUp(subtree);
            } else {
                insertHelper(newNode, subtree.getLeft());
            }
        } else {
            if (subtree.getRight() == null) {
                subtree.setRight(newNode);
                newNode.setUp(subtree);
            } else {
                insertHelper(newNode, subtree.getRight());
            }
        }
    }

    /**
     * Checks whether the tree contains the given value.
     */
    @Override
    public boolean contains(Comparable<T> find) {
        if (find == null) return false;
        return containsHelper(find, root);
    }

    /**
     * Recursive helper for contains().
     */
    private boolean containsHelper(Comparable<T> find, BinaryNode<T> subtree) {
        if (subtree == null) return false;

        int cmp = find.compareTo(subtree.getData());
        if (cmp == 0) return true;
        if (cmp < 0) return containsHelper(find, subtree.getLeft());
        return containsHelper(find, subtree.getRight());
    }

    /**
     * Counts number of values in the tree.
     */
    @Override
    public int size() {
        return sizeHelper(root);
    }

    /**
     * Recursive helper for size().
     *
     */
    private int sizeHelper(BinaryNode<T> subtree) {
        if (subtree == null) {
            return 0;
        }
        return 1 + sizeHelper(subtree.getLeft()) + sizeHelper(subtree.getRight());
    }

    /**
     * @return true if tree has no nodes.
     */
    @Override
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Removes all values from the tree.
     */
    @Override
    public void clear() {
        root = null;
    }


    /**
     * @return in-order string
     */
    public String toInOrderString() {
        if (root == null) return "[ ]";
        return root.toInOrderString();
    }

    /**
     * @return level-order string
     */
    public String toLevelOrderString() {
        if (root == null) return "[ ]";
        return root.toLevelOrderString();
    }

      // -------------
     //    TESTS
    // -------------



    /**
     * Checks parent pointers are maintained or NOT.
     */
    public boolean test1() {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.insert(10);
        bst.insert(5);
        bst.insert(15);
        bst.insert(3);
        bst.insert(7);
        bst.insert(12);
        bst.insert(18);

        // this duplicated value should go LEFT
        bst.insert(7);

        // Basic structure checks
        if (bst.root == null || !bst.root.getData().equals(10)) {
            return false;
        }

        BinaryNode<Integer> number5 = bst.root.getLeft();
        BinaryNode<Integer> number15 = bst.root.getRight();
        if (number5 == null || !number5.getData().equals(5)) {
            return false;
        }
        if (number15 == null || !number15.getData().equals(15)) {
            return false;
        }

        // Parent pointers
        if (number5.getUp() != bst.root) {
            return false;
        }
        if (number15.getUp() != bst.root) {
            return false;
        }

        // Check some deeper nodes
        BinaryNode<Integer> number7 = number5.getRight();
        if (number7 == null || !number7.getData().equals(7)) {
            return false;
        }
        if (number7.getUp() != number5) {
            return false;
        }

        // Duplicate 7 must be somewhere in LEFT subtree of that 7
        BinaryNode<Integer> duplicated7 = number7.getLeft();
        if (duplicated7 == null || !duplicated7.getData().equals(7)) {
            return false;
        }
        if (duplicated7.getUp() != number7) {
            return false;
        }

        // size should count duplicates
        if (bst.size() != 8) {
            return false;
        }

        // contains checks
            if (!bst.contains(10)) return false;      // root
            if (!bst.contains(3)) return false;  // left leaf
            if (!bst.contains(18)) return false; // right leaf
            if (!bst.contains(7)) return false;  // internal (and duplicated)
            if (bst.contains(999)) return false;

            return true;
        }

        /**
         * Test finding values that are leaves and inside nodes
         */
        public boolean test2() {
            BinarySearchTree<String> bst = new BinarySearchTree<>();

            // Insert in an order that creates a different shape
            bst.insert("m");
            bst.insert("f");
            bst.insert("t");
            bst.insert("b");
            bst.insert("h");
            bst.insert("p");
            bst.insert("z");

            // Leaves: b, h, p, z; Interior: f, t; Root: m
            if (!bst.contains("m")) return false;
            if (!bst.contains("f")) return false;
            if (!bst.contains("t")) return false;

            if (!bst.contains("b")) return false;
            if (!bst.contains("h")) return false;
            if (!bst.contains("p")) return false;
            if (!bst.contains("z")) return false;

            if (bst.contains("a")) return false;
            if (bst.contains("zzzz")) return false;

            // Parent pointers quick check
            if (bst.root == null) return false;
            if (bst.root.getLeft() == null || bst.root.getRight() == null) return false;
            if (bst.root.getLeft().getUp() != bst.root) return false;
            if (bst.root.getRight().getUp() != bst.root) return false;

            return true;
        }



        /**
         * Test the size() and clear() functions.
         */
        public boolean test3() {
            BinarySearchTree<Integer> bst = new BinarySearchTree<>();

            if (!bst.isEmpty()) return false;
            if (bst.size() != 0) return false;

            bst.insert(2);
            bst.insert(1);
            bst.insert(3);
            if (bst.isEmpty()) return false;
            if (bst.size() != 3) return false;

            bst.clear();
            if (!bst.isEmpty()) return false;
            if (bst.size() != 0) return false;
            if (bst.root != null) return false;

            // Build another shape after clearing (ensure tree still works)
            bst.insert(5);
            bst.insert(4);
            bst.insert(6);
            bst.insert(4); // duplicate
            if (bst.size() != 4) return false;
            if (!bst.contains(4)) return false;
            if (!bst.contains(6)) return false;

            // Check duplicate went left of a 4
            BinaryNode<Integer> n4 = bst.root.getLeft(); // should be 4
            if (n4 == null || !n4.getData().equals(4)) return false;
            if (n4.getLeft() == null || !n4.getLeft().getData().equals(4)) return false;

            return true;
        }

        /**
         * main method to run all the tests and check the correctness.
         */
        public static void main(String[] args) {
            BinarySearchTree<Integer> intTree = new BinarySearchTree<>();
            System.out.println("test1 (Integer insert/shape/parents/dupes): " + intTree.test1());
            System.out.println("test3 (size/clear/rebuild): " + intTree.test3());

            BinarySearchTree<String> strTree = new BinarySearchTree<>();
            System.out.println("test2 (String contains/leaves/interior): " + strTree.test2());
        }

    }
