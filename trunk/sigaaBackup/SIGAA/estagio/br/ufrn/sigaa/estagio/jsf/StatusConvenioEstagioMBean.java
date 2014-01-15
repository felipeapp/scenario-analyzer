/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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

/** Controller respons�vel por operar status de conv�nios.
 * 
 * @author �dipo Elder F. de Melo
 *
 */
@Component("statusConvenioEstagioMBean") @Scope("request")
public class StatusConvenioEstagioMBean extends SigaaAbstractController<StatusConvenioEstagio> {
	
	/** Todos poss�veis situa��es de um conv�nio. */
	private Collection<SelectItem>  allSituacoesConvenioCombo;
	/** Poss�veis situa��es de um conv�nio, espec�ficas para o m�dulo que o usu�rio est� utilizando. */
	private Collection<SelectItem> situacoesConvenioCombo;

	public StatusConvenioEstagioMBean() {
		obj = new StatusConvenioEstagio();
	}
	/**
	 * Retorna uma cole��o de selectitem de Status de Conv�nio de Est�gio.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		if (allSituacoesConvenioCombo == null){
			Collection<StatusConvenioEstagio> situacoes = new LinkedList<StatusConvenioEstagio>();
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.SUBMETIDO, "SUBMETIDO"));
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.EM_ANALISE, "EM AN�LISE"));
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.APROVADO, "APROVADO"));
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.FINALIZADO, "FINALIZADO"));
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.RECUSADO, "RECUSADO"));
			allSituacoesConvenioCombo = toSelectItems(situacoes, "id", "descricao");
		}
		return allSituacoesConvenioCombo;
	}

	/**
	 * Retorna uma cole��o de selectitem de Status de Conv�nio de Est�gio espec�fica para o m�dulo que o usu�rio est� utilizando.
	 * 
	 * @return
	 */	
	public Collection<SelectItem> getStatusConvenioEspecificosCombo() {
		if (situacoesConvenioCombo == null) {
			Collection<StatusConvenioEstagio> situacoes = new LinkedList<StatusConvenioEstagio>();
			situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.SUBMETIDO, "SUBMETIDO"));
			if (isConvenioEstagio()) {
				situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.EM_ANALISE, "EM AN�LISE"));
				situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.APROVADO, "APROVADO"));
				situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.FINALIZADO, "FINALIZADO"));
				situacoes.add(new StatusConvenioEstagio(StatusConvenioEstagio.RECUSADO, "RECUSADO"));
			}
			situacoesConvenioCombo = toSelectItems(situacoes, "id", "descricao");
		}
		return situacoesConvenioCombo;
	}
}
