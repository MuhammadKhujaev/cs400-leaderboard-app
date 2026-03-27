
/**
 * Red-Black Tree implementation that extends BSTRotation and uses RedBlackNode
 * to maintain balancing properties after insertion.
 *
 * Implements insertion repair through ensureRedProperty().
 */
public class RedBlackTree <T extends Comparable<T>> extends BSTRotation<T> {


      public RedBlackTree() {
 	   super();
      }



     /**
     * Checks if a new red node in the RedBlackTree causes a red property violation
     * by having a red parent. If this is not the case, the method terminates without
     * making any changes to the tree. If a red property violation is detected, then
     * the method repairs this violation and any additional red property violations
     * that are generated as a result of the applied repair operation.
     * Using this method might cause nodes with a value equal to the value of one of
     * their ancestors to appear within the left and the right subtree of that ancestor,
     * even if the original insertion procedure consistently inserts such nodes into only
     * the left or the right subtree. But it will preserve the ordering of nodes within
     * the tree.
     * @param newNode a newly inserted red node, or a node turned red by previous repair
     */
    protected void ensureRedProperty(RedBlackNode<T> newNode) {



        // Base cases: nothing to fix if newNode is null, parent is null, or parent is black
        // This method only repairs the "red parent of a red node" violation.
        if (newNode == null) return;
	RedBlackNode<T> parentNode = newNode.getUp();

	if (parentNode == null) return;
	RedBlackNode<T> grandParentNode = parentNode.getUp();
	RedBlackNode<T> aunt = null;

	if (parentNode.isBlackNode())  return;

        // If the parent is the root, just force it to black (root must always be black)
	if (grandParentNode == null) {
            parentNode.isBlackNode = true;
            return;
        }



	// Handling violation.
        // Determine the aunt (uncle): the sibling of parent (the other child of grandparent)
	if (parentNode == grandParentNode.getLeft()) { aunt = grandParentNode.getRight(); }
	else if (parentNode == grandParentNode.getRight()) { aunt = grandParentNode.getLeft();}


         // CASE 1: Aunt is red -> recolor (parent+aunt become black, grandparent becomes red),
        // then recursively fix grandparent if it now violates with its own parent.
	if (aunt != null && !aunt.isBlackNode()) {
	   parentNode.isBlackNode = true;
	   grandParentNode.isBlackNode = false;
	   aunt.isBlackNode = true;

	   // If grandParent now violates with its parent, fix upward
	   ensureRedProperty(grandParentNode);
	   return ;

	}


        // CASE 2 (LL): parent is left child and newNode is left child -> single rotation + recolor
	if (parentNode == grandParentNode.getLeft() && newNode == parentNode.getLeft() && (aunt == null || aunt.isBlackNode())) {

	         // rotate parent up over grandparent (right rotation)
                 rotate(parentNode, grandParentNode);

    		 //recolor
		 parentNode.isBlackNode = true;
		 grandParentNode.isBlackNode = false;
		 return;


	}


        // CASE 3 (LR): parent is left child and newNode is right child -> double rotation + recolor
	else if (parentNode == grandParentNode.getLeft() && newNode == parentNode.getRight() && (aunt == null || aunt.isBlackNode())) {

               rotate(newNode, parentNode);
	       parentNode = newNode;
	       rotate(parentNode, grandParentNode);


	       parentNode.isBlackNode = true;
	       if (parentNode.getLeft() != null) parentNode.getLeft().isBlackNode = false;
	       if (parentNode.getRight() != null) parentNode.getRight().isBlackNode = false;
	       return;

	}


         // CASE 4 (RR): parent is right child and newNode is right child -> single rotation + recolor
	 else if (parentNode == grandParentNode.getRight() && newNode == parentNode.getRight() && (aunt == null || aunt.isBlackNode())) {

	    // rotate parent up over grandparent (left rotation)
            rotate(parentNode, grandParentNode);

            // recolor
            parentNode.isBlackNode = true;
            grandParentNode.isBlackNode = false;
            return;


	}


         // CASE 5 (RL): parent is right child and newNode is left child -> double rotation + recolor
	 else if (parentNode == grandParentNode.getRight() && newNode == parentNode.getLeft() && (aunt == null || aunt.isBlackNode())) {

		rotate(newNode, parentNode);
		parentNode = newNode;
		rotate(parentNode, grandParentNode);


		parentNode.isBlackNode = true;
		if (parentNode.getLeft() != null) parentNode.getLeft().isBlackNode = false;
		if (parentNode.getRight() != null) parentNode.getRight().isBlackNode = false;
		return;


	}


    }



     @Override
     public void insert(T data) throws NullPointerException {


       if (data == null) throw new NullPointerException("Given data is empty, cannot be inserted.");


       RedBlackNode<T> newNode = new RedBlackNode<>(data);
       newNode.isBlackNode = false;

	if (this.root == null) {
           this.root = newNode;
           newNode.isBlackNode = true; 
       	   return;

        }


      // use BST insertHelper to place the node in correct position
      insertHelper(newNode, this.root);

      // fix red-red violations (new node is not root here)
      ensureRedProperty(newNode);

      // always ensure root is black after any fixes
      ((RedBlackNode<T>) this.root).isBlackNode = true;



      }




}
