package View;

import Controller.Controller;
import Model.InstrumentDTO;

import java.util.List;
import java.util.Scanner;

public class BlockingInterpreter {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private Controller controller;
    private boolean keepReceivingCmds = false;

    /**
     * Creates a new instance that will use the specified controller for all operations.
     *
     * @param ctrl The controller used by this instance.
     */
    public BlockingInterpreter(Controller ctrl) {
        this.controller = ctrl;
    }

    /**
     * Stops the commend interpreter.
     */
    public void stop() {
        keepReceivingCmds = false;
    }

    /**
     * Interprets and performs user commands. This method will not return until the
     * UI has been stopped. The UI is stopped either when the user gives the
     * "quit" command, or when the method <code>stop()</code> is called.
     */
    public void handleCmds() {
        keepReceivingCmds = true;
        while (keepReceivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case HELP:
                        for (Command command : Command.values()) {
                            if (command == Command.ILLEGAL_COMMAND) {
                                continue;
                            }
                            System.out.println(command.toString().toLowerCase());
                        }
                        break;
                    case QUIT:
                        System.out.println("Terminating session...");
                        keepReceivingCmds = false;
                        break;
                    case EXIT:
                        System.out.println("Terminating session...");
                        keepReceivingCmds = false;
                    case LIST:
                        List<? extends InstrumentDTO> instruments = controller.listInstrument(cmdLine.getParameter(0));
                        for (InstrumentDTO instrument: instruments) {
                            System.out.println(instrument);
                        }
                        break;
                    case RENT:
                        //First parameter is personal number. The second is product id of instrument
                        String response = controller.rentInstrument(cmdLine.getParameter(0), cmdLine.getParameter(1));
                        System.out.println(response);
                        break;
                    case TERMINATE:
                        String feedback = controller.endRental(cmdLine.getParameter(0));
                        System.out.println(feedback);
                        break;
                    case ILLEGAL_COMMAND:
                        System.out.println("illegal command");
                        break;
                    default:
                        System.out.println("illegal command");
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String readNextLine() {
        System.out.print(PROMPT);
        return console.nextLine();
    }
}
