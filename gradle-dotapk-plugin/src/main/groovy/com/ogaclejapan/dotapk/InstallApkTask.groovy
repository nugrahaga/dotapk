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
import org.gradle.api.tasks.TaskAction;
import org.apache.commons.lang.SystemUtils

class InstallApkTask extends BaseApkTask {
	
	@TaskAction
	def install() {
		
		plugin.initAdb()
		
		def file = targetFile()
		logger.info("file=${file}")
		
		def apk = new File("${plugin.project.projectDir}/${file}")
		logger.info("apk=${apk.absolutePath}")
		if (!apk.exists()) {
			throw new GradleException("${file} not found.")
		}
		
		install(apk)
	}

	def install(apk) {
		def stdout = new StringBuffer()
		def stderr = new StringBuffer()
		def cmd = "${plugin.adb.absolutePath} install -r ${apk.name}"

		logger.lifecycle(cmd)

		def proc = cmd.execute()
		proc.consumeProcessOutput(stdout, stderr)
		proc.waitForOrKill(1000 * 60) //wait for 1min
	 
		if (stdout.length() > 0) {
			println stdout.toString()
		}
		if (stderr.length() > 0) {
			println stderr.toString()
		}
	}
	
}
