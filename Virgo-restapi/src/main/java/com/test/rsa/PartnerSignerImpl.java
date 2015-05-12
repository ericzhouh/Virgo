package com.test.rsa;

/**
 * Created by yangtianhang on 15/5/11.
 */

import org.apache.commons.codec.binary.Base64;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class PartnerSignerImpl {
    public String sign(byte[] data, PrivateKey priKey) {
        try {
            Signature signature = Signature.getInstance("MD5WithRSA");
            signature.initSign(priKey);
            signature.update(data);
            return Base64.encodeBase64URLSafeString(signature.sign());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verify(byte[] data, String sign, PublicKey pubKey) {
        try {
            Signature signature = Signature.getInstance("MD5WithRSA");
            signature.initVerify(pubKey);
            signature.update(data);
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            return false;
        }
    }

}
