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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WebApiException extends RuntimeException {

	private static final long serialVersionUID = -1931398504229301844L;

	private ResponseEntity<WebApiErrorReturns> responseEntity;

	WebApiException(HttpStatus status) {
		this(status, (String) null);
	}

	WebApiException(HttpStatus status, Throwable cause) {
		this(status, (String) null, cause);
	}

	WebApiException(HttpStatus status, String message) {
		this(status, message, null, null);
	}

	WebApiException(HttpStatus status, String message, Throwable cause) {
		this(status, message, null, null, cause);
	}

	WebApiException(HttpStatus status, String message, String code, Object result) {
		this(status, new WebApiErrorReturns(new WebApiError(status, message, code), result));
	}

	WebApiException(HttpStatus status, String message, String code, Object result, Throwable cause) {
		this(status, new WebApiErrorReturns(new WebApiError(status, message, code), result), cause);
	}

	private WebApiException(HttpStatus status, WebApiErrorReturns errorReturns) {
		super(errorReturns.getError().getMessage());
		this.responseEntity = new ResponseEntity<WebApiErrorReturns>(errorReturns, status);
	}

	private WebApiException(HttpStatus status, WebApiErrorReturns errorReturns, Throwable cause) {
		super(errorReturns.getError().getMessage(), cause);
		this.responseEntity = new ResponseEntity<WebApiErrorReturns>(errorReturns, status);
	}

	public ResponseEntity<WebApiErrorReturns> getResponseEntity() {
		return responseEntity;
	}

	//400
	
	public static WebApiException asBadRequest(String message) {
		return new WebApiException(HttpStatus.BAD_REQUEST, message);
	}

	//404
	
	public static WebApiException asNotFound(String message) {
		return new WebApiException(HttpStatus.NOT_FOUND, message);
	}
	
	//500
	
	public static WebApiException asInternalServerError(String message) {
		return new WebApiException(HttpStatus.INTERNAL_SERVER_ERROR, message);
	}
	
	public static WebApiException asInternalServerError(String message, Throwable cause) {
		return new WebApiException(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
	}

}
