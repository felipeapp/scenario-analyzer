package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.apedagogica.dao.AtividadeAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dao.GrupoAtividadesAtualizacaoPedagogicaDAO;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.GrupoAtividadesAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.MensagensAP;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.negocio.MovimentoInscricaoAtividadeAP;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.nee.dominio.RecursoNee;
import br.ufrn.sigaa.nee.dominio.TipoRecursoNee;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
 * Controller que gerencia as opera��es dos participantes nas atividades de atualiza��o pedag�gica.
 * @author M�rio Rizzi
 *
 */
@Component("inscricaoAtividadeAP") @Scope("request")
public class InscricaoAtividadeAPMBean extends SigaaAbstractController<ParticipanteAtividadeAtualizacaoPedagogica>	 {
	
	/** Indica se a opera��o ativa � de consulta de inscri��es */
	private static final int CONSULTAR_INSCRICOES = 1;
	/** Indica se a opera��o ativa � de inscri��o em participa��o em atividades */
	private static final int INSCREVER = 2;
	
	/** Grupo de atividade selecionado pelo particpante. */
	private GrupoAtividadesAtualizacaoPedagogica grupoSelecionado;
	/** Lista de atividades carregadas de acordo com o grupo de atividade selecionado.  */
	private Set<AtividadeAtualizacaoPedagogica> atividadesSelecionadas;
	/** Lista de atividades carregadas de acordo com o grupo de atividade selecionado.  */
	private AtividadeAtualizacaoPedagogica atividadeVisualizada;
	/** Nome do docente setado no campo auto complete, para o caso de inscri��o retroativa pelo Gestor do PAP. */
	private String nomeDocente;	
	/** Recursos NEE dispon�veis para solicita��o */
	private Collection<TipoRecursoNee> recursosParaSelecionar;
	/** Conjuto de grupos de um docente que ter� sua participa��o exclu�da */
	private ArrayList<GrupoAtividadesAtualizacaoPedagogica> gruposDocenteExcluido;
	/** Se a opera��o est� setada em excluir a inscri��o */
	private boolean excluir = false;
	
	public InscricaoAtividadeAPMBean(){
		obj = new ParticipanteAtividadeAtualizacaoPedagogica();
		grupoSelecionado = new GrupoAtividadesAtualizacaoPedagogica();
		atividadesSelecionadas = new HashSet<AtividadeAtualizacaoPedagogica>();
		nomeDocente = null;
		removeOperacaoAtiva();
		all = null;
	}
	
	/**
 	 * Define o diret�rio base para as opera��es de CRUD.
 	 * M�todo n�o invocado por JSP's.
 	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getDirBase() 
 	 */
	@Override
	public String getDirBase() {
 		return "/apedagogica/InscricaoAtividadeAP";
	}
	
	/**
 	 * Define o formul�rio de inscri��o.
 	 * M�todo n�o invocado por JSP's.
 	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getFormPage() 
 	 */
	@Override
	public String getFormPage() {
		return getDirBase() + "/form_inscricao_atividade.jsf";
	}
	
	/**
 	 * Define o formul�rio de inscri��o.
 	 * M�todo n�o invocado por JSP's.
 	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getFormPage() 
 	 */
	public String getViewPage() {
		return getDirBase() + "/view_atividade.jsf";
	}
	
 	/**
 	 * Define os usu�rio que poder�o realizar as a��es de CRUD.
 	 * M�todo n�o invocado por JSP's.
 	 */
 	@Override
 	public void checkChangeRole() throws SegurancaException {
 		checkRole(SigaaPapeis.DOCENTE,SigaaPapeis.GESTOR_PAP);
 	}
 	
 	/**
 	 * Redireciona para tela contendo os detalhes da atividade.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/apedagogica/InscricaoAtividadeAP/form_inscricao_grupo.jsp</li>
	 * </ul>
 	 * @return
 	 * @throws DAOException 
 	 */
 	public String viewAtividade() throws DAOException{
 		atividadeVisualizada = getGenericDAO().findAndFetch( getParameterInt("id"), AtividadeAtualizacaoPedagogica.class, "Professores");
 		return forward(getViewPage());
 	}
	
