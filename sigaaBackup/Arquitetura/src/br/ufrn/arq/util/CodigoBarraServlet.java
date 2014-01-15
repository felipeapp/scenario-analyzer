/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/05/2005
 */
package br.ufrn.arq.util;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Classe para criação do código de Barras
 * 
 * @author Gleydson Lima
 *  
 */
public class CodigoBarraServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String codigo = req.getParameter("codigo");
        if (codigo.length() % 2 == 1) {
            codigo = "0" + codigo;
        }
        criaImagem(codigo, res.getOutputStream(), 50);

    }

    public boolean criaImagem(String texto, OutputStream out, int h)

    throws IOException {
        int i, j, tam;
        /*
         * Variavel que contem a representacao em Codigo de Barras de cada um
         * dos numeros. Nessa representacao: 0 significa Barra Fina (NARROW); 1
         * significa Barra Grossa (WIDE).
         */

        String[] barras = { "00110", //0
                "10001", //1
                "01001", //2
                "11000", //3
                "00101", //4
                "10100", //5
                "01100", //6
                "00011", //7
                "10010", //8
                "01010" };//9
        //O Codigo de barras e formado sempre por pares intercalados.
        //Por exemplo 12:
        //Pegando-se a representacao do 1 e do 2 na variavel acima teriamos o
        // seguinte:
        //1001000011.
        //Com isso, o primeiro numero representa as Barras Pretas (Fina ou
        // Grossa)
        // e o Segundo numero representa as Barras Brancas, ou espacos, (Fina ou
        // Grossa).
        int preto, branco;
        int w = 9 + (9 * texto.length());

        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        tam = 0;
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h); //começo do Codigo de Barras = 0 = 00 / 1 = 00
        g.setColor(Color.black);
        g.fillRect(tam, 0, tam + 1, h);
        tam++;
        g.setColor(Color.white);
        g.fillRect(tam, 0, tam + 1, h);
        tam++;
        g.setColor(Color.black);
        g.fillRect(tam, 0, tam + 1, h);
        tam++;
        g.setColor(Color.white);
        g.fillRect(tam, 0, tam + 1, h);
        tam++;
        //Conteudo do Codigo de Barras
        for (i = 0; i <= texto.length() - 1; i++) {
            preto = Integer.parseInt(String.valueOf(texto.charAt(i)));
            branco = Integer.parseInt(String.valueOf(texto.charAt(i + 1)));
            i++;
            for (j = 0; j < 5; j++) {
                g.setColor(Color.black);
                if (String.valueOf(barras[preto].charAt(j)).equals("0")) {
                    g.fillRect(tam, 0, tam + 1, h);
                    tam++;
                } else {
                    g.fillRect(tam, 0, tam + 3, h);
                    tam += 3;
                }
                g.setColor(Color.white);
                if (String.valueOf(barras[branco].charAt(j)).equals("0")) {
                    g.fillRect(tam, 0, tam + 1, h);
                    tam++;
                } else {
                    g.fillRect(tam, 0, tam + 3, h);
                    tam += 3;
                }
            }
        }

        //fim da barra = 0 = 10 / 1 = 0
        g.setColor(Color.black);
        g.fillRect(tam, 0, tam + 3, h);
        tam += 3;
        g.setColor(Color.white);
        g.fillRect(tam, 0, tam + 1, h);
        tam++;
        g.setColor(Color.black);
        g.fillRect(tam, 0, tam + 1, h);
        tam++;

		ImageIO.write(image, "jpeg", out);
        //GifEncoder encoder = new GifEncoder(BarImage ,outb);
        //encoder.encode();
        // ESSAS DUAS LINHAS ACIMA CRIARIAM UM .gif
        out.close();
        return true;
    }
}

