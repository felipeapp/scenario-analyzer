/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/10/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.nee.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.myfaces.custom.navmenu.jscookmenu.HtmlCommandJSCookMenu;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.CargoAcademico;
import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrm.sigaa.nee.dao.NeeDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.jsf.HistoricoMBean;
import br.ufrn.sigaa.mensagens.MensagensNee;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.nee.dominio.StatusAtendimento;
import br.ufrn.sigaa.nee.dominio.TipoNecessidadeSolicitacaoNee;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.PessoaNecessidadeEspecial;

/**
 * MBean responsável por manter as operações com os discente com NEE (Necessidades Educacionais Especiais).
 * @author Rafael Gomes
 *
 */
@Component("solicitacaoApoioNee") 
@Scope("session")
public class SolicitacaoApoioNeeMBean extends SigaaAbstractController<SolicitacaoApoioNee> implements OperadorDiscente{

	/** Lista utilizada para armazenar as necessidades especiais selecionadas 
	 * nos checkBox do formulário de solicitação de apoio de NEE. */
	List<Object> tiposNecessidadesEspeciaisSelecionadas;
	
	/** Discente atendido pela solicitação de apoio NEE. */
	DiscenteAdapter discente;
	
	/** Comando que será executado. */
	private Comando comando;
	
	/** Caminho da Jsp responsável pela lista de solicitações de NEE pendentes de parecer. */
	public static final String JSP_PENDENTES_PARECER = "/nee/solicitacaoApoioNee/lista_pendentes_parecer.jsp";
	/** Caminho da Jsp que contém as solicitações dos alunos com NEE. */
	public static final String JSP_SOLICITACOES_ALUNOS = "/nee/solicitacaoApoioNee/lista_solicitacoes.jsp";
	
	/** Atributo responsável pela habilitação do atendimento da solicitação. */
	boolean situacaoAtendimento = false;
		
	/** Constructor */
	public SolicitacaoApoioNeeMBean() {
		init();
	}
	
	/** Método de inicialização do objeto no MBean. */
	private void init() {
		obj = new SolicitacaoApoioNee();
		obj.setDiscente(this.discente);
		if ( obj.getParecerAtivo() != null )
			situacaoAtendimento = !obj.getParecerAtivo();
		else
			situacaoAtendimento = false;
	}
	
	@Override
	public String getFormPage() {
		return "/nee/solicitacaoApoioNee/form_nee.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/nee/solicitacaoApoioNee/lista.jsp";
	}
	
	@Override
	public String getViewPage() {
		return "/nee/solicitacaoApoioNee/view.jsp";
	}
	
