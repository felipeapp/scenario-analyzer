/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/05/2009'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.HashMap;

/**
 * Classe auxiliar para exibição na view do item 8.61 do relatório de avaliação para pesquisa.
 * 
 * @author Leonardo
 *
 */
public class ComponentesEnsinoPosStricto extends AbstractAvaliacaoDocente implements ViewAtividadeBuilder {

	private Long numeroComponentes;
	
	public String getItemView() {
		return "<td>Número de componentes ministrados</td>" +
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
		return 	"	    <td>Contabilização</td>" +
		"	    <td style=\"text-align:right\">Quantidade</td>";
	}

	public Long getNumeroComponentes() {
		return numeroComponentes;
	}

	public void setNumeroComponentes(Long numeroComponentes) {
		this.numeroComponentes = numeroComponentes;
	}

}
