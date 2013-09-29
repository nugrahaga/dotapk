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
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.content.FileBody
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction;

class UploadApkTask extends BaseApkTask {

	@TaskAction
	def upload() {
		def file = targetFile()
		logger.info("file=${file}")
		
		def repo = targetRepos().first()
		logger.info("repo=${repo.name}")
		
		def source = new File("${plugin.project.projectDir}/${file}")
		logger.lifecycle("upload...${source.absolutePath}")
		
		def url = "${repo.url}/api"
		println "-> ${repo.url}"
		
		try {
			new HTTPBuilder(url).request(Method.POST) { req ->
				requestContentType: "multipart/form-data"
				MultipartEntity multiPartContent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)
				multiPartContent.addPart("file", new FileBody(source.getAbsoluteFile()))
				req.entity = multiPartContent

				response.success = { resp ->
					println "-> done"
				}
				response.failure = { resp ->
					logger.error(resp.status)
				}
			}
		} catch (ex) {
			if (ex instanceof SocketException) {
				throw new GradleException("${url} is unavailable.", ex)
			} else {
				throw new GradleException("unknown error.", ex)
			}
		}
	}
	
}
