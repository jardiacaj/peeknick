package peeknick;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray;
import peeknick.errormanager.WindowNotFoundException;
import org.json.simple.JSONObject;

public class Table {
  // Table values
  private String sWindowTitle, sShortName;
  
  // Images
  private BufferedImage biWindowContent, biHandNumber, biPreviousHandNumber, biWindowTitle, biPot, biScreenshot;
  BufferedImage biWindowContentMono;
  
  // Other
  private Player[] arrPlayers;
  private Player Dealer;
  File outputDir;
  private Hand currentHand;
  Player Hero;
  private int nProcessNumber = 0;
  private int nXWinBorder, nYWinBorder; // Window border sizes
  
  
  public Table(String Title, int nBorderSizeX, int nBorderSizeY) throws IOException {
    this.sWindowTitle = Title;
    this.nXWinBorder = nBorderSizeX;
    this.nYWinBorder = nBorderSizeY;
    
    this.sShortName = sWindowTitle.substring(0, Math.min(10, sWindowTitle.length()));
    this.PrepareMainOutputDir();
    arrPlayers = new Player[9];
  }
  
  public void Process() throws Exception {
    // Prepare
    //System.out.println("############ Processing " + this.sShortName + " (" + this.nProcessNumber + ")");
    if(Config.bStoreDebugImages) this.outputDir = PrepareProcessingOutputDir(nProcessNumber);
    BufferedImage biOldHandNumber = biHandNumber;
    Hero = null;
    
    // Capture
    try{
      this.DoScreenshot();
    } catch(WindowNotFoundException e) {
      System.out.println("############ " + this.sShortName + ": Window not found!");
      return;
    }
    if(Config.bStoreDebugImages)ImageIO.write(this.biScreenshot, Config.sImageFormat, new File(outputDir.getAbsolutePath() + "/screenshot." + Config.sImageFormat));
    
    // Check window resizing
    if(this.biScreenshot.getWidth() != Config.nWindowContentWidth || this.biScreenshot.getHeight() != Config.nWindowContentHeight) {
      System.out.println("############ " + this.sShortName + ": Window has been resized! Size errors: width=" + (biScreenshot.getWidth() - Config.nWindowContentWidth) + " height=" + (biScreenshot.getHeight() - Config.nWindowContentHeight));
      return;
    }
    
    // Crop
    this.cropWindowContent();
    this.cropHandNumber();
    boolean InBetweenHands = Screenshooter.AllBlack(biHandNumber);
    
    // Check if we got a screenshot in between hands
    if(this.currentHand != null && InBetweenHands) {
      System.out.println("############ HAND FINISHED ON " + this.sShortName + "!");
      this.currentHand.Finished();
      this.currentHand = null;
      this.nProcessNumber++;
    }
    
    // Check new hand
    else if(!InBetweenHands && biOldHandNumber != null && !Screenshooter.EqualImages(biHandNumber, biOldHandNumber)) {
      System.out.println("############ NEW HAND ON " + this.sShortName + "!");
      // Signal hand that it's finished
      if(this.currentHand != null) this.currentHand.Finished();
      for(int i = 0; i < 9; i++) arrPlayers[i] = new Player(i, this);
      this.cropPot();
      for(Player p:this.arrPlayers) p.Process(1);
      this.FindDealerAndSetPositions();
      // New hand
      this.currentHand = new Hand(this);
      currentHand.ParseTableCards();
      this.SendDump();
      this.nProcessNumber++;
    }
    
    // Check new hand but in progress
    else if(this.currentHand == null) {
      System.out.println("############ HAND IN PROGRESS ON " + this.sShortName + "!");
      
      for(int i = 0; i < 9; i++) arrPlayers[i] = new Player(i, this);
      this.cropPot();
      for(Player p:this.arrPlayers) p.Process(0);
      this.FindDealerAndSetPositions();
      // New hand
      this.currentHand = new Hand(this, true);
      currentHand.ParseTableCards();
      this.nProcessNumber++;
    }
    
    // Check hero's turn
    if(HerosTurn()) {
      System.out.println("############ HERO'S TURN ON " + this.sShortName + "!");
      this.cropPot();
      for(Player p:this.arrPlayers) p.Process(2);
      currentHand.ParseTableCards();
      if(Hero == null) throw new Exception("Hero's turn but hero not found");
      
      if(Hero.PlayerHand.IsPreflopPlayable()) System.out.println("MiniIA says: let's think about it");
      else System.out.println("MiniIA says: forget it");
      
      this.SendDump();
      
      this.nProcessNumber++;
    }
  }
  
  private void SendDump() {
    System.out.println(this.BuildJSON().toJSONString());
  }
  
