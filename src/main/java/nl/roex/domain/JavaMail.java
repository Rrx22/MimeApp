package nl.roex.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaMail {
    private String subject;
    private String content;
    private List<File> attachments;

    public JavaMail() {
        attachments = new ArrayList<>();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public void addAttachment(File attachment) {
        attachments.add(attachment);
    }

}
