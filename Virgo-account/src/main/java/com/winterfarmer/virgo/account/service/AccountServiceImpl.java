package com.winterfarmer.virgo.account.service;

import com.google.common.collect.Lists;
import com.winterfarmer.virgo.account.dao.*;
import com.winterfarmer.virgo.account.model.*;
import com.winterfarmer.virgo.base.Exception.MobileNumberException;
import com.winterfarmer.virgo.base.Exception.UnexpectedVirgoException;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.base.service.IdService;
import com.winterfarmer.virgo.common.util.AccountUtil;
import com.winterfarmer.virgo.common.util.Base36Util;
import com.winterfarmer.virgo.common.util.Base62Util;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.storage.graph.Edge;
import com.winterfarmer.virgo.storage.graph.dao.GraphDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yangtianhang on 15-3-25.
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Resource(name = "idService")
    IdService idService;

    @Resource(name = "accountMysqlDao")
    AccountDao accountMysqlDao;

    @Resource(name = "accessTokenMysqlDao")
    AccessTokenDao accessTokenDao;

    @Resource(name = "openPlatformAccountMysqlDao")
    OpenPlatformAccountDao openPlatformAccountDao;

    @Resource(name = "accountRedisDao")
    AccountRedisDao accountRedisDao;

    @Resource(name = "privilegeMysqlDao")
    PrivilegeDao privilegeDao;

    @Resource(name = "userInfoHybridDao")
    UserInfoDao userInfoHybridDao;

    @Resource(name = "applyExpertMysqlDao")
    ApplyExpertMysqlDao applyExpertMysqlDao;

    @Resource(name = "userApplyExpertTagGraphMysqlDao")
    GraphDao userApplyExpertTagGraphMysqlDao;

    @Resource(name = "userFollowTagGraphMysqlDao")
    GraphDao userFollowTagGraphMysqlDao;

    private SecureRandom secureRandom;

    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final int SECURE_RANDOM_BYTES_LENGTH = 32;
    private static final int CACHE_SIGNUP_MOBILE_REQUEST_EXPIRE_S = 60; // seconds

    @PostConstruct
    void init() throws NoSuchAlgorithmException {
        try {
            secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            secureRandom.setSeed(System.currentTimeMillis());
        } catch (NoSuchAlgorithmException e) {
            VirgoLogger.fatal("No SECURE_RANDOM_ALGORITHM: {}", SECURE_RANDOM_ALGORITHM);
        }
    }

    @Override
    public Account getAccount(long userId) {
        return accountMysqlDao.retrieveAccount(userId, false);
    }

    @Override
    public List<Account> getAccounts(long... userIds) {
        List<Account> accounts = Lists.newArrayList();
        for (long userId : userIds) {
            accounts.add(getAccount(userId));
        }

        return accounts;
    }

    @Override
    public boolean isNickNameExisted(String nickName) {
        Account account = accountMysqlDao.retrieveAccountByNickName(nickName);
        return account != null;
    }

    @Override
    public boolean isMobileExisted(String mobileNumber) throws MobileNumberException {
        String formattedMobileNumber = AccountUtil.formatMobile(mobileNumber);
        if (formattedMobileNumber == null) {
            throw new MobileNumberException("Invalid mobile number: " + mobileNumber);
        }

        OpenPlatformAccount account = openPlatformAccountDao.retrieveOpenPlatformAccount(formattedMobileNumber, PlatformType.MOBILE);
        return account != null;
    }

    @Override
    public String getHashedPassword(String password, String salt) {
        return shaPassword(password, salt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccessToken signUpByMobile(String mobileNumber, String password, String nickName, String openToken, int appKey) throws MobileNumberException {
        String formattedMobileNumber = AccountUtil.formatMobile(mobileNumber);
        if (formattedMobileNumber == null) {
            throw new MobileNumberException("Invalid mobile number: " + mobileNumber);
        }

        long userId = signUp(nickName, password);
        AccessToken accessToken = createAccessToken(userId, appKey);
        insertOpenPlatformAccount(userId, formattedMobileNumber, PlatformType.MOBILE, openToken);
        createUserInfo(userId, nickName);

        return accessToken;
    }

    @Override
    public AccessToken getAccessToken(String tokenString) {
        if (!isValidTokenPattern(tokenString)) {
            return null;
        }

        long userId = getUserIdFromTokenString(tokenString);
        int appKey = (int) getUserAppKeyFromTokenString(tokenString);
        if (!AppKey.isValidAppKey(appKey)) {
            return null;
        }

        AccessToken accessToken = getAccessToken(userId, appKey);
        if (accessToken != null && StringUtils.equals(accessToken.getToken(), tokenString)) {
            return accessToken;
        } else {
            return null;
        }
    }

    @Override
    public AccessToken getAccessToken(long userId, int appKey) {
        AccessToken accessToken = accountRedisDao.getAccessToken(userId, appKey);
        if (accessToken == null) {
            accessToken = accessTokenDao.retrieveAccessToken(userId, appKey);
            if (accessToken != null && !accessToken.isExpire()) {
                accountRedisDao.insertAccessToken(accessToken);
            } else {
                accessToken = null;
            }
        }

        return accessToken;
    }

    @Override
    public OpenPlatformAccount getOpenPlatformAccount(String openId, PlatformType platformType) {
        return openPlatformAccountDao.retrieveOpenPlatformAccount(openId, platformType);
    }

    @Override
    public void cacheSentSignUpMobileVerificationCode(String mobileNumber) {
        accountRedisDao.cacheSignUpMobileRequest(mobileNumber, CACHE_SIGNUP_MOBILE_REQUEST_EXPIRE_S);
    }

    @Override
    public boolean isRequestSignUpMobileVerificationCodeTooFrequently(String mobileNumber) {
        return accountRedisDao.getSignUpMobileRequest(mobileNumber) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void resetPassword(Account account, String password) throws UnexpectedVirgoException {
        // 重设密码
        String hashedPassword = getHashedPassword(password, account.getSalt());
        account.setHashedPassword(hashedPassword);
        if (!accountMysqlDao.updatePassword(account.getUserId(), account.getHashedPassword())) {
            throw new UnexpectedVirgoException("updateApplyingExpert user(" + account.getUserId() + ")  failed: ");
        }
        // 删除所有相关token
        accessTokenDao.deleteAccessToken(account.getUserId());
        for (AppKey appKey : AppKey.values()) {
            accountRedisDao.deleteAccessToken(account.getUserId(), appKey.getIndex());
        }
    }

    @Override
    public boolean deleteAccessToken(long userId, int appKey) {
        accessTokenDao.deleteAccessToken(userId, appKey);
        accountRedisDao.deleteAccessToken(userId, appKey);
        return true;
    }

    @Override
    public AccessToken createAccessToken(long userId, int appKey) {
        AccessToken accessToken = generateToken(userId, appKey);
        insertAccessToken(accessToken);
        return accessToken;
    }

    @Override
    public Long getUserId(String openId, PlatformType platformType) {
        switch (platformType) {
            case MOBILE:
                return getUserIdByMobile(openId);
            default:
                return null;
        }
    }

    /**
     * 生成随机的用户名，规则:
     * 机器标示字符+毫秒距离(base)
     *
     * @return
     */
    @Override
    public String getRandomNickName() {
        return "车主" + Base36Util.encode(System.currentTimeMillis());
    }

    @Override
    public UserInfo getUserInfo(long userId) {
        return userInfoHybridDao.retrieveUser(userId, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public UserInfo updateUserInfo(UserInfo userInfo) {
        Account account = getAccount(userInfo.getUserId());
        if (account == null) {
            VirgoLogger.error("account " + userInfo.getUserId() + " not existed!");
            throw new RuntimeException("account " + userInfo.getUserId() + " not existed!");
        }
        if (!StringUtils.equals(account.getNickName(), userInfo.getNickName())) {
            account.setNickName(userInfo.getNickName());
            accountMysqlDao.updateAccount(account);
        }

        userInfoHybridDao.update(userInfo);
        return userInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Pair<UserInfo, ExpertApplying> updateUserInfoAndApplyExpert(UserInfo userInfo, String reason, long[] tagIds) throws UnexpectedVirgoException {
        // 1. 检查是否有申请
        ExpertApplying expertApplying = applyExpertMysqlDao.retrieveLastByUserId(userInfo.getUserId());
        if (expertApplying != null && expertApplying.getState() == ExpertApplying.APPLYING) {
            throw new UnexpectedVirgoException("user " + userInfo.getUserId() + " has already been applying.");
        }

        userInfo = updateUserInfo(userInfo);
        if (userApplyExpertTagGraphMysqlDao.insertOrUpdateEdges(Edge.createEdges(userInfo.getUserId(), tagIds)) < 1) {
            throw new UnexpectedVirgoException("insert user apply expert tag failed.");
        }

        expertApplying = applyExpertMysqlDao.create(userInfo.getUserId(), reason, System.currentTimeMillis(), ExpertApplying.APPLYING);
        return Pair.of(userInfo, expertApplying);
    }

    @Override
    public void updateApplyingExpertTags(UserInfo userInfo, long[] tagIds) throws UnexpectedVirgoException {
        ExpertApplying expertApplying = applyExpertMysqlDao.retrieveLastByUserId(userInfo.getUserId());
        if (expertApplying != null && expertApplying.getState() == ExpertApplying.APPLYING) {
            List<Edge> edgeList = userApplyExpertTagGraphMysqlDao.queryEdgesByHead(userInfo.getUserId(), 100, 0);
            for (Edge edge : edgeList) {
                edge.setState(Edge.DELETED_EDGE);
            }
            if (userApplyExpertTagGraphMysqlDao.insertOrUpdateEdges(edgeList) < 1) {
                throw new UnexpectedVirgoException("delete user apply expert old tag failed.");
            }
            if (userApplyExpertTagGraphMysqlDao.insertOrUpdateEdges(Edge.createEdges(userInfo.getUserId(), tagIds)) < 1) {
                throw new UnexpectedVirgoException("insert user apply expert tag failed.");
            }
        } else {
            throw new UnexpectedVirgoException("user " + userInfo.getUserId() + " has no applying.");
        }
    }

    @Override
    public boolean updateApplyingExpertReason(UserInfo userInfo, String reason) throws UnexpectedVirgoException {
        ExpertApplying applying = applyExpertMysqlDao.retrieveLastByUserId(userInfo.getUserId());
        if (applying != null && applying.getState() == ExpertApplying.APPLYING) {
            return applyExpertMysqlDao.updateApplyingExpert(userInfo.getUserId(), reason, applying.getState());
        } else {
            throw new UnexpectedVirgoException("user " + userInfo.getUserId() + " has no applying.");
        }
    }

    @Override
    public ExpertApplying getExpertApplying(long userId) {
        return applyExpertMysqlDao.retrieveLastByUserId(userId);
    }

    @Override
    public boolean followTag(long userId, long tagId, CommonState state) {
        return userFollowTagGraphMysqlDao.insertOrUpdateEdges(new Edge(userId, tagId, state.getIndex())) > 0;
    }

    @Override
    public List<Long> listFollowTags(long userId) {
        List<Edge> edgeList = userFollowTagGraphMysqlDao.queryEdgesByHead(userId, 100, 0);
        return Edge.listTails(edgeList);
    }

    @Override
    public List<Long> getExpertTags(long userId) {
        List<Edge> edgeList = userApplyExpertTagGraphMysqlDao.queryEdgesByHead(userId, 100, 0);
        return Edge.listTails(edgeList);
    }

    private Long getUserIdByMobile(String mobile) {
        Long userId = accountRedisDao.getUserIdByMobile(mobile);
        if (userId != null) {
            return userId;
        }

        OpenPlatformAccount openPlatformAccount = openPlatformAccountDao.retrieveOpenPlatformAccount(mobile, PlatformType.MOBILE);
        if (openPlatformAccount != null) {
            userId = openPlatformAccount.getUserId();
            accountRedisDao.setMobileUserId(mobile, userId);
            return userId;
        }

        return null;
    }

    private boolean isValidTokenPattern(String tokenString) {
        return isValidTokenLength(tokenString) && isTokenStringStartWithPrefix(tokenString);
    }

    private boolean isValidTokenLength(String tokenString) {
        return StringUtils.length(tokenString) == TOKEN_LENGTH;
    }

    private boolean isTokenStringStartWithPrefix(String tokenString) {
        return StringUtils.startsWith(tokenString, TOKEN_PREFIX);
    }

    private long getUserIdFromTokenString(String tokenString) {
        return Base62Util.decode(StringUtils.substring(tokenString, TOKEN_USER_ID_INDEX, TOKEN_USER_ID_END_INDEX_EXCLUDE));
    }

    private long getUserAppKeyFromTokenString(String tokenString) {
        return Base62Util.decode(StringUtils.substring(tokenString, TOKEN_APP_KEY_INDEX, TOKEN_APP_KEY_END_INDEX_EXCLUDE));
    }

    private void insertAccessToken(AccessToken accessToken) {
        if (!accessTokenDao.createAccessToken(accessToken.getUserId(), accessToken.getAppKey(), accessToken.getToken(), accessToken.getExpireAt())) {
            VirgoLogger.error("Insert access token to db failed! userId:{}, appKey:{}", accessToken.getUserId(), accessToken.getAppKey());
            throw new RuntimeException("insert access token to db failed!");
        }

        accountRedisDao.insertAccessToken(accessToken);
    }

    private void insertOpenPlatformAccount(long userId, String openId, PlatformType platformType, String openToken) {
        OpenPlatformAccount openPlatformAccount = new OpenPlatformAccount(userId, openId, platformType, openToken, CommonState.NORMAL);
        if (!openPlatformAccountDao.createOpenPlatformAccount(openPlatformAccount)) {
            VirgoLogger.error("Insert access token to db failed! userId:{}, openId:{}, platformType:{}, openToken:",
                    userId, openId, platformType, openToken);
            throw new RuntimeException("insert open platform account to db failed!");
        }
    }

    private long signUp(String nickName, String password) {
        long userId = idService.getId();
        String salt = generateSalt();
        String hashedPassword = getHashedPassword(password, salt);

        if (accountMysqlDao.createAccount(userId, nickName, hashedPassword, salt, AccountVersion.SALT_SHA256, null)) {
            return userId;
        } else {
            VirgoLogger.error("Create new account into db failed!");
            throw new RuntimeException("Create new account into db failed!");
        }
    }

    private void createUserInfo(long userId, String nickName) {
        UserInfo userInfo = new UserInfo(userId, nickName);
        boolean successful = userInfoHybridDao.create(userInfo);
        if (!successful) {
            throw new RuntimeException("Create new user info into db failed!");
        }
    }

    private String generateSalt() {
        return generateRandomString(SECURE_RANDOM_BYTES_LENGTH);
    }

    private String generateRandomString(int numBytes) {
        byte[] bytes = new byte[numBytes];
        secureRandom.nextBytes(bytes);
        return Base62Util.convert(bytes);
    }

    // shaPassword = sha256(sha256(password) + salt)
    private String shaPassword(String password, String salt) {
        String sha256Password = DigestUtils.sha256Hex(password);
        String sha256PasswordWithSalt = sha256Password + salt;
        return DigestUtils.sha256Hex(sha256PasswordWithSalt);
    }

    private static final String TOKEN_PREFIX = "0.";

    private static final int TOKEN_PREFIX_LENGTH = TOKEN_PREFIX.length();

    private static final int TOKEN_USER_ID_INDEX = TOKEN_PREFIX_LENGTH;

    private static final int TOKEN_USER_ID_LENGTH = Base62Util.MAX_LONG_ENCODE_LENGTH;

    private static final int TOKEN_USER_ID_END_INDEX_EXCLUDE = TOKEN_USER_ID_INDEX + TOKEN_USER_ID_LENGTH;

    private static final int TOKEN_APP_KEY_INDEX = TOKEN_USER_ID_END_INDEX_EXCLUDE;

    private static final int TOKEN_APP_KEY_LENGTH = 3;

    private static final int TOKEN_APP_KEY_END_INDEX_EXCLUDE = TOKEN_APP_KEY_INDEX + TOKEN_APP_KEY_LENGTH;

    private static final int TOKEN_SUFFIX_LENGTH = 16;

    private static final int TOKEN_LENGTH = TOKEN_PREFIX_LENGTH + TOKEN_USER_ID_LENGTH + TOKEN_APP_KEY_LENGTH + TOKEN_SUFFIX_LENGTH;

    private AccessToken generateToken(long id, int appKey) {
        AccessToken token = new AccessToken();
        token.setUserId(id);
        token.setAppKey(appKey);
        token.setToken(generateTokenString(id, appKey));
        token.setExpireAt(getExpireAtTs());

        return token;
    }

    private long getExpireAtTs() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.YEAR, 1);
        return calendar.getTimeInMillis();
    }

    // Token = TOKEN_PREFIX + encode(id, 11) + encode(appKey,3) + token_suffix
    // length = 2 + 11 + 3 + 16 = 32
    private String generateTokenString(long id, int appKey) {
        return TOKEN_PREFIX + Base62Util.encodeMaxBytes(id) + Base62Util.encode(appKey, TOKEN_APP_KEY_LENGTH) + tokenSuffix();
    }

    private String tokenSuffix() {
        return generateRandomString(TOKEN_SUFFIX_LENGTH);
    }
}
