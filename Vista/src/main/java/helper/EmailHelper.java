package helper;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailHelper {

    // Correo del gimnasio
    private static final String REMITENTE = "correo@gmail.com";
    // Contraseña de Aplicación
    private static final String PASSWORD = "appPassword";

    public static void enviarCorreo(String destinatario, String asunto, String mensajeCuerpo) throws Exception {

        // Configuracion de propiedades del servidor SMTP de Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        // props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Descomentar esta línea si da error de certificados

        // Iniciar sesión en el servidor
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, PASSWORD);
            }
        });

        // Crear el mensaje
        Message message = new MimeMessage(session);
        // El segundo parametro ("Rock On") es el nombre que le aparecera como remitente
        message.setFrom(new InternetAddress(REMITENTE, "Rock On Gym"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(asunto);

        // Asignar el texto.
        message.setText(mensajeCuerpo);

        // Envia correo
        Transport.send(message);
    }
}