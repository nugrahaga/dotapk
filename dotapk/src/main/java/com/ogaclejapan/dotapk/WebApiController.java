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

package com.ogaclejapan.dotapk;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class WebApiController {

	private static Logger log = LoggerFactory.getLogger(WebApiController.class);

	@ExceptionHandler(value = { WebApiException.class })
	@ResponseBody
	public ResponseEntity<WebApiErrorReturns> handleWebApiException(WebApiException wae, HttpServletRequest request) {
		ResponseEntity<WebApiErrorReturns> errorReturns = wae.getResponseEntity();
		final String errorMessage = errorReturns.getBody().getError().getMessage();
		switch (errorReturns.getStatusCode()) {
		case INTERNAL_SERVER_ERROR:
			log.error(errorMessage, wae);
			break;
		default:
			log.warn(errorMessage, wae);
		}
		return errorReturns;
	}
	
	@ExceptionHandler(value = { Exception.class })
	@ResponseBody
	public ResponseEntity<WebApiErrorReturns> handleException(Exception e, HttpServletRequest request) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	protected ResponseEntity<WebApiReturns> success(Object result) {
		return success(result, HttpStatus.OK);
	}
	
	protected ResponseEntity<WebApiReturns> success(Object result, HttpStatus status) {
		return new ResponseEntity<WebApiReturns>(new WebApiReturns(result), status);
	}

	protected ResponseEntity<WebApiErrorReturns> error(HttpStatus status) {
		return error(status, null);
	}
	
	protected ResponseEntity<WebApiErrorReturns> error(HttpStatus status, String message) {
		return error(status, message, null, null);
	}

	protected ResponseEntity<WebApiErrorReturns> error(HttpStatus status, String message, String code, Object result) {
		return new ResponseEntity<WebApiErrorReturns>(new WebApiErrorReturns(new WebApiError(status, message, code), result), status);
	}
	
}
