package app.utils;

import app.singletons.EmailCredentials;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public enum EmailService {

    INSTANCE();

    Properties prop;
    Session    session;

    EmailService(){
        setup();
    }

    public String buildBody(){

    }

    public void send(String to, String subject, String body) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EmailCredentials.INSTANCE.getEmail()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(body, "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        message.setContent(multipart);
        Transport.send(message);
    }

    private void setup(){
        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.mailtrap.io");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailCredentials.INSTANCE.getEmail(), EmailCredentials.INSTANCE.getPassword());
            }
        });
    }

}
