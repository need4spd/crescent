package com.tistory.devyongsik.util.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostUtil {

	private URL serverURL;
	private String DEFAULT_SERVER_URL = "http://127.0.0.1:8080/update.devys";
	private Map<String, String> mimeMap;
	private String sourceFileName = "";
	private String fileType = "json";

	public static void main(String[] args) {
		PostUtil p = new PostUtil();
		p.exec();
	}

	public PostUtil() {
		initMimeMap();
		
	}
	
	private void exec() {
		parseArgsAndInit();
		postFiles(sourceFileName);
	}

	private void parseArgsAndInit() {
		String urlStr = System.getProperty("url", DEFAULT_SERVER_URL);
		try {
			serverURL = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		sourceFileName = System.getProperty("file", "");
		fileType = System.getProperty("fileType", "json");

	}

	private void initMimeMap() {
		mimeMap = new HashMap<String,String>();
		mimeMap.put("xml", "text/xml");
		mimeMap.put("json", "application/json");
		mimeMap.put("txt", "text/plain");
		mimeMap.put("log", "text/plain");
	}

	private int postFiles(String srcFileName) {
		
		System.out.println("sourceFileName : " + sourceFileName);
		
		int filesPosted = 0;

		File srcFile = new File(srcFileName);
		if(srcFile.isDirectory() && srcFile.canRead()) {
			filesPosted += postDirectory(srcFile);
		} else {
			postFile(srcFile);
			filesPosted ++;
		}

		return filesPosted;
	}

	private int postDirectory(File dir) {
		if(dir.isHidden() && !dir.getName().equals("."))
			return(0);

		System.out.println("Indexing directory " + dir.getPath());

		int posted = 0;
		for(File file : dir.listFiles()) {
			postFile(file);
			posted++;
		}

		return posted;
	}

	private void postFile(File file) {
		
		System.out.println("file : " + file);
		
		String mimeType = mimeMap.get(fileType);

		if(mimeType == null) {
			throw new IllegalStateException("Not Supported mime Type : " + fileType);
		}

		InputStream is = null;
		try {
			is = new FileInputStream(file);

			System.out.println("POSTING..." + file.getName() + "["+mimeType+"]");

			postData(is, (int)file.length(), mimeType);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Can't open/read file: " + file);
		} finally {
			try {
				if(is!=null) is.close();
			} catch (IOException e) {
				System.out.println("IOException while closing file:" + e);
			}
		}
	}

	public boolean postData(InputStream data, Integer length, String type) {
		boolean success = true;

		HttpURLConnection urlc = null;
		try {
			try {
				urlc = (HttpURLConnection) serverURL.openConnection();
				try {
					urlc.setRequestMethod("POST");
				} catch (ProtocolException e) {
					System.out.println("Shouldn't happen: HttpURLConnection doesn't support POST??" + e);
				}
				urlc.setDoOutput(true);
				urlc.setDoInput(true);
				urlc.setUseCaches(false);
				urlc.setAllowUserInteraction(false);
				urlc.setRequestProperty("Content-type", type);

				if (null != length) urlc.setFixedLengthStreamingMode(length);

			} catch (IOException e) {
				System.out.println("Connection error (is crescent running at "+ serverURL + e);
				success = false;
			}

			OutputStream out = null;
			try {
				out = urlc.getOutputStream();
				pipe(data, out);
			} catch (IOException e) {
				System.out.println("IOException while posting data: " + e);
				success = false;
			} finally {
				try { if(out!=null) out.close(); } catch (IOException x) { /*NOOP*/ }
			}

			InputStream in = null;
			BufferedReader br = null;
			try {
				if (HttpURLConnection.HTTP_OK != urlc.getResponseCode()) {
					System.out.println("server returned an error #" + urlc.getResponseCode() + 
							" " + urlc.getResponseMessage());
					success = false;
				}

				in = urlc.getInputStream();
				br = new BufferedReader(new InputStreamReader(in));
				
				String log="";
				while((log=br.readLine()) != null) {
					System.out.println("log from server... " + log);
				}
				
			} catch (IOException e) {
				System.out.println("IOException while reading response: " + e);
				success = false;
			} finally {
				try { if(br != null) br.close(); if(in!=null) in.close(); } catch (IOException x) { /*NOOP*/ }
			}

		} finally {
			if(urlc!=null) urlc.disconnect();
		}
		return success;
	}

	private void pipe(InputStream source, OutputStream dest) throws IOException {
		byte[] buf = new byte[1024];
		int read = 0;
		while ( (read = source.read(buf) ) >= 0) {
			if (null != dest) dest.write(buf, 0, read);
		}
		if (null != dest) dest.flush();
	}
}
