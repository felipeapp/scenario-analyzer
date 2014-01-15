/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 03/02/2009
 * 
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.AreaConcentracaoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.LinhaPesquisaStrictoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.StatusProcessoSeletivo;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.OrigemDiscentePos;
import br.ufrn.sigaa.ensino.stricto.negocio.DiscenteStrictoValidator;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.mensagens.MensagensPortalCoordenadorStricto;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed Bean de Discente de P�s Gradua��o Stricto Sensu que realiza
 * as seguintes opera��es cadastro, atualiza��o e remo��o de um discente.  
 * 
 * @author leonardo
 * @author Victor Hugo
 */
@Component("discenteStricto")  @Scope("session")
public class DiscenteStrictoMBean extends SigaaAbstractController<DiscenteStricto> implements OperadorDadosPessoais, OperadorDiscente {

	/** Indica se deve bloquear a edi��o do campo ano/semestre no formul�rio. */
	private boolean blockAnoSemestre;
	
	/** Cole��o de SelectItem das formas de ingresso poss�veis para o discente. */
	private Collection<SelectItem> formasIngressoCombo = new ArrayList<SelectItem>(0);

	/** Cole��o de SelectItem de um curso para o discente .*/
	private List<SelectItem> possiveisCursos = new ArrayList<SelectItem>(0);

	/** Lista das poss�veis �reas de pesquisa do discente. */
	private List<SelectItem> possiveisAreas = new ArrayList<SelectItem>(0);

	/** Lista das poss�veis linhas de pesquisa do discente. */
	private List<SelectItem> possiveisLinhas = new ArrayList<SelectItem>(0);

	/** Lista dos poss�veis status de um discente. */
	private List<SelectItem> possiveisStatus = new ArrayList<SelectItem>(0);

	/** Lista dos poss�veis curr�culos do discente. */
	private List<SelectItem> possiveisCurriculos = new ArrayList<SelectItem>(0);
	
	/** Lista de Processos Seletivos. */
	private List<SelectItem> processosSeletivosCombo = new ArrayList<SelectItem>(0);	

	/** Orientador do discente. */
	private EquipePrograma orientador;
	
	/** Co-orientador do discente. */
	private EquipePrograma coOrientador;

	/** Possui a quantidade atualizada de orientados do docente para valida��o no cadastro de discente. */
	private int	totalOrientacoes;

	/** Data de in�cio da orienta��o. */
	private Date inicioOrientador;
	
	/** Data de in�cio da co-orienta��o. */
	private Date inicioCoOrientador;

	/** Indica se � discente antigo. */
	private boolean discenteAntigo;
	
	/** Indica se o aluno participa do programa em rede sendo aluno de uma institui��o de ensino diferente da UFRN. */
	private boolean alunoOutraInstituicao = false;

	
	/** Construtor padr�o. */
	public DiscenteStrictoMBean() {
		initObj();
	}

	/** Inicializa os atributos do controller.
	 * 
	 */
	private void initObj() {
		obj = new DiscenteStricto();
		obj.setDiscente(new Discente());
		
		CalendarioAcademico cal = getCalendarioVigente();
		if( isEmpty(cal) ){
			cal = new CalendarioAcademico();
			cal.setAno( CalendarUtils.getAnoAtual() );
			cal.setPeriodo( getPeriodoAtual() );
		}
		obj.setAnoIngresso( cal.getAno() );
		obj.setPeriodoIngresso( cal.getPeriodo() );
		obj.setMesEntrada( CalendarUtils.getMesAtual() + 1 );
		obj.inicializarAtributosNulos();

		orientador = new EquipePrograma();
		orientador.setServidor( new Servidor() );
		coOrientador = new EquipePrograma();
		coOrientador.setServidor( new Servidor() );
		inicioCoOrientador = null;
		inicioOrientador = null;
	}

