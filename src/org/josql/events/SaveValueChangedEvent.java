package org.josql.events;

import java.util.EventObject;

import org.josql.Query;

public class SaveValueChangedEvent extends EventObject
{

    private Query q = null;
    private String name = null;
    private Object from = null;
    private Object to = null;

    public SaveValueChangedEvent (Query  q,
				  String name,
				  Object from,
				  Object to)
    {

	super (q);

	this.q = q;
	this.name = name;
	this.from = from;
	this.to = to;

    }

    public Object getTo ()
    {

	return this.to;

    }

    public Object getFrom ()
    {

	return this.from;

    }

    public String getName ()
    {

	return this.name;

    }

    public Query getQuery ()
    {

	return this.q;

    }

}