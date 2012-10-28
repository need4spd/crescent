<%@page language="java" contentType="*/json" pageEncoding="utf-8" %>
<%@ page import="java.util.*" %>

<%
	String resultTokenListIndexingMode = (String)request.getAttribute("resultTokenListIndexingMode");
	String resultTokenListQueryMode = (String)request.getAttribute("resultTokenListQueryMode");	
%>

<%="색인모드"%>
<%=resultTokenListIndexingMode%>



<%="쿼리모드"%>
<%=resultTokenListQueryMode%>