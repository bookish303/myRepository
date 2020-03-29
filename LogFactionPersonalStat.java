/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalstatsfactions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/**
 * this class was created as a means of accessing the torn API website and retrieve
 * certain information. we select certain factions, and we write the data about 
 * the members in a file.. this data is updated every while, and the data collected 
 * is used in other projects
 */
public class LogFactionPersonalStat {
    private String originalPath = "C:\\Users\\asusd\\Documents\\NetBeansProjects\\UpdatedFactionLogs\\Logged Data\\"; //path where all logs are stored, only the folder..file name will be added later
    private String path;
    private String idPath;  //for storing path containing IDs of factions we want to check
    private ArrayList<String> factionPath = new ArrayList<>();  //to store the faction IDs
    private String IDsFileName = "memberIDs.txt";  //file name of member IDs
    private String inCaseOfCrash = "last checked member.txt" ; //file name for storing current member being checked
    private String timestamp = "startTimestamp.txt";  //to store start of stat logger, in case it keeps crashing 
    private long startTimestamp = 0;     //becomes true when we have started a run for logging personal stats
    private String currentFaction = "currentFaction.txt";
    private String completed = "completed.txt";
   
    
    public void  initialization (String pathFileOfIDs) throws FileNotFoundException, IOException { // initialization, creates necessary folders 
        idPath = pathFileOfIDs;
        path = originalPath.concat("Personal Stats"); //for the folder that will contain all the personal stats of all factions
        File file = new File(path);                           // for creating the folder
        boolean dirCreated;                  // boolean to check if base directory exists
        dirCreated = file.exists();                  // boolean to check if folder exists
        if( !dirCreated ){
            dirCreated = file.mkdir();                      //creates folder if non existent
        }
        if (!file.exists()){
            System.out.println("Error creating folder");
        }
        path = path.concat("\\");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(idPath), StandardCharsets.UTF_8)); //buffer reads all members from file at once
        String line; //to read from buffer line by line
        factionPath.clear();
        while ((line = in.readLine()) != null && line.length()>0) {
            factionPath.add(line); 
        }
        in.close();
        for (String oneID : factionPath){
            file = new File("" + path + oneID);                           // for creating the folder
            dirCreated = file.exists();                  // to check if folder exists
            if( !dirCreated ){
                dirCreated = file.mkdir();                      //creates folder if non existent
            }
            if (!file.exists()){
                System.out.println("Error creating folder");
            }
        }
        File createMemberFiles = new File ("" + path + currentFaction);
        FileWriter files;
        if(createMemberFiles.createNewFile()){
            files = new FileWriter("" + path + currentFaction); // create file that keeps id of last member checked (in case of crash)
            files.write("0"); //zero means there was no crash
            files.close();
        }
        createMemberFiles = new File ("" + path + completed);
        if (createMemberFiles.createNewFile()){
            files = new FileWriter("" + path + completed); // create file that tells us if complete: 0 means incomplete and 1 means complete
            files.write("0"); //zero means not yet complete
            files.close();
        }
    }
    
    public void getMemberIDs(String factionID) throws IOException { //gets member IDs of the inputted faction from the API and stores them in file 
        TornAPI api = new TornAPI();
        JSONObject logs = api.connect(/*removed here*/"", "faction", factionID, "basic");  //the basic news gives all the member's IDs in the json object
        FileWriter file;
        File createMemberFiles; //for creating an empty file for each of the members (prevent error in the next function)
        if (logs.has("members")) {          //Check to see if it exists first
            FileWriter file2 = new FileWriter("" + path + factionID + "\\allMembersData.txt"); // path for data about all members: their names, ids, etc(json object)
            file2.write(logs.toString());
            file2.flush();
            file2.close();
            JSONObject members = logs.getJSONObject("members"); //Put members into JSON Object
            Iterator<String> keys = members.keys(); //Use iterator to go over keys in JSONObject
            file = new FileWriter("" + path + factionID + "\\" + IDsFileName); // path for IDs
            while (keys.hasNext()) {    //Iterate over keys in members (since JSONObject containing objects, and not JSONArray)
                String key = (String) keys.next(); // First key in your json object is member ID
                file.write("" + key + "\n"); // write an ID on each line 
                createMemberFiles = new File ("" + path + factionID + "\\" + key + ".txt"); //if the file for the member does not exist, create it
                createMemberFiles.createNewFile();
            }
            file.flush();
            file.close();
        }
        
        createMemberFiles = new File ("" + path + factionID + "\\" + inCaseOfCrash); //this file will store the id of the member whose data we will get next, in cas the run is interrupted
        if (createMemberFiles.createNewFile()){
            file = new FileWriter("" + path + factionID + "\\" + inCaseOfCrash);
            file.write("0"); //zero means there was no crash
            file.close();
        }
        
    }
    
    public void updateStats(String factionID) throws IOException, InterruptedException{// this will update the stats of the members in the selected faction
        ArrayList<String> allMemberIDs = new ArrayList<>(); //to read all member IDs from file into
        String crashMember;   //to use for API
        JSONObject userStats;   //to get the stats of each member from API
        TornAPI api = new TornAPI(); //for the API call
        BufferedWriter writer; //for writing
        
        FileWriter file = new FileWriter("" + path + completed); //set completion status to incomplete
        file.write("0"); //zero means not yet complete
        file.close();
        
        int current = 0;  //counter for going through the members
        
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream("" + path + factionID + "\\" + IDsFileName), StandardCharsets.UTF_8)); //buffer reads all members from file at once
        String line; //to read from buffer line by line
        while ((line = in.readLine()) != null && line.length()>0) {
            allMemberIDs.add(line);  //read all members into array
        }
        in.close();  //remember to close file
        in = new BufferedReader(
                new InputStreamReader(new FileInputStream("" + path + factionID + "\\" + inCaseOfCrash), StandardCharsets.UTF_8));
        crashMember = (line = in.readLine()); //to check in case the program crashed while running, and continue where it left off.
        in.close();  //remember to close file
        if (crashMember == null){//only happens if program terminated unexpectedly
            System.out.println("Current member file was empty. Why??"); // for reference in case error occured
            crashMember = "0";//default
        }
        if (!crashMember.equals("0") ){ //this means the program has crashed and we have a member in the file, so we will find its location to continue
            while (!crashMember.equals(allMemberIDs.get(current)) && current < allMemberIDs.size()){ //find where we had reached in the file before crashing
                current++; //as long as we havent found a match the loop keeps searching in the elements of the array
            }
            userStats = api.connect(/*removed here*/"", "user", allMemberIDs.get(current), "personalstats").getJSONObject("personalstats"); // we have to get data from the crash member to see if it was written or not
            in = new BufferedReader(
                    new InputStreamReader(new FileInputStream("" + path + factionID + "\\" + allMemberIDs.get(current) + ".txt"), StandardCharsets.UTF_8)); //we open the crashed member's file
            while ((line = in.readLine()) != null && line.length()>0) {                                                          //and get the last line
                crashMember = line; //at the end of this loop this string will contain last written stats of crash member
            }
            if( crashMember.contains("\"timestamp\":")){   //start of numbers for timestamp
                int startOftimestamp = crashMember.indexOf("\"timestamp\":"); 
                crashMember = crashMember.replace(crashMember.subSequence(startOftimestamp, startOftimestamp + 23), ""); //we add the timestamp for reference, so we remove it to compare last recorded line with new one
                if (crashMember.equals(userStats.toString())){  //if the last line in the file matches the line read now that means the data was written before the crash
                    current++;                                  //so we do not write the data and move on to the next member
                    writer = new BufferedWriter(new FileWriter(path + factionID + "\\" + inCaseOfCrash));   // and write that new id to the crash file for reference
                    writer.write(allMemberIDs.get(current));
                    writer.close();
                }
                else{
                    writer = new BufferedWriter(new FileWriter(path + factionID + "\\" + allMemberIDs.get(current) + ".txt", true)); //if the called stats were not written in the file before crashing we write them
                    userStats.put("timestamp", new Date().getTime() / 1000L);
                    writer.append("" + userStats.toString() );
                    writer.append("\n");
                    writer.close();
                    current++;
                
                }
            }
        }
        else { //if the previous update had completed
            if (factionID.compareTo(factionPath.get(0)) == 0){ //if the current faction whose members we are checking is the very top of the list, it means we just began the update, therefore we reset the start time
                startTimestamp = new Date().getTime() / 1000L;  //set the time we began logging the stats
                writer = new BufferedWriter(new FileWriter(path + timestamp));   // and write that id to the faction crash file for reference
                writer.write(Long.toString(startTimestamp));
                writer.close();
            }
        }
        while (current < allMemberIDs.size() && !allMemberIDs.get(current).isEmpty()){ //after checking for crashes we begin writing stats for all members
            TimeUnit.SECONDS.sleep(5);
            writer = new BufferedWriter(new FileWriter(path + factionID + "\\" + inCaseOfCrash)); //first we update the crash file
            writer.write(allMemberIDs.get(current));
            writer.close();
                /////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////
            userStats = api.connect(/*removed here*/"", "user", allMemberIDs.get(current), "personalstats").getJSONObject("personalstats"); //then we get the current member's stats
            ////////////////////////////////////////////////////////////
               //////////////////////////////////////////////////////
            writer = new BufferedWriter(new FileWriter(path + factionID + "\\" + allMemberIDs.get(current) + ".txt", true)); //and append them to the file
            userStats.put("timestamp", new Date().getTime() / 1000L);
            writer.append("" + userStats.toString());
            writer.append("\n");
            writer.close();
            current++;
        }
        System.out.print("\n");///////////////////
        //once we have finished all the members, we update the crash file to contain zero again so that in the next run we know there was no crash
        writer = new BufferedWriter(new FileWriter(path + factionID + "\\" + inCaseOfCrash)); //first we update the crash file
        writer.write("0");
        writer.close();
    }
    public boolean isCompleted() throws FileNotFoundException, IOException{ //checks to see if the last update was completed or not
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream("" + path + completed), StandardCharsets.UTF_8));
        String num =  in.readLine(); //to check in case the program crashed while running, and continue where it left off.
        in.close();  
        if (num.equals("1")){
        return true;
        }
        return false;
    }
    public long getStartTimestamp() throws FileNotFoundException, IOException{ //gets the start time of the last update for reference
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream("" + path + timestamp), StandardCharsets.UTF_8));
         String x = in.readLine();
         in.close(); 
        return Long.parseLong(x);
    }
    
    public void getFactionMembers() throws FileNotFoundException, IOException, InterruptedException{ //this is the ,ethod that updates the stats of the members of all the factions
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream("" + path + currentFaction), StandardCharsets.UTF_8)); 
        String currentFactionID =  in.readLine(); //to check in case the program crashed while running, and continue where it left off.
        in.close();  //remember to close file
        BufferedWriter writer;
        System.out.println("Stats update in progress. Generated  " + new java.text.SimpleDateFormat("dd-MMMMM, HH:mm:ss").format(new Date()));
        if (currentFactionID.equals("0")){
            for (String oneID : factionPath){
                writer = new BufferedWriter(new FileWriter("" + path +  currentFaction));   //  write that id to the faction file for reference
                writer.write(oneID);
                writer.close();
                getMemberIDs(oneID);
                updateStats(oneID);  
            }
        }
        else {
            int current =  0;
            while (!currentFactionID.equals(factionPath.get(current)) && current < factionPath.size()){ //find what faction we had reached in the file before crashing
                current++; //as long as we havent found a match the loop keeps searching in the elements of the array
            }
            while (current < factionPath.size()){
                writer = new BufferedWriter(new FileWriter("" + path +  currentFaction));   //  write that id to the faction file for reference
                writer.write(factionPath.get(current));
                writer.close();
                getMemberIDs(factionPath.get(current));
                updateStats(factionPath.get(current));
                current++;
            }
        }
        FileWriter files = new FileWriter("" + path + currentFaction); // create file that keeps id of last member checked (in case of crash)
        files.write("0"); //zero means there was no crash
        files.close();
        System.out.println("Run completed at: " + new Date());
        FileWriter file = new FileWriter("" + path + completed); //set completion status to incomplete
        file.write("1"); //zero means not yet complete
        file.close();
    }
    public String readFromLast(String filePath){ //currently not used
        /*
        this file reads the last line of a file instead of reading the entire file.
        It becomes useful when the size of the file is increasing and we omly need the last line.
        remeber that the last character of the line is not returened, so when comparing strings compare substrings with the same length.
        the missing character for JSON objects will usually be a } or a new line, so we will not lose and data
        */
        File checkFile = new File(filePath);
        StringBuilder builder = new StringBuilder();  //to take charaters and transform them to string
        RandomAccessFile randomAccessFile = null;       //to access file from where ever we choose (specified by characters)
        try {
         randomAccessFile = new RandomAccessFile(checkFile, "r");
         long fileLength = checkFile.length() - 1;   //we will start from the end of the file
         // Set the pointer at the last of the file
         randomAccessFile.seek(fileLength);
        
         char c;   //to read from file character by character
         int counter = 0;
         while((c = (char)randomAccessFile.read()) == '\n' ){
             counter++;
         }//last chars are \n and we doont want them
         counter++;
         builder.append(c);   //sometimes this character is misread, thats why we remove it later
         for(long pointer = fileLength - counter; pointer >= 0; pointer--){
           randomAccessFile.seek(pointer);
           // read from the last one char at the time
           c = (char)randomAccessFile.read(); 
           // break when end of the line
           if(c == '\n'){
              break;
           }
           builder.append(c);
         }
         // Since line is read from the last so it 
         // is in reverse so use reverse method to make it right
         builder.reverse();
         System.out.println("Line - " + builder.toString().substring(0, builder.length() - 1));
        } catch (FileNotFoundException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
        }
        catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }finally{
           if(randomAccessFile != null){
              try {
                 randomAccessFile.close();
              } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
              }
           }
       }
        return builder.toString().substring(0, builder.length() - 1); //return the string without the last character
  }
}
