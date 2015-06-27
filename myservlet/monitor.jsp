<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.sql.*,java.io.*,java.util.Calendar,java.text.*,java.util.Properties"%>
<html>
<head>
    <style> 
        .div-a{ float:left} 
        .div-b{ float:left} 
    </style> 

</head>
<body  onload =sendRequest()>
    <%
    Connection con;
    Statement sql;
    ResultSet rs = null;
    String mode = null;

    String stemp = "samuel";
    try{
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        InputStream fis = new FileInputStream("F:/apache-tomcat-7.0.57/webapps/myservlet/cmd.properties");
        Properties prop = new Properties();
        prop.load(fis);

        mode = prop.getProperty("mode");
        String manualFlg;
        if(mode.equalsIgnoreCase("auto"))
            manualFlg = "false";
        else
            manualFlg = "true";

        System.out.println("manualFlg = " + manualFlg);
        out.write("<SCRIPT language="+"'"+"JavaScript"+"'"+">var manualFlg="+manualFlg+";</SCRIPT>");
    }catch(Exception e){
        out.print(e);
    }
    
    String sss = null;
//     try{
//         String uri="jdbc:mysql://localhost:3306/test";
//         con=DriverManager.getConnection(uri,"root","root");
//         sql=con.createStatement();

//         float speed = 0;
//         int centerOffset = 0;
//         int triggerPassed = 0;

//         try{
//             //while(true){
//                 System.out.println("Read the db records every second");
//                 rs=sql.executeQuery("select * from location where time=(select max(time) from location)");
//                 DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

//                 int cnt=0;

//                 while(rs.next()){
//                     speed = rs.getFloat(3);
//                     centerOffset = rs.getInt(4);
//                     triggerPassed = rs.getInt(5);
//                     sss = "speed = " + speed + ", center offset = " + centerOffset + ", trigger passed = " + triggerPassed;
//                 }
// /*
//                 out.write("<SCRIPT language=" + "'" + "JavaScript" + "'" +
//                 ">var speedFromDB=" + speed + ";" +
//                 ">var centerOffsetFromDB=" + centerOffset + ";" + 
//                 ">var triggerPassedFromDB=" + triggerPassed + ";" +
//                 "</SCRIPT>");
// */                
//                 //Thread.sleep(1000);
//             //}
//         }catch(Exception e){
//         }

//         con.close();
//     }catch(SQLException e1){
//         out.print(e1);
//     }catch(Exception e){
//         out.print(e);
//     }

    %>

    <div class="div-a">
        <canvas id="myCanvas" width="360" height="730" style="border:1px solid #d3d3d3;">
        Your browser does not support the HTML5 canvas tag.
        </canvas>
    </div>


    <div class="div-b">
        <canvas id='canvas' width='640' height='320'> Canvas not supported</canvas>
        <center>
            <H1>
                
                Current Mode: <font color="red"><span id="mymode"><%=mode%></span></font><br>

                Key Control: <br>
                <table>
                    <tr><td width="150">Escape:</td><td>Reset (set to auto mode and stop)</td><tr>
                    <tr><td>Enter:</td><td>Mode switch (Auto | Manual)</td><tr>
                    <tr><td>Blank Space:</td><td>Pause | Resume</td><tr>
                    <tr><td>Up / Down:</td><td>Speed Up | Down</td><tr>
                    <tr><td>Left / Right:</td><td>Turn Left | Right</td><tr>
                </table>
            </H1>
        </center>
    </div>
    <script>
        // alert("hellojspa****");
        var stemp = "<%= stemp %>";
        // var ss = setInterval(
        //     function(){  
        //         // alert(stemp);
        //     },
        // 1000);

    </script>
    <script src = 'example.js'>
    </script>

</body>
</html>