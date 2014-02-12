package org.mimacom.liferay.demo.model.listeners;

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
