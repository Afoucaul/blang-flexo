package org.rodinapihandler.core;


import javax.rmi.CORBA.Util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;


public class RodinApiHandle {
	private static IWorkspace workspace = null;
	private static IRodinDB rodinDB = null;
	private static boolean isStarted = false; 
	
	
	public static void start() {
		if (!isStarted) {
			workspace = ResourcesPlugin.getWorkspace();
			rodinDB = RodinCore.valueOf(workspace.getRoot());
			
			isStarted = true;
		}
	}
	
	
	public static IRodinDB getRodinDB() {
		return rodinDB;
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
			if (!alreadyRodin) { // Add the Rodin nature
				final String[] newNatures = new String[natures.length + 1];
				System.arraycopy(natures, 0, newNatures, 0, natures.length);
				newNatures[natures.length] = RodinCore.NATURE_ID;
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
			}
			project.setDerived(true); // mark the project as derived (this could be used to distinguish user editable
										// projects
			return rodinProject;

		} catch (final CoreException e) {
			System.out.println(e);
			return null;
		}
	}
	
	
	public static IRodinFile createRodinConstruct(final IRodinProject project, final String filename, final String comment) {
		if (project == null)
			return null;
		
		IRodinFile rodinFile = null;
		try {
			rodinFile = project.getRodinFile(filename);
			rodinFile.create(true, null);
			rodinFile.getResource().setDerived(true);    // mark the file as derived
			if (rodinFile instanceof ICommentedElement)
 				if (comment != null && !comment.trim().equals(""))
					((ICommentedElement) rodinFile).setComment(comment, null);
			
		} catch (final Exception e) {
            System.out.println("Error while creating Rodin construct");
		}
		finally {
			return rodinFile;
		}
	}
}
