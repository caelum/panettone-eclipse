package br.com.caelum.panettone.eclipse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

import br.com.caelum.panettone.eclipse.builder.Builder;

public class PanettoneProject {

	private static final String PANETTONE_JAR = "http://central.maven.org/maven2/br/com/caelum/vraptor/vraptor-panettone/1.0.0/vraptor-panettone-1.0.0.jar";
	private static final String VRAPTOR_COMPILER = "br.com.caelum.vraptor.panettone.VRaptorCompiler";
	private static final String SRC_BUILD_LIB = "src/build/lib";
	private final IProject project;

	public PanettoneProject(IProject project) {
		this.project = project;
	}

	@SuppressWarnings("resource")
	private Class<?> loadType(IProgressMonitor monitor) {
		Optional<IFile> jar = findProjectPanettone();
		try {
			IFile file = jar.orElseGet(() -> downloadJarFile(monitor));
			URL url = file.getLocationURI().toURL();
			ClassLoader parent = Builder.class.getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[] { url },
					parent);
			return (Class<?>) loader
					.loadClass(VRAPTOR_COMPILER);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private IFile downloadJarFile(IProgressMonitor monitor) {
		try {
			URL website = new URL(PANETTONE_JAR);
			IFolder folder = project.getFolder(SRC_BUILD_LIB);
			IFile file = folder.getFile("vraptor-panettone-1.0.0.jar");
			if(file.exists()) {
				file.setContents(website.openStream(), IFile.KEEP_HISTORY | IFile.FORCE, monitor);
			} else {
				file.create(website.openStream(), IFile.FORCE, monitor);
			}
			return file;
		} catch (IOException | CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public String constantValue(String name) {
		Class<?> type = loadType(null);
		try {
			Field field = type.getDeclaredField(name);
			return (String) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Optional<IFile> findProjectPanettone() {
		IFolder folder = project.getFolder(SRC_BUILD_LIB);
		try {
			JarFileLocator jarFinder = new JarFileLocator();
			folder.accept(jarFinder);
			return jarFinder.getJar();
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public Object invokeOnCompiler(String method, Class[] types, Object... args) {
		URI projectPath = project.getLocationURI();
		Class<?> type = loadType(null);
		File baseDir = new File(projectPath);
		try {
			Constructor<?> constructor = type.getDeclaredConstructor(
					File.class, List.class);
			Object compiler = constructor.newInstance(baseDir,
					new ArrayList<>());
			Method m = type.getDeclaredMethod(method, types);
			return m.invoke(compiler, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getViewOutput() {
		return constantValue("VIEW_OUTPUT");
	}

	public String getViewInput() {
		return constantValue("VIEW_INPUT");
	}

	public void prepareFolders() throws CoreException {
		prepare(project.getFolder(SRC_BUILD_LIB));
		prepare(project.getFolder(getViewOutput()));
		prepare(project.getFolder(getViewInput()));
	}

	public void prepare(IFolder folder) throws CoreException {
		if (folder.exists())
			return;
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder)
			prepare((IFolder) parent);
		folder.create(false, false, null);
	}

}
