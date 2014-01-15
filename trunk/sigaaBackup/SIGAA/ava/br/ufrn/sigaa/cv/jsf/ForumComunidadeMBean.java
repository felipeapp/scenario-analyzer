/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ForumComunidadeDao;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.cv.dominio.ForumComunidade;

/**
 * Managed-Bean para gerenciamento de F�runs da Comunidade Virtual
 * 
 */
@Component @Scope("session")
public class ForumComunidadeMBean extends CadastroComunidadeVirtual<ForumComunidade> {

	private boolean notificar;
	
	/**
	 * Lista os f�runs de uma comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/ForumComunidade/listar.jsp
	 */
	@Override
	public List<ForumComunidade> lista() {
		return getDAO(ForumComunidadeDao.class).findByComunidade(comunidade());
	}
	
	/**
	 * Padr�o Specification. Verifica campos obrigat�rios antes de cadastrar.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/ForumComunidade/novo.jsp
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				ForumComunidade ref = (ForumComunidade) objeto;
				if (isEmpty(ref.getTitulo()))
					notification.addError("� obrigat�rio informar um t�tulo!");
				
				if ( isEmpty(ref.getDescricao()) )
					notification.addError("� obrigat�rio informar descri��o!");
								
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Cadastra um novo F�rum.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/ForumComunidade/novo.jsp
	 */
	@Override
	public String cadastrar() throws DAOException {
		
		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_CV);
		} catch (ArqException e1) {		
			e1.printStackTrace();
		}
		
		object.setComunidade(comunidade());
		antesPersistir();
		Notification notification = execute(SigaaListaComando.CADASTRAR_CV, object, getEspecificacaoCadastro());
		
		if (notification.hasMessages())
			return notifyView(notification);
		
		listagem = null;
		flash("Cadastrado com sucesso.");
		
		notificarComunidade("Novo F�rum na Comunidade Virtual ", "<p>O f�rum " + object.getTitulo() + 
				" foi cadastrado na turma pelo usu�rio " + getUsuarioLogado().getPessoa().getNome() 
				+ "</p><p>Para postar mensagens entre na Comunidade Virtual do SIGAA</p>");
		
		aposPersistir();
		return redirect("/cv/" + classe.getSimpleName() + "/listar.jsf");
	}
	
	/**
	 * Instancia o dom�nio
	 */
	@Override
	public void instanciar() {		
		object = new ForumComunidade();
	}
	
	/**
	 * Antes de remover, faz o detach 
	 */
	@Override
	protected void antesRemocao() throws DAOException {
		getGenericDAO().detach(object);
	}
	
	public boolean isNotificar() {
		return notificar;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}
}
