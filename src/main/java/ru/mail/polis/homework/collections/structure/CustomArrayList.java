package ru.mail.polis.homework.collections.structure;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Необходимо реализовать свой ArrayList (динамический массив).
 * При изменении размера массива помните про метод System.arraycopy()
 * <p>
 * Задание оценивается в 10 тугриков
 */
public class CustomArrayList<E> implements List<E> {
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static final int DEFAULT_CAPACITY = 10;
    private static final double GROW_FACTOR = 1.5;
    private E[] data;
    private int size;
    int modCount;

    public CustomArrayList(CustomArrayList<E> list, int fromInclusive, int toExclusive) {
        size = toExclusive - fromInclusive;
        data = (E[]) new Object[size];
        System.arraycopy(list.data, fromInclusive, data, 0, size);
        modCount = list.modCount;
    }

    private CustomArrayList(E[] subList, int toIndex, int fromIndex) {
        size = toIndex - fromIndex;
        data = subList;
    }

    public CustomArrayList(int initCapacity) {
        if (initCapacity < 0) {
            throw new IllegalArgumentException("Capacity should be non-negative");
        }
        this.data = (E[]) new Object[initCapacity];
        modCount = 0;
    }

    public CustomArrayList() {
        this(DEFAULT_CAPACITY);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final int expectedModCount = modCount;
            private int currentIndex;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public E next() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return data[currentIndex++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(data, size, a.getClass());
        }
        System.arraycopy(data, 0, a, 0, size);
        return a;
    }

