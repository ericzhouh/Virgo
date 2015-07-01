package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.Account;
import com.winterfarmer.virgo.account.model.AccountVersion;

import java.util.Map;

/**
 * Created by yangtianhang on 15-3-4.
 */
public interface AccountDao {
    Account retrieveAccount(long userId, boolean fromWrite);

    Long createAccount(String nickName, String hashedPassword, String salt, AccountVersion version, Map<String, Object> extInfo);

    boolean updateAccount(Account account);

    boolean updatePassword(long userId, String password);

    Account retrieveAccountByNickName(String nickName);
}
