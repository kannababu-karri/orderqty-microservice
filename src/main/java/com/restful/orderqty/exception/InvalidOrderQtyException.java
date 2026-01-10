package com.restful.orderqty.exception;

public class InvalidOrderQtyException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidOrderQtyException(String message) {
        super(message);
    }
}
