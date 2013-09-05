package com.tistory.devyongsik.crescent.admin.entity;

import java.util.ArrayList;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import com.tistory.devyongsik.crescent.admin.entity.RankingTerm;
import com.tistory.devyongsik.crescent.admin.entity.TopRankingQueue;

public class TopRankingQueueTest {

	private static ArrayList<RankingTerm> terms = new ArrayList<RankingTerm>();
	@Before
	public void setUp() throws Exception {
		int T = 0;
		while ((T++) < 40) {
			RankingTerm rankingTerm = new RankingTerm("ka" + T, "title", T);
			terms.add(rankingTerm);
		}
	}

	public void testOffer1() {
		TopRankingQueue topRankingQueue = 
				new TopRankingQueue(10, new RankingTermComparator2());
		
		for (RankingTerm rankingTerm : terms) {
			topRankingQueue.add(rankingTerm);
		}
		topRankingQueue.printTree();
	}
	
	@Test
	public void testPoll() {
		TopRankingQueue topRankingQueue = 
				new TopRankingQueue(10, new RankingTermComparator2());
		
		for (RankingTerm rankingTerm : terms) {
			topRankingQueue.add(rankingTerm);
		}
		int  size = topRankingQueue.size();
		for (int i = 0; i < size; i++)
			System.out.println(i + " :" + topRankingQueue.poll().toString());
	}
}

class RankingTermComparator2 implements Comparator<RankingTerm>
{
    @Override
    public int compare(RankingTerm x, RankingTerm y)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        if (x.getCount() < y.getCount())
        {
            return -1;
        }
        if (x.getCount() > y.getCount())
        {
            return 1;
        }
        return 0;
    }
}