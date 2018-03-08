package org.blangflexo.core;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.RodinElement;

public class ApiAbstractor {
	private static IWorkspace 	workspace	= null;
	private static IRodinDB		rodinDB		= null;
	private static HashMap<Pair<Class, String>, Object> environment;
	
	
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
	
	private static void insertIntoEnvironment(Object element, String name) {
		Pair<Class, String> key = new Pair(element.getClass(), name);
		System.out.println("Inserting " + element.getClass() + " instance '" + name + "'");
		environment.put(key, element);
	}
	
	private static Object lookupEnvironment(Class type, String name) {
		Pair<String, Class> key = new Pair(type, name);
		return environment.get(key);
	}
	
	public static IRodinProject createRodinProject(final String projectName) throws ApiAbstractorException {
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
			if (!alreadyRodin) {
				final String[] newNatures = new String[natures.length + 1];
				System.arraycopy(natures, 0, newNatures, 0, natures.length);
				newNatures[natures.length] = RodinCore.NATURE_ID;
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
			}
			project.setDerived(true); // ???
			
			insertIntoEnvironment(rodinProject, projectName);
			return rodinProject;
			
		} catch (final CoreException ex) {
			System.out.println("Could not create Rodin project\n\tError:" + ex);
			throw new ApiAbstractorException();
		}
	}
	/*
	public static IRodinFile createRodinConstruct(final String projectName, final String fileName) {
		RodinProject = 
		if (rProject == null)
			return null;
		try {
			final IRodinFile rodinFile = rProject.getRodinFile(filename);
			rodinFile.create(true, null);
			rodinFile.getResource().setDerived(true);    // mark the file as derived
			if (rodinFile instanceof ICommentedElement)
 				if (comment != null && !comment.trim().equals(""))
					((ICommentedElement) rodinFile).setComment(comment, null);
			return rodinFile;
		} catch (final Exception e) {
                       .... exception code ....
		}
	}
	*/
}
