package cn.jk

import cn.jk.sftp.{BusinessData, CreditList, HdfsUtil, ReimbursementList, SFTPUtil, UseCreditList}
import cn.jk.util.DeleteFileUtil


/**
  * Created by YiZe on 2019/5/19.
  *
  */
object ScalaSchulerApplication {

  def main(args: Array[String]): Unit = {
//    ReimbursementList.getReimbursementList
    BusinessData.getBusinessData
//    CreditList.getCreditList
//    UseCreditList.getUseCreditList
  }
}


