<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.tistory.devyongsik.domain.*" %>
<%@ page import="com.tistory.devyongsik.config.*" %>

<%
	CrescentCollectionHandler collectionHandler = CrescentCollectionHandler.getInstance();
	CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();

	List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
	String selectedCollectionName = (String)request.getAttribute("selectedCollectionName");
	CrescentCollection selectedCollection = crescentCollections.getCrescentCollection(selectedCollectionName);
%>
<!DOCTYPE html>

<html lang="en">
<%@ include file="../common/header.jsp"%>
<script>
	function changeCollection() {
		$('#collectionManageForm').submit();
	}
	
	function addTableRow() {
		//$(table + ' tr:last').clone()
		var clonedRow = $('#fieldTable > tbody').find('tr:last').clone();
		
		//init
		clonedRow.find('#fieldName').val('');
		clonedRow.find('#fieldName').attr('disabled', false);
		clonedRow.find('#defaultSearchField').attr('checked', false);
		clonedRow.find('#sortField').attr('checked', false);
		clonedRow.find('#store').attr('checked', false);
		clonedRow.find('#index').attr('checked', false);
		clonedRow.find('#analyze').attr('checked', false);
		
		$('#fieldTable > tbody').append(clonedRow);
	}
	
	function save() {
		//validation
		if($('#analyzer').val() == '') {
			newAlert('Analyzer는 반드시 입력되어야합니다.', 'alert-area-analyzer');
			$('#analyzer').focus();
			return;
		}
		
		if($('#indexingDirectory').val() == '') {
			newAlert('indexingDirectory는 반드시 입력되어야합니다.', 'alert-area-indexingDirectory');
			$('#indexingDirectory').focus();
			return;
		}
		
		
		var isValid = true;
		
		$('#fieldTable > tbody').find('tr').each(function() {
			var fieldNameObj = $(this).find('#fieldName');
			var fieldName = fieldNameObj.val();
			
			if(fieldName == "") {
				newAlert('fieldName은 반드시 입력되어야합니다.', 'alert-area-fieldName');
				fieldNameObj.focus();
				isValid = false;
				return;
			}
			//console.log("fieldName : " + fieldName);
			
			$(this).find('input,select').each(function() {
				var id = $(this).attr('id');
				var name = $(this).attr('name');
				
				$(this).attr('id', fieldName+'-'+id);
				$(this).attr('name', fieldName+'-'+name);
				
				//console.log('new id : ' + $(this).attr('id'));
				//console.log('new name : ' + $(this).attr('name'));
			});
		});
		
		if(isValid) {
			$('#collectionManageForm').attr('action', 'collectionUpdate.devys').submit();
		}
	}
	
	function addCollection() {
		$('#collectionManageForm').attr('action', 'addNewCollection.devys').submit();
	}
	
	function deleteCollection() {
		$('#collectionManageForm').attr('action', 'deleteCollection.devys').submit();
	}
	
	$(window).load(function () {
		$("#collectionName > option[value = <%=selectedCollectionName%>]").attr("selected", "ture");
	});
	
