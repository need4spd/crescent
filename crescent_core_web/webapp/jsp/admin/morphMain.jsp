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
	<div class="container">
		<form id="morphForm" method="post" action="doMorphTest.devys">
			<input type="text" id="keyword" name="keyword" onkeypress="enterKey(event);" value="" size="50" />
			&nbsp;&nbsp; <a href="javascript:doMorphTest();" >형태소분석 결과보기</a>
		</form>
		<div class="row">
				<div class="span6">
					<table class="table table-striped">
							<caption>색인용 형태소분석</caption>
							<thead>
								<tr>
									<th>분석단어</th>
									<th>타입</th>
									<th>위치정보</th>
								</tr>
							</thead>
							<tbody>
								<%
									if(resultTokenListIndexingMode != null) {
										for(Token t :resultTokenListIndexingMode) {	
								%>
								<tr>
									<td><%=t.toString() %></td>
									<td><%=t.type() %></td>
									<td><%=t.startOffset() %>, <%=t.endOffset() %></td>
								</tr>
								<%
										}
									}
								%>
							</tbody>
						</table>
				</div>
				<div class="span6">
					<table class="table table-striped">
							<caption>쿼리 분석용 형태소분석</caption>
							<thead>
								<tr>
									<th>분석단어</th>
									<th>타입</th>
									<th>위치정보</th>
								</tr>
							</thead>
							<tbody>
								<%
									if(resultTokenListQueryMode != null) {
										for(Token t :resultTokenListQueryMode) {	
								%>
								<tr>
									<td><%=t.toString() %></td>
									<td><%=t.type() %></td>
									<td><%=t.startOffset() %>, <%=t.endOffset() %></td>
								</tr>
								<%
										}
									}
								%>
							</tbody>
						</table>
				</div>
		</div>
	</div>
</body>
</html>
