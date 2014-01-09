package peeknick;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.json.simple.JSONObject;


public class Card {
  private enumRanks Rank;
  private enumSuits Suit;
  private BufferedImage biNumber, biSuit;
  private Table Table;
  private enumCardSource eCardSource;
  private Player Player;
  
  // Static templates
  private static BufferedImage biSuitSpade, biSuitHeart, biSuitDiamond, biSuitClub;
  private static BufferedImage biNumber2, biNumber3, biNumber4, biNumber5, biNumber6, biNumber7, biNumber8,
          biNumber9, biNumber10, biNumberJ, biNumberQ, biNumberK, biNumberA;
  
  public Card(Table Table, enumRanks Rank, enumSuits Suit) throws IOException {
    Card.LoadStatics();
    this.Table = Table;
    this.Rank = Rank;
    this.Suit = Suit;
  }
  
  // Get from table (for hero needs hero's position!!
  Card(Table Table, enumCardSource eCardSource) throws Exception {
    Card.LoadStatics();
    this.eCardSource = eCardSource;
    this.Table = Table;
    
    if(eCardSource == enumCardSource.eCS_Player1 || eCardSource == enumCardSource.eCS_Player2) 
      throw new Exception("This constructor only allows table cards.");
    
    biNumber = Screenshooter.crop(Table.biWindowContentMono, Table.outputDir.getAbsolutePath() + "/Card" + eCardSource.toString() + " Number",
          Config.nTableCardsXCoords[eCardSource.GetVal()], Config.nTableCardsYCoords[eCardSource.GetVal()], Config.nCardNumberWidth, Config.nCardNumberHeight);
    biSuit = Screenshooter.crop(Table.biWindowContentMono, Table.outputDir.getAbsolutePath() + "/Card" + eCardSource.toString() + " Suit",
          Config.nTableCardsXCoords[eCardSource.GetVal()], Config.nTableCardsYCoords[eCardSource.GetVal()] + Config.nCardSuitYOffset, Config.nCardSuitWidth, Config.nCardSuitHeight);
    
    this.ParseNumber();
    this.ParseSuit();
  }
  
  // Get from player
  Card(Table Table, enumCardSource eCardSource, Player Player) throws Exception {
    Card.LoadStatics();
    this.eCardSource = eCardSource;
    this.Table = Table;
    this.Player = Player;
    this.eCardSource = eCardSource;
    
    if(eCardSource != enumCardSource.eCS_Player1 && eCardSource != enumCardSource.eCS_Player2) 
      throw new Exception("This constructor can just get player cards");
    
    int baseX = Config.nPlayerCardsXCoords[Player.nSeatPosition] + (eCardSource == enumCardSource.eCS_Player2 ? Config.nPlayerCardsXOffset : 0);
    int baseY = Config.nPlayerCardsYCoords[Player.nSeatPosition] + (eCardSource == enumCardSource.eCS_Player2 ? Config.nPlayerCardsYOffset : 0);
    
    biNumber = Screenshooter.crop(Table.biWindowContentMono, Table.outputDir.getAbsolutePath() + "/Player" + Player.nSeatPosition + " Card" + eCardSource.GetVal() + " Number",
          baseX, baseY, Config.nCardNumberWidth, Config.nCardNumberHeight);
    biSuit = Screenshooter.crop(Table.biWindowContentMono, Table.outputDir.getAbsolutePath() + "/Player" + Player.nSeatPosition + " Card" + eCardSource.GetVal() + " Suit",
          baseX, baseY + Config.nCardSuitYOffset, Config.nCardSuitWidth, Config.nCardSuitHeight);
    
    this.ParseNumber();
    this.ParseSuit();
  }
  
  @Override
  public String toString() {
    return Rank.GetVal() + Suit.toString();
  }
  
  private enumSuits ParseSuit() throws Exception {
    if(Screenshooter.AllBlack(biSuit)) return this.Suit = enumSuits.eS_Unknown;
    if(Screenshooter.EqualImages(biSuit, biSuitClub, 5)) return this.Suit = enumSuits.eS_Club;
    if(Screenshooter.EqualImages(biSuit, biSuitDiamond, 5)) return this.Suit = enumSuits.eS_Diamond;
    if(Screenshooter.EqualImages(biSuit, biSuitHeart, 5)) return this.Suit = enumSuits.eS_Heart;
    if(Screenshooter.EqualImages(biSuit, biSuitSpade, 5)) return this.Suit = enumSuits.eS_Spade;
    return this.Suit = enumSuits.eS_Unknown;
  }
  
