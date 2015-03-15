package br.com.caelum.panettone.eclipse;

import static java.util.Optional.of;

import java.util.Iterator;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddRemovePanettoneNatureHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection structured = (IStructuredSelection) selection;
		for (Iterator<?> it = structured.iterator(); it.hasNext();) {
			Object element = it.next();
			Optional<IProject> project = projectFor(element);
			if (project.isPresent()) {
				try {
					new PrePanettoneProject(project.get()).toggleNature();
				} catch (CoreException e) {
					throw new ExecutionException("Failed to toggle nature", e);
				}
			}
		}

		return null;
	}

	private Optional<IProject> projectFor(Object element) {
		if (element instanceof IProject) {
			return of((IProject) element);
		}
		if (element instanceof IAdaptable) {
			return of((IProject) ((IAdaptable) element)
					.getAdapter(IProject.class));
		}
		return null;
	}

}