package Startup;
import View.BlockingInterpreter;
import java.sql.*;

public class Main {
    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
            new BlockingInterpreter(new Controller.Controller()).handleCmds();
        } catch(Exception e) {
            System.out.println("Could not connect to SoundGood");
            e.printStackTrace();
        }
    }
}