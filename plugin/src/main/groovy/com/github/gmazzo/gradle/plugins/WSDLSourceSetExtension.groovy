package com.github.gmazzo.gradle.plugins

import org.gradle.api.file.ConfigurableFileTree

class WSDLSourceSetExtension {

    ConfigurableFileTree src

    File dest

    void src(ConfigurableFileTree src) {
        this.src = src
    }

    void dest(File dest) {
        this.dest = dest
    }

}
