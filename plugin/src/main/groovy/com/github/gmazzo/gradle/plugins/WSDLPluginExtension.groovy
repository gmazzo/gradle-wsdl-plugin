package com.github.gmazzo.gradle.plugins

class WSDLPluginExtension {

    Closure<File> srcDir = { ss -> file("src/${ss.name}/wsdl") }

    String jaxwsToolsDependencies = 'com.sun.xml.ws:jaxws-tools:2.2.10'

    void jaxwsToolsDependencies(String jaxwsToolsDependencies) {
        this.jaxwsToolsDependencies = jaxwsToolsDependencies
    }

}
