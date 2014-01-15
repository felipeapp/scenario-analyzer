/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 30/05/2005
 */
package br.ufrn.arq.util;

/**
 * Classe privada usada para manipular links;
 * 
 * @author Rafael Borja
 * @author Gleydson Lima
 *  
 */
public class Link {
    /** Título do link */
    private String titulo;

    /** Endereço do link */
    private String url;

    public Link() {
        titulo = new String();
        url = new String();
    }

    public Link(String titulo, String url) {
        this.titulo = titulo;
        this.url = url;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}