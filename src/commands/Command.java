package commands;
//17
/**
 * this interface encapsulates a request or action as an object. It allows the system
 * to parameterize clients with different requests, queue or log requests, and most
 */
public interface Command {
    /**
     * executes the encapsulated command or action.
     * this method contains the actual logic to be performed.
     */
    void execute();
    /**
     * reverses the action performed by the execute() method.
     * this restores the system's state to what it was before execute() was called.
     */
    void undo();
}