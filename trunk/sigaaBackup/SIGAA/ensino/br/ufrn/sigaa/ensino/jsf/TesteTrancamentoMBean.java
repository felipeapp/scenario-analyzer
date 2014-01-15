/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Aug 21, 2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.negocio.dominio.TrancamentoMatriculaMov;

/**
 *
 * @author Victor Hugo
 *
 */
@Component("testeTrancamento")
@Scope("request")
//Suppress necessário por que esse MBean não
@SuppressWarnings("unchecked")
public class TesteTrancamentoMBean extends SigaaAbstractController {

	/**
	 * Chamado por:
	 * <ul>
	 * <li>/graduacao/menus/aluno.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String consolidarTrancamentos() throws ArqException{

		prepareMovimento(SigaaListaComando.CONSOLIDAR_SOLICITACOES_TRANCAMENTO);

		TrancamentoMatriculaMov mov = new TrancamentoMatriculaMov();
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_SOLICITACOES_TRANCAMENTO);
		mov.setCalendarioAcademicoAtual(getCalendarioVigente());
		try {
			execute(mov, getCurrentRequest());
		} catch (Exception e) {
			addMensagemErro("ERRO DURANTE A CONSOLIDACAO DE TRANCAMENTOS PENDENTES" + e.getMessage());
			notifyError(e);
			e.printStackTrace();
			return null;
		}

		addMessage("CONSOLIDACAO DE TRANCAMENTOS PENDENTES REALIZADO COM SUCESSO!", TipoMensagemUFRN.INFORMATION);

		return null;
	}

}
