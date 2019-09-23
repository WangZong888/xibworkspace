package cn.jq.bigdata.client;



import cn.jq.bigdata.service.XmlElementAnno;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Body")
@XmlType
public class JQRequestBody {

    @XmlElement(name = "Request")
    @XmlElementAnno
    private JQRquestBodyReq bodyReq;

    public JQRquestBodyReq getBodyReq() {
        return bodyReq;
    }

    public void setBodyReq(JQRquestBodyReq bodyReq) {
        this.bodyReq = bodyReq;
    }
}
