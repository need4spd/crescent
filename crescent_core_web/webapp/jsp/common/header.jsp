<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<!-- <meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">-->
<meta content="text/html; charset=UTF-8">
<script type="text/javascript" src="js/jquery-1.8.1.js"></script>
<script src="js/bootstrap.min.js"></script>

<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">

<!-- Le styles -->
<style type="text/css">
body {
	padding-top: 60px;
	padding-bottom: 40px;
}

.sidebar-nav {
	padding: 9px 0;
}
</style>
<script>
function newAlert (message, id) {
    $("#"+id).append($("<div class='alert alert-block alert-error fade in' id='keyword_alert'><p> " + message + " </p></div>"));
	$("#keyword_alert").delay(2000).fadeOut("slow", function () { $(this).remove(); });
}
</script>
</head>