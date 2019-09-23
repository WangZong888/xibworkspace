package cn.bigdata.rhdetailindex.deal

import java.text.DecimalFormat

import cn.bigdata.rhdetailindex.dao.Save_To_Mysql
import cn.bigdata.rhdetailindex.special.{Cal_Index_Loan_Status, Cal_Index_Overdue_State}
import cn.bigdata.rhdetailindex.utils.{KafkaProducerUtil, SendContext}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.rdd.RDD

import scala.xml.Elem

object Cal_Derive_Collect {

  val config: Config = ConfigFactory.load()
  val INVAILD_NUM = config.getString("INVAILD_NUM").toDouble
  val df = new DecimalFormat("#.##")

  def cal_index(perrdd: RDD[Elem]): Unit = {
    perrdd.foreachPartition(iterator => {
      iterator.foreach(elem => {
        var returnStatus = ""
        var returnMsg = ""
        //获取返回给引擎的消息
        val apply_no_xib = (elem \\ "CreditApplyNoXib").text
        val risk_id = (elem \ "CreditRiskId").text
        println("risk_id----------------"+risk_id)
        if(risk_id==null || "".equals(risk_id)){
          throw new RuntimeException("risk_id解析为空")
        }
        val request_biz_id = (elem \\ "TblperRequestBizId").text
        try {
          /*if(risk_id==null || "".equals(risk_id)){
             throw new RuntimeException("CreditRiskId节点为空，无法解析数据");
          }
          if(apply_no_xib==null || "".equals(apply_no_xib)){
            throw new RuntimeException("CreditApplyNoXib节点为空，无法解析数据");
          }
          if(request_biz_id==null || "".equals(request_biz_id)){
            throw new RuntimeException("TblperRequestBizId节点为空，无法解析数据");
          }*/
          var field_mapper = scala.collection.mutable.Map[String, Object]()

          //获取本报告基本信息
          var bar_code = ""
          var report_id = ""
          var query_time = ""
          var report_create_time = ""
          var name = ""
          var cert_type = ""
          var cert_id = ""
          val basic_info = elem \\ "TblperResultInfo"
          basic_info.foreach(m => {
            bar_code = (m \ "BarCode").text
            report_id = (m \ "ReportSn").text
            query_time = (m \ "QueryTime").text
            report_create_time = (m \ "ReportCreateTime").text
            name = (m \ "Name").text
            cert_type = (m \ "CertType").text
            cert_id = (m \ "CertNo").text
          })
          field_mapper += ("risk_id" -> risk_id)
          field_mapper += ("bar_code" -> bar_code)
          field_mapper += ("report_id" -> report_id)
          field_mapper += ("query_time" -> query_time)
          field_mapper += ("report_create_time" -> report_create_time)
          field_mapper += ("name" -> name)
          field_mapper += ("cert_type" -> cert_type)
          field_mapper += ("cert_id" -> cert_id)

          /*
          计算指标：贷记卡相关指标
             新增 （准）贷记卡状态异常：0-无异常  1-有异常 add by grace 2019-06-04
             修改计算逻辑：限制只计算贷记卡  add by grace 2019-07-24
             方法返回：贷记卡总使用额度|贷记卡总共享额度|贷记卡平均使用额度比例||贷记卡有效账户中历史最大额度使用比例|（准）贷记卡状态异常
           */
          val loancard_index = Cal_Index_LoanCard.cal_index(elem)
          val loan_split = loancard_index.split("\\|")
          val total_ucla = loan_split(0)
          val total_scla = loan_split(1)
          val used_share_ratio = loan_split(2)
          val amount_mean = loan_split(3)
          val max_used_share_ratio = loan_split(4)
          val card_status_error = loan_split(5)
          val max_creditamount = loan_split(6)
          println("贷记卡总使用额度:" + total_ucla)
          println("贷记卡总共享额度:" + total_scla)
          println("贷记卡平均使用额度比例:" + used_share_ratio)
          println("授信额度:" + amount_mean)
          println("贷记卡有效账户中历史最大额度使用比例:" + max_used_share_ratio)
          println("贷记卡状态异常:" + card_status_error)
          println("信用卡授信额度最大值:" + max_creditamount)
          field_mapper += ("total_ucla" -> total_ucla)
          field_mapper += ("total_scla" -> total_scla)
          field_mapper += ("used_share_ratio" -> used_share_ratio)
          field_mapper += ("amount_mean" -> amount_mean)
          field_mapper += ("max_used_share_ratio" -> max_used_share_ratio)
          field_mapper += ("card_status_error" -> card_status_error)
          field_mapper += ("max_creditamount" -> max_creditamount)

          /*
            计算信息概要相关指标
            需求变更，在信息概要里增加“最长逾期期数”指标   add by grace 2019-06-04
            方法返回：发卡机构数|最长逾期期数
           */
          val summary_info = Cal_Index_Summary.cal_index(elem)
          val summary_info_split = summary_info.split("\\|")
          val finance_org_count = summary_info_split(0)
          val max_overdue_num = summary_info_split(1)
          println("发卡机构数:" + finance_org_count)
          field_mapper += ("finance_org_count" -> finance_org_count)


          /*
            计算贷款交易信息明细相关指标：未结清贷款平均合同金额、未结清贷款比例
            方法返回：未结清贷款平均合同金额|未结清贷款比例
           */
          val loan_info = Cal_Index_LoanInfo.cal_index(elem)
          val loaninfo_split = loan_info.split("\\|")
          val unsettled_bal_mean = loaninfo_split(0).toDouble
          val unsetteled_ratio = loaninfo_split(1).toDouble
          val loan_repayamount = loaninfo_split(2).toDouble
          println("未结清贷款平均合同金额:" + unsettled_bal_mean)
          println("未结清贷款比例:" + unsetteled_ratio)
          println("每月应还贷款额:" + loan_repayamount)
          field_mapper += ("unsettled_bal_mean" -> unsettled_bal_mean.toString)
          field_mapper += ("loan_repayamount" -> loan_repayamount.toString)
          field_mapper += ("unsetteled_ratio" -> unsetteled_ratio.toString)


          /*  计算  近一年人行征信查询次数指标
              需求变更，指标由近一年改为近3个月（距离申请日期） add by grace 2019-06-04
          */
          val inquiry_num_3m = Cal_Index_ResultInfo.cal_indx(elem,risk_id,query_time)
          println("近三个月人行征信查询次数:" + inquiry_num_3m)
          field_mapper += ("inquiry_num_3m" -> inquiry_num_3m.toString)

          /*  计算  历史逾期次数指标
              需求变更，指标改为距离申请日期近两年 add by grace 2019-06-04
              需求变更，增加贷记卡和准贷记卡的区分 add by grace 2019-07-24
          */
          val rh_overdue_times_his = Cal_Index_Overdue.cal_index(elem,risk_id)
          println("近两年逾期次数:" + rh_overdue_times_his)
          field_mapper += ("rh_overdue_times_his" -> rh_overdue_times_his.toString)

          val rh_overdue_times_his_5y = Cal_Index_Overdue_5y.cal_index(elem,risk_id)
          println("五年内最长逾期期数:" + rh_overdue_times_his_5y)
          field_mapper += ("max_overdue_num" -> rh_overdue_times_his_5y.toString)


          /*
            新增当前是否有逾期记录：0-无  1-有 add by grace 2019-06-04

           */
          val overdue_state = Cal_Index_Overdue_State.cal_overdue_state(elem)
          println("当前是否有逾期记录:" + overdue_state)
          field_mapper += ("overdue_state" -> overdue_state)
          /*
            新增 存有不良违法行为：0-无  1-有
            add by grace 2019-06-04
           */
          val illegal_state = Cal_Index_OverdueSummary.cal_illegal(elem)
          println("是否存有不良授信:" + illegal_state)
          field_mapper += ("illegal_state" -> illegal_state)
          /*
             新增 贷款交易分类异常：0-无异常  1-有异常
             add by grace 2019-06-04
           */
          val loan_status_error = Cal_Index_Loan_Status.cal_loanstatus(elem)
          println("贷款交易分类异常:" + loan_status_error)
          field_mapper += ("loan_status_error" -> loan_status_error)

          //8.将所有衍生指标进行存储
          Save_To_Mysql.save_data(field_mapper, "person_credit_detail")

          returnStatus = "1"
          returnMsg = "SUCCESS"
        } catch {
          case e:RuntimeException=>e.printStackTrace(); returnStatus = "0"; returnMsg = e.getMessage;
          case e: Exception => e.printStackTrace(); returnStatus = "0"; returnMsg = "UNKNOWN ERROR";
        } finally {
          //给引擎返回指标计算的结果
          val send_info = SendContext.send_data(apply_no_xib, risk_id, request_biz_id, returnStatus, returnMsg)
          println("发送给引擎的数据:"+send_info)
          KafkaProducerUtil.sendSync(config.getString("SEND_TOPIC"), send_info)
        }
      })
    })
  }

}
