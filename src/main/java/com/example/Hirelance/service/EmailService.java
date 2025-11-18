package com.example.Hirelance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // El email verificado en SendGrid (debe coincidir con Single Sender)
    @Value("${spring.mail.username:lilvegaxx@gmail.com}")
    private String fromEmail;

    /**
     * Envía un correo de recuperación de contraseña
     * @param destinatario Email del usuario
     * @param enlaceRecuperacion Link para resetear la contraseña
     */
    public void enviarCorreoRecuperacion(String destinatario, String enlaceRecuperacion) {
        SimpleMailMessage mensaje = new SimpleMailMessage();

        // ⚠️ CRÍTICO: Debe ser el email verificado en SendGrid
        mensaje.setFrom("lilvegaxx@gmail.com");

        mensaje.setTo(destinatario);
        mensaje.setSubject("Recuperación de Contraseña - Hirelance");
        mensaje.setText(
                "Hola,\n\n" +
                        "Has solicitado restablecer tu contraseña en Hirelance.\n\n" +
                        "Haz clic en el siguiente enlace para continuar:\n" +
                        enlaceRecuperacion + "\n\n" +
                        "Este enlace expirará en 24 horas.\n\n" +
                        "Si no solicitaste este cambio, ignora este correo.\n\n" +
                        "Saludos,\n" +
                        "Equipo de Hirelance"
        );

        try {
            mailSender.send(mensaje);
            System.out.println("✅ Correo enviado exitosamente a: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperación", e);
        }
    }

    /**
     * Método genérico para enviar cualquier tipo de email
     * @param destinatario Email destino
     * @param asunto Asunto del email
     * @param contenido Cuerpo del mensaje
     */
    public void enviarCorreo(String destinatario, String asunto, String contenido) {
        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setFrom("lilvegaxx@gmail.com"); // Email verificado en SendGrid
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(contenido);

        try {
            mailSender.send(mensaje);
            System.out.println("✅ Correo enviado exitosamente a: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo", e);
        }
    }
}