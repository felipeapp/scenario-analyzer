/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/02/2008
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.graduacao.negocio.AproveitamentoValidator;
import br.ufrn.sigaa.ensino.graduacao.negocio.DiscenteCalculosHelper;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.AproveitamentoMov;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * <p>
 * MBean responsável pelas operações de aproveitamento e cancelamento do
 * aproveitamento curricular.
 * </p>
 * <p>
 * O aproveitamento pode ser feito para qualquer discente de qual quer nível.
 * </p>
 *
 * @author Andre M Dantas
 */
@Component("aproveitamento")
@Scope("session")
public class AproveitamentoComponenteMBean extends SigaaAbstractController<MatriculaComponente> implements OperadorDiscente {

	/**
	 * Lista de aproveitamentos a realizar
	 */
	private List<MatriculaComponente> aproveitamentos = new ArrayList<MatriculaComponente>();

	/**
	 * Comando parametrizado pra ser usado ou no cadastro ou no cancelamento
	 */
	private Comando comando;

	/**
	 * Coleção encontrada no passo de busca de componentes que podem ser
	 * aproveitados
	 */
	private Collection<ComponenteCurricular> componentesEncontrados;

	/** Aproveitamentos carregados do discente para a escolha de qual será cancelado */
	public Collection<MatriculaComponente> aproveitamentosAluno;

	/**
	 * Indica se a operação vigente é de cancelamento
	 */
	private boolean cancelar = false;

	/**
	 * Indica se Aproveitou o componente e foi dispensando.
	 */
	private boolean dispensa = false;

	/** matrículas do aluno carregadas para identificar os tipos de integralização dos aproveitamentos */
	private Collection<MatriculaComponente> matriculasIntegralizadas;

	/**
	 * Lista de disciplinas para remoção.
	 */
	private Collection<MatriculaComponente> listaDisciplinasParaRemocao;// = new ArrayList<MatriculaComponente>();

	/**
	 * Indica a última situação da matricula selecionada.
	 */
	private SituacaoMatricula ultimaSituacaoMatSelecionada;
	
	/** 
	 * Indica se é permitido o cancelamento de matrícula do componente informado. 
	 */
	private boolean permiteCancelarMatricula;

	public boolean isCancelar() {
		return cancelar;
	}

	public void setCancelar(boolean cancelar) {
		this.cancelar = cancelar;
	}

	public void setDispensa(boolean dispensa) {
		this.dispensa = dispensa;
	}

	public AproveitamentoComponenteMBean() {
		initObj();
	}

	/**
	 * Popula os dados necessários ao objeto a ser persistido
	 */
	private void initObj() {
		obj = new MatriculaComponente();
		obj.setComponente(new ComponenteCurricular());
		obj.getComponente().setPermiteCancelarMatricula(false);
		if (ultimaSituacaoMatSelecionada == null)
			obj.setSituacaoMatricula(new SituacaoMatricula());
		else
			obj.setSituacaoMatricula(ultimaSituacaoMatSelecionada);
		obj.setNumeroFaltas(100);
		permiteCancelarMatricula = false;
	}

