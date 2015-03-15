package br.com.caelum.panettone.eclipse.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

import br.com.caelum.panettone.eclipse.Activator;
import br.com.caelum.panettone.eclipse.VRaptorProject;
import br.com.caelum.panettone.eclipse.ToneMarkers;
import br.com.caelum.panettone.eclipse.builder.Build;

public class DynamicLibrary {

	private final String mainType;
	private final RemoteJar jar;
	private final ToneMarkers markers = new ToneMarkers();
	private final IProject project;

	public DynamicLibrary(IProject project, String repositoryJar, String mainType) {
		this.project = project;
		this.jar = new RemoteJar(repositoryJar);
		this.mainType = mainType;
	}

	public Class<?> loadType(IProgressMonitor monitor) throws CoreException {
		return loadOrError(monitor);
	}

	@SuppressWarnings("resource")
	private Class<?> loadOrError(IProgressMonitor monitor)
			throws CoreException {
		try {
			String plugin = jar.getNameWithoutVersion();
			Optional<IFile> jar = findJar(plugin);
			IFile file = extractJar(monitor, jar);
			URL url = file.getLocationURI().toURL();
			ClassLoader parent = Build.class.getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[] { url },
					parent);
			return (Class<?>) loader.loadClass(mainType);
		} catch (ClassNotFoundException | IOException e) {
			markers.markAsDisabled(project);
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to load panettone jar file!", e));
		}
	}

	private IFile extractJar(IProgressMonitor monitor, Optional<IFile> jar) throws CoreException, IOException {
		if(jar.isPresent()) return jar.get();
		return downloadJarFile(monitor);
	}

	private IFile downloadJarFile(IProgressMonitor monitor) throws CoreException, IOException {
		URL website = jar.toURL();
		IFolder folder = project.getFolder(VRaptorProject.SRC_BUILD_LIB);
		IFile file = folder.getFile(jar.getFullFilename());
		if (file.exists()) {
			file.setContents(website.openStream(), IFile.KEEP_HISTORY
					| IFile.FORCE, monitor);
		} else {
			file.create(website.openStream(), IFile.FORCE, monitor);
		}
		return file;
	}

	private Optional<IFile> findJar(String name) throws CoreException {
		IFolder folder = project.getFolder(VRaptorProject.SRC_BUILD_LIB);
		JarFileLocator jarFinder = new JarFileLocator(name);
		folder.accept(jarFinder);
		return jarFinder.getJar();
	}

}
