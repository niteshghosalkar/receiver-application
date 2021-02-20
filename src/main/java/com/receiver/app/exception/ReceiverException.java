package com.receiver.app.exception;

public class ReceiverException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReceiverException() {
		super();
	}

	public ReceiverException(final String message) {
		super(message);
	}

	public ReceiverException(final Throwable cause) {
		super(cause);
	}

	public ReceiverException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
