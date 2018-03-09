package org.blangflexo.plugin;

import java.util.Arrays;

import org.blangflexo.core.ApiAbstractor;
import org.blangflexo.core.ApiAbstractorException;

public class ApiOperationDispatcher {
	private static enum Operation {
		CREATE_PROJECT,
		CREATE_MACHINE,
		ADD_EVENT,
		ADD_ACTION,
		ADD_GUARD;
		
		public static Operation fromInt(int value) {
			switch (value) {
			case 1:
				return CREATE_PROJECT;
			case 2:
				return CREATE_MACHINE;
			case 3:
				return ADD_EVENT;
			case 4:
				return ADD_ACTION;
			case 5:
				return ADD_GUARD;
					
				default:
					return null;
			}
		}
	}
	
	public static boolean execute(String instruction) {
		try {
			String[] splitted = instruction.split(";");
			Operation operation = Operation.fromInt(Integer.decode(splitted[0]));
			String[] args = Arrays.copyOfRange(splitted, 1, splitted.length);
			
			boolean result = false;
			try {
				result = dispatchOperation(operation, args);
			} catch (ApiAbstractorException ex) {
				result = false;
			}
			
			return result;
		} catch (Exception ex) {
			System.out.println("Could not interpret '" + instruction + "' as a valid instruction");
			return false;
		}
	}
	
	/**
	 * Execute the given operation
	 * @param operation
	 * @param args
	 * @return true if success, false else
	 * @throws ApiAbstractorException
	 */
	public static boolean dispatchOperation(Operation operation, String[] args) throws ApiAbstractorException {
		Object result = null;
		switch (operation) {
			case CREATE_PROJECT:
				result = ApiAbstractor.createRodinProject(args[0]);
				break;
				
			case CREATE_MACHINE:
				result = ApiAbstractor.createMachine(args[0], args[1]);
				break;
				
			case ADD_EVENT:
				result = ApiAbstractor.addEvent(args[0], args[1]);
				break;
				
			case ADD_ACTION:
				result = ApiAbstractor.addAction(args[0], args[1]);
				break;
				
			case ADD_GUARD:
				result = ApiAbstractor.addGuard(args[0], args[1]);
				break;
		}
		
		return result != null;
	}
}
