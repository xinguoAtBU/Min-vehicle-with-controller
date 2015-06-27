<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.io.*,java.util.Properties"%>

    <%  
        InputStream fis = new FileInputStream("F:/apache-tomcat-7.0.57/webapps/myservlet/recvFile.properties");
        Properties prop = new Properties();
        prop.load(fis);

        String speed = "0";
        String centerOffset = prop.getProperty("deviation");
        String passedTriggerNo = prop.getProperty("cornerNum");

        //设置输出信息的格式及字符集  
        response.setContentType("text/xml; charset=UTF-8");  
        response.setHeader("Cache-Control","no-cache");  
        out.println("<response>");  
          
        out.println("<speed>"+speed+ "</speed>");  
        out.println("<centerOffset>" +centerOffset+ "</centerOffset>");  
        out.println("<passedTriggerNo>"+passedTriggerNo+ "</passedTriggerNo>");
        out.println("</response>");  
        out.close();  
    %>   