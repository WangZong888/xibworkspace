package cn.jq.bigdata.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class JaXmlBeanUtil {

    public static String parseBeanToXml(Class clazz,Object bean){
        StringWriter sw = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            sw = new StringWriter();
            //该值默认为false,true则不会创建即头信息,即<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            //jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(bean, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }
}
