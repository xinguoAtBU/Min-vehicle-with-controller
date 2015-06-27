<%@ page contentType="image/jpeg"
import="java.awt.*,java.awt.image.*,
com.sun.image.codec.jpeg.*,java.util.*"
%>
<%
    // // Create image
    // int width=800, height=600;
    // BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    // // Get drawing context
    // Graphics g = image.getGraphics();
    // // Fill background
    // g.setColor(Color.white);
    // g.fillRect(0, 0, width, height);

    // // Draw a circle
    // g.setColor(Color.blue);
    // g.fillOval(50, 50, 10, 10);
    // // Dispose context
    // g.dispose();
    // // Send back image
    // ServletOutputStream sos = response.getOutputStream();
    // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(sos);
    // encoder.encode(image);
    // sos.close();

    int xx = 0;
    int yy = 0;
    for(int k=0; k<10; k++)
    {
        int width=200+2*k;
        int height=200; 
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);   
         
        Graphics g = image.getGraphics(); 
        g.setColor(Color.red); 
        g.drawRect(xx,yy,width,height); 
        g.fillRect(xx,yy,width,height); 
        //g.setColor(Color.black);
        //g.drawString("loop:  "+Integer.toString(k),width/2,height/2);
        g.dispose(); 
        ServletOutputStream sos = response.getOutputStream(); 
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(sos); 
        encoder.encode(image); 
        xx += 20;
        yy += 20;
    }
%>