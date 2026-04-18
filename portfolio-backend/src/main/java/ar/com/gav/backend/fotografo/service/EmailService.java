package ar.com.gav.backend.fotografo.service;

import ar.com.gav.backend.fotografo.config.AppConfig;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para envío de correos electrónicos usando Gmail SMTP.
 */
@Stateless
public class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());

    private Properties mailProperties;
    private String mailUser;
    private String mailPassword;

    @PostConstruct
    public void init() {
        Properties props = new Properties();

        // Base versionada (sin secretos)
        loadInto(props, "email.properties");

        // Override local opcional (ignorado por git)
        loadInto(props, "email.local.properties");

        // Prioridad final: variables APP_* (entorno)
        mailUser = AppConfig.get("APP_SMTP_USER", props.getProperty("mail.user"));
        mailPassword = AppConfig.get("APP_SMTP_PASSWORD", props.getProperty("mail.password"));

        mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", AppConfig.get("APP_SMTP_HOST", props.getProperty("mail.smtp.host", "smtp.gmail.com")));
        mailProperties.put("mail.smtp.port", AppConfig.get("APP_SMTP_PORT", props.getProperty("mail.smtp.port", "587")));
        mailProperties.put("mail.smtp.auth", AppConfig.get("APP_SMTP_AUTH", props.getProperty("mail.smtp.auth", "true")));
        mailProperties.put("mail.smtp.ssl.enable", AppConfig.get("APP_SMTP_SSL_ENABLE", props.getProperty("mail.smtp.ssl.enable", "false")));
        mailProperties.put("mail.smtp.starttls.enable", AppConfig.get("APP_SMTP_STARTTLS_ENABLE", props.getProperty("mail.smtp.starttls.enable", "true")));
        mailProperties.put("mail.smtp.ssl.trust", AppConfig.get("APP_SMTP_SSL_TRUST", props.getProperty("mail.smtp.ssl.trust", "smtp.gmail.com")));

        if (mailUser == null || mailPassword == null) {
            LOG.warning("EmailService no configurado: faltan APP_SMTP_USER / APP_SMTP_PASSWORD (o email.local.properties)");
        }
    }

    private void loadInto(Properties target, String resourceName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                return;
            }
            Properties loaded = new Properties();
            loaded.load(input);
            target.putAll(loaded);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Error al cargar " + resourceName, e);
        }
    }

    private boolean isConfigured() {
        return mailUser != null && mailPassword != null;
    }

    private String buildResetUrl(String token) {
        String frontendBaseUrl = AppConfig.getFrontendBaseUrl();
        return frontendBaseUrl + "/reset-password?token=" + token;
    }

    /**
     * Envía un email de recuperación de contraseña.
     */
    public void enviarRecuperacionPassword(String emailDestino, String token) {
        if (!isConfigured()) {
            LOG.warning("EmailService no configurado correctamente");
            return;
        }

        String asunto = "Recuperación de Contraseña - FotoPortfolio";
        String cuerpo = "Hola,\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Hacé click en el siguiente enlace para crear una nueva contraseña:\n"
                + buildResetUrl(token) + "\n\n"
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
        if (!isConfigured()) {
            LOG.warning("EmailService no configurado correctamente");
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
            LOG.info("Email enviado correctamente a: " + destino);

        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "Error al enviar email a " + destino, e);
        }
    }
}
