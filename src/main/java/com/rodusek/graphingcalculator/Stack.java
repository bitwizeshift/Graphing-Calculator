package com.rodusek.graphingcalculator;

import java.util.EmptyStackException;

/**
 * The <code>Stack</code> class represents a last-in-first-out (LIFO) stack of objects. It is implemented through a series of linked <code>Node</code>s.
 * The usual push and pop operations are provided, as well as a method to <code>peek</code> at the top item on the stack, and a method to test for whether the stack <code>is empty</code>.
 * <p>When a stack is first created, it contains no items.</p>
 * 
 * @author Matthew Rodusek, 120184140, rodu4140@mylaurier.ca
 * @version 1.0, 09/09/13
 * @since 1.0
 * @see rodu4140.Node
 */
public class Stack<E> {
	
	private Node<E> top;
	
	/**
	 * Creates an empty Stack.
	 * 
	 * <p><b>Usage:</b></p>
	 * <code>Stack s = new Stack();</code></br></br>
	 * @see rodu4140.Node
	 */
	public Stack(){
		this.top = null;
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Removes the object at the top of this stack and returns that object as the value of this function.
	 * 
	 * @return The object at the top of this stack
	 * @throws EmptyStackException if this stack is empty
	 */
	public synchronized E pop() throws EmptyStackException{
		if(this.top==null) throw new EmptyStackException();
		E value = this.top.getElement();
		this.top = this.top.getNext();
		return value;
	}
	
	/**
	 * Looks at the object at the top of this stack without removing it from the stack.
	 * 
	 * @return the object at the top of this stack
	 * @throws EmptyStackException if this stack is empty
	 */
	public synchronized E peek() throws EmptyStackException{
		if(this.top==null) throw new EmptyStackException();
		E value = this.top.getElement();
		return value;			
	}
	
	/**
	 * Pushes an item onto the top of this stack.
	 * 
	 * @param element the item to be pushed onto this stack.
	 */
	public synchronized void push(E element){
		this.top = new Node<E>(element, this.top);
	}
	
	// ---------------------------------------------------------------------------------
	
	/**
	 * Tests if the stack is empty.
	 * 
	 * @return <code>true</code> if and only if the stack is empty, <code>false</code> otherwise
	 */
	public boolean isEmpty(){
		return this.top==null;
	}
}
