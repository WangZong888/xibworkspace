package cn.bigdata.rhdetailindex.deal

import cn.bigdata.rhdetailindex.deal.Cal_Derive_Collect.{INVAILD_NUM, df}
import cn.bigdata.rhdetailindex.utils.Global

import scala.xml.{Elem, NodeSeq}

/*
  3.计算指标：贷记卡相关指标
  方法返回：贷记卡总使用额度|贷记卡总共享额度|贷记卡平均使用额度比例|贷记卡平均授信额度|贷记卡有效账户中历史最大额度使用比例
 */
object Cal_Index_LoanCard {

  def cal_index(elem: Elem):String = {
    var total_ucla = 0.0
    var total_scla = 0.0
    var used_share_ratio = 0.0
    var max_used_share_ratio = 0.0
    var amount_sum = 0.0
    var amount_count = 0
    var amount_mean = 0.0
    var max_creditamount = 0
    val loan_info = elem \\ "TblperLoanCardInfo"
    val count_status: NodeSeq = loan_info.filter(f=>{"贷记卡".equals((f \ "CardType").text) || "准贷记卡".equals((f \ "CardType").text)})
    val temp: NodeSeq = loan_info.filter(f=>{"贷记卡".equals((f \ "CardType").text)})
    temp.foreach(m => {
      val usedcredit_limit_amount = Global.getIntValue((m \ "Usedcreditlimitamount").text).getOrElse(0)
      val sharecredit_limit_amount = Global.getIntValue((m \ "Sharecreditlimitamount").text).getOrElse(0)
      val used_highest_amount = Global.getIntValue((m \ "Usedhighestamount").text).getOrElse(0)
      val account = Global.getIntValue((m \ "Sharecreditlimitamount").text).getOrElse(0)
      val state = (m \ "State").text
      if (!"销户".equals(state)) {
        total_ucla += usedcredit_limit_amount
        total_scla += sharecredit_limit_amount
        if(account>0){
          amount_sum += account
          amount_count += 1
        }
        if(sharecredit_limit_amount>max_creditamount){
          max_creditamount = sharecredit_limit_amount
        }
      }
    })
    if (total_scla == 0.0) {
      used_share_ratio = INVAILD_NUM
    } else {
      used_share_ratio = df.format(total_ucla / total_scla).toDouble
    }
    if (amount_count == 0) {
      amount_mean = INVAILD_NUM
    } else {
      amount_mean = df.format(amount_sum / amount_count).toDouble
    }
    //2.下面开始计算贷记卡有效账户中历史最大额度使用比例，先进行分组
    val tuples = temp
      .filter(t => {
        !"".equals((t \ "Cue").text ) &&  !"销户".equals((t \ "State").text ) && !"未激活".equals((t \ "State").text )
      })
      .map(f => {
        val cue = (f \ "Cue").text
        val shared = Global.getIntValue((f \ "Sharecreditlimitamount").text).getOrElse(0)
        val used = Global.getIntValue((f \ "Usedhighestamount").text).getOrElse(0)
        val finance = cue.substring(cue.indexOf("“")+1,cue.indexOf("”发放"))
        (finance, shared, used)
      }).groupBy(g => {
      g._1
    })
    if(!tuples.isEmpty){
      max_used_share_ratio = tuples.map(m => {
        var sum = 0.0
        var max_value = 0.0
        m._2.foreach(f => {
          sum += f._3.toDouble
          if (f._2 > max_value) {
            max_value = f._2
          }
        })

        if (max_value == 0.0) {
          (INVAILD_NUM)
        } else {
          (sum / max_value)
        }
      }).max
    }
    //3.账户状态异常
    var creditcard_status_error = ""
    val creditcard_status_list: NodeSeq = count_status.filter(p => {
      "呆账".equals((p \\ "State").text.trim) ||
        "止付".equals((p \\ "State").text.trim) ||
        "冻结".equals((p \\ "State").text.trim)
    })

    val account_info = elem \\ "TblperLoanInfo"
    val loan_out_list: NodeSeq = account_info.filter(p => {"转出".equals((p \\ "State").text)})
    if(!creditcard_status_list.isEmpty || !loan_out_list.isEmpty ){
      creditcard_status_error = "1"
    }else{
      creditcard_status_error = "0"
    }

    total_ucla + "|" + total_scla + "|" + used_share_ratio +
      "|" + amount_mean + "|" + max_used_share_ratio+
      "|" + creditcard_status_error+"|"+max_creditamount
  }
}
