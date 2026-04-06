package ar.com.gav.backend.fotografo.security;

/**
 * Utilidad temporal para generar un hash de contraseña válido
 * para la base de datos usando la librería BCrypt del proyecto.
 * 
 * Instrucciones:
 * 1. Click derecho en este archivo -> Run File.
 * 2. Copiar el hash que aparece en la consola.
 * 3. Ejecutar el SQL en HeidiSQL con ese hash.
 */
public class GenerarPassword {
    public static void main(String[] args) {
        String passwordPlana = "123456";
        String hash = PasswordUtil.hashPassword(passwordPlana);
        
        System.out.println("==========================================================");
        System.out.println("HASH GENERADO PARA LA CONTRASEÑA: " + passwordPlana);
        System.out.println("COPIA LA LÍNEA DE ABAJO PARA TU SQL:");
        System.out.println(hash);
        System.out.println("==========================================================");
    }
}
