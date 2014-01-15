/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Todos os relatórios de atividades de extensão tem informações em comum. Essas
 * informações em comum são mapeadas por essa classe.
 * 
 * @author Gleydson
 * 
 * @param <T>
 ******************************************************************************/
public class AbstractRelatorioAtividadeMBean<T> extends SigaaAbstractController<T> {

	/** Auxilia a inclusão, pelo coordenador, dos dados referentes ao orçamento realizado na ação. */
	private Collection<DetalhamentoRecursos> detalhamentoRecursos = new ArrayList<DetalhamentoRecursos>();

	/** Auxilia a inclusão, pelo coordenador, dos dados referentes às atividades desenvolvidas durante a execução da ação. */
	private Collection<AtividadeDesenvolvida> atividadesDesenvolvidas = new ArrayList<AtividadeDesenvolvida>();

	/**
	 * Carrega os ítens da coleção de detalhamento financeiro
	 * 
	 * Método não chamado por jsp.
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
	 * Método não chamado por jsp.
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
