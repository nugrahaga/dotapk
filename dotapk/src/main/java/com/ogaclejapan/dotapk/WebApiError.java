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

public class WebApiError {

	private int status;
	private String message;
	private String code;

	public WebApiError(HttpStatus status) {
		this(status, null);
	}

	public WebApiError(HttpStatus status, String message) {
		this(status, message, null);
	}

	public WebApiError(HttpStatus status, String message, String code) {
		this.status = status.value();
		this.message = (message != null) ? message : status.getReasonPhrase();
		this.code = code;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}

}
