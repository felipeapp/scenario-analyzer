/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.cv.dominio.ConteudoComunidade;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;

/**
 * Managed bean para cadastro de conteúdos para a turma virtual
 * 
 * @author David Pereira
 * 
 */
@Component @Scope("request")
public class ConteudoComunidadeMBean extends CadastroComunidadeVirtual<ConteudoComunidade> {

	/** Página de origem do fluxo. */
	private String paginaOrigem;
	
	/**
	 * Construtor
	 */
	public ConteudoComunidadeMBean() {
		object = new ConteudoComunidade();
		object.setTopico(new TopicoComunidade());
		classe = ReflectionUtils.getParameterizedTypeClass(this);
	}
	
	/**
	 * Lista os Conteúdos da comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ConteudoComunidade/listar.jsp
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@Override
	public List<ConteudoComunidade> lista() throws HibernateException, DAOException {

		ComunidadeVirtualMBean cvBean = getMBean("comunidadeVirtualMBean");
		boolean moderador = cvBean.getMembro() != null && cvBean.getMembro().isPermitidoModerar();
		
		return getDAO(ComunidadeVirtualDao.class).findConteudoComunidade(comunidade(), moderador, getUsuarioLogado());
	}

	/**
	 * Lista os Conteúdos da comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/principal.jsp
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<ConteudoComunidade> getListaPermanentes() throws HibernateException, DAOException {
		
		ComunidadeVirtualMBean cvBean = getMBean("comunidadeVirtualMBean");
		boolean moderador = cvBean.getMembro() != null && cvBean.getMembro().isModerador();
		
		return getDAO(ComunidadeVirtualDao.class).findConteudoPermanenteByComunidade(comunidade(),moderador,getUsuarioLogado());
	}
	
	/**
	 * Remove um Conteúdo da Comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ConteudoComunidade/listar.jsp
	 */
	@Override
	public String remover() {

		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
			antesRemocao();
			prepare(SigaaListaComando.REMOVER_CV);
			Notification notification = execute(SigaaListaComando.REMOVER_CV,
					object, getEspecificacaoRemocao());

			if (notification.hasMessages())
				return notifyView(notification);

			listagem = null;
			flash("Conteúdo removido com sucesso.");

		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// força recarregar os tópicos de aulas
		clearCacheTopicosAula();

		return redirect(getPaginaListagem());
	}

	/**
	 * Edita um Conteúdo da comunidade.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ConteudoComunidade/listar.jsp
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
	 * Atualizar um Conteúdo.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ConteudoComunidade/editar.jsp 
	 */
	@Override
	public String atualizar() throws DAOException {
		object.setComunidade(comunidade());
		antesPersistir();
		
		Specification specification = getEspecificacaoAtualizacao();
		if (specification == null || specification instanceof NullSpecification ) 
			specification = getEspecificacaoCadastro();
		
		Notification notification = execute(SigaaListaComando.ATUALIZAR_CV, object, specification);
		
		if (notification.hasMessages()) {
			prepare(SigaaListaComando.ATUALIZAR_CV);
			return notifyView(notification);
		}
		
		if ( object.isNotificarMembros() ) {
			String assunto = "Conteúdo atualizado(a) na comunidade virtual: " + comunidade().getNome();
			String texto = "Um Conteúdo foi atualizado(a) na comunidade virtual  <b>"+comunidade().getNome()+"</b> <p>Para visualizar acesse a turma virtual no SIGAA.</p>";
			notificarComunidade(assunto,texto);
		}

		listagem = null;
		flash("Atualizado com sucesso.");
		aposPersistir();
		
		if ( paginaOrigem.equals("portalPrincipal") )
			return forward(getPaginaPrincipal());
		else 
			return forward(getPaginaListagem());
	}

	/**
	 * Padrão Specification. Valida campos obrigatórios.<br/><br/>
	 * 
	 * Não invocado por JSP.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();

			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				ConteudoComunidade cc = (ConteudoComunidade) objeto;
				if (cc.getTopico() == null || cc.getTopico().getId() == 0)
					notification
							.addError("É obrigatório informar o tópico da comunidade");
				if (isEmpty(cc.getNome()))
					notification
							.addError("É obrigatório informar o título do conteúdo");
				if (isEmpty(cc.getConteudo()))
					notification.addError("É obrigatório informar o conteúdo");
				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Abre página para inserir novo Conteúdo.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/cv/ConteudoComunidade/listar.jsp
	 * 
	 * @param idTopicoSelecionado
	 */
	public void inserirConteudo(Integer idTopicoSelecionado) {
		object = new ConteudoComunidade();
		object.setTopico(new TopicoComunidade(idTopicoSelecionado));
		forward("/cv/ConteudoComunidade/novo.jsp");
	}

	public String getPaginaOrigem() {
		return paginaOrigem;
	}

	public void setPaginaOrigem(String paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}
	
}