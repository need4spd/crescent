<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="java.util.*"%>
<%
	List<String> dictionary = (List<String>)request.getAttribute("dictionary");
	String dicType = (String)request.getAttribute("dicType");
	String startOffset = (String)request.getAttribute("startOffset");
	Integer dictionarySize = (Integer)request.getAttribute("dictionarySize");
	
%>
<head>
<link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">

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
	<div class="container">
		<script type="text/javascript" src="js/jquery-1.8.1.js"></script>
		<script src="js/bootstrap.min.js"></script>
	
		<form class="form-horizontal" id="dictionaryForm" method="post" action="/dictionaryManage.devys">
			<input type="hidden" id="dicType" name="dicType" value="<%=dicType %>" />
			<input type="hidden" id="startOffset" name="startOffset" value="<%=startOffset%>" /> 
			<input type="hidden" id="pagingAction" name="pagingAction" value="" />
			<input type="hidden" id="wordsToRemove" name="wordsToRemove" value="" /> 
			<table class="table table-hover">
				<thead>
					<tr>
						<th>선택</th>
						<th>단어</th>
					</tr>
				</thead>
				<tbody>
					<% for(String word : dictionary) { %>
					<tr>
						<td><input type="checkbox" id="<%=word%>" name="wordToRemove[]" value="<%=word %>" /></td>
						<td><%=word %></td>
					</tr>
					<% } %>
				</tbody>			
			</table>
			<div class="form-inline"><p class="text-info">단어개수 <%=dictionarySize%>개</p>
				<a href="javascript:removeWord();" class="btn  btn-small btn-danger disabled">선택단어삭제</a>
				<a href="/adminMain.devys" class="btn  btn-small btn-success disabled">관리자 메인으로</a>
			</div>
			<div class="control-group">
				<label class="control-label" for="wordToAdd">단어추가</label>
				<div class="controls">
					<input type="text" id="wordToAdd" name="wordToAdd" value="" placeholder="추가할 단어를 입력해주세요." class="input-large search-query"/>
					<a href="javascript:addWord();" class="btn btn-small btn-primary disabled">단어추가</a>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="wordToFind">단어찾기</label>
				<div class="controls">
					<input type="text" id="wordToFind" name="wordToFind" value="" placeholder="찾을 단어를 입력해주세요." class="input-large search-query"/>
					<a href="javascript:findWord();" class="btn btn-small btn-primary disabled">단어찾기</a>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="wordToFind">형태소분석 테스트하기</label>
				<div class="controls">
					<input type="text" id="wordForTest" name="wordForTest" value="" placeholder="테스트할 단어를 입력해주세요." class="input-large search-query"/>
					<a href="javascript:morphTest();" class="btn btn-small btn-primary disabled">형태소분석 테스트하기</a>
				</div>
			</div>
			
			<div id="morphResult"></div>
			<% if(dictionarySize > 20) { %>
			<ul class="pager">
				<li><a href="javascript:prev();">Previous</a></li>
  				<li><a href="javascript:next();">Next</a></li>
			</ul>
			<% } %>
		</form>
	</div>
</body>