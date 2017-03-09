package com.azeesoft.libs.httprequester.core;

/**
 * Created by Aziz on 4/19/2015.
 */
public class AZHTTPFormException extends RuntimeException {

    int id=-1;
    String s="Form Exception";

    public AZHTTPFormException()
    {

    }

    public AZHTTPFormException(int id)
    {
        this.id=id;
    }

    public AZHTTPFormException(String msg)
    {

    }

    public AZHTTPFormException(int id, String msg)
    {

    }

    public int getid()
    {
        return id;
    }

}
