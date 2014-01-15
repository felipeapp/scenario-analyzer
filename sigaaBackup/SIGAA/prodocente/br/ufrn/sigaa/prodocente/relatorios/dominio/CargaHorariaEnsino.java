/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '30/04/2008'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.HashMap;

/**
 * @author Ricardo Wendell
 *
 */
public class CargaHorariaEnsino extends AbstractAvaliacaoDocente implements ViewAtividadeBuilder {

	private Long cargaHoraria;

	public String getItemView() {
		return "<td>" + getDescricao() + "</td>" +
		"<td style=\"text-align:right\">" + cargaHoraria + "</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("cargaHoraria", null);
		return itens;
	}

	public float getQtdBase() {
		return  receberPontuacaoMaxima() ? 15 : 10;
	}

	/**
	 * @return
	 */
	private boolean receberPontuacaoMaxima() {
		return cargaHoraria/15 > 4;
	}

	public String getTituloView() {
		return 	"	    <td>Contabilização</td>" +
			"	    <td style=\"text-align:right\">CH</td>";
	}

	public String getDescricao() {
		if (!receberPontuacaoMaxima()) {
			return "Carga horária de 4 créditos";
		} else {
			return "Carga horária acima de 4 créditos";
		}
	}

	public Long getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Long cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}


}
