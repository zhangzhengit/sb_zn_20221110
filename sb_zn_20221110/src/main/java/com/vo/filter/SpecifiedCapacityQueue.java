package com.vo.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 指定容量大小的队列，add时如果满了，则删除最老的腾出位置来放最新的
 *
 * @author zhangzhen
 * @date 2022年12月12日
 *
 */
public class SpecifiedCapacityQueue<E> implements Queue<E> {

	private final int capacity;
	private final Queue<E> queue= new LinkedList<>();

	public SpecifiedCapacityQueue(final int capacity) {
		this.capacity = capacity;
	}

	@Override
	public boolean contains(final Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(final Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.queue.size();
	}

	@Override
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	@Override
	public boolean add(final E e) {

		if (this.size() >= this.capacity) {
			this.queue.poll();
		}

		this.queue.add(e);

		return true;
	}

	@Override
	public boolean offer(final E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E poll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E element() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E peek() {
		throw new UnsupportedOperationException();
	}

}
