package com.github.gmazzo.gradle.plugins

class WSDLEntryConfig implements Serializable {
    final String name

    String forcePackage

    List<String> extraArgs = []

    WSDLEntryConfig(String name) {
        this.name = name
    }

    void forcePackage(String forcePackage) {
        this.forcePackage = forcePackage
    }

    void argument(String... arguments) {
        extraArgs.addAll(arguments)
    }

    List<String> arguments() {
        return (forcePackage ? ['-p', forcePackage] : []) + extraArgs
    }

}
