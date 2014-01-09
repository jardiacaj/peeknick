/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peeknick;

public class Config {
  // General config
  public static boolean bStoreDebugImages = false;
  public static final int nMillisecsSleepBetweenUpdates = 200;
  public static final String sImageFormat = "png";
  
  // OCR
  public static final boolean bOCRDebugoutput = false;
  
  
  // CONSTANTS ###########################################
  // Colors
  public final static int nBlack = -16777216;
  public final static int nWhite = -1;
  
  // File names
  public final static String sTablesFilename = "tables.txt";
  public final static String sWindowBordersFilename = "window_borders.txt";
  public final static String sTemplatesFolder = "templates";
  public final static String sCardTemplatesFolder = "templates/cards";
  
  // Window screenshots config
  public final static int nWindowContentWidth = 792;
  public final static int nWindowContentHeight = 546;
  public final static String sMainOutputFolder = "output";
  
  // Card cropping constants
  public final static int[] nTableCardsXCoords = {0, 0, 271, 325, 379, 433, 487};
  public final static int[] nTableCardsYCoords = {0, 0, 185, 185, 185, 185, 185};
  public final static int nCardSuitYOffset = 17;
  public final static int nCardNumberWidth = 10;
  public final static int nCardNumberHeight = 13;
  public final static int nCardSuitWidth = 10;
  public final static int nCardSuitHeight = 14;
  public final static int[] nPlayerCardsXCoords = {235, 0, 628, 0, 564, 363, 0, 0, 99};
  public final static int[] nPlayerCardsYCoords = {31, 0, 99, 0, 338, 356, 0, 0, 106};
  public final static int nPlayerCardsXOffset = 15;
  public final static int nPlayerCardsYOffset = 4;
  
  // Player cropping constants
  public final static int nPlayerBoxWidth = 96; // player box sizes
  public final static int nPlayerBoxHeight = 14;
  public final static int nStackBoxYOffset = 19; // offset between name and stack
  public final static int nSeparatorXOffset = 25; // offset between name and separator line
  public final static int nSeparatorYOffset = 16;
  public final static int nSeparatorWidth = 20;
  public final static int[] nNameBoxXCoords = {131, 563, 686, 652, 633, 432, 233, 46, 11}; // player boxes coords
  public final static int[] nNameBoxYCoords = {50, 50, 169, 295, 356, 373, 356, 295, 169};
  public final static int[] nButtonXCoords = {231, 486, 604, 652, 611, 439, 237, 150, 147}; // button pixel check coords
  public final static int[] nButtonYCoords = {121, 114, 141, 232, 323, 356, 340, 281, 196};
  //public final static int[] nHerosTurnCheckXCoords = {582, 588, 582, 588}; these checked the textbox to introduce bet qty
  //public final static int[] nHerosTurnCheckYCoords = {461, 461, 472, 472}; but doesn't work when we have to do an all-in :(
  public final static int[] nHerosTurnCheckXCoords = {429, 446, 463, 463};
  public final static int[] nHerosTurnCheckYCoords = {514, 516, 512, 518};
}
