<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<SCRIPT LANGUAGE = "JavaScript" >
var tmp = "testing";
function test(){
    var xmlHttp; 
     
    // 处理Ajax浏览器兼容
    if (window.ActiveXObject) {   
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");   
    }   
    else if (window.XMLHttpRequest) {   
        xmlHttp = new XMLHttpRequest();   
    } 
     
    var url = "bbb.jsp?test=" + tmp; // 使用JS中变量tmp    
    xmlHttp.open("post",url,true);   //配置XMLHttpRequest对象
      
    //设置回调函数
    xmlHttp.onreadystatechange = function (){
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
           // var respText = xmlHttp.responseText;
           alert("调用成功!");       }
    }
    xmlHttp.send(null);  // 发送请求
}
</script>
<meta http-equiv=Content-Type content="text/html;charset=GB2312">
<title>Ajax简单案例</title>
</head>
<body >
<input type="button" name="btTest1" value="测试"  onclick="test()"/>
</body>
</html>