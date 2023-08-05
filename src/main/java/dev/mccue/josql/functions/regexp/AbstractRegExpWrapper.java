package dev.mccue.josql.functions.regexp;

import dev.mccue.josql.Query;

public abstract class AbstractRegExpWrapper 
{

    public abstract boolean isAvailable (Query q);

    public abstract String getSupportedVersion ();

}
