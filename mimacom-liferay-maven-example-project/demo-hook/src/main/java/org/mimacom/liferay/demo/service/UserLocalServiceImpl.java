package org.mimacom.liferay.demo.service;

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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Team;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.service.UserLocalService;

public class UserLocalServiceImpl extends
		com.liferay.portal.service.UserLocalServiceWrapper {

	public UserLocalServiceImpl(UserLocalService userLocalService) {
		super(userLocalService);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addTeamUsers(long teamId, long[] userIds)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		super.addTeamUsers(teamId, userIds);

		Team team = TeamLocalServiceUtil.getTeam(teamId);
		_log.info("Assigning users to team :" + team.getName());
	}

	Log _log = LogFactoryUtil.getLog(UserLocalServiceImpl.class);

}
