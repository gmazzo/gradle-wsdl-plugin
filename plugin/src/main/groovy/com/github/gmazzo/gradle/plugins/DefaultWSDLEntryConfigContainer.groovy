package com.github.gmazzo.gradle.plugins

import org.gradle.api.Namer
import org.gradle.api.internal.FactoryNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator

class DefaultWSDLEntryConfigContainer extends FactoryNamedDomainObjectContainer<WSDLEntryConfig> implements WSDLEntryConfigContainer {

    DefaultWSDLEntryConfigContainer(Instantiator instantiator) {
        super(WSDLEntryConfig.class, instantiator, (Namer<WSDLEntryConfig>) { WSDLEntryConfig it -> it.name })
    }

}
