# DotApk

Implementation of client and server that manages the apk files.

### Gradle DotApk Plugin

The plugin provides tasks for uploading, downloading and installing [APK][]

[Plugin][] implemented in Groovy/Gradle. Repository: [http://ogaclejapan.github.com/dotapk]

Required gradle version : `1.5+`

### Server

The server provides API for managing [APK][]

[Server-side][] implemented in Java. Download the [dotapk.war][]

# Usage

To use the Gradle DotApk plugin, include in your build script:

	buildscript {
	    repositories {
	    	mavenCentral()
	        maven {
	            url uri('http://ogaclejapan.github.com/dotapk')
	        }
	    }
	    dependencies {
	        classpath group: 'com.ogaclejapan', name: 'gradle-dotapk-plugin', version: '1.0'
	    }
	}

	apply plugin: 'dotapk'

	dotapk {
		repositories {
			local { 
				url = '[Server URL to the dotapk.war]'
				__default__ = 'foo.apk' //task -Pfile parameter set default value.(optional)
			}
			//repository can define multiple
		}
		__default__ = 'local' //optional: task -Prepo parameter set default value.(optional)
	}

*Note:* the plugin creates a folder '{user.home}/.apk'.

### Tasks

The Gradle DotApk plugin defines the following tasks:

* `apk_list`: Show all the APKs that are registered in the repository. Task property '-Prepo' can change the repository to be displayed.
* `apk_get`: Download the APK from the repository. Task property '-Pfile' enter the filename of APK.
* `apk_install`: Install the APK from the repository. Task property '-Pfile' enter the filename of APK. Device required [Enable USB debugging][]
* `apk_upload`: Upload the APK to the repository. Task property '-Pfile' enter the filename of APK.

# Example

	git clone git@github.com:ogaclejapan/dotapk.git // or download dotapk.zip
	cd samples


Sample is developing a dotapk.war on Jetty (press Ctrl-C to Stop)
	
	./gradlew -b server.gradle runDotApk
	//dotapk.war running localhost:8080/dotapk on jetty


Run new terminal. (because keep on running jetty)   
Use the [jota-text-editor][] application as a test apk file.

	wget https://jota-text-editor.googlecode.com/files/jota-text-editor0230.apk

Run UploadApkTask.

	./gradlew -b client.gradle apk_upload -Pfile=jota-text-editor0230.apk

Run ListApkTask.

	./gradlew -b client.gradle apk_list

Run DownloadApkTask.
	
	rm -f jota-text-editor0230.apk
	./gradlew -b client.gradle apk_get -Pfile=jota-text-editor0230.apk

Run InstallApkTask.

	./gradlew -b client.gradle apk_install -Pfile=jota-text-editor0230.apk


## License
The DotApk is released under version 2.0 of the [Apache License][].


[jota-text-editor]: https://code.google.com/p/jota-text-editor/
[Enable USB debugging]: http://www.groovypost.com/howto/mobile/how-to-enable-usb-debugging-android-phone/
[dotapk.war]: http://ogaclejapan.github.io/dotapk/com/ogaclejapan/dotapk/1.0/dotapk-1.0.war
[Server-side]: https://github.com/ogaclejapan/dotapk/tree/master/dotapk
[APK]: http://en.wikipedia.org/wiki/APK_(file_format)
[Plugin]: https://github.com/ogaclejapan/dotapk/tree/master/gradle-dotapk-plugin
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

