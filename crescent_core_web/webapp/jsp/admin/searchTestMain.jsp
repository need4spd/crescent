<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>

<%@page import="com.tistory.devyongsik.domain.SearchResult"%>
<%@page import="java.util.*"%>
<%
	SearchResult searchResult = (SearchResult) request.getAttribute("searchResult");
	List<Map<String, String>> resultList = null;
	
	boolean firstCallPage = true;
	
	if(searchResult == null) {
		resultList = new ArrayList<Map<String, String>>();
	} else {
		resultList = searchResult.getResultList();
		firstCallPage = false;
	}

%>
<html lang="en">
<%@ include file="../common/header.jsp"%>
<body>
	<%@ include file="../common/menu.jsp"%>

	<script>
	function enterKey(e) {
		if (e.keyCode ==13) {
			search();
		}
		return true;
	};
		function search() {
			if ($('#keyword').val() == '') {
				newAlert('검색어를 입력해주세요.', 'alert-area');
				//$('#keyword_alert').show();
				return;
			}
			$('#searchForm').attr('action', 'searchTest.devys').submit();
		}
	</script>
	<div class="container">
		<form class="form-horizontal" id="searchForm" name="searchForm" action="searchTest.devys" method="post">
			<div class="control-group">
				<label class="control-label" for="col_name">검색대상 Collection</label>
				<div class="controls">
					<input type="text" id="col_name" onkeypress="enterKey(event);" placeholder="Default Sample">
				</div>
			</div>
			<div id="alert-area"></div>
			<div class="control-group">
				<label class="control-label" for="keyword">검색어</label>
				<div class="controls">
					<input type="text" id="keyword" name="keyword" onkeypress="enterKey(event);" placeholder="keyword">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="search_field">검색대상필드</label>
				<div class="controls">
					<input type="text" id="search_field" name="search_field" onkeypress="enterKey(event);" 
						placeholder="검색대상필드 - 없으면 Default">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="sort">정렬조건</label>
				<div class="controls">
					<input type="text" id="sort" name="sort" onkeypress="enterKey(event);" 
													placeholder="정렬조건 - 없으면 Doc Score">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="page_num">Page Number</label>
				<div class="controls">
					<input type="text" id="page_num" onkeypress="enterKey(event);" 
						name="page_num" placeholder="Page Number - Default 1">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="page_size">Page Size</label>
				<div class="controls">
					<input type="text" id="page_size" name="page_size" onkeypress="enterKey(event);" 
						placeholder="Page Size - Default 20">
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<button class="btn btn-small btn-primary" type="button"
						onclick="javascript:search();">검색</button>
				</div>
			</div>
		</form>
		<div class="container">
		<%
			if(resultList.size() > 0) {
				Set<String> fieldNameSet = resultList.get(0).keySet();
				int columnSize = fieldNameSet.size();
		%>
			<table class="table table-hover">
				<caption>검색 결과</caption>
				<thead>
					<tr>
						<%
							for(String fieldName : fieldNameSet) {
						%>
							<th width="40%"><%=fieldName %></th>
						<%
							}
						%>
					</tr>
				</thead>
				<tbody>
					<%
						for(Map<String, String> resultRow : resultList) {
					%>
					<tr>
						<%
							for(String fieldName : fieldNameSet) {
						%>
							<td><%=resultRow.get(fieldName) %></td>
						<%
							}
						%>
					</tr>
					<%
						}
					%>
				</tbody>
			</table>
		<%
			} else if (!firstCallPage){
		%>
			검색결과가 없거나, 잘못된 요청입니다. <br/>
			
			totalCount : <%=searchResult.getTotalHitsCount() %> <br/>
			ErrorCode : <%=searchResult.getErrorCode() %> <br/>
			ErrorMessage : <%=searchResult.getErrorMsg() %> <br/>
		<% 
			}
		%>
		</div>
	</div>
</body>
</html>