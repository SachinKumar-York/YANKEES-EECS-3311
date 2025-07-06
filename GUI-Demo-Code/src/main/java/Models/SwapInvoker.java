package Models;

public class SwapInvoker {
    private SwapCommand command;

    public void setCommand(SwapCommand cmd) {
        this.command = cmd;
    }

    public void run() {
        if (command != null) {
            command.execute();
        }
    }
}
