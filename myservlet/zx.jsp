<head>  
    <META http-equiv=Content-Type content="text/html; charset=gb2312">  
    </head>  
    <script language="javascript">  
      
    var XMLHttpReq;  
        //创建XMLHttpRequest对象         
        function createXMLHttpRequest() {  
            if(window.XMLHttpRequest) { //Mozilla 浏览器  
                XMLHttpReq = new XMLHttpRequest();  
            }  
            else if (window.ActiveXObject) { // IE浏览器  
                try {  
                    XMLHttpReq = new ActiveXObject("Msxml2.XMLHTTP");  
                } catch (e) {  
                    try {  
                        XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP");  
                    } catch (e) {}  
                }  
            }  
        }  
        //发送请求函数  
        function sendRequest() {  
            createXMLHttpRequest();  
            var url = "ajax.jsp";  
            XMLHttpReq.open("GET", url, true);  
            // XMLHttpReq.open("GET","cmd.properties",true);
            XMLHttpReq.onreadystatechange = processResponse;//指定响应函数  
            XMLHttpReq.send(null);  // 发送请求  
        }  
        // 处理返回信息函数  
        function processResponse() {  
            if (XMLHttpReq.readyState == 4) { // 判断对象状态  
                if (XMLHttpReq.status == 200) { // 信息已经成功返回，开始处理信息  
                    DisplayHot();  
                    setTimeout("sendRequest()", 1000);  
                } else { //页面不正常  
                    window.alert("您所请求的页面有异常。");  
                }  
            }  
        }  
        function DisplayHot() {  
            var name = XMLHttpReq.responseXML.getElementsByTagName("name")[0].firstChild.nodeValue;  
            var count = XMLHttpReq.responseXML.getElementsByTagName("count")[0].firstChild.nodeValue;
            var age = XMLHttpReq.responseXML.getElementsByTagName("age")[0].firstChild.nodeValue;  
            var comment = XMLHttpReq.responseXML.getElementsByTagName("comment")[0].firstChild.nodeValue;  
            document.getElementById("product").innerHTML = name;      
            document.getElementById("count").innerHTML = count;  
            document.getElementById("age").innerHTML = age;      
            document.getElementById("comment").innerHTML = comment;    
        }  
      
      
    </script>  
      
    <body onload =sendRequest()>  
    <table style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=0 cellPadding=0 width=200    bgColor=#f5efe7 border=0>  
      
    <TR>  
       <TD align=middle bgColor=#dbc2b0 height=19 colspan="2"><B>Wireless Sensors</B> </TD>  
    </TR>  
    <tr>  
       <td height="20"> product:</td>  
       <td height="20" id="product"> </td>  
    </tr>  
    <tr>  
       <td height="20"> count:</td>  
       <td height="20" id="count"> </td>  
    </tr>  
    <tr>  
       <td height="20"> age:</td>  
       <td height="20" id="age"> </td>  
    </tr>  
    <tr>  
       <td height="20"> comment:</td>  
       <td height="20" id="comment"> </td>  
    </tr>  
    </body>  
    </table>