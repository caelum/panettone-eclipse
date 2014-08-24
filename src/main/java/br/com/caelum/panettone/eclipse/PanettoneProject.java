package br.com.caelum.panettone.eclipse;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

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
}
