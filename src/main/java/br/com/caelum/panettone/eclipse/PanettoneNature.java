package br.com.caelum.panettone.eclipse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import br.com.caelum.panettone.eclipse.builder.BiscottiBuilder;
import br.com.caelum.panettone.eclipse.builder.PanettoneBuilder;

public class PanettoneNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "panettone-eclipse.panettoneNature";
	
	private final static List<String> BUILDERS = Arrays.asList(PanettoneBuilder.BUILDER_ID, BiscottiBuilder.BUILDER_ID);

	private IProject project;

	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		List<ICommand> commands = new ArrayList<>(Arrays.asList(desc.getBuildSpec()));
		BUILDERS.stream()
			.map(id -> createCommand(desc, id))
			.forEach(commands::add);
		ICommand[] newCommands = commands.toArray(new ICommand[commands.size()]);
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, null);

		VRaptorProject tone = new VRaptorProject(project);
		tone.prepareFolders();
	}
	
	private ICommand createCommand(IProjectDescription desc, String builderId) {
		ICommand command = desc.newCommand();
		command.setBuilderName(builderId);
		return command;
	}

	public void deconfigure() throws CoreException {
		new ToneMarkers().clear(project);
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = Stream.of(description.getBuildSpec())
			.filter(command -> !BUILDERS.contains(command.getBuilderName()))
			.toArray(n -> new ICommand[n]);
		description.setBuildSpec(commands);
		project.setDescription(description, null);			
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
