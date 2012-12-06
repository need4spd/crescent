<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="collection" value="${RESULT.collectionName }" />
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
		if(e.keyCode == 13) {
			indexFileManageDoc();
		}
		return true;
	}
	function indexFileManageDoc() {
		var err = 0;
		if ($('#collection').val() == '') {
			newAlert('colleciton 명을 입력해주세요', 'alert_collection');
			err = 1;
		}
		if ($('#docNum').val() == '') {
			newAlert('Document Number를 입력해주세요', 'alert_docNum'	);
			err = 1;
		}
		if (err == 1)
			return ;
		$('#indexFileManageDocForm').attr('action', 'indexFileManageDoc.devys').submit();
	}
	
	function upAndDown(docNum) {
		$('#docNum').val(docNum);
		indexFileManageDoc();
	}
	
	$(document).ready(function() {
		$('#collection').val('${collection}');
		$('#docNum').val('${docNum}');
	});
</script>
<body>
	<%@ include file="../common/menu.jsp"%>
	<div class="container">
		<form class="form-horizontal" id="indexFileManageDocForm" name="indexFileManageDocForm" method="post" action="indexFileManageDocForm.devys">
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
          <input type="text" class="input-large" id="collection" name="collection" onkeypress="enterKey(event)"/> 
        </div>
        <div id="alert_collection"></div>
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