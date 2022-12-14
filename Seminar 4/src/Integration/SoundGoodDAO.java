package Integration;

import Model.Instrument;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class SoundGoodDAO {

    private Connection connection;
    private PreparedStatement listInstruments;
    private PreparedStatement getAmountOfRentalsForStudent;
    private PreparedStatement getDatabaseIDofStudent;
    private PreparedStatement getDatabaseIDofInstrument;
    private PreparedStatement rentInstrument;
    private PreparedStatement terminateRental;

    public SoundGoodDAO() throws SoundGoodDBException {
        try {
            connectToDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundGoodDBException("Could not connect to datasource.", exception);
        }
    }


    private void prepareStatements() throws SQLException{
        listInstruments = connection.prepareStatement("SELECT DISTINCT INSTRUMENT_ID, BRAND, PRICE, INSTRUMENT_TYPE\n" +
                "FROM PHYSICAL_INSTRUMENTS\n" +
                "LEFT JOIN RENTED_INSTRUMENT ON PHYSICAL_INSTRUMENTS.DATABASE_ID = RENTED_INSTRUMENT.INSTRUMENT_DB_ID\n" +
                "WHERE (RENTED_INSTRUMENT.INSTRUMENT_DB_ID IS NULL OR END_DATE IS NOT NULL)\n" +
                "AND INSTRUMENT_TYPE = ?\n" +
                "ORDER BY PRICE");

        getAmountOfRentalsForStudent = connection.prepareStatement("SELECT (COUNT(*)-1) AS \"COUNT\", STUDENT_DB_ID, PERSONAL_NUMBER\n" +
                "FROM RENTED_INSTRUMENT\n" +
                "RIGHT JOIN STUDENT ON RENTED_INSTRUMENT.STUDENT_DB_ID = STUDENT.DATABASE_ID\n" +
                "WHERE PERSONAL_NUMBER = ?\n" +
                "GROUP BY STUDENT_DB_ID, PERSONAL_NUMBER\n" +
                "ORDER BY STUDENT_DB_ID");

        getDatabaseIDofStudent = connection.prepareStatement("SELECT DATABASE_ID FROM STUDENT\n" +
                "WHERE PERSONAL_NUMBER = ?");
        getDatabaseIDofInstrument = connection.prepareStatement("SELECT DATABASE_ID FROM PHYSICAL_INSTRUMENTS\n" +
                "WHERE INSTRUMENT_ID = ?");
        rentInstrument = connection.prepareStatement("INSERT INTO RENTED_INSTRUMENT(STUDENT_DB_ID, INSTRUMENT_DB_ID, START_DATE, RECEIPT_ID)\n" +
                "VALUES (?,?, CURRENT_DATE, ?)");
    }

    private void connectToDB() throws ClassNotFoundException, SQLException {
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/SoundGood","postgres","password");
            connection.setAutoCommit(false);
        }catch(ClassNotFoundException | SQLException exception){
            exception.printStackTrace();
        }
    }

    /**
     * Commits the current transaction.
     *
     * @throws SoundGoodDBException If unable to commit the current transaction.
     */
    public void commit() throws SoundGoodDBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }

    public void handleException(String failureMessage, Exception exception) throws SoundGoodDBException {
        String completeFailureMessage = failureMessage;
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            completeFailureMessage +=
                    ". Also failed to rollback transaction because of: " + rollbackException.getMessage();
        }

        if (exception != null) {
            throw new SoundGoodDBException(failureMessage, exception);
        } else {
            throw new SoundGoodDBException(failureMessage);
        }
    }

    private void closeResultSet(String failureMessage, ResultSet result) throws SoundGoodDBException {
        try {
            result.close();
        } catch (Exception exception) {
            throw new SoundGoodDBException(failureMessage + " Could not close result set.", exception);
        }
    }

    public int getAmountOfRentalsByStudent(String studentPersonalNumber) throws SoundGoodDBException{
        try{
            getAmountOfRentalsForStudent.setString(1,studentPersonalNumber);
            ResultSet rs = getAmountOfRentalsForStudent.executeQuery();

            if(rs.next()){
                return rs.getInt("COUNT");
            }else{
                handleException("Student does not exist", null);
            }
            rs.close();
        }catch(Exception exception){
            handleException("Could not get amount of rentals by student.", exception);
        }
        return -1;
    }

    public int getStudentDatabaseID(String studentPersonalNumber) throws SoundGoodDBException{
        try{
            getDatabaseIDofStudent.setString(1,studentPersonalNumber);
            ResultSet rs = getDatabaseIDofStudent.executeQuery();
            if(rs.next()){
                return rs.getInt("DATABASE_ID");
            }else{
                handleException("Student does not exist", null);
            }
            rs.close();
        }catch(Exception exception){
            handleException("Could not retrieve database id of student", exception);
        }
        return -1;
    }

    public int getInstrumentDatabaseID(String instrumentProductID) throws SoundGoodDBException{
        try{
            getDatabaseIDofInstrument.setString(1,instrumentProductID);
            ResultSet rs = getDatabaseIDofInstrument.executeQuery();
            if(rs.next()){
                return rs.getInt("DATABASE_ID");
            }else{
                handleException("Instrument does not exist", null);
            }
            rs.close();
        }catch(Exception exception){
            handleException("Could not retrieve database id of student.", exception);
        }
        return -1;
    }

    public void rentInstrument(int studentDbID, int instrumentDbID, String receiptID) throws SoundGoodDBException{
        try{
            rentInstrument.setInt(1, studentDbID);
            rentInstrument.setInt(2,instrumentDbID);
            rentInstrument.setString(3,receiptID);
            rentInstrument.executeUpdate();
        }catch(Exception exception){
            handleException("Could not rent instrument.", exception);
        }
    }

    public List<Instrument> listInstruments(String type) throws SoundGoodDBException {

        List<Instrument> instruments = null;
        try{
            listInstruments.setString(1, type);
            ResultSet rs = listInstruments.executeQuery();
            instruments = new ArrayList<Instrument>();
            while (rs.next()) {
                instruments.add(new Instrument(
                        rs.getString("INSTRUMENT_ID"),
                        rs.getString("INSTRUMENT_TYPE"),
                        rs.getString("BRAND"),
                        rs.getInt("PRICE")
                ));
            }
            rs.close();
        }catch(Exception exception){
            handleException("Could not list instruments.", exception);
        }
        return instruments;
    }
}