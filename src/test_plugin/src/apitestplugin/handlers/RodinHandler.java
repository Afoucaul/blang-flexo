package apitestplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RodinHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		createRodinProject("RodinTestProject");
		
		ScenarioTester tester = new ScenarioTester(this);
		tester.testProjectCreation();
		
		return null;
	}
	
	public String listProjectElements(String projectName) {
		IRodinProject project = RodinCore.getRodinDB().getRodinProject(projectName);
		StringBuilder sb = new StringBuilder();
		try {
			for (IRodinElement element : project.getChildren()) {
				if (element instanceof IRodinFile) {
					IInternalElement root = ((IRodinFile) element).getRoot();
					if (root instanceof IMachineRoot) {
						for (IInvariant invariant : ((IMachineRoot) root).getInvariants()) {
							if (invariant.isTheorem()) {
								sb.append(
										"Théorème : " + invariant.getLabel() + " : " + invariant.getPredicateString() + "\n");
							} else {
								sb.append(
										"Invariant : " + invariant.getLabel() + " : " + invariant.getPredicateString() + "\n");
							}
						}
						for (IEvent event : ((IMachineRoot) root).getEvents()) {
							sb.append("Événement : " + event.getLabel() + "\n");
							for (IGuard garde : event.getGuards()) {
								sb.append("Garde : " + garde.getLabel() + " : " + garde.getPredicateString() + "\n");
							}
						}
					}
				}
			}
			return sb.toString();
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	 public static IRodinProject createRodinProject(final String projectName) {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
			try {
				final IRodinDB rodinDB = RodinCore.valueOf(workspace.getRoot());
				final IRodinProject rodinProject = rodinDB.getRodinProject(projectName);
				final IProject project = rodinProject.getProject();
				if (!project.exists()) project.create(null);
				project.open(null);

				// add the rodin nature if it isn't already there
				final IProjectDescription description = project.getDescription();
				final String[] natures = description.getNatureIds();
				boolean alreadyRodin = false;
				for (int i = 0; i < natures.length; ++i)
					if (RodinCore.NATURE_ID.equals(natures[i])) {
						alreadyRodin = true;
						break;
					}
				if (!alreadyRodin) {     // Add the Rodin nature
					final String[] newNatures = new String[natures.length + 1];
					System.arraycopy(natures, 0, newNatures, 0, natures.length);
					newNatures[natures.length] = RodinCore.NATURE_ID;
					description.setNatureIds(newNatures);
					project.setDescription(description, null);
				}
				project.setDerived(true);  // mark the project as derived (this could be used to distinguish user editable projects
				return rodinProject;
				
			} catch (final CoreException e) {
				System.out.println(e);
				return null;
			}
		}
}
