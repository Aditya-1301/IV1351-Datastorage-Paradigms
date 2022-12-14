package Model;
/**
 * InstrumentDTO represents an instrument in the database.
 */
public interface InstrumentDTO {

    public String getType();

    public String getBrand();

    public String getInstrumentId();

    public int getPrice();

    public String toString();
}