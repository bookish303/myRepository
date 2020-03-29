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
public class RunUpdater {
/*this was written to use the LogFactionPersonalStat class to acces and update 
    the stats of members of selected factions every time it is initialized
    (to see if it made a difference to previous method of calling other method))*/
    public RunUpdater(String FactionIDpath) throws IOException {
        LogFactionPersonalStat s = new LogFactionPersonalStat();
        try {
            s.initialization(FactionIDpath);//get factions we wish to update
        } catch (IOException ex) {
            Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, "Error: Problems with a file");
        }
        int waitTime = 3600;  //time period we want between 2 consecutive updates (in seconds)
        try {
            if (!s.isCompleted()) {//if the previous run was interrupted due to exception
                try {
                    s.getFactionMembers(); //continue stats update
                } catch (IOException ex) {
                    Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, "Error: Possible timeout or problems with a file");
                } catch (InterruptedException ex) {
                    Logger.getLogger(OtherFactionsStats.class.getName()).log(Level.SEVERE, "Error: problem with timer");
                }
            } else {//if the run had completed and all members had been updated
                long currentTimestamp = new Date().getTime() / 1000L;
                try {
                    if (currentTimestamp - s.getStartTimestamp() < waitTime) {//we check if enough time has passed to start the new run
                        try {//if not we "sleep" until it is time then run the member update
                            System.out.println("Update will begin in " + (waitTime - currentTimestamp + s.getStartTimestamp())/60  + " minutes. Generated at " + new java.text.SimpleDateFormat("dd-MMMMM, HH:mm:ss").format(new Date()) );
                            TimeUnit.SECONDS.sleep(waitTime - currentTimestamp + s.getStartTimestamp());
                        } catch (InterruptedException ex) {
                            Logger.getLogger("Error: problem with timer");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger("Error reading from file");
                }
                try {
                    s.getFactionMembers(); //get member update
                } catch (IOException ex) {
                    Logger.getLogger("Error: Possible timeout or problems with file");
                } catch (InterruptedException ex) {
                    Logger.getLogger("Error: problem with timer");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger("Error: Problems with file");
        }
    }
}
