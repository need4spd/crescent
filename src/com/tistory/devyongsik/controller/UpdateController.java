package com.tistory.devyongsik.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.JsonDataHandler;
import com.tistory.devyongsik.index.CrescentIndexerExecutor;

/**
 * author : need4spd, need4spd@naver.com, 2012. 8. 15.
 */

@Controller
public class UpdateController {
	
	Logger logger = LoggerFactory.getLogger(UpdateController.class);
	
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
		
		logger.info("collection name : {}", collectionName);
		
		StringBuilder text = new StringBuilder();
		OutputStream outToClient = null;
		
		try {
			
			BufferedReader reader = request.getReader();
			String tmp = "";
			while((tmp = reader.readLine()) != null) {
				text.append(tmp);
				//logger.info(tmp);
			}
			
			reader.close();
			
			CrescentIndexerExecutor excutor = new CrescentIndexerExecutor(CollectionConfig.getInstance().getCollection(collectionName), handler);
			String message = excutor.execute(text.toString());
			
			outToClient = response.getOutputStream();			
			outToClient.write(message.getBytes());
			outToClient.flush();
			
			outToClient.close();
			
		} catch (IOException e) {
			logger.error("error : ", e);
		}
	}
}
