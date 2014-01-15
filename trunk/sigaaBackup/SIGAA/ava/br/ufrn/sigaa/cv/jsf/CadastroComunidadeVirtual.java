/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 21/01/2008
 */
package br.ufrn.sigaa.cv.jsf;

import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.cv.dominio.DominioComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MaterialComunidade;

/**
 * Layer-Supertype (http://martinfowler.com/eaaCatalog/layerSupertype.html) 
 * para os managed beans que realizam CADASTRO na turma virtual.
 * 
 * @author David Pereira
 *
 */
public abstract class CadastroComunidadeVirtual<T extends DominioComunidadeVirtual> extends ControllerComunidadeVirtual {

	protected Class<T> classe;
	
	protected T object;
	
	protected List<T> listagem;
	
	/** Ordem para as listagens. */
	protected boolean crescente = false;
	
	/**
	 * Implementa a funcionalidade padrão para o cadastro de um objeto da comunidade virtual.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrar() throws ArqException {
		
		object.setComunidade(comunidade());
		antesPersistir();
		prepare(SigaaListaComando.CADASTRAR_CV);
		Notification notification = execute(SigaaListaComando.CADASTRAR_CV, object, getEspecificacaoCadastro());
		
		if ( object instanceof MaterialComunidade ) {
			MaterialComunidade material = (MaterialComunidade) object;
			if ( material.isNotificarMembros() ) {
				String assunto = (genero() == 'M' ? "Novo " : "Nova ") +material.getTipoMaterial()+ " cadastrado(a) na comunidade virtual: " + comunidade().getNome();
				String texto = (genero() == 'M' ? "Um " : "Uma ") +material.getTipoMaterial()+ " foi disponibilizado(a) na comunidade virtual: <b>"+comunidade().getNome()+"</b> <p>Para visualizar acesse a comunidade virtual no SIGAA.</p>";
				notificarComunidade(assunto,texto);
			}
		}
		
		if (notification.hasMessages())
			return notifyView(notification);
		
		listagem = null;
		TopicoComunidadeMBean bean = (TopicoComunidadeMBean) getMBean("topicoComunidadeMBean");
		bean.setTopicosComunidade(null);
		
		flash(entityName()  + " " + (genero() == 'M' ? "cadastrado" : "cadastrada") + " com sucesso.");
		aposPersistir();
		return redirect(forwardCadastrar());
	}

	/**
	 * Encaminha o usuário para a página padrão após um cadastro.
	 * @return
	 */
	public String forwardCadastrar() {
		return getPaginaPrincipal();
	}

	/**
	 * Encaminha o usuário para a página padrão após atualização.
	 * @return
	 */
	public String forwardAtualizar() {
		return  forward("/cv/" + ReflectionUtils.getParameterizedTypeClass(this).getSimpleName() + "/listar.jsp");
	}
	
	/**
	 * Implementa a funcionalidade padrão de remover um objeto da comunidade virtual.
	 * 
	 * @return
	 */
	public String remover() {
		try {
			classe = ReflectionUtils.getParameterizedTypeClass(this);
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
			antesRemocao();
			prepare(SigaaListaComando.REMOVER_CV);
			
			Notification notification = null;
			if ( object != null ) {
				notification = execute(SigaaListaComando.REMOVER_CV, object, getEspecificacaoRemocao());	
			}

			if (notification != null && notification.hasMessages())
				return notifyView(notification);
			
			listagem = null;		
			flash(entityName()  + " " + (genero() == 'M' ? "removido" : "removida") + " com sucesso.");
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}

		return redirect("/cv/" + classe.getSimpleName() + "/listar.jsf");
	}

	/**
	 * Método que pode ser sobrescrito para adicionar operações antes da remoção do objeto. 
	 * 
	 * @throws DAOException
	 */
	protected void antesRemocao() throws DAOException {
		
	}
	
	/**
	 * Método que pode ser sobrescrito para adicionar operações após persistência do objeto.
	 * 
	 * @throws DAOException
	 */
	protected void aposPersistir() throws DAOException {
		
	}

	/**
	 * Exibe o formulário para se alterar o objeto.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String editar() throws DAOException {
		try {
			classe = ReflectionUtils.getParameterizedTypeClass(this);
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
			instanciarAposEditar();
			prepare(SigaaListaComando.ATUALIZAR_CV);

			return forward("/cv/" + classe.getSimpleName() + "/editar.jsp");

		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 
	}

	/**
	 * Exibe a página com a listagem para os objetos do tipo do mbean que extende esta classe.
	 * @return
	 */
	public String listar() {
		classe = ReflectionUtils.getParameterizedTypeClass(this);
		if (classe != null) {
			return forward("/cv/" + classe.getSimpleName() + "/listar.jsp");
		} else {
			return forward("/cv/" + classe.getSimpleName() + "/listar.jsp");
		}
	}