 	/**
	 * Seleciona grupo e carrega a listagem daas atividades abertas 
	 * para que o docente posso escolher. 
 	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_inscricao_gruPO.jsp</li>
 	 * </ul>
	 */
	public String selecionarGrupo() throws DAOException, SegurancaException{
		Integer idGrupo = getParameterInt("idGrupo");
		setOperacaoAtiva(INSCREVER);
		return selecionarGrupo(idGrupo);
	}
	
 	/**
 	 * Seleciona o grupo conforme o o id passado.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/apedagogica/InscricaoAtividadeAP/form_inscricao_grupo.jsp</li>
	 * </ul>
 	 * @param idGrupo
 	 * @return
 	 * @throws DAOException
 	 * @throws SegurancaException
 	 */
 	private String selecionarGrupo(Integer idGrupo) throws DAOException, SegurancaException{
		
		checkChangeRole();
		
		if( isEmpty(idGrupo) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Grupo de Atividade");
				
		grupoSelecionado = getGenericDAO().findByPrimaryKey(idGrupo, GrupoAtividadesAtualizacaoPedagogica.class);
		grupoSelecionado.setAtividades( CollectionUtils.toList( getDAO(AtividadeAtualizacaoPedagogicaDAO.class).findByGrupoAtividade(idGrupo, null)) );
		
		if( !getAcessoMenu().isProgramaAtualizacaoPedagogica() && !grupoSelecionado.isAberto() )
			addMensagem(MensagensAP.GRUPO_ATIVIDADE_FECHADO);
		
		if( hasErrors() )
			return null; 

		return forward(getDirBase() + "/form_inscricao_atividade.jsf");
		
	}
	
 	/**
	 * Seleciona grupo e carrega a listagem das atividades abertas 
	 * para que o docente posso escolher. 
 	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_remocao_grupo.jsp</li>
 	 * </ul>
	 */
	public String selecionarGrupoRemocao() throws DAOException, SegurancaException{
		Integer idGrupo = getParameterInt("idGrupo");
		setOperacaoAtiva(SigaaListaComando.REMOVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA.getId());
		return selecionarGrupoRemocao(idGrupo);
	}
	
 	/**
 	 * Seleciona o grupo conforme o o id passado.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/apedagogica/InscricaoAtividadeAP/form_remocao_grupo.jsp</li>
	 * </ul>
 	 * @param idGrupo
 	 * @return
 	 * @throws SegurancaException 
 	 * @throws DAOException
 	 */
	private String selecionarGrupoRemocao(Integer idGrupo) throws SegurancaException, DAOException {

		checkChangeRole();
		
		if( isEmpty(idGrupo) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Grupo de Atividade");
				
		grupoSelecionado = getGenericDAO().findByPrimaryKey(idGrupo, GrupoAtividadesAtualizacaoPedagogica.class);
		grupoSelecionado.setAtividades( CollectionUtils.toList( getDAO(AtividadeAtualizacaoPedagogicaDAO.class).findByGrupoParticipante(idGrupo, obj.getDocente().getId())) );
		
		if( !getAcessoMenu().isProgramaAtualizacaoPedagogica() && !grupoSelecionado.isAberto() )
			addMensagem(MensagensAP.GRUPO_ATIVIDADE_FECHADO);
		
		if( hasErrors() )
			return null; 

		return forward(getDirBase() + "/form_remocao_atividade.jsf");
	}

	/**
	 * Popula o objeto antes de remover.
	 * M�todo n�o invocado por JSP's. 
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#beforeRemover() 
	 */
	@Override
	public void beforeRemover() throws DAOException {
		removeOperacaoAtiva();
		setOperacaoAtiva(ArqListaComando.REMOVER.getId());
		try {
			prepareMovimento(ArqListaComando.REMOVER);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
		populateObj(true);
	}
	
	/**
	 * Limpa a listagem antiga das participa��es.
	 * M�todo n�o invocado por JSP's. 
	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#afterRemover() 
	 */
	@Override
	public void afterRemover() {
		all = null;
		resetBean();
	}
	
	/**
	 * Realiza a inscri��o do docente em atividades de atualiza��o pedag�gica.
 	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_inscricao_atividade.jsp</li>
 	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		checkChangeRole();
		
		carregarAtividadesSelecionadas();
		carregarRecursosSelecionados();
		
		if(hasErrors())
			return null;
		
		/** N�o seta automaticamente o docente se o acesso estiver sendo realizado pelo gestor do pap. */
		if( !getAcessoMenu().isProgramaAtualizacaoPedagogica() )
			obj.setDocente(getServidorUsuario());
		
		MovimentoInscricaoAtividadeAP movInscricao = new MovimentoInscricaoAtividadeAP();
		movInscricao.setCodMovimento(SigaaListaComando.INSCREVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA );
		movInscricao.setParticipante(obj);
		movInscricao.setAtividadesSelecionadas(atividadesSelecionadas);
		movInscricao.setGestorPAP( getAcessoMenu().isProgramaAtualizacaoPedagogica() );
		
		try {
			execute(movInscricao);
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
		}
		
		if(hasErrors())
			return null;
		
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Inscri��o em Atividades");
		removeOperacaoAtiva();
		all = null;
		
		if( getAcessoMenu().isProgramaAtualizacaoPedagogica() )
			return cancelar();
		
		return redirectJSF(getListPage());
	}
	
 	/**
 	 * Carrega os recursos nee selecionados.
	 * <br /> 
	 * M�todo n�o invocado por JSP(s):
 	 */
	private void carregarRecursosSelecionados() {
		if (obj.getSolicitacaoRecursoNEE()){
			List<RecursoNee> recursos = new ArrayList<RecursoNee>();
			for (TipoRecursoNee tr : recursosParaSelecionar){
				if (tr.isSelecionado()){
					RecursoNee r = new RecursoNee();
					if (tr.isPermiteComplemento()){
						if (!isEmpty(tr.getComplemento()))
							r.setOutros(tr.getComplemento());
						else addMensagemErro("� necess�rio especificar o tipo de recurso solicitado");
					}
					r.setTipoRecursoNee(tr);
					r.setParticipante(obj);
					recursos.add(r);
				}
			}
			if (isEmpty(recursos))
				addMensagemErro("� necess�rio selecionar pelo menos um recurso.");
			obj.setRecursosNee(recursos);
		}
	}

	/**
	 * Realiza a remo��o do docente em atividades de atualiza��o pedag�gica.
 	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_remocao_atividade.jsp</li>
 	 * </ul>
	 */
	@Override
	public String remover() throws SegurancaException, ArqException {
		
		checkChangeRole();
		
		carregarAtividadesSelecionadas();
				
		if(hasErrors())
			return null;
			
		MovimentoInscricaoAtividadeAP movInscricao = new MovimentoInscricaoAtividadeAP();
		movInscricao.setCodMovimento(SigaaListaComando.REMOVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA );
		movInscricao.setParticipante(obj);
		movInscricao.setAtividadesSelecionadas(atividadesSelecionadas);
		movInscricao.setGestorPAP( getAcessoMenu().isProgramaAtualizacaoPedagogica() );
		
		try {
			execute(movInscricao);
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
		}
		
		if(hasErrors())
			return null;
		
		addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Participa��o em Atividade(s)");
		removeOperacaoAtiva();
		all = null;
		
		if( getAcessoMenu().isProgramaAtualizacaoPedagogica() )
			return cancelar();
		
		return redirectJSF(getListPage());
	}
	
	/**
	 * Popula somente as atividades selecionadas pelo doncete.
	 * M�todo n�o invocado por JSP's.
	 */
	private void carregarAtividadesSelecionadas(){
		atividadesSelecionadas = new HashSet<AtividadeAtualizacaoPedagogica>();
		for (AtividadeAtualizacaoPedagogica a : grupoSelecionado.getAtividades()) 
			if( a.isSelecionada() )
				atividadesSelecionadas.add(a);
		
		if (atividadesSelecionadas==null||atividadesSelecionadas.isEmpty())
			addMensagemErro("Nenhuma atividade selecionada.");
		
	}
	
	/**
	 * Redireciona para p�gina da consulta das inscri��es
 	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/view_atividade.jsp</li>
 	 *    <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
 	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		all = null;
		setOperacaoAtiva(CONSULTAR_INSCRICOES);
		return super.listar();
	}
	
	/**
 	 * Retornar todas inscri��es do docente.
 	 * M�todo n�o invocado por JSP's.
 	 * @see br.ufrn.sigaa.arq.jsf.AbstractControllerRelatorio#getAll() 
 	 */
	@Override
	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> getAll()
			throws ArqException {
		if(all == null)
			all = getGenericDAO().
				findByExactField(ParticipanteAtividadeAtualizacaoPedagogica.class, "docente.id", getServidorUsuario().getId(),"ASC","situacao,atividade.inicio");
		
		if(all == null)
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS, "Inscri��es");
		
		return all;
		
	}
	
	/**
	 * Redireciona para o formul�rio das atividades.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_inscricao_atividade.jsp</li>
 	 * </ul>
 	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.INSCREVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA);
		setOperacaoAtiva(SigaaListaComando.INSCREVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA.getId());
		setConfirmButton("Inscrever em Atividades");
		return forward(getDirBase()+"/form_inscricao_grupo.jsf");
	}
	
	/**
	 * Redireciona para o formul�rio das atividades.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_inscricao_atividade.jsp</li>
 	 * </ul>
 	 */
	@Override
	public String preRemover() {
		
		GrupoAtividadesAtualizacaoPedagogicaDAO gDao = null;
		
		try {
			
			gDao = getDAO(GrupoAtividadesAtualizacaoPedagogicaDAO.class);
			gruposDocenteExcluido = (ArrayList<GrupoAtividadesAtualizacaoPedagogica>) gDao.findByDocente(obj.getDocente().getId());
			
			if (gruposDocenteExcluido==null || gruposDocenteExcluido.isEmpty())
				addMensagemErro("O participante n�o est� inscrito em nenhum grupo de atividade.");
			
			prepareMovimento(SigaaListaComando.REMOVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA);
			setOperacaoAtiva(SigaaListaComando.REMOVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA.getId());
			setConfirmButton("Remover de Atividades");
	
		} catch (ArqException e) {
			e.printStackTrace();
			tratamentoErroPadrao(e);
		}finally {
			if (gDao!=null)
				gDao.close();
		}
		return forward(getDirBase()+"/form_remocao_grupo.jsf");
	}
	
	/**
	 * Redireciona para o formul�rio das atividades para inscri��o como Gestor do PAP.
	 * Define um novo passo par selecionar o discente ante de selecionar a atividade.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/geral.jsp</li>
 	 * </ul>
 	 */
	public String preCadastrarGestor() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_PAP);	
		excluir = false;
		return forward(getDirBase()+"/form_selecionar_docente.jsf");
	}
	
	/**
	 * Redireciona para o formul�rio das atividades para inscri��o como Gestor do PAP.
	 * Define um novo passo par selecionar o discente ante de selecionar a atividade.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/geral.jsp</li>
 	 * </ul>
 	 */
	public String preExcluirGestor() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_PAP);	
		excluir = true;
		return forward(getDirBase()+"/form_selecionar_docente.jsf");
	}
		
	/**
	 * Redireciona para a tela de sele��o do grupo de atividade.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_selecionar_docente.jsp</li>
 	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterDocente() throws ArqException, NegocioException{
		
		if (ValidatorUtil.isEmpty(obj.getDocente()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Participante");

		if( hasErrors() ){
			return null;
		}
		return preCadastrar();
	}
	
	/**
	 * Redireciona para a tela de sele��o do grupo de atividade.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_selecionar_docente.jsp</li>
 	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String removerDocente() throws ArqException, NegocioException{
		
		if (ValidatorUtil.isEmpty(obj.getDocente()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Participante");
		
		if( hasErrors() ){
			return null;
		}
		return preRemover();
	}
	
	/**
	 * O docente seleciona no campo AUTOCOMPLETE.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_selecionar_docente.jsp</li>
 	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarDocente(ActionEvent e) throws DAOException{
		Integer idServidor = (Integer) e.getComponent().getAttributes().get("idServidor");
		if( isEmpty(idServidor) ){
			addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Docente da institui��o");
		}else{
			obj.setDocente( getGenericDAO().findByPrimaryKey(idServidor, Servidor.class, "id,pessoa.id,pessoa.nome,categoria.id,categoria.descricao") );
		}
	}
	
	/**
	 * Carregar os tipos de recursos NEE selecion�veis na p�gina do formul�rio.
	 * <br /> 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>/sigaa.war/apedagogica/InscricaoAtividadeAP/form_inscricao_atividade.jsp</li>
 	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarRecursosNee(ValueChangeEvent evt) throws DAOException{
		obj.setSolicitacaoRecursoNEE(!obj.getSolicitacaoRecursoNEE());
		recursosParaSelecionar = getGenericDAO().findAll(TipoRecursoNee.class);
	}
	
	public GrupoAtividadesAtualizacaoPedagogica getGrupoSelecionado() {
		return grupoSelecionado;
	}

	public void setGrupoSelecionado(
			GrupoAtividadesAtualizacaoPedagogica grupoSelecionado) {
		this.grupoSelecionado = grupoSelecionado;
	}

	public Set<AtividadeAtualizacaoPedagogica> getAtividadesSelecionadas() {
		return atividadesSelecionadas;
	}

	public void setAtividadesSelecionadas(
			Set<AtividadeAtualizacaoPedagogica> atividadesSelecionadas) {
		this.atividadesSelecionadas = atividadesSelecionadas;
	}

	public AtividadeAtualizacaoPedagogica getAtividadeVisualizada() {
		return atividadeVisualizada;
	}
	
	/**
	 * Verifica se a opera��o executada � de inscri��o em atividade.
	 * Utilizado para indicar qualo comportamento do link Voltar na tela dos 
	 * detalhes da atividade. 
	 * @return
	 */
	public boolean isInscrever(){
		return isOperacaoAtiva( INSCREVER ); 
	}
	
	/**
	 * Verifica se a opera��o executada � de consulta de status da inscri��o.
	 * Utilizado para indicar qualo comportamento do link Voltar na tela dos 
	 * detalhes da atividade. 
	 * @return
	 */
	public boolean isConsulta(){
		return isOperacaoAtiva( CONSULTAR_INSCRICOES ); 
	}

	public String getNomeDocente() {
		return nomeDocente;
	}

	public void setNomeDocente(String nomeDocente) {
		this.nomeDocente = nomeDocente;
	}

	public void setRecursosParaSelecionar(Collection<TipoRecursoNee> recursosParaSelecionar) {
		this.recursosParaSelecionar = recursosParaSelecionar;
	}

	public Collection<TipoRecursoNee> getRecursosParaSelecionar() {
		return recursosParaSelecionar;
	}

	public void setExcluir(boolean excluir) {
		this.excluir = excluir;
	}

	public boolean isExcluir() {
		return excluir;
	}

	public void setGruposDocenteExcluido(ArrayList<GrupoAtividadesAtualizacaoPedagogica> gruposDocenteExcluido) {
		this.gruposDocenteExcluido = gruposDocenteExcluido;
	}

	public ArrayList<GrupoAtividadesAtualizacaoPedagogica> getGruposDocenteExcluido() {
		return gruposDocenteExcluido;
	}
	
	public String getCategoriasParticipantes () {
		return String.valueOf(Categoria.DOCENTE) + ";" + String.valueOf(Categoria.TECNICO_ADMINISTRATIVO);
	}
}
