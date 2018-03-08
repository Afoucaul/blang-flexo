package org.blangflexo.plugin.handlers;

import org.blangflexo.core.ApiAbstractor;
import org.blangflexo.core.ApiAbstractorException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class BlangFlexoHandler extends AbstractHandler {
	

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!ApiAbstractor.isStarted())
			ApiAbstractor.start();
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		try {
			ApiAbstractor.createRodinProject("hello");
			ApiAbstractor.createRodinConstruct("hello", "machine1.bum");
		} catch (ApiAbstractorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