	/**
	 * Exibe o formulário para se cadastrar um novo objeto.
	 * @return
	 */
	public String novo() {
		try {
			instanciar();
			classe = ReflectionUtils.getParameterizedTypeClass(this);
			prepare(SigaaListaComando.CADASTRAR_CV);
			return forward("/cv/" + classe.getSimpleName() + "/novo.jsp");
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Exibe o objeto selecionado.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String mostrar() throws DAOException {
		classe = ReflectionUtils.getParameterizedTypeClass(this);
		object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), classe);
		if (acessoPublico())
			return forward("/cv/" + classe.getSimpleName() + "/mostrarPublico.jsp");
		else
			return forward("/cv/" + classe.getSimpleName() + "/mostrar.jsp");
	}

	/**
	 * Indica se o acesso do usuário foi através da área publica.
	 * @return
	 */
	private boolean acessoPublico() {
		//ComunidadeVirtualMBean bean = (ComunidadeVirtualMBean) getMBean("comunidadeVirtualMBean");
		return false;//!bean.isDocente() && !bean.isDiscente();
	}

	/**
	 * Implementa a funcionalidade padrão de atualizar um objeto na comunidade virtual.
	 * 
	 * @return
	 * @throws DAOException
	 */
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

		if ( object instanceof MaterialComunidade ) {
			MaterialComunidade material = (MaterialComunidade) object;
			if ( material.isNotificarMembros() ) {
				String assunto = material.getTipoMaterial()+ " atualizado(a) na comunidade virtual: " + comunidade().getNome();
				String texto = (genero() == 'M' ? "Um " : "Uma ") +material.getTipoMaterial()+ " foi atualizado(a) na comunidade virtual: <b>"+comunidade().getNome()+"</b> <p>Para visualizar acesse a comunidade virtual no SIGAA.</p>";
				notificarComunidade(assunto,texto);
			}
		}
		
		listagem = null;
		flash(entityName() + " " + (genero() == 'M' ? "atualizado" : "atualizada") + " com sucesso.");
		aposPersistir();
		return forwardAtualizar();
	}

	/**
	 * Método que pode ser sobrescrito para adicionar funcionalidades antes da persistência de um objeto.
	 */
	public void antesPersistir() {
		
	}

	/**
	 * Retorna o nome da entidade gerenciada pelo managed bean que extende esta classe.
	 * 
	 * @return
	 */
	private String entityName() {
		classe = ReflectionUtils.getParameterizedTypeClass(this);
		HumanName entity = (HumanName) ReflectionUtils.getAnnotation(classe, HumanName.class);
		if (entity != null)
			return entity.value();
		else
			return StringUtils.humanFormat(classe.getSimpleName());
	}
	
	/**
	 * Indica se o nome da classe é masculino ou feminino.
	 * 
	 * @return
	 */
	private char genero() {
		classe = ReflectionUtils.getParameterizedTypeClass(this);
		HumanName entity = (HumanName) ReflectionUtils.getAnnotation(classe, HumanName.class);
		if (entity != null)
			return entity.genero();
		else
			return 'M';
	}
	
	public T getObject() {
		return object;
	}
	
	public void setObject(T object) {
		this.object = object;
	}
	
	public GenericDAO getGenericDAO() {
		return getDAO(GenericSigaaDAO.class);
	}
	
	@SuppressWarnings("unchecked")
	public void setClasse(String className) {
		try {
			classe = (Class<T>) Class.forName(className);
			object = classe.newInstance();
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	public List<T> getListagem() throws DAOException {
		if (listagem == null)
			listagem = lista();
		return listagem;
	}
	
	public abstract List<T> lista() throws DAOException;
	
	/**
	 * Instancia o objeto da classe gerenciada pelo managed bean que extende esta classe.
	 */
	public void instanciar() {
		try {
			classe = ReflectionUtils.getParameterizedTypeClass(this);
			object = classe.newInstance();
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Método que deve ser sobrescrito para instanciar o objeto após uma atualização.
	 */
	public void instanciarAposEditar() {
		
	}
	
	public Specification getEspecificacaoCadastro() {
		return new NullSpecification();
	}
	
	public Specification getEspecificacaoAtualizacao() {
		return new NullSpecification();
	}
	
	public Specification getEspecificacaoRemocao() {
		return new NullSpecification();
	}

	public void setListagem(List<T> listagem) {
		this.listagem = listagem;
	}

	protected String getPaginaPrincipal() {
		return "/cv/principal.jsf";
	}
	
	protected String getPaginaListagem() {
		return "/cv/" + classe.getSimpleName() + "/listar.jsf";
	}

	protected String getPaginaEdicao() {
		return "/cv/" + classe.getSimpleName() + "/editar.jsf";
	}

	public boolean isCrescente() {
		return crescente;
	}

	public void setCrescente(boolean crescente) {
		this.crescente = crescente;
	}
}
