package com.dn.DNApi.DTO.PayPal;

public class Resource
{
    private String parent_payment;

    private String update_time;

    private Amount amount;

    private String is_final_capture;

    private String create_time;

    private Transaction_fee transaction_fee;

    private Links[] links;

    private String id;

    private String state;

    private String reasonCode;

    public String getParent_payment ()
    {
        return parent_payment;
    }

    public void setParent_payment (String parent_payment)
    {
        this.parent_payment = parent_payment;
    }

    public String getUpdate_time ()
    {
        return update_time;
    }

    public void setUpdate_time (String update_time)
    {
        this.update_time = update_time;
    }

    public Amount getAmount ()
    {
        return amount;
    }

    public void setAmount (Amount amount)
    {
        this.amount = amount;
    }

    public String getIs_final_capture ()
    {
        return is_final_capture;
    }

    public void setIs_final_capture (String is_final_capture)
    {
        this.is_final_capture = is_final_capture;
    }

    public String getCreate_time ()
    {
        return create_time;
    }

    public void setCreate_time (String create_time)
    {
        this.create_time = create_time;
    }

    public Transaction_fee getTransaction_fee ()
    {
        return transaction_fee;
    }

    public void setTransaction_fee (Transaction_fee transaction_fee)
    {
        this.transaction_fee = transaction_fee;
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

    public String getState ()
    {
        return state;
    }

    public void setState (String state)
    {
        this.state = state;
    }

    public String getReasonCode ()
    {
        return reasonCode;
    }

    public void setReasonCode (String reasonCode)
    {
        this.reasonCode = reasonCode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [parent_payment = "+parent_payment+", update_time = "+update_time+", amount = "+amount+", is_final_capture = "+is_final_capture+", create_time = "+create_time+", transaction_fee = "+transaction_fee+", links = "+links+", id = "+id+", state = "+state+", reasonCode = "+reasonCode+"]";
    }
}