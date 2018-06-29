package com.github.gmazzo.gradle.plugins

import com.github.gmazzo.gradle.plugins.tasks.WsImport
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class WSDLPlugin implements Plugin<Project> {
    private final Instantiator instantiator

    @Inject
    WSDLPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    @Override
    void apply(Project project) {
        project.with {
            def extension = extensions.create('wsdl', WSDLPluginExtension.class)

            configurations {
                jaxws
            }

            afterEvaluate {
                def val = extension.jaxwsToolsDependencies
                if (val) {
                    dependencies.add('jaxws', val)
                }
            }

            sourceSets.all { ss ->
                def wsdl = ss.ext.wsdl = instantiator.newInstance(WSDLSourceSetExtension.class).with {
                    src = fileTree("src/main/wsdl") {
                        include '**/*.wsdl'
                    }
                    dest = file("$buildDir/generated/${ss.name}/java")
                    return it
                }

                def suffix = ss.name == 'main' ? '' : ss.name.capitalize()

                def wsImport = tasks.<WsImport> create("wsImport$suffix", WsImport.class)
                wsImport.with { self ->
                    onlyIf { !wsdl.src.empty }
                    wsdls = wsdl.src
                    outDir = wsdl.dest

                    tasks[ss.compileJavaTaskName].dependsOn self
                }

                afterEvaluate {
                    ss.java.srcDirs wsdl.dest
                }
            }
        }
    }

}