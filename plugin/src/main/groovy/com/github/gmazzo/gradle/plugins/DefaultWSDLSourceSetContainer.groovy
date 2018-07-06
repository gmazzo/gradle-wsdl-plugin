package com.github.gmazzo.gradle.plugins

import org.gradle.api.Namer
import org.gradle.api.internal.FactoryNamedDomainObjectContainer
import org.gradle.api.internal.file.FileOperations
import org.gradle.internal.reflect.Instantiator

class DefaultWSDLSourceSetContainer extends FactoryNamedDomainObjectContainer<WSDLSourceSet> implements WSDLSourceSetContainer {
    private final FileOperations fileOperations

    DefaultWSDLSourceSetContainer(Instantiator instantiator, FileOperations fileOperations) {
        super(WSDLSourceSet.class, instantiator, (Namer<WSDLSourceSet>) { WSDLSourceSet it -> it.name })

        this.fileOperations = fileOperations
    }

    @Override
    protected WSDLSourceSet doCreate(String name) {
        return instantiator.newInstance(WSDLSourceSet.class, name, instantiator, fileOperations)
    }

}
