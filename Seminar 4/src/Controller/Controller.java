package Controller;

import Integration.SoundGoodDAO;
import Integration.SoundGoodDBException;
import Model.InstrumentDTO;
import Model.InstrumentException;

import java.util.List;
import java.util.Random;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {

    private final SoundGoodDAO soundGoodDb;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     *
     * @throws SoundGoodDBException If unable to connect to the database.
     */
    public Controller() throws SoundGoodDBException {
        soundGoodDb = new SoundGoodDAO();
    }

    private void commitOngoingTransaction(String failureMsg) throws InstrumentException {
        try {
            soundGoodDb.commit();
        } catch (SoundGoodDBException e) {
            throw new InstrumentException(failureMsg, e);
        }
    }

    public List<? extends InstrumentDTO> listInstrument(String instrumentType) throws InstrumentException {
        try {
            List<? extends InstrumentDTO> result = (List<? extends InstrumentDTO>) soundGoodDb.listInstruments(instrumentType);
            commitOngoingTransaction("Could not list available instruments.");
            return result;
        } catch (Exception e) {
            throw new InstrumentException("Could not list instruments", e);
        }
    }

    public String rentInstrument(String studentPersonalNumber, String instrumentProductID) throws InstrumentException{
        try{
            int amountOfRentals = soundGoodDb.getAmountOfRentalsByStudent(studentPersonalNumber);
            if(amountOfRentals>=2){
                return "You already have 2 active rentals under your name.";
            }
            //Get database id of student
            int studentDbID = soundGoodDb.getStudentDatabaseID(studentPersonalNumber);
            if(studentDbID<0) return "Student does not exist";

            //Get database id of instrument
            int instrumentDbID = soundGoodDb.getInstrumentDatabaseID(instrumentProductID);
            if(instrumentDbID<0) return "Instrument does not exist";

            //Create receipt
            String receipt = createReceiptID(studentPersonalNumber, instrumentProductID);
            soundGoodDb.rentInstrument(studentDbID, instrumentDbID, receipt);

            commitOngoingTransaction("Could not rent instrument");
        } catch(Exception e){
            throw new InstrumentException("Could not rent an instrument", e);
        }
        return "Success";
    }

    private String createReceiptID(String studentPersonalNumber, String instrumentProductID){
        String date = String.valueOf(java.time.LocalDate.now());
        Random random = new Random();
        int x = random.nextInt();
        return ""+x+date+studentPersonalNumber+instrumentProductID;
    }

}