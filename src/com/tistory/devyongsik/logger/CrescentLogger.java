package com.tistory.devyongsik.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrescentLogger {

	public static void logging(Query query, int totalCount, long millisecond) {
		Logger logger = LoggerFactory.getLogger(CrescentLogger.class);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd,kk:mm:ss,SSS");
		String dateFormat = simpleDateFormat.format(new Date());
		
		StringBuilder logText = new StringBuilder();
		
		logText.append(dateFormat).append(" @$ ")
			   .append(millisecond).append(" @$ ")
			   .append(totalCount).append(" @$ ")
			   .append(query.toString());
		
		logger.info(logText.toString());
	}
}