  private JSONObject BuildJSON() {
    JSONObject obj = new JSONObject();
    obj.put("dealer_sits_on", this.Dealer.nSeatPosition);
    if(this.Hero != null) obj.put("hero_sits_on", this.Hero.nSeatPosition);
    obj.put("table_cards", this.currentHand.BuildJSON());
    obj.put("iteration_number", this.nProcessNumber);
    obj.put("short_name", this.sShortName);
    obj.put("full_name", this.sWindowTitle);
    obj.put("num_playing", this.GetNumPlayingPlayers());
    obj.put("heros_turn", this.HerosTurn());
    
    JSONArray PlayerList = new JSONArray();
    for(Player p : arrPlayers) PlayerList.add(p.BuildJSON());
    obj.put("players", PlayerList);
    
    return obj;
  }
  
  private boolean HerosTurn() {
    for(int i = 0; i < Config.nHerosTurnCheckXCoords.length; i++)
      if(biWindowContentMono.getRGB(Config.nHerosTurnCheckXCoords[i], Config.nHerosTurnCheckYCoords[i]) != Config.nWhite) return false;
    return true;
  }
  
  private File PrepareMainOutputDir() {
    outputDir = new File(Config.sMainOutputFolder + "/" + sShortName);
    outputDir.mkdir();
    return outputDir;
  }
  
  private File PrepareProcessingOutputDir(int nProcessNumber) {
    outputDir = new File(Config.sMainOutputFolder + "/" + sShortName + "/" + nProcessNumber);
    outputDir.mkdir();
    return outputDir;
  }
  
  public void CaptureEverythingFromScreenshot(String sScreenshotpath) throws Exception {
    this.biScreenshot = ImageIO.read(new File(sScreenshotpath));
    if(Config.bStoreDebugImages) ImageIO.write(this.biScreenshot, Config.sImageFormat, new File(outputDir.getAbsolutePath() + "/screenshot." + Config.sImageFormat));
    if(this.biScreenshot.getWidth() != Config.nWindowContentWidth || this.biScreenshot.getHeight() != Config.nWindowContentHeight) {
      System.out.println("Window has been resized!");
      return;
    }
    cropAll();
    for(Player p:this.arrPlayers) p.Process(0);
  }
  
  private void DoScreenshot() throws WindowNotFoundException, Exception {
    this.biScreenshot = Screenshooter.captureWindow(this.sWindowTitle);
  }
  
  private void cropWindowContent() throws Exception {
    this.biWindowContent = Screenshooter.crop(biScreenshot, outputDir.getAbsolutePath() + "/WindowContent", nXWinBorder, nYWinBorder, Config.nWindowContentWidth, Config.nWindowContentHeight);
    this.biWindowContentMono = Screenshooter.toMono(this.biWindowContent);
    if(Config.bStoreDebugImages) ImageIO.write(biWindowContentMono, Config.sImageFormat, new File(outputDir.getAbsolutePath() + "/WindowContent_mono." + Config.sImageFormat));
  }
  
  private void cropHandNumber() throws Exception {
    biHandNumber = Screenshooter.crop(this.biWindowContentMono, outputDir.getAbsolutePath() + "/HandNum", 49, 10, 87, 8);
  }
  
  private void cropPreviousHandNumber() throws Exception {
    biPreviousHandNumber = Screenshooter.crop(this.biWindowContentMono, outputDir.getAbsolutePath() + "/PrevHandNum", 231, 10, 87, 8);
  }
  
  private void cropPot() throws Exception {
    biPot = Screenshooter.crop(this.biWindowContentMono, outputDir.getAbsolutePath() + "/Pot", 346, 46, 110, 12);
  }
  
  private void cropAll() throws Exception {
    cropWindowContent();
    cropHandNumber();
    cropPreviousHandNumber();
    cropPot();
    
    // this one is wrong because the screenshot now is just the window content!!
    //biWindowTitle = Screenshooter.crop(this.biScreenshot, outputDir.getAbsolutePath() + "/WindowTitle", 28, 9, 400, 11);
  }
  
  private int GetNumPlayingPlayers() {
    int num = 0;
    for (Player p:arrPlayers) if(p.isPlaying()) num ++;
    return num;
  }
  
  private void FindDealerAndSetPositions() throws Exception {
    for(int i = 0; i < 9; i++) {
      if(biWindowContentMono.getRGB(Config.nButtonXCoords[i], Config.nButtonYCoords[i]) == Config.nWhite) {
        this.Dealer = arrPlayers[i];
        System.out.println("Dealer is on position " + i);
        
        int numPositionsGiven = 0;
        for(int k = i; k < arrPlayers.length; k++) {
          if(arrPlayers[k].isPlaying()) {
            arrPlayers[k].HandPosition = enumPlayerPosition.get(numPositionsGiven);
            System.out.println("Player " + k + " is on position " + arrPlayers[k].HandPosition.toString());
            numPositionsGiven++;
          }
        }
        for(int k = 0; k < i; k++) {
          if(arrPlayers[k].isPlaying()) {
            arrPlayers[k].HandPosition = enumPlayerPosition.get(numPositionsGiven);
            System.out.println("Player " + k + " is on position " + arrPlayers[k].HandPosition.toString());
            numPositionsGiven++;
          }
        }
        return;
      }
    }
    throw new Exception("Dealer not found");
  }
  
}
