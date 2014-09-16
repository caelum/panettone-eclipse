package br.com.caelum.panettone.eclipse.builder;

import br.com.caelum.panettone.eclipse.builder.biscotti.BiscottiBuild;

public class BiscottiBuilder extends GenericBuilder {

	public static final String BUILDER_ID = "panettone-eclipse.biscottiBuilder";

	@Override
	protected Build getBuild() {
		return new BiscottiBuild(getProject());
	}

}
