package peeknick;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Hand {
  private Card[] arrTableCards;
  private Table Table;
  
  public Hand(Table table) throws Exception {
    this(table, false);
  }
  
  public Hand(Table table, boolean bInProgress) {
    this.Table = table;
    arrTableCards = new Card[5];
  }
  
  public void ParseTableCards() throws Exception {
    arrTableCards[0] = new Card(Table, enumCardSource.eCS_Flop1);
    arrTableCards[1] = new Card(Table, enumCardSource.eCS_Flop2);
    arrTableCards[2] = new Card(Table, enumCardSource.eCS_Flop3);
    arrTableCards[3] = new Card(Table, enumCardSource.eCS_Turn);
    arrTableCards[4] = new Card(Table, enumCardSource.eCS_River);
    System.out.println("Table cards: " + arrTableCards[0].toString() + " " + arrTableCards[1].toString() + " " + arrTableCards[2].toString() + " " + arrTableCards[3].toString() + " " + arrTableCards[4].toString() + " ");
  }
  
  public void Finished() {
    
  }
  
  JSONArray BuildJSON() {
    JSONArray CardList = new JSONArray();
    for(Card c : arrTableCards) CardList.add(c.BuildJSON());
    return CardList;
  }
}
