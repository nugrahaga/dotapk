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

import groovyx.net.http.*
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction;

class DownloadApkTask extends BaseApkTask {
	
	@TaskAction
	def download() {
		def file = targetFile()
		logger.info("file=${file}")
		
		def dest = new File("${plugin.project.projectDir}/${file}")
		logger.info("dest=${dest.absolutePath}")
		if (dest.exists()) {
			dest.delete()
		}
		
		def repos = targetRepos()
		logger.info("repos=${repos.collect({it.name})}")

		repos.each { repo ->
			
			if (dest.exists()) {
				return
			}

			def url = "${repo.url}/api/${file}"
			
			logger.lifecycle("download...${url}")
			try {
				new HTTPBuilder(url).request(Method.GET, ContentType.BINARY) { req ->
					response.success = { resp ->
						dest << resp.entity.content
						println "-> ${dest.absolutePath}"
					}
					
					response.failure = { resp ->
						logger.warn("${url} is ${resp.status}")
					}
				}
			} catch (ex) {
				if (ex instanceof SocketException) {
					logger.warn("${url} is unavailable.")
				} else {
					throw new GradleException("unknown error.", ex)
				}
			}
			
		}
		
		if (!dest.exists()) {
			println "${file} not found."
		}
		
	}
	
}
