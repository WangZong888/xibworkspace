package cn.jq.bigdata.client;

import cn.jq.bigdata.service.XmlElementAnno;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Request")
@XmlType
public class JQRquestBodyReq {

    @XmlElement(name = "BranchId")
    @XmlElementAnno
    private String branchId;

    @XmlElement(name = "UserId")
    @XmlElementAnno
    private String userId;

    @XmlElement(name = "GlobalType")
    @XmlElementAnno
    private String globalType;

    @XmlElement(name = "GlobalId")
    @XmlElementAnno
    private String globalId;

    @XmlElement(name = "CustomerId")
    @XmlElementAnno
    private String customerId;

    @XmlElement(name = "QueryReason")
    @XmlElementAnno
    private String queryReason;

    @XmlElement(name = "VerType")
    @XmlElementAnno
    private String verType;

    @XmlElement(name = "IdauthFlag")
    @XmlElementAnno
    private String idauthFlag;

    @XmlElement(name = "ImageId")
    @XmlElementAnno
    private String imageId;

    @XmlElement(name = "EffectiveDay")
    @XmlElementAnno
    private String effectiveDay;

    @XmlElement(name = "ONLINE_LOAN")
    @XmlElementAnno
    private String online_loan;

    @XmlElement(name = "Note")
    @XmlElementAnno
    private String note;

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGlobalType() {
        return globalType;
    }

    public void setGlobalType(String globalType) {
        this.globalType = globalType;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getQueryReason() {
        return queryReason;
    }

    public void setQueryReason(String queryReason) {
        this.queryReason = queryReason;
    }

    public String getVerType() {
        return verType;
    }

    public void setVerType(String verType) {
        this.verType = verType;
    }

    public String getIdauthFlag() {
        return idauthFlag;
    }

    public void setIdauthFlag(String idauthFlag) {
        this.idauthFlag = idauthFlag;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getEffectiveDay() {
        return effectiveDay;
    }

    public void setEffectiveDay(String effectiveDay) {
        this.effectiveDay = effectiveDay;
    }

    public String getOnline_loan() {
        return online_loan;
    }

    public void setOnline_loan(String online_loan) {
        this.online_loan = online_loan;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
