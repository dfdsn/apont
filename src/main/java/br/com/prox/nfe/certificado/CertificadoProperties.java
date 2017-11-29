package br.com.prox.nfe.certificado;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;

import br.com.prox.nfe.certificado.exception.CertificadoException;

/**
 * Classe para uso exclusivo em caso do Erro Unknow CA
 */
public class CertificadoProperties {

    public static void inicia(Certificado certificado , InputStream iSCacert ) throws CertificadoException {

        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
        System.clearProperty("javax.net.ssl.trustStore");

        System.setProperty("jdk.tls.client.protocols", "TLSv1.2"); // Servidor do	Sefaz RS

        if(certificado.getTipo().equals(Certificado.WINDOWS)){
            System.setProperty("javax.net.ssl.keyStoreProvider", "SunMSCAPI");
            System.setProperty("javax.net.ssl.keyStoreType", "Windows-MY");
            System.setProperty("javax.net.ssl.keyStoreAlias", certificado.getNome());
        }else if(certificado.getTipo().equals(Certificado.ARQUIVO) || certificado.getTipo().equals(Certificado.ARQUIVO_BYTES)){
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.keyStore", certificado.getArquivo());
        }

        System.setProperty("javax.net.ssl.keyStorePassword",certificado.getSenha());

        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        //Extrair Cacert do Jar
        String cacert = "";
        try {
            File file = File.createTempFile("tempfile", ".tmp");
            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = iSCacert.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.close();
            cacert = file.getAbsolutePath();
            file.deleteOnExit();
        } catch (IOException ex) {
            throw new CertificadoException(ex.getMessage());
        }

        System.setProperty("javax.net.ssl.trustStore", cacert);

    }
}
