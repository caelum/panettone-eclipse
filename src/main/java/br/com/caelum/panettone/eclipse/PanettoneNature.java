package br.com.caelum.panettone.eclipse;

import static java.util.Arrays.stream;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import br.com.caelum.panettone.eclipse.builder.PanettoneBuilder;

public class PanettoneNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "panettone-eclipse.panettoneNature";

	private IProject project;

	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();

		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(PanettoneBuilder.BUILDER_ID)) {
				return;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		ICommand command = desc.newCommand();
		command.setBuilderName(PanettoneBuilder.BUILDER_ID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, null);
		
		IJavaProject java = JavaCore.create(project);

		PanettoneProject tone = new PanettoneProject(project);
		try {
			tone.prepareFolders();
			if(tone.isEnabled()) {
				IPath srcPath= java.getPath().append(tone.getViewOutput());
				addToClasspath(java, srcPath);
			}
		} catch (Exception e) {
			tone.markAsDisabled();
		}
	}

	private void addToClasspath(IJavaProject java, IPath srcPath)
			throws JavaModelException {
		IClasspathEntry[] entries = java.getRawClasspath();
		boolean isPresent = stream(entries).anyMatch(entry -> entry.getPath().equals(srcPath));
		if(isPresent) return;
		
		IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
		System.arraycopy(entries, 0, newEntries, 0, entries.length);
		IClasspathEntry srcEntry= JavaCore.newSourceEntry(srcPath, null);
		newEntries[entries.length] = JavaCore.newSourceEntry(srcEntry.getPath());
		java.setRawClasspath(newEntries, null);
	}

	public void deconfigure() throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(PanettoneBuilder.BUILDER_ID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
						commands.length - i - 1);
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);			
				return;
			}
		}
	}

	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

}
