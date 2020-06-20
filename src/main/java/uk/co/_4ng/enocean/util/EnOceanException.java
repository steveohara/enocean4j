/*
 *
 * Copyright (c) 2020, 4NG and/or its affiliates. All rights reserved.
 * 4NG PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package uk.co._4ng.enocean.util;

/**
 * Superclass for all specialised exceptions
 */
public class EnOceanException extends Exception {

    /**
     * Constructs a new <tt>EnOceanException</tt> instance.
     */
    public EnOceanException() {
        super();
    }

    /**
     * Constructs a new <tt>EnOceanException</tt> instance with the given
     * message.
     * <p>
     *
     * @param message the message describing this <tt>EnOceanException</tt>.
     */
    public EnOceanException(String message) {
        super(message);
    }

    /**
     * Constructs a new <tt>EnOceanException</tt> instance with the given
     * message.
     * <p>
     *
     * @param message the message describing this <tt>EnOceanException</tt>.
     * @param values optional values of the exception
     */
    public EnOceanException(String message, Object... values) {
        super(String.format(message, values));
    }

    /**
     * Constructs a new <tt>EnOceanException</tt> instance with the given
     * message and underlying cause.
     * <p>
     *
     * @param message the message describing this <tt>EnOceanException</tt>.
     * @param cause   the cause (which is saved for later retrieval by the {@code getCause()} method).
     *                (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public EnOceanException(String message, Throwable cause) {
        super(message, cause);
    }

}
