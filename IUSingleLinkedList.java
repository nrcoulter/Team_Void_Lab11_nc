import java.util.*;

/**
 * Single-linked node implementation of IndexedUnsortedList.
 * An Iterator with working remove() method is implemented, but
 * ListIterator is unsupported.
 * 
 * @author 
 * 
 * @param <E> type to store
 */
public class IUSingleLinkedList<E> implements IndexedUnsortedList<E> {
	private LinearNode<E> front, rear;
	private int count;
	private int modCount;
	
	/** Creates an empty list */
	public IUSingleLinkedList() {
		front = rear = null;
		count = 0;
		modCount = 0;
	}

	@Override
	public void addToFront(E element) {
		add(0, element);
	}

	@Override
	public void addToRear(E element) {
		add(size(), element);
	}

	@Override
	public void add(E element) {
		addToRear(element);
	}

	@Override
	public void addAfter(E element, E target) {
		if (isEmpty()) throw new NoSuchElementException();

		LinearNode<E> temp = new LinearNode<E>(element);

		LinearNode<E> current = front;
		while (current != null && !current.getElement().equals(target)) {
			current = current.getNext();
		}
		if (current == null) throw new NoSuchElementException();

		temp.setNext(current.getNext());
		current.setNext(temp);
		if (current == rear) rear = temp; 

		temp = null;
		count++;
		modCount++;
	}

	@Override
	public void add(int index, E element) {
		if (index < 0 || index > size()) throw new IndexOutOfBoundsException();

		LinearNode<E> temp = new LinearNode<E>(element);

		if (index == 0) {
			if (isEmpty()) rear = temp;
			temp.setNext(front);
			front = temp;
		} else {
			LinearNode<E> current = front;
			for (int i = 0; i < index-1; i++) {
				current = current.getNext();
			}

			temp.setNext(current.getNext());
			current.setNext(temp);

			if (index == size()) rear = temp; 
		}

		temp = null;
		count++;
		modCount++;
	}

	@Override
	public E removeFirst() {
		if (isEmpty()) throw new NoSuchElementException();
		return remove(front.getElement());
	}

	@Override
	public E removeLast() {
		if (isEmpty()) throw new NoSuchElementException();
		return remove(rear.getElement());
	}

	@Override
	public E remove(E element) {
		if (isEmpty()) throw new NoSuchElementException();

		LinearNode<E> current = front, previous = null;
		while (current != null && !current.getElement().equals(element)) {
			previous = current;
			current = current.getNext();
		}
		// Matching element not found
		if (current == null) {
			throw new NoSuchElementException();
		}
		return removeElement(previous, current);		
	}

	@Override
	public E remove(int index) {
		if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

		LinearNode<E> current = front;
		LinearNode<E> previous = null;

		for (int i = 0; i < index; i++) {
			previous = current;
			current = current.getNext();
		}

		return removeElement(previous, current);
	}

	@Override
	public void set(int index, E element) {
		if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

		LinearNode<E> current = front;
		int i = 0;
		while (i != index) {
			current = current.getNext();
			i++;
		}
		current.setElement(element);
		modCount++;
	}

	@Override
	public E get(int index) {
		if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

		LinearNode<E> current = front;
		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}

		return current.getElement();
	}

	@Override
	public int indexOf(E element) {
		LinearNode<E> current = front;
		for (int i = 0; i < size(); i++) {
			if (current.getElement().equals(element)) {
				return i;
			}
			current = current.getNext();
		}
		return -1;
	}

	@Override
	public E first() {
		if (isEmpty()) throw new NoSuchElementException();
		return front.getElement();
	}

	@Override
	public E last() {
		if (isEmpty()) throw new NoSuchElementException();
		return rear.getElement();
	}

	@Override
	public boolean contains(E target) {
		LinearNode<E> current = front;
		while (current != null) {
			if (current.getElement().equals(target)) {
				return true;
			}
			current = current.getNext();
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return this.count;
	}

	@Override
	public String toString() {
		String result = "[";
		LinearNode<E> current = front;
		while (current != null) {
			result += current.getElement();
			if (current.getNext() != null) result += ", ";
			current = current.getNext();
		}
		return result + "]";
	}

	private E removeElement(LinearNode<E> previous, LinearNode<E> current) {
		// Grab element
		E result = current.getElement();
		// If not the first element in the list
		if (previous != null) {
			previous.setNext(current.getNext());
		} else { // If the first element in the list
			front = current.getNext();
		}
		// If the last element in the list
		if (current.getNext() == null) {
			rear = previous;
		}
		count--;
		modCount++;

		return result;
	}

	@Override
	public Iterator<E> iterator() {
		return new SLLIterator();
	}

	/** Iterator for IUSingleLinkedList */
	private class SLLIterator implements Iterator<E> {
		private LinearNode<E> previous;
		private LinearNode<E> current;
		private LinearNode<E> next;
		private int iterModCount, index;
		private boolean canRemove;
		
		/** Creates a new iterator for the list */
		public SLLIterator() {
			previous = null;
			current = null;
			next = front;
			iterModCount = modCount;
			canRemove = false;
			index = 0;
		}

		@Override
		public boolean hasNext() {
			if (iterModCount != modCount) throw new ConcurrentModificationException();
            return index < size();
		}

		@Override
		public E next() {
			if (!hasNext()) throw new NoSuchElementException();
            E item = next.getElement();

			previous = current;
			current = next;
			next = next.getNext();

			index++;
			canRemove = true;
			
            return item;
		}
		
		@Override
		public void remove() {
			if (iterModCount != modCount) throw new ConcurrentModificationException();
            if (!canRemove) throw new IllegalStateException();

			removeElement(previous, current);

			current = previous;
			index--;
			iterModCount++;
            canRemove = false;
		}
	}

	// IGNORE THE FOLLOWING CODE
	// DON'T DELETE ME, HOWEVER!!!
	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<E> listIterator(int startingIndex) {
		throw new UnsupportedOperationException();
	}
}
