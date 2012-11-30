package com.tistory.devyongsik.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tistory.devyongsik.config.CrescentCollectionHandler;
import com.tistory.devyongsik.domain.CrescentCollection;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.JsonDataHandler;
import com.tistory.devyongsik.index.CrescentIndexerExecutor;

/**
 * author : need4spd, need4spd@naver.com, 2012. 8. 15.
 */

@Controller
public class UpdateController {
	
	private Logger logger = LoggerFactory.getLogger(UpdateController.class);
	
	@RequestMapping("/update")
	public void updateDocument(HttpServletRequest request, HttpServletResponse response) {
		
		String contentsType = request.getHeader("Content-type");
		
		//TODO contentsType별로 핸들러 분리
		//TODO 일단 json만..
		Handler handler = null;
		if("application/json".equals(contentsType)) {
			handler = new JsonDataHandler();
		}
		
		String collectionName = request.getParameter("collection_name");
		String isIncrementalIndexing = StringUtils.defaultString(request.getParameter("isIncIndex"), "N");
		
		logger.info("collection name : {}", collectionName);
		logger.info("is incremental indexing : {}", isIncrementalIndexing);
		
		StringBuilder text = new StringBuilder();
		
		try {
			
			BufferedReader reader = request.getReader();
			String tmp = "";
			while((tmp = reader.readLine()) != null) {
				text.append(tmp);
				//logger.info(tmp);
			}
			
			reader.close();
			
			CrescentCollection collection = CrescentCollectionHandler.getInstance().getCrescentCollections().getCrescentCollection(collectionName);
			CrescentIndexerExecutor excutor = new CrescentIndexerExecutor(collection, handler);
			
			String message = "";
			
			if("Y".equals(isIncrementalIndexing)) {
				message = excutor.incrementalIndexing(text.toString());
			} else {
				message = excutor.bulkIndexing(text.toString());
			}
			
			Writer writer = null;
			
			response.setContentType("text/html;  charset=UTF-8");
			writer = response.getWriter();
			writer.write(message);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			logger.error("error : ", e);
		}
	}
}
