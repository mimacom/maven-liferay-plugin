package org.mimacom.liferay.demo.model.listeners;

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

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portlet.journal.model.JournalArticle;


public class WebContentListener extends BaseModelListener<JournalArticle> {

    @Override
    public void onAfterRemove(JournalArticle article) throws ModelListenerException {
        logChange(article);
        super.onAfterRemove(article);
    }

    @Override
    public void onAfterUpdate(JournalArticle article) throws ModelListenerException {
        logChange(article);
        super.onAfterUpdate(article);
    }

    private void logChange(JournalArticle article) {

        _log.info("article " + article.getArticleId()
                + " has been changed by " + article.getUserName() + " - with id: " + article.getUserId() );
    }

    Log _log = LogFactoryUtil.getLog(WebContentListener.class);
}
