package cn.bigdata.rhdetailindex.special

import scala.xml.{Elem, NodeSeq}

/*
   新增 贷款交易分类异常：0-无异常  1-有异常
   add by grace 2019-06-04
 */
object Cal_Index_Loan_Status {

  def cal_loanstatus(elem: Elem):String = {
    var loan_status_error = ""
    val loaninfo = elem \\ "TblperLoanInfo"
    val Specialinfo = elem \\ "TblperSpecialTrade"
    val Guaranteeinfo = elem \\ "TblperGuarantee"

    val loan_list: NodeSeq = loaninfo.filter(p => {
        "次级".equals((p \\ "Class5state").text.trim) ||
        "可疑".equals((p \\ "Class5state").text.trim) ||
        "损失".equals((p \\ "Class5state").text.trim) ||
        "未知".equals((p \\ "Class5state").text.trim)
    })



    val Special_list: NodeSeq = Specialinfo.filter(p => {
      "特殊交易类型".equals((p \\ "Changingtype").text.trim) ||
        "展期（延期）".equals((p \\ "Changingtype").text.trim) ||
        "担保人代偿".equals((p \\ "Changingtype").text.trim) ||
        "以资抵债类".equals((p \\ "Changingtype").text.trim)
    })


    val Guarantee_list: NodeSeq = Guaranteeinfo.filter(p => {
      "次级".equals((p \\ "Class5state").text.trim) ||
        "可疑".equals((p \\ "Class5state").text.trim) ||
        "损失".equals((p \\ "Class5state").text.trim)
    })

    if(!loan_list.isEmpty ||  !Special_list.isEmpty || !Guarantee_list.isEmpty){
      loan_status_error = "1"
    }else{
      loan_status_error = "0"
    }
    loan_status_error
  }
}
