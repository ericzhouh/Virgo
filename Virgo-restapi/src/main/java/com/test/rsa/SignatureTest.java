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

        String verifyStr = "<Request><InputsList><Inputs type='insuredInfo'><input name='insuredIdNo'>130429198601173637</input><input name='insuredEmail'>546456567@163.com</input><input name='insuredName'>王振</input><input name='insuredMobile'>13811138111</input></Inputs><Inputs type='applicantInfo'><input name='applicantEmail'>546456567@163.com</input> <input name='applicantIdNo'>130429198601173637</input><input name='applicantMobile'>13811138111</input><input name='applicantName'>王振</input></Inputs><Inputs type='ownerInfo'><input name='ownerName'>王振</input><input name='ownerMobile'>13811138111</input><input name='ownerIdNo'>130429198601173637</input><input name='ownerEmail'>546456567@163.com</input></Inputs><Inputs type='deliverInfo'><input name='sendDate'>2015-05-20</input><input name='addresseeName'>王振</input><input name='addresseeMobile'>13811138111</input><input name='addresseeProvince'>11</input><input name='addresseeCity'>1101</input><input name='addresseeTown'>110100</input><input name='insuredaddresseeDetails'>上海浦东陆家嘴</input><input name='addresseeDetails'>上海浦东陆家嘴</input></Inputs><Inputs type='lifeTablePresent'><input name='preAccountNumbers'></input><input name='pkIdEncryptions'></input><input name='prePhoneNos'></input><input name='prepresentnums'></input></Inputs><Inputs type='CarThroughCity'><input name='carPlace'>0</input></Inputs></InputsList><Order><TBOrderId>order123</TBOrderId><SubOrderList><SubOrder type='force'><TBOrderId>order123</TBOrderId></SubOrder><SubOrder type='biz'><TBOrderId>order123</TBOrderId></SubOrder></SubOrderList></Order></Request>";
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
