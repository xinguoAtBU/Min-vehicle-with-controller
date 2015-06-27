 <%@ page contentType="text/html; charset=gb2312" %>  
      
    <%  
    //设置输出信息的格式及字符集  
    response.setContentType("text/xml; charset=UTF-8");  
    response.setHeader("Cache-Control","no-cache");  
    out.println("<response>");  
      
    out.println("<name>"+(int)(Math.random()*10)+ "</name>");  
    out.println("<count>" +(int)(Math.random()*100)+ "</count>");  
    out.println("<age>"+(int)(Math.random()*10)+ "</age>");  
    out.println("<comment>" +"is it good?"+ "</comment>");  
    out.println("</response>");  
    out.close();  
    %>   