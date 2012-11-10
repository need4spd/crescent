<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>

<html lang="en">
<%@ include file="../common/header.jsp"%>
<body>
	<%@ include file="../common/menu.jsp"%>
	<div class="container">
		<ul>
			<li><a href="/dictionaryManage.devys?dicType=noun">명사사전</a></li>
			<li><a href="/dictionaryManage.devys?dicType=stop">불용어사전</a></li>
			<li><a href="/dictionaryManage.devys?dicType=syn">동의어사전</a></li>
			<li><a href="/dictionaryManage.devys?dicType=compound">복합명사사전</a></li>
		</ul>
	</div>
</body>
</html>