package com.lifeForce.exception;

public class ImageSavingException extends Exception {
	
	public ImageSavingException(Throwable cause)
    {
        super(cause);
    }

    public ImageSavingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ImageSavingException(String message)
    {
        super(message);
    }

    public ImageSavingException()
    {
        super();
    }
}
