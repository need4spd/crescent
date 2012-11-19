<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>

<!DOCTYPE html>

<html lang="en">
<%@ include file="../common/header.jsp"%>
<script>
	
</script>
<body>
	<%@ include file="../common/menu.jsp"%>
	<div class="container">
		<form class="form-horizontal" id="collectionManageForm" name="collectionManageForm" method="post" action="collectionManageMain.devys">
			<div>
				<ul class="nav nav-tabs">
					<li class="active"><a href="indexFileManageMain.devys">Overview</a></li>
					<li><a href="indexFileManageDoc.devys">Document</a></li>
				</ul>
			</div>
			<div class="control-group">
				<label class="control-label">Index name</label>
				<div class="controls">
					<input type="text" class="input-large" disabled>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Number of Fields</label>
				<div class="controls">
					<input type="text" class="input-large" disabled>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Number of Documents</label>
				<div class="controls">
					<input type="text" class="input-large" disabled>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Has Deletions / Optimized</label>
				<div class="controls">
					<input type="text" class="input-large" disabled>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Last modified</label>
				<div class="controls">
					<input type="text" class="input-large" disabled>
				</div>
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
								<tr>
									<td>categoryName</td>
									<td>435,282</td>
									<td>26.12%</td>
								</tr>
								<tr>
									<td>categoryName</td>
									<td>435,282</td>
									<td>26.12%</td>
								</tr>
								<tr>
									<td>categoryName</td>
									<td>435,282</td>
									<td>26.12%</td>
								</tr>
								<tr>
									<td>categoryName</td>
									<td>435,282</td>
									<td>26.12%</td>
								</tr>
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
							<ul class="dropdown-menu">
								<!-- dropdown menu links -->
							</ul>
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
								<tr>
									<td>1</td>
									<td>42212</td>
									<td>logDate</td>
									<td>20120202</td>
								</tr>
								<tr>
									<td>1</td>
									<td>42212</td>
									<td>logDate</td>
									<td>20120202</td>
								</tr>
								<tr>
									<td>1</td>
									<td>42212</td>
									<td>logDate</td>
									<td>20120202</td>
								</tr>
								<tr>
									<td>1</td>
									<td>42212</td>
									<td>logDate</td>
									<td>20120202</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</form>
	</div>
</body>
</html>