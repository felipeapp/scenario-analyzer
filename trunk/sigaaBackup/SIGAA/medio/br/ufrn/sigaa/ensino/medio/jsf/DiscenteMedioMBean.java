/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/06/2011
 */
package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.medio.dao.DiscenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.negocio.DiscenteMedioValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TipoRedeEnsino;

/**
 * Managed-Bean das operações e casos de uso envolvendo os Discentes Médio
 *
 * @author Arlindo
 */
@Component("discenteMedio") @Scope("session")
public class DiscenteMedioMBean extends SigaaAbstractController<DiscenteMedio> implements OperadorDadosPessoais, OperadorDiscente {
	
	/** Indica se o discente é antigo */
	private boolean discenteAntigo;
	
	/** Lista das séries por curso de ensino médio. */
	private List<SelectItem> seriesByCurso = new ArrayList<SelectItem>(0);
	
	/** Coleção de SelectItem das formas de ingresso possíveis para o discente. */
	private List<SelectItem> formasIngressoCombo = new ArrayList<SelectItem>(0);
	
	/** Refere-se a campos não informados */
	private static final int NAO_INFORMADO = -1;
	/** 
	 * Construtor padrão. Invoca o método {@link #initObj()}.
	 * @throws DAOException
	 */	
	public DiscenteMedioMBean() throws DAOException {
		initObj();
	}
	
	/** Inicializa os dados do controller.
	 * @throws DAOException
	 */
	private void initObj() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();		
		if(cal == null){
			cal = new CalendarioAcademico();
			cal.setAno( CalendarUtils.getAnoAtual() );
		}
		
