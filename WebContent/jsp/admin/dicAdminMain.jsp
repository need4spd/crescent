<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<head>
	<script type="text/javascript" src="js/jquery-1.8.1.js"></script>
	<link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css"> 
</head>
<body>
	<div class="navbar navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container-fluid">

      <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </a>
      <a class="brand" href="#">Project name</a>
      <div class="nav-collapse">
        <ul class="nav">

          <li class="active"><a href="#">Home</a></li>
          <li><a href="#about">About</a></li>
          <li><a href="#contact">Contact</a></li>
        </ul>
        <p class="navbar-text pull-right">Logged in as <a href="#">username</a></p>
      </div><!--/.nav-collapse -->

    </div>
  </div>
</div>
 <div class="span9 span-fixed-sidebar">
    <div class="hero-unit">
	<a href="/dictionaryManage.devys?dicType=noun">명사사전</a><br/>
	<a href="/dictionaryManage.devys?dicType=stop">불용어사전</a><br/>
	<a href="/dictionaryManage.devys?dicType=syn">동의어사전</a><br/>
	<a href="/dictionaryManage.devys?dicType=compound">복합명사사전</a><br/>
	</div>
</div>
</body>