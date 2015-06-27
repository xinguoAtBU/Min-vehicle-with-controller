<%@ page language="java" pageEncoding="utf-8"%>
<%
    String test = request.getParameter("test"); // 接收aaa.jsp界面传来的test参数<br>
    %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<br>
<html>
<br>
<head>
<br></head>
<br><body>
<br>    <input type="text" name="test" value="<%=test%>">
 <!-- 引用jsp中变量 --><br>
 </body>
 </html>