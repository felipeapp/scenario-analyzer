/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/04/2010
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.GenericTipo;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.DiscenteLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.TurmaEntradaLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.ensino.negocio.DiscenteValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.mensagens.MensagensPortalCoordenadorStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoProcedenciaAluno;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TipoRedeEnsino;

/**
 * Managed Bean de Discente de Lato-Sensu, que realiza
 * as seguintes operações cadastro, atualização, remoção de um discente.
 *   
 * @author sist-sigaa-12
 *
 */
@Component("discenteLato")  @Scope("session")
public class DiscenteLatoMBean extends SigaaAbstractController<DiscenteLato> 
	implements OperadorDadosPessoais, OperadorDiscente {

	/** Refere-se a campos não informados */
	private static final int NAO_INFORMADO = -1;
	/** Refere-se a forma de ingresso não informada */
	private static final int FORMA_INGRESSO_NAO_INFORMADA = 34112;
	
	/** Coleção de SelectItem de um curso para o discente .*/
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>(0);
	
	/** Lista dos possíveis currículos do discente. */
	private List<SelectItem> turmasCombo = new ArrayList<SelectItem>(0);
	
	/** Lista dos possíveis currículos do discente. */
	private List<SelectItem> processosSeletivosCombo = new ArrayList<SelectItem>(0);
	
	/** Lista das possíveis procedências do discente. */
	private List<SelectItem> tiposProcedenciaCombo = new ArrayList<SelectItem>(0);
	
	/** Coleção de SelectItem das formas de ingresso possíveis para o discente. */
	private List<SelectItem> formasIngressoCombo = new ArrayList<SelectItem>(0);
	
	/** Indica se é discente antigo. */
	private boolean discenteAntigo;

	/** Realiza a busca pela Matricula */
	private boolean buscaMatricula;
	
	/** Realiza a busca pelo Nome */
	private boolean buscaNome;
	
	/** Realiza a busca pelo Curso */
	private boolean buscaCurso;
	
	/** Coleção de discentes */
	Collection<Discente> lista = null;
	
	/** Construtor padrão. */
	public DiscenteLatoMBean() {
		initObj();
	}

	/** Inicializa os atributos do controller.
	 * 
	 */
	private void initObj() {
		obj = new DiscenteLato();
		obj.setDiscente(new Discente());
		obj.setAnoIngresso( CalendarUtils.getAnoAtual() );
		obj.setTurmaEntrada(new TurmaEntradaLato());
		obj.getDiscente().setCurso(new Curso());
		obj.setProcessoSeletivo(new ProcessoSeletivo());
		processosSeletivosCombo.clear();
		lista = null;
		buscaCurso = false;
		buscaMatricula = false;
		buscaNome = false;
	}

	/**
	 * Iniciar o cadastro de discentes novos.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteNovo() throws ArqException {
	
		checkRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO);		
		setConfirmButton("Cadastrar");
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
		initObj();
		setDiscenteAntigo(false);
		obj.setStatus(StatusDiscente.ATIVO);
		return popular();
		
	}

	/**
	 * Inicia o cadastro de discentes antigos.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteAntigo() throws ArqException {
		
		checkRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO);
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
		initObj();
		setDiscenteAntigo(true);
		obj.setStatus(StatusDiscente.CONCLUIDO);
		return popular();
	}

	/**
	 * Chama o operador de dados pessoais para realizar a verificação do CPF
	 * e redireciona para o cadastro do discente de pós-graduação.
	 * @return
	 * @throws ArqException
	 */
	private String popular() throws ArqException{
		
		obj.setGestoraAcademica(getGenericDAO().findByPrimaryKey(getUnidadeGestora(), Unidade.class));
		obj.setNivel(getNivelEnsino());
		
		obj.setTipoProcedenciaAluno(new TipoProcedenciaAluno(GenericTipo.NAO_INFORMADO));
		if( (getAcessoMenu().isCoordenadorCursoLato() || getAcessoMenu().isSecretarioLato()) 
				&& getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) )
			obj.getTurmaEntrada().setCursoLato((CursoLato) getCursoAtualCoordenacao());

		if (getUsuarioLogado().isCoordenadorLato())
			obj.getTurmaEntrada().setCursoLato(getUsuarioLogado().getCursoLato());
		
		if (obj.getDiscente().getCurso().getId() != 0) 
			obj.getTurmaEntrada().setCursoLato((CursoLato) obj.getDiscente().getCurso());	
		else
			obj.setCurso(obj.getTurmaEntrada().getCursoLato());
		
		if (!(ValidatorUtil.isEmpty(obj.getProcessoSeletivo()))) 
			obj.getProcessoSeletivo().getEditalProcessoSeletivo();
			
		//Prepara as combos utilizados no formulário
		popularCombos();
		
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setObj(obj.getPessoa());
		dadosPessoaisMBean.carregarMunicipios();
		
		if (!ValidatorUtil.isEmpty( dadosPessoaisMBean.getObj() ))
			dadosPessoaisMBean.setSubmitButton("Atualizar");
		
		dadosPessoaisMBean.setCodigoOperacao(OperacaoDadosPessoais.DISCENTE_LATO);
	
		return dadosPessoaisMBean.popular();
		
	}
	
	/**
	 * Popula selects a serem usados nas JSPs. O método é usado tanto para
	 * visualização do formulário para cadastro como para atualização ->>> Cada
	 * tipo de discente deve ter seus SELECTS específicos populados aqui.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>Método não invocado por JSP.</li>
	 *	</ul>
	 * 
	 * @throws ArqException 
	 */
	public void popularCombos() throws ArqException {
		
		CursoLatoDao dao = getDAO(CursoLatoDao.class);
		
		if( getSubSistema().getId() == SigaaSubsistemas.LATO_SENSU.getId()) {
			setCursosCombo( toSelectItems( dao.findByNivel(NivelEnsino.LATO),"id","nome") );
		}
		else if ( getSubSistema().getId() == SigaaSubsistemas.PORTAL_COORDENADOR_LATO.getId()){
			if(getUsuarioLogado().isUserInRole(SigaaPapeis.SECRETARIA_LATO)) {
				getCursosCombo().clear();
				getCursosCombo().add(new SelectItem(  obj.getTurmaEntrada().getCursoLato().getId(),obj.getTurmaEntrada().getCursoLato().getDescricao())  ); 
			}
			else { //É Coordenador
				setCursosCombo( toSelectItems( dao.findAllCoordenadoPor(getServidorUsuario().getId()),"id","nome") );
			}
		}
		
		
		carregarTurmas(null);
		
		setTiposProcedenciaCombo( toSelectItems( dao.findAll(TipoProcedenciaAluno.class), "id", "descricao") );
		setFormasIngressoCombo( toSelectItems(dao.findAll(FormaIngresso.class), "id", "descricao") );
		
	}

	/**
	 * Operação executada antes de entrar no cadastrar e do validar.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>Método não invocado por JSP.</li>
	 *	</ul>
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {

		if( obj.getPessoa().getTipoRaca().getId() == 0 )
			obj.getPessoa().setTipoRaca(null);
		
		super.beforeCadastrarAndValidate();
	}

	/**
	 * Serve para cadastrar um discente Lato Sensu.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/lato/discente/resumo.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException {

		clearMensagens();
		if (!getUltimoComando().equals(SigaaListaComando.CADASTRAR_DISCENTE) && 
				!getUltimoComando().equals(SigaaListaComando.CADASTRAR_PESSOA) && 
				!getUltimoComando().equals(SigaaListaComando.ALTERAR_PESSOA) &&  
				!getUltimoComando().equals(SigaaListaComando.ALTERAR_DISCENTE_LATO))
			return cancelar();
		
		ListaMensagens mensagens = new ListaMensagens();
 
		if( obj.getId() > 0){
			DiscenteLato discOriginal = getGenericDAO().findByPrimaryKey( obj.getId() , DiscenteLato.class);
			if( !obj.getTipo().equals( discOriginal.getTipo() ) ){
				addMensagem(MensagensPortalCoordenadorStricto.NAO_E_POSSIVEL_ALTERAR_TIPO_DO_DISCENTE);
				return null;
			}
		}

		DiscenteValidator.validarDadosDiscenteLato(obj, null,  mensagens);
		

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		// removendo atributos transientes
		if( obj.getPessoa().getEstadoCivil() != null &&
				obj.getPessoa().getEstadoCivil().getId() == 0 )
			obj.getPessoa().setEstadoCivil(null);

		if( obj.getPessoa().getTipoRaca()!= null &&
				obj.getPessoa().getTipoRaca().getId() == 0 )
			obj.getPessoa().setTipoRaca(null);

		Comando comando = SigaaListaComando.CADASTRAR_DISCENTE;
		if( obj.getId() > 0 )
			comando = SigaaListaComando.ALTERAR_DISCENTE_LATO;
		
		DiscenteMov mov = new DiscenteMov(comando, obj);
		mov.setDiscenteAntigo(isDiscenteAntigo());


		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			if( comando.equals(SigaaListaComando.CADASTRAR_DISCENTE) ){
				addMessage( "Discente "+obj.getNome()+" cadastrado com sucesso, e associado com o " +
						"número de  matrícula " + obj.getMatricula(), TipoMensagemUFRN.INFORMATION);
			}else{
				addMessage( "O discente "+obj.toString()+" foi atualizado com sucesso.", TipoMensagemUFRN.INFORMATION);
			}
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		}

		if (getCurrentSession().getAttribute("discenteProcessoSeletivo") != null) {
			resetBean();
			getCurrentSession().removeAttribute("discenteProcessoSeletivo");
			return redirect("/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsf");
		} else {
		    	resetBean();
			return forward("/lato/discente/buscar.jsp");
		}
	}
	
	
	/**
	 * Carrega o combobox das turmas no formulário de acordo com os currículos selecionados.<br>
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/lato/discente/form.jsp</li>
	 *	</ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarTurmas(ValueChangeEvent e) throws DAOException  {
		
		TurmaEntradaLatoDao dao = getDAO(TurmaEntradaLatoDao.class);
		Integer id = null;
		if( e != null )
			id = (Integer) e.getNewValue();
		else if( !isEmpty( obj.getTurmaEntrada().getCursoLato().getId() ) ){
			id = obj.getTurmaEntrada().getCursoLato().getId();
			
		}
		if( id == null )
			return;
			turmasCombo = toSelectItems(dao.findByCursoLato(id, true), "id", "descricao");

		setProcessosSeletivosCombo(toSelectItems(getDAO(ProcessoSeletivoDao.class).
				findByCursoLato(id),"id", "nomeCompleto"));
			
	}

	/**
	 * Seta os dados pessoais da pessoa.
	 */
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * Direciona para a tela de dados pessoais.
	 * 
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>Não invocado por jsp.</li>
	 *	</ul>
	 */
	public String submeterDadosPessoais() {
		return telaDadosDiscente();
	}
	
	/**
	 * Submete os dados dos discente Lato Sensu.
	 * 
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>Não invocado por jsp.</li>
	 *	</ul>
	 */
	public String submeterDadosDiscente() throws ArqException {

		ListaMensagens mensagens = new ListaMensagens();
		DiscenteValidator.validarDadosDiscenteLato(obj, null, mensagens);
		erros.addAll(mensagens);
		
		if(hasErrors())
			return null;
		
		GenericDAO dao = getGenericDAO();
		obj.setFormaIngresso( dao.findByPrimaryKey(obj.getFormaIngresso().getId(),	FormaIngresso.class));
		obj.setTurmaEntrada( dao.findByPrimaryKey(obj.getTurmaEntrada().getId(), TurmaEntradaLato.class) );
		obj.setTipoProcedenciaAluno( dao.findByPrimaryKey(obj.getTipoProcedenciaAluno().getId(), 
				TipoProcedenciaAluno.class) );
		
		if (obj.getFormaIngresso() != null && obj.getFormaIngresso().getId()>0)
		obj.setFormaIngresso( dao.findByPrimaryKey(
				obj.getFormaIngresso().getId(), FormaIngresso.class) );
		else
			obj.setFormaIngresso(new FormaIngresso(FORMA_INGRESSO_NAO_INFORMADA));
		
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

		if (obj.getId() > 0) 
			prepareMovimento(SigaaListaComando.ALTERAR_DISCENTE_LATO);
		else
			prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
		
		return telaResumo();
	}
	
	/** 
	 * Redireciona para a busca de discentes, a fim de realizar a operação de atualização de dados.<br>
	 * 
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 *	</ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,SigaaPapeis.SECRETARIA_LATO);
		prepareMovimento( SigaaListaComando.ALTERAR_DISCENTE_LATO);
		initObj();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_LATO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Seleciona o discente e redireciona para o formulário de alteração de discente.
	 * 
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>Não invocado por jsp.</li>
	 *	</ul>
	 */
	public String selecionaDiscente() throws ArqException {
		
		
		obj.getDiscente().setId(Integer.parseInt(getParameter("idDiscente")));
		setObj(getGenericDAO().findAndFetch(obj.getDiscente().getId(), DiscenteLato.class, "discente.pessoa.contaBancaria"));
		
		/** carregando forma de ingresso */
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		ProcessoSeletivoDao daoPS = getDAO(ProcessoSeletivoDao.class);
		
		try{			
			
			popular();
			

		} finally {
			if (dao != null)
				dao.close();
			if (daoPS != null)
				daoPS.close();
		}

		return telaDadosDiscente();
	}

	/**
	 * Serve pra direcionar para a tela de alteração e remoção de 
	 * um discente de Lato Sensu.<br>
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp
	 * @return
	 */
	public String alterarRemover() {
		initObj();
		return forward("/lato/discente/buscar.jsp");
	}

	/**
	 * Método responsável pela realização das buscas dependendo do parâmetro selecionado e do papel 
	 * que o usuário logado possui.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/discente/buscar.jsp
	 */
	@Override
	public String buscar() throws Exception {
		DiscenteLatoDao daoLato = getDAO(DiscenteLatoDao.class);
		
		if (!buscaCurso && !buscaMatricula && !buscaNome) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		
		if (buscaMatricula && obj.getDiscente().getMatricula() == null) 
			addMensagemErro("Matricula: Campo não informado.");
		if (buscaCurso && obj.getDiscente().getCurso().getId() == 0) 
			addMensagemErro("Curso: Campo não informado.");
		if (buscaNome && (obj.getDiscente().getNome().equals("") || obj.getDiscente().getNome() == null)) 
			addMensagemErro("Nome: Campo não informado.");
		
		if (buscaMatricula && !hasOnlyErrors()) {
			
			DiscenteAdapter disc = daoLato.findByMatricula(obj.getDiscente().getMatricula(), getNivelEnsino());
					
			lista = new HashSet<Discente>();
			if (disc != null)
				lista.add(disc.getDiscente());
		
		}else if (buscaNome && !hasOnlyErrors()) {
			if (getUsuarioLogado().isCoordenadorLato()) {
					lista = daoLato.findByCursoNome(getUsuarioLogado().getCursoLato().getId(), obj.getDiscente().getNome(), 
							getPaginacao());
			}else
				lista = daoLato.findByNome(obj.getDiscente().getNome(), UnidadeGeral.UNIDADE_DIREITO_GLOBAL, 
						new char[]{getNivelEnsino()}, null, false, true, getPaginacao());
		}else if (buscaCurso && !hasOnlyErrors()) {
			if (getUsuarioLogado().isCoordenadorLato()) 
				lista = daoLato.findAllAtivosByCurso((CursoLato) getCursoAtualCoordenacao());
			else
				lista = daoLato.findAllAtivosByCurso(new CursoLato(obj.getDiscente().getCurso().getId()));
		}
		return getCurrentURL();
	}
	
	/**
	 * Método que carrega todos os dados pessoais do discente Lato Sensu.<br>
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/discente/buscar.jsp
	 * @return
	 * @throws ArqException
	 */
	public String atualizarDadosPessoais() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO);		
		setConfirmButton("Cadastrar");
		prepareMovimento(SigaaListaComando.ALTERAR_DISCENTE);
		obj.getDiscente().setId(Integer.parseInt(getParameter("idDiscente")));
		setObj(getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), DiscenteLato.class));
		return popular();
	}

	/**
	 * Método usado quando se deseja remover um discenteLato.<br>
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/lato/discente/buscar.jsp
	 */
	@Override
	public String remover() throws ArqException {
		obj.getDiscente().setId(Integer.parseInt(getParameter("idDiscente")));
		obj.setDiscente(getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), Discente.class));
		
		if (obj.getDiscente().getId() == 0 && obj.getDiscente() == null) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		}
		
		DiscenteMov mov = new DiscenteMov(SigaaListaComando.REMOVER_DISCENTE, obj.getDiscente());
		try {
			prepareMovimento(SigaaListaComando.REMOVER_DISCENTE);
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, obj.getDiscente().getNome());
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		}
		return alterarRemover();
	}
	
	/**
	 * Seta o discente no obj.
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj = (DiscenteLato) discente;
	}

	/** Retorna o formulário de dados do discente.
	 * @return /sigaa.war/lato/discente/form.jsp
	 */
	public String telaDadosPessoais() {
		obj.getPessoa().prepararDados();
		return forward( "/geral/pessoa/dados_pessoais.jsp");
	}
	
	/** Retorna o formulário de dados do discente.
	 * @return /sigaa.war/lato/discente/form.jsp
	 */
	private String telaDadosDiscente() {
		return forward( "/lato/discente/form.jsp");
	}
	
	/** Retorna o formulário de dados do discente.
	 * @return /sigaa.war/lato/discente/form.jsp
	 */
	private String telaResumo() {
		return forward( "/lato/discente/resumo.jsp");
	}

	/** Retorna a coleção de SelectItem de um curso para o discente . 
	 * @return
	 */
	public List<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	
	/** Seta a coleção de SelectItem de um curso para o discente.
	 * @param possiveisCursos
	 */
	public void setCursosCombo(List<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	/** Indica se é discente antigo. 
	 * @return
	 */
	public boolean isDiscenteAntigo() {
		return discenteAntigo;
	}

	/** 
	 * Seta se é discente antigo. 
	 */
	public void setDiscenteAntigo(boolean discenteAntigo) {
		this.discenteAntigo = discenteAntigo;
	}

	public List<SelectItem> getTurmasCombo() {
		return turmasCombo;
	}

	public void setTurmasCombo(List<SelectItem> turmasCombo) {
		this.turmasCombo = turmasCombo;
	}

	public List<SelectItem> getProcessosSeletivosCombo() {
		return processosSeletivosCombo;
	}

	public void setProcessosSeletivosCombo(List<SelectItem> processosSeletivosCombo) {
		this.processosSeletivosCombo = processosSeletivosCombo;
	}

	public List<SelectItem> getTiposProcedenciaCombo() {
		return tiposProcedenciaCombo;
	}

	public void setTiposProcedenciaCombo(List<SelectItem> tiposProcedenciaCombo) {
		this.tiposProcedenciaCombo = tiposProcedenciaCombo;
	}

	public List<SelectItem> getFormasIngressoCombo() {
		return formasIngressoCombo;
	}

	public void setFormasIngressoCombo(List<SelectItem> formasIngressoCombo) {
		this.formasIngressoCombo = formasIngressoCombo;
	}

	public boolean isBuscaMatricula() {
		return buscaMatricula;
	}

	public void setBuscaMatricula(boolean buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	/**
	 * Verifica se o mesmo é coordenador se for habita a possibilidate de buscar curso.
	 * 
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/SIGAA/app/sigaa.ear/sigaa.war/lato/discente/buscar.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 */
	public boolean isBuscaCurso() {
		if (getUsuarioLogado().isCoordenadorLato()){
			buscaCurso = true;
			obj.getDiscente().setCurso(getCursoAtualCoordenacao());
			return true;
		}
		return buscaCurso;
	}

	public void setBuscaCurso(boolean buscaCurso) {
		this.buscaCurso = buscaCurso;
	}

	public Collection<Discente> getLista() {
		return lista;
	}

	public void setLista(Collection<Discente> lista) {
		this.lista = lista;
	}
	
}