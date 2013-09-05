package com.tistory.devyongsik.crescent.admin.entity;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class TopRankingQueue extends AbstractQueue<RankingTerm> implements java.io.Serializable{

	private static final long serialVersionUID = 679478964616779493L;
	
	private int initialCapacity;
	private final Comparator<RankingTerm> comparator;
	
	private int minCount;
	private int size = 0;
	private transient RankingTerm[] queue;
	
	public TopRankingQueue (int initialCapacity, Comparator<RankingTerm> compartor) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException();
		
		this.queue = new RankingTerm[initialCapacity];
		this.initialCapacity = initialCapacity;
		this.comparator = compartor;
	}
	
	@Override
	public boolean offer(RankingTerm e) {
		if (e == null)
			throw new NullPointerException();
		if (size >= initialCapacity) {
			if(e.getCount() > minCount) {
				queue[0] = e;
				siftDown(0, queue[0]);
			}
		} else {
			if (size == 0) {
				queue[0] = e;
			} else {
				siftUp(size, e);
			}
			size++;
		}
		
		minCount = peek().getCount();
		return true;
	}
	
	public boolean add(RankingTerm e) {
		return offer(e);
	}

	@Override
	public RankingTerm poll() {
		if (size == 0)
			return null;
		int s = --size;
		RankingTerm result = queue[0];
		RankingTerm x = queue[s];
		queue[s] = null;
		if (s != 0)
			siftDown(0, x);
		return result;
	}

	@Override
	public RankingTerm peek() {
		 if (size == 0)
	            return null;
		 return queue[0];
	}
	
	public void clear() {
		for (int i = 0; i < this.size; i++)
			queue[i] = null;
		size = minCount = 0;
	}

	@Override
	public Iterator<RankingTerm> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return size;
	}
	
	private void siftUp(int k, RankingTerm x) {
		while (k > 0) {
            int parent = (k - 1) >>> 1;
            RankingTerm e = queue[parent];
            if (comparator.compare(x, e) >= 0)
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = x;
	}
	
	private void siftDown(int k, RankingTerm x) {
		int half = size >>> 1;
		while (k < half) {
			int child = (k << 1) + 1;
			RankingTerm c = queue[child];
			int right = child + 1;
			if (right < size && comparator.compare(c, queue[right]) > 0)
				c = queue[child = right];
			if (comparator.compare(x, c) <= 0)
				break;
			queue[k] = c;
			k = child;
		}
		queue[k] = x;
	}
	
	public RankingTerm[] toArray() {
        return Arrays.copyOf(queue, size);
    }
	
	public void printTree() {
		int half = (int)Math.log(size + 1) + 1;
		int base = 0;
		for (int i = 0; i <= half; i++) {
			for (int j = 0; j < half - i; j++)
				System.out.print("\t");
			
			int levelSize = (i != 0) ? (i<<1) : 1;
			int j;
			for (j = 0; j < levelSize; j++) {
				if (base +j >= initialCapacity) break;
				System.out.print(queue[base + j].toString() + "\t");
			}
			base += j;
			System.out.println();
		}
	}
}