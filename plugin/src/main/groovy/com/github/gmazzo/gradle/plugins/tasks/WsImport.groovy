package com.github.gmazzo.gradle.plugins.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.ExecActionFactory
import org.gradle.process.internal.JavaExecAction

import javax.inject.Inject

class WsImport extends DefaultTask {
    private final ExecActionFactory actionFactory

    @InputFiles
    FileTree source

    @Input
    List<String> extraArgs = []

    @OutputDirectory
    File destDir

    @Inject
    WsImport(ExecActionFactory actionFactory) {
        this.actionFactory = actionFactory
    }

    @TaskAction
    void process() {
        source.each { processFile(it) }
    }

    void processFile(File file) {
        JavaExecAction action = actionFactory.newJavaExecAction()
        action.classpath = project.configurations['jaxws']
        action.main = 'com.sun.tools.ws.WsImport'
        action.args = [
                file.absolutePath,
                '-s', destDir.absolutePath,
                '-extension',
                '-Xnocompile',
                logger.isDebugEnabled() ? '-Xdebug' : '-quiet'] + extraArgs
        action.workingDir = destDir
        action.execute().rethrowFailure()
    }

}
