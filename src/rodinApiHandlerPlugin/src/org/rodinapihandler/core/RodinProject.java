package org.rodinapihandler.core;

import org.rodinp.core.IRodinProject;

public class RodinProject {
	
	private IRodinProject m_rodinProject = null;
	
	public RodinProject(String name) {
		m_rodinProject = RodinApiHandle.createRodinProject(name);
	}
	
	public IRodinProject getProject() {
		return m_rodinProject;
	}
	
	
}
