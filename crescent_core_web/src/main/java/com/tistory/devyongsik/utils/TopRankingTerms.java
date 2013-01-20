package com.tistory.devyongsik.utils;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TopRankingTerms extends PriorityQueue<RankingTerm>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int minCount;
	private int initialCapacity;
	
	public TopRankingTerms (int initialCapacity, Comparator<RankingTerm> compartor) {
		super(initialCapacity, compartor);
		this.initialCapacity = initialCapacity;
	}
	
	public boolean isOffer(int count) {
		if (super.size() < this.initialCapacity) {
			return true;
		}
		if (minCount < count) {
			return true;
		}
		
		return false;
	}
	
	public boolean push(RankingTerm e) {
		if (super.size() >= this.initialCapacity)
			super.poll();
		super.offer(e);
		minCount = super.peek().getCount();
		return true;
	}
}