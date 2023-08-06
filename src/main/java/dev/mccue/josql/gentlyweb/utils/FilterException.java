package dev.mccue.josql.gentlyweb.utils;

public class FilterException extends ChainException
{

    public FilterException (String    message,
			    Exception e)
    {
	
	super (message,
	       e);

    }

    public FilterException (String message)
    {

	super (message);

    }

}
