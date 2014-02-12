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
 *
 *
 */
--%>

<%@ include file="/html/portlet/search/init.jsp" %>

<%
    String redirect = ParamUtil.getString(request, "redirect");

    if (Validator.isNotNull(redirect)) {
        portletDisplay.setURLBack(redirect);
    }

    String primarySearch = ParamUtil.getString(request, "primarySearch");

    if (Validator.isNotNull(primarySearch)) {
        portalPreferences.setValue(PortletKeys.SEARCH, "primary-search", primarySearch);
    }
    else {
        primarySearch = portalPreferences.getValue(PortletKeys.SEARCH, "primary-search", StringPool.BLANK);
    }

    long groupId = ParamUtil.getLong(request, "groupId");

    Group group = themeDisplay.getScopeGroup();

    String keywords = ParamUtil.getString(request, "keywords");

    String format = ParamUtil.getString(request, "format");

    List<String> portletTitles = new ArrayList<String>();

    PortletURL portletURL = renderResponse.createRenderURL();

    portletURL.setParameter("struts_action", "/search/search");
    portletURL.setParameter("groupId", String.valueOf(groupId));
    portletURL.setParameter("keywords", keywords);
    portletURL.setParameter("format", format);

    request.setAttribute("search.jsp-portletURL", portletURL);
%>

<liferay-portlet:renderURL varImpl="searchURL">
    <portlet:param name="struts_action" value="/search/search" />
</liferay-portlet:renderURL>

<aui:form action="<%= searchURL %>" method="get" name="fm" onSubmit='<%= "event.preventDefault();" %>'>
    <liferay-portlet:renderURLParams varImpl="searchURL" />
    <aui:input name="<%= SearchContainer.DEFAULT_CUR_PARAM %>" type="hidden" value="<%= ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_CUR) %>" />
    <aui:input name="format" type="hidden" value="<%= format %>" />

    <aui:fieldset>
        <div id="autoComplete">
            <aui:input inlineField="<%= true %>"  label="" name="keywords" size="30" value="<%= keywords %>" />
        </div>

        <aui:select inlineField="<%= true %>" label="" name="groupId">
            <aui:option label="everything" selected="<%= groupId == 0 %>" value="0" />
            <aui:option label='<%= "this-" + (group.isOrganization() ? "organization" : "site") %>' selected="<%= groupId != 0 %>" value="<%= group.getGroupId() %>" />
        </aui:select>

        <aui:button align="absmiddle" border="0" name="search" onClick='<%= renderResponse.getNamespace() + "search();" %>' src='<%= themeDisplay.getPathThemeImages() + "/common/search.png" %>' title="search" type="image" />

        <portlet:renderURL copyCurrentRenderParameters="<%= false %>" var="clearSearchURL">
            <portlet:param name="groupId" value="0" />
        </portlet:renderURL>

        <aui:button align="absmiddle" border="0" href="<%= clearSearchURL %>" name="clear-search" src='<%= themeDisplay.getPathThemeImages() + "/common/close.png" %>' title="clear-search" type="image" />
    </aui:fieldset>

    <div class="lfr-token-list" id="<portlet:namespace />searchTokens">
        <div class="lfr-token-list-content" id="<portlet:namespace />searchTokensContent"></div>
    </div>

    <aui:script use="liferay-token-list">
        Liferay.namespace('Search').tokenList = new Liferay.TokenList(
        {
        after: {
        close: function(event) {
        var item = event.item;

        var fieldValues = item.attr('data-fieldValues').split();

        A.Array.each(
        fieldValues,
        function(item, index, collection) {
        var values = item.split('|');

        var field = A.one('#' + values[0]);

        if (field) {
        field.val(values[1]);
        }
        }
        );

        var clearFields = A.all('#' + event.item.attr('data-clearFields').split().join(',#'));

        clearFields.remove();

        if (fieldValues.length || clearFields.size()) {
        submitForm(document.<portlet:namespace />fm);
        }
        }
        },
        boundingBox: '#<portlet:namespace />searchTokens',
        contentBox: '#<portlet:namespace />searchTokensContent'
        }
        ).render();
    </aui:script>

    <%@ include file="/html/portlet/search/main_search.jspf" %>

    <c:if test="<%= displayOpenSearchResults %>">
        <liferay-ui:panel collapsible="<%= true %>" cssClass="open-search-panel" extended="<%= true %>" id="searchOpenSearchPanelContainer" persistState="<%= true %>" title="open-search">
            <%@ include file="/html/portlet/search/open_search.jspf" %>
        </liferay-ui:panel>
    </c:if>
