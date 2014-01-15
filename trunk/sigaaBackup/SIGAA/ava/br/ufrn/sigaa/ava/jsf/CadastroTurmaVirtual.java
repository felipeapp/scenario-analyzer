/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 21/01/2008
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.ChatTurma;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.Enquete;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.NoticiaTurma;
import br.ufrn.sigaa.ava.dominio.RotuloTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.negocio.RegistroAtividadeAvaHelper;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Layer-Supertype (http://martinfowler.com/eaaCatalog/layerSupertype.html) 
 * para os managed beans que realizam CADASTRO na turma virtual.
 * 
 * @author David Pereira
 *
 */
public abstract class CadastroTurmaVirtual<T extends DominioTurmaVirtual> extends ControllerTurmaVirtual {

	/** Objeto movimentado */
	protected T object;
	
	/** Lista de objetos */
	protected List<T> listagem;
	
	/**
	 * Cadastra um objeto do tipo T.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrar() throws ArqException {
		
		// Registra a tentativa de cadastrar algum objeto.
		registrarAcaoCadastrarEm(AcaoAva.INICIAR_INSERCAO);
		
		object.setTurma(turma());
		antesPersistir();
		prepare(SigaaListaComando.CADASTRAR_AVA);
		Notification notification = execute(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastro());
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		List <Turma> ts = tBean.getTurmasSemestre();
		
		if ( !notification.hasError() )	{
			
			for (Turma t : ts)
				if (cadastrarEm != null && cadastrarEm.contains("" + t.getId()))
					RegistroAtividadeAvaHelper.getInstance().registrarAtividade(t, object.getMensagemAtividade());
	
			if ( object instanceof AbstractMaterialTurma ) {
				AbstractMaterialTurma mt = (AbstractMaterialTurma) object;
				
				if ( mt.isNotificarAlunos() ) {
					
					// Para todas as turmas selecionadas, adiciona seu nome ao assunto do email.
					String nomeTurmas = "";
					for (Turma t : ts)
						if (cadastrarEm.contains(""+t.getId()))
							nomeTurmas += (nomeTurmas.equals("") ? "" : ", ") + t.getDescricaoSemDocente();
					
					String assunto = (genero() == 'M' ? "Novo " : "Nova ") + StringUtils.primeriaMaiuscula(mt.getMaterial().getTipoMaterial().toString()) + " adicionado(a): " +  mt.getNome() + " - " + nomeTurmas;
					String texto = (genero() == 'M' ? "Um " : "Uma ") + StringUtils.primeriaMaiuscula(mt.getMaterial().getTipoMaterial().toString()) + " foi disponibilizado(a) na turma virtual: " + nomeTurmas + "<p>Para visualizar acesse a turma virtual no SIGAA.</p>";
					
					if (cadastrarEmVariasTurmas()) {
						notificarTurmas(cadastrarEm, assunto, texto, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO , ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);
					} else {
						notificarTurma(assunto, texto, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE , ControllerTurmaVirtual.AUTORIZADO , ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);
					}
				}
			}
		}
		
		if (notification.hasMessages()) {
			if (!notification.getMessages().isErrorPresent()) {
				addMensagens(notification.getMessages());
			} else {
				object.setId(0);
				return notifyView(notification);
			}
		}
		
		// Registra inserção.
		registrarAcaoTurmas(AcaoAva.INSERIR,notification.getTurmasSucesso());
		
		listagem = null;
		TopicoAulaMBean bean = (TopicoAulaMBean) getMBean("topicoAula");
		bean.setTopicosAulas(null);
		
		flash(entityName()  + " " + (genero() == 'M' ? "cadastrado" : "cadastrada") + " com sucesso.");
		aposPersistir();
		
		String stringForward = forwardCadastrar();
		if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal") || StringUtils.isEmpty(stringForward)){
			return tBean.retornarParaTurma();
		}
		
		return redirect(stringForward);
	}


	/**
	 * Remove um objeto do tipo T.
	 * @return
	 */
	public String remover() {
		
		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
			registrarAcao(AcaoAva.INICIAR_REMOCAO);
			antesRemocao();
			prepare(SigaaListaComando.REMOVER_AVA);
			
			Notification notification = null;
			if ( object != null ) {
				notification = execute(SigaaListaComando.REMOVER_AVA, object, getEspecificacaoRemocao());	
			}

			aposRemocao();
			
			if (notification != null && notification.hasMessages())
				return notifyView(notification);
			
			registrarAcao(AcaoAva.REMOVER);
			listagem = null;		
			flash(entityName()  + " " + (genero() == 'M' ? "removido" : "removida") + " com sucesso.");
			
			if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
				TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
				return tBean.retornarParaTurma();
			}
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
		
		return redirect("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
	}

	/** Aqui não lança DAOException mas as classes que herdam dessa precisam. */
	protected void antesRemocao() throws DAOException {
		
	}
	
	/** Chamado no método remover, após remover o objeto com sucesso. */
	protected void aposRemocao() {
		
	}
	
	/** Chamado no método cadastrar, após inserir o objeto com sucesso. */
	protected void aposPersistir() {
		
	}

	/**
	 * Atualiza um objeto do tipo T da turma virtual.
	 * @return
	 */
	public String editar() {
		try {
			if (getParameterInt("id") == null) {
				addMensagemErro("Nenhum objeto para alterar.");
				return null;
			}
			
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
			
			if (object == null) {
				addMensagemErro("Nenhum objeto para alterar.");
				return null;
			}
			instanciarAposEditar();
			prepare(SigaaListaComando.ATUALIZAR_AVA);

		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 

		return forward("/ava/" + getClasse().getSimpleName() + "/editar.jsp");
	}

	/**
	 * Exibe a página que lista os objetos do tipo do parâmetro T.
	 * @return
	 */
	public String listar() {
		listagem = null;
		return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsp");
	}

	/**
	 * Prepara para cadastar um novo objeto do tipo T.
	 * @return
	 */
	public String novo() {
		try {
			instanciar();
			if (cadastrarEmVariasTurmas()) {
				cadastrarEm = new ArrayList<String>();
				cadastrarEm.add(String.valueOf(turma().getId()));
			}
			prepare(SigaaListaComando.CADASTRAR_AVA);
			return forward("/ava/" + getClasse().getSimpleName() + "/novo.jsp");
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Exibe um objeto do tipo T.
	 * @return
	 * @throws DAOException
	 */
	public String mostrar() throws DAOException {
		
		registrarAcao(AcaoAva.ACESSAR);
		
		object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
		
		boolean ativo = true;
		if (ReflectionUtils.propertyExists(getClass(), "ativo")) {
			ativo = (Boolean) ReflectionUtils.getFieldValue(object, "ativo");
		}
		
		if (object == null || !ativo){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
			return tBean.retornarParaTurma();
		}
		
		if (acessoPublico())
			return forward("/ava/" + getClasse().getSimpleName() + "/mostrarPublico.jsp");
		else
			return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
	}

	/**
	 * Informa se o acesso está sendo feito por um visitante. Ou seja, uma pessoa que não é docente
	 * nem discente e nem tem permissão de usuário.
	 * @return
	 */
	protected boolean acessoPublico() {
		TurmaVirtualMBean bean = getMBean("turmaVirtual");
		PermissaoAvaMBean pBean = getMBean("permissaoAva");
		return !bean.isDocente() && !bean.isDiscente() && pBean.getPermissaoUsuario() == null;
	}

	/**
	 * Atualiza um objeto do tipo T e o exibe.
	 * @return
	 */
	public String atualizar() {

		try {
			
			registrarAcao(AcaoAva.INICIAR_ALTERACAO);

			object.setTurma(turma());
			antesPersistir();

			Specification specification = getEspecificacaoAtualizacao();
			if (specification == null || specification instanceof NullSpecification ) 
				specification = getEspecificacaoCadastro();

			Notification notification = execute(SigaaListaComando.ATUALIZAR_AVA, object, specification);

			if (notification.hasMessages()) {
				prepare(SigaaListaComando.ATUALIZAR_AVA);
				return notifyView(notification);
			}

			registrarAcao(AcaoAva.ALTERAR);
			listagem = null;
			flash(entityName() + " " + (genero() == 'M' ? "atualizado" : "atualizada") + " com sucesso.");
			aposPersistir();
			if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
				TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
				return tBean.retornarParaTurma();
			}

		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}

		return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
	}

	/** Chamado no método cadastrar, antes de salvar o objeto. */
	public void antesPersistir() {
		
	}

	/**
	 * Retorna o nome da classe em formato legível<br>
	 * Ex: TurmaVirtual é retornado Turma Virtual.
	 * @return
	 */
	private String entityName() {
		HumanName entity = (HumanName) ReflectionUtils.getAnnotation(getClasse(), HumanName.class);
		if (entity != null)
			return entity.value();
		else
			return StringUtils.humanFormat(getClasse().getSimpleName());
	}
	
	/**
	 * Retorna o gênero para o nome da classe.
	 * Ex: TurmaVirutal é retornado F.
	 * @return
	 */
	private char genero() {
		HumanName entity = (HumanName) ReflectionUtils.getAnnotation(getClasse(), HumanName.class);
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
	
	@Override
	public GenericDAO getGenericDAO() {
		return getDAO(GenericSigaaDAO.class);
	}
	
	public Class<T> getClasse() {
		return ReflectionUtils.getParameterizedTypeClass(this);
	}
	
	/**
	 * Retorna a lista com objetos de cadastro.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>sigaa.war/ava/ConteudoTurma/listar.jsp</li>
	 * 	<li>sigaa.war/ava/DataAvaliacao/listar.jsp</li>
	 *  <li>sigaa.war/ava/Enquete/listar.jsp</li>
	 *  <li>sigaa.war/ava/Forum/listar.jsp</li>
	 *  <li>sigaa.war/ava/ForumMensagem/listar.jsp</li>
	 * 	<li>sigaa.war/ava/ForumMensagem/mostrar.jsp</li>
	 *  <li>sigaa.war/ava/IndicacaoReferencia/listar.jsp</li>
	 *  <li>sigaa.war/ava/NoticiaTurma/listar.jsp</li>
	 *  <li>sigaa.war/PermissaoAva/listar.jsp</li>
	 *  <li>sigaa.war/ava/PlanoEnsino/listar.jsp</li>
	 * 	<li>sigaa.war/ava/TopicoAula/listar.jsp</li>
	 * </ul>
	 */
	public List<T> getListagem() {
		if (listagem == null)
			listagem = lista();
		return listagem;
	}
	
	/**
	 * Método abstrato que deve buscar objetos.<br/><br/>
	 */
	public abstract List<T> lista();
	
	/**
	 * Instância o objeto.<br/><br/>
	 */
	public void instanciar() {
		object = ReflectionUtils.instantiateClass(getClasse());
	}
	
	/**
	 * Instância o objeto depois de editar.<br/><br/>
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
	
	/**
	 * Diz se o usuário encontra-se no portal do discente
	 * 
	 * @return
	 */
	public boolean isPortalDiscente(){
		return !ValidatorUtil.isEmpty(getSubSistema()) && SigaaSubsistemas.PORTAL_DISCENTE.getId() == getSubSistema().getId();
	}

	/**
	 * Diz se o usuário encontra-se no portal de coordenação de graduação 
	 * @return
	 */
	public boolean isPortalCoordenadorGraduacao(){
		return !ValidatorUtil.isEmpty(getSubSistema()) && SigaaSubsistemas.PORTAL_COORDENADOR.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de coordenação de programa de pós STRICTO
	 * @return
	 */
	public boolean isPortalCoordenadorStricto(){
		return !ValidatorUtil.isEmpty(getSubSistema()) && SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.getId() == getSubSistema().getId();
	}
	
	/**
	 * Diz se o usuário encontra-se no portal de coordenação de programa de LATO
	 * @return
	 */
	public boolean isPortalCoordenadorLato(){
		if (SigaaSubsistemas.PORTAL_COORDENADOR_LATO != null ) {
			
			SubSistema subsistema = getSubSistema();
			if (subsistema != null) {
				if ( SigaaSubsistemas.PORTAL_COORDENADOR_LATO.getId() == subsistema.getId() )
					return true;
			}
		}
		return false;
	}
		
	/**
	 * Registra a ação de um objeto movimentado.<br/><br/>
	 *
	 * <ul>
	 * 	<li>Não é utilizado por JSPs.</li>
	 * </ul>
	 * 	 
	 * @param acao
	 */
	protected void registrarAcao ( AcaoAva acao ){
		if ( object instanceof TopicoAula )
			registrarAcao(((TopicoAula) object).getDescricao(), EntidadeRegistroAva.TOPICO_AULA, acao, object.getId());
		else if ( object instanceof NoticiaTurma )
			registrarAcao(((NoticiaTurma) object).getDescricao(), EntidadeRegistroAva.NOTICIA, acao, object.getId());
		else if ( object instanceof AulaExtra )
			registrarAcao(((AulaExtra) object).getDescricao(), EntidadeRegistroAva.AULA_EXTRA, acao, object.getId());
		else if ( object instanceof ConteudoTurma )
			registrarAcao(((ConteudoTurma) object).getTitulo(), EntidadeRegistroAva.CONTEUDO, acao, object.getId());
		else if ( object instanceof IndicacaoReferencia ){
			IndicacaoReferencia ir = (IndicacaoReferencia) object;
			registrarAcao(!StringUtils.isEmpty(ir.getTitulo()) ? ir.getTitulo() : ir.getNome(), EntidadeRegistroAva.INDICACAO_REFERENCIA, acao, object.getId());
		}else if ( object instanceof Enquete )
			registrarAcao(((Enquete) object).getPergunta(), EntidadeRegistroAva.ENQUETE, acao, object.getId());
		else if ( object instanceof DataAvaliacao )
			registrarAcao(((DataAvaliacao) object).getDescricao(), EntidadeRegistroAva.AVALIACAO, acao, object.getId());
		else if ( object instanceof RotuloTurma )
			registrarAcao(((RotuloTurma) object).getDescricao(), EntidadeRegistroAva.ROTULO, acao, object.getId());
		else if ( object instanceof ChatTurma )
			registrarAcao(((ChatTurma) object).getDescricao(), EntidadeRegistroAva.CHAT, acao, object.getId());

	}
	
	/**
	 * Registra a ação em todas as turmas do cadastrarEm.<br/><br/>
	 *
	 * <ul>
	 * 	<li>Não é utilizado por JSPs.</li>
	 * </ul>
	 * 	 
	 * @param acao
	 */
	protected void registrarAcaoCadastrarEm ( AcaoAva acao ){
		if ( object instanceof TopicoAula )
			registrarAcao(((TopicoAula) object).getDescricao(), EntidadeRegistroAva.TOPICO_AULA, acao, true, object.getId());
		else if ( object instanceof NoticiaTurma )
			registrarAcao(((NoticiaTurma) object).getDescricao(), EntidadeRegistroAva.NOTICIA, acao,  true, object.getId());
		else if ( object instanceof AulaExtra )
			registrarAcao(((AulaExtra) object).getDescricao(), EntidadeRegistroAva.AULA_EXTRA, acao, true, object.getId());
		else if ( object instanceof ConteudoTurma )
			registrarAcao(((ConteudoTurma) object).getTitulo(), EntidadeRegistroAva.CONTEUDO, acao, true, object.getId());
		else if ( object instanceof IndicacaoReferencia ){
			IndicacaoReferencia ir = (IndicacaoReferencia) object;
			registrarAcao(!StringUtils.isEmpty(ir.getTitulo()) ? ir.getTitulo() : ir.getNome(), EntidadeRegistroAva.INDICACAO_REFERENCIA, acao, true, object.getId());
		}else if ( object instanceof Enquete )
			registrarAcao(((Enquete) object).getPergunta(), EntidadeRegistroAva.ENQUETE, acao, true, object.getId());
		else if ( object instanceof DataAvaliacao )
			registrarAcao(((DataAvaliacao) object).getDescricao(), EntidadeRegistroAva.AVALIACAO, acao, true, object.getId());
		else if ( object instanceof RotuloTurma )
			registrarAcao(((RotuloTurma) object).getDescricao(), EntidadeRegistroAva.ROTULO, acao, true, object.getId());
		else if ( object instanceof ChatTurma )
			registrarAcao(((ChatTurma) object).getDescricao(), EntidadeRegistroAva.CHAT, acao, true, object.getId());
	}
	
	/**
	 * Registra a ação em várias turmas.<br/><br/>
	 *
	 * <ul>
	 * 	<li>Não é utilizado por JSPs.</li>
	 * </ul>
	 * 	 
	 * @param acao
	 */
	protected void registrarAcaoTurmas ( AcaoAva acao, List<Turma> turmasSucesso ){
		if ( object instanceof TopicoAula )
			registrarAcao(((TopicoAula) object).getDescricao(), EntidadeRegistroAva.TOPICO_AULA, acao, turmasSucesso, object.getId());
		else if ( object instanceof NoticiaTurma )
			registrarAcao(((NoticiaTurma) object).getDescricao(), EntidadeRegistroAva.NOTICIA, acao,  turmasSucesso, object.getId());
		else if ( object instanceof AulaExtra )
			registrarAcao(((AulaExtra) object).getDescricao(), EntidadeRegistroAva.AULA_EXTRA, acao, turmasSucesso, object.getId());
		else if ( object instanceof ConteudoTurma )
			registrarAcao(((ConteudoTurma) object).getTitulo(), EntidadeRegistroAva.CONTEUDO, acao, turmasSucesso, object.getId());
		else if ( object instanceof IndicacaoReferencia ){
			IndicacaoReferencia ir = (IndicacaoReferencia) object;
			registrarAcao(!StringUtils.isEmpty(ir.getTitulo()) ? ir.getTitulo() : ir.getNome(), EntidadeRegistroAva.INDICACAO_REFERENCIA, acao, turmasSucesso, object.getId());
		}else if ( object instanceof Enquete )
			registrarAcao(((Enquete) object).getPergunta(), EntidadeRegistroAva.ENQUETE, acao, turmasSucesso, object.getId());
		else if ( object instanceof DataAvaliacao )
			registrarAcao(((DataAvaliacao) object).getDescricao(), EntidadeRegistroAva.AVALIACAO, acao, turmasSucesso, object.getId());
		else if ( object instanceof RotuloTurma )
			registrarAcao(((RotuloTurma) object).getDescricao(), EntidadeRegistroAva.ROTULO, acao, turmasSucesso, object.getId());
		else if ( object instanceof ChatTurma )
			registrarAcao(((ChatTurma) object).getDescricao(), EntidadeRegistroAva.CHAT, acao, turmasSucesso, object.getId());
	}
	
	/**
	 * Redefinição da página após o cadastrar
	 * 
	 * @return
	 */
	public String forwardCadastrar() {
		return null;
	}
	
	/**
	 * Inativar um objeto do tipo T.
	 * 
	 * @return
	 */
	public String inativar() {
		
		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
			registrarAcao(AcaoAva.INICIAR_INATIVACAO);
			prepare(SigaaListaComando.INATIVAR_AVA);
			
			Notification notification = null;
			if ( object != null ) {
				notification = execute(SigaaListaComando.INATIVAR_AVA, object, getEspecificacaoRemocao());	
			}
			
			if (notification != null && notification.hasMessages())
				return notifyView(notification);
			
			registrarAcao(AcaoAva.INATIVAR);
			listagem = null;		
			flash(entityName()  + " " + (genero() == 'M' ? "removido" : "removida") + " com sucesso.");
			
			if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
				TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
				return tBean.retornarParaTurma();
			}
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
		
		return redirect("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
	}

}