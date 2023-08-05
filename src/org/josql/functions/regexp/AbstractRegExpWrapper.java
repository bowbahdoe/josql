package org.josql.functions.regexp;

import org.josql.*;

public abstract class AbstractRegExpWrapper 
{

    public abstract boolean isAvailable (Query q);

    public abstract String getSupportedVersion ();

}
