/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/03/2013
 *
 */
package br.ufrn.sigaa.estagio.jsf;

import java.util.Collection;
import java.util.LinkedList;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.estagio.dominio.StatusConvenioEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusConvenioEstagio;

/** Controller responsável por operar status de convênios.
 * 
 * @author Édipo Elder F. de Melo
 *
 */
@Component("statusConvenioEstagioMBean") @Scope("request")
public class StatusConvenioEstagioMBean extends SigaaAbstractController<StatusConvenioEstagio> {
	
	/** Todos possíveis situações de um convênio. */
	private Collection<SelectItem>  allSituacoesConvenioCombo;
	/** Possíveis situações de um convênio, específicas para o módulo que o usuário está utilizando. */
	private Collection<SelectItem> situacoesConvenioCombo;

	public StatusConvenioEstagioMBean() {
		obj = new StatusConvenioEstagio();
	}
	/**
	 * Retorna uma coleção de selectitem de Status de Convênio de Estágio.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		if (allSituacoesConvenioCombo == null){
			Collection<StatusConvenioEstagio> situacoes = new LinkedList<StatusConvenioEstagio>();
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.SUBMETIDO, "SUBMETIDO"));
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.EM_ANALISE, "EM ANÁLISE"));
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.APROVADO, "APROVADO"));
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.FINALIZADO, "FINALIZADO"));
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.RECUSADO, "RECUSADO"));
			allSituacoesConvenioCombo = toSelectItems(situacoes, "id", "descricao");
		}
		return allSituacoesConvenioCombo;
	}

	/**
	 * Retorna uma coleção de selectitem de Status de Convênio de Estágio específica para o módulo que o usuário está utilizando.
	 * 
	 * @return
	 */	
	public Collection<SelectItem> getStatusConvenioEspecificosCombo() {
		if (situacoesConvenioCombo == null) {
			Collection<StatusConvenioEstagio> situacoes = new LinkedList<StatusConvenioEstagio>();
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.SUBMETIDO, "SUBMETIDO"));
			if (isConvenioEstagio()) {
				situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.EM_ANALISE, "EM ANÁLISE"));
				situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.APROVADO, "APROVADO"));
				situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.FINALIZADO, "FINALIZADO"));
				situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.RECUSADO, "RECUSADO"));
			}
			situacoesConvenioCombo = toSelectItems(situacoes, "id", "descricao");
		}
		return situacoesConvenioCombo;
	}
}
