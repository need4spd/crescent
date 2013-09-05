package com.tistory.devyongsik.crescent.admin.entity;

import java.util.ArrayList;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import com.tistory.devyongsik.crescent.admin.entity.RankingTerm;
import com.tistory.devyongsik.crescent.admin.entity.TopRankingTerms;

public class TopRankingTermTest {

	private static ArrayList<RankingTerm> terms = new ArrayList<RankingTerm>();
	@Before
	public void setUp() throws Exception {
		int T = 0;
		while ((T++) < 40) {
			RankingTerm rankingTerm = new RankingTerm("ka" + T, "title", T);
			terms.add(rankingTerm);
		}
	}

	@Test
	public void testOffer1() {
		TopRankingTerms topRankingTerms = new TopRankingTerms(10, new RankingTermComparator());
		
		for (RankingTerm rankingTerm : terms) {
			if (topRankingTerms.isOffer(rankingTerm.getCount()))
				topRankingTerms.push(rankingTerm);
		}
	}

	
}

class RankingTermComparator implements Comparator<RankingTerm>
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