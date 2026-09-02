package com.murtuza.mdp.md;

public class MarketDataEventType
{
    public static final short ADJUSTMENT = 0;
    public static final short BASE_RATE = 1;
    public static final short SPREAD = 2;
    public static final short NONE = -1;

    private static final String STR_ADJUSTMENT = "adjustment";
    private static final String STR_BASE_RATE = "base_rate";
    private static final String STR_SPREAD = "spread";

    public static short parse(String type)
    {
        if (type.equals(STR_ADJUSTMENT))
        {
            return ADJUSTMENT;
        }
        else if (type.equals(STR_BASE_RATE))
        {
            return BASE_RATE;
        }
        else if (type.equals(STR_SPREAD))
        {
            return SPREAD;
        }
        else
        {
            return NONE;
        }
    }
}
