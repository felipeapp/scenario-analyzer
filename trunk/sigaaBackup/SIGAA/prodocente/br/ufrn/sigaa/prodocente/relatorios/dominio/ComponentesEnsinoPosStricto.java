/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '05/05/2009'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.HashMap;

/**
 * Classe auxiliar para exibi��o na view do item 8.61 do relat�rio de avalia��o para pesquisa.
 * 
 * @author Leonardo
 *
 */
public class ComponentesEnsinoPosStricto extends AbstractAvaliacaoDocente implements ViewAtividadeBuilder {

	private Long numeroComponentes;
	
	public String getItemView() {
		return "<td>N�mero de componentes ministrados</td>" +
		"<td style=\"text-align:right\">" + numeroComponentes + "</td>";
	}

	public HashMap<String, String> getItens() {
		return null;
	}

	public float getQtdBase() {
		return  receberPontuacaoMaxima() ? 10 : 5;
	}

	private boolean receberPontuacaoMaxima() {
		return numeroComponentes > 1;
	}
	
	public String getTituloView() {
		return 	"	    <td>Contabiliza��o</td>" +
		"	    <td style=\"text-align:right\">Quantidade</td>";
	}

	public Long getNumeroComponentes() {
		return numeroComponentes;
	}

	public void setNumeroComponentes(Long numeroComponentes) {
		this.numeroComponentes = numeroComponentes;
	}

}
