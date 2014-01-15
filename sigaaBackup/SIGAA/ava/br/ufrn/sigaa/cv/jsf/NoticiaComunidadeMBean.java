/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.cv.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.cv.dominio.NoticiaComunidade;

/**
 * Managed bean para cadastro de not�cias para a Comunidade Virtual
 * 
 * @author David Pereira
 */
@Component @Scope("request")
public class NoticiaComunidadeMBean extends CadastroComunidadeVirtual<NoticiaComunidade> {

	private String paginaOrigem;
	private boolean notificar;
	private boolean editar = false;
	
	/**
	 * Construtor
	 */
	public NoticiaComunidadeMBean() {
		object = new NoticiaComunidade();
		classe = ReflectionUtils.getParameterizedTypeClass(this);
	}
	
	/**
	 * Ap�s cadastrar, d� forward para a p�gina de listagem.<br/><br/>
	 * 
	 * N�o invocado por JSP.
	 */
	public String forwardCadastrar() {
		return getPaginaListagem();
	}
	
	/**
	 * Retorna lista de not�cias da comunidade.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/NoticiaComunidade/listar.jsp
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@Override
	public List<NoticiaComunidade> lista() throws HibernateException, DAOException {
		return getDAO(ComunidadeVirtualDao.class).findNoticiasByComunidade(comunidade());
	}
	
	/**
	 * Cadastra uma not�cia.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/NoticiaComunidade/novo.jsp
	 */
	@Override
	public String cadastrar() throws DAOException {
		try {
			editar = false;
			return super.cadastrar();
			
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 
	}
	
	/**
	 * Edita uma not�cia.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/NoticiaComunidade/editar.jsp
	 */
	@Override
	public String atualizar() throws DAOException {
		try {
			editar = true;
			return super.atualizar();
			
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 
	}
	
	
	/**
	 * Edita uma not�cia.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/NoticiaComunidade/listar.jsp
	 */
	@Override
	public String editar() throws DAOException {
		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
			instanciarAposEditar();
			prepare(SigaaListaComando.ATUALIZAR_CV);
			paginaOrigem = getParameter("paginaOrigem");
			return forward(getPaginaEdicao());
			
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 
	}
	
	/**
	 * Redireciona para p�gina de listagem.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/NoticiaComunidade/listar.jsp
	 */
	@Override
	public String listar() {
		return redirect("/cv/NoticiaComunidade/listar.jsf");
	}

	/**
	 * Padr�o Specification. Verifica preenchimento dos campos.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/cv/NoticiaComunidade/novo.jsp
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				NoticiaComunidade noticia = (NoticiaComunidade) objeto;
				if (isEmpty(noticia.getDescricao()))
					notification.addError("O t�tulo � obrigat�rio");
				if (isEmpty(noticia.getDescricao()))
					notification.addError("O texto � obrigat�rio");
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Ap�s persistir uma not�cia, verifica se deve ou n�o notificar os usu�rios da comunidade.
	 */
	@Override
	protected void aposPersistir() throws DAOException {
		if (isNotificar()) { // Envia e-mail para os alunos
			if (!editar)
				notificarComunidade("Nova Not�cia cadastrado(a) na comunidade virtual: " + comunidade().getNome() , "<h1>" + object.getDescricao() + "</h1>" + object.getNoticia());
			else
				notificarComunidade("Uma Not�cia foi atualizado(a) na comunidade virtual: " + comunidade().getNome() , "<h1>" + object.getDescricao() + "</h1>" + object.getNoticia());
		}
	}

	public boolean isNotificar() {
		return notificar;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}

	
	public String getPaginaOrigem() {
		return paginaOrigem;
	}

	public void setPaginaOrigem(String paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}

	public void setEditar(boolean editar) {
		this.editar = editar;
	}

	public boolean isEditar() {
		return editar;
	}
}