package com.github.gmazzo.gradle.plugins

import org.gradle.api.file.FileTree
import org.gradle.api.internal.file.FileOperations
import org.gradle.internal.reflect.Instantiator

class WSDLSourceSet extends DefaultWSDLEntryConfigContainer {
    final String name
    final FileOperations fileOperations

    FileTree from

    File outputDir

    WSDLSourceSet(String name, Instantiator instantiator, FileOperations fileOperations) {
        super(instantiator)

        this.name = name
        this.fileOperations = fileOperations
    }

    void from(Object from) {
        this.from += fileOperations.fileTree(from)
    }

    void outputDir(Object dest) {
        this.outputDir = fileOperations.file(dest)
    }

}
