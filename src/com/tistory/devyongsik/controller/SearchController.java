package com.tistory.devyongsik.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tistory.devyongsik.config.CollectionConfig;
import com.tistory.devyongsik.handler.Handler;
import com.tistory.devyongsik.handler.JsonDataHandler;
import com.tistory.devyongsik.index.FullmoonIndexExecutor;

@Controller
public class SearchController {
	Logger logger = LoggerFactory.getLogger(SearchController.class);

	@RequestMapping("/search")
	public void updateDocument(HttpServletRequest request, HttpServletResponse response) {

		Map<String, String> searchRequest = new HashMap<String, String>();
		Enumeration enumeration = request.getParameterNames();
		
		for(String parameterName :enumeration.) {
			
		}
		
		String contentsType = request.getHeader("Content-type");

		//TODO contentsType별로 핸들러 분리
		//TODO 일단 json만..
		Handler handler = null;
		if("application/json".equals(contentsType)) {
			handler = new JsonDataHandler();
		}

		StringBuilder text = new StringBuilder();
		OutputStream outToClient = null;

		try {

			BufferedReader reader = request.getReader();
			String tmp = "";
			while((tmp = reader.readLine()) != null) {
				text.append(tmp);
				logger.info(tmp);
			}

			reader.close();

			FullmoonIndexExecutor excutor = new FullmoonIndexExecutor(CollectionConfig.getInstance().getCollection("sample"), handler);
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
