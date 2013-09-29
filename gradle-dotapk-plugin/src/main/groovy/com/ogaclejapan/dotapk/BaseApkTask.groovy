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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project

abstract class BaseApkTask extends DefaultTask {
	
	static final String COMMON_PARAMS_FILE = 'file'
	static final String COMMON_PARAMS_REPO = 'repo'
	
	ApkPlugin plugin
	ApkPluginExtension dotapk
	
	def targetRepos() {
		def repoList = dotapk.repositories.collect()
		def target = dotapk.__default__ ?: repoList.first()?.name 
		if (plugin.project.hasProperty(COMMON_PARAMS_REPO)) {
			target = plugin.project.property(COMMON_PARAMS_REPO)
		}
		
		if (target == null) {
			throw new GradleException('-Prepo=<name> is required params.')
		}
		
		def repos = repoList.findAll({ it.name =~ target })
		if (repos.empty) {
			throw new GradleException("'${target}' repositories does not exist.")
		}
		
		return repos
	}
	
	def targetFile() {
		if (plugin.project.hasProperty(COMMON_PARAMS_FILE)) {
			return plugin.project.property(COMMON_PARAMS_FILE)
		}
 
		def target = targetRepos().first()?.__default__
		if (target == null) {
			throw new GradleException('-Pfile=<name> is required params.')
		}
		
		return target
	}
}