    @Override
    public boolean add(E e) {
        modCount++;
        if (size == data.length) {
            data = grow();
        }
        data[size++] = e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int firstCurrIndex = indexOf(o);
        if (firstCurrIndex != -1) {
            remove(firstCurrIndex);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object elem : c) {
            if (!contains(elem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        modCount++;
        return addAll(size, c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }

        modCount++;
        int cSize = c.size();
        if (data.length - size < cSize) {
            data = grow(size + cSize);
        }
        if (index != size) {
            System.arraycopy(data, index, data, index + cSize, size - index);
        }
        System.arraycopy(c.toArray(), 0, data, index, cSize);
        size += cSize;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return removeOrRetainAll(c, true);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return removeOrRetainAll(c, false);
    }

    @Override
    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    @Override
    public E get(int index) {
        return data[index];
    }

    @Override
    public E set(int index, E element) {
        modCount++;
        E oldValue = data[index];
        data[index] = element;
        return oldValue;
    }

    @Override
    public void add(int index, E element) {
        modCount++;
        if (size == data.length) {
            data = grow();
        }
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    @Override
    public E remove(int index) {
        E oldValue = data[index];
        modCount++;
        int newSize;
        if ((newSize = size - 1) > index) {
            System.arraycopy(data, index + 1, data, index, newSize - index);
        }
        size = newSize;
        data[size] = null;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        return indexOfRange(o, 0, size);
    }

    @Override
    public int lastIndexOf(Object o) {
        return lastIndexOfRange(o, 0, size);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListIterator<E>() {
            private int currentIndex = index;
            private int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public E next() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return data[currentIndex++];
            }

            @Override
            public boolean hasPrevious() {
                return currentIndex > 0;
            }

            @Override
            public E previous() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return data[--currentIndex];
            }

            @Override
            public int nextIndex() {
                return currentIndex;
            }

            @Override
            public int previousIndex() {
                return currentIndex - 1;
            }

            @Override
            public void remove() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                CustomArrayList.this.remove(currentIndex);
                expectedModCount = modCount;
            }

            @Override
            public void set(E e) {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                CustomArrayList.this.set(currentIndex, e);
                expectedModCount = modCount;
            }

            @Override
            public void add(E e) {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                CustomArrayList.this.add(currentIndex, e);
                currentIndex++;
                expectedModCount = modCount;
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        return new SubList<>(this, fromIndex, toIndex);
    }

    private int indexOfRange(Object o, int index, int size) {
        if (o == null) {
            for (int i = index; i < size; i++) {
                if (data[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = index; i < size; i++) {
                if (o.equals(data[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int lastIndexOfRange(Object o, int index, int size) {
        if (o == null) {
            for (int i = size - 1; i >= index; i--) {
                if (data[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = size - 1; i >= index; i--) {
                if (o.equals(data[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    private E[] grow() {
        return grow((int) (GROW_FACTOR * size));
    }

    private E[] grow(int newCapacity) {
        if (newCapacity > MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError();
        }
        return Arrays.copyOf(data, newCapacity);
    }


    private boolean removeOrRetainAll(Collection<?> c, boolean isRemoved) {
        boolean isModified = false;
        for (ListIterator<E> iterator = this.listIterator(); iterator.hasNext(); ) {
            E currElem = iterator.next();
            if (isRemoved ^ !c.contains(currElem)) {
                iterator.previous();
                iterator.remove();
                isModified = true;
            }
        }
        return isModified;
    }

    private static class SubList<E> implements List<E> {
        private final CustomArrayList<E> parentList;
        private final int startPosition;
        private int endPosition;

        public SubList(CustomArrayList<E> parentList, int startPosition, int endPosition) {
            this.parentList = parentList;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        @Override
        public int size() {
            return endPosition - startPosition;
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public void add(int index, E element) {
            endPosition++;
            parentList.add(startPosition + index, element);
        }

        @Override
        public E remove(int index) {
            endPosition--;
            return parentList.remove(startPosition + index);
        }

        @Override
        public int indexOf(Object o) {
            return parentList.indexOfRange(o, startPosition, size()) - startPosition;
        }

        @Override
        public int lastIndexOf(Object o) {
            return parentList.lastIndexOfRange(o, startPosition, size()) - startPosition;
        }

        //TODO listIterator
        @Override
        public ListIterator<E> listIterator() {
            return parentList.listIterator(startPosition);
        }

        //TODO listIterator#2
        @Override
        public ListIterator<E> listIterator(int index) {
            return parentList.listIterator(startPosition + index);
        }

        //TODO sbuList
        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return null;
        }

        @Override
        public boolean contains(Object object) {
            return this.indexOf(object) != -1;
        }

        //TODO iterator
        @Override
        public Iterator<E> iterator() {
            return parentList.iterator();
        }

        @Override
        public Object[] toArray() {
            Object[] temp = new Object[endPosition - startPosition];
            System.arraycopy(parentList.data, startPosition, temp, 0, endPosition - startPosition);
            return Arrays.copyOf(temp, endPosition - startPosition);
        }

        @Override
        public <T> T[] toArray(T[] a) {
            if (a.length < size()) {
                return (T[]) Arrays.copyOf(parentList.data, size(), a.getClass());
            }
            System.arraycopy(parentList.data, 0, a, 0, size());
            return a;
        }

        @Override
        public boolean add(E e) {
            endPosition++;
            parentList.add(startPosition + size(), e);
            return true;
        }

        @Override
        public boolean remove(Object o) {
            int currIndex = this.indexOf(o);
            if (startPosition <= currIndex && currIndex <= endPosition) {
                parentList.remove(currIndex);
                endPosition--;
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object elem : c) {
                if (!this.contains(elem)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return this.addAll(startPosition, c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            endPosition += c.size();
            return parentList.addAll(startPosition + index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return removeOrRetainAll(c, true);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return removeOrRetainAll(c, false);
        }

        @Override
        public void clear() {
            parentList.modCount++;
            for (int i = endPosition - 1; i >= startPosition; i--) {
                parentList.remove(i);
            }
            endPosition = startPosition;
        }

        @Override
        public E get(int index) {
            return parentList.get(startPosition + index);
        }

        @Override
        public E set(int index, E element) {
            return parentList.set(startPosition + index, element);
        }

        private boolean removeOrRetainAll(Collection<?> c, boolean isRemoved) {
            boolean isModified = false;
            int index = startPosition;
            final int fixedEndPosition = endPosition;
            for (ListIterator<E> iterator = this.listIterator(); iterator.hasNext() && index < fixedEndPosition; ) {
                E currElem = iterator.next();
                if (isRemoved ^ !c.contains(currElem)) {
                    iterator.previous();
                    iterator.remove();
                    isModified = true;
                    endPosition--;
                }
                index++;
            }
            return isModified;
        }
    }
}

