package com.rodusek.graphingcalculator;

/**
 * The <code>Node</code> class contains only two pieces of data; a value, and the next <code>Node</code> in the list
 * <p>It can be constructed empty, or with data depending on requirements</p>
 * 
 * @author Matthew Rodusek
 * @version 1.0, 09/09/13
 * @since 1.0
 */
public class Node<E> {
    
    private E         element;
    private Node<E> next;
    
    /**
     * Creates an empty <code>Node</code>
     * 
     */
    public Node(){
        this.element = null;
        this.next = null;
    }
    
    /**
     * Creates and initializes a <code>Node</code>
     * 
     * @param element The element of the StackNode
     * @param next The next Node in the list
     */
    public Node(E element, Node<E> next){
        this.element = element;
        this.next = next;
    }
    
    // ---------------------------------------------------------------------------------
    
    /**
     * Returns the value of the node
     */
    public E getElement(){
        return this.element;
    }
    
    /**
     * Returns the next node pointed at by this node
     */
    public Node<E> getNext(){
        return this.next;
    }
    
    // ---------------------------------------------------------------------------------
    
    /**
     * Sets the new value of the node
     * 
     * @param element the new value of the node
     */
    public void setElement(E element){
        this.element = element;
    }
    
    /**
     * Sets the new next node to be pointed at
     * 
     * @param next the next node to be set
     */
    public void setNext(Node<E> next){
        this.next = next;
    }
}