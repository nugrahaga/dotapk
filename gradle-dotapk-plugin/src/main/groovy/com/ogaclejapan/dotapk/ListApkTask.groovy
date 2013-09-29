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

class ListApkTask extends BaseApkTask {

	@TaskAction
	def list() { 
		def repos = targetRepos()
		logger.info("repos=${repos.collect({it.name})}")
		
		repos.each { repo ->
			def url = "${repo.url}/api"
			try {
				new HTTPBuilder(url).request(Method.GET, ContentType.JSON) { req ->
					response.success = { resp, json ->
						json.result.files.each { apk ->
							def date = new Date(apk.lastModifiedTime).format("yyyy-MM-dd HH:mm:ss")
							println "${apk.name} \t [ repo-> ${repo.name}, time-> ${date} ]"
						}
					}
					response.failure = { resp ->
						logger.error("${url} - ${resp.status}")
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
	}
	
}