	/** Método responsável pelo redirect da função voltar da tela de  solicitação. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista.jsp</li>
	 * </ul> 
	 * */
	public String voltar() {
		return forward("/graduacao/busca_discente.jsp");
	}

	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String preCadastrarDiscenteNee() throws SegurancaException {
		init();
		setConfirmButton("Cadastrar");
		comando = SigaaListaComando.CADASTRAR_SOLICITACAO_NEE;
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		if(isInfantil())
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.DISCENTE_INFANTIL_NEE);
		else 
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.DISCENTE_NEE);
			
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação, 
	 * em seguida redireciona o usuário para a lista de solicitações do discente.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>  
	 * @return
	 */
	public String listarSolicitacoesDiscente() throws SegurancaException {
		init(); 
		setConfirmButton("Alterar");
		comando = SigaaListaComando.ALTERAR_SOLICITACAO_NEE;
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.LISTAR_SOLICITACOES_DISCENTE_NEE);
			
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação, 
	 * em seguida redireciona o usuário para a lista de solicitações do discente, 
	 * que se deseja cadastrar o parecer.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/menu.jsp</li>
	 * </ul>   
	 * @return
	 */
	public String preCadastrarParecerSolicitacao() throws SegurancaException {
		init();
		setConfirmButton("Cadastrar");
		comando = SigaaListaComando.CADASTRAR_PARECER_SOLICITACAO_NEE;
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.LISTAR_SOLICITACOES_DISCENTE_NEE);
			
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de graduação, 
	 * em seguida redireciona o usuário para a lista de solicitações do discente,
	 * para ser realizada a alteração do parecer a respeito do discente.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/menu.jsp</li>
	 * </ul>   
	 * @return
	 */
	public String alterarParecerSolicitacao() throws SegurancaException {
		init();
		setConfirmButton("Alterar");
		comando = SigaaListaComando.ALTERAR_PARECER_SOLICITACAO_NEE;
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.LISTAR_SOLICITACOES_DISCENTE_NEE);
			
		return buscaDiscenteMBean.popular();
	}
	
	@Override
	public String cancelar() {
		return super.cancelar();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		prepareMovimento(comando);
		validarAtributosRequeridos();
		if (hasErrors()) return null;
		if ( comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_NEE) )
			obj.setStatusAtendimento(new StatusAtendimento(StatusAtendimento.SUBMETIDO));
				
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjAuxiliar(tiposNecessidadesEspeciaisSelecionadas);
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(comando);
		
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
			if ( mov.getMensagens().isInfoPresent() )
				addMessage(mov.getMensagens().toString(), TipoMensagemUFRN.INFORMATION);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		if ( comando.equals(SigaaListaComando.CADASTRAR_PARECER_SOLICITACAO_NEE ) )
			return forward(JSP_PENDENTES_PARECER);
		else
			return forward(getListPage());
	}
	
	/**
	 * Método utilizado para realizar as validações dos campos requeridos conforme o comando de processamento.
	 */
	private void validarAtributosRequeridos(){
		if ( comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_NEE) || comando.equals(SigaaListaComando.ALTERAR_SOLICITACAO_NEE) ){
			ValidatorUtil.validateRequired(obj.getJustificativaSolicitacao(), "Justificativa para Solicitação de Apoio a CAENE", erros);
			ValidatorUtil.validateRequired(tiposNecessidadesEspeciaisSelecionadas, "Tipo de Necessidade Educacional Especial", erros);
		}
		
		if ( comando.equals(SigaaListaComando.CADASTRAR_PARECER_SOLICITACAO_NEE) || comando.equals(SigaaListaComando.ALTERAR_PARECER_SOLICITACAO_NEE) ){
			ValidatorUtil.validateRequired(tiposNecessidadesEspeciaisSelecionadas, "Tipo de Necessidade Educacional Especial", erros);
			ValidatorUtil.validateRequired(obj.getParecerAtivo(), "Parecer", erros);
			ValidatorUtil.validateRequired(obj.getStatusAtendimento(), "Situação do Atendimento", erros);
			ValidatorUtil.validateRequired(obj.getParecerComissao(), "Parecer Técnico da CAENE", erros);
		}
	}
	
	/**
	 * Método utilizado para iniciar a funcionalidade de alteração e preenchimento 
	 * dos dados e parecer a respeito da solicitação de apoio ao NEE.  
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista_pendentes_parecer.jsp</li>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista.jsp</li>
	 * </ul>  
	 * */
	public String atualizar() throws ArqException{
		GenericDAO dao = getGenericDAO();
		setId();
		setReadOnly(false);
		if ( isSolicitacaoCoordenador() ){
			comando = SigaaListaComando.ALTERAR_SOLICITACAO_NEE;
			setConfirmButton("Alterar");
		}	
		this.obj = dao.findByPrimaryKey(obj.getId(), SolicitacaoApoioNee.class);
		this.obj.setDiscente(getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()));
		carregarTipoNecessidades();
		manterSolicitacaoLida(this.obj);
		
		return forward(getFormPage());
	}
	
	/**
	 * Método utilizado para atualizar a situação da solicitação com respeito a sua 
	 * leitura ou não pela comissão da responsável.
	 * @param solicitacaoNee
	 * @throws ArqException
	 */
	private void manterSolicitacaoLida(SolicitacaoApoioNee solicitacaoNee) throws ArqException{
		if ( comando.equals(SigaaListaComando.CADASTRAR_PARECER_SOLICITACAO_NEE) 
			|| comando.equals(SigaaListaComando.ALTERAR_PARECER_SOLICITACAO_NEE) ){
			NeeDao dao = getDAO(NeeDao.class);
			this.obj.setLida( true );
			dao.atualizarSolicitacaoLida(solicitacaoNee);
		}
	}
	
	/**
	 * Método utilizada para chamar a funcionalidade de visualização 
	 * dos dados cadastrados da solicitação de apoio de NEE.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista_solicitacoes.jsp</li>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String visualizar() {
		try {
			GenericDAO dao = getGenericDAO();
			setId();
			this.obj = dao.findByPrimaryKey(obj.getId(), SolicitacaoApoioNee.class);
			this.obj.setDiscente(getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()));
			return forward(getViewPage());
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Método utilizada para chamar a funcionalidade com a lista de solicitações com status de Submetido e Em Análise 
	 * ou que não tiveram o seu parecer preenchido pela comissão responsável pelos discente com NEE.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/menu.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String listaSolicitacoesPendentesParecer() {
		setConfirmButton("Cadastrar");
		comando = SigaaListaComando.CADASTRAR_PARECER_SOLICITACAO_NEE;
		return forward(JSP_PENDENTES_PARECER);
		
	}
	
	/**
	 * Método utilizado para chamar a funcionalidade com a lista de todas as solicitações de apoio ativas no sistema.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/menu.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String listaAllSolicitacoes() {
		setConfirmButton("Alterar");
		comando = SigaaListaComando.ALTERAR_PARECER_SOLICITACAO_NEE;
		return forward(JSP_SOLICITACOES_ALUNOS);
		
	}
	
	/** 
	 * Retorna a lista de alunos com solicitação de apoio pendentes de parecer sobre a NEE, ordenado por curso e nome.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista_pendentes_parecer.jsp</li>
	 * </ul> 
	 * @throws DAOException
	 */
	public Collection <SolicitacaoApoioNee> getSolicitacaoApoioPendentesParecer() throws DAOException {
		NeeDao dao = getDAO(NeeDao.class);
		return dao.findAllPendentesParecer();
	}
	
	/** 
	 * Retorna a lista de alunos com solicitação de apoio a NEE, ordenado por curso e nome.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista_solicitacoes.jsp</li>
	 * </ul> 
	 * @throws DAOException
	 */
	public Collection <SolicitacaoApoioNee> getAllSolicitacoesNee() throws DAOException {
		NeeDao dao = getDAO(NeeDao.class);
		return dao.findAllSolicitacaoNee( isParecerNee(), getCursoAtualCoordenacao(), getProgramaStricto());
	}
	
	@Override
	public String selecionaDiscente() throws ArqException {
		init();
		carregarTipoNecessidades();
		if ( comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_NEE)){
			return forward(getFormPage());
		} else{
			return forward(getListPage());
		}
	}
	
	/**
	 * Método responsável pelo cancelamento do cadastro ou alteração,
	 * redirecionando o usuário para a listagem de discentes.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/form_nee.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String cancelarCadastro(){
		if ( comando == SigaaListaComando.CADASTRAR_SOLICITACAO_NEE )
			return forward("/graduacao/busca_discente.jsp");
		else if ( comando == SigaaListaComando.CADASTRAR_PARECER_SOLICITACAO_NEE ) 
			return forward(JSP_PENDENTES_PARECER);
		else
			return forward(getListPage());
	}
	
	/**
	 * Exibe o histórico do discente selecionado
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista_solicitacoes.jsp</li>
	 * </ul> 
	 * @return
	 * @throws Exception
	 */
	public String historico() throws Exception {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		setId();
		obj = dao.findAndFetch(obj.getId(), SolicitacaoApoioNee.class,"discente");
		obj.setDiscente(dao.findByPK(obj.getDiscente().getId()));
		HistoricoMBean historico = (HistoricoMBean) getMBean("historico");
		historico.setDiscente(obj.getDiscente());
		return historico.selecionaDiscente();
	}
	
	/**
	 * Exibe o atestado de matrícula do discente selecionado
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista_solicitacoes.jsp</li>
	 * </ul> 
	 * @return
	 * @throws Exception
	 */
	public String atestadoMatricula() throws Exception {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		setId();
		obj = dao.findAndFetch(obj.getId(), SolicitacaoApoioNee.class,"discente");
		obj.setDiscente(dao.findByPK(obj.getDiscente().getId()));
		AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
		getCurrentSession().setAttribute("atestadoLiberado", obj.getId());
		atestado.setDiscente(obj.getDiscente());
		return atestado.selecionaDiscente();
	}
	
	/**
	 * Método utilizado para popular a lista contendo as necessidades especiais do discente.
	 * @throws DAOException
	 */
	private void carregarTipoNecessidades() throws DAOException{
		NeeDao dao = getDAO(NeeDao.class);
		tiposNecessidadesEspeciaisSelecionadas = new ArrayList<Object>();

		if ( obj.getDiscente().getPessoa().getTipoNecessidadeEspecial() != null ){
			tiposNecessidadesEspeciaisSelecionadas.add(
				new Integer( obj.getDiscente().getPessoa().getTipoNecessidadeEspecial().getId()).toString());
		}
		
		/* Inserindo as necessidades especiais do alunos relacionadas com a Pessoa do mesmo através da entidade PessoaNecessidadeEspecial. */
		if ( obj.getDiscente().getPessoa().getId() > 0  ){
			for (PessoaNecessidadeEspecial pne : dao.findByExactField(PessoaNecessidadeEspecial.class, "pessoa.id", obj.getDiscente().getPessoa().getId())) {
				if ( !tiposNecessidadesEspeciaisSelecionadas.contains(new Integer(pne.getTipoNecessidadeEspecial().getId()).toString()) ){
					tiposNecessidadesEspeciaisSelecionadas.add(
						new Integer(pne.getTipoNecessidadeEspecial().getId()).toString() );
				}
			}
		}
		
		/* Inserindo as necessidades especiais do alunos relacionadas com a Solicitação de apoio do mesmo através da entidade TipoNecessidadeSolicitacaoNee. */
		if ( obj.getId() > 0  ){
			for (TipoNecessidadeSolicitacaoNee tipo : dao.findNecessidadesEspeciaisBySolicitacaoNee(obj.getId())) {
				if ( !tiposNecessidadesEspeciaisSelecionadas.contains(new Integer(tipo.getTipoNecessidadeEspecial().getId()).toString()) ){
					tiposNecessidadesEspeciaisSelecionadas.add(
						new Integer(tipo.getTipoNecessidadeEspecial().getId()).toString() );
				}
			}
		}
	}
	
	/** 
	 * Método responsável para exibir mensagens informativas sobre a conclusão do cadastro da solicitação. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/form.jsp</li>
	 * </ul> 
	 */
	public String getInfomativoCoordenador(){
		if ( comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_NEE)){
			MensagemAviso msg = UFRNUtils.getMensagem(MensagensNee.INFORMATIVO_APOS_CADASTRO_SOLICITACAO );
			return msg.getMensagem();
		}
		else
			return null;
	}
	
	/**
	 * Collection preenchida com as solicitações de apoio de um determinado discente.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoApoioNee> getSolicitacoesApoioDiscente() throws DAOException{
		return getDAO(NeeDao.class).findSolicitacaoApoioNeeByDiscente(obj.getDiscente().getId());
	}
	
	/**
	 * Collection preenchida com as necessidades especiais vinculada a solicitações de apoio do objeto.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/view.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoNecessidadeSolicitacaoNee> getNecessidadesEspeciaisDiscente() throws DAOException{
		return getDAO(NeeDao.class).findNecessidadesEspeciaisBySolicitacaoNee(obj.getId());
	}

	/**
	 * Método lógico responsável por verificar se é um caso de operações oriundas da coordenação de curso.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista_pendentes_parecer.jsp</li>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista_solicitacoes.jsp</li>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/lista.jsp</li>
	 * </ul> 
	 * @return
	 */
	public boolean isSolicitacaoCoordenador(){
		return ( comando.equals(SigaaListaComando.CADASTRAR_SOLICITACAO_NEE)
				|| comando.equals(SigaaListaComando.ALTERAR_SOLICITACAO_NEE) );
	}
	
	/**
	 * Método responsável por verificar se é uma operação oriundo de um usuário da 
	 * comissão de apoio a alunos com necessidades especiais. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/form.jsp</li>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/view.jsp</li>
	 * </ul> 
	 * @return
	 */
	public boolean isParecerNee(){
		return (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_NEE) && getSubSistema().equals(SigaaSubsistemas.NEE))
				|| ( isModuloNee() &&  (this.obj.getStatusAtendimento() != null && this.obj.getStatusAtendimento().getId() == StatusAtendimento.EM_ATENDIMENTO));
	}
	
	/**
	 * Informa se algum campo da jsp será exibido para usuário da comissão de apoio a alunos com necessidades especiais. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getParecerNeeClass() {
		return isParecerNee() ? "on" : "off";
	}
	
	/**
	 * Retornar se a operação atual está sendo realizada no subsistema do módulo do NEE.
	 * @return
	 */
	public boolean isModuloNee(){
		return getSubSistema().equals(SigaaSubsistemas.NEE);
	}
	
	/**
	 * Retornar o atual coordenador de curso do aluno. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/view.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getNomeCoordenadorDiscente() throws DAOException{
		CoordenacaoCurso coordCurso = getDAO(CoordenacaoCursoDao.class).findUltimaByCursoCargo(obj.getDiscente().getCurso(), CargoAcademico.COORDENACAO); 
		return coordCurso != null ? coordCurso.getServidor().getNome() : "Não Informado";
	}
	
	/**
	 * Retornar a atual orientação acadêmica do aluno. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/view.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getNomeOrientador() throws DAOException{
		OrientacaoAcademica o = getDAO(OrientacaoAcademicaDao.class)
				.findOrientadorAcademicoAtivoByDiscente(obj.getDiscente().getId());
		
		return ValidatorUtil.isEmpty(o)?"Não Informado":o.getNomeOrientador();
	}
	
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = discente;
		obj.setDiscente(this.discente);
	}
	
	public List<Object> getTiposNecessidadesEspeciaisSelecionadas() {
		return tiposNecessidadesEspeciaisSelecionadas;
	}

	public void setTiposNecessidadesEspeciaisSelecionadas(
			List<Object> tiposNecessidadesEspeciaisSelecionadas) {
		this.tiposNecessidadesEspeciaisSelecionadas = tiposNecessidadesEspeciaisSelecionadas;
	}

	/** Retornar o email utilizado pela CAENE, para contato com docentes. */
	public String getEmailCaene() {
		return ParametroHelper.getInstance().getParametro(ParametrosGerais.EMAIL_CAENE);
	}
	
	/**
	 * Método utilizado para gerenciar o tipo de parecer selecionado.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/form.jsp</li>
	 * </ul> 
	 * @param evt
	 */
	public void selecionarParecer(ValueChangeEvent evt) {
		situacaoAtendimento = false;
		if (evt.getNewValue() != null && evt.getNewValue().toString().equals("false")) {
			situacaoAtendimento = true;
		}
	}
	
	/**
	 * Redirecionar no menu de acordo com o valor passado no menu
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/nee/solicitacaoApoioNee/form.jsp</li>
	 * </ul> 
	 * @param evt
	 */
	public void redirecionar(ActionEvent evt) {
		HtmlCommandJSCookMenu itemMenu = (HtmlCommandJSCookMenu) evt.getSource();
		redirect((String)itemMenu.getValue());
	}

	/**
	 * Retornar se a operação atual está sendo realizada no subsistema do módulo de Ensino Infantil.
	 * @return
	 */
	public boolean isInfantil(){
		return getNivelEnsino() == NivelEnsino.INFANTIL;
	}
	
	public boolean isSituacaoAtendimento() {
		return situacaoAtendimento;
	}

	public void setSituacaoAtendimento(boolean situacaoAtendimento) {
		this.situacaoAtendimento = situacaoAtendimento;
	}
	
}