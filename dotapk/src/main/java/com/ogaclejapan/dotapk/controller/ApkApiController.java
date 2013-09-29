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

package com.ogaclejapan.dotapk.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ogaclejapan.dotapk.WebApiController;
import com.ogaclejapan.dotapk.WebApiException;
import com.ogaclejapan.dotapk.WebApiReturns;
import com.ogaclejapan.dotapk.dto.ApkFileDto;
import com.ogaclejapan.dotapk.dto.ApkListDto;
import com.ogaclejapan.dotapk.manager.ApkManager;

@Controller
@RequestMapping("/api")
public class ApkApiController extends WebApiController {
	
	private static final Logger log = LoggerFactory.getLogger(ApkApiController.class);
	
	@Autowired
	private ApkManager apkManager;
	
	@RequestMapping(value={"", "/"}, method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<WebApiReturns> getList() throws Exception {
		log.info("list");
		
		List<ApkFileDto> apkList = new ArrayList<ApkFileDto>();
		for (File f : apkManager.getList()) {
			apkList.add(new ApkFileDto(f.getName(), f.lastModified()));
		}
		
		return success(new ApkListDto(apkList));
	}

	@RequestMapping(value="/{name:.+}", method=RequestMethod.GET)
	public void download(@PathVariable String name, HttpServletResponse response) throws Exception {
		log.info("download: " + name);
		
		Resource file = new FileSystemResource(apkManager.getByName(name));
		response.setContentType("application/vnd.android.package-archive");
		response.setContentLength((int)FileUtils.sizeOf(file.getFile()));
		response.setHeader("Content-Disposition","attachment; filename=\"" + file.getFile().getName() +"\"");
		FileCopyUtils.copy(file.getInputStream(), response.getOutputStream());
		
	}
	
	@RequestMapping(value={"", "/"}, method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<WebApiReturns> upload(@RequestParam("file") MultipartFile file) throws Exception {
		log.info("upload");
		
		if (file == null || file.isEmpty()) {
			throw WebApiException.asBadRequest("invalid fileSize");
		}
		apkManager.save(file);
		
		return success("ok");
	}
	
}
