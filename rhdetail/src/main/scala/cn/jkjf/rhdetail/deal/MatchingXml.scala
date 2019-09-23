package cn.jkjf.rhdetail.deal

import org.apache.spark.rdd.RDD

import scala.xml.Elem

object MatchingXml {

  def xmlmatch(perrdd: RDD[Elem]): Unit = {
    //人行征信详版-报告查询结果
    perrdd.filter(f=>{!(f \\ "TblperResultInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperResultInfo.savetomysql(iterator)
    })
    //人行征信详版-身份和配偶信息
    perrdd.filter(f=>{!(f \\ "TblperPersionalInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperPersionalInfo.savetomysql(iterator)
    })
    //人行征信详版-居住信息
    perrdd.filter(f=>{!(f \\ "TblperResidenceInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperResidenceInfo.savetomysql(iterator)
    })
    //人行征信详版-职业信息
    perrdd.filter(f=>{!(f \\ "TblperProfessional").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperProfessional.savetomysql(iterator)
    })
    //人行征信详版-信息概要
    perrdd.filter(f=>{!(f \\ "TblperInfoSumarry").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperInfoSummary.savetomysql(iterator)
    })
    //人行征信详版-逾期及违约信息
    perrdd.filter(f=>{!(f \\ "TblperOverdueSummary").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperOverdueSummary.savetomysql(iterator)
    })
    //人行征信详版-资产处置信息
    perrdd.filter(f=>{!(f \\ "TblperAssetDisposition").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperAssetDisposition.savetomysql(iterator)
    })
    //人行征信详版-贷款交易信息明细
    perrdd.filter(f=>{!(f \\ "TblperLoanInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperLoanInfo.savetomysql(iterator)
    })
    //人行征信详版-逾期记录
    perrdd.filter(f=>{!(f \\ "TblperOverdueRecord").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperOverdueRecord.savetomysql(iterator)
    })
    //人行征信详版-贷记卡信息
    perrdd.filter(f=>{!(f \\ "TblperLoanCardInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperLoanCardInfo.savetomysql(iterator)
    })
    //人行征信详版-查询信息汇总
    perrdd.filter(f=>{!(f \\ "TblperQueryRecordCollect").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperQueryRecordCollect.savetomysql(iterator)
    })
    //人行征信详版-信贷审批查询记录明细
    perrdd.filter(f=>{!(f \\ "TblperRecordDetail").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperRecordDetail.savetomysql(iterator)
    })
    //人行征信详版-车辆交易和抵押记录
    perrdd.filter(f=>{!(f \\ "TblperVehicle").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperVehicle.savetomysql(iterator)
    })
    //人行征信详版-民事判决记录
    perrdd.filter(f=>{!(f \\ "TblperCivilJudgement").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperCivilJudgement.savetomysql(iterator)
    })
    //人行征信详版-强制执行记录
    perrdd.filter(f=>{!(f \\ "TblperForceExecution").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperForceExecution.savetomysql(iterator)
    })
    //人行征信详版-住房公积金参缴记录
    perrdd.filter(f=>{!(f \\ "TblperCrePbRFInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperCrePbRFInfo.savetomysql(iterator)
    })
    //人行征信详版-养老保险金缴存记录
    perrdd.filter(f=>{!(f \\ "TblperPublicInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperPublicInfo.savetomysql(iterator)
    })
    //人行征信详版-保证人代偿信息
    perrdd.filter(f=>{!(f \\ "TblperAssurerRepay").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperAssurerRepay.savetomysql(iterator)
    })
    //人行征信详版-特殊交易信息
    perrdd.filter(f=>{!(f \\ "TblperSpecialTrade").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperSpecialTrade.savetomysql(iterator)
    })
    //人行征信详版-对外担保信息明细
    perrdd.filter(f=>{!(f \\ "TblperGuarantee").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperGuarantee.savetomysql(iterator)
    })
    //人行征信详版-低保救助记录
    perrdd.filter(f=>{!(f \\ "TblperSalvation").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperSalvation.savetomysql(iterator)
    })
    //人行征信详版-个人征信欠税记录
    perrdd.filter(f=>{!(f \\ "TblperTaxArrear").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperTaxArrear.savetomysql(iterator)
    })
    //人行征信详版-行政奖励记录
    perrdd.filter(f=>{!(f \\ "TblperAdminAward").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperAdminAward.savetomysql(iterator)
    })
    //人行征信详版-电信缴费记录
    perrdd.filter(f=>{!(f \\ "TblperTelPayment").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperTelPayment.savetomysql(iterator)
    })
    //人行征信详版-行政处罚记录
    perrdd.filter(f=>{!(f \\ "TblperAdminPunishment").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperAdminPunishment.savetomysql(iterator)
    })
    //人行征信详版-养老保险金发放记录
    perrdd.filter(f=>{!(f \\ "TblperEndOwmentInsuranceDel").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperEndOwmentInsuranceDel.savetomysql(iterator)
    })
    //人行征信详版-执业资格记录
    perrdd.filter(f=>{!(f \\ "TblperCompetence").isEmpty}).foreachPartition(iterator=>{
      Deal_TblperCompetence.savetomysql(iterator)
    })
    //人行征信详版-被担保人列表
    perrdd.filter(f=>{!(f \\ "TblZrrGuaranteedList").isEmpty}).foreachPartition(iterator=>{
      Deal_TblZrrGuaranteedList.savetomysql(iterator)
    })
    //人行征信详版-担保合同详细信息表
    perrdd.filter(f=>{!(f \\ "TblZrrGuarConInfoPri").isEmpty}).foreachPartition(iterator=>{
      Deal_TblZrrGuarConInfoPri.savetomysql(iterator)
    })
    //人行征信详版-抵质押物详细信息表
    perrdd.filter(f=>{!(f \\ "TblZrrGuarGuarantyInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblZrrGuarGuarantyInfo.savetomysql(iterator)
    })
    //人行征信详版-担保合同对应主业务汇总信息表
    perrdd.filter(f=>{!(f \\ "TblZrrGuarLoanSumInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblZrrGuarLoanSumInfo.savetomysql(iterator)
    })
    //人行征信详版-担保合同对象主业务详细信息
    perrdd.filter(f=>{!(f \\ "TblZrrGuarLoanMainInfo").isEmpty}).foreachPartition(iterator=>{
      Deal_TblZrrGuarLoanMainInfo.savetomysql(iterator)
    })

  }
}
