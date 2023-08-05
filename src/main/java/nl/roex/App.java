package nl.roex;

import nl.roex.domain.JavaMail;
import nl.roex.service.JavaMailService;
import nl.roex.service.JavaResourceService;

import java.io.IOException;

public class App {

    private static final JavaMailService javaMailService = new JavaMailService();
    private static final JavaResourceService javaResourceService = new JavaResourceService();

    public static void main(String[] args) throws IOException {
        JavaMail javaMail = new JavaMail();
        javaMail.setSubject("Java mailing test");
        javaMail.setContent(javaResourceService.getContent("mail/mailbody.html"));
        javaMail.addAttachment(javaResourceService.getFile("attachments/attachment1.docx"));
        javaMail.addAttachment(javaResourceService.getFile("attachments/attachment2.jpeg"));

        javaMailService.sendMail(javaMail);
    }
}
