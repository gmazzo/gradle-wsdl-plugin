package com.github.gmazzo.gradle.plugins

import com.github.gmazzo.gradle.plugins.tasks.WsImport
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileOperations
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class WSDLPlugin implements Plugin<Project> {
    private final Instantiator instantiator
    private final FileOperations fileOperations

    @Inject
    WSDLPlugin(Instantiator instantiator, FileOperations fileOperations) {
        this.instantiator = instantiator
        this.fileOperations = fileOperations
    }

    @Override
    void apply(Project project) {
        project.with {
            def extension = extensions.create('wsdl', WSDLPluginExtension.class, instantiator, fileOperations)

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
                def wsdl = extension.create("${ss.name}")
                wsdl.with {
                    from = fileTree("src/${ss.name}/wsdl") {
                        include '**/*.wsdl'
                    }
                    outputDir = file("$buildDir/generated/jaxws/${ss.name}/java")
                }

                def suffix = ss.name == 'main' ? '' : ss.name.capitalize()
                def wsImport = tasks.<WsImport> create("wsImport$suffix", WsImport.class)

                afterEvaluate {
                    ss.java.srcDirs wsdl.outputDir

                    wsImport.with { self ->
                        onlyIf { !wsdl.from.empty }
                        source = wsdl.from
                        outputDir = wsdl.outputDir
                        configs = wsdl

                        tasks[ss.compileJavaTaskName].dependsOn self
                    }
                }
            }
        }
    }

}
