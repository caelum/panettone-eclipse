package br.com.caelum.panettone.eclipse;

import static java.util.Arrays.stream;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

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

	public static final String TONE_OUTPUT = "target/views-classes";
	public static final String TONE_INPUT = "src/main/views";

	public static final String CUTTI_OUTPUT = "target/i18n-classes";
	public static final String CUTTI_INPUT = "src/main/resources";
	
	private static final String PANETTONE_JAR = "http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-panettone/1.0.0/vraptor-panettone-1.0.0.jar";
	private static final String PANETTONE_TYPE = "br.com.caelum.vraptor.panettone.VRaptorCompiler";
	public static final String SRC_BUILD_LIB = "src/build/lib";
	
	private static final String BISCUTTI_JAR = "http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-panettone/1.0.0/vraptor-panettone-1.0.0.jar";
	private static final String BISCUTTI_TYPE = "br.com.caelum.vraptor.biscotti.compiler.Compiler";

	private final IProject project;
	private final ToneMarkers markers = new ToneMarkers();
	private final DynamicLibrary toneCooker;
	private final DynamicLibrary cuttiCooker;

	public PanettoneProject(IProject project) {
		this.project = project;
		this.toneCooker = new DynamicLibrary(project, PANETTONE_JAR, PANETTONE_TYPE);
		this.cuttiCooker = new DynamicLibrary(project, BISCUTTI_JAR, BISCUTTI_TYPE);
	}

	public Object invokeOnTone(String method, Class[] types, Object... args) throws CoreException {
		return invoke(method, types, getTone(), args);
	}

	public Object invokeOnCutti(String method, Class[] types, Object... args) throws CoreException {
		return invoke(method, types, getCutti(), args);
	}

	private Object invoke(String method, Class[] types, Class<?> type,
			Object... args) throws CoreException {
		URI projectPath = project.getLocationURI();
		try {
			File baseDir = new File(projectPath);
			Constructor<?> constructor = type.getDeclaredConstructor(File.class);
			Object compiler = constructor.newInstance(baseDir);
			Method m = type.getDeclaredMethod(method, types);
			return m.invoke(compiler, args);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to load panettone jar file!", e));
		}
	}

	private Class<?> getTone() throws CoreException {
		return toneCooker.loadType(null);
	}

	private Class<?> getCutti() throws CoreException {
		return cuttiCooker.loadType(null);
	}

	public void prepareFolders() {
		try {
			mkDirs(project.getFolder(SRC_BUILD_LIB));
			mkDirs(project.getFolder(CUTTI_INPUT));
			mkDirs(project.getFolder(CUTTI_OUTPUT));
			mkDirs(project.getFolder(TONE_INPUT));
			mkDirs(project.getFolder(TONE_OUTPUT));
			prepareClasspath(TONE_OUTPUT);
			prepareClasspath(CUTTI_OUTPUT);
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
