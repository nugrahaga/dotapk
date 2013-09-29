/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ogaclejapan.dotapk

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.apache.commons.lang.SystemUtils
import org.apache.maven.artifact.ant.InstallTask;

class ApkPlugin implements Plugin<Project> {
	
	static final String PLUGIN_GROUP = 'DotApk'
	
	Project project
	File homeDir
//	File cacheDir
	File adb
	
	void apply(Project project) {
		def repositories = project.container(ApkRepository)
		repositories.all {
			url = "http://localhost:8080/dotapk-repo"
			__default__ = null
		}
		def dotapk = project.extensions.create('dotapk', ApkPluginExtension, repositories)

		init(project)
		
		def listTask = createListTask(project)
		def uploadTask = createUploadTask(project)
		def downloadTask = createDownloadTask(project)
		def installTask = createInstallTask(project)

		installTask.dependsOn downloadTask
 
		[listTask, uploadTask, downloadTask, installTask].each { task ->
			task.group = PLUGIN_GROUP
			task.plugin = this
			task.dotapk = dotapk
		}

	}
	
	String os() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return 'win'
		}
		if (SystemUtils.IS_OS_MAC_OSX) {
			return 'mac'
		}
		if (SystemUtils.IS_OS_LINUX) {
			return 'linux'
		}
		return 'other'
	}
	
	void initAdb() {
		switch (os()) {
			case 'win':
				initAdbForWindows()
				break;
			case 'mac':
				initAdbForMac()
				break;
			default:
				throw new GradleException('not supported os. adb must be windows or mac')
		}
	}
	
	private void initAdbForWindows() {
		adb = new File(homeDir, 'adb.exe')
		if (!adb.exists()) {
			adb << getClass().getResourceAsStream('/forWin/adb.exe')
		}
		def adbWinApiDll = new File(homeDir, 'AdbWinApi.dll')
		if (!adbWinApiDll.exists()) {
			adbWinApiDll << getClass().getResourceAsStream('/forWin/AdbWinApi.dll')
		}
		def adbWinUsbApiDll = new File(homeDir, 'AdbWinUsbApi.dll')
		if (!adbWinUsbApiDll.exists()) {
			adbWinUsbApiDll << getClass().getResourceAsStream('/forWin/AdbWinUsbApi.dll')
		}
	}
	
	private void initAdbForMac() {
		adb = new File(homeDir, 'adb')
		if (!adb.exists()) {
			adb << getClass().getResourceAsStream("/forMac/adb")
			"chmod u+x ${adb.getAbsolutePath()}".execute()
		}
	}

	private void init(Project project) {
		this.project = project
		
		homeDir = new File(System.getProperty('user.home'), '.apk');
		if (!homeDir.exists()) {
			if (!homeDir.mkdirs()) {
				throw new IOException("can not create folder: ${homeDir.getAbsolutePath()}");
			}
		}
		if (!homeDir.canWrite()) {
			throw new IOException("write permission denied: ${homeDir.getAbsoluteFile()}");
		}

//		cacheDir = new File(homeDir, 'cached')
//		if (!cacheDir.exists()) {
//			if (!cacheDir.mkdirs()) {
//				throw new IOException("can not create folder: ${cacheDir.getAbsolutePath()}");
//			}
//		}
//		if (!cacheDir.canWrite()) {
//			throw new IOException("write permission denied: ${cacheDir.getAbsoluteFile()}");
//		}
	}
	
	private ListApkTask createListTask(Project project) {
		def task = project.tasks.create('apk_list', ListApkTask)
		task.description = "Show all the apks that are registered in the repository."

		return task
	}
	
	private UploadApkTask createUploadTask(Project project) {
		def task = project.tasks.create('apk_upload', UploadApkTask)
		task.description = "Upload the apk to the repository."

		return task
	}

	private DownloadApkTask createDownloadTask(Project project) {
		def task = project.tasks.create('apk_get', DownloadApkTask)
		task.description = "Download the apk from the repository."

		return task
	}

	private InstallApkTask createInstallTask(Project project) {
		def task = project.tasks.create('apk_install', InstallApkTask)
		task.description = "Install the apk from the repository."

		return task
	}

}
