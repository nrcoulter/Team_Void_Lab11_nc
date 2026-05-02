import java.util.*;

/**
 * Array-based implementation of IndexedUnsortedList.
 * 
 * @author Team Void
 *
 * @param <E> type to store
 */
public class IUArrayList<E> implements IndexedUnsortedList<E> {
	private static final int DEFAULT_CAPACITY = 10;
	private static final int NOT_FOUND = -1;

	private E[] list;
	private int rear, count;
	private int modCount; // DO NOT REMOVE ME

	/** Creates an empty list with default initial capacity */
	public IUArrayList() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Creates an empty list with the given initial capacity
	 * 
	 * @param initialCapacity
	 */
	@SuppressWarnings("unchecked")
	public IUArrayList(int initialCapacity) {
		list = (E[]) (new Object[initialCapacity]);
		rear = count = 0;

		modCount = 0; // DO NOT REMOVE ME
	}

	/** Double the capacity of array */
	private void expandCapacity() {
		list = Arrays.copyOf(list, list.length * 2);
	}

	@Override
	public void addToFront(E element) {
		if (size() == this.list.length) {
			expandCapacity();
		}
		for (int i = rear; i > 0; i--) {
			list[i] = list[i - 1];
		}
		list[0] = element;
		count++;
		rear++;

		modCount++; // DO NOT REMOVE ME
	}

	@Override
	public void addToRear(E element) {
		if (size() == this.list.length) {
			expandCapacity();
		}
		list[rear] = element;
		count++;
		rear++;

		modCount++; // DO NOT REMOVE ME
	}

	@Override
	public void add(E element) {
		if (size() == this.list.length) {
			expandCapacity();
		}
		list[rear] = element;
		count++;
		rear++;

		modCount++; // DO NOT REMOVE ME
	}

	@Override
	public void addAfter(E element, E target) {
		if (size() == this.list.length) {
			expandCapacity();
		}

		int index = indexOf(target);
		if (index == NOT_FOUND) throw new NoSuchElementException();

		for (int i = rear; i > index; i--) {
			list[i] = list[i - 1];
		}
		list[index+1] = element;
		count++;
		rear++;

		modCount++; // DO NOT REMOVE ME
	}

	@Override
	public void add(int index, E element) {
		if (size() == this.list.length) {
			expandCapacity();
		}

		if (index < 0 || index > rear) throw new IndexOutOfBoundsException();

		for (int i = rear; i > index; i--) {
			list[i] = list[i - 1];
		}
		list[index] = element;
		count++;
		rear++;

		modCount++; // DO NOT REMOVE ME
	}

	@Override
	public E removeFirst() {
		if (isEmpty()) throw new NoSuchElementException();
		E result = list[0];

		remove(result);

		modCount++; // DO NOT REMOVE ME
		return result;
	}

	@Override
	public E removeLast() {
		if (isEmpty()) throw new NoSuchElementException();

		E result = list[rear-1];
		list[rear-1] = null;
		rear--;
		count--;

		modCount++; // DO NOT REMOVE ME
		return result;
	}

	@Override
	public E remove(E element) {
		if (isEmpty()) throw new NoSuchElementException();

		int index = indexOf(element);
		if (index == NOT_FOUND) {
			throw new NoSuchElementException();
		}

		E retVal = list[index];

		rear--;
		// shift elements
		for (int i = index; i < rear; i++) {
			list[i] = list[i + 1];
		}
		list[rear] = null;
		count--;

		modCount++; // DO NOT REMOVE ME
		return retVal;
	}

	@Override
	public E remove(int index) {
		if (isEmpty()) throw new IndexOutOfBoundsException();
		
		if (index < 0 || index >= rear) {
			throw new IndexOutOfBoundsException();
		}

		E retVal = list[index];

		rear--;
		// shift elements
		for (int i = index; i < rear; i++) {
			list[i] = list[i + 1];
		}
		list[rear] = null;
		count--;

		modCount++; // DO NOT REMOVE ME
		return retVal;
	}

	@Override
	public void set(int index, E element) {
		if (isEmpty()) throw new IndexOutOfBoundsException();
		
		if (index < 0 || index >= rear) {
			throw new IndexOutOfBoundsException();
		}
		list[index] = element;

		modCount++; // DO NOT REMOVE ME
	}

	@Override
	public E get(int index) {
		if (isEmpty()) throw new IndexOutOfBoundsException();
		
		if (index < 0 || index >= rear) {
			throw new IndexOutOfBoundsException();
		}

		return list[index];
	}

	@Override
	public int indexOf(E element) {
		int index = NOT_FOUND;

		if (!isEmpty()) {
			int i = 0;
			while (index == NOT_FOUND && i < rear) {
				if (element.equals(list[i])) {
					index = i;
				} else {
					i++;
				}
			}
		}

		return index;
	}

	@Override
	public E first() {
		if (isEmpty()) throw new NoSuchElementException();
		return list[0];
	}

	@Override
	public E last() {
		if (isEmpty()) throw new NoSuchElementException();
		return list[rear-1];
	}

	@Override
	public boolean contains(E target) {
		return (indexOf(target) != NOT_FOUND);
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
;
        for (int i = 0; i < size(); i++) {
            if (i > 0) {
                result += ", ";
            }
            result += list[i];
        } 

        result += "]";
		return result;
	}

	// IGNORE THE FOLLOWING COMMENTED OUT CODE UNTIL LAB 10
	// DON'T DELETE ME, HOWEVER!!!
	public Iterator<E> iterator() {
		return new IUArrayListIterator(); // UNCOMMENT ME IN LAB 10
		return null; // REMOVE ME IN LAB 10
	}

	// UNCOMMENT THE CODE BELOW IN LAB 10

	private class IUArrayListIterator implements Iterator<E> {

	private int iterModCount, current;
	private boolean canRemove;

	public IUArrayListIterator() {
	iterModCount = modCount;
	current = 0;
	canRemove = false;
	}

	@Override
	public boolean hasNext() {
	if (iterModCount != modCount) {
	throw new ConcurrentModificationException();
	}
	return current < rear;
	}

	@Override
	public E next() {
	if (!hasNext()) {
	throw new NoSuchElementException();
	}
	E item = array[current];
	current++;
	canRemove = true;
	return item;
	}

	@Override
	public void remove() {
	if (iterModCount != modCount) {
	throw new ConcurrentModificationException();
	}
	if (!canRemove) {
	throw new IllegalStateException();
	}
	// remove the element in the array at index current-1
	// presumably decrement the rear
	// presumably the modCount is getting incremented
	// all indices have to back up by one
	current--;
	rear--;
	// shift elements to the left
	for (int i = current; i < rear; i++) {
	array[i] = array[i + 1];
	}
	array[rear] = null;
	modCount++;
	iterModCount++;
	// Can only remove the LAST "seen" element
	// set back to a non-removal state
	canRemove = false;
	}

	// }

	// IGNORE THE FOLLOWING CODE
	// DON'T DELETE ME, HOWEVER!!!
	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int startingIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
