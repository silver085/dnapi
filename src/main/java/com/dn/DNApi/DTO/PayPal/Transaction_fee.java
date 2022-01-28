package com.dn.DNApi.DTO.PayPal;

public class Transaction_fee
{
    private String currency;

    private String value;

    public String getCurrency ()
    {
        return currency;
    }

    public void setCurrency (String currency)
    {
        this.currency = currency;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [currency = "+currency+", value = "+value+"]";
    }
}
