package br.com.jardelbarbosa.cliente.utils;

import org.springframework.util.StringUtils;

public class TextoUltils {

    public static String removeEspecialCaracter(String valueCaracter){
        if (valueCaracter != null) {
            return valueCaracter.replaceAll("[^0-9]", "");
        }
        return null;
    }
    public static Boolean contemTexto(String texto) {
        return StringUtils.hasText(texto);
    }
}
