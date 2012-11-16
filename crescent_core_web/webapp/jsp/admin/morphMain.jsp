<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>

<%@ page import="java.util.*"%>
<%@ page import="org.apache.lucene.analysis.Token"%>

<%
        List<Token> resultTokenListIndexingMode = (List<Token>)request.getAttribute("resultTokenListIndexingMode");
        List<Token> resultTokenListQueryMode = (List<Token>)request.getAttribute("resultTokenListQueryMode");

%>
<html lang="en">
<%@include file="../common/header.jsp"%>
<script>
	function enterKey(e) {
		if (e.keyCode ==13) {
			doMorphTest();
		}
		return true;
	};
        function doMorphTest() {
                $('#morphForm').submit();
        }
</script>
<body>
	<%@include file="../common/menu.jsp"%>
	<form id="morphForm" method="post" action="doMorphTest.devys">
		<input type="text" id="keyword" name="keyword" onkeypress="enterKey(event);" value="" size="50" />
		&nbsp;&nbsp; <a href="javascript:doMorphTest();" >형태소분석 결과보기</a>
	</form>
	<div>
		색인모드<br />
		<%
                        if(resultTokenListIndexingMode != null) {
                                for(Token t : resultTokenListIndexingMode) {
                %>
		<li><%=t.toString() %> : (<%=t.startOffset() %>, <%=t.endOffset() %>)
			[<%=t.type() %>]</li>
		<%
                                }
                        }
                %>
		<br /> 쿼리모드<br />
		<%
                        if(resultTokenListQueryMode != null) {
                                for(Token t : resultTokenListQueryMode) {
                %>
		<li><%=t.toString() %> : (<%=t.startOffset() %>, <%=t.endOffset() %>)
			[<%=t.type() %>]</li>
		<%
                                }
                        }
                %>

	</div>
</body>
</html>
