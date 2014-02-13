package org.mimacom.liferay.demo.model.impl;

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
