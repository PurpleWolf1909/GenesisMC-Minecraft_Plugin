package me.dueris.calio.data.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPlugin {
	String pluginName() default "genesismc";
}