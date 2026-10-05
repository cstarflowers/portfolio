package refactor;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EndlessLinkedList<T> extends StaticEndlessLinkedList<T> {
    protected Node<T> tail;

    /**
     * A constructor used to create a new EndlessLinkedList.
     * Instantiates variables head, tail, and size to all be 0 or null, resetting the list.
     */
    public EndlessLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public void addFirst(T data) {
        if (data == null) {
            throw new IllegalArgumentException("You can not add a null object to this list.");
        }
        Node<T> newNode = new Node<>(data, head);
        head = newNode;
        if (size == 0) {
            tail = head;
        }
        size++;
    }

    @Override
    public void addLast(T data) {
        if (data == null) {
            throw new IllegalArgumentException("You can not add a null object to this list.");
        }
        Node<T> newNode = new Node<>(data);
        if (size == 0) {
            head = new Node<>(data);
            tail = head;
        } else if (tail == null) {
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = tail.getNext();
        }
        size++;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("There is no object to remove");
        } else {
            T removed = head.getData();
            if (size == 1) {
                head = null;
            } else {
                head = head.getNext();
            }
            size--;
            return removed;
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("There is no object to remove");
        } else {
            T removed = tail.getData();
            if (size == 1) {
                head = null;
                tail = null;
            } else {
                Node<T> temp = head;
                while (size >= 2 && temp.getNext().getNext() != null) {
                    temp = temp.getNext();
                }
                temp.setNext(null);
                tail = temp;
            }
            size--;
            return removed;
        }
    }

    @Override
    public int removeValue(T data) {
        if (data == null) {
            throw new IllegalArgumentException("You can not remove a null object from this list.");
        } else if (size == 0) {
            throw new IndexOutOfBoundsException("There is no object to remove");
        }
        Node<T> temp = head;
        if (head.getData().equals(data)) {
            removeFirst();
            return 0;
        } else if (tail.getData().equals(data)) {
            removeLast();
            return size;
        }
        for (int i = 0; i < size - 1; i++) {
            if (temp.getNext().getData().equals(data)) {
                temp.setNext(temp.getNext().getNext());
                if (temp.getNext() == null) {
                    tail = temp;
                }
                size--;
                return i + 1;
            } else {
                temp = temp.getNext();
            }
        }
        throw new NoSuchElementException("The element you're removing does not exist in the list.");
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("The index you provided is greater than the list length.");
        } else if (index < 0) {
            throw new IndexOutOfBoundsException("The index you're retrieving can not be less than 0.");
        }
        Node<T> temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.getNext();
            if (temp == null) {
                throw new IndexOutOfBoundsException("The index you provided does not exist in the list.");
            }
        }
        return temp.getData();
    }

    @Override
    public void reverse() {
        if (size == 0) {
            throw new IllegalStateException("The list is empty. There is nothing to reverse!");
        } else if (size == 1) {
            return;
        }
        Node<T> prev = null;
        Node<T> next = null;
        Node<T> temp = head;

        while (temp != null) {
            next = temp.getNext();
            temp.setNext(prev);
            prev = temp;
            temp = next;
        }
        head = prev;
    }

    @Override
    public Iterator<T> iterator() {
        return new EndlessLinkedListIterator<>();
    }

    private class EndlessLinkedListIterator<E> implements Iterator<E> {
        private Node<T> index;

        /**
         * A constructor used to create a new iterator with a default index value of the first item in the list (head).
         * This index is then iterated through until it is null, meaning the list has ended.
         */
        public EndlessLinkedListIterator() {
            index = head;
        }

        @Override
        public boolean hasNext() {
            return size > 0;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new UnsupportedOperationException("There is no next element to be iterated through!");
            } else if (size == 1) {
                return (E) head.getData();
            }
            E data = (E) index.getData();
            if (index.getNext() == null) {
                index = head;
            } else {
                index = index.getNext();
            }
            return data;
        }
    }
}
