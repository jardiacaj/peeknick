/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peeknick;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


enum enumCardSource {
  eCS_Player1 (0), eCS_Player2 (1), eCS_Flop1 (2), eCS_Flop2 (3), eCS_Flop3 (4), eCS_Turn (5), eCS_River (6);
  
  private int nVal;
  private enumCardSource(int nVal) {this.nVal=nVal;}
  public int GetVal() {return nVal;}
  
  @Override
  public String toString() {
    switch(this) {
      case eCS_Flop1: return "Flop1";
      case eCS_Flop2: return "Flop2";
      case eCS_Flop3: return "Flop3";
      case eCS_Turn: return "Turn";
      case eCS_River: return "River";
      case eCS_Player1: return "Player1";
      case eCS_Player2: return "Player2";
    }
    return "WRONG!";
  }
}

enum enumSuits {
  eS_Club, eS_Heart, eS_Spade, eS_Diamond, eS_Unknown;
  
  public char toChar() {
    switch(this) {
      case eS_Club: return 'c';
      case eS_Diamond: return 'd';
      case eS_Heart: return 'h';
      case eS_Spade: return 's';
      case eS_Unknown: return '?';
    }
    return '-';
  }
  
  @Override
  public String toString() {return Character.toString(toChar());}
}

enum enumRanks {
  eR_2 (2), eR_3 (3), eR_4 (4), eR_5 (5), eR_6 (6), eR_7 (7), eR_8 (8), eR_9 (9),
  eR_10 (10), eR_J (11), eR_Q (12), eR_K (13), eR_A (14), eR_Unknown (0);
  
  private int nVal;
  enumRanks(int nVal) { this.nVal = nVal; }
  public int GetVal() { return nVal; }
}

enum enumPlayerPosition {
  ePP_Button (0), ePP_SmallBlind (1), ePP_BigBlind (2), ePP_UTG (3), ePP_EP1 (4), ePP_EP2 (5), ePP_MP (6), ePP_LP (7), ePP_Cutoff (8);

  private int nVal;
  private enumPlayerPosition(int nVal) {this.nVal = nVal;};
  public int GetVal() { return nVal; }
  
  // generate reverse lookup
  private static final Map<Integer,enumPlayerPosition> lookup = new HashMap<>();
  static {for(enumPlayerPosition s : EnumSet.allOf(enumPlayerPosition.class)) lookup.put(s.GetVal(), s);}
  public static enumPlayerPosition get(int nVal) {return lookup.get(nVal);}
  
  @Override
  public String toString() {
    switch(this) {
      case ePP_SmallBlind: return "Small Blind";
      case ePP_BigBlind: return "Big Blind";
      case ePP_UTG: return "Under the gun";
      case ePP_EP1: return "Early 1";
      case ePP_EP2: return "Early 2";
      case ePP_MP: return "Middle";
      case ePP_LP: return "Late";
      case ePP_Cutoff: return "Cutoff";
      case ePP_Button: return "Button";
    }
    return "WRONG!";
  }
}

enum enumPlayerState {
  ePS_Normal, ePS_Allin, ePS_Disconnected, ePS_SittingOut, ePS_EmptySeat
}
