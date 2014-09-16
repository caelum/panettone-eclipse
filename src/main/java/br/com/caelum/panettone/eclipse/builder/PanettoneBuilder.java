package br.com.caelum.panettone.eclipse.builder;

import br.com.caelum.panettone.eclipse.builder.panettone.PanettoneBuild;

public class PanettoneBuilder extends GenericBuilder {

	public static final String BUILDER_ID = "panettone-eclipse.panettoneBuilder";

	@Override
	protected PanettoneBuild getBuild() {
		return new PanettoneBuild(getProject());
	}

}
