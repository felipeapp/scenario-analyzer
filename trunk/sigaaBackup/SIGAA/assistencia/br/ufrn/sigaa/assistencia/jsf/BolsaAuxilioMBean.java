/*
 * Universidade Federal do Rio Grande do Norte
 * SuperintendÃªncia de InformÃ¡tica
 * Diretoria de Sistemas
 *
 * Created on 17/09/2009
 *
 */
package br.ufrn.sigaa.assistencia.jsf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.dao.sae.DiasAlimentacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.dao.AnoPeriodoReferenciaSAEDao;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioAtleta;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioCreche;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.DocumentosEntreguesSAE;
import br.ufrn.sigaa.assistencia.dominio.ModalidadeEsportiva;
import br.ufrn.sigaa.assistencia.dominio.PeriodoAtividadeAcademica;
import br.ufrn.sigaa.assistencia.dominio.ResidenciaUniversitaria;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoMeioTransporte;
import br.ufrn.sigaa.assistencia.negocio.BolsaAuxilioValidation;
import br.ufrn.sigaa.assistencia.negocio.ValidaBolsaAuxilio;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosSAE;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 *	MBean responsÃ¡vel pelo CRUD das Bolsas AuxÃ­lio AlimentaÃ§Ã£o/ResidÃªncia
 *	Usada pelos coordenadores do SAE e pelos alunos
 *         
 * @author agostinho campos
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class BolsaAuxilioMBean extends SigaaAbstractController<BolsaAuxilio> {

	/** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
	private AnoPeriodoReferenciaSAE anoReferencia;
	/** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
	private Object bolsaAuxiliar;
	/** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
    private int[] transportesIds = new int[0];
    /** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
	private int[] documentosIds = new int[0];
	/** Atributo utilizado em operações de CRUD de Bolsa Auxílio. */
	private Collection<BolsaAuxilioPeriodo> bolsas;
	
	public BolsaAuxilioMBean() {
		clear();
	}
		
	/**
	 * Inicializa os atributos do MBean
	 */
	public void clear() {
		obj = new BolsaAuxilio();
		obj.setResidencia(new ResidenciaUniversitaria());
		obj.setTipoBolsaAuxilio(new TipoBolsaAuxilio());
		obj.setResidencia(new ResidenciaUniversitaria());
		documentosIds = new int[0];
		transportesIds = new int[0];
		bolsas = new ArrayList<BolsaAuxilioPeriodo>();
	}	
	
	/**
	 *  Método chamado pela(s) seguinte(s) JSP's:
	 *  <br/>
	 *  <ul>
	 *  	<li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarSolicitacaoBolsaAuxilio() throws DAOException {
		clear();
		AnoPeriodoReferenciaSAEDao dao = getDAO(AnoPeriodoReferenciaSAEDao.class);
		try {
			carregarDados(dao);
			if ( obj.getDiscente().isGraduacao() && obj.getDiscente().getStatus() == StatusDiscente.GRADUANDO) {
				addMensagemErro("Não é possí­vel realizar a Solicitação de Bolsa Auxílio, para alunos com status GRADUANDO.");
			}
		} finally {
			dao.close();
		}
		
		return forward("/sae/BolsaAuxilio/form_aviso.jsf");
	}

	/**
	 * Método chamado pela(s) seguinte(s) JSP's:
	 *  <br/>
	 *  <ul>
	 *  	<li>/sigaa.war/sae/BolsaAuxilio/form_aviso.jsp</li>
	 *  </ul>
	 * @return
	 * @throws DAOException
	 */
	public String solicitacaoBolsaAuxilio() throws DAOException {
		erros = new ListaMensagens();
		BolsaAuxilioValidation.validacaoDadosBasico(getUsuarioLogado(), obj, erros);
		BolsaAuxilioValidation.validacaoBolsaPromisaes(obj, erros);

		if ( !erros.isEmpty() ) {
			addMensagens(erros);
			return null;
		}

		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		Pessoa pessoa = pessoaDao.findCompleto(obj.getDiscente().getPessoa().getId());
		obj.getDiscente().setPessoa(pessoa);
		if (  obj.getTurnoAtividade() == null || obj.getTurnoAtividade().isEmpty() )
			obj.setTurnoAtividade(BolsaAuxilio.APENAS_UM_TURNO);
		if ( obj.getSituacaoBolsa() == null || obj.getSituacaoBolsa().getId() == 0 )
			obj.setSituacaoBolsa(new SituacaoBolsaAuxilio(SituacaoBolsaAuxilio.EM_ANALISE));
		
		carregarQuestionarioRespostas();

		setOperacaoAtiva(SigaaListaComando.CADASTRAR_BOLSA_AUXILIO.getId());
		return direcionar();
	}

	/**
	 * Cadastra a solicitação de bolsa auxílio
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> <li> /sigaa.war/sae/BolsaAuxilio/form.jsp </ul>
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_BOLSA_AUXILIO.getId())){
			if( isPortalDiscente() )
				return cancelar();
			else
				return forward("/sae/menu.jsp");
		}

		ListaMensagens lista = new ListaMensagens();
		ValidaBolsaAuxilio.validate(obj, bolsaAuxiliar, lista);
		if ( !lista.isEmpty() ) {
			addMensagens(lista);
			return null;
		}
		
		if (obj.getId() == 0) {
			
			try {
				
				if ( !BolsaAuxilioValidation.verificarExistenciaDadosBancarios(obj) ) {
					addMensagemErro("Atenção: o discente não possui conta bancária cadastrada no" + RepositorioDadosInstitucionais.get("siglaSigaa") + 
							". Bolsas Auxí­lios são podem ser solicitadas para discentes que possuem conta bancária cadastrada.");
					return direcionar();
				}
				
				setarDadosAntesCadastrar();
				gerarHashAutenticacao();
				
				prepareMovimento(SigaaListaComando.CADASTRAR_BOLSA_AUXILIO);
				MovimentoCadastro movCad = new MovimentoCadastro();
				movCad.setObjMovimentado(obj);
				movCad.setObjAuxiliar( bolsaAuxiliar );
				movCad.setCodMovimento(SigaaListaComando.CADASTRAR_BOLSA_AUXILIO);
				
				execute(movCad);
									
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			}
			
			haDiasAlimentacao(obj);
			removeOperacaoAtiva();
			return forward("/sae/BolsaAuxilio/comprovante.jsp");
		}
		else {
			
			try {
				
				prepareMovimento(SigaaListaComando.ALTERAR_BOLSA_AUXILIO);
				MovimentoCadastro movCad = new MovimentoCadastro();
				movCad.setObjMovimentado(obj);
				movCad.setObjAuxiliar( bolsaAuxiliar );
				movCad.setCodMovimento(SigaaListaComando.ALTERAR_BOLSA_AUXILIO);
				
				execute(movCad);
				
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			}

			haDiasAlimentacao(obj);
			removeOperacaoAtiva();
			return forward("/sae/menu.jsp");
		}
	}
	
	/**
	 * Método não chamado por JSP's.
	 * 
	 * @param bolsaAuxilio
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void haDiasAlimentacao( BolsaAuxilio bolsaAuxilio ) throws HibernateException, DAOException {
		DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class);
		boolean definirDiasAlimentacao = ParametroHelper.getInstance().getParametroBoolean(ParametrosSAE.DEFINICAO_DIAS_PADROES_BOLSA_AUXILIO);
		try {
			if ( bolsaAuxilio.isPermiteDefinirDiasAlimentacao() ) {

				Collection<DiasAlimentacao> diasAli = dao.findDiasAlimentacaoByBolsa(bolsaAuxilio.getId());
				if ( ValidatorUtil.isEmpty( diasAli ) )
					dao.criarDiasAlimentacao(bolsaAuxilio, bolsaAuxilio.getSituacaoBolsa().getId() == SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA && definirDiasAlimentacao);
				else if ( !ValidatorUtil.isEmpty(diasAli) && ( bolsaAuxilio.getSituacaoBolsa().getId() == SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA 
						|| bolsaAuxilio.getSituacaoBolsa().getId() == SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA) ) {
					dao.alterarDiasAlimentacao(bolsaAuxilio, bolsaAuxilio.getSituacaoBolsa().getId() == SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA && definirDiasAlimentacao);
				}
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Gera o número de inscrição e um hash de autentição para exibir no
	 * formulário que será impresso quando o usuário cadastrar uma bolsa.
	 * <br/>
	 * Método não chamado por JSP's.
	 * 
	 */
	private void gerarHashAutenticacao() {
		Integer numeroInscricao = getDAO(BolsaAuxilioDao.class).getNextSeq("sae", "comprovante_solicit_bolsa_seq");
		String strAutent = String.valueOf(numeroInscricao + obj.getDiscente().getId());
		String hashAutenticacao = UFRNUtils.toSHA1Digest(strAutent);
		
		obj.setNumeroComprovante(numeroInscricao);
		obj.setHashAutenticacao(hashAutenticacao);
	}
	
	/**
	 * Método não chamado por JSP's.
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void setarDadosAntesCadastrar() throws ArqException, NegocioException {
		BolsaAuxilioDao bolsaDao = getDAO(BolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoPeriodoDao = getDAO(AnoPeriodoReferenciaSAEDao.class);
		try {
			AnoPeriodoReferenciaSAE anoPeriodoSAE = anoPeriodoDao.anoPeriodoVigente();
			BolsaAuxilioPeriodo bauxp = new BolsaAuxilioPeriodo();
			
			if ( obj.getTipoBolsaAuxilio().isAlimentacao() || obj.getTipoBolsaAuxilio().isResidenciaGraduacao() 
					|| obj.getTipoBolsaAuxilio().isResidenciaPos() ) {
				
				List<TipoMeioTransporte> transportesSelecionados = new ArrayList<TipoMeioTransporte>();
				for (int groupId : transportesIds) {
					TipoMeioTransporte tipoMeioTransporte = bolsaDao.findByPrimaryKey( groupId, TipoMeioTransporte.class);
					transportesSelecionados.add(tipoMeioTransporte);
				}
				
				obj.setTipoMeioTransporte(transportesSelecionados);
				
				List<DocumentosEntreguesSAE> documentosEntregues = new ArrayList<DocumentosEntreguesSAE>();
				for (int documentosId : documentosIds) {
					DocumentosEntreguesSAE docs = anoPeriodoDao.findByPrimaryKey( documentosId, DocumentosEntreguesSAE.class);
					documentosEntregues.add(docs);
				}
				
				obj.setDocumentosEntregues(documentosEntregues);
				
				if ( obj.getTipoBolsaAuxilio().isAlimentacao() )
					UFRNUtils.anularAtributosVazios(obj, "residencia");
				
			} else {
				UFRNUtils.anularAtributosVazios(obj, "tipoMeioTransporte", "residencia", "documentosEntregues");
			}
			
			obj.setDiscente(obj.getDiscente().getDiscente());
			criarBolsaAuxilioPeriodo(bauxp, anoPeriodoSAE);
		} finally {
			bolsaDao.close();
			anoPeriodoDao.close();
		}
	}
	 
	/**
	 * Cria bolsa auxÃ­lio.
	 * @param bauxp
	 * @param anoPeriodoSAE
	 */
	private void criarBolsaAuxilioPeriodo(BolsaAuxilioPeriodo bauxp, AnoPeriodoReferenciaSAE anoPeriodoSAE) {
		bauxp.setAno( anoPeriodoSAE.getAno() );
		bauxp.setPeriodo( anoPeriodoSAE.getPeriodo() );
		bauxp.setDataCadastro(new Date());
		bauxp.setBolsaAuxilio(obj);
		for (CalendarioBolsaAuxilio calendario : anoPeriodoSAE.getCalendario()) {
			if ( calendario.getTipoBolsaAuxilio().getId() == obj.getTipoBolsaAuxilio().getId() && 
					calendario.getMunicipio().getId() == obj.getDiscente().getCurso().getMunicipio().getId() ) {
				
				bauxp.setInicioBolsa( calendario.getInicioExecucaoBolsa() );
				bauxp.setFimBolsa( calendario.getFimExecucaoBolsa() );
			}
		}
		obj.setBolsaAuxilioPeriodo( new ArrayList<BolsaAuxilioPeriodo>() );
		obj.getBolsaAuxilioPeriodo().add(bauxp);
	}
	
	/**
	 * Método não chamado por JSP's.
	 * @return
	 */
	public String direcionar() {
		if ( obj.getTipoBolsaAuxilio().isPromisaes() ) {
			bolsaAuxiliar = null;
			return forward("/sae/" + obj.getClass().getSimpleName() + "/form_promisaes.jsf");
		}
		if ( obj.getTipoBolsaAuxilio().isCreche() ) {
			if ( ValidatorUtil.isEmpty( bolsaAuxiliar ) )
				iniciarBolsaCreche();
			return forward("/sae/" + obj.getClass().getSimpleName() + "/form_creche.jsf");
		}
		if ( obj.getTipoBolsaAuxilio().isAtleta() ) {
			if ( ValidatorUtil.isEmpty( bolsaAuxiliar ) )
				iniciarBolsaAtleta();
			return forward("/sae/" + obj.getClass().getSimpleName() + "/form_atleta.jsf");
		}
		if ( obj.getTipoBolsaAuxilio().isOculos() )
			return forward("/sae/" + obj.getClass().getSimpleName() + "/form_oculos.jsf");
		else
			return forward("/sae/" + obj.getClass().getSimpleName() + "/form.jsf");
	}
	
	/** Inicar a bolsa auxiliar com as informaÃ§Ãµes bÃ¡sicas da bolsa Atleta */
	private void iniciarBolsaAtleta() {
		bolsaAuxiliar = new BolsaAuxilioAtleta();
		((BolsaAuxilioAtleta) bolsaAuxiliar).setModalidadeEsportiva(new ModalidadeEsportiva(ModalidadeEsportiva.ATLETISMO));
		((BolsaAuxilioAtleta) bolsaAuxiliar).setBolsaAuxilio(obj);
	}
	
	/** Inicar a bolsa auxiliar com as informaÃ§Ãµes bÃ¡sicas da bolsa Creche */
	private void iniciarBolsaCreche() {
		bolsaAuxiliar = new BolsaAuxilioCreche();
		((BolsaAuxilioCreche) bolsaAuxiliar).setPeriodoAtividadeAcademica( new PeriodoAtividadeAcademica(PeriodoAtividadeAcademica.EM_CRECHE) );
		((BolsaAuxilioCreche) bolsaAuxiliar).setBolsaAuxilio(obj);
	}
	
	/**
	 * Método não chamado por JSP's.
	 * @throws DAOException
	 */
	public void carregarQuestionarioRespostas() throws DAOException {
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		try {
			AdesaoCadastroUnicoBolsa adesao = dao.findByDiscente(obj.getDiscente().getId(), 
					CalendarioAcademicoHelper.getCalendario(obj.getDiscente()).getAno(), 
					CalendarioAcademicoHelper.getCalendario(obj.getDiscente()).getPeriodo());
			if ( adesao != null ) {
				QuestionarioRespostasMBean mBean = getMBean("questionarioRespostasBean");
				mBean.popularVizualicacaoRespostas(adesao);
				obj.setAdesaoCadUnico(null);
				obj.setAdesaoCadUnico(adesao);
			} 
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Carrega dados do domínio.
	 * <br/>
	 * Método não chamado por JSP's.
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarDados(AnoPeriodoReferenciaSAEDao dao) throws DAOException {
		obj.setDiscente( getUsuarioLogado().getDiscente() );
		anoReferencia = dao.anoPeriodoVigente();
		obj.setSituacaoBolsa(new SituacaoBolsaAuxilio(SituacaoBolsaAuxilio.EM_ANALISE));
	}

	/**
	 * Método chamado pela(s) seguinte(s) JSP's: <br/>
	 * <ul>
	 * 	<li>/sigaa.war/portais/discente/include/bolsas.jsp</li>
	 * 	<li>/sigaa.war/portais/discente/medio/menu_discente_medio.jsp</li>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SQLException
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String acompanharSituacaoBolsaAuxilio() throws SQLException, HibernateException, DAOException {
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoDao = getDAO(AnoPeriodoReferenciaSAEDao.class);
		try {
			anoReferencia = anoDao.anoPeriodoVigente();
			if ( getUsuarioLogado().getVinculoAtivo().isVinculoDiscente() ) {
				 bolsas = dao.acompanhamentoSolicitacaoBolsa(getUsuarioLogado().getVinculoAtivo().getDiscente().getId());
				 bolsas.addAll(getSolicitacoesAntigas());
			} else {
				addMensagemErro("Apenas Discentes podem acompanhar a solicitaÃ§Ã£o de bolsa.");
				return null;
			}
		} finally {
			dao.close();
		}
		
		return forward("/sae/BolsaAuxilio/acompanhamento.jsp");
	}
	
	/**
	 * Método não chamado por JSP's.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<BolsaAuxilioPeriodo> getSolicitacoesAntigas() throws HibernateException, DAOException {
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class);
		AnoPeriodoReferenciaSAEDao anoPeriodoDao = getDAO(AnoPeriodoReferenciaSAEDao.class);
		Collection<BolsaAuxilioPeriodo> bolsa = new ArrayList<BolsaAuxilioPeriodo>();
		try {
			AnoPeriodoReferenciaSAE anoPeriodo = anoPeriodoDao.anoPeriodoVigente();
			Collection<BolsaAuxilioPeriodo> bolsasAntigas = dao.findAllSolicitacoesBolsaAuxilio(getUsuarioLogado().getVinculoAtivo().getDiscente().getId());
			bolsa = UFRNUtils.deepCopy(bolsasAntigas);
			for (BolsaAuxilioPeriodo bolsaAuxilioPeriodo : bolsasAntigas) {
				if ( bolsaAuxilioPeriodo.getAno().equals(anoPeriodo.getAno()) && 
						bolsaAuxilioPeriodo.getPeriodo().equals(anoPeriodo.getPeriodo()) )
					bolsa.remove(bolsaAuxilioPeriodo);
			}
		} finally {
			dao.close();
			anoPeriodoDao.close();
		}
		return bolsa;
	}
	
	public String voltar() throws DAOException, SegurancaException {
		if ( isProae() ) {
			BuscarBolsaAuxilioMBean mBean = getMBean("buscarBolsaAuxilioMBean");
			return mBean.iniciarNovaSolicitacao();
		} else {
			return iniciarSolicitacaoBolsaAuxilio();
		}
	}
	
	/**
	 * Método chamado pela(s) seguinte(s) JSP's: <br/>
	 * <ul>
	 * 	<li>/sigaa.war/sae/BolsaAuxilio/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getMeiosTransporte() throws ArqException {
		return getAll(TipoMeioTransporte.class, "id", "meioTransporte");
	}
	
	/**
	 * Método chamado pela(s) seguinte(s) JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/sae/BolsaAuxilio/form.jsp</li>
	 * 	<li>/sigaa.war/sae/BolsaAuxilio/form_atleta.jsp</li>
	 * 	<li>/sigaa.war/sae/BolsaAuxilio/form_creche.jsp</li>
	 * 	<li>/sigaa.war/sae/BolsaAuxilio/form_oculos.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getDocumentosEntregues() throws ArqException {
		return getAll(DocumentosEntreguesSAE.class, "id", "descricao");
	}
	
	/**
	 * Retorna SelectItem com todas as residências
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
     *    <li> /SIGAA/app/sigaa.ear/sigaa.war/sae/BolsaAuxilio/form.jsp </li> 
	 * </ul>
	 *  	
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllResidencias()  {
		return getAllAtivo(ResidenciaUniversitaria.class, "id", "localizacao");
	}	
	
	public String imprimirComprovanteSAE() throws ArqException {
		BolsaAuxilioDao dao = getDAO(BolsaAuxilioDao.class);
		try {
			setId();
			setObj( dao.findByPrimaryKey(obj.getId(), BolsaAuxilio.class) );
			return forward("/sae/BolsaAuxilio/comprovante.jsp"); 
		} finally {
			dao.close();
		}
	}
	
	public AnoPeriodoReferenciaSAE getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(AnoPeriodoReferenciaSAE anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public Object getBolsaAuxiliar() {
		return bolsaAuxiliar;
	}

	public void setBolsaAuxiliar(Object bolsaAuxiliar) {
		this.bolsaAuxiliar = bolsaAuxiliar;
	}

	public int[] getTransportesIds() {
		return transportesIds;
	}

	public void setTransportesIds(int[] transportesIds) {
		this.transportesIds = transportesIds;
	}

	public int[] getDocumentosIds() {
		return documentosIds;
	}

	public void setDocumentosIds(int[] documentosIds) {
		this.documentosIds = documentosIds;
	}

	public Collection<BolsaAuxilioPeriodo> getBolsas() {
		return bolsas;
	}

	public void setBolsas(Collection<BolsaAuxilioPeriodo> bolsas) {
		this.bolsas = bolsas;
	}

}