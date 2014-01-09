package peeknick;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import peeknick.errormanager.WindowNotFoundException;

public class Screenshooter {
  public static BufferedImage captureWindow(String sTitle) throws WindowNotFoundException, Exception {
    //int[] rect = GetWindowRect.getRect(Title);
    //return captureRect(rect[0], rect[1], rect[2], rect[3]);
    return peeknick.system.Paint.captureWindow(sTitle);
  }
  
  public static BufferedImage captureRect(int x, int y, int x2, int y2) throws Exception {
    Rectangle screenRectangle = new Rectangle(x, y, x2-x, y2-y);
    Robot robot = new Robot();
    return robot.createScreenCapture(screenRectangle);
  }
  
  public static BufferedImage captureScreen() throws Exception {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle screenRectangle = new Rectangle(screenSize);
    Robot robot = new Robot();
    return robot.createScreenCapture(screenRectangle);
  }
  
  public static BufferedImage toMono(BufferedImage image) {
    double image_width = image.getWidth();
    double image_height = image.getHeight();
    BufferedImage bimg;

    // reduce brightness hard (substracting)
    RescaleOp rescaleOp = new RescaleOp(1.0f, -170, null);
    rescaleOp.filter(image, image); 
    
    // saturate a fucking lot
    rescaleOp = new RescaleOp(50.0f, 0, null);
    rescaleOp.filter(image, image); 
    
    // now to monochrome
    bimg = new BufferedImage((int)image_width, (int)image_height,
                                BufferedImage.TYPE_BYTE_BINARY);
    Graphics2D gg = bimg.createGraphics();
    gg.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
    
    return bimg;
  }
  
  public static BufferedImage crop(BufferedImage image, String path, int x, int y, int w, int h) throws IOException {
    BufferedImage cropped = image.getSubimage(x, y, w, h);
    if(Config.bStoreDebugImages) ImageIO.write(cropped, Config.sImageFormat, new File(path + "." + Config.sImageFormat));
    return cropped;
  }
  
  public static boolean EqualImages(BufferedImage bi1, BufferedImage bi2, int nErrorMargin) throws Exception {
    if(bi1.getHeight() != bi2.getHeight()) throw new Exception("Cannot compare images of inequal size! (heights " + bi1.getHeight() + " vs " + bi2.getHeight() + ")");
    if(bi1.getWidth() != bi2.getWidth()) throw new Exception("Cannot compare images of inequal size!(widths " + bi1.getWidth() + " vs " + bi2.getWidth() + ")");
    
    //if(bi1.getColorModel() != bi2.getColorModel()) throw new Exception("Cannot compare images of inequal color models!");
    
    // Get all the pixels
    int w = bi1.getWidth(null);
    int h = bi1.getHeight(null);
    int[] rgbs1 = new int[w * h];
    bi1.getRGB(0, 0, w, h, rgbs1, 0, w);
    int[] rgbs2 = new int[w * h];
    bi2.getRGB(0, 0, w, h, rgbs2, 0, w);
    
    // check pixels one by one
    int nErrors = 0;
    for(int i = 0; i<w*h; i++) {
      if (rgbs1[i] != rgbs2[i]) {
        nErrors++;
        if(nErrors > nErrorMargin) return false;
      }
    }
    
    return true;
  }
  
  public static boolean EqualImages(BufferedImage bi1, BufferedImage bi2) throws Exception {
    return EqualImages(bi1, bi2, 0);
  }
  
  public static boolean AllBlack(BufferedImage biImage) {
    return AllSameColor(biImage, Config.nBlack);
  }
  
  public static boolean AllWhite(BufferedImage biImage) {
    return AllSameColor(biImage, Config.nWhite);
  }
  
  public static boolean AllSameColor(BufferedImage biImage, int nColor)  {
    return AllSameColor(biImage, nColor, 0);
  }
  
  public static boolean AllSameColor(BufferedImage biImage, int nColor, int nErrorMargin)  {
    int w = biImage.getWidth(null);
    int h = biImage.getHeight(null);
    int[] rgbs = new int[w * h];
    biImage.getRGB(0, 0, w, h, rgbs, 0, w);
    
    int nErrors = 0;
    for(int i = 0; i<w*h; i++) {
      if (rgbs[i] != nColor) {
        nErrors++;
        if(nErrors > nErrorMargin) return false;
      }
    }
    return true;
  }
}
