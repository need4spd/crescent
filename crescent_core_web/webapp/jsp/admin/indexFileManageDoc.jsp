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
					<li><a href="indexFileManageMain.devys">Overview</a></li>
					<li class="active"><a href="indexFileManageDoc.devys">Document</a></li>
				</ul>
			</div>
			<span class="label">Browse by document number</span>
      <div class="control-group">
        <label class="control-label">Doc. #:0</label>
        <div class="controls">
          <input type="text" class="input-large"> 
        </div>
      </div>
      <a class="btn" href="#"><span class="btn-label">Down</span></a>
      <a class="btn" href="#"><span class="btn-label">Up</span></a>
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
            <tr>
              <td>logDate</td>
              <td>idfp-S------#</td>
              <td>---</td>
              <td>일별시나리오별요청건수</td>
            </tr>
            <tr>
              <td>logDate</td>
              <td>idfp-S------#</td>
              <td>---</td>
              <td>일별시나리오별요청건수</td>
            </tr>
            <tr>
              <td>logDate</td>
              <td>idfp-S------#</td>
              <td>---</td>
              <td>일별시나리오별요청건수</td>
            </tr>
            <tr>
              <td>logDate</td>
              <td>idfp-S------#</td>
              <td>---</td>
              <td>일별시나리오별요청건수</td>
            </tr>
            <tr>
              <td>logDate</td>
              <td>idfp-S------#</td>
              <td>---</td>
              <td>일별시나리오별요청건수</td>
            </tr>
          </tbody>
        </table>
      </div>
      <span class="label">I - Indexed(docs, freqs, pos) T- tokenized, S - stored, V - Term Vector
        (offsets, pos) N - with Norms L - Lazym B - Binary N - Numeric</span>
		</form>
	</div>
</body>
</html>