		obj = new DiscenteMedio();
		obj.setAnoIngresso( cal.getAno() );
		obj.setSerieIngresso(new Serie() );
		obj.setCurso(new CursoMedio());
		obj.setSerieIngresso(new Serie());
		obj.setOpcaoTurno(new Turno());
		obj.setFormaIngresso(new FormaIngresso());
		obj.setNivel(NivelEnsino.MEDIO);
		obj.setParticipaBolsaFamilia(false);
		obj.setUtilizaTransporteEscolarPublico(false);
		obj.setTipo(Discente.REGULAR);
	}	
		
	/**
	 * Inicia o cadastro de um novo discente
	 * <br><br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarNovoDiscente() throws ArqException{
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		initObj();
		setDiscenteAntigo(false);
		obj.setStatus(StatusDiscente.ATIVO);
		return popular();
	}
	
	/**
	 * Inicia o cadastro de um discente antigo
	 * <br><br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDiscenteAntigo() throws ArqException{
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		initObj();
		setDiscenteAntigo(true);
		obj.setStatus(StatusDiscente.CONCLUIDO);
		return popular();
	}	
	
	/** Redireciona para a busca de discentes, 
	 * a fim de realizar a operação de atualização de dados.
	 * <br><br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		prepareMovimento( SigaaListaComando.ALTERAR_DISCENTE_MEDIO);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_DISCENTE_MEDIO.getId());
		initObj();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_MEDIO);
		return buscaDiscenteMBean.popular();
	}
	
	/** Redireciona para a busca de discentes. 
	 * 	 * <br><br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/consultas.jsp</li>
	 * </ul>
	 */
	public String listar() throws ArqException {
		checkRole(new int[] {SigaaPapeis.PEDAGOGICO_MEDIO});
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_MEDIO);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Seleciona o discente e redireciona para o formulário de alteração de discente.
	 * Chamado por {@link BuscaDiscenteMBean#redirecionarDiscente(Discente)}
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 * 
	 * JSP: Não invocado por jsp.
	 */
	@Override
	public String selecionaDiscente() throws ArqException {
		popular();
		if (getAcessoMenu().isPedagogico()) {
			return redirect( telaResumo() );
		} else {
			setOperacaoAtiva(SigaaListaComando.ALTERAR_DISCENTE_MEDIO.getId());
			popularCombos();
			return forward( getFormPage() );
		}
	}
	
	/**
	 * Seta o discente
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj = (DiscenteMedio) discente;
	}
	
	/**
	 * Submete os dados do discente
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/discente/form.jsp</li>
	 * </ul>
	 */
	public String submeterDadosDiscente() throws DAOException {

		erros = new ListaMensagens();
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId(),SigaaListaComando.ALTERAR_DISCENTE_MEDIO.getId()))
				return cancelar();
			
			DiscenteMedioValidator.validarDadosDiscenteMedio(obj, discenteAntigo, erros);
			
			if(hasErrors())
				return null;
			
			if ( isDiscenteAntigo() && dao.findByMatricula(obj.getMatricula()) != null ) 
				addMensagemErro("Já existe um discente cadastrado com a matrícula informada");		
		
			obj.setFormaIngresso( dao.findByPrimaryKey(
					obj.getFormaIngresso().getId(), FormaIngresso.class) );
			obj.setCurso( dao.findByPrimaryKey( 
					obj.getCurso().getId(), CursoMedio.class, "id", "nome", "unidade.id"));
			
			if (!ValidatorUtil.isEmpty(obj.getOpcaoTurno()))
				obj.setOpcaoTurno( dao.findByPrimaryKey( 
						obj.getOpcaoTurno().getId(), Turno.class) );
			else
				obj.setOpcaoTurno(null);
			
			obj.setSerieIngresso( dao.findByPrimaryKey( 
					obj.getSerieIngresso().getId(), Serie.class) );
			
			if( obj.getId() > 0 )
				obj.setPessoa( dao.findAndFetch(obj.getPessoa().getId(), Pessoa.class, "estadoCivil", "tipoRedeEnsino", 
						"tipoRaca", "unidadeFederativa", "pais") );	
			else 
				popularDadosPessoais();
			
			if (ValidatorUtil.isEmpty(obj.getGestoraAcademica()))
				obj.setGestoraAcademica( dao.findByPrimaryKey(
							obj.getCurso().getUnidade().getId(), Unidade.class));
			
		} finally {
			dao.close();
		}

		return telaResumo();
	}	
	
	/**
	 * Popula os comboboxs exibidos no cadastro
	 * @throws DAOException
	 */
	private void popularCombos() throws DAOException{
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		SerieDao daoSerie = getDAO( SerieDao.class );
		try {
			List<FormaIngresso> lista = new ArrayList<FormaIngresso>();
			
			lista.addAll(dao.findByIds(FormaIngresso.PROCESSO_SELETIVO.getId(), 
					FormaIngresso.REINGRESSO_AUTOMATICO.getId(),
					FormaIngresso.MOBILIDADE_ESTUDANTIL.getId()));
			
			formasIngressoCombo = toSelectItems(lista, "id", "descricao");
			
			if (obj.getId() > 0 && obj.getCurso() != null && obj.getCurso().getId() > 0){
				seriesByCurso = toSelectItems(daoSerie.findByCurso((CursoMedio) obj.getCurso()), "id", "descricaoCompleta");
			}
			
			if (ValidatorUtil.isEmpty(obj.getOpcaoTurno()))
				obj.setOpcaoTurno(new Turno());
			else
				dao.refresh(obj.getOpcaoTurno());
			
			dao.refresh(obj.getSerieIngresso());
			
		} finally {
			if (dao != null)
				dao.close();
			if (daoSerie != null)
				daoSerie.close();
		}		
	}
	
	/**
	 * Chama o operador de dados pessoais para realizar a verificação do 
	 * CPF e redireciona para o cadastro do discente de graduação. 
	 * <br/><br/>
	 * Método não chamado por JSPs.
	 * @return
	 * @throws ArqException
	 */
	public String popular() throws ArqException{
		
		if (getAcessoMenu().isPedagogico()) {
			checkRole(SigaaPapeis.PEDAGOGICO_MEDIO);
		} else {
			checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
			setConfirmButton("Cadastrar");
			prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
			getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId());
		}
		
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.DISCENTE_MEDIO );
		return dadosPessoaisMBean.popular();
	}	
	
	/** 
	 * Cadastra o discente selecionado
	 * <br/><br/>
	 * Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/discente/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		erros = new ListaMensagens();
		
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId(), SigaaListaComando.ALTERAR_DISCENTE_MEDIO.getId()))
			return cancelar();
		
		DiscenteMedioValidator.validarDadosDiscenteMedio(obj, discenteAntigo, erros);
		
		if ( !discenteAntigo && obj.getId() == 0) {
			CalendarioAcademico cal = getCalendarioVigente();
			int semDiscente = new Integer(obj.getAnoIngresso());
			int semAnterior = cal.getAno() - 1;
			int semSeguinte = cal.getAno() + 1;
			
			if (semDiscente < semAnterior || semDiscente > semSeguinte)
				erros.addMensagem(new MensagemAviso("Ano-Período de Ingresso Inválido", TipoMensagemUFRN.ERROR));
		} 

		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}

		// Cria o movimento
		DiscenteMov mov = new DiscenteMov(SigaaListaComando.CADASTRAR_DISCENTE, obj);
		mov.setDiscenteAntigo(isDiscenteAntigo());
		
		try {
			execute(mov);
			addMessage( "Discente cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}

		getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		
		resetBean();
		DadosPessoaisMBean dMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dMBean.resetBean();
		initObj();

		return cancelar();
	}	

	/**
	 * Seta a pessoa no discente
	 */
	@Override
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * Submete os dados pessoais para serem cadastrados
	 * <br/><br/>
	 * Método não chamado por JSPs.
	 */
	@Override
	public String submeterDadosPessoais() {

		try {
			/* Verifica se a pessoa já está associada a um outro discente ativo */
			if( obj.getPessoa().getId() != 0 ){
				DiscenteMedioDao dao = getDAO(DiscenteMedioDao.class);
				try {
					DiscenteMedio d = dao.findAtivoByPessoa( obj.getPessoa().getId() );
					if( d != null && d.getId() != 0 ) {
						addMessage("Atenção! Já existe um discente " + d.getStatusString() 
							+ " associado a esta pessoa (mat. " + d.getMatricula() + ").", TipoMensagemUFRN.WARNING);
						return null;
					}
 				} finally {
 					if (dao != null)
 						dao.close();
 				}
			}
			
			if(checkOperacaoDadosPessoaisAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId()+"")){
				addMensagem(MensagensGerais.JA_EXISTE_OUTRA_OPERACAO_DADOS_PESSOAIS_ATIVA);
				redirectJSF(getSubSistema().getLink());
				return null;
			}
			
			obj.getPessoa().getEnderecoContato().setTipoLogradouro(
						getGenericDAO().refresh(obj.getPessoa().getEnderecoContato().getTipoLogradouro()));
			
			popularCombos();
			
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		return forward( getFormPage() );
	}
	
	/** 
	 * Carrega as séries pertencentes ao curso de ensino médio selecionado na jsp..
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso(ValueChangeEvent e) throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		
		CursoMedio cursoMedio = null;
		
		if( e != null && (Integer)e.getNewValue() > 0 )
			cursoMedio = dao.findByPrimaryKey((Integer)e.getNewValue(), CursoMedio.class);
		else {
			seriesByCurso = new ArrayList<SelectItem>(0);
			return;
		}	
		
		cursoMedio.setNivel(getNivelEnsino());
		if (cursoMedio != null)
			seriesByCurso = toSelectItems(dao.findByCurso(cursoMedio), "id", "descricaoCompleta");
	}	
	
	/**
	 * Popula os dados pessoais do discente
	 * <br/>
	 * Método não chamado por JSP's
	 * @throws DAOException
	 */
	public void popularDadosPessoais() throws DAOException{
		GenericDAO dao = getGenericDAO();
		if(obj.getPessoa().getEstadoCivil() != null && (obj.getPessoa().getEstadoCivil().getId()>0 || obj.getPessoa().getEstadoCivil().getId()==NAO_INFORMADO))
			obj.getPessoa().setEstadoCivil(dao.findByPrimaryKey( 
				obj.getPessoa().getEstadoCivil().getId(), EstadoCivil.class));
		else
			obj.getPessoa().setEstadoCivil(new EstadoCivil(NAO_INFORMADO));
		
		if(obj.getPessoa().getTipoRedeEnsino() != null && (obj.getPessoa().getTipoRedeEnsino().getId()>0 || obj.getPessoa().getTipoRedeEnsino().getId()==NAO_INFORMADO))
			obj.getPessoa().setTipoRedeEnsino(dao.findByPrimaryKey( 
				obj.getPessoa().getTipoRedeEnsino().getId(), TipoRedeEnsino.class));
		else
			obj.getPessoa().setTipoRedeEnsino(new TipoRedeEnsino(NAO_INFORMADO));
		
		if(obj.getPessoa().getTipoRaca() != null && (obj.getPessoa().getTipoRaca().getId()>0 || obj.getPessoa().getTipoRaca().getId()==NAO_INFORMADO))
			obj.getPessoa().setTipoRaca(dao.findByPrimaryKey( 
				obj.getPessoa().getTipoRaca().getId(), TipoRaca.class));
		else
			obj.getPessoa().setTipoRaca(new TipoRaca(NAO_INFORMADO));
		
		if(obj.getPessoa().getUnidadeFederativa() != null && (obj.getPessoa().getUnidadeFederativa().getId()>0 || obj.getPessoa().getUnidadeFederativa().getId()==NAO_INFORMADO))
			obj.getPessoa().setUnidadeFederativa(dao.findByPrimaryKey(
				obj.getPessoa().getUnidadeFederativa().getId(), UnidadeFederativa.class));
		else
			obj.getPessoa().setUnidadeFederativa(new UnidadeFederativa(NAO_INFORMADO));
		
		if(obj.getPessoa().getPais() != null && (obj.getPessoa().getPais().getId()>0 || obj.getPessoa().getPais().getId()==NAO_INFORMADO))
			obj.getPessoa().setPais(dao.findByPrimaryKey(
				obj.getPessoa().getPais().getId(), Pais.class));
		else
			obj.getPessoa().setPais(new Pais(NAO_INFORMADO));

		if(obj.getPessoa().getIdentidade() != null && obj.getPessoa().getIdentidade().getUnidadeFederativa() != null && (obj.getPessoa().getIdentidade().getUnidadeFederativa().getId()>0 || obj.getPessoa().getIdentidade().getUnidadeFederativa().getId()==NAO_INFORMADO))
			obj.getPessoa().getIdentidade().setUnidadeFederativa(dao.findByPrimaryKey(
				obj.getPessoa().getIdentidade().getUnidadeFederativa().getId(), UnidadeFederativa.class));
		else
			obj.getPessoa().getIdentidade().setUnidadeFederativa(new UnidadeFederativa(NAO_INFORMADO));
		
		if(obj.getPessoa().getEnderecoContato().getUnidadeFederativa() != null && (obj.getPessoa().getEnderecoContato().getUnidadeFederativa().getId()>0 || obj.getPessoa().getEnderecoContato().getUnidadeFederativa().getId()==NAO_INFORMADO))
			obj.getPessoa().getEnderecoContato().setUnidadeFederativa(dao.findByPrimaryKey(
				obj.getPessoa().getEnderecoContato().getUnidadeFederativa().getId(), UnidadeFederativa.class));
		else
			obj.getPessoa().getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa(NAO_INFORMADO));
		
		if(obj.getPessoa().getEnderecoContato().getMunicipio() != null && (obj.getPessoa().getEnderecoContato().getMunicipio().getId()>0 || obj.getPessoa().getEnderecoContato().getMunicipio().getId()==NAO_INFORMADO))
			obj.getPessoa().getEnderecoContato().setMunicipio(dao.findByPrimaryKey(
				obj.getPessoa().getEnderecoContato().getMunicipio().getId(), Municipio.class));
		else
			obj.getPessoa().getEnderecoContato().setMunicipio(new Municipio(NAO_INFORMADO));

	}
	
	/**
	 * Verifica se a operação de dados pessoais (cadastro ou atualização) continua ativa
	 * @param operacoes
	 * @return
	 */
	private boolean checkOperacaoDadosPessoaisAtiva(String ...operacoes) {
		String operacaoAtiva = (String) getCurrentSession().getAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		if (operacaoAtiva != null) {
			for (String operacao : operacoes) {
				if (operacaoAtiva.equals(operacao)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}	
	
	/** Retorna o formulário de dados do discente.
	 * @return /medio/discente/form.jsp
	 */
	private String telaResumo() {
		return forward( "/medio/discente/resumo.jsp");
	}	
	
	/** Formulário de cadastro */
	@Override
	public String getFormPage() {
		return "/medio/discente/form.jsp";
	}
	
	/**
	 * Direciona o usuário para a tela de busca de discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/discente/resumo.jsp.</li>
	 * </ul>
	 */
	public String telaBuscaDiscentes() {
		return forward("/graduacao/busca_discente.jsp");
	}
	
	/**
	 * Retorna as formas de ingresso possíveis
	 * <br/><br/>
	 * Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/discente/form.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public List<SelectItem> getFormasIngressoCombo() throws DAOException {
		return formasIngressoCombo;
	}

	public void setFormasIngressoCombo(List<SelectItem> formasIngressoCombo) {
		this.formasIngressoCombo = formasIngressoCombo;
	}

	public boolean isDiscenteAntigo() {
		return discenteAntigo;
	}

	public void setDiscenteAntigo(boolean discenteAntigo) {
		this.discenteAntigo = discenteAntigo;
	}

	public List<SelectItem> getSeriesByCurso() {
		return seriesByCurso;
	}

	public void setSeriesByCurso(List<SelectItem> seriesByCurso) {
		this.seriesByCurso = seriesByCurso;
	}

	/**
	 * Retorna os status do discente médio
	 * <br/><br/>
	 * Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/discente/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getStatusCombo() {
		return toSelectItems(StatusDiscente.getStatusMedio(), "id", "descricao");
	}
}
