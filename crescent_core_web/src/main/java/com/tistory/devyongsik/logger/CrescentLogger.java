package com.tistory.devyongsik.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrescentLogger {

	public static void logging(LogInfo logInfo) {
		Logger logger = LoggerFactory.getLogger(CrescentLogger.class);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd,kk:mm:ss,SSS");
		String dateFormat = simpleDateFormat.format(new Date());
		
		StringBuilder logText = new StringBuilder();
		
		logText.append(dateFormat).append(" @$ ")
			   .append("col_name:"+logInfo.getCollectionName()).append(" @$ ")
			   .append("elapsedTime:"+logInfo.getElaspedTimeMil()).append(" @$ ")
			   .append("totalCount:"+logInfo.getTotalCount()).append(" @$ ")
			   .append("keyword:"+logInfo.getKeyword()).append(" @$ ")
			   .append("query:"+logInfo.getQuery().toString()).append(" @$ ")
			   .append("sort:"+logInfo.getSort()).append(" @$ ")
			   .append("filter:"+logInfo.getFilter()).append(" @$ ")
			   .append("pageNum:"+logInfo.getPageNum()).append(" @$ ")
			   .append("pcid:"+StringUtils.defaultIfEmpty(logInfo.getPcid(), "00000000")).append(" @$ ")
			   .append("userid:"+StringUtils.defaultIfEmpty(logInfo.getUserId(), "guest")).append(" @$ ")
			   .append("userip:"+StringUtils.defaultIfEmpty(logInfo.getUserIp(), "127.0.0.1")).append(" @$ ");
			   
		logger.info(logText.toString());
	}
}
