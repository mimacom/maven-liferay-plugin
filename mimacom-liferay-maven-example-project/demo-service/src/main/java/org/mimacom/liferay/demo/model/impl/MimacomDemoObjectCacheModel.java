package org.mimacom.liferay.demo.model.impl;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

import org.mimacom.liferay.demo.model.MimacomDemoObject;

import java.io.Serializable;

import java.util.Date;

/**
 * The cache model class for representing MimacomDemoObject in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see MimacomDemoObject
 * @generated
 */
public class MimacomDemoObjectCacheModel implements CacheModel<MimacomDemoObject>,
    Serializable {
    public long ID;
    public long USERID;
    public String SCREENNAME;
    public String ACTION;
    public String MESSAGE;
    public long CREATEDATE;

    @Override
    public String toString() {
        StringBundler sb = new StringBundler(13);

        sb.append("{ID=");
        sb.append(ID);
        sb.append(", USERID=");
        sb.append(USERID);
        sb.append(", SCREENNAME=");
        sb.append(SCREENNAME);
        sb.append(", ACTION=");
        sb.append(ACTION);
        sb.append(", MESSAGE=");
        sb.append(MESSAGE);
        sb.append(", CREATEDATE=");
        sb.append(CREATEDATE);
        sb.append("}");

        return sb.toString();
    }

    public MimacomDemoObject toEntityModel() {
        MimacomDemoObjectImpl mimacomDemoObjectImpl = new MimacomDemoObjectImpl();

        mimacomDemoObjectImpl.setID(ID);
        mimacomDemoObjectImpl.setUSERID(USERID);

        if (SCREENNAME == null) {
            mimacomDemoObjectImpl.setSCREENNAME(StringPool.BLANK);
        } else {
            mimacomDemoObjectImpl.setSCREENNAME(SCREENNAME);
        }

        if (ACTION == null) {
            mimacomDemoObjectImpl.setACTION(StringPool.BLANK);
        } else {
            mimacomDemoObjectImpl.setACTION(ACTION);
        }

        if (MESSAGE == null) {
            mimacomDemoObjectImpl.setMESSAGE(StringPool.BLANK);
        } else {
            mimacomDemoObjectImpl.setMESSAGE(MESSAGE);
        }

        if (CREATEDATE == Long.MIN_VALUE) {
            mimacomDemoObjectImpl.setCREATEDATE(null);
        } else {
            mimacomDemoObjectImpl.setCREATEDATE(new Date(CREATEDATE));
        }

        mimacomDemoObjectImpl.resetOriginalValues();

        return mimacomDemoObjectImpl;
    }
}
