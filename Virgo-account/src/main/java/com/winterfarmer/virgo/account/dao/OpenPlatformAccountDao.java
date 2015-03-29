package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.OpenPlatformAccount;

/**
 * Created by yangtianhang on 15-3-26.
 */
public interface OpenPlatformAccountDao {
    boolean createOpenPlatformAccount(OpenPlatformAccount openPlatformAccount);

    OpenPlatformAccount retrieveOpenPlatformAccount(long userId, String openId);
}
