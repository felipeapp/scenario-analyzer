/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/12/2006
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.extensao.relatorio.dominio.AtividadeDesenvolvida;
import br.ufrn.sigaa.extensao.relatorio.dominio.DetalhamentoRecursos;

/*******************************************************************************
 * Todos os relat�rios de atividades de extens�o tem informa��es em comum. Essas
 * informa��es em comum s�o mapeadas por essa classe.
 * 
 * @author Gleydson
 * 
 * @param <T>
 ******************************************************************************/
public class AbstractRelatorioAtividadeMBean<T> extends SigaaAbstractController<T> {

	/** Auxilia a inclus�o, pelo coordenador, dos dados referentes ao or�amento realizado na a��o. */
	private Collection<DetalhamentoRecursos> detalhamentoRecursos = new ArrayList<DetalhamentoRecursos>();

	/** Auxilia a inclus�o, pelo coordenador, dos dados referentes �s atividades desenvolvidas durante a execu��o da a��o. */
	private Collection<AtividadeDesenvolvida> atividadesDesenvolvidas = new ArrayList<AtividadeDesenvolvida>();

	/**
	 * Carrega os �tens da cole��o de detalhamento financeiro
	 * 
	 * M�todo n�o chamado por jsp.
	 * 
	 * @return
	 */
	public void carregaDetalhamentoFinanceiro() {

		Collection<ElementoDespesa> elementos = getAllObj(ElementoDespesa.class);
		for (ElementoDespesa e : elementos) {
			DetalhamentoRecursos det = new DetalhamentoRecursos();
			det.setElemento(e);
			detalhamentoRecursos.add(det);
		}

	}

	/**
	 * Cria uma nova atividade para persisti-la
	 * 
	 * M�todo n�o chamado por jsp.
	 * 
	 */
	public void novaAtividade(ActionEvent e) {
		atividadesDesenvolvidas.add(new AtividadeDesenvolvida());
	}

	public Collection<AtividadeDesenvolvida> getAtividadesDesenvolvidas() {
		return atividadesDesenvolvidas;
	}

	public void setAtividadesDesenvolvidas(
			Collection<AtividadeDesenvolvida> atividadesDesenvolvidas) {
		this.atividadesDesenvolvidas = atividadesDesenvolvidas;
	}

	public Collection<DetalhamentoRecursos> getDetalhamentoRecursos() {
		return detalhamentoRecursos;
	}

	public void setDetalhamentoRecursos(
			Collection<DetalhamentoRecursos> detalhamentoRecursos) {
		this.detalhamentoRecursos = detalhamentoRecursos;
	}

}
