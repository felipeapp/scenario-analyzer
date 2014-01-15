/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
*  Created on 05/04/2010
*
*/
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.FormaParticipacaoAtividade;

/**
 * MBean para facilitar o uso da entidade {@link FormaParticipacaoAtividade} na view
 * 
 * @author henrique
 */
@Component("formaParticipacaoAtividade")
@Scope("session")
public class FormaParticipacaoAtividadeMBean extends SigaaAbstractController<FormaParticipacaoAtividade> {
	
	/** Cole��o de selecItem de todas as poss�veis formas de participa��o em atividades. */
	private Collection<SelectItem> todasFormasCombo = null;
	
	/** Cole��o de selecItem das poss�veis formas de participa��o em atividades do tipo Est�gio. */
	private Collection<SelectItem> formasEstagioCombo = null;

	/** Cole��o de selecItem das poss�veis formas de participa��o em atividades do tipo Trabalho de Conclus�o de Curso (TCC). */
	private Collection<SelectItem> formasTCCCombo;
	
	/** Retorna uma cole��o de selecItem de todas as poss�veis formas de participa��o em atividades. 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		if (todasFormasCombo == null) {
			Collection<FormaParticipacaoAtividade> formas = getGenericDAO().findAll(FormaParticipacaoAtividade.class);
			todasFormasCombo = toSelectItems(formas, "id", "descricao");
		}
		return todasFormasCombo;
	}
	
	/** Retorna uma cole��o de selecItem das poss�veis formas de participa��o em atividades do tipo Est�gio. 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getFormasEstagioCombo() throws DAOException {
		if (formasEstagioCombo == null) {
			formasEstagioCombo = new ArrayList<SelectItem>();
			for (FormaParticipacaoAtividade forma : getGenericDAO().findAll(FormaParticipacaoAtividade.class)) {
				switch (forma.getId()) {
				case FormaParticipacaoAtividade.ORIENTACAO_INDIVIDUAL:
				case FormaParticipacaoAtividade.ESPECIAL_COLETIVA:
					formasEstagioCombo.add(new SelectItem(forma.getId(), forma.getDescricao()));
					break;
				}
			}
		}
		return formasEstagioCombo;
	}

	/** Retorna uma cole��o de selecItem das poss�veis formas de participa��o em atividades do tipo Trabalho de Conclus�o de Curso (TCC). 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getFormasTCCCombo() throws DAOException {
		if (formasTCCCombo == null) {
			formasTCCCombo = new ArrayList<SelectItem>();
			for (FormaParticipacaoAtividade forma : getGenericDAO().findAll(FormaParticipacaoAtividade.class)) {
				switch (forma.getId()) {
				case FormaParticipacaoAtividade.ORIENTACAO_INDIVIDUAL:
					formasTCCCombo.add(new SelectItem(forma.getId(), forma.getDescricao()));
					break;
				}
			}
		}
		return formasTCCCombo;
	}
	
}
