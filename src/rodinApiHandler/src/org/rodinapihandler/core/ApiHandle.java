package org.rodinapihandler.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.core.internal.content.Activator;
import org.eclipse.core.internal.resources.LocalMetaArea;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ICommentedElement;
import org.eventb.internal.core.EventBProject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.RodinProject;

public class ApiHandle {
	
	private static Framework framework;
	private static BundleContext context;
	
	private static final LinkedHashMap<String, String> jarLocations;
	static {
		jarLocations = new LinkedHashMap<String, String>();

		jarLocations.put(
				"org.eclipse.osgi.services",
				"/home/armand/Programs/rodin/plugins/org.eclipse.osgi.services_3.5.100.v20160504-1419.jar");
		jarLocations.put(
				"com.ibm.icu",
				"/home/armand/Programs/rodin/plugins/com.ibm.icu_56.1.0.v201601250100.jar");
		jarLocations.put(
				"org.eclipse.core.variables",
				"/home/armand/Programs/rodin/plugins/org.eclipse.core.variables_3.3.0.v20160419-1720.jar");
		jarLocations.put(
				"org.eclipse.debug.core",
				"/home/armand/Programs/rodin/plugins/org.eclipse.debug.core_3.10.100.v20160419-1720.jar");
		jarLocations.put(
				"org.eclipse.equinox.app",
				"/home/armand/Programs/rodin/plugins/org.eclipse.equinox.app_1.3.400.v20150715-1528.jar");
		jarLocations.put(
				"org.eclipse.core.contenttype",
				"/home/armand/Programs/rodin/plugins/org.eclipse.core.contenttype_3.5.100.v20160418-1621.jar");
		jarLocations.put(
				"org.eclipse.equinox.preferences",
				"/home/armand/Programs/rodin/plugins/org.eclipse.equinox.preferences_3.6.1.v20160815-1406.jar");
		jarLocations.put(
				"org.eclipse.equinox.registry",
				"/home/armand/Programs/eclipse/software/plugins/org.eclipse.equinox.registry_3.7.0.v20170222-1344.jar");
		jarLocations.put(
				"org.eclipse.core.jobs",
				"/home/armand/Programs/eclipse/software/plugins/org.eclipse.core.jobs_3.9.2.v20171030-1027.jar");
		jarLocations.put(
				"org.eclipse.equinox.common",
				"/home/armand/Programs/eclipse/software/plugins/org.eclipse.equinox.common_3.9.0.v20170207-1454.jar");
		jarLocations.put(
				"org.eclipse.core.runtime",
				"/home/armand/Programs/rodin/plugins/org.eclipse.core.runtime_3.12.0.v20160606-1342.jar");
		jarLocations.put(
				"org.eclipse.core.expressions",
				"/home/armand/Programs/rodin/plugins/org.eclipse.core.expressions_3.5.100.v20160418-1621.jar");
		jarLocations.put(
				"org.eclipse.core.filesystem",
				"/home/armand/Programs/rodin/plugins/org.eclipse.core.filesystem_1.6.1.v20161113-2349.jar");
		jarLocations.put(
				"org.eclipse.core.resources",
				"/home/armand/Programs/rodin/plugins/org.eclipse.core.resources_3.11.1.v20161107-2032.jar");
		jarLocations.put(
				"org.rodinp.core",
				"/home/armand/Programs/rodin/plugins/org.rodinp.core_1.7.0.201704022034-f9fbb0d.jar");
		jarLocations.put(
				"org.eventb.core.seqprover",
				"/home/armand/Programs/rodin/plugins/org.eventb.core.seqprover_3.2.0.201704022034-f9fbb0d.jar");
		jarLocations.put(
				"org.eventb.core.ast",
				"/home/armand/Programs/rodin/plugins/org.eventb.core.ast_3.3.0.201704022034-f9fbb0d.jar");
		jarLocations.put(
				"org.eventb.core",
				"/home/armand/Programs/rodin/plugins/org.eventb.core_3.3.0.201704022034-f9fbb0d.jar");
	}
	
	public static IRodinProject createRodinProject(final String projectName) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			final IRodinDB rodinDB = RodinCore.valueOf(workspace.getRoot());
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
	
	public static IRodinFile createRodinConstruct(final String filename, final IRodinProject rProject, final String comment) {
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
			System.out.println(e);
			return null;
		}
	}
	
	private static void startFramework() throws BundleException {
		FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
		Map<String, String> config = new HashMap<String, String>();
		framework = frameworkFactory.newFramework(config);
		framework.start();
	}
	
	private static void startContext() {
		context = framework.getBundleContext();
	}
	
	// Wichtiger und besser
	public static void startBundles() throws Exception {
		System.out.println("Loading bundles...");
		
		LinkedList<Bundle> bundles = new LinkedList<Bundle>();
		
		for (Map.Entry<String, String> entry : jarLocations.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println("Loading bundle " + key + "...");
			
			Bundle bundle = context.installBundle("file:" + value);
			try {
				bundle.start();
			} catch (Exception e) {
				System.out.println("Could not start bundle '" + bundle + "'\n\tError: " + e);
				throw e;
			}			
		}
		System.out.println("Bundles successfully loaded!");
	}
	
	public static void main(String[] args) throws Exception {
		startFramework();
		startContext();
		startBundles();
		
		InternalPlatform.getDefault().start(context);

		// LocalMetaArea localMetaArea = new LocalMetaArea();
		// System.out.println(ResourcesPlugin.getPlugin());
		
		ResourcesPlugin resourcesPlugin = new ResourcesPlugin();
		resourcesPlugin.start(context);
		
		// Activator activator = new Activator();
		// activator.start(context);
		
		// System.out.println(Activator.getDefault());
		
		
		// Instanciation du workspace
		// Workspace workspace = new Workspace();
	
		
		
		//ResourcesPlugin.getWorkspace();
	}
}
