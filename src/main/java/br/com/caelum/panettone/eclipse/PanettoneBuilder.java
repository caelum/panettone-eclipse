package br.com.caelum.panettone.eclipse;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import br.com.caelum.vraptor.panettone.VRaptorCompiler;

public class PanettoneBuilder extends IncrementalProjectBuilder {

	class DeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (!(delta.getResource() instanceof IFile))
				return true;
			IFile file = (IFile) delta.getResource();

			switch (delta.getKind()) {
			case IResourceDelta.REMOVED:
				remove(file);
				break;
			case IResourceDelta.ADDED:
			case IResourceDelta.CHANGED:
				compile(file);
				break;
			}
			return true;
		}
	}

	private static final boolean KEEP_VISITING = true;

	class VisitTones implements IResourceVisitor {
		public boolean visit(IResource resource) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				boolean extensionMatch = file.getName().endsWith(".tone") ||
										file.getName().endsWith(".tone.html");
				if(extensionMatch &&
					file.getProjectRelativePath().toString()
							.startsWith("src/main/view"))
				compile(file);
			}
			return KEEP_VISITING;
		}
	}

	public static final String BUILDER_ID = "panettone-eclipse.panettoneBuilder";

	private static final String MARKER_TYPE = "panettone-eclipse.toneProblem";

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		getProject().getFolder("target/view-classes").refreshLocal(
				IResource.DEPTH_INFINITE, monitor);
		return null;
	}

	protected void clear(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	void remove(IFile file) {
		compiler.remove(relativePathTOView(file));
	}
	
	private VRaptorCompiler compiler = new VRaptorCompiler(baseDir,
			Arrays.asList("java.util.*"));

	void compile(IFile file) {
		deleteMarkers(file);
		URI projectPath = file.getProject().getLocationURI();
		File baseDir = new File(projectPath);
		try {
			List<Exception> errors = compiler.compileAndRetrieveErrors();
			for (Exception ex : errors) {
				addMarker(file, ex.getMessage(), 1, IMarker.SEVERITY_ERROR);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		clear(monitor);
		try {
			getProject().accept(new VisitTones());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		delta.accept(new DeltaVisitor());
	}
}
