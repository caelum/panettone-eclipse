package br.com.caelum.panettone.eclipse;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.builder.Builder;

public class PanettoneProject {

	private final IProject project;

	public PanettoneProject(IProject project) {
		this.project = project;
	}

	public void toggleNature() throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = naturesFor(natures);
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

	private String[] naturesFor(String[] natures) {
		boolean isPresent = Arrays.asList(natures)
				.contains(PanettoneNature.NATURE_ID);
		if (isPresent) {
			return removeNatureFrom(natures);
		}
		return addNatureTo(natures);
	}

	private String[] addNatureTo(String[] natures) {
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = PanettoneNature.NATURE_ID;
		return newNatures;
	}

	private String[] removeNatureFrom(String[] natures) {
		String[] newNatures = Arrays.stream(natures)
				.filter(n -> !n.equals(PanettoneNature.NATURE_ID))
				.toArray(String[]::new);
		return newNatures;
	}
	
	@SuppressWarnings("resource")
	private Class<?> loadType() {
		Optional<IFile> jar = findProjectPanettone();
		try {
			if (!jar.isPresent()) {
				throw new RuntimeException(
						"Unable to find panettone on your src/build/libs.");
			}
			URL url = jar.get().getLocationURI().toURL();
			ClassLoader parent = Builder.class.getClassLoader();
			URLClassLoader loader = new URLClassLoader(new URL[] { url },
					parent);
			return (Class<?>) loader
					.loadClass("br.com.caelum.vraptor.panettone.VRaptorCompiler");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String constantValue(String name) {
		Class<?> type = loadType();
		try {
			Field field = type.getDeclaredField(name);
			return (String) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Optional<IFile> findProjectPanettone() {
		IFolder folder = project.getFolder("src/build/lib");
		if (!folder.exists()) {
			return Optional.empty();
		}
		try {
			JarFileLocator jarFinder = new JarFileLocator();
			folder.accept(jarFinder);
			return jarFinder.getJar();
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public Object invokeOnCompiler(String method, Class[] types,
			Object... args) {
		URI projectPath = project.getLocationURI();
		Class<?> type = loadType();
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

}
