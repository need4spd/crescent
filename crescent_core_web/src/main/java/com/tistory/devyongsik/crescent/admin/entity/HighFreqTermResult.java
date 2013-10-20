package com.tistory.devyongsik.crescent.admin.entity;

import org.apache.lucene.util.PriorityQueue;

public class HighFreqTermResult {

    public static int numTerms = 5;

    private TermStatsQueue tiq;

    public HighFreqTermResult() {
        this.tiq = new TermStatsQueue(numTerms);
    }

    public TermStatsQueue getTermStatsQueue() {
        return this.tiq;
    }

    public final class TermStatsQueue extends PriorityQueue<CrescentTermStats> {
        TermStatsQueue(int size) {
            super(size);
        }

        @Override
        protected boolean lessThan(CrescentTermStats termInfoA, CrescentTermStats termInfoB) {
            return termInfoA.getTotalTermFreq() < termInfoB.getTotalTermFreq();
        }
    }
}
