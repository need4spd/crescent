<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="collection" value="${RESULT.collectionName }" />
<c:set var="index_name" value="${RESULT.indexName }" />
<c:set var="num_field" value="${RESULT.numOfField }" />
<c:set var="num_doc" value="${RESULT.numOfDoc }" />
<c:set var="num_term" value="${RESULT.numOfTerm }" />
<c:set var="has_del" value="${RESULT.hasDel }" />
<c:set var="is_optimize" value="${RESULT.isOptimize }" />
<c:set var="index_version" value="${RESULT.indexVersion }" />
<c:set var="last_modify" value="${RESULT.lastModify }" />
<c:set var="term_count" value="${RESULT.termCount }" />
<c:set var="top_ranking" value="${RESULT.topRanking }" />
<c:set var="top_ranking_count" value="${RESULT.topRankingCount }" />
<c:set var="top_ranking_fields" value="${RESULT.topRankingFields }" />


<!DOCTYPE html>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>

<html lang="en">
<%@ include file="../common/header.jsp"%>
<script>
	function enterKey(e) {
		if (e.keyCode == 13) {
			indexFileManage();
		}
		return true;
	}
	
	function indexFileManage() {
		if ($('#collection').val() == '') {
			newAlert('collection 명을 입력해주세요', 'alert-area');
			return;
		}
		$('#indexFileManageForm').attr('action', 'indexFileManageMain.devys').submit();
	}
	
	$(document).ready(function() {
		$('#collection').val('${collection}');
		
		$('#indexName').val('${index_name}');
		$('#numOfField').val('${num_field}');
		$('#numOfDocument').val('${num_doc}');
		$('#numOfTerm').val('${num_term}');
		$('#indexVersion').val('${index_version}');
		$('#lastModified').val('${last_modify}');
		$('#hasDelIsOpt').val('${has_del}' + " / " + '${is_optimize}');

		$('#indexName').attr('disabled','disabled');
		$('#numOfField').attr('disabled','disabled');
		$('#numOfDocument').attr('disabled','disabled');
		$('#numOfTerm').attr('disabled','disabled');
		$('#indexVersion').attr('disabled','disabled');
		$('#lastModified').attr('disabled','disabled');
		$('#hasDelIsOpt').attr('disabled','disabled');
		
		$("#fieldList").click(function(event){
			var targetText = event.target.text;
			
			$("input[name=topRankingField]").val(targetText);
			indexFileManage();
			
		});
	});
</script>
<body>
	<%@ include file="../common/menu.jsp"%>
	<div class="container">
		<form class="form-horizontal" method="post" id="indexFileManageForm" name="indexFileManageForm">
			<div>
				<ul class="nav nav-tabs">
					<li class="active"><a href="indexFileManageMain.devys">Overview</a></li>
					<li><a href="indexFileManageDoc.devys">Document</a></li>
				</ul>
			</div>
			<div class="control-group">
				<label class="control-label">Collection Name</label>
				<div class="controls">
					<input type="text" class="input-large" id="collection" name="collection" onkeypress="enterKey(event);" placeholder="input collection name..."/>
				</div>
			</div>
			<div id="alert-area"></div>
			<div class="control-group">
				<label class="control-label">Index name</label>
				<div class="controls">
					<input type="text" id="indexName" placeholder="Index Name" disables>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Number of Fields</label>
				<div class="controls">
					<input type="text" id="numOfField" placeholder="Number of Fields" disables>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Number of Terms</label>
				<div class="controls">
					<input type="text" id="numOfTerm" placeholder="Number of Terms" disables>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Number of Documents</label>
				<div class="controls">
					<input type="text" id="numOfDocument" placeholder="Number of Documents" disables>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Has Deletions / Optimized</label>
				<div class="controls">
					<input type="text" id="hasDelIsOpt" placeholder="hasDelete / isOptimized" disables>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Index Version</label>
				<div class="controls">
					<input type="text" id="indexVersion" placeholder="IndexVersion" disables>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Last modified</label>
				<div class="controls">
					<input type="text" id="lastModified" placeholder="Last modified" disables>
				</div>
				<div class="span6 offset1" id="lastModified"></div>
			</div>
			<div class="row">
				<div class="span6">
					<div>
						<table class="table table-striped">
							<caption>Fields and Term Count</caption>
							<thead>
								<tr>
									<th width="40%">Name</th>
									<th width="40%">Term Count</th>
									<th>%</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${term_count }" var="product">
								<tr>
									<td>${product.key }</td>
									<td>${product.value }</td>
									<c:choose>
										<c:when test="${num_term != 0}"> <td><fmt:formatNumber type="number" value="${(product.value / num_term)*100 }" 
											maxFractionDigits="3"/> %</td>
										</c:when>
										<c:otherwise>
											<td></td>
										</c:otherwise>
									</c:choose>
									
								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<div class="span6">
					<div>
						<div class="btn-group">
							<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
								Field 선택 <span class="caret"></span>
							</a>
							<ul class="dropdown-menu" id="fieldList" >
								<!-- dropdown menu links -->
								<c:forEach items="${top_ranking_fields }" var="top_ranking_field">
									<li><a href="#">${top_ranking_field }</a></li>
								</c:forEach>	
							</ul>
							<input type="hidden" name="topRankingField" value="" />
						</div>
						<table class="table table-striped">
							<caption>Top ranking terms</caption>
							<thead>
								<tr>
									<th>NO</th>
									<th>Count</th>
									<th>Field</th>
									<th>Text</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach begin="1" end="${top_ranking_count }" step="1" var="i">
								<tr>
									<td>${i }</td>
									<td>${top_ranking[top_ranking_count-i].count }</td>
									<td>${top_ranking[top_ranking_count-i].field }</td>
									<td>${top_ranking[top_ranking_count-i].text }</td>
									
								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</form>
	</div>
</body>
</html>