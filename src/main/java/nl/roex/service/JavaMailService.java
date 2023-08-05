package nl.roex.service;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import nl.roex.domain.JavaMail;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

public class JavaMailService {
    public static final Logger LOG = Logger.getLogger("JavaMailService");

    public static String FROM_MAIL;
    public static String TO_MAIL;
    public static String USERNAME;
    public static String PASSWORD;

    public JavaMailService() {
        init();
    }

    public void sendMail(JavaMail javaMail) throws IOException {
        try {
            LOG.info("1=====> Generate and send mail starts..");
            MimeMessage msg = new MimeMessage(getSession());
            msg.addHeader("Content-Type", "text/html; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(FROM_MAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_MAIL));
            msg.setSubject(javaMail.getSubject());
            msg.setSentDate(new Date());

            // Create the message body part
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(javaMail.getContent(), "text/html");

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(mimeBodyPart);

            mimeBodyPart = new MimeBodyPart();

            // Create disclaimer with image
            LOG.info("2=====> Displaying image in the email..");
            addDisclaimer(mimeBodyPart, multipart);

            LOG.info("3=====> Adding attachments..");
            addAttachments(javaMail, multipart);

            LOG.info("4=====> Finally send message..");
            msg.setContent(multipart);
            Transport.send(msg);
            LOG.info("5=====> Message was sent successfully");
        } catch (MessagingException e) {
            LOG.severe("Failed to send message");
            e.printStackTrace();
        }
    }

    private void addAttachments(JavaMail javaMail, Multipart multipart) throws MessagingException, IOException {
        MimeBodyPart mimeBodyPart;
        DataSource source;
        for (File file : javaMail.getAttachments()) {
            mimeBodyPart = new MimeBodyPart();
            source = new FileDataSource(file.getPath());
            mimeBodyPart.setDataHandler(new DataHandler(source));
            mimeBodyPart.setFileName(file.getName());
            mimeBodyPart.attachFile(file);
            mimeBodyPart.setHeader("Content-ID", file.getName());
            multipart.addBodyPart(mimeBodyPart);
        }
    }

    private void addDisclaimer(MimeBodyPart mimeBodyPart, Multipart multipart) throws MessagingException {
        String filename = getClass().getClassLoader().getResource("disclaimer/triodos.png").getPath();
        DataSource source = new FileDataSource(filename);
        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName("Disclaimer image");
        // Trick is to add the content-id header here
        mimeBodyPart.setHeader("Content-ID", "image_id");
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(new JavaResourceService().getContent("disclaimer/text.html"), "text/html");
        multipart.addBodyPart(mimeBodyPart);
    }

    private Session getSession() throws IOException {
        LOG.info("Starting session...");
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    private void init() {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        Properties mailProperties = new Properties();

        try {
            mailProperties.load(new FileInputStream(rootPath + "mail.properties"));
        } catch (IOException e) {
            LOG.severe("Failed to load properties");
            e.printStackTrace();
        }
        USERNAME = mailProperties.getProperty("mail.user");
        PASSWORD = mailProperties.getProperty("mail.password");
        FROM_MAIL = mailProperties.getProperty("mail.from");
        TO_MAIL = mailProperties.getProperty("mail.to");
    }
}
