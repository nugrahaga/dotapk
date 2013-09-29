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

package com.ogaclejapan.dotapk.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ogaclejapan.dotapk.WebApiException;

@Component
public class ApkFileManager implements ApkManager {

	private static final Logger log = LoggerFactory.getLogger(ApkFileManager.class);
	
	private File homeDir = new File(System.getProperty("user.home"), ".apk");
	private File apkDir = new File(homeDir, "files");

	public File getHomeDir() {
		return homeDir;
	}

	public File getApkDir() {
		return apkDir;
	}

	@PostConstruct
	public void init() throws IOException {

		if (!homeDir.exists()) {
			if (!homeDir.mkdirs()) {
				throw new IOException("can not create folder: " + homeDir.getAbsolutePath());
			}
		}

		if (!homeDir.canWrite()) {
			throw new IOException("write permission denied: " + homeDir.getAbsoluteFile());
		}

		if (!apkDir.exists()) {
			if (!apkDir.mkdirs()) {
				throw new IOException("can not create folder: " + apkDir.getAbsolutePath());
			}
		}

		if (!apkDir.canWrite()) {
			throw new IOException("write permission denied: " + apkDir.getAbsoluteFile());
		}

		log.debug("dotapk-directory: {}", homeDir.getAbsolutePath());
	}

	@Override
	public File[] getList() throws WebApiException {
		if (!apkDir.exists()) {
			throw WebApiException.asInternalServerError("does not exist: " + apkDir.getAbsolutePath());
		}
		
		return apkDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(".+\\.apk");
			}
		});
	}

	@Override
	public File getByName(final String apk) throws WebApiException {
		if (!apkDir.exists()) {
			throw WebApiException.asInternalServerError("does not exist: " + apkDir.getAbsolutePath());
		}
		
		File[] files = apkDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(Pattern.quote(apk));
			}
		});
		
		if (files == null || files.length == 0) {
			throw WebApiException.asNotFound("does not exist: " + apk);
		}
		
		return files[0];
	}

	@Override
	public void save(MultipartFile file) throws WebApiException {
		if (!file.getOriginalFilename().endsWith(".apk")) {
			throw WebApiException.asBadRequest("file must be format *.apk: ");
		}
		
		log.debug("save apk: {}", file.getOriginalFilename());
		
		File apkfile = new File(apkDir, file.getOriginalFilename());
		if (apkfile.exists()) {
			if (!FileUtils.deleteQuietly(apkfile)) {
				throw WebApiException.asInternalServerError("can not delete old apk.");
			}
		}
		
		try {
			FileUtils.copyInputStreamToFile(file.getInputStream(), apkfile);
		} catch (IOException ioe) {
			throw WebApiException.asInternalServerError("can not save apk.", ioe);
		}
	}	
	

}
