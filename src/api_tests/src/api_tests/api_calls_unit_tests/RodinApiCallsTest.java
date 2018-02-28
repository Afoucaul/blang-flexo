package api_tests.api_calls_unit_tests;

import junit.framework.*;

import java.util.ArrayList;

import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import api_tests.api_calls.RodinApiCalls;

public class RodinApiCallsTest extends TestCase {	
	public void testCreateRodinProject() throws Exception {
		final String projectName = "TestProject";
		RodinApiCalls.createRodinProject(projectName);
		 
		IRodinProject[] projects = RodinCore.getRodinDB().getRodinProjects();

		ArrayList<String> projectNames = new ArrayList<String>();
		
		
		for(IRodinProject project : projects)
			projectNames.add(project.getProject().getName());

		assertTrue(projectNames.contains(projectName));
		

	}
}
