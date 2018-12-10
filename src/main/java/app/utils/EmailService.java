package app.utils;

import app.controllers.models.AdoptionRequestModel;
import app.controllers.models.Model;
import app.entities.User;
import app.singletons.EmailCredentials;
import app.singletons.EmailType;
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

    public String buildBody(Model model, User requestor, EmailType type){
        String body = "";

        if(type == EmailType.ADOPTION_REQ){
            AdoptionRequestModel am = (AdoptionRequestModel)model;
            body = "Olá,\n" +
                    "Estamos muito felizes em informar que seu anúncio " + am.announcement.title + " possui uma pessoa interessada!\n"+
                    "Dados do anúncio: \n"+
                    "   - Título: " + am.announcement.title + "\n" +
                    "   - Descrição: " + am.announcement.description + "\n" +
                    "   - Idade: " + am.announcement.age + "\n" +
                    "   - Raça: " + am.announcement.race + "\n" +
                    "   - Porte: " + am.announcement.size + "\n" +
                    "   - Endereço: " + am.announcement.address + "\n"
                    + "\n" +
                    "Seguem os dados de contato informados pelo potencial adotante: \n" +
                    "   - Nome Completo: " + requestor.getFirstName()+" "+requestor.getLastName()+"\n"+
                    "   - Telefone: " + am.phone + "\n" +
                    "   - Email: " + requestor.getEmail() + "\n" +
                    "   - Cidade: " + am.city + "\n" +
                    "   - Melhor data para contato: " + am.dateForContact + " - " + am.shiftForContact + "\n" +
                   "Agradecemos sua ação!";
        }

        return body;
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
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailCredentials.INSTANCE.getEmail(), EmailCredentials.INSTANCE.getPassword());
            }
        });
    }

}
