package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.runtime.CoreException;

public interface CoreConsumer<T> {
	void accept(T f) throws CoreException;
}
