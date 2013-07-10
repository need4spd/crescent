<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="selectCollection" value="${RESULT.selectCollection }" />
<c:set var="collectionNames" value="${RESULT.collectionNames }" />
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
<c:set var="select_top_ranking_field" value="${RESULT.topRankingField }" />
<c:set var="top_ranking_fields" value="${RESULT.topRankingFields }" />


<!DOCTYPE html>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>

<html lang="en">
<%@ include file="../common/header.jsp"%>
<script>
	$(document).ready(function() {
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
		
		
		$("#collection").change(function(event) {
			var selectCollection = $("#collection").find("option:selected").text();
			$("input[name='selectCollection']").val(selectCollection);
			$("input[name='topRankingField']").val('');
			$('#indexFileManageForm').attr("action", "indexFileManageMain.devys").submit();
		});
		
		
		$("#fieldList").change(function(event){
			var topRankingField = $("#fieldList").find("option:selected").text();
			$("input[name=topRankingField]").val(topRankingField);
			
			$('#indexFileManageForm').attr("action", "indexFileManageMain.devys").submit();
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
					<select id="collection">
						<c:forEach items="${collectionNames }" var="collectionName">
							<option value="${collectionName }" ${collectionName == selectCollection ? 'selected' : '' }>${collectionName }</option>
						</c:forEach>
					</select>
				</div>
				<input type="hidden" name="selectCollection" value="${selectCollection }"/>
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
							<select id="fieldList" >
								<c:forEach items="${top_ranking_fields }" var="top_ranking_field">
									<option value="${top_ranking_field }" ${top_ranking_field == select_top_ranking_field ? 'selected' : ''}>${top_ranking_field }</option>
								</c:forEach>	
							</select>
							<input type="hidden" name="topRankingField" value="${select_top_ranking_field }" />
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
							<c:choose>
								<c:when test="${top_ranking != null}">
								<c:forEach items="${top_ranking }" var="top_item" varStatus="index">
								<tr>
									<td>${index.count }</td>
									<td>${top_item.count }</td>
									<td>${top_item.field }</td>
									<td>${top_item.text }</td>
								</tr>
								</c:forEach>
								</c:when>
								<c:otherwise>
								</c:otherwise>
							</c:choose>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</form>
	</div>
</body>
</html>