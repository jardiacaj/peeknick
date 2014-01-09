package peeknick;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Peeknick {
  
  private int nXBorderSize, nYBorderSize;
  private Map<String, Table> mapTables = new HashMap<>();
  
  /**
    * @param args the command line arguments
    */
  public static void main(String[] args) throws Exception {
    String sScreenshotPath = null;
    boolean bOneRun = false;
    
    // Argument processing
    for(String arg:args) {
      if(arg.substring(0, Math.min(3, arg.length())).equals("-s=")) sScreenshotPath = arg.substring(3);
      else if(arg.equals("-onerun")) bOneRun = true;
      else if(arg.equals("-storeimages")) Config.bStoreDebugImages = true;
      else if(arg.equals("-dontstoreimages")) Config.bStoreDebugImages = false;
      else {
        System.out.println("Unrecognized argument: " + arg);
        return;
      }
    }
    
    Peeknick Me = new Peeknick();
    Me.LoadConfig();
    
    if(sScreenshotPath != null) {
      Me.AnalyzeScreenshot(sScreenshotPath);
      return;
    }
    
    Me.MainLoop(bOneRun);
  }
  
  private void MainLoop(boolean bOneRun) throws Exception {
    
    File fileTables = new File(Config.sTablesFilename);
    long nFileTables_LastModificationTimeStampWhenLoaded = 0;
    
    while(true) {
      // Check if tables.txt has to be reloaded
      if(fileTables.lastModified() != nFileTables_LastModificationTimeStampWhenLoaded) {
        System.out.println("Reloading tables file");
        this.LoadTablesFile();
        nFileTables_LastModificationTimeStampWhenLoaded = fileTables.lastModified();
      }
      
      // Process each table
      for (Table T : this.mapTables.values()) {
        T.Process();
      }
      
      if(bOneRun) break;
      Thread.sleep(Config.nMillisecsSleepBetweenUpdates);
    }
    
  }
  
  private void LoadTablesFile() throws Exception {
    // Open and read tables file
    Set <String> setTableNamesInFile = new HashSet<>();
    String strLine;
    FileInputStream fTables = new FileInputStream(Config.sTablesFilename);
    DataInputStream disTables = new DataInputStream(fTables);
    BufferedReader brTables = new BufferedReader(new InputStreamReader(disTables));
    while ((strLine = brTables.readLine()) != null) setTableNamesInFile.add(strLine);
    
    // Add new tables
    for(String sTableName:setTableNamesInFile) {
      if (!this.mapTables.containsKey(sTableName)) 
        this.mapTables.put(sTableName, new Table(sTableName, nXBorderSize, nYBorderSize));
    }
    
    // Remove old tables
    for(String sTableName:mapTables.keySet()) {
      if (!setTableNamesInFile.contains(sTableName))
        mapTables.remove(sTableName);
    }
  }
  
  private void AnalyzeScreenshot(String sScreenshotPath) throws Exception {
    Table table = new Table(sScreenshotPath, nXBorderSize, nYBorderSize);
    table.CaptureEverythingFromScreenshot(sScreenshotPath);
  }
  
  private void LoadConfig() throws Exception {
    // Load window border offsets
    FileInputStream fWindowBorders = new FileInputStream(Config.sWindowBordersFilename);
    DataInputStream disWindowBorders = new DataInputStream(fWindowBorders);
    BufferedReader brWindowsBorders = new BufferedReader(new InputStreamReader(disWindowBorders));
    nXBorderSize = Integer.parseInt(brWindowsBorders.readLine());
    nYBorderSize = Integer.parseInt(brWindowsBorders.readLine());
  }
}
