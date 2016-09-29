package brotherhood.onboardcomputer.ecuCommands;

public class EngineLoadCommand extends Command {

    float engineLoadInPercents = 0;

    public EngineLoadCommand(Pid pid) {
        super(pid);
    }

    @Override
    protected void performCalculations() {
        engineLoadInPercents = (buffer.get(2) * 100) / 255;
    }

    @Override
    public String getFormattedResult() {
        return String.valueOf(engineLoadInPercents);
    }

    @Override
    public String getCalculatedResult() {
        return null;
    }

    @Override
    public String getName() {
        return getPid().getDescription();
    }

    @Override
    protected String getUnit() {
        return getPid().getUnit();
    }
}
