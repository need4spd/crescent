package com.tistory.devyongsik.crescent.admin.entity;

import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.util.PriorityQueue;

public class HighFreqTermResult {

    public static int numTerms = 100;

    private TermStatsQueue tiq;

    public HighFreqTermResult() {
        tiq = new TermStatsQueue(numTerms);
    }

    public TermStatsQueue getTermStatsQueue() {
        return tiq;
    }

    public final class TermStatsQueue extends PriorityQueue<TermStats> {
        TermStatsQueue(int size) {
            super(size);
        }

        @Override
        protected boolean lessThan(TermStats termInfoA, TermStats termInfoB) {
            return termInfoA.docFreq < termInfoB.docFreq;
        }
    }
}