	/**
	 * Inicia o cadastro de discentes novos.<br/><br/>
	 *	Chamado pelas JSP(s): <ul>
	 *  <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 * 	<li>/sigaa.war/stricto/menu_coordenador.jsp</li></ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteNovo() throws ArqException {
		checkRole(new int[] { SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO });
		initObj();
		blockAnoSemestre = true;
		setDiscenteAntigo(false);
		obj.setStatus(StatusDiscente.ATIVO);
		if (!discenteAntigo && !isUserInRole(SigaaPapeis.PPG)) {
			addMensagemWarning(UFRNUtils.getMensagem(MensagensStrictoSensu.NEGAR_CADASTRO_DISCENTE_REGULAR_FORA_OP_VESTIBULAR).getMensagem());
			getCurrentSession().setAttribute("mensagemStricto", Boolean.TRUE);
			getCurrentSession().removeAttribute("discenteProcessoSeletivo");
		}
		return popular();
	}

	/**
	 * Inicia o cadastro de discentes novos.<br/><br/>
	 *	Chamado pelas JSP(s): <ul>
	 *  <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 * 	<li>/sigaa.war/stricto/menu_coordenador.jsp</li></ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteProcessoSeletivo() throws ArqException {
		checkRole(new int[] { SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO });
		initObj();
		blockAnoSemestre = true;
		setDiscenteAntigo(false);
		obj.setStatus(StatusDiscente.ATIVO);
		return popular();
	}
	
	
	/**
	 * Inicia o cadastro de discentes antigos.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/stricto/menus/discente.jsp</li>
	 *	<li> /sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteAntigo() throws ArqException {
		checkRole(new int[] { SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO });
		
		if( isPortalCoordenadorStricto() && !ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.PERMITE_PROGRAMA_POS_CADASTRAR_DISCENTE_ANTIGO) ){
			addMensagemErro("O cadastro de discente antigo est� configurado para ser realizado apenas pela PPG.");
			return null;
		}
		
		initObj();
		setDiscenteAntigo(true);
		obj.setStatus(StatusDiscente.CONCLUIDO);
		blockAnoSemestre = false;
		return popular();
	}

	/**
	 * Chama o operador de dados pessoais para realizar a verifica��o do CPF
	 * e redireciona para o cadastro de discente de p�s-gradua��o.
	 * @return
	 * @throws ArqException
	 */
	private String popular() throws ArqException{
		checkRole(new int[] { SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO });

		setConfirmButton("Cadastrar");
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);

