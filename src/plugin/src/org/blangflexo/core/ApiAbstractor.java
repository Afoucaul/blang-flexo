package org.blangflexo.core;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAction;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IVariable;
import org.eventb.core.basis.Action;
import org.eventb.core.basis.Event;
import org.eventb.core.basis.Guard;
import org.eventb.core.basis.Variable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.RodinFile;
import org.rodinp.internal.core.RodinProject;

public class ApiAbstractor {
	private static boolean 								started 	= false;
	private static IWorkspace 							workspace	= null;
	private static IRodinDB								rodinDB		= null;
	private static HashMap<Pair<Class, String>, Object> environment = null;
	
	
	private static void say(String message) {
		System.out.println(message);
	}	
	
	private static ApiAbstractorException fail(String message, Exception error) {
		System.out.println(message + "\n\tError: " + error);
		return new ApiAbstractorException(error);
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
			System.out.println("===");
			System.out.println(itKey.second + ", " + key.second + ", " + ((Boolean)(itKey.second.equals(key.second))).toString());
			System.out.println(itKey.first + ", " + key.first + ", " + ((Boolean)(itKey.first.equals(key.first))).toString());
			if (itKey.equals(key)) {
				result = environment.get(itKey);
				break;
			}
		}
		
		if (result == null)
			say("Could not find '" + type + "' instance '" + name + "'");
		
		return result;
	}
	
	private static RodinFile getMachine(String name) throws ApiAbstractorException {
		RodinFile machine = (RodinFile) lookupEnvironment(RodinFile.class, name + ".bum");
		if (machine == null)
			throw new ApiAbstractorException("Machine '" + name + "' not found");
		
		return machine;
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
			throw fail("Could not create Rodin project", ex);
		}
	}
	
	public static IRodinFile createMachine(final String projectName, String machineName) throws ApiAbstractorException {
		RodinProject project = (RodinProject) lookupEnvironment(RodinProject.class, projectName);
		if (project == null)
			throw new ApiAbstractorException("Project not found");
		
		machineName += ".bum";
		
		try {
			final IRodinFile rodinFile = project.getRodinFile(machineName);
			rodinFile.create(true, null);
			//rodinFile.getResource().setDerived(true);    // mark the file as derived
			
			insertIntoEnvironment(rodinFile, machineName);
			return rodinFile;
		} catch (final Exception ex) {
			throw fail("Could not create machine", ex);
		}
	}
	
	public static IInternalElement createRodinElement(
			final IInternalElementType type,
			final String name,
			final IInternalElement parent)
		throws ApiAbstractorException
		{
		if (parent == null) return null;
		try {
			final IInternalElement rodinEl = parent.getInternalElement(type, name);
			rodinEl.create(null, null);
			if (rodinEl instanceof ILabeledElement)
				((ILabeledElement) rodinEl).setLabel(name, null);
			if (rodinEl instanceof IIdentifierElement)
				((IIdentifierElement) rodinEl).setIdentifierString(name, null);
			if (rodinEl instanceof IRefinesEvent)
				((IRefinesEvent) rodinEl).setAbstractEventLabel(name, null);
			return rodinEl;
			
		} catch (final Exception ex) {
			throw fail("Could not create event", ex);
		}
	}
	
	public static IEvent addEvent(String machineName, final String eventName) throws ApiAbstractorException {
		RodinFile machine = getMachine(machineName);
		
		Event event = (Event) createRodinElement(IEvent.ELEMENT_TYPE, eventName, machine.getRoot());
		insertIntoEnvironment(event, eventName);
		
		return event;
	}
	
	public static IAction addAction(String eventName, String actionName) throws ApiAbstractorException {
		Event event = (Event) lookupEnvironment(Event.class, eventName);
		if (event == null)
			throw new ApiAbstractorException("Event '" + eventName + "' not found");
		
		Action action = (Action) createRodinElement(IAction.ELEMENT_TYPE, actionName, event);
		insertIntoEnvironment(action, actionName);
		
		return action;
	}
	
	public static IGuard addGuard(String eventName, String guardName) throws ApiAbstractorException {
		Event event = (Event) lookupEnvironment(Event.class, eventName);
		if (event == null)
			throw new ApiAbstractorException("Event '" + eventName + "' not found");
		
		Guard guard = (Guard) createRodinElement(IGuard.ELEMENT_TYPE, guardName, event);
		insertIntoEnvironment(guard, guardName);
		
		return guard;
	}
	
	public static IVariable addVariable(String machineName, String variableName) throws ApiAbstractorException {
		RodinFile machine = getMachine(machineName);
			
		Variable variable = (Variable) createRodinElement(IVariable.ELEMENT_TYPE, variableName, machine.getRoot());
		insertIntoEnvironment(variable, variableName);
		
		return variable;
	}
	
}