</aui:form>

<aui:script use="aui-base">
    var pageLinks = A.one('.portlet-search .result .page-links');

    if (pageLinks) {
    pageLinks.delegate(
    'click',
    function(event) {
    document.<portlet:namespace />fm.<portlet:namespace /><%= SearchContainer.DEFAULT_CUR_PARAM %>.value = 1;

    submitForm(document.<portlet:namespace />fm);

    event.preventDefault();
    },
    'a.first'
    );

    pageLinks.delegate(
    'click',
    function(event) {
    document.<portlet:namespace />fm.<portlet:namespace /><%= SearchContainer.DEFAULT_CUR_PARAM %>.value = parseInt(document.<portlet:namespace />fm.<portlet:namespace /><%= SearchContainer.DEFAULT_CUR_PARAM %>.value) - 1;

    submitForm(document.<portlet:namespace />fm);

    event.preventDefault();
    },
    'a.previous'
    );

    pageLinks.delegate(
    'click',
    function(event) {
    document.<portlet:namespace />fm.<portlet:namespace /><%= SearchContainer.DEFAULT_CUR_PARAM %>.value = parseInt(document.<portlet:namespace />fm.<portlet:namespace /><%= SearchContainer.DEFAULT_CUR_PARAM %>.value) + 1;

    submitForm(document.<portlet:namespace />fm);

    event.preventDefault();
    },
    'a.next'
    );
    }

    var resultsGrid = A.one('.portlet-search .result .results-grid');

    if (resultsGrid) {
    resultsGrid.delegate(
    'click',
    function(event) {
    var handle = event.currentTarget;
    var rowTD = handle.ancestor('.results-row td');

    var documentFields = rowTD.one('.asset-entry .asset-entry-fields');

    if (handle.text() == '[+]') {
    documentFields.show();
    handle.text('[-]');
    }
    else if (handle.text() == '[-]') {
    documentFields.hide();
    handle.text('[+]');
    }
    },
    '.results-row td .asset-entry .toggle-details'
    );
    }

    Liferay.provide(
    window,
    '<portlet:namespace />addSearchProvider',
    function() {
    window.external.AddSearchProvider("<%= themeDisplay.getPortalURL() %><%= PortalUtil.getPathMain() %>/search/open_search_description.xml?p_l_id=<%= themeDisplay.getPlid() %>&groupId=<%= groupId %>");
    },
    ['aui-base']
    );

    Liferay.provide(
    window,
    '<portlet:namespace />search',
    function() {
    var keywords = document.<portlet:namespace />fm.<portlet:namespace />keywords.value;

    keywords = keywords.replace(/^\s+|\s+$/, '');

    if (keywords != '') {
    submitForm(document.<portlet:namespace />fm);
    }
    },
    ['aui-base']
    );

    <c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
        Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />keywords);
    </c:if>
</aui:script>

<%
    String pageSubtitle = LanguageUtil.get(pageContext, "search-results");
    String pageDescription = LanguageUtil.get(pageContext, "search-results");
    String pageKeywords = LanguageUtil.get(pageContext, "search");

    if (!portletTitles.isEmpty()) {
        pageDescription = LanguageUtil.get(pageContext, "searched") + StringPool.SPACE + StringUtil.merge(portletTitles, StringPool.COMMA_AND_SPACE);
    }

    if (Validator.isNotNull(keywords)) {
        pageKeywords = keywords;

        if (StringUtil.startsWith(pageKeywords, Field.ASSET_TAG_NAMES + StringPool.COLON)) {
            pageKeywords = StringUtil.replace(pageKeywords, Field.ASSET_TAG_NAMES + StringPool.COLON, StringPool.BLANK);
        }
    }

    PortalUtil.setPageSubtitle(pageSubtitle, request);
    PortalUtil.setPageDescription(pageDescription, request);
    PortalUtil.setPageKeywords(pageKeywords, request);
%>


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
            input: '#autoComplete input',
            contentBox: '#autoComplete'
        } );

        AC.render();

    });

</aui:script>


<%!
    private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.search.search_jsp");
%>