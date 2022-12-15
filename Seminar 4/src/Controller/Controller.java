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

    /**
     * Retrieves a list from the database with instruments that are available for rental
     * @param instrumentType The type of the instrument. For example 'Guitar'. First letter should be capitalized.
     * @return The list with available instruments.
     * @throws InstrumentException if unable to retrieve the list with instruments
     */
    public List<? extends InstrumentDTO> listInstrument(String instrumentType) throws InstrumentException {
        try {
            List<? extends InstrumentDTO> result = soundGoodDb.readAvailableInstruments(instrumentType);
            commitOngoingTransaction("Could not list available instruments.");
            return result;
        } catch (Exception e) {
            throw new InstrumentException("Could not list instruments", e);
        }
    }

    /**
     * Updates a specified rental in the database with the current date as end date, effectively
     * terminating the rental.
     * @param receiptID The id of the receipt for the rental a.k.a the order id
     * @return A message confirming the termination of the rental
     * @throws InstrumentException if unable to terminate the rental
     */
    public String endRental(String receiptID) throws InstrumentException{
        try{
            soundGoodDb.updateEndRental(receiptID);
            commitOngoingTransaction("Could not end rental");
            return "Successfully closed rental";
        }catch(Exception e){
            throw new InstrumentException("Could not end rental");
        }
    }

    /**
     * Creates a new rental in the database
     * @param studentPersonalNumber The student's personal number
     * @param instrumentProductID The instrument id of the instrument to be rented
     * @return Confirmation message of successful rental
     * @throws InstrumentException if unable to create the rental
     */
    public String rentInstrument(String studentPersonalNumber, String instrumentProductID) throws InstrumentException{
        try{
            //Get database id of student
            int studentDbID = soundGoodDb.readStudentDatabaseId(studentPersonalNumber);
            if(studentDbID<0) return "Student does not exist";

            //Checks the amount of rentals by student
            int amountOfRentals = soundGoodDb.readCurrentAmountOfRentalsHeldByStudent(studentPersonalNumber);
            if(amountOfRentals>=2){
                return "You already have 2 active rentals under your name.";
            }

            //Get database id of instrument
            int instrumentDbID = soundGoodDb.readInstrumentDatabaseID(instrumentProductID);
            if(instrumentDbID<0) return "Instrument does not exist";

            //Create receipt
            String receipt = createReceiptID(studentPersonalNumber, instrumentProductID);
            soundGoodDb.createRentalOfInstrument(studentDbID, instrumentDbID, receipt);

            commitOngoingTransaction("Could not rent instrument");
            return "This is your order number: " +receipt+
                    "\nUse this when returning the instrument";
        } catch(Exception e){
            throw new InstrumentException("Could not rent an instrument", e);
        }
    }

    private String createReceiptID(String studentPersonalNumber, String instrumentProductID){
        String date = String.valueOf(java.time.LocalDate.now());
        Random random = new Random();
        int x = random.nextInt(0, 300000);
        return ""+x+date+studentPersonalNumber+instrumentProductID;
    }

}