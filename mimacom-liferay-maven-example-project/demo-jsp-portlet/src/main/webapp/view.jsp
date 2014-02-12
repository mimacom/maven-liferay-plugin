<%@page import="com.liferay.portal.model.Role"%>
<%@page import="com.liferay.portal.service.RoleLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%
	/**
	 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
	 *
	 * This library is free software; you can redistribute it and/or modify it under
	 * the terms of the GNU Lesser General Public License as published by the Free
	 * Software Foundation; either version 2.1 of the License, or (at your option)
	 * any later version.
	 *
	 * This library is distributed in the hope that it will be useful, but WITHOUT
	 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
	 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
	 * details.
	 */
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
	ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest
			.getAttribute(WebKeys.THEME_DISPLAY);
			 Role role = RoleLocalServiceUtil.getRole( themeDisplay.getCompanyId(),
					 "DemoRole");

	if (themeDisplay.getLayout().getName(themeDisplay.getLocale())
			.equals("Delete Role")) {
%>
<%="Deleting role from user..."%>

<%

RoleLocalServiceUtil.unsetUserRoles(themeDisplay.getRealUserId(), new
		long[] { role.getRoleId() });
	} else {
%>
<%="Adding role to user..."%>
<%
	

RoleLocalServiceUtil.addUserRoles(themeDisplay.getRealUserId(), new
long[] { role.getRoleId() });
	
	}
%>



