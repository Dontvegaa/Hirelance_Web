package com.example.Hirelance.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class main {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // La contraseña que quieres usar
        String miPassword = "password";

        // Generar el hash
        String hash = encoder.encode(miPassword);

        System.out.println("--- COPIA ESTE CÓDIGO ABAJO ---");
        System.out.println(hash);
        System.out.println("-------------------------------");
    }
}