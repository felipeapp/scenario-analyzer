/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/12/2008'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.GrupoPublicoAlvo;

/**
 * MBean para efetuar as operações de CRUD de GrupoPublicoAlvo
 * 
 * @author leonardo
 *
 */
@Component("grupoPublicoAlvo") @Scope("request")
public class GrupoPublicoAlvoMBean extends SigaaAbstractController<GrupoPublicoAlvo> {

	
	public GrupoPublicoAlvoMBean() {
		obj = new GrupoPublicoAlvo();
	}
	
	/**
	 * Retorna todos os grupos de público alvo em ordem alfabética
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoPublicoAlvo> getAllGrupoPublicoAlvo() throws DAOException{
		return getGenericDAO().findAllAtivos(GrupoPublicoAlvo.class, "descricao");
	}
	
	/**
	 * Método chamado para entrar no modo de remoção
	 * a partir da Jsp:
	 * /sigaa.war/extensao/GrupoPublicoAlvo/lista.jsp
	 *
	 * @return
	 */
	public String preInativar() {

		try {
			setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
			prepareMovimento(ArqListaComando.DESATIVAR);
			populateObj(true);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}

		setReadOnly(true);
		setConfirmButton("Remover");
		return forward(getFormPage());

	}
	
	@Override
	public String getLabelCombo() {
		return "descricao";
	}
}