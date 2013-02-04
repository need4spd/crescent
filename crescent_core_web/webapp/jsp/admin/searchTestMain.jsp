<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>

<c:set var="firstCallPage" value="true" />
<c:if test="${searchResult != null}">
	<c:set var="firstCallPage" value="false" />
</c:if>
<c:set var="resultList" value="${searchResult.resultList}" />

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
			if ($('#keyword').val() == '' && $('#cq').val() == '') {
				newAlert('검색어나 커스텀쿼리 중 하나는 꼭 입력해주세요.', 'alert-area');
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
				<label class="control-label" for="keyword">커스텀쿼리</label>
				<div class="controls">
					<input type="text" id="cq" name="cq" onkeypress="enterKey(event);" placeholder="커스텀쿼리-최우선조건">
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
			<c:if test="${fn:length(resultList) gt 0}">
				<table class="table table-hover">
					<caption>검색 결과</caption>
					<thead>
						<tr>
							<c:set var="firstRowMap" value="${resultList[0]}" />
							<c:forEach var="firstRow" items="${firstRowMap}" varStatus="status">
								<th width="40%">${firstRow.key}</th>
							</c:forEach>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach var="resultMap" items="${resultList}" varStatus="status">
							<c:out value="${resultMap.key}" />
							<tr>
								<c:forEach var="result" items="${resultMap}">
									<td>${result.value}</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			<c:if test="${fn:length(resultList) == 0 and firstCallPage == false}">
				검색결과가 없거나, 잘못된 요청입니다. <br/>
				
				totalCount : ${searchResult.totalHitsCount} <br/>
				ErrorCode : ${searchResult.errorCode} <br/>
				ErrorMessage : ${searchResult.errorMsg} <br/>
			</c:if>
		</div>
	</div>
</body>
</html>