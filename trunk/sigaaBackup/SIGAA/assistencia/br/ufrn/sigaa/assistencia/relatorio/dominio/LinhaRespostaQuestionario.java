/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/05/2009
 *
 */	
package br.ufrn.sigaa.assistencia.relatorio.dominio;

import br.ufrn.sigaa.questionario.dominio.Alternativa;

/**
 * Usada para agregar informações obtidas através do relatório
 * 
 * @author Henrique André
 *
 */
public class LinhaRespostaQuestionario {
	
	private RelatorioQuestionarioCadastroUnico relatorio = new RelatorioQuestionarioCadastroUnico();
	
	private Alternativa alternativa = new Alternativa();
	/**
	 * Número de pessoas que escolheram a alternativa
	 */
	private Long total;
	private Double porcentegem;

	public Alternativa getAlternativa() {
		return alternativa;
	}

	public void setAlternativa(Alternativa alternativa) {
		this.alternativa = alternativa;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Double getPorcentegem() {
		porcentegem = ((100*total)/ new Double( relatorio.getTotalParticipantes() ));
		return porcentegem;
	}

	public void setPorcentegem(Double porcentegem) {
		this.porcentegem = porcentegem;
	}

	public RelatorioQuestionarioCadastroUnico getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(RelatorioQuestionarioCadastroUnico relatorio) {
		this.relatorio = relatorio;
	}
	
	
	
}
