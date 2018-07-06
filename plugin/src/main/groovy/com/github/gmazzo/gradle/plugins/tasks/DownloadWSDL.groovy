package com.github.gmazzo.gradle.plugins.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class DownloadWSDL extends DefaultTask {
    private final FileOperations fileOperations

    @Input
    Map<URL, String> froms = new HashMap<>()

    @OutputDirectory
    File into

    @Inject
    DownloadWSDL(FileOperations fileOperations) {
        this.fileOperations = fileOperations
    }

    void from(Object url) {
        from(null, url)
    }

    void from(String name, Object url) {
        def u = fileOperations.uri(url).toURL()
        froms[u] = name ?: new File(u.path).name.replaceFirst('\\.[^.]+$', '')
    }

    void into(Object path) {
        this.into = fileOperations.file(path)
    }

    @TaskAction
    void perform() {
        Set<URL> downloaded = []
        Set<File> generated = []

        froms.each { url, name ->
            def file = new File(into, "${name}.wsdl")

            downloadAndProcess file, name, url, downloaded, generated
        }
    }

    void downloadAndProcess(File targetFile, String entry, URL url, Set<URL> downloaded, Set<File> generated) {
        logger.info "Downloading: ${into.toURI().relativize(targetFile.toURI())} from $url..."

        // downloads the given WSDL
        targetFile.parentFile.mkdirs()
        targetFile.withOutputStream { os ->
            os << url.openStream()
        }

        // parses the content and downloads the imports too
        new XmlSlurper()
                .parse(targetFile)
                .'**'.findAll({ it.name() == 'import' })
                .each {
            String location = it.@schemaLocation.text()

            if (location) {
                if (into == targetFile.parentFile) {
                    // a WSDL with XSD dependencies detected, move them to a grouping folder
                    def dir = new File(into, entry)
                    dir.mkdirs()

                    def oldFile = targetFile
                    targetFile = new File(dir, targetFile.name)
                    oldFile.renameTo(targetFile)
                }

                def importUrl = url.toURI().resolve(location).toURL()

                if (downloaded.add(importUrl)) {
                    def name = url.toURI().relativize(importUrl.toURI()).path
                            .replaceFirst('^\\s*$', 'schema')
                            .replaceFirst('(?<!\\.xsd)$', '$0.xsd')
                    def importFile = new File(targetFile.parentFile, name)
                    for (int i = 2; !generated.add(importFile); i++) {
                        importFile = new File(targetFile.parentFile,
                                name.replaceFirst('(?=\\.xsd$)', "_$i"))
                    }

                    downloadAndProcess importFile, entry, importUrl, downloaded, generated
                }
            }
        }
    }

}
