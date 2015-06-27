<%@ page import="java.io.*,java.util.*" %>
<html>
<head>
<title>HTTP Header Request Example</title>
</head>
<body>
<center>
<h2>HTTP Header Request Example</h2>
<table width="100%" border="1" align="center">
<tr bgcolor="#949494">
<th>Header Name</th><th>Header Value(s)</th>
</tr>
<%
   Enumeration headerNames = request.getHeaderNames();
   while(headerNames.hasMoreElements()) {
      String paramName = (String)headerNames.nextElement();
      out.print("<tr><td>" + paramName + "</td>\n");
      String paramValue = request.getHeader(paramName);
      out.println("<td> " + paramValue + "</td></tr>\n");
   }

   out.println("Light = " + request.getHeader("light") + "\n");
   out.println("Temperature = " + request.getHeader("temperature") + "\n");
%>
</table>
</center>

<%! 
  private int initVar=0;
  private int serviceVar=0;
  private int destroyVar=0;
%>
  
<%!
  public void jspInit(){
    initVar++;
    System.out.println("jspInit(): JSP initialization times: "+initVar);
  }
  public void jspDestroy(){
    destroyVar++;
    System.out.println("jspDestroy(): JSP destroy times: "+destroyVar);
  }
%>

<%
  serviceVar++;
  System.out.println("_jspService(): JSP request times: "+serviceVar);

  String content1="initialization times: "+initVar;
  String content2="request times: "+serviceVar;
  String content3="destroy times: "+destroyVar;
%>

<h1><%=content1 %></h1>
<h1><%=content2 %></h1>
<h1><%=content3 %></h1>

</body>
</html>