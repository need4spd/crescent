<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="java.util.*" %>
<%
	List<String> dictionary = (List<String>)request.getAttribute("dictionary");
	String dicType = (String)request.getAttribute("dicType");
	String startOffset = (String)request.getAttribute("startOffset");
	Integer dictionarySize = (Integer)request.getAttribute("dictionarySize");
	
%>
<head>
	<script type="text/javascript" src="js/jquery-1.8.1.js"></script>
	
	<script language="javascript">
		function prev() {
			$('#pagingAction').val("prev");
			$('#dictionaryForm').submit();
			
		}
		
		function next() {
			$('#pagingAction').val("next");
			$('#dictionaryForm').submit();
			
		}
		
		function addWord() {
			$('#dictionaryForm').attr('action', '/dictionaryManageAdd.devys').submit();
		}
		
		function removeWord() {
			var checked = []
			$("input[name='wordToRemove[]']:checked").each(function ()
			{
			    checked.push($(this).val());
			});
			
			$('#wordsToRemove').val(checked);
			
			$('#dictionaryForm').attr('action', '/dictionaryManageRemove.devys').submit();
		}
		
		function findWord() {
			$('#dictionaryForm').attr('action', '/dictionaryManageFind.devys').submit();
		}
		
		function morphTest() {
			var param = {keyword:$('#wordForTest').val()};
			$.post("/doMorphTestAjax.devys", param, function(data){
				$('#morphResult').text("");
				$('#morphResult').text(data);
			});
		}
	</script>
</head>
<body>
	<form id="dictionaryForm" method="post" action="/dictionaryManage.devys" >
		<input type="hidden" id="dicType" name="dicType" value="<%=dicType %>" />
		<input type="hidden" id="startOffset" name="startOffset" value="<%=startOffset%>" />
		<input type="hidden" id="pagingAction" name="pagingAction" value="" />
		<input type="hidden" id="wordsToRemove" name="wordsToRemove" value="" />
		
		<a href="/adminMain.devys">관리자 메인으로</a>
		<br/><br/>
		
		<% for(String word : dictionary) { %>
			<tr><input type="checkbox" id="<%=word%>" name="wordToRemove[]" value="<%=word %>" /><%=word %></tr><br/>
		<% } %>
		
		<br/>
		단어개수 : <%=dictionarySize%>개
		
		<br/><br/>
		
		<a href="javascript:removeWord();">선택단어삭제</a><br/>
		
		<input type="text" id="wordToAdd" name="wordToAdd" value="" /><a href="javascript:addWord();">단어추가</a><br/>
		<input type="text" id="wordToFind" name="wordToFind" value="" /><a href="javascript:findWord();">단어찾기</a><br/>
		
		<input type="text" id="wordForTest" name="wordForTest" value="" /><a href="javascript:morphTest();">형태소분석 테스트하기</a><br/>
		<div id="morphResult"></div>
		<% if(dictionarySize > 30) { %>
			<a href="javascript:prev();"> 이전 </a>&nbsp;&nbsp; | &nbsp;&nbsp;<a href="javascript:next();"> 다음 </a>
		<% } %>
	</form>
</body>