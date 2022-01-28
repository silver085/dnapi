package com.dn.DNApi.DTO.PayPal;

import java.util.Arrays;

public class PaypalEventRequest
{
    private String summary;

    private String event_version;

    private String event_type;

    private String create_time;

    private Resource resource;

    private String resource_type;

    private Links[] links;

    private String id;

    public String getSummary ()
    {
        return summary;
    }

    public void setSummary (String summary)
    {
        this.summary = summary;
    }

    public String getEvent_version ()
    {
        return event_version;
    }

    public void setEvent_version (String event_version)
    {
        this.event_version = event_version;
    }

    public String getEvent_type ()
    {
        return event_type;
    }

    public void setEvent_type (String event_type)
    {
        this.event_type = event_type;
    }

    public String getCreate_time ()
    {
        return create_time;
    }

    public void setCreate_time (String create_time)
    {
        this.create_time = create_time;
    }

    public Resource getResource ()
    {
        return resource;
    }

    public void setResource (Resource resource)
    {
        this.resource = resource;
    }

    public String getResource_type ()
    {
        return resource_type;
    }

    public void setResource_type (String resource_type)
    {
        this.resource_type = resource_type;
    }

    public Links[] getLinks ()
    {
        return links;
    }

    public void setLinks (Links[] links)
    {
        this.links = links;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [summary = "+summary+", event_version = "+event_version+", event_type = "+event_type+", create_time = "+create_time+", resource = "+resource+", resource_type = "+resource_type+", links = "+ Arrays.toString(links) +", id = "+id+"]";
    }
}
