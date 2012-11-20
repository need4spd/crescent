<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.tistory.devyongsik.domain.*" %>

<!DOCTYPE html>

<html lang="en">
<%@ include file="../common/header.jsp"%>
<script>
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
			$('#collectionManageForm').attr('action', 'collectionAdd.devys').submit();
		}
	}	
</script>
<body>
	<%@ include file="../common/menu.jsp"%>
	<div style="padding-left:10px;">
      <form class="form-horizontal" id="collectionManageForm" name="collectionManageForm" method="post" action="collectionManageMain.devys">
        <div class="control-group">
          <label class="control-label">Collection Name</label>
          <div class="controls">
          	<input type="text" id="collectionName" name="collectionName" class="input-large" value="">
          </div>
        </div>
      <div class="control-group">
        <label class="control-label">Analyzer</label>
        <div class="controls">
          <input type="text" id="analyzer" name="analyzer" class="input-xxlarge" value=""> 
        </div>
      </div>
      <div id="alert-area-analyzer"></div>
      <div class="control-group">
        <label class="control-label">Indexing Directory</label>
        <div class="controls">
          <input type="text" id="indexingDirectory" name="indexingDirectory" class="input-xxlarge" value=""> 
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
              <th>termposition</th>
              <th>termoffset</th>
              <th>boost</th>
              <th>termvector</th>
              <th>DefaultSearchField</th>
              <th>SortField</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><input type="text" class="input-midium" id="fieldName" name="fieldName" value=""></td>
              <td style="text-align: center;"><input type="checkbox" id="analyze" name="analyze"></td>
              <td style="text-align: center;"><input type="checkbox" id="store" name="store"></td>
              <td style="text-align: center;"><input type="checkbox" id="index" name="index"></td>
              <td style="text-align: center;">
              	<select id="type" name="type">
              		<option value="STRING">STRING</option>
              		<option value="LONG">LONG</option>
              	</select>
              </td>
              <td style="text-align: center;"><input type="checkbox" id="termposition" name="termposition"></td>
              <td style="text-align: center;"><input type="checkbox" id="termoffset" name="termoffset"></td>
              <td style="text-align: center;"><input type="text" class="input-midium" id="boost" name="boost"></td>
              <td style="text-align: center;"><input type="checkbox" id="termvector" name="termvector"></td>
              <td style="text-align: center;"><input type="checkbox" id="defaultSearchField" name="defaultSearchField"></td>
              <td style="text-align: center;"><input type="checkbox" id="sortField" name="sortField"></td>
            </tr>
          </tbody>
        </table>
      </div>
      </form>
      <div id="alert-area-fieldName"></div>
      <div>
      	<button class="btn btn-small btn-primary" type="button" onclick="javascript:save();">저장</button>
		<button class="btn btn-small btn-primary" type="button" onClick="javascript:addTableRow();">필드추가</button>
	  </div>
    </div>
</body>
</html>