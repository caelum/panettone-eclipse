package br.com.caelum.panettone.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class ToneMarkers {

	public void addCompilationMarker(IFile file, Exception e) {
		addMarker(file, e.getMessage(), 1, IMarker.SEVERITY_ERROR);
	}

	public void removeMarkersFor(IFile file) {
		try {
			file.deleteMarkers(TONE_PROBLEM, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	private static final String TONE_PROBLEM = "panettone-eclipse.toneProblem";

	void add(IProject project, String message, int severity) {
		try {
			IMarker marker = project.createMarker(TONE_PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.LINE_NUMBER, 1);
		} catch (CoreException e) {
		}
	}
	void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(TONE_PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	public void clear(IProject project) {
		try {
			project.deleteMarkers(TONE_PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
		}
	}

	public void markAsDisabled(IProject project) {
		clear(project);
		add(project, "Unable to find panettone at " + PanettoneProject.SRC_BUILD_LIB, IMarker.SEVERITY_ERROR);
	}

}
