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
        if (isEmpty())
            throw new NoSuchElementException();
        return removeElement(front);
	}

    @Override
    public E removeLast() {
        if (isEmpty())
            throw new NoSuchElementException();
        return removeElement(rear);
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

        // Set to null to avoid memory leak
        current.setNext(null);
        current.setPrevious(null);

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

			removeElement(current);

			current = previous;
			index--;
			iterModCount++;
            canRemove = false;
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
		
		public DLLListIterator(int startingIndex) {
            if (startingIndex > size() || startingIndex < 0) throw new IndexOutOfBoundsException();
			previous = null;
			current = null;
			next = front;
            
			iterModCount = modCount;
			currentIndex = startingIndex;
			state = CursorState.NEITHER;

            if (startingIndex > 0) moveToCorrectLoc(startingIndex);
		}

        @Override
        public boolean hasNext() {
            if (iterModCount != modCount) throw new ConcurrentModificationException();
            return currentIndex < size();
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            E result = next.getElement();

            previous = current;
			current = next;
			next = next.getNext();

            currentIndex++;
            state = CursorState.NEXT;

            return result;
        }

        @Override
        public boolean hasPrevious() {
            if (iterModCount != modCount) throw new ConcurrentModificationException();
            return currentIndex > 0;
        }

        /* VV Need to finish VV */
        @Override
        public E previous() {
            if (!hasPrevious()) throw new NoSuchElementException();
            E result = current.getElement();

            next = current;
            current = previous;
            previous = previous != null ? previous.getPrevious() : null;

            currentIndex--;
            state = CursorState.PREVIOUS;

            return result;
        }

        @Override
        public int nextIndex() {
            return currentIndex;
        }

        @Override
        public int previousIndex() {
            return currentIndex - 1;
        }

        /* VV Need to finish VV */
        @Override
        public void remove() {
            if (iterModCount != modCount) throw new ConcurrentModificationException();
            switch (state) {
                case NEXT:
                    removeElement(current);
                    current = previous;
                    currentIndex--;
                    break;
                case PREVIOUS:
                    removeElement(next);
                    // After removving, the node that was after next is now the new next
                    if (current == null) {
                        // removed the first element, next is the new front
                        next = front;
                    } else {
                        // next is whatever follows the current position
                        next = current.getNext();
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }

            iterModCount++;
            state = CursorState.NEITHER;
        }

        @Override
        public void set(E e) {
            switch (state) {
                case NEXT:
                    current.setElement(e);
                    break;
                case PREVIOUS:
                    next.setElement(e);
                    break;
                default:
                    throw new IllegalStateException();
            }

            state = CursorState.NEITHER;
        }

        /* VV Need to finish VV */
        @Override
        public void add(E e) {
            if (iterModCount != modCount) throw new ConcurrentModificationException();
            IUDoubleLinkedList.this.add(currentIndex, e); 

            // Update the iterator's 'previous' pointer to point to the new node
            if (next == null) {
                // Cursor is at the end of the list, the new node is the rear
                current = rear;
                // Decide what the previous pointer should be based on current position
                if (current != null) {
                    // If the current node exists, then previous is the node behind
                    previous = current.getPrevious();
                } else {
                    // If current is null (front of the list), previous must be null
                    previous = null;
                }
            } else {
                // Cursor is in the middle, the new node is behind next
                current = next.getPrevious();
                previous = current.getPrevious();
            }

            currentIndex++;
            iterModCount++;
            state = CursorState.NEITHER;
        }

        private void moveToCorrectLoc(int index) {
            for (int i = 0; i < index; i++) {
                previous = current;
                current = next;
                next = next.getNext();
		    }
        }
        
    }

    private enum CursorState { NEXT, PREVIOUS, NEITHER };

}
