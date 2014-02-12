package org.mimacom.liferay.demo.service;

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
