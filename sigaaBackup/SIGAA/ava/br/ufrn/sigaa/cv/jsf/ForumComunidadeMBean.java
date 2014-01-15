/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
 * Managed-Bean para gerenciamento de Fóruns da Comunidade Virtual
 * 
 */
@Component @Scope("session")
public class ForumComunidadeMBean extends CadastroComunidadeVirtual<ForumComunidade> {

	private boolean notificar;
	
	/**
	 * Lista os fóruns de uma comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumComunidade/listar.jsp
	 */
	@Override
	public List<ForumComunidade> lista() {
		return getDAO(ForumComunidadeDao.class).findByComunidade(comunidade());
	}
	
	/**
	 * Padrão Specification. Verifica campos obrigatórios antes de cadastrar.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumComunidade/novo.jsp
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
					notification.addError("É obrigatório informar um título!");
				
				if ( isEmpty(ref.getDescricao()) )
					notification.addError("É obrigatório informar descrição!");
								
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Cadastra um novo Fórum.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ForumComunidade/novo.jsp
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
		
		notificarComunidade("Novo Fórum na Comunidade Virtual ", "<p>O fórum " + object.getTitulo() + 
				" foi cadastrado na turma pelo usuário " + getUsuarioLogado().getPessoa().getNome() 
				+ "</p><p>Para postar mensagens entre na Comunidade Virtual do SIGAA</p>");
		
		aposPersistir();
		return redirect("/cv/" + classe.getSimpleName() + "/listar.jsf");
	}
	
	/**
	 * Instancia o domínio
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
