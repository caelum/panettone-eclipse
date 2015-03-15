package br.com.caelum.panettone.eclipse;

import static java.util.Arrays.stream;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

/**
 * All code that does not require panettone to be in the project.
 * 
 * @author guilherme silveira
 */
public class PrePanettoneProject {

	private final IProject project;

	public PrePanettoneProject(IProject project) {
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
		boolean isPresent = Arrays.asList(natures).contains(
				PanettoneNature.NATURE_ID);
		if (isPresent) {
			return removeNatureFrom(natures);
		}
		return addNatureTo(natures);
	}

	private String[] addNatureTo(String[] natures) {
		String[] newNatures = copyArrayWithOneExtraSpace(natures);
		newNatures[natures.length] = PanettoneNature.NATURE_ID;
		return newNatures;
	}

	private String[] copyArrayWithOneExtraSpace(String[] origin) {
		String[] target = new String[origin.length + 1];
		System.arraycopy(origin, 0, target, 0, origin.length);
		return target;
	}

	private String[] removeNatureFrom(String[] natures) {
		return stream(natures)
				.filter(this::nonPanettone)
				.toArray(String[]::new);
	}

	private boolean nonPanettone(String n) {
		return !n.equals(PanettoneNature.NATURE_ID);
	}
}
