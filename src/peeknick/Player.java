package peeknick;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Player {
  int nSeatPosition; // change this please
  enumPlayerPosition HandPosition;
  private Table Table;
  PlayerHand PlayerHand;
  private boolean IsHero = false;
  
  private enumPlayerState Status;
  private BufferedImage biName, biStack, biSeparatorLine;
  private int nCurrentStack;
  private int nStartingStack;
  
  private static BufferedImage biSittingOut, biDisconnected, biDisconnected_bis, biAllIn;
  
  public Player(int SeatPosition, Table Table) throws IOException {
    this.nSeatPosition = SeatPosition;
    this.Table = Table;
    Player.LoadStatics();
  }
  
  public boolean isPlaying() { return Status == enumPlayerState.ePS_Allin || Status == enumPlayerState.ePS_Normal; }
  
  public void Process(int nHandState) throws Exception {
    // Capture
    this.cropThingies();
    this.getStatus();
    switch(this.Status) {
      case ePS_Allin: this.nCurrentStack = 0; System.out.println("Player " + this.nSeatPosition + " is all-in"); break;
      case ePS_Disconnected: System.out.println("Player " + this.nSeatPosition + " is disconnected"); break;
      case ePS_SittingOut: this.nCurrentStack = 0; System.out.println("Player " + this.nSeatPosition + " is sitting out"); break;
      case ePS_EmptySeat: this.nCurrentStack = 0; System.out.println("Player " + this.nSeatPosition + ": empty seat"); break;
      case ePS_Normal: this.parseStack(); break;
      default: throw new Exception("Player status not defined");
    }
    
    // State-dependant actions
    switch(nHandState) {
      case 0: // stateless
        break;
      case 1: // hand start
        this.nStartingStack = this.nCurrentStack;
        break;
      case 2: // hero's turn
        break;
      default:
        throw new Exception("Hand status not defined");
    }
    
    // status check?
    this.PlayerHand = new PlayerHand(Table, this, nSeatPosition);
    if(this.IsHero = PlayerHand.IsValid()) {
      Table.Hero = this;
      System.out.println("Assuming hero is on position " + this.nSeatPosition);
    }
  }
  
  private void cropThingies() throws Exception {
    biName = Screenshooter.crop(Table.biWindowContentMono, Table.outputDir.getAbsolutePath() + "/Player" + nSeatPosition + " Name",
          Config.nNameBoxXCoords[nSeatPosition], Config.nNameBoxYCoords[nSeatPosition], Config.nPlayerBoxWidth, Config.nPlayerBoxHeight);
    biStack = Screenshooter.crop(Table.biWindowContentMono, Table.outputDir.getAbsolutePath() + "/Player" + nSeatPosition + " Stack",
          Config.nNameBoxXCoords[nSeatPosition], Config.nNameBoxYCoords[nSeatPosition] + Config.nStackBoxYOffset, Config.nPlayerBoxWidth, Config.nPlayerBoxHeight);
    biSeparatorLine = Screenshooter.crop(Table.biWindowContentMono, Table.outputDir.getAbsolutePath() + "/Player" + nSeatPosition + " Separator",
          Config.nNameBoxXCoords[nSeatPosition] + Config.nSeparatorXOffset, Config.nNameBoxYCoords[nSeatPosition] + Config.nSeparatorYOffset, Config.nSeparatorWidth, 1);
  }
  
  private void getStatus() throws Exception {
    if(!Screenshooter.AllWhite(this.biSeparatorLine)) this.Status = enumPlayerState.ePS_EmptySeat;
    else if(Screenshooter.EqualImages(this.biStack, Player.biSittingOut, 10)) this.Status = enumPlayerState.ePS_SittingOut;
    else if(Screenshooter.EqualImages(this.biStack, Player.biDisconnected, 10)) this.Status = enumPlayerState.ePS_Disconnected;
    else if(Screenshooter.EqualImages(this.biStack, Player.biDisconnected_bis, 10)) this.Status = enumPlayerState.ePS_Disconnected;
    else if(Screenshooter.EqualImages(this.biStack, Player.biAllIn, 10)) this.Status = enumPlayerState.ePS_Allin;
    else this.Status = enumPlayerState.ePS_Normal;
  }
  
  private void parseStack() {
    try {
      nCurrentStack = Integer.parseInt(
              OCR.Process("Player " + nSeatPosition, biStack).replace("$", "").replace(".", ""));
      System.out.println("Player " + nSeatPosition + ": " + nCurrentStack);
    } catch(Exception e) {
      System.out.println("Player " + nSeatPosition + ": Ocr failed!");
      nCurrentStack = 0;
    }
  }
  
  private static void LoadStatics() throws IOException {
    if(biSittingOut != null) return;
    biSittingOut = ImageIO.read(new File(Config.sTemplatesFolder + "/sittingout.png"));
    biDisconnected = ImageIO.read(new File(Config.sTemplatesFolder + "/disconnected.png"));
    biDisconnected_bis = ImageIO.read(new File(Config.sTemplatesFolder + "/disconnected_bis.png"));
    biAllIn = ImageIO.read(new File(Config.sTemplatesFolder + "/allin.png"));
  }
  
  public boolean IsHero() { return this.IsHero; }
  
  JSONObject BuildJSON() {
    JSONObject obj = new JSONObject();
    if(HandPosition != null) obj.put("game_pos", this.HandPosition.GetVal());
    obj.put("is_hero", this.IsHero);
    obj.put("status", this.Status.toString());
    obj.put("current_stack", this.nCurrentStack);
    obj.put("table_pos", this.nSeatPosition);
    obj.put("starting_stack", this.nStartingStack);
    obj.put("hand", this.PlayerHand.BuildJSON());
    obj.put("is_playing", this.isPlaying());
    return obj;
  }
}