	/**
	 * Seleciona o discente e redireciona para a tela: - ou de busca de
	 * componentes (no caso de cadastro) - ou de lista de aproveitamentos
	 * cadastrados para o discente escolhido
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 */
	public String selecionaDiscente() {
		try {
			if(getSubSistema().equals(SigaaSubsistemas.TECNICO))
				obj.setDiscente(getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), DiscenteTecnico.class));
			else
				obj.setDiscente(getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()));

			if (cancelar) {
				MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
				aproveitamentosAluno = dao.findAproveitamentosByDiscente(obj.getDiscente());

				if (aproveitamentosAluno == null || aproveitamentosAluno.isEmpty()) {
					addMensagemErro("O discente escolhido não possui aproveitamentos que possam ser cancelados");
					return null;
				}
				return telaAproveitamentos();
			} else {
				//carregando matrículas integralizadas
				MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class);
				matriculasIntegralizadas = mdao.findByDiscente(obj.getDiscente(), SituacaoMatricula.getSituacoesPagas());
				for (MatriculaComponente matricula : matriculasIntegralizadas) {
					matricula.getComponente().getDetalhes().getCrTotal();
					if (matricula.getDetalhesComponente() != null) {
						matricula.getDetalhesComponente().getCrTotal();
					}
				}

				if (obj.getDiscente().isConcluido()) {
					addMensagemWarning("Atenção! O discente selecionado encontra-se com status "+ StatusDiscente.getDescricao(StatusDiscente.CONCLUIDO) +".");
				}
				aproveitamentos = new ArrayList<MatriculaComponente>();
				permiteCancelarMatricula = false;
				return telaDadosAproveitamento();
			}
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
	}
	
	/**
	 * Continua com o aproveitamento após aparecer a tela para cancelamento de matrícula.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String continuarAproveitamento() {
		//limpando os atributos definidos na página para o usuário realizar a busca de outro componente e digitar seus respectivos dados.
		obj.setComponente(new ComponenteCurricular());
		obj.setSituacaoMatricula(new SituacaoMatricula());
		obj.setAno(null);
		obj.setPeriodo(null);
		obj.setMediaFinal(null);
		obj.setNumeroFaltas(null);
		obj.setMes(null);
		obj.setAnoInicio(null);
		obj.setMesFim(null);
		obj.setAnoFim(null);
		obj.setConceito(null);
		permiteCancelarMatricula = false;
		return telaDadosAproveitamento();
	}

	/**
	 * Encaminha para a tela dos aproveitamentos.
	 * <br><br>
	 * O método não é invocado por nenhuma JSP
	 * @return
	 */
	public String telaAproveitamentos() {
		return forward("/ensino/aproveitamento/aproveitamentos.jsp");
	}

	/**
	 * Seta o discente da {@link MatriculaComponente} trabalhada.
	 */
	public void setDiscente(DiscenteAdapter discente) {
		obj.setDiscente(discente.getDiscente());
	}

	/**
	 * Método inicial para cadastro de aproveitamento.
	 * <br><br>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * 	 <li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * 	 <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * 	 <li>/sigaa.war/stricto/menus/discente.jsp</li>
	 * 	 <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * 	 <li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAproveitamento() throws ArqException {
		aproveitamentos = new ArrayList<MatriculaComponente>();
		cancelar = false;
		dispensa = false;
		permiteCancelarMatricula = false;
		comando = SigaaListaComando.APROVEITAR_COMPONENTE;
		return iniciar();
	}

	/**
	 * Método inicial para cadastro de dispensa de componente curricular.
	 * <br /><br />
	 * O método não é invocado por nenhuma JSP.
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDispensa() throws ArqException {
		cancelar = false;
		dispensa = true;
		comando = SigaaListaComando.APROVEITAR_COMPONENTE;
		obj.setSituacaoMatricula(SituacaoMatricula.APROVEITADO_DISPENSADO);
		return iniciar();
	}

	public boolean isDispensa() {
		return dispensa;
	}

	/**
	 * Método inicial para cancelamento de um aproveitamento cadastrado de um
	 * aluno.
	 * <br><br>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * 	<li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * 	<li>/sigaa.war/stricto/menus/discente.jsp</li>
	 * 	<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * 	<li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCancelamento() throws ArqException {
		cancelar = true;
		comando = SigaaListaComando.ALTERAR_STATUS_MATRICULA;
		return iniciar();
	}

	/**
	 * Método de início genérico, preparando o MBean tanto para operação de
	 * cadastro como de cancelamento.
	 * <br><br>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/ensino/aproveitamento/busca_componente.jsp</li>
	 * 	<li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * 	<li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * 	<li>/sigaa.war/stricto/menus/discente.jsp</li>
	 * 	<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * 	<li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.CDP, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		prepareMovimento(comando);
		return buscarDiscente();
	}

	/**
	 * Busca os discentes para incluir o aproveitamento.
	 * <br><br>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/graduacao/busca_discente.jsp</li>
	 * 	<li>/sigaa.war/graduacao/menus/aluno.jsp</li>
	 * 	<li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * 	<li>/sigaa.war/stricto/menus/discente.jsp</li>
	 * 	<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * 	<li>/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String buscarDiscente() {

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.APROVEITAMENTO_COMPONENTE);

		return buscaDiscenteMBean.popular();
	}

	/**
	 * Método final para aproveitamento
	 * Chama o processador.
	 * <br><br>
	 * Método chamado pelas seguintes JSPs:
	 * 	<ul>
	 *    <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * 	  <li>/sigaa.war/ensino/aproveitamento/confirma_remocao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {

		if (comando == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "Solicitação já processada");
		
		if (cancelar)
			return confirmarCancelamento();

		if( aproveitamentos == null || aproveitamentos.isEmpty() ){
			addMensagemErro("Adicione pelo menos uma disciplina para realizar o aproveitamento.");
			return null;
		}

		AproveitamentoMov mov = new AproveitamentoMov();
		mov.setCodMovimento(comando);
		mov.setAproveitamentos( aproveitamentos );

		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
		super.afterCadastrar();
		initObj();
		addMessage("Aproveitamento registrado com sucesso.", TipoMensagemUFRN.INFORMATION);
		return cancelar();
	}


	/**
	 * Confirma a senha para efetivar o cancelamento
	 * @return
	 * @throws ArqException
	 */
	private String confirmarCancelamento() throws ArqException {

		if( !confirmaSenha() )
			return null;

			MovimentoOperacaoMatricula mov = new MovimentoOperacaoMatricula();
			mov.setCodMovimento(comando);
			mov.setNovaSituacao(SituacaoMatricula.EXCLUIDA);
			mov.setMatriculas( listaDisciplinasParaRemocao );

			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			} catch (Exception e) {
				return tratamentoErroPadrao(e);
			}

		super.afterCadastrar();
		initObj();
		addMessage("Aproveitamento cancelado com sucesso.", TipoMensagemUFRN.INFORMATION);
		return cancelar();
	}

	/**
	 * Validação das informações submetidas antes de serem enviadas para o processador
	 * @return
	 * @throws DAOException
	 */
	private Collection<MensagemAviso> validarDados() throws DAOException {
		
		Collection<MensagemAviso> erros = new ArrayList<MensagemAviso>();
		
		// not nulls
		if (obj.getDiscente() == null) {
			erros.add(new MensagemAviso("É necessário informar um discente na tela anterior.", TipoMensagemUFRN.ERROR));
			return erros;
		}
		
		if (ValidatorUtil.isEmpty(obj.getComponente())) {
			erros.add(new MensagemAviso("Componente Curricular não informado", TipoMensagemUFRN.ERROR));
		}
		
		if (ValidatorUtil.isEmpty(obj.getSituacaoMatricula())) {
			erros.add(new MensagemAviso("Tipo de Aproveitamento não informado", TipoMensagemUFRN.ERROR));
		}
		
		if( isAtribuirNotaFrequencia() && !obj.getDiscente().isStricto() ){
			if ( obj.getAno() == null || (obj.getAno() != null && obj.getAno() < 1900) ) {
				erros.add(new MensagemAviso("Ano inválido", TipoMensagemUFRN.ERROR));
			}
			
			if ( obj.getPeriodo() == null || ( obj.getPeriodo() != null && (obj.getPeriodo() < 0 || obj.getPeriodo() > 4)) ) {
				erros.add(new MensagemAviso("Período inválido", TipoMensagemUFRN.ERROR));
			}
		}
		
		if (obj.getDiscente().isStricto()) {
			
			if (obj.getAnoInicio() != null && obj.getAnoInicio() < 1900) {
				erros.add(new MensagemAviso("Ano início inválido", TipoMensagemUFRN.ERROR));
			}
			
			if (obj.getMes() != null && (obj.getMes() < 0 || obj.getMes() > 12)) {
				erros.add(new MensagemAviso("Mês início inválido", TipoMensagemUFRN.ERROR));
			}
			
			if (obj.getAnoFim() != null && obj.getAnoFim() < 1900) {
				erros.add(new MensagemAviso("Ano Fim inválido", TipoMensagemUFRN.ERROR));
			}
			
			if (obj.getMesFim() != null && (obj.getMesFim() < 0 || obj.getMesFim() > 12)) {
				erros.add(new MensagemAviso("Mês Fim inválido", TipoMensagemUFRN.ERROR));
			}
		}

		if (!SituacaoMatricula.APROVEITADO_DISPENSADO.equals(obj.getSituacaoMatricula()))  {
			
			if (isNota() && isAtribuirNotaFrequencia()) {
				
				if (obj.getMediaFinal() == null || obj.getMediaFinal() <= 0 || obj.getMediaFinal() > 10) {
					erros.add(new MensagemAviso("Média Final inválida", TipoMensagemUFRN.ERROR));
				}
			} else if (isConceito() && isAtribuirNotaFrequencia() && obj.getComponente().isNecessitaMediaFinal() &&  obj.getConceito() == null) {
				erros.add(new MensagemAviso("Selecione um Conceito", TipoMensagemUFRN.ERROR));
			} else if (isCompetencia()) {
				obj.setMediaFinal(10.0);
			}
			
			
			if (isAtribuirNotaFrequencia() && (obj.getNumeroFaltas() == null || obj.getNumeroFaltas() < 0)) {
				erros.add(new MensagemAviso("Frequência inválida", TipoMensagemUFRN.ERROR));
			}
		}
		
		return erros;
	}

	/**
	 * Encaminha para a tela com os dados do aproveitamento.<br />
	 * O método não é invocado por nenhuma JSP
	 */
	public String telaDadosAproveitamento() {
		return forward("/ensino/aproveitamento/dados_aproveitamento.jsp");
	}

	/**
	 * Encaminha para a tela de confirmação da remoção.<br />
	 * O método não é invocado por nenhuma JSP
	 * @return
	 */
	public String telaConfirmaRemocao() {
		return forward("/ensino/aproveitamento/confirma_remocao.jsp");
	}

	/**
	 * Busca componentes a partir de quatro critérios simultâneos.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/busca_componente.jsp</li>
	 * </ul>
	 * @return
	 * @throws
	 */
	public String buscarComponentes() throws ArqException {
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		ComponenteCurricular cc = obj.getComponente();
		String codigo = null;
		String nome = null;
		TipoComponenteCurricular tipo = null;
		Unidade unidade = null;
		boolean filtroTotal = true;
		if (Boolean.valueOf(getParameter("buscaCodigo")))
			codigo = cc.getCodigo();
		if (Boolean.valueOf(getParameter("buscaNome")))
			nome = cc.getNome();
		if (Boolean.valueOf(getParameter("buscaTipo")))
			tipo = cc.getTipoComponente();
		if (Boolean.valueOf(getParameter("buscaUnidade")))
			unidade = cc.getUnidade();
		if (codigo == null && nome == null && tipo == null && unidade == null) {
			addMensagemErro("Por favor, escolha algum critério de busca");
		} else {
			if (NivelEnsino.isEnsinoBasico(getNivelEnsino()))
				unidade = new Unidade(getUnidadeGestora());
			try {
				if (getUsuarioLogado() != null) {
					if (isUserInRole(SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG))
						filtroTotal = false;
				}
				componentesEncontrados = dao.findCompleto(codigo, nome, tipo, unidade, getNivelEnsino(), filtroTotal);

				if (componentesEncontrados.size() == 2000) 
					addMensagemErro("A busca ultrapasso o limite de 2000 registros, refine mais sua busca");
				
			} catch (Exception e) {
				componentesEncontrados = new ArrayList<ComponenteCurricular>(0);
				return tratamentoErroPadrao(e);
			}
		}
		return null;
	}

	/**
	 * Testa se a forma de avaliação é por nota.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isNota() throws DAOException {
		return getParametrosAcademicos().getMetodoAvaliacao() == br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao.NOTA;
	}
	
	/**
	 * Testa se a forma de avaliação é por conceito.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isConceito() throws DAOException {
		return getParametrosAcademicos().getMetodoAvaliacao() == br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao.CONCEITO;
	}
	
	/**
	 * Testa se a forma de avaliação é por competência.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isCompetencia() throws DAOException {
		return getParametrosAcademicos().getMetodoAvaliacao() == br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao.COMPETENCIA;
	}
	
	/**
	 * Testa se o caso de uso atribui Nota e frequência ao aproveitamento de estudos.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isAtribuirNotaFrequencia() throws DAOException {
		return getParametrosAcademicos().isExigeNotaAproveitamento();
	}
	
	/**
	 * Permite Cancelar a Matrícula do Componente a ser aproveitado.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String cancelarMatricula() throws ArqException{
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.PPG,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.CDP, SigaaPapeis.GESTOR_LATO,
				SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.GESTOR_INFANTIL);	
		
		if (ValidatorUtil.isEmpty(obj.getComponente())){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Componente Curricular");
			return null;
		}
		
		if (!confirmaSenha())
			return null;
				
		MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class);
		Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
		try {		
			matriculas = mdao.findByDiscente(obj.getDiscente(), SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA);
			
			if (matriculas == null || matriculas.isEmpty()){
				addMensagemErro("Não existe nenhuma matrícula a ser excluída!");
				return null;
			}
			
			if (matriculas.size() == 1){
				addMensagemErro("Não é possível Excluir a Matrícula do Componente "+obj.getComponente().getCodigo()+						
						", pois é o Único que o discente está matriculado.");
				return null;
			}
			
			obj.getComponente().setPermiteCancelarMatricula(true); 								
			return adicionarAproveitamento();
		} finally {
			if (mdao != null)
				mdao.close();
		}		
	}
	
	/**
	 * Adiciona um aproveitamento a lista de aproveitamentos para realizar.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String adicionarAproveitamento() throws DAOException {
		erros = new ListaMensagens();
		erros.addAll(validarDados());
		if (hasErrors())
			return null;
		try {
			GenericDAO dao = getGenericDAO();
			if (!obj.getComponente().isPermiteCancelarMatricula())
				obj.setComponente( dao.findByPrimaryKey(obj.getComponente().getId(), ComponenteCurricular.class) );
			
			// verificando se é uma subunidade de um bloco
			if(obj.getComponente().isSubUnidade() && !MetropoleDigitalHelper.isMetropoleDigital(obj.getComponente())){
				addMensagemErro("Não é possível registrar aproveitamento para uma subunidade de um bloco." +
						"<br/>Informe apenas o componente curricular do tipo BLOCO correspondente.");
				return null;
			}
			
			DiscenteAdapter discente  = obj.getDiscente();

			// verificando se já foi inserido aproveitamento para esta disciplina
			for( MatriculaComponente mc : getAproveitamentos()){
				if( mc.getComponente().getId() == obj.getComponente().getId() && ! ( mc.getComponente().isConteudoVariavel() && mc.getDiscente().isStricto() ) ){
					addMensagemErro("Este componente já foi adicionado.");
					obj.getComponente().setId(0);
					obj.getComponente().setNome("");
					return null;
				}
			}
			
			if (obj.getNumeroFaltas() != null && obj.getNumeroFaltas() > 100){
				addMensagem(MensagensArquitetura.VALOR_MENOR_IGUAL_A, "Frequência", 100);
				return null;
			}
			
			AproveitamentoValidator.validaAproveitamento(obj.getDiscente(), obj,  getParametrosAcademicos(), erros, getUsuarioLogado());
			
			/* 
			 * Verifica se já está matriculado na disciplina. 
			 * Se estiver habilita para poder cancelar a matrícula do componente informado. */
			MatriculaComponenteDao matriculadao = getDAO(MatriculaComponenteDao.class);
			Collection<MatriculaComponente> matriculas = matriculadao.findByDiscenteEDisciplina(obj.getDiscente(), obj.getComponente(), SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA);
			if (matriculas != null && matriculas.size() > 0 && !obj.getComponente().isPermiteCancelarMatricula()) {				
				if( ! (obj.getDiscente().isStricto() && obj.getComponente().isConteudoVariavel()) )
					permiteCancelarMatricula = true;				
			}
			
			if (hasErrors() || (permiteCancelarMatricula && !obj.getComponente().isPermiteCancelarMatricula()))
				return null;

			obj.setSituacaoMatricula( getGenericDAO().findByPrimaryKey( obj.getSituacaoMatricula().getId(), SituacaoMatricula.class ) );
			obj.setDetalhesComponente(obj.getComponente().getDetalhes());
			if (obj.getNumeroFaltas() != null){
				ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(obj.getComponente());
				obj.setNumeroFaltasPorFrequencia(obj.getNumeroFaltas(),parametros.getMinutosAulaRegular());
			}	
			obj.setNotas(null);
			obj.setRecuperacao(null);

			if (discente.isStricto()){
				if (!isPortalPpg() && obj.getComponente().getDetalhes().isProibeAproveitamento()){
					addMensagem(MensagensStrictoSensu.APROVEITAMENTO_COMPONENTE_APENAS_PPG);
					return null;
				}
			}

			// preenchendo tipos de integralização
			if (discente.isGraduacao()) {
				Collection<MatriculaComponente> todas = new ArrayList<MatriculaComponente>(aproveitamentos);
				todas.addAll(matriculasIntegralizadas);
				
				MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class);
				todas.addAll( mdao.findByDiscente(obj.getDiscente(), SituacaoMatricula.getSituacoesMatriculadas()) );
				todas.add(obj);

				
				DiscenteGraduacao dg = getGenericDAO().findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
				DiscenteCalculosHelper.calcularTipoIntegralizacaoMatriculas(dg, todas);
				// verifica se o aproveitamento adicionado leva o aluno a exceder o limite de eletiva (ou extra)
				if (TipoIntegralizacao.EXTRA_CURRICULAR.equals(obj.getTipoIntegralizacao())) {
					int crExtra = 0;
					for (MatriculaComponente mat : todas) {
						if (mat.getTipoIntegralizacao().equals(TipoIntegralizacao.EXTRA_CURRICULAR))
							crExtra += mat.getComponenteCrTotal();
					}
					if (crExtra > getParametrosAcademicos().getMaxCreditosExtra()) {
						addMensagemErro("Não é possível aproveitar o componente selecionado pois o limite de  "
								+ getParametrosAcademicos().getMaxCreditosExtra() + 
								" créditos eletivos foi excedido.");
						return null;
					}
				}
			}
			aproveitamentos.add(obj);

			Short ano = obj.getAno() != null ? obj.getAno() : null;
			Byte periodo = obj.getPeriodo() != null ? obj.getPeriodo() : null;
			ultimaSituacaoMatSelecionada = UFRNUtils.deepCopy(obj.getSituacaoMatricula());
			
			initObj();
			obj.setDiscente(discente);
			obj.setAno( ano );
			obj.setPeriodo( periodo );
			
			permiteCancelarMatricula = false;			

			return telaDadosAproveitamento();
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}
	}

	/**
	 * Remove um aproveitamento da lista de aproveitamentos.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * </ul>
	 * @return
	 */
	public String removeAproveitamento() {
		int index = 0;
		Integer idComponente = getParameterInt("idComponente");
		
		for(int i = 0 ; i < getAproveitamentos().size(); i++) {
			if( getAproveitamentos().get(i).getComponente().getId() == idComponente) {
				index = i;
			}
		}	
				
		getAproveitamentos().remove(index);
		
		return null;
	}

	/**
	 * Seleciona os aproveitamentos dentre os aproveitamentos cadastrados do aluno selecionado.
	 * Essa seleção é usada no caso do cancelamento de aproveitamento.
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/aproveitamentos.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarAproveitamento() throws DAOException {

		listaDisciplinasParaRemocao = new ArrayList<MatriculaComponente>();

		ArrayList<Integer> idsParaRemocacao = new ArrayList<Integer>();
		Collection<MatriculaComponente> mat = getAproveitamentosAluno();

		for (MatriculaComponente ite : mat) {

			if ( ite.isSelected() ) {
				idsParaRemocacao.add( ite.getId() );
			}
		}

		for (int i = 0; i < idsParaRemocacao.size(); i++) {
			MatriculaComponente matr = getGenericDAO().findByPrimaryKey( idsParaRemocacao.get(i), MatriculaComponente.class);
			if( matr.getDetalhesComponente() != null )
				matr.getDetalhesComponente().getId();
			listaDisciplinasParaRemocao.add( matr );
		}

		if ( listaDisciplinasParaRemocao.size() == 0 ) {
			addMensagemWarning("É necessário escolher ao menos um aproveitamento!");
			return "";
		} else {
			if(getSubSistema().equals(SigaaSubsistemas.TECNICO))
				obj.setDiscente(getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), DiscenteTecnico.class));
			else
				obj.setDiscente(getGenericDAO().findByPrimaryKey(obj.getDiscente().getId(), Discente.class));
			Integer metodoAvaliacao = ParametrosGestoraAcademicaHelper.getParametros(obj.getDiscente()).getMetodoAvaliacao();
			for( MatriculaComponente mc : listaDisciplinasParaRemocao )
				mc.setMetodoAvaliacao( metodoAvaliacao );
			return telaConfirmaRemocao();
		}
	}

	/**
	 * Serve para informar se deve ou não exibir o Tipo de Componente.
	 * 
	 * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/aproveitamento/dados_aproveitamento.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isExibirTipoComponente(){
		if (isTecnico() || isPortalEscolasEspecializadas())
			return false;
		return true;
	}
	
	public Collection<ComponenteCurricular> getComponentesEncontrados() {
		return componentesEncontrados;
	}

	public void setComponentesEncontrados(Collection<ComponenteCurricular> componentesEncontrados) {
		this.componentesEncontrados = componentesEncontrados;
	}

	public Comando getComando() {
		return comando;
	}

	public void setComando(Comando comando) {
		this.comando = comando;
	}

	public Collection<MatriculaComponente> getAproveitamentosAluno() {
		return aproveitamentosAluno;
	}

	public void setAproveitamentosAluno(Collection<MatriculaComponente> aproveitamentosAluno) {
		this.aproveitamentosAluno = aproveitamentosAluno;
	}

	public List<MatriculaComponente> getAproveitamentos() {
		return aproveitamentos;
	}

	public void setAproveitamentos(List<MatriculaComponente> aproveitamentos) {
		this.aproveitamentos = aproveitamentos;
	}


	public Collection<SelectItem> getSituacoesPagasCombo() {
		return toSelectItems(SituacaoMatricula.getSituacoesPagas(), "id", "descricao");
	}

	public Collection<SelectItem> getSituacoesAproveitadasCombo() {
		return toSelectItems(SituacaoMatricula.getSituacoesAproveitadas(), "id", "descricao");
	}

	public Collection<MatriculaComponente> getListaDisciplinasParaRemocao() {
		return listaDisciplinasParaRemocao;
	}

	public void setListaDisciplinasParaRemocao(
			Collection<MatriculaComponente> listaDisciplinasParaRemocao) {
		this.listaDisciplinasParaRemocao = listaDisciplinasParaRemocao;
	}

	public SituacaoMatricula getUltimaSituacaoMatSelecionada() {
		return ultimaSituacaoMatSelecionada;
	}

	public void setUltimaSituacaoMatSelecionada(
			SituacaoMatricula ultimaSituacaoMatSelecionada) {
		this.ultimaSituacaoMatSelecionada = ultimaSituacaoMatSelecionada;
	}

	public boolean isPermiteCancelarMatricula() {
		return permiteCancelarMatricula;
	}

	public void setPermiteCancelarMatricula(boolean permiteCancelarMatricula) {
		this.permiteCancelarMatricula = permiteCancelarMatricula;
	}
}
