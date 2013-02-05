<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
		$("#collectionName > option[value = '${selectedCollectionName}']").attr("selected", "ture");
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
            <c:forEach var="collection" items="${crescentCollectionList}">
            	<option value="${collection.name}">${collection.name}</option>
            </c:forEach>
            </select>
          </div>
        </div>
      <div class="control-group">
        <label class="control-label">Analyzer</label>
        <div class="controls">
          <input type="text" id="analyzer" name="analyzer" class="input-xxlarge" value="${selectedCollection.analyzer}"> 
        </div>
      </div>
      <div id="alert-area-analyzer"></div>
      <div class="control-group">
        <label class="control-label">Indexing Directory</label>
        <div class="controls">
          <input type="text" id="indexingDirectory" name="indexingDirectory" class="input-xxlarge" value="${selectedCollection.indexingDirectory}"> 
        </div>
      </div>
      <div id="alert-area-indexingDirectory"></div>
      <div class="control-group">
        <label class="control-label">Searcher Reload Schedule</label>
        <div class="controls">
          <input type="text" id="searcherReloadScheduleMin" name="searcherReloadScheduleMin" class="input-mideum" value="${selectedCollection.searcherReloadScheduleMin}"> 
        </div>
      </div>
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
          	<c:forEach var="field" items="${selectedCollection.fields}">
	          	<tr>
	              <td><input type="text" class="input-midium" id="fieldName" name="fieldName" value="${field.name}" disabled></td>
	              <td style="text-align: center;"><input type="checkbox" id="analyze" name="analyze" <c:if test="${field.analyze}">checked</c:if>></td>
	              <td style="text-align: center;"><input type="checkbox" id="store" name="store" <c:if test="${field.store}">checked</c:if>></td>
	              <td style="text-align: center;"><input type="checkbox" id="index" name="index" <c:if test="${field.index}">checked</c:if>></td>
	              <td style="text-align: center;">
	              	<select id="type" name="type">
	              		<option value="STRING" <c:if test='${field.type eq "STRING"}'>selected</c:if>>STRING</option>
	              		<option value="LONG" <c:if test='${field.type eq "LONG"}'>selected</c:if>>LONG</option>
	              	</select>
	              </td>
	              <td style="text-align: center;"><input type="checkbox" id="must" name="must" <c:if test="${field.must}">checked</c:if>></td>
	              <td style="text-align: center;"><input type="checkbox" id="termposition" name="termposition" <c:if test="${field.termposition}">checked</c:if>></td>
	              <td style="text-align: center;"><input type="checkbox" id="termoffset" name="termoffset" <c:if test="${field.termoffset}">checked</c:if>></td>
	              <td style="text-align: center;"><input type="text" class="input-midium" id="boost" name="boost" value="${field.boost}"></td>
	              <td style="text-align: center;"><input type="checkbox" id="termvector" name="termvector" <c:if test="${field.termvector}">checked</c:if>></td>
	              
	              <c:set var="isDefaultSearchField" value="false" />
	              <c:forEach var="defaultSearchField" items="${selectedCollection.defaultSearchFields}">
	              	<c:if test="${not isDefaultSearchField}">
	              		<c:if test="${defaultSearchField.name eq field.name}">
	              			<c:set var="isDefaultSearchField" value="true" />
	              		</c:if>
	              	</c:if>
	              </c:forEach>
	              <td style="text-align: center;"><input type="checkbox" id="defaultSearchField" name="defaultSearchField" <c:if test="${isDefaultSearchField}">checked</c:if>></td>
	              <c:set var="isSortField" value="false" />
	              <c:forEach var="sortField" items="${selectedCollection.sortFields}">
	              	<c:if test="${not isSortField}">
	              		<c:if test="${sortField.source eq field.name}">
	              			<c:set var="isSortField" value="true" />
	              		</c:if>
	              	</c:if>
	              </c:forEach>
	              <td style="text-align: center;"><input type="checkbox" id="sortField" name="sortField" <c:if test="${isSortField}">checked</c:if>></td>
	            </tr>
          	</c:forEach>
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