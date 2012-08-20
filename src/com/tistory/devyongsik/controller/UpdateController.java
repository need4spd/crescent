package com.tistory.devyongsik.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author : need4spd, need4spd@naver.com, 2012. 8. 15.
 */

@Controller
public class UpdateController {
	
	Logger logger = LoggerFactory.getLogger(UpdateController.class);
	
	@RequestMapping("/update")
	public String updateDocument(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			BufferedReader reader = request.getReader();
			String tmp = "";
			while((tmp = reader.readLine()) != null) {
				logger.info(tmp);
			}
			
		} catch (IOException e) {
			logger.error("error : ", e);
		}
		
		
		return null;
	}
}
