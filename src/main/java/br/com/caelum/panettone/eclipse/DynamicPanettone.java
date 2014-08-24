package br.com.caelum.panettone.eclipse;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.builder.Builder;

public class DynamicPanettone {
	
	@SuppressWarnings("resource")
	private static Class<?> loadType(IProject project) {
		try {
			Optional<IFile> jar = findProjectPanettone(project);
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

	public static String constantValue(IProject project, String name) {
		Class<?> type = loadType(project);
		try {
			Field field = type.getDeclaredField(name);
			return (String) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Optional<IFile> findProjectPanettone(IProject project) {
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
	public
	static Object invokeOnCompiler(IProject project, String method, Class[] types,
			Object... args) {
		URI projectPath = project.getLocationURI();
		Class<?> type = DynamicPanettone.loadType(project);
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
}
