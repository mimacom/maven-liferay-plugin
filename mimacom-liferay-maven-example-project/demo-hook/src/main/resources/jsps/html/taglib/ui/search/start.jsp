<%--
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *

--%>

<%@ include file="/html/taglib/ui/search/init.jsp" %>

<%
    long groupId = ParamUtil.getLong(request, namespace + "groupId");

    Group group = themeDisplay.getScopeGroup();

    String keywords = ParamUtil.getString(request, namespace + "keywords");

    PortletURL portletURL = null;

    if (portletResponse != null) {
        LiferayPortletResponse liferayPortletResponse = (LiferayPortletResponse)portletResponse;

        portletURL = liferayPortletResponse.createLiferayPortletURL(PortletKeys.SEARCH, PortletRequest.RENDER_PHASE);
    }
    else {
        portletURL = new PortletURLImpl(request, PortletKeys.SEARCH, plid, PortletRequest.RENDER_PHASE);
    }

    portletURL.setWindowState(WindowState.MAXIMIZED);
    portletURL.setPortletMode(PortletMode.VIEW);

    portletURL.setParameter("struts_action", "/search/search");
    portletURL.setParameter("redirect", currentURL);

    pageContext.setAttribute("portletURL", portletURL);
%>

<form action="<%= portletURL.toString() %>" method="get" name="<%= randomNamespace %><%= namespace %>fm" onSubmit="<%= randomNamespace %><%= namespace %>search(); return false;">
    <liferay-portlet:renderURLParams varImpl="portletURL" />

    <div id="autoComplete">
        <input id="myAutoComplete" name="<%= namespace %>keywords" size="30" type="text" value="<%= HtmlUtil.escapeAttribute(keywords) %>"  />
    </div>

    <select name="<%= namespace %>groupId">
        <option value="0" <%= (groupId == 0) ? "selected" : "" %>><liferay-ui:message key="everything" /></option>
        <option value="<%= group.getGroupId() %>" <%= (groupId != 0) ? "selected" : "" %>><liferay-ui:message key='<%= "this-" + (group.isOrganization() ? "organization" : "site") %>' /></option>
    </select>

    <input align="absmiddle" border="0" src="<%= themeDisplay.getPathThemeImages() %>/common/search.png" title="<liferay-ui:message key="search" />" type="image" />

    <aui:script>
        function <%= randomNamespace %><%= namespace %>search() {
        var keywords = document.<%= randomNamespace %><%= namespace %>fm.<%= namespace %>keywords.value;

        keywords = keywords.replace(/^\s+|\s+$/, '');

        if (keywords != '') {
            document.<%= randomNamespace %><%= namespace %>fm.submit();
            }
        }
    </aui:script>

    <aui:script >

        AUI().use('aui-autocomplete', 'aui-editable', 'aui-toolbar', 'aui-panel', 'aui-sortable','aui-calendar-base', function(A) {

            var states = [
                ['AL', 'Alabama', 'The Heart of Dixie'],
                ['AK', 'Alaska', 'The Land of the Midnight Sun'],
                ['AZ', 'Arizona', 'The Grand Canyon State'],
                ['AR', 'Arkansas', 'The Natural State'],
                ['CA', 'California', 'The Golden State'],
                ['CO', 'Colorado', 'The Mountain State'],
                ['CT', 'Connecticut', 'The Constitution State'],
                ['DE', 'Delaware', 'The First State'],
                ['DC', 'District of Columbia', "The Nation's Capital"],
                ['FL', 'Florida', 'The Sunshine State']
            ];

            window.AC = new A.AutoComplete( {
                dataSource: states,
                schema: {
                    resultFields: ['key', 'name', 'description']
                },
                matchKey: 'name',
                typeAhead: true,
                input: '#myAutoComplete',
                contentBox: '#autoComplete'
            } );

            AC.render();

        });


    </aui:script>