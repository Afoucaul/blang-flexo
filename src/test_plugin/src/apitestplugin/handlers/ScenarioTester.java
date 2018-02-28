package apitestplugin.handlers;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class ScenarioTester {
	
	private RodinHandler m_handler;
	
	public ScenarioTester(RodinHandler handler) {
		m_handler = handler;
	}
	
	public void testProjectCreation() {
		final String projectName = "BrandNewProject";
		m_handler.createRodinProject(projectName);
		try {
			IRodinProject[] projects = RodinCore.getRodinDB().getRodinProjects();
			
			ArrayList<String> projectNames = new ArrayList<String>();
			
			for(IRodinProject project : projects) {
				projectNames.add(project.getProject().getName());
			}
			if (projectNames.contains(projectName))
				System.out.println("Success");
			else
				System.out.println("Failure");
			if (projectNames.contains("SomethingThatIsNotAProject"))
				System.out.println("Success");
			else
				System.out.println("Failure");
		} catch (CoreException e) {
			System.out.println("Error");
		}
	}
}
