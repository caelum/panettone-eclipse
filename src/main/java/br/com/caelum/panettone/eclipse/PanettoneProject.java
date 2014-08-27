package br.com.caelum.panettone.eclipse;

import static java.util.Arrays.stream;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import br.com.caelum.panettone.eclipse.loader.DynamicLibrary;

@SuppressWarnings({ "rawtypes" })
public class PanettoneProject {

	public static final String TONE_OUTPUT = "target/view-classes";
	public static final String TONE_INPUT = "src/main/views";

	public static final String COTTI_OUTPUT = "target/i18n-classes";
	public static final String COTTI_INPUT = "src/main/resources";
	
	private static final String PANETTONE_JAR = "http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-panettone/1.0.0/vraptor-panettone-1.0.0.jar";
	private static final String PANETTONE_TYPE = "br.com.caelum.vraptor.panettone.VRaptorCompiler";
	public static final String SRC_BUILD_LIB = "src/build/lib";
	
	private static final String BISCOTTI_JAR = "http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-biscotti/1.0.0/vraptor-biscotti-1.0.0.jar";
	private static final String BISCOTTI_TYPE = "br.com.caelum.vraptor.biscotti.compiler.Compiler";

	private final IProject project;
	private final ToneMarkers markers = new ToneMarkers();
	private final DynamicLibrary toneCooker;
	private final DynamicLibrary cottiCooker;

	public PanettoneProject(IProject project) {
		this.project = project;
		this.toneCooker = new DynamicLibrary(project, PANETTONE_JAR, PANETTONE_TYPE);
		this.cottiCooker = new DynamicLibrary(project, BISCOTTI_JAR, BISCOTTI_TYPE);
	}

	public Object invokeOnTone(String method, Class[] types, Object... args) throws CoreException {
		return invoke(method, types, getTone(), args);
	}

	public Object invokeOnCotti(String method, Class[] types, Object... args) throws CoreException {
		return invoke(method, types, getCotti(), args);
	}

	private Object invoke(String method, Class[] types, Class<?> type,
			Object... args) throws CoreException {
		URI projectPath = project.getLocationURI();
		try {
			Object compiler = getCompiler(type, projectPath);
			Method m = type.getDeclaredMethod(method, types);
			return m.invoke(compiler, args);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to load compatible jar file!", e));
		}
	}

	private Object getCompiler(Class<?> type, URI projectPath)
			throws InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		File baseDir = new File(projectPath);
		try {
			Constructor<?> constructor= type.getDeclaredConstructor(File.class);
			return  constructor.newInstance(baseDir);
		} catch (NoSuchMethodException e) {
			Constructor<?> constructor = type.getDeclaredConstructor(File.class, List.class);
			return  constructor.newInstance(baseDir, new ArrayList<>());
		}
	}

	private Class<?> getTone() throws CoreException {
		return toneCooker.loadType(null);
	}

	private Class<?> getCotti() throws CoreException {
		return cottiCooker.loadType(null);
	}

	public void prepareFolders() {
		try {
			mkDirs(project.getFolder(SRC_BUILD_LIB));
			mkDirs(project.getFolder(COTTI_INPUT));
			mkDirs(project.getFolder(COTTI_OUTPUT));
			mkDirs(project.getFolder(TONE_INPUT));
			mkDirs(project.getFolder(TONE_OUTPUT));
			prepareClasspath(TONE_OUTPUT);
			prepareClasspath(COTTI_OUTPUT);
			refresh(null);
		} catch (Exception e) {
			markAsDisabled();
		}
	}

	private void prepareClasspath(String folder) throws JavaModelException {
		IJavaProject java = JavaCore.create(project);
		IPath srcPath = java.getPath().append(folder);
		addToClasspath(java, srcPath);
	}

	public void mkDirs(IFolder folder) throws CoreException {
		if (folder.exists())
			return;
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder)
			mkDirs((IFolder) parent);
		folder.create(false, false, null);
	}

	public void markAsDisabled() {
		markers.markAsDisabled(project);
	}

	public void refresh(IProgressMonitor monitor) throws CoreException {
		project.getFolder(TONE_OUTPUT).refreshLocal(DEPTH_INFINITE, monitor);
		project.getFolder(COTTI_OUTPUT).refreshLocal(DEPTH_INFINITE, monitor);
	}

	private void addToClasspath(IJavaProject java, IPath srcPath)
			throws JavaModelException {
		IClasspathEntry[] entries = java.getRawClasspath();
		boolean isPresent = stream(entries)
								.map(e -> e.getPath())
								.anyMatch(srcPath::equals);
		if(isPresent) return;
		
		IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
		System.arraycopy(entries, 0, newEntries, 0, entries.length);
		IClasspathEntry srcEntry= JavaCore.newSourceEntry(srcPath, null);
		newEntries[entries.length] = JavaCore.newSourceEntry(srcEntry.getPath());
		java.setRawClasspath(newEntries, null);
	}

}
