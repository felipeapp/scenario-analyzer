/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/06/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.jsf.OperacoesCoordenadorGeralEadMBean;
import br.ufrn.sigaa.ensino.dominio.AlteracaoAtivacaoComponente;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.negocio.dominio.ComponenteCurricularMov;

/**
 * Controller para autorizar cadastro de componentes curriculares
 * 
 * @author Andre M Dantas
 * @author Édipo Elder F. Melo (13/03/2009)
 * 
 */
@Component("autorizacaoComponente")
@Scope("request")
public class AutorizacaoCadastroComponenteMBean extends
		SigaaAbstractController<ComponenteCurricular> {

	/**
	 * Form de autorização do cadastro do componente
	 * 
	 */
	public static final String JSP_VALIDACAO = "/graduacao/componente/autorizacao/validacao.jsp";

	/**
	 * Form para buscar/listar/selecionar solicitações de componentes
	 * 
	 */
	public static final String JSP_COMPONENTES_SOLICITADOS = "/graduacao/componente/autorizacao/solicitados.jsp";

	/**
	 * Observações pertinentes ao cadastro
	 * 
	 */
	private String observacaoCadastro;

	/**
	 * Lista de componentes
	 * 
	 */
	private Collection<ComponenteCurricular> componentes;

	/**
	 * Restringe as solicitações de EAD
	 * 
	 */
	private boolean ead;

	/**
	 * Filtra a busca por componentes com status desativado
	 * 
	 */
	private boolean desativado;
	/**
	 * Filtra a busca por componentes com status aguardando
	 * 
	 */
	private boolean aguardando;
	/**
	 * Filtra a busca por componentes com status negado
	 * 
	 */
	private boolean negado;

	/**
	 * Filtra a busca por código do componente
	 * 
	 */
	private boolean filtroCodigo;
	/**
	 * Filtra a busca por nome do componente
	 * 
	 */
	private boolean filtroNome;

	/**
	 * Nome do componente para filtrar a busca
	 * 
	 */
	private String nome;
	/**
	 * Código do componente para filtrar a busca
	 * 
	 */
	private String codigo;

	/**
	 * Construtor padrão.
	 */
	public AutorizacaoCadastroComponenteMBean() {
		initObj();
	}

	/**
	 * Retorna as observações pertinentes ao cadastro
	 * 
	 * @return
	 */
	public String getObservacaoCadastro() {
		return observacaoCadastro;
	}

	/**
	 * Seta as observações pertinentes ao cadastro
	 * 
	 * @param observacaoCadastro
	 */
	public void setObservacaoCadastro(String observacaoCadastro) {
		this.observacaoCadastro = observacaoCadastro;
	}

	/**
	 * Lista apenas as solicitações que usuário fez.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return @see {@link #JSP_COMPONENTES_SOLICITADOS}
	 * @throws NegocioException
	 */
	public String verMinhasSolicitacoes() {
		if (getNivelEnsino() != NivelEnsino.STRICTO) {
			ComponenteCurricularMBean bean = new ComponenteCurricularMBean();
			bean.setEad(ead);
			bean.validarUnidadeCoordenacao();
			if (hasErrors())
				return null;
		}
		return telaComponentes();
	}

	/**
	 * Inicializa o objeto do controller
	 * 
	 */
	private void initObj() {
		obj = new ComponenteCurricular();
		obj.setNivel(NivelEnsino.GRADUACAO);
		obj.setPrograma(null);
	}

	/**
	 * Seleciona o componente a autorizar.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/componente/autorizacao/solicitados.jsp</li>
	 * </ul>
	 * 
	 * @return @see {@link #JSP_VALIDACAO}
	 * @throws DAOException
	 */
	public String selecionaComponente() throws DAOException {
		populateObj(true);
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		obj = dao.refresh(obj);
		observacaoCadastro = "";
		
		try {
			ExpressaoUtil.buildExpressaoFromDB(obj, dao, false);
		} catch (ArqException e) {
			addMensagemErro("Erro ao recuperar componentes das expressões");
		}		

		if (hasErrors())
			return null;
		
		possuiRequisitoAguardandoValidacao(dao, erros);
		
		if (hasErrors())
			return null;
			
		return forward(JSP_VALIDACAO);
	}

	/**
	 * Verifica se o componente que esta sendo selecionado possui requisitos que ainda aguardam validação
	 * 
	 * @param dao
	 * @param erros
	 * @throws DAOException
	 */
	public void possuiRequisitoAguardandoValidacao(ComponenteCurricularDao dao, ListaMensagens erros) throws DAOException {
		
		Collection<ComponenteCurricular> requisitos = new ArrayList<ComponenteCurricular>();
		
		if (!isEmpty(obj.getPreRequisito()))
			requisitos.addAll( ExpressaoUtil.expressaoCodigoToComponentes(obj.getPreRequisito()) );
		if (!isEmpty(obj.getCoRequisito()))
			requisitos.addAll( ExpressaoUtil.expressaoCodigoToComponentes(obj.getCoRequisito()) );
		if (!isEmpty(obj.getEquivalencia()))
			requisitos.addAll( ExpressaoUtil.expressaoCodigoToComponentes(obj.getEquivalencia()) );
		
		StringBuilder msgPendetes = new StringBuilder("Este componente não pode ser analisado porque possui requisitos que ainda aguardam validação. Componentes aguardando validação: ");
		
		boolean achou = false;
		for (ComponenteCurricular cc1 : requisitos) {
			List<ComponenteCurricular> lista = dao.findByCodigo(cc1.getCodigo(), false);
			for (ComponenteCurricular cc2 : lista) {
				if (cc2.isAguardandoConfirmacao()) {
					msgPendetes.append(" " + cc2.getCodigo());
					achou = true;
				}
			}
			
		}
		
		if (achou)
			erros.addErro(msgPendetes.toString());
		
	}
	
	/**
	 * Recupera as observações da autorização do componente em forma de string
	 * com quebra de linhas.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/componente/autorizacao/validacao.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getObservacoes() throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		Collection<AlteracaoAtivacaoComponente> alteracoes = dao
				.findAlteracoesAtivacaoByComponente(obj);
		StringBuffer sb = new StringBuffer();
		for (AlteracaoAtivacaoComponente alteracao : alteracoes) {
			sb.append("<br>" + alteracao.getObservacao());
			sb.append("<br>("
					+ alteracao.getRegistroEntrada().getUsuario().getLogin()
					+ ") - <i>" + alteracao.getData() + "</i>");
			sb.append("<br>---------------------------------------------");
		}
		return sb.toString();
	}

	/**
	 * Recupera a lista de componentes.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/componente/autorizacao/solicitados.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscarComponentes() throws DAOException {

		String nome = null;
		String codigo = null;

		if (filtroNome) {
			if (StringUtils.isEmpty(this.nome)) {
				addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Nome");
				return null;
			}
			nome = this.nome;
		} else if (filtroCodigo) {
			if (StringUtils.isEmpty(this.codigo)) {
				addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Código");
				return null;
			}
			codigo = this.codigo;
		}

		List<Integer> statusInativo = new ArrayList<Integer>(0);
		if (this.aguardando)
			statusInativo.add(ComponenteCurricular.AGUARDANDO_CONFIRMACAO);
		if (this.desativado)
			statusInativo.add(ComponenteCurricular.DESATIVADO);
		if (this.negado)
			statusInativo.add(ComponenteCurricular.CADASTRO_NEGADO);

		if (isEmpty(nome) && isEmpty(codigo) && isEmpty(statusInativo)) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			componentes = new ArrayList<ComponenteCurricular>();
			return null;
		}

		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		try {
			if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO))
				if ( isPortalPpg() )
					componentes = dao.findByStatus(nome, codigo, getNivelEnsino(), 0, statusInativo);
				else	
					componentes = dao.findByStatus(nome, codigo, getNivelEnsino(),
						getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), statusInativo);
			else if (isUserInRole(SigaaPapeis.SECRETARIA_POS,
					SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG))
				componentes = dao.findByStatus(nome, codigo, getNivelEnsino(),
						0, statusInativo);
			else if (isUserInRole(SigaaPapeis.CDP))
				componentes = dao.findByStatus(nome, codigo,
						NivelEnsino.GRADUACAO, 0, statusInativo);
			else if (isUserInRole(SigaaPapeis.COORDENADOR_GERAL_EAD)){
				Curso curso = getGenericDAO().refresh(getCursoAtualCoordenacao());
				Unidade unidadeCoordenacao = getGenericDAO().refresh(curso.getUnidadeCoordenacao());
				componentes = dao.findAtividadesByStatus(nome, codigo,
						NivelEnsino.GRADUACAO, unidadeCoordenacao ,
						null);
			} else if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO)) {
				componentes = dao.findAtividadesByStatus(nome, codigo,
						NivelEnsino.GRADUACAO, getCursoAtualCoordenacao().getUnidadeCoordenacao(),
						statusInativo);
			} else if (isUserInRole(SigaaPapeis.SECRETARIA_COORDENACAO)) {
				SecretariaUnidadeDao suDao = getDAO(SecretariaUnidadeDao.class);
				SecretariaUnidade secretaria = suDao
						.findCoordenacaoCursoAtivoByUsuarioCurso(
								getUsuarioLogado(), getCursoAtualCoordenacao());
				componentes = dao.findAtividadesByStatus(nome, codigo,
						NivelEnsino.GRADUACAO, secretaria.getUnidade(),
						statusInativo);
			}
		} catch (LimiteResultadosException lre) {
			addMensagemErro(lre.getMessage());
			lre.printStackTrace();
			componentes = null;
			return null;
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
			componentes = null;
			return null;
		}
		if (isEmpty(componentes))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		getCurrentRequest().setAttribute("busca", Boolean.TRUE);

		return null;
	}

	/**
	 * Autoriza o cadastro do componente curricular.
	 * 
	 * @param comando
	 * @return @see {@link #cancelar()}
	 * @throws ArqException
	 */
	private String autorizarCadastro(Comando comando) throws ArqException {
		checkRole(SigaaPapeis.CDP, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG);
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		if (obj.getId() == 0)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "");
		try {
			ExpressaoUtil.buildExpressaoToDB(obj, dao, true);
		} catch (NegocioException ne) {
			notifyError(ne);
			erros.addErro(ne.getMessage());
		}
		
		prepareMovimento(comando);
		ComponenteCurricularMov mov = new ComponenteCurricularMov();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		mov.setObservacaoCadastro(observacaoCadastro);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		String msg = "Cadastro de Componente curricular autorizado com sucesso";
		if (comando.getId() == SigaaListaComando.NEGAR_CADASTRO_COMPONENTE
				.getId())
			msg = "Cadastro de Componente curricular invalidado com sucesso";
		addMessage(msg, TipoMensagemUFRN.INFORMATION);
		buscarComponentes();
		obj.setId(0);
		return telaComponentes();
	}

	/**
	 * Autorização do cadastro de componente curricular.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>graduacao/componente/autorizacao/validacao.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String validar() throws ArqException {
		return autorizarCadastro(SigaaListaComando.AUTORIZAR_CADASTRO_COMPONENTE);
	}

	/**
	 * Invalida o autorização do cadastro de componente curricular
	 * 
	 * chamado por graduacao/componente/autorizacao/validacao.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String invalidar() throws ArqException {
		return autorizarCadastro(SigaaListaComando.NEGAR_CADASTRO_COMPONENTE);
	}

	/**
	 * Invalida o autorização do cadastro de componente curricular.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>graduacao/componente/autorizacao/validacao.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String desativar() throws ArqException {
		return autorizarCadastro(SigaaListaComando.DESATIVAR_COMPONENTE_CURRICULAR);
	}

	/**
	 * Abre o formulário para busca de componentes
	 * 
	 * chamando por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return @see {@link #JSP_COMPONENTES_SOLICITADOS}
	 */
	public String telaComponentes() {
		return forward(JSP_COMPONENTES_SOLICITADOS);
	}

	/**
	 * Indica se o usuário tem o papel CDP.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/componente/autorizacao/solicitados.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 */
	public boolean isRoleCDP() {
		return isUserInRole(SigaaPapeis.CDP, SigaaPapeis.PPG);
	}

	/** Retorna a lista de componentes encontrados na busca.
	 * @return
	 */
	public Collection<ComponenteCurricular> getComponentes() {
		return componentes;
	}

	/** Seta a lista de componentes encontrados na busca.
	 * @param componentes
	 */
	public void setComponentes(Collection<ComponenteCurricular> componentes) {
		this.componentes = componentes;
	}

	/** Seta se restringe as solicitações de EAD
	 * @param ead
	 */
	public void setEad(boolean ead) {
		this.ead = ead;
	}

	/** Retorna o curso atual da coordenação.
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getCursoAtualCoordenacao()
	 */
	@Override
	public Curso getCursoAtualCoordenacao() {
		if (ead) {
			OperacoesCoordenadorGeralEadMBean bean = (OperacoesCoordenadorGeralEadMBean) getMBean("opCoordenadorGeralEad");
			return bean.getCurso();
		} else {
			return super.getCursoAtualCoordenacao();
		}
	}

	/** Indica se filtra a busca por componentes com status desativado
	 * @return
	 */
	public boolean isDesativado() {
		return desativado;
	}

	/** Seta se filtra a busca por componentes com status desativado
	 * @param desativado
	 */
	public void setDesativado(boolean desativado) {
		this.desativado = desativado;
	}

	/** Indica se filtra a busca por componentes com status aguardando
	 * @return
	 */
	public boolean isAguardando() {
		return aguardando;
	}

	/** Seta se filtra a busca por componentes com status aguardando
	 * @param aguardando
	 */
	public void setAguardando(boolean aguardando) {
		this.aguardando = aguardando;
	}

	/** Indica se filtra a busca por componentes com status negado
	 * @return
	 */
	public boolean isNegado() {
		return negado;
	}

	/** Seta se filtra a busca por componentes com status negado
	 * @param negado
	 */
	public void setNegado(boolean negado) {
		this.negado = negado;
	}

	/** Indica se restringe as solicitações de EAD
	 * @return
	 */
	public boolean isEad() {
		return ead;
	}

	/** Indica se filtra a busca por código do componente
	 * @return
	 */
	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	/** Seta se filtra a busca por código do componente
	 * @param filtroCodigo
	 */
	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	/** Indica se filtra a busca por nome do componente.
	 * @return
	 */
	public boolean isFiltroNome() {
		return filtroNome;
	}

	/** Seta se filtra a busca por nome do componente.
	 * @param filtroNome
	 */
	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	/** Retorna o nome do componente para filtrar a busca.
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/** Seta o nome do componente para filtrar a busca.
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Retorna o código do componente para filtrar a busca.
	 * @return
	 */
	public String getCodigo() {
		return codigo;
	}

	/** Seta o código do componente para filtrar a busca.
	 * @param codigo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * Inicia a tela de busca de componentes.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {
		this.obj = new ComponenteCurricular();
		componentes = null;
		nome = null;
		codigo = null;
		desativado = negado = filtroCodigo = filtroNome = false;

		// Exibe todos com status "Aguardando".
		setAguardando(true);
		buscarComponentes();
		return telaComponentes();
	}
}