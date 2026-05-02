import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IUDoubleLinkedList<E> implements IndexedUnsortedList<E> {

    private BidirectionalNode<E> front, rear;
	private int count;
	private int modCount;

    public IUDoubleLinkedList() {
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
        int targetIdx = indexOf(target);

        if (targetIdx < 0) throw new NoSuchElementException();
		else add(targetIdx + 1, element);
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size()) throw new IndexOutOfBoundsException();

        BidirectionalNode<E> temp = new BidirectionalNode<E>(element);

        if (index == 0) {
            temp.setNext(front);
            front = temp;
            if (isEmpty()) rear = temp;
            else temp.getNext().setPrevious(temp);
        } else {
            BidirectionalNode<E> current = front;
			for (int i = 0; i < index - 1; i++) {
				current = current.getNext();
			}

            temp.setNext(current.getNext());
            current.setNext(temp);
            temp.setPrevious(current);

			if (index == size()) rear = temp;

            else temp.getNext().setPrevious(temp);
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

		BidirectionalNode<E> current = front;
		while (current != null && !current.getElement().equals(element)) {
			current = current.getNext();
		}

		// Matching element not found
		if (current == null) {
			throw new NoSuchElementException();
		}

		return removeElement(current);	
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

		BidirectionalNode<E> current = front;

		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}

		return removeElement(current);
    }

    private E removeElement(BidirectionalNode<E> current) {
		// Grab element
		E result = current.getElement();

        if (current.getPrevious() != null) { // If not the first element in the list
            current.getPrevious().setNext(current.getNext());

            if (current.getNext() != null) { // In case that current is the last element
                current.getNext().setPrevious(current.getPrevious());
            }
        } else { // If the first element in the list
            front = current.getNext();
            if (front != null) front.setPrevious(null);
        }

        if (current.getNext() == null) { // If the last element in the list
            rear = current.getPrevious();
        }

		count--;
		modCount++;

		return result;
	}

    @Override
    public void set(int index, E element) {
        if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();

		BidirectionalNode<E> current = front;
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

		BidirectionalNode<E> current = front;
		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}

		return current.getElement();
    }

    @Override
    public int indexOf(E element) {
        int NOT_FOUND = -1;
        BidirectionalNode<E> current = front;
		for (int i = 0; i < size(); i++) {
			if (current.getElement().equals(element)) {
				return i;
			}
			current = current.getNext();
		}
		return NOT_FOUND;
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
        return indexOf(target) == -1 ? false : true;
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
		BidirectionalNode<E> current = front;
		while (current != null) {
			result += current.getElement();
			if (current.getNext() != null) result += ", ";
			current = current.getNext();
		}
		return result + "]";
	}

    @Override
    public Iterator<E> iterator() {
        return new DLLIterator();
    }

    private class DLLIterator implements Iterator<E> {
		private BidirectionalNode<E> previous;
		private BidirectionalNode<E> current;
		private BidirectionalNode<E> next;
		private int iterModCount, index;
		private boolean canRemove;
		
		/** Creates a new iterator for the list */
		public DLLIterator() {
			previous = null;
			current = null;
			next = front;
			iterModCount = modCount;
			canRemove = false;
			index = 0;
		}

        @Override
        public boolean hasNext() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'hasNext'");
        }

        @Override
        public E next() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'next'");
        }

        @Override
        public void remove() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'remove'");
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        return new DLLListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int startingIndex) {
        return new DLLListIterator(startingIndex);
    }

    private class DLLListIterator implements ListIterator<E> {
		private BidirectionalNode<E> previous;
		private BidirectionalNode<E> current;
		private BidirectionalNode<E> next;
		private int iterModCount, currentIndex;
		private CursorState state;
		
		/** Creates a new iterator for the list */
		public DLLListIterator(int startingIndex) {
			previous = null;
			current = null;
			next = front;
            
			iterModCount = modCount;
			currentIndex = startingIndex;
			state = CursorState.NEITHER;
		}

        @Override
        public boolean hasNext() {
            if (iterModCount != modCount) throw new ConcurrentModificationException();
            return currentIndex < size() + 1;
        }

        @Override
        public E next() {
            E result = get(currentIndex);

            state = CursorState.NEXT;
            currentIndex++;

            return result;
        }

        @Override
        public boolean hasPrevious() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'hasPrevious'");
        }

        @Override
        public E previous() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'previous'");
        }

        @Override
        public int nextIndex() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'nextIndex'");
        }

        @Override
        public int previousIndex() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'previousIndex'");
        }

        @Override
        public void remove() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'remove'");
        }

        @Override
        public void set(E e) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'set'");
        }

        @Override
        public void add(E e) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'add'");
        }
        
    }

    private enum CursorState { NEXT, PREVIOUS, NEITHER };

}