		if (!getAcessoMenu().isPpg() && getProgramaStricto() != null) {
			obj.setGestoraAcademica(getProgramaStricto());
			carregarCursos(null);
		}

		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.DISCENTE_STRICTO );
		return dadosPessoaisMBean.popular();
	}

	/**
	 * JSP: N�o invocado por jsp.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAndValidate()
	 * 
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {

		if( obj.getPessoa().getTipoRaca().getId() == 0 )
			obj.getPessoa().setTipoRaca(null);

		super.beforeCadastrarAndValidate();
	}
	
	/**
	 * Popula o orientador
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/stricto/menus/discente.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarOrientador(ActionEvent e) throws DAOException {

		EquipePrograma orientadorTemp = getGenericDAO().refresh(orientador);
		orientador = new EquipePrograma();
		orientador.setId(orientadorTemp.getId());
		orientador.setServidor(orientadorTemp.getServidor());
		orientador.setDocenteExterno(orientadorTemp.getDocenteExterno());

	}
	
	/**
	 * Popula o coOrientador
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/stricto/menus/discente.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarCoOrientador(ActionEvent e2) throws DAOException {
		
		EquipePrograma coOrientadorTemp = getGenericDAO().refresh(coOrientador);
		coOrientador = new EquipePrograma();
		coOrientador.setId(coOrientadorTemp.getId());
		coOrientador.setServidor(coOrientadorTemp.getServidor());
		coOrientador.setDocenteExterno(coOrientadorTemp.getDocenteExterno());
		
	}

	/**
	 * Cadastra o discente stricto.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *<br/> 
	 *<ul>
	 *	<li>/sigaa.war/stricto/menus/discente.jsp</li>
	 *	<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *</ul> 
	 * @throws NegocioException 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		if( isProcessoSeletivoObrigatorio() && !discenteAntigo && getCurrentSession().getAttribute("discenteProcessoSeletivo") == null){
			addMensagemErro(UFRNUtils.getMensagem(MensagensStrictoSensu.NEGAR_CADASTRO_DISCENTE_REGULAR_FORA_OP_VESTIBULAR).getMensagem());
		}
		populaOrientacao();

		/**
		 *  Informando o tipo do discente, caso este pertence a uma programa em rede de Ensino e perten�a a outra institui��o de ensino.
		 */
		if(alunoOutraInstituicao && !isEmpty( obj.getInstituicaoEnsinoRede() ) ){
			obj.setTipo(Discente.EM_ASSOCIACAO);
		}
		
		if ( hasErrors() ) 
			return null;
		
		Comando comando = SigaaListaComando.CADASTRAR_DISCENTE;
		if( obj.getId() > 0 )
			comando = SigaaListaComando.ALTERAR_DISCENTE_STRICTO;
		
		obj.anularAtributosTransient();
		DiscenteMov mov = new DiscenteMov(comando, obj);
		mov.setDiscenteAntigo(isDiscenteAntigo());

		try {
			execute(mov, getCurrentRequest());
			if( comando.equals(SigaaListaComando.CADASTRAR_DISCENTE) ){
				addMessage( "Discente "+obj.getNome()+" cadastrado com sucesso, e associado com o " +
						"n�mero de  matr�cula " + obj.getMatricula(), TipoMensagemUFRN.INFORMATION);
			}else{
				addMessage( "O discente "+obj.toString()+" foi atualizado com sucesso.", TipoMensagemUFRN.INFORMATION);
			}
		} catch (NegocioException e) {
			obj.inicializarAtributosNulos();	
			if( getUltimoComando().equals(SigaaListaComando.CADASTRAR_DISCENTE) )
				obj.setId(0);
			addMensagens(e.getListaMensagens());
			return null;
		} 

		if (getCurrentSession().getAttribute("discenteProcessoSeletivo") != null) {
			resetBean();
			getCurrentSession().removeAttribute("discenteProcessoSeletivo");
			return redirect("/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsf");
		} else {
			return cancelar();
		}
		
	}

	/**
	 * M�todo que popula os dados da orienta��o e coOrienta��o. 
	 * Para o caso da orientador verifica se j� excedeu a quantidade m�xima de orientandos e bolsas.
	 * Para o caso coOrientador verifica se � diferente do orientador
	 * @throws DAOException
	 */
	private void populaOrientacao() throws DAOException {
		
		/**
		 * A altera��o dos dados da oroenta��o e coOrienta��o somente � permitido
		 * na opera��o de cadastro do discente 
		 */
		if( obj.getId() == 0  ){
	
			obj.setOrientacao( new OrientacaoAcademica() );
			obj.setCoOrientacao(  new OrientacaoAcademica() );
			
			//Popula e verifica os dados da orienta��o
			if(orientador != null && orientador.getId() != 0 ){
				
				//Popula os dados da orienta��o se o servidor ou docente externo foi selecionado
				if( (orientador.getServidor() != null && orientador.getServidor().getId() > 0 )
						|| ( orientador.getDocenteExterno() != null && orientador.getDocenteExterno().getId() > 0 ) ){
					obj.setOrientacao(new OrientacaoAcademica());
					obj.getOrientacao().setServidor( orientador.getServidor() );
					obj.getOrientacao().setDocenteExterno( orientador.getDocenteExterno() );
					obj.getOrientacao().setInicio(inicioOrientador);
					obj.getOrientacao().ajustar();
				}
				
			}
			
			//Popula e verifica os dados da coOrienta��o
			if( coOrientador != null && coOrientador.getId() != 0 ){
			
				if( (coOrientador.getServidor() != null && coOrientador.getServidor().getId() > 0 )
						|| ( coOrientador.getDocenteExterno() != null && coOrientador.getDocenteExterno().getId() > 0 ) ){
					obj.setCoOrientacao(new OrientacaoAcademica());
					obj.getCoOrientacao().setServidor( coOrientador.getServidor() );
					obj.getCoOrientacao().setDocenteExterno( coOrientador.getDocenteExterno() );
					obj.getCoOrientacao().setInicio(inicioCoOrientador);
					obj.getCoOrientacao().ajustar();
				}
				
			}	
			
		}	
		
	}

	/**
	 * Carrega os poss�veis curr�culos do aluno de acordo com o curso selecionado.
	 * @param e
	 * @throws DAOException
	 */
	private void carregarCurriculos(ValueChangeEvent e) throws DAOException  {
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		Integer id = null;
		if( e != null )
			id = (Integer) e.getNewValue();
		else if( !isEmpty( obj.getCurso() ) ){
			id = obj.getCurso().getId();
		}
		if( id == null )
			return;
		
		if (isDiscenteAntigo())
			possiveisCurriculos = toSelectItems(dao.findByCurso(id, NivelEnsino.STRICTO), "id", "descricao");
		else
			possiveisCurriculos = toSelectItems(dao.findByCurso(id, NivelEnsino.STRICTO, true), "id", "descricao");
	}


	/**
	 *  Carrega os poss�veis cursos do aluno de acordo com a unidade do programa stricto senso.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/stricto/curriculo/lista.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public void carregarCursos(ValueChangeEvent e) throws DAOException, SegurancaException {
		EquipeProgramaMBean mbean = getMBean("equipePrograma");
		mbean.getObj().getPrograma().setId(obj.getGestoraAcademica().getId());		
		CursoDao dao = getDAO(CursoDao.class);
		LinhaPesquisaStrictoDao daoLinha = getDAO(LinhaPesquisaStrictoDao.class);
		possiveisLinhas = new ArrayList<SelectItem>();
		int unidade = 0;
		if ((e == null && getProgramaStricto() != null) && !getAcessoMenu().isPpg()) {
			unidade = getProgramaStricto().getId();
		} else if( e != null ) {
			unidade = (Integer) e.getNewValue();
		} else if( !isEmpty( obj.getGestoraAcademica() ) ){
			unidade = obj.getGestoraAcademica().getId();
		}
		if (unidade > 0)  {
			possiveisCursos = toSelectItems(dao.findByUnidade(unidade, !isEmpty(getNivelEnsino()) ? getNivelEnsino(): ' '), "id", "nomeTipoCursoStricto");
			//possiveisAreas = toSelectItems(areaDao.findByProgramaNivel(unidade, ), "id", "denominacao");
			possiveisLinhas.addAll( toSelectItems(daoLinha.findByProgramaSemArea( new Unidade(unidade) ), "id", "denominacao") );
			EquipeProgramaMBean equipeMBean = (EquipeProgramaMBean) getMBean( "equipePrograma" );
			EquipePrograma equipe = new EquipePrograma();
			equipe.setPrograma( new Unidade(unidade));
			equipeMBean.setObj(equipe);
		}else{
			checkRole(SigaaPapeis.PPG);
			possiveisCursos = toSelectItems(dao.findByNivel(NivelEnsino.STRICTO, true), "id", "nomeTipoCursoStricto");
			if( !isEmpty(obj.getGestoraAcademica()) ){
				possiveisAreas = toSelectItems(dao.findByExactField(AreaConcentracao.class, "programa.id", obj.getGestoraAcademica().getId()), "id", "denominacao");
				possiveisLinhas.addAll( toSelectItems(daoLinha.findByProgramaSemArea(obj.getGestoraAcademica()), "id", "denominacao") );
			}if( !isEmpty(obj.getArea()) ){
				possiveisLinhas = toSelectItems(dao.findByExactField(LinhaPesquisaStricto.class, "area.id", obj.getArea().getId()), "id", "denominacao");
			}
		}
	}

	/** Carrega as poss�veis �reas de concentra��o e linhas de pesquisa do discente.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarAreasLinhas(ValueChangeEvent e) throws DAOException {
		AreaConcentracaoDao dao = getDAO( AreaConcentracaoDao.class );
		LinhaPesquisaStrictoDao daoLinha = getDAO(LinhaPesquisaStrictoDao.class);
		Curso curso = null;
		if( e != null )
			curso = dao.findByPrimaryKey((Integer)e.getNewValue(), Curso.class);
		else if( !isEmpty( obj.getCurso() ) )
			curso = dao.refresh(obj.getCurso());
		else
			return;
		
		obj.inicializarAtributosNulos();
		obj.setNivel(curso.getNivel());
		if (curso != null){
			possiveisAreas = toSelectItems(dao.findByProgramaNivel(obj.getGestoraAcademica().getId(), obj.getNivel() ), "id", "denominacao");
			if( obj.getArea() != null )
				possiveisLinhas = toSelectItems(dao.findByExactField(LinhaPesquisaStricto.class, "area.id", obj.getArea().getId() ), "id", "denominacao");
			if( !isEmpty(obj.getGestoraAcademica()) )
				possiveisLinhas.addAll( toSelectItems(daoLinha.findByProgramaSemArea(obj.getGestoraAcademica()), "id", "denominacao") );
		}

		if( e != null )
			carregarCurriculos(e);
		else
			carregarCurriculos(null);
	}

	/** 
	 * Listener respons�vel por carregar as poss�veis linhas de pesquisa do discente quando o valor da �rea de concentra��o � alterado.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/stricto/discente/form.jsp</li>
	 * </ul> 
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarLinhas(ValueChangeEvent e) throws DAOException {
		LinhaPesquisaStrictoDao dao = getDAO(LinhaPesquisaStrictoDao.class);
		possiveisLinhas = toSelectItems(dao.findByExactField(LinhaPesquisaStricto.class, "area.id", e.getNewValue()), "id", "denominacao");
		possiveisLinhas.addAll( toSelectItems(dao.findByProgramaSemArea(obj.getGestoraAcademica()), "id", "denominacao") );
	}

	/**
	 * Define os dados pessoaia do discente.
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#setDadosPessoais(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * Valida os dados preenchidos no formul�rio de dados pessoais e redireciona para 
	 * o formul�rio dos dados do discente.
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#submeterDadosPessoais()
	 * Chamado por {@link DadosPessoaisMBean#submeterDadosPessoais()}
	 * 
	 * JSP: N�o invocado por jsp.
	 */
	public String submeterDadosPessoais() {

		try {
			/** se pessoa j� existir verifica se j� existe um discente stricto ativo associado a esta pessoa */
			if (obj.getPessoa().getId() != 0)
				DiscenteStrictoValidator.validarExisteDiscenteAtivoPessoa(obj, erros);

			if( obj.getGestoraAcademica() != null){
				carregarCursos(null);
			}
			
			if( obj.getCurso() != null ){
				carregarAreasLinhas(null);
			}	
			
			carregarProcessoSeletivo();
			
			if(obj.getInstituicaoEnsinoRede() != null && obj.getInstituicaoEnsinoRede().getId() > 0){
				alunoOutraInstituicao = true;
			}else{
				obj.setInstituicaoEnsinoRede(new InstituicoesEnsino());
			}
			
			if (hasErrors())
				return null;
			
		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			return null;
		}

		return telaDadosDiscente();
	}

	/**
	 * Carrega os processos seletivos da unidade do usu�rio logado.
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregarProcessoSeletivo() throws HibernateException, DAOException {
		ProcessoSeletivoDao daoPS = getDAO(ProcessoSeletivoDao.class);
		try {
			
			Integer idPrograma = null;
			
			if ( getProgramaStricto() != null )
				idPrograma = getProgramaStricto().getId();
			
			processosSeletivosCombo = toSelectItems( daoPS.findGeral(idPrograma, getNivelEnsino(), 
					null,  -1,
					StatusProcessoSeletivo.PUBLICADO), "id", "descricaoCompleta");
		
		} finally {
			daoPS.close();
		}
	}
	
	/** Redireciona para a busca de discentes, a fim de realizar a opera��o de atualiza��o de dados.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/stricto/menus/discente.jsp</li>
	 *	<li> /sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul> 
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG);
		prepareMovimento( SigaaListaComando.ALTERAR_DISCENTE_STRICTO );
		initObj();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		
		if (isUserInRole(SigaaPapeis.PPG))
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_POS_PPG);
		else
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_POS_COORDENACAO);
		
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Seleciona o discente e redireciona para o formul�rio de altera��o de discente.
	 * Chamado por {@link BuscaDiscenteMBean#redirecionarDiscente(Discente)}
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 * 
 	 *  M�todo n�o invocado por JSP.
	 */
	public String selecionaDiscente() throws ArqException {
		if( isUserInRole( SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO ) && !isUserInRole(SigaaPapeis.PPG) && obj.getGestoraAcademica().getId() != getProgramaStricto().getId() ){
			addMensagem(MensagensPortalCoordenadorStricto.ALTERACAO_APENAS_DO_MESMO_PROGRAMA);
			return null;
		}
		
		/** carregando forma de ingresso */
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		
		try{			
			char tipo = NivelEnsino.RESIDENCIA;
			if(obj.getTipo() == Discente.ESPECIAL)
				tipo = NivelEnsino.MESTRADO;
			
			formasIngressoCombo = toSelectItems(dao.findByNivelTipo(getNivelEnsino(), tipo), "id", "descricao");
			
			carregarProcessoSeletivo();
			
			/** setando curso do aluno especial para popular o select de n�vel na tela */
			if( !obj.isRegular() && (obj.getNivel() == NivelEnsino.MESTRADO || obj.getNivel() == NivelEnsino.DOUTORADO ) ){
				CursoDao cursoDao = getDAO(CursoDao.class);
				obj.setCurso( cursoDao.findByUnidade(obj.getGestoraAcademica().getId(), obj.getNivel()).iterator().next() );
			}
			
			obj.setOrientacao(  DiscenteHelper.getOrientadorAtivo(obj.getDiscente()) );
			obj.setCoOrientacao( DiscenteHelper.getCoOrientadorAtivo(obj) );

			/** inicializando atributos nulos */
			obj.inicializarAtributosNulos();
			carregarCursos(null);
			carregarCurriculos(null);
			carregarAreasLinhas(null);
			
			
			obj.setOrigem((OrigemDiscentePos) dao.initializeClone(obj.getOrigem()) );
			obj.setLinha( (LinhaPesquisaStricto) dao.initializeClone(obj.getLinha()) );
			obj.setArea( (AreaConcentracao) dao.initializeClone(obj.getArea()) );
			
			if(obj.getInstituicaoEnsinoRede() != null && obj.getInstituicaoEnsinoRede().getId() > 0){
				alunoOutraInstituicao = true;
			}else{
				obj.setInstituicaoEnsinoRede(new InstituicoesEnsino());
			}
		
		} finally {
			if (dao != null)
				dao.close();
		}

		return telaDadosDiscente();
	}

	/**
	 * Este m�todo diz se � obrigat�rio informar o processo seletivo no cadastro de discente que est� sendo realizado
	 * � obrigat�rio informar caso atenda as seguintes condi��es:
	 *  - O discente deve ser regular
	 *  - O cadastro tem que estar sendo realizado pela coordena��o do programa. 
	 *  - O caso de uso em quest�o deve ser cadastrar discente
	 *  - O parametro OBRIGATORIO_INFORMAR_PROCESSO_SELETIVO_CADASTRO_DISCENTE deve estar configurado com o valor true
	 * @return
	 */
	public boolean isProcessoSeletivoObrigatorio(){
		boolean processoObrigatorio = ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.OBRIGATORIO_INFORMAR_PROCESSO_SELETIVO_CADASTRO_DISCENTE);
		return obj.isRegular() && obj.getId() == 0 &&  processoObrigatorio && !getAcessoMenu().isStricto() && !isUserInRole(SigaaPapeis.PPG);
		
	}
	
	/**
	 * Define os dados do discente.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 *  M�todo n�o invocado por JSP.
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj = (DiscenteStricto) discente;
	}

	/** Retorna o formul�rio de dados do discente.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 */
	private String telaDadosDiscente() {
		obj.getPessoa().anularAtributosVazios();
		obj.inicializarAtributosNulos();
		return forward( "/stricto/discente/form.jsp");
	}
	
	/** Retorna o formul�rio de dados do discente.
	 * @return /sigaa.war/stricto/discente/form.jsp
	 */
	public String telaDadosPessoais() {
		obj.getPessoa().prepararDados();
		return forward( "/geral/pessoa/dados_pessoais.jsp");
	}

	/** Indica se deve bloquear a edi��o do campo ano/semestre no formul�rio.  
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isBlockAnoSemestre() {
		return blockAnoSemestre;
	}

	/** Seta se deve bloquear a edi��o do campo ano/semestre no formul�rio. 
 	 *  M�todo n�o invocado por JSP.
	 * @param blockAnoSemestre
	 */
	public void setBlockAnoSemestre(boolean blockAnoSemestre) {
		this.blockAnoSemestre = blockAnoSemestre;
	}

	/** 
	 * Retorna a cole��o de SelectItem de um curso para o discente. 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getPossiveisCursos() {
		return possiveisCursos;
	}

	/** Retorna a lista dos poss�veis curr�culos do discente. 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getPossiveisCurriculos() {
		return possiveisCurriculos;
	}

	
	/** Seta a cole��o de SelectItem de um curso para o discente.
 	 *  M�todo n�o invocado por JSP.
	 * @param possiveisCursos
	 */
	public void setPossiveisCursos(List<SelectItem> possiveisCursos) {
		this.possiveisCursos = possiveisCursos;
	}

	/** Retorna a lista das poss�veis �reas de pesquisa do discente. 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getPossiveisAreas() {
		return possiveisAreas;
	}

	/** Retorna a lista das poss�veis �reas de pesquisa do discente. 
 	 *  M�todo n�o invocado por JSP.
	 * @param possiveisAreas
	 */
	public void setPossiveisAreas(List<SelectItem> possiveisAreas) {
		this.possiveisAreas = possiveisAreas;
	}

	/** Retorna a lista das poss�veis linhas de pesquisa do discente. 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getPossiveisLinhas() {
		return possiveisLinhas;
	}

	/** Seta a lista das poss�veis linhas de pesquisa do discente. 
 	 *  M�todo n�o invocado por JSP.
	 * @param possiveisLinhas
	 */
	public void setPossiveisLinhas(List<SelectItem> possiveisLinhas) {
		this.possiveisLinhas = possiveisLinhas;
	}

	/**
	 * Carrega as formas de entrada relativas ao tipo de discente informado.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void filtraFormaEntrada(ValueChangeEvent evt) throws DAOException {
		obj.setTipo((Integer) evt.getNewValue());
		obj.setFormaIngresso(new FormaIngresso());
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		char tipo = 'R';
		if(obj.getTipo() == 2)
			tipo = 'E';
		formasIngressoCombo = toSelectItems(dao.findAllFormasEntradaHabilitadasByNivelTipo(getNivelEnsino(), tipo), "id", "descricao");
	}

	/**
	 * Carrega as formas de entrada relativas ao tipo de discente informado.
	 * <br/>
	 * M�todo N�o � invocado chamado por JSP(s):
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void filtraFormaEntrada() throws DAOException {
		
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		char tipo = 'R';
		if(obj.getTipo() != null && obj.getTipo() == 2)
			tipo = 'E';
		formasIngressoCombo = toSelectItems(dao.findAllFormasEntradaHabilitadasByNivelTipo(getNivelEnsino(), tipo), "id", "descricao");
	}
	
	/** Retorna a cole��o de SelectItem das formas de ingresso poss�veis para o discente. 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getFormasIngressoCombo() {
		return formasIngressoCombo;
	}

	/** Seta a cole��o de SelectItem das formas de ingresso poss�veis para o discente.  
	 * @param formasIngressoCombo
	 */
	public void setFormasIngressoCombo(Collection<SelectItem> formasIngressoCombo) {
		this.formasIngressoCombo = formasIngressoCombo;
	}

	/** Retorna o orientador do discente. 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public EquipePrograma getOrientador() {
		return orientador;
	}

	/** Seta o orientador do discente. 
 	 *  M�todo n�o invocado por JSP.
	 * @param orientador
	 */
	public void setOrientador(EquipePrograma orientador) {
		this.orientador = orientador;
	}

	/** Retorna o Co-orientador do discente.  
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public EquipePrograma getCoOrientador() {
		return coOrientador;
	}

	/** Seta o Co-orientador do discente.
 	 *  M�todo n�o invocado por JSP.
	 * @param coOrientador
	 */
	public void setCoOrientador(EquipePrograma coOrientador) {
		this.coOrientador = coOrientador;
	}

	/** Indica se � discente antigo. 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isDiscenteAntigo() {
		return discenteAntigo;
	}

	/** Seta se � discente antigo. 
 	 *  M�todo n�o invocado por JSP.
	 * @param discenteAntigo
	 */
	public void setDiscenteAntigo(boolean discenteAntigo) {
		this.discenteAntigo = discenteAntigo;
	}

	/** Retorna a lista dos poss�veis status discente. 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<SelectItem> getPossiveisStatus() {
		return possiveisStatus;
	}

	/** Seta a lista dos poss�veis status discente. 
 	 *  M�todo n�o invocado por JSP.
	 * @param possiveisStatus
	 */
	public void setPossiveisStatus(List<SelectItem> possiveisStatus) {
		this.possiveisStatus = possiveisStatus;
	}

	/** Retorna a data de in�cio da orienta��o. 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Date getInicioOrientador() {
		return inicioOrientador;
	}

	/** Seta a data de in�cio da orienta��o.
 	 *  M�todo n�o invocado por JSP.
	 * @param inicioOrientador
	 */
	public void setInicioOrientador(Date inicioOrientador) {
		this.inicioOrientador = inicioOrientador;
	}

	/** Retorna a data de in�cio da co-orienta��o. 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Date getInicioCoOrientador() {
		return inicioCoOrientador;
	}

	/** Seta a data de in�cio da co-orienta��o.
 	 *  M�todo n�o invocado por JSP.
	 * @param inicioCoOrientador
	 */
	public void setInicioCoOrientador(Date inicioCoOrientador) {
		this.inicioCoOrientador = inicioCoOrientador;
	}

	/**
	 * Popula todos os processos seletivos dispon�vel para o curso.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getProcessosSeletivosCombo() {
		return processosSeletivosCombo; 
	}

	/**
	 * Seta os processos seletivos para serem exibidos no formul�rio.
	 * @param processosSeletivosCombo
	 *  <br/> M�todo n�o invocado por JSP.
	 */
	public void setProcessosSeletivosCombo(List<SelectItem> processosSeletivosCombo) {
		this.processosSeletivosCombo = processosSeletivosCombo;
	}

	/**
 	 *  M�todo n�o invocado por JSP.
	 * @param possiveisCurriculos
	 */
	public void setPossiveisCurriculos(List<SelectItem> possiveisCurriculos) {
		this.possiveisCurriculos = possiveisCurriculos;
	}

	/**
	 * Retorna o m�s por extenso referente a entrada do discente no programa.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String getMesEntradaFormatado() {
		if (obj.getMesEntrada() == null)
			return " - ";
		return CalendarUtils.getNomeMes(obj.getMesEntrada());
	}

	/**
	 * Possui a quantidade atualizada de orientados do docente para valida��o no cadastro de discente.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getTotalOrientacoes() {
		return totalOrientacoes;
	}

	/**
	 * Seta a quantidade atualizada de orientados do docente para valida��o no
	 * cadastro de discente
	 * 
	 * @param totalOrientacoes
	 * <br/>
	 *            M�todo n�o invocado por JSP.
	 */
	public void setTotalOrientacoes(int totalOrientacoes) {
		this.totalOrientacoes = totalOrientacoes;
	}

	/**
	 * Indica se o discente � proveniente de outra institui��o.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/stricto/discente/form.jsp</li>
	 * </ul>
	 * 
	 * @return the alunoOutraInstituicao
	 */
	public boolean isAlunoOutraInstituicao() {
		return alunoOutraInstituicao;
	}

	/**
	 * Seta se o aluno participa do programa em rede sendo aluno de uma
	 * institui��o de ensino diferente da UFRN.
	 * 
	 * @param alunoOutraInstituicao
	 *            the alunoOutraInstituicao to set <br/>
	 *            M�todo n�o invocado por JSP.
	 */
	public void setAlunoOutraInstituicao(boolean alunoOutraInstituicao) {
		this.alunoOutraInstituicao = alunoOutraInstituicao;
	}
	
}