</script>
<body>
	<%@ include file="../common/menu.jsp"%>
	<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			<h3 id="myModalLabel">컬렉션 삭제</h3>
		</div>
		<div class="modal-body">
			<p>정말 삭제하시겠습니까?</p>
		</div>
		<div class="modal-footer">
			<button class="btn" data-dismiss="modal" aria-hidden="true">No</button>
			<button class="btn btn-primary" onClick="javascript:deleteCollection();">Yes</button>
		</div>
	</div>
	<div style="padding-left:10px;">
      <form class="form-horizontal" id="collectionManageForm" name="collectionManageForm" method="post" action="collectionManageMain.devys">
        <div class="control-group">
          <label class="control-label">Collection Name</label>
          <div class="controls">
            <select id="collectionName" name="collectionName" onChange="javascript:changeCollection();">
            <%
            	for(CrescentCollection collection : crescentCollectionList) {
            %>
              <option value="<%=collection.getName() %>"><%=collection.getName() %></option>
            <%
            	}
            %>
            </select>
          </div>
        </div>
      <div class="control-group">
        <label class="control-label">Analyzer</label>
        <div class="controls">
          <input type="text" id="analyzer" name="analyzer" class="input-xxlarge" value="<%=selectedCollection.getAnalyzer()%>"> 
        </div>
      </div>
      <div id="alert-area-analyzer"></div>
      <div class="control-group">
        <label class="control-label">Indexing Directory</label>
        <div class="controls">
          <input type="text" id="indexingDirectory" name="indexingDirectory" class="input-xxlarge" value="<%=selectedCollection.getIndexingDirectory()%>"> 
        </div>
      </div>
      <div id="alert-area-indexingDirectory"></div>
      <div>
        <table class="table table-striped" id="fieldTable">
          <thead>
            <tr>
              <th>name</th>
              <th>analyze</th>
              <th>store</th>
              <th>index</th>
              <th>type</th>
              <th>must</th>
              <th>termposition</th>
              <th>termoffset</th>
              <th>boost</th>
              <th>termvector</th>
              <th>DefaultSearchField</th>
              <th>SortField</th>
            </tr>
          </thead>
          <tbody>
          	<% 
          		List<CrescentCollectionField> fieldsList = selectedCollection.getFields();
          		for(CrescentCollectionField field : fieldsList) {
          	%>
            <tr>
              <td><input type="text" class="input-midium" id="fieldName" name="fieldName" value="<%=field.getName() %>" disabled></td>
              <td style="text-align: center;"><input type="checkbox" id="analyze" name="analyze" <%=field.isAnalyze() ? "checked" : "" %>></td>
              <td style="text-align: center;"><input type="checkbox" id="store" name="store" <%=field.isStore() ? "checked" : "" %>></td>
              <td style="text-align: center;"><input type="checkbox" id="index" name="index" <%=field.isIndex() ? "checked" : "" %>></td>
              <td style="text-align: center;">
              	<select id="type" name="type">
              		<option value="STRING" <%="STRING".equals(field.getType()) ? "selected" : "" %>>STRING</option>
              		<option value="LONG" <%="LONG".equals(field.getType()) ? "selected" : "" %>>LONG</option>
              	</select>
              </td>
              <td style="text-align: center;"><input type="checkbox" id="must" name="must" <%=field.isMust() ? "checked" : "" %>></td>
              <td style="text-align: center;"><input type="checkbox" id="termposition" name="termposition" <%=field.isTermposition() ? "checked" : "" %>></td>
              <td style="text-align: center;"><input type="checkbox" id="termoffset" name="termoffset" <%=field.isTermoffset() ? "checked" : "" %>></td>
              <td style="text-align: center;"><input type="text" class="input-midium" id="boost" name="boost" value="<%=field.getBoost() %>"></td>
              <td style="text-align: center;"><input type="checkbox" id="termvector" name="termvector" <%=field.isTermvector() ? "checked" : "" %>></td>
              <%
              	List<CrescentDefaultSearchField> defaultSearchFieldList = selectedCollection.getDefaultSearchFields();
              	boolean isDefaultSearchField = false;
              	for(CrescentDefaultSearchField defaultSearchField : defaultSearchFieldList) {
              		if(field.getName().equals(defaultSearchField.getName())) {
              			isDefaultSearchField = true;
              			break;
              		}
              	}
              %>
              <td style="text-align: center;"><input type="checkbox" id="defaultSearchField" name="defaultSearchField" <%=isDefaultSearchField ? "checked" : "" %>></td>
              <%
              	List<CrescentSortField> sortFieldList = selectedCollection.getSortFields();
              	boolean isSortField = false;
              	for(CrescentSortField sortField : sortFieldList) {
              		if(field.getName().equals(sortField.getSource())) {
              			isSortField = true;
              			break;
              		}
              	}
              %>
              <td style="text-align: center;"><input type="checkbox" id="sortField" name="sortField" <%=isSortField ? "checked" : "" %>></td>
            </tr>
            <%
          		}
            %>
          </tbody>
        </table>
      </div>
      </form>
      <div id="alert-area-fieldName"></div>
      <div>
      	<button class="btn btn-small btn-primary" type="button" onclick="javascript:save();">저장</button>
		<button class="btn btn-small btn-primary" type="button" onClick="javascript:addTableRow();">필드추가</button>
		<button class="btn btn-small btn-warning" type="button" onClick="javascript:addCollection();">컬렉션추가</button>
		<a href="#myModal" role="button" class="btn btn-small btn-danger" data-toggle="modal">컬렉션삭제</a>
	  </div>
    </div>
</body>
</html>