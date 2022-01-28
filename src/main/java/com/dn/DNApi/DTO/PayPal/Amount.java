package com.dn.DNApi.DTO.PayPal;

public class Amount
{
    private String total;

    private String currency;

    public String getTotal ()
    {
        return total;
    }

    public void setTotal (String total)
    {
        this.total = total;
    }

    public String getCurrency ()
    {
        return currency;
    }

    public void setCurrency (String currency)
    {
        this.currency = currency;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [total = "+total+", currency = "+currency+"]";
    }
}

