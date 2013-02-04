<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>

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
								<c:forEach var="token" items="${indexingModeList}" varStatus="status">
									<tr>
										<td>${token.term}</td>
										<td>${token.type}</td>
										<td>${token.startOffset}, ${token.endOffset}</td>
									</tr>
								</c:forEach>
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
								<c:forEach var="token" items="${queryModeList}" varStatus="status">
									<tr>
										<td>${token.term}</td>
										<td>${token.type}</td>
										<td>${token.startOffset}, ${token.endOffset}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
				</div>
		</div>
	</div>
</body>
</html>