  private enumRanks ParseNumber() throws Exception {
    if(Screenshooter.AllBlack(biNumber)) return this.Rank = enumRanks.eR_Unknown;
    if(Screenshooter.EqualImages(biNumber, biNumber2, 5)) return this.Rank = enumRanks.eR_2;
    if(Screenshooter.EqualImages(biNumber, biNumber3, 5)) return this.Rank = enumRanks.eR_3;
    if(Screenshooter.EqualImages(biNumber, biNumber4, 5)) return this.Rank = enumRanks.eR_4;
    if(Screenshooter.EqualImages(biNumber, biNumber5, 5)) return this.Rank = enumRanks.eR_5;
    if(Screenshooter.EqualImages(biNumber, biNumber6, 5)) return this.Rank = enumRanks.eR_6;
    if(Screenshooter.EqualImages(biNumber, biNumber7, 5)) return this.Rank = enumRanks.eR_7;
    if(Screenshooter.EqualImages(biNumber, biNumber8, 5)) return this.Rank = enumRanks.eR_8;
    if(Screenshooter.EqualImages(biNumber, biNumber9, 5)) return this.Rank = enumRanks.eR_9;
    if(Screenshooter.EqualImages(biNumber, biNumber10, 5)) return this.Rank = enumRanks.eR_10;
    if(Screenshooter.EqualImages(biNumber, biNumberJ, 5)) return this.Rank = enumRanks.eR_J;
    if(Screenshooter.EqualImages(biNumber, biNumberQ, 5)) return this.Rank = enumRanks.eR_Q;
    if(Screenshooter.EqualImages(biNumber, biNumberK, 5)) return this.Rank = enumRanks.eR_K;
    if(Screenshooter.EqualImages(biNumber, biNumberA, 5)) return this.Rank = enumRanks.eR_A;
    return this.Rank = enumRanks.eR_Unknown;
  }
  
  private static void LoadStatics() throws IOException {
    if(biSuitClub != null) return;
    biSuitClub = ImageIO.read(new File(Config.sCardTemplatesFolder + "/club.png"));
    biSuitDiamond = ImageIO.read(new File(Config.sCardTemplatesFolder + "/diamond.png"));
    biSuitHeart = ImageIO.read(new File(Config.sCardTemplatesFolder + "/heart.png"));
    biSuitSpade = ImageIO.read(new File(Config.sCardTemplatesFolder + "/spade.png"));
    
    biNumber2 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n2.png"));
    biNumber3 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n3.png"));
    biNumber4 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n4.png"));
    biNumber5 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n5.png"));
    biNumber6 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n6.png"));
    biNumber7 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n7.png"));
    biNumber8 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n8.png"));
    biNumber9 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n9.png"));
    biNumber10 = ImageIO.read(new File(Config.sCardTemplatesFolder + "/n10.png"));
    biNumberJ = ImageIO.read(new File(Config.sCardTemplatesFolder + "/nJ.png"));
    biNumberQ = ImageIO.read(new File(Config.sCardTemplatesFolder + "/nQ.png"));
    biNumberK = ImageIO.read(new File(Config.sCardTemplatesFolder + "/nK.png"));
    biNumberA = ImageIO.read(new File(Config.sCardTemplatesFolder + "/nA.png"));
  }
  
  public boolean IsValid() { return(this.Suit != enumSuits.eS_Unknown || this.Rank != enumRanks.eR_Unknown); }
  public enumSuits GetSuit() { return this.Suit; }
  public enumRanks GetRank() { return this.Rank; }
  
  JSONObject BuildJSON() {
    JSONObject obj = new JSONObject();
    obj.put("rank", this.GetRank().GetVal());
    obj.put("suit", this.GetSuit().toString());
    obj.put("valid", this.IsValid());
    obj.put("source", this.eCardSource.toString());
    if(this.Player != null) {
      obj.put("player_seating_pos", this.Player.nSeatPosition);
      if(Player.HandPosition != null) obj.put("player_game_pos", this.Player.HandPosition.GetVal());
    }
    
    return obj;
  }
  
}
