package io.agora.chatroom.topic;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Map;

public class TopicList implements Serializable {
    @JSONField(name="id")
    String id = "";
    @JSONField(name="property")
    String property = "";
    @JSONField(name="topic")
    String topic = "";
    @JSONField(name="topic_first_word")
    String topic_first_word = "";
    @JSONField(name="topicdescription")
    String topicdescription = "";
    @JSONField(name="topicimage")
    String topicimage = "";
    @JSONField(name="topictag")
    String topictag = "";
    @JSONField(name="topicdiscuss")
    int topicdiscuss = 0;
    @JSONField(name="topicpv")
    int topicpv = 0;

    public TopicList() {

    }

    public TopicList(String topic,String topicdescription) {
        this.topic = topic;
        this.topicdescription = topicdescription;
    }

    public TopicList(Map argsMap) {
        if (argsMap.get("id")!=null) {
            this.id = argsMap.get("id").toString();
        }
        if (argsMap.get("property")!=null) {
            this.property = argsMap.get("property").toString();
        }
        if (argsMap.get("topic")!=null) {
            this.topic = argsMap.get("topic").toString();
        }
        if (argsMap.get("topicdescription")!=null) {
            this.topicdescription = argsMap.get("topicdescription").toString();
        }
        if (argsMap.get("topicimage")!=null) {
            this.topicimage = argsMap.get("topicimage").toString();
        }
        if (argsMap.get("topictag")!=null) {
            this.topictag = argsMap.get("topictag").toString();
        }
        if (argsMap.get("topicdiscuss")!=null) {
            this.topicdiscuss = Integer.parseInt(argsMap.get("topicdiscuss").toString());
        }
        if (argsMap.get("topicpv")!=null) {
            this.topicpv = Integer.parseInt(argsMap.get("topicpv").toString());
        }
    }

    public String getTopicdescription() {
        return topicdescription;
    }

    public void setTopicdescription(String topicdescription) {
        this.topicdescription = topicdescription;
    }

    public String getTopic_first_word() {
        return topic_first_word;
    }

    public void setTopic_first_word(String topic_first_word) {
        this.topic_first_word = topic_first_word;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopicimage() {
        return topicimage;
    }

    public void setTopicimage(String topicimage) {
        this.topicimage = topicimage;
    }

    public String getTopictag() {
        return topictag;
    }

    public void setTopictag(String topictag) {
        this.topictag = topictag;
    }

    public int getTopicdiscuss() {
        return topicdiscuss;
    }

    public void setTopicdiscuss(int topicdiscuss) {
        this.topicdiscuss = topicdiscuss;
    }

    public int getTopicpv() {
        return topicpv;
    }

    public void setTopicpv(int topicpv) {
        this.topicpv = topicpv;
    }
}

