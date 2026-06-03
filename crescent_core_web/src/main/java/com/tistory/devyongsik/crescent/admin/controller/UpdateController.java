package com.tistory.devyongsik.crescent.admin.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.data.handler.Handler;
import com.tistory.devyongsik.crescent.data.handler.JsonDataHandler;
import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import com.tistory.devyongsik.crescent.index.indexer.CrescentIndexerExecutor;

/**
 * author : need4spd, need4spd@naver.com, 2012. 8. 15.
 */

@Controller
public class UpdateController {

	private Logger logger = LoggerFactory.getLogger(UpdateController.class);

	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;

	@Autowired
	@Qualifier("crescentIndexerExecutor")
	private CrescentIndexerExecutor crescentIndexerExecutor;

	@RequestMapping("/update")
	public void updateDocument(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String contentType = request.getHeader("Content-Type");
		if (contentType == null || !contentType.contains("application/json")) {
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
				"지원하지 않는 Content-Type입니다. application/json을 사용하세요.");
			return;
		}

		String collectionName = request.getParameter("collection_name");
		if (collectionName == null || collectionName.trim().isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "collection_name 파라미터가 필요합니다.");
			return;
		}

		CrescentCollection collection = collectionHandler.getCrescentCollections().getCrescentCollection(collectionName);
		if (collection == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "존재하지 않는 컬렉션입니다: " + collectionName);
			return;
		}

		logger.info("collection name : {}", collectionName);

		StringBuilder text = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String tmp;
			while ((tmp = reader.readLine()) != null) {
				text.append(tmp);
			}
		}

		try {
			Handler handler = new JsonDataHandler();
			IndexingRequestForm indexingRequestForm = handler.handledData(text.toString());
			String message = crescentIndexerExecutor.indexing(collection, indexingRequestForm);

			response.setContentType("application/json; charset=UTF-8");
			try (Writer writer = response.getWriter()) {
				writer.write("{\"result\":\"SUCCESS\",\"message\":\"" + message + "\"}");
			}
		} catch (IllegalStateException e) {
			logger.error("색인 요청 처리 실패: ", e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			logger.error("색인 처리 중 오류 발생: ", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "색인 처리 중 오류가 발생하였습니다.");
		}
	}
}
