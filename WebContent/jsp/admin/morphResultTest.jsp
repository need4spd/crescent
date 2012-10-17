<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="java.util.*" %>

<%
	StringBuilder resultTokenListIndexingMode = (StringBuilder)request.getAttribute("resultTokenListIndexingMode");
	StringBuilder resultTokenListQueryMode = (StringBuilder)request.getAttribute("resultTokenListQueryMode");	
%>

<%="색인모드"%>
<%=resultTokenListIndexingMode.toString()%>



<%="쿼리모드"%>
<%=resultTokenListQueryMode.toString()%>