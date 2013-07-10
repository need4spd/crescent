<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="collectionNames" value="${RESULT.collectionNames }" />
<c:set var="selectCollection" value="${RESULT.selectCollection }" />
<c:set var="docNum" value="${RESULT.docNum }" />
<c:set var="field_name" value="${RESULT.fieldName }" />
<c:set var="flag" value="${RESULT.flag }" />
<c:set var="norm" value="${RESULT.norm }" />
<c:set var="value" value="${RESULT.value }" />
	
<!DOCTYPE html>

<html lang="en">
<%@ include file="../common/header.jsp"%>
<script>
	function enterKey(e) {
		if (e.keyCode == 13) {
			var docNum = parseInt($('#docNum').val());
			if (docNum < 0) {
				$('#docNum').val("0");
			} else
				$('#indexFileManageDocForm').attr('action', 'indexFileManageDoc.devys').submit();
		}
		return true;
	}
	function upAndDown(docNum) {
		if (parseInt(docNum) < 0) {
			newAlert('0이상의 문서번호를 입력해주세요.', "alert_docNum");
		} else {
			$('#docNum').val(docNum);
			$('#indexFileManageDocForm').attr('action', 'indexFileManageDoc.devys').submit();
		}
		return false;
	}
	
	$(document).ready(function() {
		$('#docNum').val('${docNum}');
		
		$('#collection').change(function() {
			var selectCollection = $('#collection').find("option:selected").text();
			$("input[name='selectCollection']").val(selectCollection);
			$('#docNum').val(0);
			
			$('#indexFileManageDocForm').attr('action', 'indexFileManageDoc.devys').submit();
		})
	});
</script>
<body>
	<%@ include file="../common/menu.jsp"%>
	<div class="container">
		<form class="form-horizontal" id="indexFileManageDocForm" method="post" action="indexFileManageDoc.devys">
			<div>
				<ul class="nav nav-tabs">
					<li><a href="indexFileManageMain.devys">Overview</a></li>
					<li class="active"><a href="indexFileManageDoc.devys">Document</a></li>
				</ul>
			</div>
			<span class="label">Browse by document number</span>
      <div class="control-group">
      <label class="control-label">Collection Name:</label>
        <div class="controls">
          	<select id="collection">
          		<c:forEach items="${collectionNames }" var="collectionName">
          			<option value="${collectionName }" ${collectionName == selectCollection ? 'selected' : '' }>${collectionName }</option>
          		</c:forEach>
			</select> 
			<input type="hidden" name="selectCollection" value="${selectCollection }"/>
        </div>
        <label class="control-label">Doc. #:0</label>
        <div class="controls">
          <input type="text" class="input-large" id="docNum" name="docNum" onkeypress="enterKey(event)"/> 
        </div>
        <div id="alert_docNum"></div>
      </div>
      <button type="button" class="btn" onclick="upAndDown('${docNum - 1}')">Down</button>
      <button type="button" class="btn" onclick="upAndDown('${docNum + 1}')">Up</button>
      <div>
        <table class="table table-striped">
          <caption>Document</caption>
          <thead>
            <tr>
              <th>Field</th>
              <th>IdfpTSVopNLB#</th>
              <th>Norm</th>
              <th>Value</th>
            </tr>
          </thead>
          <tbody>
          <c:forEach items="${field_name }" var="field" varStatus="status">
          	<tr>
          		<td>${field }</td>
          		<td>${flag[status.index] }</td>
          		<td>${norm[status.index] }</td>
          		<td>${value[status.index] }</td>
          	</tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
      <span class="label">I - Indexed(docs, freqs, pos) T- tokenized, S - stored, V - Term Vector
        (offsets, pos) N - with Norms L - Lazym B - Binary N - Numeric</span>
		</form>
	</div>
</body>
</html>