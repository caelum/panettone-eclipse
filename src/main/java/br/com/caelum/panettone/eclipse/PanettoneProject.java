package br.com.caelum.panettone.eclipse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

import br.com.caelum.panettone.eclipse.builder.Builder;

public class PanettoneProject {

	private static final String PANETTONE_JAR = "http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-panettone/1.0.0/vraptor-panettone-1.0.0.jar";
	private static final String VRAPTOR_COMPILER = "br.com.caelum.vraptor.panettone.VRaptorCompiler";
	static final String SRC_BUILD_LIB = "src/build/lib";
	private final IProject project;
	private final ToneMarkers markers = new ToneMarkers();
	private boolean enabled = false;

	public PanettoneProject(IProject project) {
		this.project = project;
		this.enabled = tryToEnable();
	}

	private boolean tryToEnable() {
		try {
			loadOrError(null);
			return true;
		} catch(Exception ex) {
			markers.markAsDisabled(project);
			return false;
		}
	}

	private Class<?> loadType(IProgressMonitor monitor) throws CoreException {
		return loadOrError(monitor);
	}

	@SuppressWarnings("resource")
	private Class<?> loadOrError(IProgressMonitor monitor)
			throws CoreException {
		try {
			Optional<IFile> jar = findProjectPanettone();
			IFile file = extractJar(monitor, jar);
			URL url = file.getLocationURI().toURL();
			ClassLoader parent = Builder.class.getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[] { url },
					parent);
			return (Class<?>) loader.loadClass(VRAPTOR_COMPILER);
		} catch (ClassNotFoundException | IOException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to load panettone jar file!", e));
		}
	}

	private IFile extractJar(IProgressMonitor monitor, Optional<IFile> jar) throws CoreException, IOException {
		if(jar.isPresent()) return jar.get();
		return downloadJarFile(monitor);
	}

	private IFile downloadJarFile(IProgressMonitor monitor) throws CoreException, IOException {
		URL website = new URL(PANETTONE_JAR);
		IFolder folder = project.getFolder(SRC_BUILD_LIB);
		IFile file = folder.getFile("vraptor-panettone-1.0.0.jar");
		if (file.exists()) {
			file.setContents(website.openStream(), IFile.KEEP_HISTORY
					| IFile.FORCE, monitor);
		} else {
			file.create(website.openStream(), IFile.FORCE, monitor);
		}
		return file;
	}

	public String constantValue(String name) throws CoreException {
		try {
			Class<?> type = loadType(null);
			Field field = type.getDeclaredField(name);
			return (String) field.get(null);
		} catch (NoSuchFieldException | CoreException | IllegalArgumentException | IllegalAccessException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to load panettone jar file!", e));
		}
	}

	private Optional<IFile> findProjectPanettone() throws CoreException {
		IFolder folder = project.getFolder(SRC_BUILD_LIB);
		JarFileLocator jarFinder = new JarFileLocator();
		folder.accept(jarFinder);
		return jarFinder.getJar();
	}

	@SuppressWarnings({ "rawtypes" })
	public Object invokeOnCompiler(String method, Class[] types, Object... args) throws CoreException {
		URI projectPath = project.getLocationURI();
		try {
			Class<?> type = loadType(null);
			File baseDir = new File(projectPath);
			Constructor<?> constructor = type.getDeclaredConstructor(
					File.class, List.class);
			Object compiler = constructor.newInstance(baseDir,
					new ArrayList<>());
			Method m = type.getDeclaredMethod(method, types);
			return m.invoke(compiler, args);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to load panettone jar file!", e));
		}
	}

	public String getViewOutput() throws CoreException {
		return constantValue("VIEW_OUTPUT");
	}

	public String getViewInput() throws CoreException {
		return constantValue("VIEW_INPUT");
	}

	public void prepareFolders() throws CoreException {
		prepare(project.getFolder(SRC_BUILD_LIB));
		enabled = this.isEnabled();
		if(enabled) {
			prepare(project.getFolder(getViewOutput()));
			prepare(project.getFolder(getViewInput()));
		}
	}

	public void prepare(IFolder folder) throws CoreException {
		if (folder.exists())
			return;
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder)
			prepare((IFolder) parent);
		folder.create(false, false, null);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void markAsDisabled() {
		markers.markAsDisabled(project);
	}

}
