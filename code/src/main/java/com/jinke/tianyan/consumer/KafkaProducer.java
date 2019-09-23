package com.jinke.tianyan.consumer;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生产者
 * 使用@EnableScheduling注解开启定时任务
 */
@RestController
public class KafkaProducer {
    @SuppressWarnings("rawtypes")
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 定时任务
     */
    @SuppressWarnings({ "unchecked" })
    @RequestMapping("sendmsg")
    public String send1(HttpServletRequest request){
	String type = request.getParameter("type");
	
        String message = "{\"eventId\":\"222222222333\",\"params\":{\"education\":\"本科\",\"entName\":\"皮包公司\",\"sellCreditId\":\"12345678\",\"idCard\":\"211322199410250316\",\"linkMan2Phone\":\"13264287896\",\"contactsPhone2\":\"13264287896\",\"compName\":\"皮包公司\",\"contactsPhone1\":\"13644201485\",\"creditType\":\"个人\",\"sellAddressCompCode\":\"11211\",\"arrivalTime\":1559092973273,\"linkMan1Phone\":\"13644201485\",\"callbackUrl\":\"http://baidu.com\",\"applyTime\":\"20190425\",\"creditNoKey\":\"w_001\",\"homeAddress\":\"北京市天安门故宫\",\"purchaseFrequency\":\"经常购买\",\"addressCompCode\":\"211322\",\"childNum\":1,\"sellName\":\"chai\",\"linkMan2Name\":\"小王\",\"linkMan2Relation\":\"朋友\",\"ip\":\"127.0.0.1\",\"creditApplyNo\":\"werwer313\",\"sync\":false,\"entAddress\":\"兰考\",\"phone\":\"15689567485\",\"linkMan1Name\":\"小李子\",\"name\":\"上官婉儿\",\"contactsName1\":\"小李子\",\"contactsName2\":\"小王\",\"relation2\":\"朋友\",\"relation1\":\"同学\",\"maritalStatus\":\"已婚\",\"addressComp\":\"兰考\",\"idOfCore\":\"w_001\",\"supplierCode\":\"5643534543543\",\"applyAmount\":5000,\"riskId\":\"6882cf74fa5c4ab09a26974a5afb4eab\",\"creditId\":\"1111111111111111\",\"creditCode\":\"ewr34423423423\",\"marriage\":\"已婚\",\"childStatus\":1,\"sellCompName\":\"中国石油\",\"platformName\":\"厦门国际银行20190528-3\",\"cooperationPeriod\":\"二年\",\"email\":\"ggggg@qq.com\",\"supplierName\":\"中国烟草\",\"addressCode\":\"4563\",\"address\":\"北京市天安门故宫\",\"creditApplyNoXib\":\"234rewrwer23423423\",\"sellAddressComp\":\"东明\",\"creditIdType\":\"上市公司\",\"wechat\":\"325799684445\",\"mobile\":\"15689567485\",\"linkMan1Relation\":\"同学\",\"certId\":\"211322199410250316\",\"platformId\":\"w_001\",\"applyDate\":\"20190425\",\"creditName\":\"个人\"}}\001xiaguohang_first_loan";
        
        if (type!=null && "2".equals(type)) {

            message = "{\"eventId\":\"222222222\",\"params\":{\"bankAccount\":\"12121\",\"supplierName\":\"gongyingshangbianma2\",\"openingBankName\":\"北京银行-往事寻思寻思新思想\",\"creditApplyNoXib\":\"w_001\",\"orderApplyNo\":\"w_001\",\"billList\":[{\"orderType\":\"order\",\"amount\":10.300000000000000710542735760100185871124267578125,\"orderEndDate\":1559100438552,\"billNo\":\"12345\",\"orderDate\":1560222109865},{\"orderType\":\"order\",\"amount\":20.300000000000000710542735760100185871124267578125,\"orderEndDate\":1559100438552,\"billNo\":\"12345\",\"orderDate\":1560222109865}],\"ip\":\"127.0.0.1\",\"platformId\":\"w_001\",\"supplierCode\":\"gongyingshang1\",\"applyterm\":\"3\",\"sync\":false,\"applyAmt\":100.2999999999999971578290569595992565155029296875,\"riskId\":\"6882cf74fa5c4ab09a26974a5afb4eab\",\"loanpurpose\":\"扩大经营\",\"fiduciaryAccount\":\"wowowo\",\"arrivalTime\":1559100446498,\"callbackUrl\":\"www.baidu.com\",\"creditRiskId\":\"6882cf74fa5c4ab09a26974a5afb4eab\",\"platformName\":\"厦门国际银行5\"}}\001xiaguohang";
	}
        kafkaTemplate.send("xib_credit_apply_info", message);
        return new Date()+" send-消息发送成功：" + message;
    }
    
    /**
     * 定时任务
     */
    @SuppressWarnings({ "unchecked" })
    @RequestMapping("sendmsg1")
    public String send2(HttpServletRequest request){
	
        String message = "{\"tblperRequestBizId\":\"262c15688006af89\",\"returnMsg\":\"SUCCESS\",\"taskType\":\"wlflow\",\"creditApplyNoXib\":\"234rewrwer23423423\",\"returnStatus\":\"1\",\"creditRiskId\":\"9206000413414a6484353b2edb52917d\",\"platformId\":\"3423423423423423\",\"creditNoKey\":\"w_001\"}";
        
        kafkaTemplate.send("xib_credit_report22", message);
        return new Date()+" send-消息发送成功：" + message;
    }

   

}