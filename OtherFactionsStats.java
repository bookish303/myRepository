/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package personalstatsfactions;



import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author Asus Desktop
 */
public class OtherFactionsStats {

    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) throws IOException {
        // TODO code application logic here
       String FactionIDpath = "C:\\Users\\asusd\\Documents\\NetBeansProjects\\UpdatedFactionLogs\\Logged Data\\Personal Stats\\FactionIDs.txt";
       LogFactionPersonalStat s = new LogFactionPersonalStat();
       s.initialization(FactionIDpath);
       s.getStartTimestamp();
       long thisTimestamp = new Date().getTime() / 1000L;
       int waitTime = 3600;  //we want 1 hour between runs (in seconds)
        if (thisTimestamp - s.getStartTimestamp() < waitTime && s.isCompleted()) {
            try {
                System.out.println("Update will begin in " + (3600 - thisTimestamp + s.getStartTimestamp() )/60 + " minutes. Generated  " + new java.text.SimpleDateFormat("dd-MMMMM, HH:mm:ss").format(new Date()));
                TimeUnit.SECONDS.sleep(waitTime - thisTimestamp + s.getStartTimestamp());
            } catch (InterruptedException ex) {
                Logger.getLogger("Error: problem with timer");
                //Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
       System.out.println("Program now running");
       Runnable runnable = new Runnable() {
            public void run() {
               try {
                   System.out.flush();
                   s.initialization(FactionIDpath);
               } catch (IOException ex) {
                    Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, "Error initializing variables");
                  //Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
               }
               try {
                   if(!s.isCompleted()){
                       try {
                           s.getFactionMembers();
                       } catch (IOException ex) {
                           Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, "Error: Possible timeout or problems with a file");
                           // Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
                       } catch (InterruptedException ex) {
                           Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, "Error: problem with timer");
                           // Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
                       }
                   }
                   else{
                       long currentTimestamp = new Date().getTime() / 1000L;
                       try {
                           int waitTime  = 3600;  //we want 1 hour between runs (in seconds)
                           if (currentTimestamp - s.getStartTimestamp() <  waitTime){
                               try {
                                   TimeUnit.SECONDS.sleep(waitTime - currentTimestamp + s.getStartTimestamp());
                               } catch (InterruptedException ex) {
                                   Logger.getLogger( "Error: problem with timer");
                                   //Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
                               }
                           }
                       } catch (IOException ex) {
                           Logger.getLogger("Error reading from file");
                           //Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
                       }
                       try {
                           s.getFactionMembers();
                       } catch (IOException ex) {
                           Logger.getLogger("Error: Possible timeout or problems with file");
                           // Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
                       } catch (InterruptedException ex) {
                           Logger.getLogger( "Error: problem with timer");
                           // Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
                       }
                   }
               } catch (IOException ex) {
                   Logger.getLogger( "Error: Problems with file");
                   //Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
               }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);
    }*/
    public static void main(String[] args){
        String FactionIDpath = "C:\\Users\\asusd\\Documents\\NetBeansProjects\\UpdatedFactionLogs\\Logged Data\\Personal Stats\\FactionIDs.txt";
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    System.out.println("Program now running");
                    System.out.flush();
                    new RunUpdater(FactionIDpath);
                } catch (IOException ex) {
                    Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, "Error: Problems with a file(main)");
                    //Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);
    }
}
