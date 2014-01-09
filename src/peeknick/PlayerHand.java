package peeknick;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PlayerHand {
  Card Cards[];
  Player Player;
  
  public PlayerHand(Card card1, Card card2) {
    Cards = new Card[2];
    Cards[0] = card1;
    Cards[1] = card2;
  }
  
  public PlayerHand(Table Table, Player Player, int nSeatPosition) throws Exception {
    this.Player = Player;
    Cards = new Card[2];
    Cards[0] = new Card(Table, enumCardSource.eCS_Player1, Player);
    Cards[1] = new Card(Table, enumCardSource.eCS_Player2, Player);
    
    if(IsValid()) System.out.println("Player cards found on position " + Player.nSeatPosition + ": " + this.toString());
  }
  
  @Override
  public String toString() {return Cards[0].toString() + Cards[1].toString();}
  public boolean IsValid() {return Cards[0].IsValid() || Cards[1].IsValid(); }
  public boolean Suited() { return Cards[0].GetSuit() == Cards[1].GetSuit(); }
  public boolean Paired() { return Cards[0].GetRank() == Cards[1].GetRank(); }
  public int Low() { return Math.min(Cards[0].GetRank().GetVal(), Cards[1].GetRank().GetVal()); }
  public int High() { return Math.max(Cards[0].GetRank().GetVal(), Cards[1].GetRank().GetVal()); }
  public int Difference() { return Math.abs(Cards[0].GetRank().GetVal() - Cards[1].GetRank().GetVal()); }
  
  public boolean IsPreflopPlayable() {
    // just some test
    if(Suited() && Low() > 10) return true;
    if(!Suited() && Low() > 11) return true;
    return false;
  }
  
  JSONArray BuildJSON () {
    JSONArray CardList = new JSONArray();
    for(Card c : this.Cards) CardList.add(c.BuildJSON());
    return CardList;
  }
}
