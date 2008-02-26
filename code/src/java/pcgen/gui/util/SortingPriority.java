package pcgen.gui.util;

public class SortingPriority
{

    private int column;
    private SortMode mode;

    public SortingPriority(int column, SortMode mode)
    {
        super();
        this.column = column;
        this.mode = mode;
    }

    public int getColumn()
    {
        return column;
    }

    public SortMode getMode()
    {
        return mode;
    }
}
