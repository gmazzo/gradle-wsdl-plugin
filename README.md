# gradle-wsdl-plugin
A Gradle plugin for generate JAB-WS clients from given WSDL files and URLs.

## Usage
On your `build.gradle` add:
```groovy
plugins {
    id 'java' // or 'war'
    id 'com.github.gmazzo.wsdl' version '0.1'
}
```
Check [https://plugins.gradle.org/plugin/com.github.gmazzo.wsdl](https://plugins.gradle.org/plugin/com.github.gmazzo.wsdl) for other instructions

After applying the plugin, a task `wsImport` will be added to your build.
By default, any `*.wsdl` file under `src/main/wsdl` will processed and generated classes stored under `build/generated/jaxws/main/java`.

### Customization
```groovy
wsdl {
    main { // the sourceSet
        from 'path/to/custom/folder' // adds a custom the source folder
        outputDir "$buildDir/jaxwbClients" // sets a custom the output folder
    
        all { // applies to all WSDLs
            argument '-encoding', 'UTF-8' // adds extra arguments to the 'wsImport' command
        }
        europepmc { // applies only to 'europepmc.wsdl' file
            forcePackage 'uk.ac.ebi.europepmc' // sets the package for the generated client
        }
    }
}
```

## Downloading WSDLs from remote URLs
The `com.github.gmazzo.gradle.plugins.tasks.DownloadWSDL` task allows you to download a WSDL to a local folder, and all its referenced `XSD` schemas.

Example:
````groovy
import com.github.gmazzo.gradle.plugins.tasks.DownloadWSDL

task downloadWsdls(type: DownloadWSDL) { self ->
    into 'src/main/wsdl' // where to store the downloaded files
    from 'http://www.dneonline.com/calculator.asmx?wsdl' // just an URL, the local name will be picked from it
    from 'europepmc', 'https://www.ebi.ac.uk:443/europepmc/webservices/soap?wsdl' // local name and URL
    from 'countries', 'http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL'

    wsImport.dependsOn self // optional, links this task to the build graph, forcing it to run before `wsImport`
}
````
