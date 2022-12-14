package View;

public enum Command {
    /**
     * List all instruments of specified kind that are not rented.
     */
    LIST,
    /**
     * Command to rent an instrument. It is not possible for a student
     * to rent more than two instruments.
     */
    RENT,
    /**
     * Command to terminate an ongoing rental of an instrument.
     */
    TERMINATE,
    /**
     * Get a list of commands.
     */
    HELP,
    /**
     * Leave the chat application.
     */
    QUIT,
    /**
     * Leave the chat application.
     */
    EXIT,
    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND
}