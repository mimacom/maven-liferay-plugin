package org.mimacom.liferay.demo.struts;

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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.StrutsPortletAction;
import com.liferay.portlet.blogs.EntryTitleException;

import javax.portlet.*;

public class BlogArticleAddAction extends com.liferay.portal.kernel.struts.BaseStrutsPortletAction {

    private static final String[] BANNED_WORDS = {"beer", "vodka", "alcohol"};

    @Override
    public void processAction(StrutsPortletAction originalStrutsPortletAction,
                              PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {

        String title = actionRequest.getParameter("title").toLowerCase();

        for (String s : BANNED_WORDS) {
            if (title.contains(s)) {
                SessionErrors.add(actionRequest, EntryTitleException.class.getCanonicalName());
                return;
            }
        }

        originalStrutsPortletAction.processAction(originalStrutsPortletAction, portletConfig, actionRequest, actionResponse);
    }

    @Override
    public String render(StrutsPortletAction originalStrutsPortletAction,
                         PortletConfig portletConfig, RenderRequest renderRequest,
                         RenderResponse renderResponse) throws Exception {
        return originalStrutsPortletAction.render(null, portletConfig,
                renderRequest, renderResponse);
    }


    @Override
    public void serveResource(StrutsPortletAction originalStrutsPortletAction,
                              PortletConfig portletConfig, ResourceRequest resourceRequest,
                              ResourceResponse resourceResponse) throws Exception {
        originalStrutsPortletAction.serveResource(originalStrutsPortletAction,
                portletConfig, resourceRequest, resourceResponse);
    }

    Log _log = LogFactoryUtil.getLog(BlogArticleAddAction.class);
}
