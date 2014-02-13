package org.mimacom.liferay.demo.security.auth;

/*
 * Copyright (c) 2014 mimacom a.g.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AutoLoginException;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleAutoLogin implements
		com.liferay.portal.security.auth.AutoLogin {

    @Override
    public String[] handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws AutoLoginException {
        return new String[0]; //TODO
    }

    public String[] login(HttpServletRequest arg0, HttpServletResponse arg1)
			throws AutoLoginException {
		//performm authentication logic and retrieve user to log-in
		User user;
		String[] credentials = new String[3];
		try {
			user = UserLocalServiceUtil.getUserByEmailAddress(
					PortalUtil.getDefaultCompanyId(), "test@liferay.com");
			credentials[0] = String.valueOf(user.getUserId());
			credentials[1] = user.getPassword();
			credentials[2] = Boolean.TRUE.toString();
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//provide Liferay the credentials to login
		return credentials;
	}

}
