package br.com.caelum.panettone.eclipse.builder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.vraptor.panettone.VRaptorCompiler;

public class Builder {

	private final IProject project;

	public Builder(IProject project) {
		this.project = project;
	}
	
	void full() throws CoreException {
		clear();
		project.accept(new VisitToners(this::compile));
	}

	void incremental(IResourceDelta delta) throws CoreException {
		delta.accept(new DeltaVisitor(this::remove, this::compile));
	}

	private void clear() throws CoreException {
		project.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	private void remove(IFile file) {
		invokeOnCompiler("removeJavaVersionOf", new Class[]{String.class}, file.getFullPath().toPortableString());
	}

	@SuppressWarnings({ "rawtypes", "deprecation", "resource", "unchecked" })
	private Object invokeOnCompiler(String method, Class[] types,
			Object... args) {
		URI projectPath = project.getLocationURI();
		File baseDir = new File(projectPath);
		Optional<IFile> project = findProjectPanettone();
		if(!project.isPresent()) {
			throw new RuntimeException("Unable to find panettone on your src/build/libs.");
		}
		try {
			URL url = project.get().getFullPath().toFile().toURL();
			ClassLoader parent = getClass().getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[]{url}, parent);
			Class<?> type = (Class<VRaptorCompiler>) loader.loadClass("br.com.caelum.vraptor.panettone.VRaptorCompiler");
			Constructor<?> constructor = type.getDeclaredConstructor(File.class, List.class);
			Object compiler = constructor.newInstance(baseDir, new ArrayList<>());
			Method m = type.getDeclaredMethod(method, types);
			return m.invoke(compiler, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Optional<IFile> findProjectPanettone() {
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	private void compile(IFile file) {
		deleteMarkers(file);
		try {
			Optional<Exception> ex = (Optional<Exception>) invokeOnCompiler("compile", new Class[]{File.class}, file.getLocation().toFile()); 
			ex.ifPresent(e -> addCompilationMarker(file, e));
		} catch (Exception e1) {
			addCompilationMarker(file, e1);
		}
	}

	private void addCompilationMarker(IFile file, Exception e) {
		addMarker(file, e.getMessage(), 1, IMarker.SEVERITY_ERROR);
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

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

}
