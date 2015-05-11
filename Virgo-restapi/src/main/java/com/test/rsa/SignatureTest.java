package com.test.rsa;

/**
 * Created by yangtianhang on 15/5/11.
 */

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;


public class SignatureTest {
    public static void main(String[] args) {


		/*
         *此内容为合租商 生成签名 和 阳光验证前面的方式
		 *反之 阳光会用用过私钥生成签名串    合作商用阳光公钥进行验证
		 */
//        String verifyStr = "验证字符：yanzhengzifu";

        String verifyStr = "<Request><InputsList><Inputs type=\"vehicleInfo\"></Inputs><Inputs type=\"ownerInfo\"></Inputs><Inputs type=\"luxury\"> <input name=\"cov_200\">134400.00</input> <input name=\"cov_600\">50000.00</input> <input name=\"cov_701\">10000.00</input> <input name=\"cov_702\">10000.00</input> <input name=\"cov_500\">69027.84</input> <input name=\"cov_231\">1</input> <input name=\"cov_310\">115046.40</input> <input name=\"cov_210\">2000.00</input> <input name=\"cov_321\">15.00</input> <input name=\"cov_911\">1</input> <input name=\"cov_912\">1</input> <input name=\"cov_928\">1</input> <input name=\"cov_929\">1</input> <input name=\"cov_921\">1</input> <input name=\"cov_922\">0</input> <input name=\"cov_291\">1</input> <input name=\"cov_390\">1</input> <input name=\"cov_640\">10000.00</input> <input name=\"forceFlag\">0</input></Inputs><Inputs type=\"deadline\"> <input name=\"bizBeginDate\">2015-05-12</input> <input name=\"forceBeginDate\">2015-05-12</input></Inputs><Inputs type=\"force\"></Inputs><Inputs type=\"lifeTableCpm\"> <input name=\"presentType\">0</input> <input name=\"presentVal\">0</input> <input name=\"hasAddPresent\">0</input></Inputs><Inputs type=\"runAreaType\"></Inputs><Inputs type=\"driverInfoFlag\"></Inputs></InputsList></Request>";
        //生产签名串 和 验证类
        PartnerSignerImpl signer = new PartnerSignerImpl();

        //得到私钥
        PrivateKey ygPrivate = KeyPairer.getPrivateKey(KeyPairer.PARTNER_PRIVATE_KEY);

        //返回报文签名串  此字符传放入报文的<Sign>标签中
        String sign = "";
        try {
            sign = signer.sign(verifyStr.getBytes("GBK"), ygPrivate);
            System.out.println(sign);
        } catch (UnsupportedEncodingException e) {
        }
        //以上为生成签名串的方法

        //以下为验证发送报文签名一致的方法
        //获取公钥
        PublicKey aliPublicKey = KeyPairer.getPublicKey(KeyPairer.PARTNER_PUBLIC_KEY);
        //校验签名
        boolean signFlag = false;
        try {
            signFlag = signer.verify(verifyStr.getBytes("GBK"), sign, aliPublicKey);
        } catch (UnsupportedEncodingException e1) {
        }

        System.out.println("验证结果:" + signFlag);
    }

}
