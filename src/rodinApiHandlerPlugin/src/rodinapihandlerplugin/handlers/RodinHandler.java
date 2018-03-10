package rodinapihandlerplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.rodinapihandler.core.EventBMachine;
import org.rodinapihandler.core.RodinApiHandle;
import org.rodinapihandler.core.RodinProject;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RodinHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		RodinApiHandle.start();
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		RodinProject project = new RodinProject("MyBrandNewProject");
		EventBMachine machine = new EventBMachine(project, "Machine1");
		
		return null;
	}
	
	
}
