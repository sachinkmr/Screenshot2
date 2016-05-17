/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sachin.bws.exceptions;

/**
 *
 * @author Sachin
 */
public class BWSException extends Exception{


	private static final long serialVersionUID = 1L;

	public BWSException() {
    }

    public BWSException(String message) {
        super(message);
    }


}
