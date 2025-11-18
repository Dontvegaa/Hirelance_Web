package com.example.Hirelance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoRecuperacion(String destinatario, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@hirelance.com");
        message.setTo(destinatario);
        message.setSubject("Restablecer Contraseña - Hirelance");
        message.setText("Hola,\n\n"
                + "Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace:\n"
                + link + "\n\n"
                + "Si no fuiste tú, ignora este mensaje.");

        mailSender.send(message);
    }
}