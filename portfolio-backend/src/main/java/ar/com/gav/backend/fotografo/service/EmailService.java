package ar.com.gav.backend.fotografo.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Servicio para envío de correos electrónicos usando Gmail SMTP.
 */
@Stateless
public class EmailService {

    private Properties mailProperties;
    private String mailUser;
    private String mailPassword;

    @PostConstruct
    public void init() {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties");
            if (input == null) {
                System.err.println("No se encontró email.properties");
                return;
            }

            Properties props = new Properties();
            props.load(input);

            mailUser = props.getProperty("mail.user");
            mailPassword = props.getProperty("mail.password");

            mailProperties = new Properties();
            mailProperties.put("mail.smtp.host", props.getProperty("mail.smtp.host"));
            mailProperties.put("mail.smtp.port", props.getProperty("mail.smtp.port"));
            mailProperties.put("mail.smtp.auth", props.getProperty("mail.smtp.auth"));
            mailProperties.put("mail.smtp.ssl.enable", props.getProperty("mail.smtp.ssl.enable", "true"));
            mailProperties.put("mail.smtp.starttls.enable", props.getProperty("mail.smtp.starttls.enable", "false"));
            mailProperties.put("mail.smtp.ssl.trust", props.getProperty("mail.smtp.ssl.trust", "smtp.gmail.com"));

            input.close();
        } catch (IOException e) {
            System.err.println("Error al cargar email.properties: " + e.getMessage());
        }
    }

    /**
     * Envía un email de recuperación de contraseña.
     */
    public void enviarRecuperacionPassword(String emailDestino, String token) {
        if (mailUser == null || mailPassword == null) {
            System.err.println("EmailService no configurado correctamente");
            return;
        }

        String asunto = "Recuperación de Contraseña - FotoPortfolio";
        String cuerpo = "Hola,\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Hacé click en el siguiente enlace para crear una nueva contraseña:\n"
                + "http://localhost:4200/reset-password?token=" + token + "\n\n"
                + "Este enlace expira en 1 hora.\n\n"
                + "Si no solicitaste este cambio, podés ignorar este mensaje.\n\n"
                + "Saludos,\n"
                + "Equipo de FotoPortfolio";

        enviarEmail(emailDestino, asunto, cuerpo);
    }

    /**
     * Envía un email genérico.
     */
    public void enviarEmail(String destino, String asunto, String cuerpo) {
        if (mailUser == null || mailPassword == null) {
            System.err.println("EmailService no configurado correctamente");
            return;
        }

        Session session = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUser, mailPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUser));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject(asunto);
            message.setText(cuerpo);

            Transport.send(message);
            System.out.println("Email enviado correctamente a: " + destino);

        } catch (MessagingException e) {
            System.err.println("Error al enviar email a " + destino + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
