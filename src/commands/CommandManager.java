package commands;

import java.util.Stack;

/**
 * This class manages the execution, undoing, and redoing of commands.
 * It uses two stacks to keep track of the operation history.
 */
public class CommandManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    /**
     * executes a new command and adds it to the undo history.
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * reverts the most recently executed command.
     * The undone command is then moved to the redo stack so it can be restored if needed.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            if (!undoStack.isEmpty()) {
                Command previousCommand = undoStack.peek();
                previousCommand.execute();
            }
        }
    }
    /**
     * the redone command is moved back to the undo stack.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        }
    }
}