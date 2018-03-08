package org.blangflexo.core;

import java.util.HashMap;

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
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.RodinProject;

public class ApiAbstractor {
	private static boolean 								started 	= false;
	private static IWorkspace 							workspace	= null;
	private static IRodinDB								rodinDB		= null;
	private static HashMap<Pair<Class, String>, Object> environment = null;
	
	
	private static void say(String message) {
		System.out.println(message);
	}	
	
	private static void fail(String message, Exception error) {
		System.out.println(message + "\n\tError: " + error);
	}
	
	public static void start() {
		say("Starting API abstractor...");
		
		initWorkspace();
		initRodinDB();
		environment = new HashMap<>();
		
		started = true;
		say("API abstractor started.");
	}
	
	public static boolean isStarted() {
		return started;
	}
	
	private static void dumpEnvironment() {
		say("Environment: {");
		for(Pair<Class, String> key : environment.keySet()) {
			say("\t" + key.second + ": " + key.first);
		}
		say("}");
	}
	
	public static void dumpState() {
		dumpEnvironment();
	}
	
	private static void initWorkspace() {
		workspace = ResourcesPlugin.getWorkspace();
	}
	
	private static void initRodinDB() {
		rodinDB = RodinCore.valueOf(workspace.getRoot());
	}
	
	private static void insertIntoEnvironment(Object element, String name) {
		Pair<Class, String> key = new Pair(element.getClass(), name);
		say("Inserting " + element.getClass() + " instance '" + name + "'");
		environment.put(key, element);
		dumpEnvironment();
	}
	
	private static Object lookupEnvironment(Class type, String name) {
		Pair<Class, String> key = new Pair(type, name);
		say("Performing lookup: " + key.toString());
		
		Object result = null;
		for (Pair itKey : environment.keySet()) {
			if (itKey.equals(key)) {
				result = environment.get(itKey);
				break;
			}
		}
		
		if (result == null)
			say("Could not find " + type + " instance '" + name + "'");
		
		return result;
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
			//project.setDerived(true); // ???
			
			insertIntoEnvironment(rodinProject, projectName);
			return rodinProject;
			
		} catch (final CoreException ex) {
			fail("Could not create Rodin project", ex);
			throw new ApiAbstractorException(ex);
		}
	}
	
	public static IRodinFile createRodinConstruct(final String projectName, final String fileName) throws ApiAbstractorException {
		RodinProject project = (RodinProject) lookupEnvironment(RodinProject.class, projectName);
		if (project == null)
			return null;
		
		try {
			final IRodinFile rodinFile = project.getRodinFile(fileName);
			rodinFile.create(true, null);
			//rodinFile.getResource().setDerived(true);    // mark the file as derived
			
			insertIntoEnvironment(rodinFile, fileName);
			return rodinFile;
		} catch (final Exception ex) {
			fail("Could not create Rodin construct", ex);
			throw new ApiAbstractorException(ex);
		}
	}
	
}
