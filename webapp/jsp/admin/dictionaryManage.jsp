<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>

<%@ page import="java.util.*"%>
<%
        List<String> dictionary = (List<String>)request.getAttribute("dictionary");
        String dicType = (String)request.getAttribute("dicType");
        String startOffset = (String)request.getAttribute("startOffset");
        Integer dictionarySize = (Integer)request.getAttribute("dictionarySize");

%>
<html lang="en">
<%@ include file="../common/header.jsp"%>
<script>
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
                        var checked = [];
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

                /* function morphTest() {
                        var param = {keyword:$('#wordForTest').val()};
                        $.post("/doMorphTestAjax.devys", param, function(data){
                                $('#morphResult').text("");
                                $('#morphResult').text(data);
                        });
                } */

                function morphTest() {
                        var param = {keyword:$('#wordForTest').val()};

                        $.getJSON('doMorphTestAjax.devys', param, function(data) {

                                  var indexingResult = '';
                                  for(var i = 0; i < data.indexResult.length; i++) {
                                          if(indexingResult.length > 0) {
                                                  indexingResult = indexingResult + ', ';
                                          }
                                          indexingResult = indexingResult + ' [' + data.indexResult[i].word + '(' + data.indexResult[i].type;
                                          indexingResult = indexingResult + ', ' + data.indexResult[i].startOffset + ',' + data.indexResult[i].endOffset + ')]';
                                  }

                                  var queryResult = '';
                                  for(var i = 0; i < data.queryResult.length; i++) {
                                          if(queryResult.length > 0) {
                                                  queryResult = queryResult + ', ';
                                          }
                                          queryResult = queryResult + ' [' + data.queryResult[i].word + '(' + data.queryResult[i].type;
                                          queryResult = queryResult + ', ' + data.queryResult[i].startOffset + ',' + data.queryResult[i].endOffset + ')]';
                                  }

                                  var result = '<ul>';
                                  result += '<li>'+'인덱싱 : ' + indexingResult+'</li>';
                                  result += '<li>'+'쿼리 : ' + queryResult+'</li>';
                                  result += '</ul>';

                                  $('#morphResult').html(result);
                        });
                }
        </script>
<body>
	<%@ include file="../common/menu.jsp"%>
	<div class="container">
		<form class="form-horizontal" id="dictionaryForm" method="post"
			action="/dictionaryManage.devys">
			<input type="hidden" id="dicType" name="dicType"
				value="<%=dicType %>" /> <input type="hidden" id="startOffset"
				name="startOffset" value="<%=startOffset%>" /> <input type="hidden"
				id="pagingAction" name="pagingAction" value="" /> <input
				type="hidden" id="wordsToRemove" name="wordsToRemove" value="" />
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
						<td><input type="checkbox" id="<%=word%>"
							name="wordToRemove[]" value="<%=word %>" /></td>
						<td><%=word %></td>
					</tr>
					<% } %>
				</tbody>
			</table>
			<div class="form-inline">
				<p class="text-info">
					단어개수
					<%=dictionarySize%>개
				</p>
				<a href="javascript:removeWord();" class="btn  btn-small btn-danger">선택단어삭제</a>
				<a href="/adminMain.devys" class="btn  btn-small btn-success">관리자
					메인으로</a>
			</div>
			<div class="control-group">
				<label class="control-label" for="wordToAdd">단어추가</label>
				<div class="controls">
					<input type="text" id="wordToAdd" name="wordToAdd" value=""
						placeholder="추가할 단어를 입력해주세요." class="input-large search-query" />
					<button class="btn btn-small btn-primary" type="button"
						onclick="javascript:addWord();">단어추가</button>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="wordToFind">단어찾기</label>
				<div class="controls">
					<input type="text" id="wordToFind" name="wordToFind" value=""
						placeholder="찾을 단어를 입력해주세요." class="input-large search-query" />
					<button class="btn btn-small btn-primary" type="button"
						onclick="javascript:findWord();">단어찾기</button>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="wordToFind">형태소분석 테스트하기</label>
				<div class="controls">

					<input type="text" id="wordForTest" name="wordForTest" value=""
						placeholder="테스트할 단어를 입력해주세요." class="input-large search-query" />
					<button class="btn btn-small btn-primary" type="button"
						onclick="javascript:morphTest();">형태소분석 테스트하기</button>

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
</html>
