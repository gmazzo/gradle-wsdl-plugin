package com.github.gmazzo.gradle.plugins

import org.gradle.api.internal.file.FileOperations
import org.gradle.internal.reflect.Instantiator

class WSDLPluginExtension extends DefaultWSDLSourceSetContainer {

    String jaxwsToolsDependencies = 'com.sun.xml.ws:jaxws-tools:2.2.10'

    WSDLPluginExtension(Instantiator instantiator, FileOperations fileOperations) {
        super(instantiator, fileOperations)
    }

}
