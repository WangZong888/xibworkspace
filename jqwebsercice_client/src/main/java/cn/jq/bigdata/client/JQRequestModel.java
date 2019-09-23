package cn.jq.bigdata.client;


import cn.jq.bigdata.service.XmlElementAnno;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="Service")
@XmlType
public class JQRequestModel {

    @XmlElement(name="Header")
    @XmlElementAnno
    private JQRequesetHeader header;

    @XmlElement(name="Body")
    @XmlElementAnno
    private JQRequestBody body;

    public JQRequesetHeader getHeader() {
        return header;
    }

    public void setHeader(JQRequesetHeader header) {
        this.header = header;
    }

    public JQRequestBody getBody() {
        return body;
    }

    public void setBody(JQRequestBody body) {
        this.body = body;
    }
}
