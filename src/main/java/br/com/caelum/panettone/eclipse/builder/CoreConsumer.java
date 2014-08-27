package br.com.caelum.panettone.eclipse.builder;

import org.eclipse.core.runtime.CoreException;

interface CoreConsumer<T> {
	void accept(T f) throws CoreException;
}
