package com.github.gmazzo.gradle.plugins.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class DownloadWSDLs extends DefaultTask {

    @Input
    Map<String, String> froms = new HashMap<>()

    @OutputDirectory
    File into

    void from(String file, String url) {
        froms[file] = url
    }

    void into(File into) {
        this.into = into
    }

    @TaskAction
    void perform() {
        Set<URL> downloaded = []

        froms.each { k, v ->
            def targetDir = new File(into, k)
            def url = new URL(v)
            def file = "${new File(url.path).name}.wsdl"

            downloadAndProcess(targetDir, k, file, url, downloaded)
        }
    }

    void downloadAndProcess(File targetDir, String entry, String file, URL url, Set<URL> downloaded) {
        println "$entry: downloading: $url..."

        // downloads the given WSDL
        targetDir.mkdirs()
        def targetFile = new File(targetDir, file)
        targetFile.withOutputStream { os ->
            os << url.openStream()
        }

        // parses the content and downloads the imports too
        new XmlSlurper()
                .parse(targetFile)
                .'**'.findAll({ it.name() == 'import' })
                .each {
            def name = it.@schemaLocation.text()

            if (name) {
                def importUrl = url.toURI().resolve("$name").toURL()

                if (downloaded.add(importUrl)) {
                    downloadAndProcess(targetDir, entry, name, importUrl, downloaded)
                }
            }
        }
    }

}
