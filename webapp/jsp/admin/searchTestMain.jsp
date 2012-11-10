<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en">
<%@ include file="../common/header.jsp"%>
<body>
	<%@ include file="../common/menu.jsp"%>

	<script>
                function search() {
                        if($('#keyword').val() == '') {
                                newAlert('검색어를 입력해주세요.');
                                //$('#keyword_alert').show();
                                return;
                        }
                        $('#searchForm').attr('action', '/searchTest.devys').submit();
                }

        </script>
	<div class="container">
		<form class="form-horizontal" id="searchForm" name="searchForm"
			action="/searchTest.devys" method="post">
			<div class="control-group">
				<label class="control-label" for="col_name">검색대상 Collection</label>
				<div class="controls">
					<input type="text" id="col_name" placeholder="Default Sample">
				</div>
			</div>
			<div id="alert-area"></div>
			<div class="control-group">
				<label class="control-label" for="keyword">검색어</label>
				<div class="controls">
					<input type="text" id="keyword" placeholder="keyword">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="search_field">검색대상필드</label>
				<div class="controls">
					<input type="text" id="search_field"
						placeholder="검색대상필드 - 없으면 Default">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="sort">정렬조건</label>
				<div class="controls">
					<input type="text" id="sort" placeholder="정렬조건 - 없으면 Doc Score">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="page_num">Page Number</label>
				<div class="controls">
					<input type="text" id="page_num"
						placeholder="Page Number - Default 1">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="page_size">Page Size</label>
				<div class="controls">
					<input type="text" id="page_size"
						placeholder="Page Size - Default 30">
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<button class="btn btn-small btn-primary" type="button"
						onclick="javascript:search();">검색</button>
				</div>
			</div>
		</form>
	</div>
</body>
</html>