package com.tistory.devyongsik.servlets;

import javax.servlet.http.HttpServlet;

import com.tistory.devyongsik.index.SearcherManagerReloader;

public class InitializeServlet extends HttpServlet {

	private static final long serialVersionUID = -5410940780879832526L;
	private SearcherManagerReloader searcherManagerReloader = new SearcherManagerReloader();
	
	@Override
	public void init() {
		searcherManagerReloader.reloadStart();
	}
	
	@Override
	public void destroy() {
		searcherManagerReloader.shutdown();
	}
}

