package cn.jq.bigdata.client;

import cn.jq.bigdata.service.XmlElementAnno;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Header")
@XmlType
public class JQRequesetHeader {

    @XmlElement(name = "ServiceCode")
    @XmlElementAnno
    private String serviceCode;

    @XmlElement(name = "ChannelId")
    @XmlElementAnno
    private String channelId;

    @XmlElement(name = "ExternalReference")
    @XmlElementAnno
    private String externalReference;

    @XmlElement(name = "OriginalChannelId")
    @XmlElementAnno
    private String originalChannelId;

    @XmlElement(name = "OriginalReference")
    @XmlElementAnno
    private String originalReference;

    @XmlElement(name = "RequestTime")
    @XmlElementAnno
    private String requestTime;

    @XmlElement(name = "TradeDate")
    @XmlElementAnno
    private String tradeDate;

    @XmlElement(name = "Version")
    @XmlElementAnno
    private String version;

    @XmlElement(name = "RequestBranchCode")
    @XmlElementAnno
    private String requestBranchCode;

    @XmlElement(name = "RequestOperatorId")
    @XmlElementAnno
    private String requestOperatorId;

    @XmlElement(name = "RequestOperatorType")
    @XmlElementAnno
    private String requestOperatorType;

    @XmlElement(name = "TermType")
    @XmlElementAnno
    private String termType;

    @XmlElement(name = "TermNo")
    @XmlElementAnno
    private String termNo;

    @XmlElement(name = "RequestType")
    @XmlElementAnno
    private String requestType;

    @XmlElement(name = "Encrypt")
    @XmlElementAnno
    private String encrypt;




    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getOriginalChannelId() {
        return originalChannelId;
    }

    public void setOriginalChannelId(String originalChannelId) {
        this.originalChannelId = originalChannelId;
    }

    public String getOriginalReference() {
        return originalReference;
    }

    public void setOriginalReference(String originalReference) {
        this.originalReference = originalReference;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRequestBranchCode() {
        return requestBranchCode;
    }

    public void setRequestBranchCode(String requestBranchCode) {
        this.requestBranchCode = requestBranchCode;
    }

    public String getRequestOperatorId() {
        return requestOperatorId;
    }

    public void setRequestOperatorId(String requestOperatorId) {
        this.requestOperatorId = requestOperatorId;
    }

    public String getRequestOperatorType() {
        return requestOperatorType;
    }

    public void setRequestOperatorType(String requestOperatorType) {
        this.requestOperatorType = requestOperatorType;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getTermNo() {
        return termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

}
