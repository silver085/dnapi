package com.dn.DNApi.DTO.PayPal;
public class Links
{
    private String method;

    private String rel;

    private String href;

    public String getMethod ()
    {
        return method;
    }

    public void setMethod (String method)
    {
        this.method = method;
    }

    public String getRel ()
    {
        return rel;
    }

    public void setRel (String rel)
    {
        this.rel = rel;
    }

    public String getHref ()
    {
        return href;
    }

    public void setHref (String href)
    {
        this.href = href;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [method = "+method+", rel = "+rel+", href = "+href+"]";
    }
}
