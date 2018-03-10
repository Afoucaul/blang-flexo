package org.rodinapihandler.core;

import org.rodinp.core.IRodinFile;

public class EventBMachine {
	private RodinProject 	m_project 	= null;
	private IRodinFile 		m_file		= null;
	
	public EventBMachine(RodinProject project, String name) {
		m_project = project;
		m_file = RodinApiHandle.createRodinConstruct(project.getProject(), name + ".bum", "This is a comment");
	}
}
