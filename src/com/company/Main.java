// Anesh Turk
// 2-3-4 (B-tree) tree implementation
package com.company;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        BTree tree = new BTree();

        while (true){
            Scanner input = new Scanner(System.in);
            System.out.println("Insert Nodes: (Enter \"D\" to begin deleting nodes)");
            String inputTxt =  input.next();

            // if we are not going to start the process of deletion, then we are inserting
            if (inputTxt != "D"){
                tree.insertKey(Integer.parseInt(inputTxt));
            }
        }

    }
}

class BTree {
    Node root;

    // default constructor makes the root of the tree null
    public BTree(){
        root = null;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    void insertKey(int k){
        Node newNode = new Node(true);
        newNode.addKey(k);

        // if there are no nodes in the tree currently
        if (this.root == null){
            root = newNode;
        }
        else {
            this.root = recursiveInsert(this.root, k);
        }
        printTree();
    }


    private Node recursiveInsert(Node rootPtr, int newKey){
        // if we come upon a node with 3 keys, we must split it
        if (rootPtr.getNumKey() == 3){
            // split node with children
            if (rootPtr.getChildren().size() > 0 ){
                rootPtr = splitNodeWithChildren(rootPtr);
            }
            // split node without any children
            else
                rootPtr = splitNode(rootPtr);
        }
        // if our new key is larger than the greatest value in a node and the node is not a leaf, recurse to the right child node
        if (!rootPtr.isLeaf) {
            if (newKey > rootPtr.getLargestKey()) {
                // select the rightmost (max) child
                if (rootPtr.getLargestChild().getNumKey() == 3){
                    rootPtr = splitNode(rootPtr, rootPtr.getLargestChild());
                }
                recursiveInsert(rootPtr.getLargestChild(), newKey);
            }
            else {
                // select the leftmost (min) child at index 0
                recursiveInsert(rootPtr.getChildren().get(0), newKey);
            }
        }
        else
            rootPtr.addKey(newKey);

        return rootPtr;
    }

    private Node splitNode(Node ptr){
        ArrayList<Integer> keys = ptr.getKeys();
        Node newRoot = null;
        // if we have to split the root node
            newRoot = new Node(false, keys.get(1));

            Node leftNode = new Node(true, keys.get(0));
            Node rightNode = new Node(true, keys.get(2));

            newRoot.addChild(leftNode);
            newRoot.addChild(rightNode);

        return newRoot;
    }

    // used to split a non root node
    private Node splitNode(Node rootPtr, Node splitNode){
        Node leftChild = new Node(true);
        Node rightChild = new Node(true);

        ArrayList<Integer> keys = splitNode.getKeys();

        // add the middle key to the root pointer
        rootPtr.addKey(keys.get(1));

        // add the smallest key to the left child
        leftChild.addKey(keys.get(0));

        // add the largest key to the right child
        rightChild.addKey(keys.get(2));

        rootPtr.deleteChild(splitNode);
        rootPtr.addChild(leftChild);
        rootPtr.addChild(rightChild);

        return rootPtr;
    }

    private Node splitNodeWithChildren(Node rootPtr) {
        Node leftChild = new Node(false);
        Node rightChild = new Node(false);
        Node newRoot = new Node(false);

        ArrayList<Node> children = rootPtr.getChildren();
        ArrayList<Integer> keys = rootPtr.getKeys();

        int middleKey = keys.get(1);

        // add the middle key to the root pointer
        newRoot.addKey(middleKey);

        // add the smallest key to the left child
        leftChild.addKey(keys.get(0));

        // add the largest key to the right child
        rightChild.addKey(keys.get(2));

        for (int i = 0; i< children.size(); i++){
            if (middleKey > Collections.max(children.get(i).getKeys())){
                leftChild.addChild(children.get(i));
            }
            else
                rightChild.addChild(children.get(i));
        }

        newRoot.addChild(leftChild);
        newRoot.addChild(rightChild);

        return newRoot;
    }


    void printTree(){
        if (root != null){
            recursivePrintTree(root, 0);
            System.out.println();
        }
        else
            System.out.println("The tree is empty");
    }

    // this does a preorder traversal of the tree
    void recursivePrintTree(Node rootPtr, int depth) {
        System.out.println("Depth level " + depth + " " + rootPtr.getKeys());

        if (rootPtr.isLeaf){
            return;
        }
        else {
            System.out.print("Depth level " + (depth + 1) + " ");
            for (int i = 0; i<rootPtr.getChildren().size(); i++) {
                System.out.print("|" + rootPtr.getChildren().get(i).getKeys() + "|  ");
            }

            System.out.println();
            System.out.print("Depth level " + (depth + 2) + " ");
            for (int i = 0; i<rootPtr.getChildren().size(); i++) {
                for (int j = 0; j < rootPtr.getChildren().get(i).getChildren().size(); j++)
                    System.out.print("|" + rootPtr.getChildren().get(i).getChildren().get(j).getKeys() + "|  ");
            }
        }
    }


}


class Node implements Comparable<Node> {
    ArrayList<Integer> keys = new ArrayList<Integer>();
    ArrayList<Node> children = new ArrayList<Node>();
    int numKey;
    Boolean isLeaf;

    // default constructor
    public Node(Boolean leaf){
        isLeaf = leaf;
        numKey = 0;
    }

    // overloaded constructor
    public Node(Boolean leaf, int key){
        isLeaf = leaf;
        numKey = 0;
        addKey(key);
    }

    @Override
    public int compareTo(Node d) {
        // this will compare the largest values in each node
        return Collections.max(this.keys) - Collections.max(d.getKeys());
    }

    // this will add a key
    public void addKey(int key) {
        this.keys.add(key);
        this.keys.sort(Comparator.comparingInt(Integer::valueOf));
        Collections.sort(this.children);
        numKey++;
    }

    // this will add a child
    public void addChild(Node child) {
        this.children.add(child);
        this.keys.sort(Comparator.comparingInt(Integer::valueOf));
        numKey++;
    }

    public void deleteChild(Node child){
        this.children.remove(child);
    }

    public int getLargestKey(){
        return Collections.max(this.keys);
    }

    public int getSmallestKey(){
        return Collections.min(this.keys);
    }

    public Node getLargestChild(){
        return Collections.max(this.children);
    }

    public void setKeyList(ArrayList<Integer> k){
        this.keys = k;
    }

    // getters and setters
    public ArrayList<Integer> getKeys() {
        return keys;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public int getNumKey() {
        numKey = this.keys.size();
        return numKey;
    }

    public void setLeaf(Boolean leaf) {
        isLeaf = leaf;
    }

    public Boolean getLeaf() {
        return isLeaf;
    }
}