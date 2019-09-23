package cn.jk.task

import cn.jk.util.{KafkaProducerUtil, Save_To_Mysql}
import com.alibaba.fastjson.{JSON, JSONObject}

import scala.collection.mutable

/**
  * Created by YiZe on 2019/5/30.
  *
  * @todo 授信申请和复贷申请
  */
object CreditApplyTask {
  def doCreditApply(topic: String, value: String): Unit = {
    var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
    println(value + "\001" + topic)
    println("开始往xib_credit_apply_info中发送数据")
    KafkaProducerUtil.sendSync("xib_credit_apply_info", value + "\001" + topic)
    var platform_id, platform_name, credit_apply_no_xib, id_of_core, credit_apply_no, credit_no_key, credit_type, credit_name,
    credit_id_type, credit_id, supplier_name, supplier_code, cooperation_period, purchase_frequency, names, phone, cert_id, marriage,
    child_num, education, wechat, address, address_code, email, contacts_name1, contacts_phone1, relation1, contacts_name2, contacts_phone2,
    relation2, apply_amount, apply_date, comp_name, credit_code, address_comp, address_comp_code, sell_comp_name, sell_credit_id, sell_address_comp,
    sell_address_comp_code, sell_name, callback_url, risk_id, types = ""
    println("结束往xib_credit_apply_info中发送数据")
    //首次申请、复贷申请
    if (null != topic && (topic.equals("xiaguohang_first_loan") || topic.equals("xiaguohang_repeatloan_send"))) {

      //0 首次授信   //1 复贷申请
      if (topic.equals("xiaguohang_first_loan")) {
        types = "0"
      } else {
        types = "1"
      }
      val jsons: JSONObject = JSON.parseObject(value)
      println("jsons----------------------------"+jsons)
      if (jsons.containsKey("params")) {
        val params: String = jsons.getString("params")
        val json: JSONObject = JSON.parseObject(params)
        if (json.containsKey("platformId")) platform_id = json.getString("platformId")
        if (json.containsKey("platformName")) platform_name = json.getString("platformName")
        if (json.containsKey("creditApplyNoXib")) credit_apply_no_xib = json.getString("creditApplyNoXib")
        if (json.containsKey("idOfCore")) id_of_core = json.getString("idOfCore")
        if (json.containsKey("creditApplyNo")) credit_apply_no = json.getString("creditApplyNo")
        if (json.containsKey("creditNoKey")) credit_no_key = json.getString("creditNoKey")
        if (json.containsKey("creditType")) credit_type = json.getString("creditType")
        if (json.containsKey("creditName")) credit_name = json.getString("creditName")
        if (json.containsKey("creditIdType")) credit_id_type = json.getString("creditIdType")
        if (json.containsKey("creditId")) credit_id = json.getString("creditId")
        if (json.containsKey("supplierName")) supplier_name = json.getString("supplierName")
        if (json.containsKey("supplierCode")) supplier_code = json.getString("supplierCode")
        /*  if (json.containsKey("cooperationPeriod")) cooperation_period = json.getString("cooperationPeriod")
          if (json.containsKey("purchaseFrequency")) purchase_frequency = json.getString("purchaseFrequency")*/
        if (json.containsKey("name")) names = json.getString("name")
        if (json.containsKey("phone")) phone = json.getString("phone")
        if (json.containsKey("certId")) cert_id = json.getString("certId")
        if (json.containsKey("marriage")) marriage = json.getString("marriage")
        if (json.containsKey("childNum")) child_num = json.getString("childNum")
        if (json.containsKey("education")) education = json.getString("education")
        if (json.containsKey("wechat")) wechat = json.getString("wechat")
        if (json.containsKey("address")) address = json.getString("address")
        if (json.containsKey("addressCode")) address_code = json.getString("addressCode")
        if (json.containsKey("email")) email = json.getString("email")
        if (json.containsKey("contactsName1")) contacts_name1 = json.getString("contactsName1")
        if (json.containsKey("contactsPhone1")) contacts_phone1 = json.getString("contactsPhone1")
        if (json.containsKey("relation1")) relation1 = json.getString("relation1")
        if (json.containsKey("contactsName2")) contacts_name2 = json.getString("contactsName2")
        if (json.containsKey("contactsPhone2")) contacts_phone2 = json.getString("contactsPhone2")
        if (json.containsKey("relation2")) relation2 = json.getString("relation2")
        if (json.containsKey("applyAmount")) apply_amount = json.getString("applyAmount")
        if (json.containsKey("applyDate")) apply_date = json.getString("applyDate")
        if (json.containsKey("compName")) comp_name = json.getString("compName")
        if (json.containsKey("creditCode")) credit_code = json.getString("creditCode")
        if (json.containsKey("addressComp")) address_comp = json.getString("addressComp")
        if (json.containsKey("addressCompCode")) address_comp_code = json.getString("addressCompCode")
        if (json.containsKey("sellCompName")) sell_comp_name = json.getString("sellCompName")
        if (json.containsKey("sellCreditId")) sell_credit_id = json.getString("sellCreditId")
        if (json.containsKey("sellAddressComp")) sell_address_comp = json.getString("sellAddressComp")
        if (json.containsKey("sellAddressCompCode")) sell_address_comp_code = json.getString("sellAddressCompCode")
        if (json.containsKey("sellName")) sell_name = json.getString("sellName")
        if (json.containsKey("callbackUrl")) callback_url = json.getString("callbackUrl")
        if (json.containsKey("riskId")) risk_id = json.getString("riskId")
      }
      map += ("types" -> types)
      map += ("platform_id" -> platform_id)
      map += ("platform_name" -> platform_name)
      map += ("credit_apply_no_xib" -> credit_apply_no_xib)
      map += ("id_of_core" -> id_of_core)
      map += ("credit_apply_no" -> credit_apply_no)
      map += ("credit_no_key" -> credit_no_key)
      map += ("credit_type" -> credit_type)
      map += ("credit_name" -> credit_name)
      map += ("credit_id_type" -> credit_id_type)
      map += ("credit_id" -> credit_id)
      map += ("supplier_name" -> supplier_name)
      map += ("supplier_code" -> supplier_code)
      /* map += ("cooperation_period" -> cooperation_period)
       map += ("purchase_frequency" -> purchase_frequency)*/
      map += ("names" -> names)
      map += ("phone" -> phone)
      map += ("cert_id" -> cert_id)
      map += ("marriage" -> marriage)
      map += ("child_num" -> child_num)
      map += ("education" -> education)
      map += ("wechat" -> wechat)
      map += ("address" -> address)
      map += ("address_code" -> address_code)
      map += ("email" -> email)
      map += ("contacts_name1" -> contacts_name1)
      map += ("contacts_phone1" -> contacts_phone1)
      map += ("relation1" -> relation1)
      map += ("contacts_name2" -> contacts_name2)
      map += ("contacts_phone2" -> contacts_phone2)
      map += ("relation2" -> relation2)
      map += ("apply_amount" -> apply_amount)
      map += ("apply_date" -> apply_date)
      map += ("comp_name" -> comp_name)
      map += ("credit_code" -> credit_code)
      map += ("address_comp" -> address_comp)
      map += ("address_comp_code" -> address_comp_code)
      map += ("sell_comp_name" -> sell_comp_name)
      map += ("sell_credit_id" -> sell_credit_id)
      map += ("sell_address_comp" -> sell_address_comp)
      map += ("sell_address_comp_code" -> sell_address_comp_code)
      map += ("sell_name" -> sell_name)
      map += ("callback_url" -> callback_url)
      map += ("risk_id" -> risk_id)
    }
    try{
      Save_To_Mysql.save_data(map, "credit_apply_info_dtl")
    }catch {
      case e:Exception=>println("存储credit_apply_info_dtl失败");e.printStackTrace()
    }

  }

}
