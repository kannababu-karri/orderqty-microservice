package com.restful.orderqty.exception;

public class OrderQtyNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OrderQtyNotFoundException(String message) {
        super(message);
    }
}