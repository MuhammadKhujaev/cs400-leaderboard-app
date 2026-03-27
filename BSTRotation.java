public class BSTRotation <T extends Comparable<T>> extends BinarySearchTree<T> {

    /**
     * A default constructor with no arguments
     */
    public BSTRotation(){

    }

    /**
     * Performs the rotation operation on the provided nodes within this tree.
     * When the provided child is a left child of the provided parent, this
     * method will perform a right rotation. When the provided child is a right
     * child of the provided parent, this method will perform a left rotation.
     *
     * @param child is the node being rotated from child to parent position
     * @param parent is the node being rotated from parent to child position
     */
    protected void rotate (BinaryNode<T> child, BinaryNode<T> parent) {


        // Guard against null references (invalid rotation request)
        if (parent == null || child == null)
            throw new NullPointerException("parent and child cannot be null");

	// Case 1: child is right child -> perform LEFT rotation
        if (parent.getRight() == child) {

	    // Save child's left subtree: it will become parent's right subtree after rotation
            BinaryNode<T> childLeftSubtree =  child.getLeft();

	    // Save grandparent reference so we can reconnect rotated subtree back to the rest of the tree
            BinaryNode<T> grandparent = parent.getUp();

	    // If parent was not the root, reconnect grandparent to point to child (new subtree root)
            if (grandparent != null) {
                if (grandparent.getLeft() == parent) {
                    grandparent.setLeft(child);
                } else if (grandparent.getRight() == parent) {
                    grandparent.setRight(child);
                } else {
                    throw new IllegalArgumentException("Parent is not equal to neither grandparents " +
                            "right subtree nor left subtree");
                }

		// Update parent pointers: child moves up, parent moves down
                child.setUp(grandparent);
                parent.setUp(child);

	  	// Perform rotation links:
		// child becomes above parent, and child's left subtree shifts to parent's right
                child.setLeft(parent);
                parent.setRight(childLeftSubtree);

		// If child's former left subtree exists, update its parent pointer to parent
                if (childLeftSubtree !=null) {
                    childLeftSubtree.setUp(parent);
                }

	    // If parent was the root, then child becomes the new root after rotation
            } else {
                root = child;
                child.setUp(null);

		// Finish the same rotation link updates as above
                parent.setUp(child);
                child.setLeft(parent);
                parent.setRight(childLeftSubtree);

                if (childLeftSubtree !=null) {
                    childLeftSubtree.setUp(parent);
                }

           }


	// Case 2: child is left child -> perform RIGHT rotation
        }  else if (parent.getLeft() == child) {

	    // Save child's right subtree: it will become parent's left subtree after rotation
            BinaryNode<T> childRightSubtree = child.getRight();
            BinaryNode<T> grandparent = parent.getUp();

	    // Update parent pointer of moved subtree
            if (grandparent != null) {
                if (grandparent.getLeft() == parent) {
                    grandparent.setLeft(child);
                } else if (grandparent.getRight() == parent) {
                    grandparent.setRight(child);
                } else {
                    throw new IllegalArgumentException("Parent is not equal to neither grandparents " +
                            "right subtree nor left subtree");
                }

                child.setUp(grandparent);
                parent.setUp(child);
                child.setRight(parent);
                parent.setLeft(childRightSubtree);

                if (childRightSubtree !=null) {
                    childRightSubtree.setUp(parent);
                }

            } else {
                root = child;
                child.setUp(null);

                parent.setUp(child);
                child.setRight(parent);
                parent.setLeft(childRightSubtree);

                if (childRightSubtree !=null) {
                    childRightSubtree.setUp(parent);
                }
            }


        } else {
            throw new IllegalArgumentException("child is not a direct child of parent");
        }

    }




    /**
     * Tester 1: right rotation with three children
     */
    public boolean test1(){
        Boolean allPassedTest = true;

        BinaryNode<Integer> grandParentValue = new BinaryNode<>(20);
        BinaryNode<Integer> parentValue = new BinaryNode<>(15);
        BinaryNode<Integer> childValue = new BinaryNode<>(13);
        BinaryNode<Integer> parentRightSubtree = new BinaryNode<>(17);
        BinaryNode<Integer> childLeftSubtree = new BinaryNode<>(9);
        BinaryNode<Integer> childRightSubtree = new BinaryNode<>(14);


        BSTRotation<Integer> tree = new BSTRotation<>();
        tree.root = grandParentValue;

        grandParentValue.setLeft(parentValue);

        parentValue.setUp(grandParentValue);
        parentValue.setLeft(childValue);
        parentValue.setRight(parentRightSubtree);

        parentRightSubtree.setUp(parentValue);
        childLeftSubtree.setUp(childValue);
        childRightSubtree.setUp(childValue);

        childValue.setUp(parentValue);
        childValue.setRight(childRightSubtree);
        childValue.setLeft(childLeftSubtree);

        tree.rotate(childValue, parentValue);

        allPassedTest &= tree.root.getLeft() == childValue;
        allPassedTest &= grandParentValue.getLeft() == childValue;

        allPassedTest &= childValue.getUp() == tree.root;
        allPassedTest &= childValue.getLeft() == childLeftSubtree;
        allPassedTest &= childValue.getRight() == parentValue;
        allPassedTest &= childRightSubtree.getUp() == parentValue;

        allPassedTest &= parentValue.getUp() == childValue;
        allPassedTest &= parentValue.getRight() == parentRightSubtree;
        allPassedTest &= parentValue.getLeft() == childRightSubtree;

        return allPassedTest;
    }


    /**
     * Tester 2: left rotation with 1 child
     */
    public boolean test2(){
        Boolean allPassedTest = true;

        BinaryNode<Integer> grandParentValue = new BinaryNode<>(20);
        BinaryNode<Integer> parentValue = new BinaryNode<>(15);
        BinaryNode<Integer> parentLeftSubtree = new BinaryNode<>(10);
        BinaryNode<Integer> childValue = new BinaryNode<>(17);



        BSTRotation<Integer> tree = new BSTRotation<>();
        tree.root = grandParentValue;

        grandParentValue.setLeft(parentValue);

        parentValue.setUp(grandParentValue);
        parentValue.setLeft(parentLeftSubtree);
        parentValue.setRight(childValue);

        parentLeftSubtree.setUp(parentValue);

        childValue.setUp(parentValue);


        tree.rotate(childValue, parentValue);

        allPassedTest &= tree.root.getLeft() == childValue;
        allPassedTest &= grandParentValue.getLeft() == childValue;

        allPassedTest &= childValue.getUp() == tree.root;
        allPassedTest &= childValue.getLeft() == parentValue;
        allPassedTest &= childValue.getRight() == null;

        allPassedTest &= parentValue.getUp() == childValue;
        allPassedTest &= parentValue.getRight() == null;
        allPassedTest &= parentValue.getLeft() == parentLeftSubtree;

        return allPassedTest;
    }


    /**
     * Tester 3: parent as a root itself and rotation with two children
     */
    public boolean test3(){
        Boolean allPassedTest = true;

        BinaryNode<Integer> parentValue = new BinaryNode<>(15);

        BinaryNode<Integer> childValue = new BinaryNode<>(17);
        BinaryNode<Integer> childLeftSubtree = new BinaryNode<>(16);
        BinaryNode<Integer> childRightSubtree = new BinaryNode<>(19);


        BSTRotation<Integer> tree = new BSTRotation<>();
        tree.root = parentValue;



        parentValue.setUp(null);
        parentValue.setRight(childValue);


        childLeftSubtree.setUp(childValue);
        childRightSubtree.setUp(childValue);

        childValue.setUp(parentValue);
        childValue.setRight(childRightSubtree);
        childValue.setLeft(childLeftSubtree);

        tree.rotate(childValue, parentValue);

        allPassedTest &= tree.root == childValue;


        allPassedTest &= childValue.getUp() == null;
        allPassedTest &= childValue.getLeft() == parentValue;
        allPassedTest &= childValue.getRight() == childRightSubtree;
        allPassedTest &= childLeftSubtree.getUp() == parentValue;

        allPassedTest &= parentValue.getUp() == childValue;
        allPassedTest &= parentValue.getRight() == childLeftSubtree;
        allPassedTest &= parentValue.getLeft() == null;

        return allPassedTest;
    }



    /**
     * Tester 4: right rotation with 0 children
     */
    public boolean test4(){
        Boolean allPassedTest = true;

        BinaryNode<Integer> grandParentValue = new BinaryNode<>(20);
        BinaryNode<Integer> parentValue = new BinaryNode<>(15);
        BinaryNode<Integer> childValue = new BinaryNode<>(13);



        BSTRotation<Integer> tree = new BSTRotation<>();
        tree.root = grandParentValue;

        grandParentValue.setLeft(parentValue);
        parentValue.setUp(grandParentValue);
        parentValue.setLeft(childValue);
        childValue.setUp(parentValue);


        tree.rotate(childValue, parentValue);

        allPassedTest &= tree.root.getLeft() == childValue;
        allPassedTest &= grandParentValue.getLeft() == childValue;

        allPassedTest &= childValue.getUp() == tree.root;
        allPassedTest &= childValue.getLeft() == null;
        allPassedTest &= childValue.getRight() == parentValue;

        allPassedTest &= parentValue.getUp() == childValue;
        allPassedTest &= parentValue.getRight() == null;
        allPassedTest &= parentValue.getLeft() == null;

        return allPassedTest;
    }





    /**
     * Checks all the testers, whether they work correctly or not.
     */
    public static void main(String[] args) {
        BSTRotation<Integer> inTree = new BSTRotation<>();
        System.out.println("Checking Test1: " + inTree.test1());
        System.out.println("Checking Test2: " + inTree.test2());
        System.out.println("Checking Test3: " + inTree.test3());
        System.out.println("Checking Test4: " + inTree.test4());


    }
}



