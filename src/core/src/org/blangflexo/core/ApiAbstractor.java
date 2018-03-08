package org.blangflexo.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class ApiAbstractor {
	private static IWorkspace 	workspace	= null;
	private static IRodinDB		rodinDB		= null;
	
	
	private static void start() {
		initWorkspace();
		initRodinDB();
	}
	
	private static void initWorkspace() {
		workspace = ResourcesPlugin.getWorkspace();
	}
	
	private static void initRodinDB() {
		rodinDB = RodinCore.valueOf(workspace.getRoot());
	}
	
	public static IRodinProject createRodinProject(final String projectName) {
		try {
			final IRodinProject rodinProject = rodinDB.getRodinProject(projectName);
			final IProject project = rodinProject.getProject();
			if (!project.exists())
				project.create(null);
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
			
		} catch (final CoreException ex) {
			System.out.println("Could not create Rodin project\n\tError:" + ex);
			return null;
		}
	}
}
