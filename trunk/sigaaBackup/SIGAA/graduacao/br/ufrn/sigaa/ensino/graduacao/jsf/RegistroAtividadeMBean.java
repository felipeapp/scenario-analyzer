/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 07/02/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.prodocente.TrabalhoFimCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.RegistroAtividadeDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.OrientacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.graduacao.dao.ConsolidarMatriculasAtividadesDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.RegistroAtividadeValidator;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.negocio.dominio.RegistroAtividadeMov;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.negocio.MatriculaStrictoValidator;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Estagio;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;
import br.ufrn.sigaa.prodocente.atividades.jsf.EstagioMBean;
import br.ufrn.sigaa.prodocente.atividades.jsf.TrabalhoFimCursoMBean;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Managed Bean para controle das opera��es relacionadas ao registro de
 * atividades acad�micas espec�ficas
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
@Component("registroAtividade") @Scope("session")
public class RegistroAtividadeMBean extends SigaaAbstractController<MatriculaComponente> implements OperadorDiscente {

	/** Tipo da opera��o sendo executada */
	private OperacaoRegistroAtividade operacao;
	
	/** MBean respons�vel pelos dados da atividade no prodocente. Pode assumir o valor de EstagioMBean ou TrabalhoFimCursoMBean. */
	private AbstractControllerAtividades<?> atividadesMBean;

	/** Identificador do orientador da atividade. 
	 * Utilizado no formul�rio dos dados do registro, campo orientador  
	 */
	private Integer idOrientador;
	
	/** Identificador do coorientador da atividade. 
	 * Utilizado no formul�rio dos dados do registro, campo coorientador  
	 */
	private Integer idCoOrientador;
	
	/** Nome do orientador da atividade.
	 * Utilizado no formul�rio dos dados do registro, campo orientador 
	 */
	private String nomeOrientador;
	
	/** Nome do coorientador da atividade.
	 * Utilizado no formul�rio dos dados do registro, campo coorientador 
	 */
	private String nomeCoOrientador;
	
	/** Indica se a matr�cula na atividade foi migrada do PontoA. */
	private boolean migrado;

	/** Indica se deve filtrar a busca por departamento. */
	private boolean buscaDepartamento;
	/** Indica se deve filtrar a busca pelo nome da atividade. */
	private boolean buscaNomeAtividade;
	/** Indica se deve filtrar a busca por tipo de atividade. */
	private boolean buscaTipoAtividade;

	
	// Defini��o das Views 
	/** Link para o formul�rio de busca de atividade. */
	public static final String VIEW_BUSCA_ATIVIDADE = "/graduacao/registro_atividade/busca_atividade.jsp";
	/** Link para o formul�rio de sele��o de atividade. */
	public static final String VIEW_SELECAO_ATIVIDADE = "/graduacao/registro_atividade/selecao_atividade.jsp";
	/** Link para o formul�rio de visualiza��o do registro de atividade. */
	public static final String VIEW_DADOS_REGISTRO = "/graduacao/registro_atividade/dados_registro.jsp";
	/** Link para o formul�rio de visualiza��o do registro de atividade stricto sensu. */
	public static final String VIEW_DADOS_REGISTRO_STRICTO = "/stricto/registro_atividade/dados_registro.jsp";
	/** Link para o formul�rio de visualiza��o do registro de atividade lato sensu. */
	public static final String VIEW_DADOS_REGISTRO_LATO = "/lato/registro_atividade/dados_registro.jsp";
	/** Link para o formul�rio de visualiza��o de atividade dos orientandos. */
	public static final String VIEW_ATIVIDADES_ORIENTANDOS = "/graduacao/registro_atividade/atividades_orientandos.jsp";
	/** Link para o formul�rio de visualiza��o do resumo da atividade. */
	public static final String VIEW_RESUMO = "/graduacao/registro_atividade/resumo.jsp";
	/** Link para o formul�rio de visualiza��o do resumo da atividade de stricto sensu. */
	public static final String VIEW_RESUMO_STRICTO = "/stricto/registro_atividade/resumo.jsp";
	/** Link para o formul�rio de visualiza��o do resumo da atividade de lato sensu. */
	public static final String VIEW_RESUMO_LATO = "/lato/registro_atividade/resumo.jsp";
	/** Link para o formul�rio de visualiza��o da consolida��o de matr�culas do coordenador de gradua��o. */
	public static final String VIEW_CONSOLIDACAO_MATRICULAS = "/graduacao/registro_atividade/listar_atividades.jsp";
	/** Link para o formul�rio de visualiza��o do registro de atividade resid�ncia m�dica **/
	public static final String VIEW_DADOS_REGISTRO_RESIDENCIA_MEDICA = "/complexo_hospitalar/RegistroAtividade/dados_registro.jsp";
	/** Link para o formul�rio de visualiza��o do registro de atividade resid�ncia m�dica. */
	public static final String VIEW_RESUMO_RESIDENCIA_MEDICA = "/complexo_hospitalar/RegistroAtividade/resumo.jsp";

	/** Indica se � apenas visualiza��o da atividade pelo orientador. */
	private boolean visualizarResumo = false;

	/** Indica a de dispensa de atividade (mesmo quando o componente necessita de nota) */
	private boolean dispensa = false;

	/** Indica que a atividade � de Ensino � Dist�ncia. */
	private boolean ead;
	
	/** Determina se a avalia��o da atividade � por nota ou conceito. Usado apenas no Lato Sensu. */
	private int metodoAvaliacao;
	
	/** Carga hor�ria do orientador dedicada � orienta��o desta atividade. */
	private int chDedicada;

	/** Cole��o de atividades retornadas pela busca no segundo passo da matr�cula */
	private Collection<ComponenteCurricular> atividades = null;
	
	/** Cole��o de atividades retornadas pela busca no segundo passo da matr�cula */
	private Collection<MatriculaComponente> atividadesMatriculadas = null;
	
	/** Indica se � obrigat�rio informar docentes envolvidos no registro da atividade. */
	private Boolean obrigatorioInformarDocentesEnvolvidos;
	
	/** Calend�rio que est� sendo utilizado na opera��o em quest�o */
	private CalendarioAcademico calendario;
	
	/** Indica se � a opera��o de consolidar matr�culas do coordenador de gradua��o. */
	private boolean consolidarMatriculas = false;
	
	public RegistroAtividadeMBean() {
		clear();
	}

	/**
	 * Inicia o fluxo de registro de matr�culas em atividades acad�micas espec�ficas. <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ead/menu.jsp</li>
	 *   	<li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 *   	<li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 *   	<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *      <li>/sigaa.war/stricto/menus/matricula.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatricula() throws ArqException {
		if (!isTutorEad()) {
			checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD,
					SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE,
					SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, 
					SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
					SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
					SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.COORDENADOR_TECNICO});
		}
		prepareMovimento(SigaaListaComando.MATRICULAR_ATIVIDADE);

		return iniciar(OperacaoRegistroAtividade.MATRICULA);
	}

	/**
	 * Inicia o fluxo de consolida��o de matr�culas em atividades acad�micas espec�ficas. 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ead/menu.jsp</li>
	 *   	<li>/sigaa.war/graduacao/menu_coordenador.jsp </li>
	 *   	<li>/sigaa.war/graduacao/menus/programa.jsp </li>
	 *   	<li>/sigaa.war/graduacao/registro_atividade/atividades_orientados.jsp </li>
	 *   	<li>/sigaa.war/stricto/menu_coordenador.jsp </li>
	 *      <li>/sigaa.war/stricto/menus/matricula.jsp </li>
	 * </ul>     
	 * @return
	 * @throws ArqException
	 */
	public String iniciarConsolidacao() throws ArqException {
		checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD,SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.COORDENADOR_TECNICO});
		prepareMovimento(SigaaListaComando.CONSOLIDAR_ATIVIDADE);
		
		return iniciar(OperacaoRegistroAtividade.CONSOLIDACAO);
	}

	/**
	 * Inicia o fluxo de consolida��o de matr�culas em atividades acad�micas espec�ficas. Todos os discentes s�o listados de uma vez.
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   	<li>/sigaa.war/graduacao/menu_coordenador.jsp </li>
	 * </ul>     
	 * @return
	 * @throws ArqException
	 */
	public String iniciarConsolidarMatriculas() throws ArqException {
		
		checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO});
		consolidarMatriculas = true;
		atividadesMatriculadas = null;
		ConsolidarMatriculasAtividadesDao dao = getDAO(ConsolidarMatriculasAtividadesDao.class);
		atividadesMatriculadas = dao.findDiscentesEAtividadesByCurso(getCursoAtualCoordenacao(),SituacaoMatricula.MATRICULADO);
		setOperacao(OperacaoRegistroAtividade.CONSOLIDACAO);
		return forward(VIEW_CONSOLIDACAO_MATRICULAS);
	}

	/**
	 * Inicia o fluxo de registro da valida��o de matr�culas em atividades acad�micas espec�ficas.	 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ead/menu.jsp</li>
	 *   	<li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 *   	<li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 *   	<li>/sigaa.war/lato/menu_coordenador.jsp</li>
	 *   	<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *      <li>/sigaa.war/stricto/menus/matricula.jsp</li>
	 *      <li>/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 * </ul>      
	 * @return
	 * @throws ArqException
	 */
	public String iniciarValidacao() throws ArqException {
		checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.COORDENADOR_TECNICO});
		prepareMovimento(SigaaListaComando.VALIDAR_ATIVIDADE);
		return iniciar(OperacaoRegistroAtividade.VALIDACAO);
	}

	/**
	 * Inicia o fluxo de exclus�o de matr�culas em atividades acad�micas espec�ficas. 	 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ead/menu.jsp</li>
	 *   	<li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 *   	<li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 *   	<li>/sigaa.war/stricto/menu_coordenador.jsp </li>
	 *      <li>/sigaa.war/stricto/menus/matricula.jsp</li>
	 * </ul>     
	 * @return
	 * @throws ArqException
	 */
	public String iniciarExclusao() throws ArqException {
		checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.COORDENADOR_TECNICO});
		prepareMovimento(SigaaListaComando.EXCLUIR_MATRICULA_ATIVIDADE);
		return iniciar(OperacaoRegistroAtividade.EXCLUSAO);
	}

	/**
	 * Inicia o fluxo de renova��o de matr�culas em atividades acad�micas espec�ficas.
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>/sigaa.war/stricto/menu_coordenador.jsp </li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRenovacao() throws ArqException {
		checkRole(new int[]{SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG});
		prepareMovimento(SigaaListaComando.RENOVAR_MATRICULA_ATIVIDADE);
		return iniciar(OperacaoRegistroAtividade.RENOVACAO);
	}
	
	/**
	 * Inicia o fluxo de altera��o de per�odo de uma atividade acad�mica espec�fica.
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>/sigaa.war/stricto/menu_coordenador.jsp </li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlterarPeriodo() throws ArqException{
		checkRole(new int[]{SigaaPapeis.PPG});
		prepareMovimento(SigaaListaComando.ALTERAR_PERIODO_ATIVIDADE);		
		return iniciar(OperacaoRegistroAtividade.ALTERACAO_PERIODO);
	}
	
	/**
	 * Inicia o fluxo de altera��o de per�odo de uma atividade acad�mica espec�fica.
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menus/programa.jsp </li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlterarTrabalhoEstagio() throws ArqException{
		checkRole(new int[]{ SigaaPapeis.DAE,SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO });
		removeOperacaoAtiva();
		prepareMovimento(SigaaListaComando.ALTERAR_ATIVIDADE_GRADUACAO);		
		return iniciar(OperacaoRegistroAtividade.ALTERACAO_GRADUACAO);
	}
	
	/**
	 * Inicia o fluxo de valida��o de matr�culas em atividades acad�micas espec�ficas, para 
	 * alunos do n�vel Lato Sensu. 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   	<li>/sigaa.war/lato/menu_coordenador.jsp </li>
	 *      <li>/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp </li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarValidacaoLatoSensu() throws ArqException {
		checkRole(new int[]{SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO});
		prepareMovimento(SigaaListaComando.VALIDAR_ATIVIDADE);
		if (atividadesMBean != null)
			atividadesMBean.resetBean();
		return iniciar(OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU);
	}

	/**
	 * Inicia o fluxo de altera��o de matr�culas em atividades acad�micas espec�ficas, para 
	 * alunos do n�vel Lato Sensu. 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *      <li>/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp </li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String alterarValidacaoLatoSensu() throws ArqException {
		operacao = OperacaoRegistroAtividade.ALTERACAO_VALIDACAO_LATO_SENSU;
		checkRole(new int[]{SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO});
		prepareMovimento(SigaaListaComando.ALTERAR_ATIVIDADE_GRADUACAO);
		return iniciar(OperacaoRegistroAtividade.ALTERACAO_VALIDACAO_LATO_SENSU);
	}
	
	/**
	 * Verifica se o fluxo deve considerar as especificidades do Ensino a Dist�ncia
	 */
	private void verificaEad() {
		if (getParameterBoolean("ead")) { ead = true; }
	}

	/**
	 * Prepara para o inicio das opera��es de registro de atividades
	 *
	 * @param op
	 * @return
	 */
	private String iniciar(OperacaoRegistroAtividade op) {
		clear();
		verificaEad();
		setOperacao(op);

		// Verificar se o registro � compuls�rio
		String compulsoria = getParameter("compulsoria");

		if ( compulsoria != null && "true".equals(compulsoria) ){
			if (isPermissaoMatriculaCompulsoria()) {
				obj.getRegistroAtividade().setMatriculaCompulsoria(true);
			} else {
				addMensagemErro("Seu usu�rio n�o possui permiss�o para realizar o registro compuls�rio de atividades");
				return null;
			}
		}

		if ( isTutorEad() ) {
			return telaSelecaoTutoria();
		} else {
			return buscarDiscente();
		}
	}
	
	/**
	 * Inicia a Opera��o atual com outro discente
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/confirmacao.jsp </li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarNovoDiscente() throws ArqException{
		preparaMovimentoOperacaoAtual();
		return selecionarDiscente();
	}
	
	/**
	 * Inicia a Opera��o atual com o mesmo discente
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/confirmacao.jsp </li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarMesmoDiscente() throws ArqException{
		preparaMovimentoOperacaoAtual();
		return selecionaDiscente();
	}	
	
	/**
	 * Prepara o Movimento da Opera��o Atual
	 * @throws ArqException
	 */
	private void preparaMovimentoOperacaoAtual() throws ArqException {
		switch(operacao) {
			case MATRICULA:
				prepareMovimento(SigaaListaComando.MATRICULAR_ATIVIDADE);
				break;
			case CONSOLIDACAO:
				prepareMovimento(SigaaListaComando.CONSOLIDAR_ATIVIDADE);
				break;
			case VALIDACAO:
			case VALIDACAO_LATO_SENSU:
				prepareMovimento(SigaaListaComando.VALIDAR_ATIVIDADE);
				break;
			case EXCLUSAO:
				prepareMovimento(SigaaListaComando.EXCLUIR_MATRICULA_ATIVIDADE);
				break;
			case RENOVACAO:
				prepareMovimento(SigaaListaComando.RENOVAR_MATRICULA_ATIVIDADE);
				break;
			case ALTERACAO_PERIODO:
				prepareMovimento(SigaaListaComando.ALTERAR_PERIODO_ATIVIDADE);
				break;	
			case ALTERACAO_GRADUACAO:
			case ALTERACAO_VALIDACAO_LATO_SENSU:
				prepareMovimento(SigaaListaComando.ALTERAR_ATIVIDADE_GRADUACAO);
				break;		
		}		
	}

	/**
	 * Seleciona o discente para que seja realizada a opera��o de registro desejada. 
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>/sigaa.war/graduacao/registro_atividade/selecao_atividade.jsp </li>
	 * </ul> 
	 * @return
	 */
	public String selecionarDiscente() {
		return iniciar(operacao);
	}


	/**
	 * Limpa os atributos do MBean
	 */
	private void clear() {
		obj = new MatriculaComponente();
		obj.setRegistroAtividade(new RegistroAtividade());
		obj.getRegistroAtividade().setCoordenador(new Servidor());
		obj.getRegistroAtividade().setOrientacoesAtividade( new HashSet<OrientacaoAtividade>() );
		obj.getRegistroAtividade().setMatricula(obj);
		clearTransientObjects();
	}
	
	/**
	 * Limpa os atributos n�o persistidos
	 */
	private void clearTransientObjects(){
		consolidarMatriculas = false;
		clearTransientObjectsFor();
	}
	
	/**
	 * Limpa os atributos n�o persistidos
	 */
	private void clearTransientObjectsFor(){
		idOrientador = null;
		nomeOrientador = null;
		idCoOrientador = null;
		nomeCoOrientador = null;
		buscaDepartamento = false;
		buscaNomeAtividade = false;
		buscaTipoAtividade = false;
		visualizarResumo = false;
		atividades = null;
		chDedicada = 0;
		obrigatorioInformarDocentesEnvolvidos = null;
		atividadesMBean = null;
	}
	
	/**
	 * Inicializa objetos envolvidos e popula o discente.
	 * @throws DAOException 
	 */
	private void clearDadosRegistroAtividade() throws DAOException{
		if ( !isEmpty( obj.getDiscente() ) ){
			int idDiscente = obj.getDiscente().getId();
			boolean compulsoria = obj.getRegistroAtividade().isMatriculaCompulsoria();
			clear();
			obj.setDiscente( getDAO(DiscenteDao.class).findByPK(idDiscente) );
			obj.getRegistroAtividade().setMatriculaCompulsoria(compulsoria);
		}
	}

	/**
	 * Redireciona para o Managed Bean para a busca de discentes de gradua��o 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   	<li>/sigaa.war/graduacao/registro_atividade/busca_atividade.jsp </li>
	 *      <li>/sigaa.war/lato/registro_atividade/dados_registro.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String buscarDiscente() {

		if ( isTutorEad() ) {
			return telaSelecaoTutoria();
		}

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		// Definir c�digo da opera��o
		if ( isExclusao() ) {
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EXCLUIR_REGISTRO_ATIVIDADE);
		} else {
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.REGISTRO_ATIVIDADE);
		}

		buscaDiscenteMBean.setEad(ead);
		String forward = buscaDiscenteMBean.popular();

		/**
		 * ( Pr�-Reitoria de P�s-Gradua��o) pode validar atividade de discente conclu�do.
		 */
		if( isValidacao() && isUserInRole(SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO)){
			buscaDiscenteMBean.getOperacao().setStatusValidos(new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO, StatusDiscente.GRADUANDO, StatusDiscente.CONCLUIDO });
		}

		return forward;
	}

	/**
	 * Seleciona um discente de ensino a dist�ncia para que o registro seja realizado 
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>/sigaa.war/graduacao/matricula/selecao_tutoria.jsp </li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String selecionaDiscenteTutoria() throws ArqException {
		obj.setDiscente(new Discente(getParameterInt("id")));
		getGenericDAO().initialize(obj.getDiscente());

		if ( isTutorEad() && obj.getDiscente().isDiscenteEad() && !CalendarioAcademicoHelper.getCalendario(obj.getDiscente()).isPeriodoMatriculaRegular() ){
			addMensagemErro("N�o est� no per�odo oficial de matr�culas on-line");
			return iniciarMatricula();
		}

		return selecionaDiscente();
	}

	/**
	 * M�todo invocado pelo MBean de busca de discente, ap�s a sele��o do discente.
	 * 
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>M�todo n�o invocado por JSP.</li>
	 * </ul> 
	 * 
	 * @throws ArqException 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		
		/** Na situa��o de repetir a opera��o com o mesmo discente, inicializa todos os atributos do MBean. */
		clearDadosRegistroAtividade();
		String codComponente = ParametroHelper.getInstance().getParametro(ParametrosLatoSensu.COMPONENTE_OBRIGATORIO_LATO);
		if ( isEmpty( obj.getDiscente() ) ){
			addMensagem(MensagensGerais.DISCENTE_NAO_SELECIONADO);
			return iniciarNovoDiscente();
		}	
		
		ComponenteCurricularDao componenteDAO = getDAO(ComponenteCurricularDao.class);
		DiscenteAdapter discente = obj.getDiscente();

		if ( discente.isLato() && metodoAvaliacao != ((CursoLato)discente.getCurso()).getPropostaCurso().getMetodoAvaliacao() ) {
			metodoAvaliacao = ((CursoLato)discente.getCurso()).getPropostaCurso().getMetodoAvaliacao();
		}

		/** Carrega as atividades que o discente est� matriculado */
		populaAtividadesMatriculadas();
		
		/** Verifica se � permitido registrar atividade para o discente selecionado */
		RegistroAtividadeValidator.isPermiteRegistrarAtividade(discente, erros);
			
		if( hasErrors() ){
			return null;
		}

		switch (operacao) {
			
			case MATRICULA:
			case VALIDACAO:
			
				if (discente.isStricto()) { 
					prepararSelecaoAtividadesStricto(); 
				}
				return telaAtividades();
			
			case CONSOLIDACAO:
			case EXCLUSAO:
			case RENOVACAO:
			case ALTERACAO_GRADUACAO:
			case ALTERACAO_PERIODO:
				
				/** Se Gradua��o, mant�m somente as matr�culas de atividades do tipo TCC e Est�gio. */
				if( operacao.equals(OperacaoRegistroAtividade.ALTERACAO_GRADUACAO) ){
					 populaApenasMatriculasTrabalhoEstagio();
					 if ( isAllEmpty(atividadesMatriculadas) ) {
						 addMensagem( MensagensGerais.DISCENTE_SEM_MATRICULA_TIPO_ATIVIDADE, 
								 getObj().getDiscente().getNome(), "Trabalho Conclus�o de Curso ou Est�gio" );
					 }
				}else if ( isAllEmpty(atividadesMatriculadas) ) {
					 addMensagem( MensagensGerais.DISCENTE_SEM_MATRICULA_ATIVIDADE, 
							 getObj().getDiscente().getNome() );
					 return cancelar();
				}
				if( hasErrors() )
					 return null;
				
				return telaAtividades();
				
			case VALIDACAO_LATO_SENSU:
				
				if(isUserInRole(SigaaPapeis.GESTOR_LATO) ){
					CursoLato c = getGenericDAO().
						findByPrimaryKey(obj.getDiscente().getCurso().getId(), CursoLato.class);
					metodoAvaliacao = c.getPropostaCurso().getMetodoAvaliacao();
				}
				
				
				getCurrentRequest().setAttribute("idAtividade", componenteDAO.findIdByCodigo(codComponente, Unidade.UNIDADE_DIREITO_GLOBAL, NivelEnsino.LATO));

				return selecionarAtividade();
			
			case ALTERACAO_VALIDACAO_LATO_SENSU:
					
					RegistroAtividadeDao rDAO = getDAO(RegistroAtividadeDao.class);
					CursoLato cDAO = getGenericDAO().findByPrimaryKey(obj.getDiscente().getCurso().getId(), CursoLato.class);
					metodoAvaliacao = cDAO.getPropostaCurso().getMetodoAvaliacao();
					Integer idAtividade = componenteDAO.findIdByCodigo(codComponente, Unidade.UNIDADE_DIREITO_GLOBAL, NivelEnsino.LATO);
					/** Carrega a matr�cula do discente na atividade ssetNomeOrientador( obj.getRegistroAtividade().getOrientador().getNome() );
						setIdOrientador( obj.getRegistroAtividade().getOrientador().getIdDocente() );
						etada.  */
					MatriculaComponente matriculaComponente = rDAO.findByDiscenteAtividade(obj.getDiscente().getId(), idAtividade);
					if (matriculaComponente == null) {
						addMensagemErro( "O discente "+obj.getDiscente().getMatriculaNome()+" n�o est� matriculado em atividades acad�micas espec�ficas.");
						return null;
					}
					obj = matriculaComponente;
					/** Carrega o orientador para visualiza��o. */
					if (!isEmpty(obj.getRegistroAtividade().getOrientador())) {
						setNomeOrientador( obj.getRegistroAtividade().getOrientador().getNome() );
						setIdOrientador( obj.getRegistroAtividade().getOrientador().getIdDocente() );
					}
					setAtividadesMBean( (TrabalhoFimCursoMBean) getMBean("trabalhoFimCurso") );
					return telaDadosRegistro();
					
			default:
				addMensagem( MensagensGerais.OPERACAO_ATIVA_INDEFINIDA );
				return cancelar();
		}
	}
	
	/**
	 * Retorna somente as atividades do tipo {@link TipoAtividade#TRABALHO_CONCLUSAO_CURSO & TipoAtividade#ESTAGIO}
	 * Utilizado somente para o caso do n�vel de Gradua��o.
	 * @param matriculas
	 * @return
	 */
	private void populaApenasMatriculasTrabalhoEstagio(){
		Collection<MatriculaComponente> matriculasTrabalhoEstagio = new ArrayList<MatriculaComponente>();
		for (MatriculaComponente m : atividadesMatriculadas) {
			if( m.getComponente().isEstagio() || m.getComponente().isTrabalhoConclusaoCurso() )
				matriculasTrabalhoEstagio.add(m);
		}
		atividadesMatriculadas = new ArrayList<MatriculaComponente>();
		atividadesMatriculadas.addAll(matriculasTrabalhoEstagio);
	}


	/**
	 * Para o caso de discentes stricto, prepara o formul�rio de busca de atividades com algumas op��es
	 * pr�-selecionadas
	 */
	private void prepararSelecaoAtividadesStricto() {
		buscaDepartamento = true;
		obj.getComponente().setUnidade( obj.getDiscente().getGestoraAcademica() );
	}
	

	/**
	 * Retorna o redirecionamento para a tela de busca e sele��o de atividades <br/>
 	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>M�todo n�o invocado por JSP.</li>
	 * </ul> 
	 * @return
	 */
	public String getTelaSelecaoAtividades() {
		return forward(VIEW_BUSCA_ATIVIDADE);
	}

	/**
	 * Prepara os dados necess�rios e redireciona para a tela adequada para informa��o dos 
	 * dados complementares do registro, dependendo do n�vel de ensino.
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException 
	 */
	public String telaDadosRegistro()  {
		if (obj.getDiscente() == null) {
			addMensagemWarning("Aten��o! � necess�rio selecionar um discente para realizar o registro");
			return buscarDiscente();
		}
		
		if (isConsolidacao() || isValidacao() || isAlteracao()) {
			if (obj.getAnoFim() == null || obj.getAnoFim() <= 0)
				obj.setAnoFim(CalendarUtils.getAnoAtual());
			if (obj.getMesFim() == null || obj.getMesFim() <= 0)
				obj.setMesFim(CalendarUtils.getMesAtual()+1);
		}

		if (isValidacao() || isMatricula() || isConsolidacao() || isAlteracao()) {
			if (obj.getMes() == null || obj.getMes() <= 0) {
				obj.setMes(CalendarUtils.getMesAtual()+1);
			}
		}
		
		if (obj.getAno() == null || obj.getAno() <= 0 )
			obj.setAno(Short.parseShort(String.valueOf(CalendarUtils.getAnoAtual())));
		
		if (obj.getAnoInicio() == null || obj.getAnoInicio() <= 0) 
			obj.setAnoInicio(Integer.parseInt(obj.getAno().toString()));
		
		if (obj.getDiscente().isGraduacao() 
			|| obj.getDiscente().isTecnico() 
			|| obj.getDiscente().isFormacaoComplementar())
			return forward(VIEW_DADOS_REGISTRO);
		else if(obj.getDiscente().isStricto())
			return forward(VIEW_DADOS_REGISTRO_STRICTO);
		else if(obj.getDiscente().isResidencia())
			return forward(VIEW_DADOS_REGISTRO_RESIDENCIA_MEDICA);
		else
			return forward(VIEW_DADOS_REGISTRO_LATO);
	}

	/**
	 * Redireciona o usu�rio para tela de resumo do registro de atividade, cada n�vel de ensino tem uma tela de resumo diferente. <br/>
 	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>M�todo n�o invocado por JSP.</li>
	 * </ul> 
	 * @return
	 */
	public String telaResumo() {
		if (obj.getDiscente().isGraduacao() || obj.getDiscente().isTecnico())
			return forward(VIEW_RESUMO);
		else if(obj.getDiscente().isStricto())
			return forward(VIEW_RESUMO_STRICTO);
		else if(obj.getDiscente().isResidencia())
			return forward(VIEW_RESUMO_RESIDENCIA_MEDICA);
		else
			return forward(VIEW_RESUMO_LATO);
	}

	/**
	 * Efetua a busca das atividades acad�micas espec�ficas.<br/>
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/busca_atividade.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String buscarAtividades() {
		ComponenteCurricularDao componenteDao = getDAO(ComponenteCurricularDao.class);

		try {
			// Validar op��es de busca
			ListaMensagens lista = new ListaMensagens();
			validarOpcoesBusca(lista);
			if (!lista.isEmpty()) {
				addMensagens(lista);
				return null;
			}

			if (NivelEnsino.isGraduacao(getNivelEnsino()) &&  isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD,
					SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE)) {
				// Buscar as atividades de acordo com os crit�rios especificados
				atividades = componenteDao.findAtividadesMesmoCursoAndGrau(getNivelEnsino(),
						(isBuscarTodas() ? null : obj.getDiscente().getCurriculo()),
						(isBuscaDepartamento() ? obj.getComponente().getUnidade() : null),
						(isBuscaNomeAtividade() ? obj.getComponente().getNome().trim().toUpperCase() : null),
						(isBuscaTipoAtividade() ? obj.getComponente().getTipoAtividade().getId() : null));
			} else {
				atividades = componenteDao.findAtividades(getNivelEnsino(),
						(getObj().getRegistroAtividade().isMatriculaCompulsoria() || obj.getDiscente().isStricto() ? null : obj.getDiscente().getCurriculo()),
						(isBuscaDepartamento() ? obj.getComponente().getUnidade() : null),
						(isBuscaNomeAtividade() ? obj.getComponente().getNome().trim().toUpperCase() : null),
						(isBuscaTipoAtividade() ? obj.getComponente().getTipoAtividade().getId() : null));
			}
			
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		if (atividades == null || atividades.isEmpty()) {
			addMensagemErro("N�o foram encontradas atividades individuais de acordo com os crit�rios de busca informados");
		}
		return forward(VIEW_BUSCA_ATIVIDADE);
	}

	/**
	 * Testa alguns crit�rios para dizer se deve buscar apenas as atividades do curr�culo do aluno
	 * ou todas as atividades independentemente do curr�culo do aluno, permitindo, neste caso, que atividades equivalentes sejam selecionadas. 
	 * @return
	 */
	private boolean isBuscarTodas() {
		return getObj().getRegistroAtividade().isMatriculaCompulsoria() || getObj().getDiscente().isStricto() || isBuscaNomeAtividade();
	}

	/**
	 * Redireciona o usu�rio para a tela de atividades. Dependo do fluxo, pode redirecionar para a tela
	 * de busca de atividades ou para a tela que lista as atividades em que o discente est� matriculado.
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 *   <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 *   <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String telaAtividades() {
		if ( isEmpty(obj.getDiscente()) ) {
			addMensagemErro("Aten��o! Esta opera��o precisa ser iniciada novamente para que seja poss�vel registrar os dados de forma correta!");
			return cancelar();
		}
		
		if (isConsolidacao() && isPortalDocente())
			return forward(VIEW_ATIVIDADES_ORIENTANDOS);
		else if (isConsolidacao() && consolidarMatriculas)
			return forward("/graduacao/registro_atividade/listar_atividades.jsp");
		else if (isConsolidacao() || isExclusao() || isRenovacao() || isAlteracao() || isAlteracaoGraduacao() )
			return forward(VIEW_SELECAO_ATIVIDADE);
		else if ( isMatricula() || isValidacao() )
			return forward(VIEW_BUSCA_ATIVIDADE);
		else
			return null;
	}
	
	/**
	 * Verifica se os campos selecionados para a busca foram preenchidos
	 *
	 * @param erros
	 */
	private void validarOpcoesBusca(ListaMensagens erros) {
		if (isBuscaDepartamento() && obj.getComponente().getUnidade().getId() <= 0) {
			erros.addErro("Informe o departamento que oferece a atividade");
		}
		String nomeAtividade = obj.getComponente().getDetalhes().getNome();
		if (isBuscaNomeAtividade() && (nomeAtividade == null || "".equals(nomeAtividade.trim()))) {
			erros.addErro("Informe o nome da atividade");
		}
		if (isBuscaTipoAtividade() && obj.getComponente().getTipoAtividade().getId() <= 0) {
			erros.addErro("Informe o tipo da atividade");
		}
	}

	/**
	 * Buscar os dados da atividade selecionada. Utilizado para matr�culas e
	 * valida��es.
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/busca_atividade.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarAtividade() throws ArqException {
		
		clearDadosRegistroAtividade();
		
		Integer idAtividade = getParameterInt("idAtividade");
		if(idAtividade == null)
			idAtividade = (Integer) getCurrentRequest().getAttribute("idAtividade");

		try {
			// Buscar componente selecionado
			GenericDAO dao = getGenericDAO();
			ComponenteCurricular atividade = dao.findByPrimaryKey(idAtividade, ComponenteCurricular.class);
			if (atividade == null) {
				addMensagemErro("A atividade selecionada n�o � v�lida");
				return null;
			} else {
				atividade.setUnidade( dao.refresh(atividade.getUnidade()) );
			}

			/** secret�rio de coordena��o de curso s� pode validar/consolidar atividades SEM NOTA */
			if( isUserInRole(SigaaPapeis.SECRETARIA_COORDENACAO) ){
				if( operacao.equals(OperacaoRegistroAtividade.CONSOLIDACAO) || operacao.equals(OperacaoRegistroAtividade.VALIDACAO) ){
					if( atividade.isNecessitaMediaFinal() ){
						throw new NegocioException("Secret�rios de coordena��o de curso s� podem consolidar e validar atividades acad�micas espec�ficas sem nota.");
					}
				}
			}
			
			// Valida��es para discentes stricto
			if (obj.getDiscente().isStricto()) {
				ListaMensagens lista = new ListaMensagens();
				validarMatriculaStricto(atividade, lista);
				if (!lista.isEmpty()) {
					addMensagens(lista);
					return telaAtividades();
				}
			}
			
			if(obj.getDiscente().isGraduacao()){
				ListaMensagens lista = new ListaMensagens();
				String nomeAtividade = atividade.getNome();
				if (obj.getDiscente().isRegular() && !obj.getRegistroAtividade().isMatriculaCompulsoria() 
						&& !isUserInRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO)) 
					RegistroAtividadeValidator.validarCurriculoEquivalencia(obj.getDiscente(), atividade, lista);
				if (!lista.isEmpty()) {
					addMensagens(lista);
					obj.getComponente().getDetalhes().setNome(nomeAtividade);
					return telaAtividades();
				}
			}

			return prepararRegistro(atividade);

		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}
	}

	/**
	 * Valida a matr�cula de discentes stricto na atividade selecionada
	 * 
	 * @param atividade
	 * @param erros 
	 * @throws DAOException
	 */
	private void validarMatriculaStricto(ComponenteCurricular atividade, ListaMensagens lista) throws DAOException {
		DiscenteAdapter discente = obj.getDiscente();
		if (atividade.isQualificacao()) {
			MatriculaStrictoValidator.isPassivelSolicitacaoQualificacao(discente, lista);
		}
		if (atividade.isTese()) {
			MatriculaStrictoValidator.isPassivelSolicitacaoDefesa(discente, lista);
		}
	}

	/**
	 * Valida se a atividade selecionada � v�lida para o fluxo atual 
	 * e prepara os dados para que seja populado o formul�rio para o usu�rio.
	 * 
	 * @param atividade
	 * @return
	 * @throws DAOException
	 */
	private String prepararRegistro(ComponenteCurricular atividade ) throws DAOException{
		ListaMensagens lista = new ListaMensagens();

		RegistroAtividadeValidator.validarAtividade(atividade, lista);

		// Verificar duplicidade no registro da atividade
		if (OperacaoRegistroAtividade.MATRICULA.equals(operacao) || OperacaoRegistroAtividade.VALIDACAO.equals(operacao) || OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU.equals(operacao)) {
			RegistroAtividadeValidator.verificarDuplicidade(obj.getDiscente(), atividade, lista);
		}

		// Verificar se a atividade selecionada pode ser validada
		if (OperacaoRegistroAtividade.VALIDACAO.equals(operacao)) {
			populaMetodoAvaliacao( atividade );
			RegistroAtividadeValidator.isPassivelValidacao(atividade, lista);
		}

		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}

		obj.setComponente(atividade);
		obj.setDetalhesComponente(atividade.getDetalhes());
		obj.getRegistroAtividade().setMatricula(obj);

		if (!atividade.isNecessitaMediaFinal()) {
			obj.setSituacaoMatricula(new SituacaoMatricula());
		}

		if (isEstagio()) {
			setAtividadesMBean((EstagioMBean) getMBean("estagio"));
		} else if (isTrabalhoFimCurso()) {
			setAtividadesMBean((TrabalhoFimCursoMBean) getMBean("trabalhoFimCurso") );
		}

		return telaDadosRegistro();
	}

	/**
	 * Valida os dados e apresenta esses na tela de confirma��o dos dados
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 *   <li>/sigaa.war/lato/registro_atividade/dados_registro.jsp</li>
	 *   <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 * </ul> 
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String verConfirmacao() throws NegocioException, ArqException {
		RegistroAtividade registro = getObj().getRegistroAtividade();
		GenericDAO dao = getGenericDAO();

		chDedicada = obj.getComponente().getDetalhes().getChDedicadaDocente();
		
		validarDadosAtividade(erros);
		
		if (hasErrors()) {
			return null;
		}
		
		if (operacao.equals(OperacaoRegistroAtividade.VALIDACAO) || operacao.equals(OperacaoRegistroAtividade.MATRICULA) 
				|| (operacao.equals(OperacaoRegistroAtividade.CONSOLIDACAO) && isMigrado()) || operacao.equals(OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU) 
				|| operacao.equals(OperacaoRegistroAtividade.ALTERACAO_VALIDACAO_LATO_SENSU) || operacao.equals(OperacaoRegistroAtividade.ALTERACAO_GRADUACAO)) {
			// Buscar dados do orientador
			if ((isGraduacao() || isLato() || isTecnico()) && !isEmpty(idOrientador)) {
				
				// S� valida se for Gradua��o ou T�cnico
				RegistroAtividadeValidator.validarCHDedicadaDocente(chDedicada, obj, erros);
				
				if( !isEmpty(idCoOrientador) && idOrientador.equals(idCoOrientador))
					addMensagemErro("O coorientador deve ser diferente do orientador.");
				
				if(hasErrors()){ 
					return null;
				}	
				
				// Cria Orienta��o para esta atividade
				populaOrientacaoAtividade(registro);
			}
			
			populaCoordenador(registro);
			
			// Validar dados
			if( !registro.isMatriculaCompulsoria() )
				RegistroAtividadeValidator.validarDadosMatricula( getUsuarioLogado(), registro.getMatricula(), isValidacao(), operacao, erros);
			
			if (hasErrors()) {
				return null;
			}
		}
		
		if (operacao.equals(OperacaoRegistroAtividade.CONSOLIDACAO) || operacao.equals(OperacaoRegistroAtividade.VALIDACAO) || operacao.equals(OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU) || operacao.equals(OperacaoRegistroAtividade.ALTERACAO_VALIDACAO_LATO_SENSU)) {

			if ( !isInformarDocentesEnvolvidos() && isDispensa() )
				obj.setSituacaoMatricula( SituacaoMatricula.APROVEITADO_DISPENSADO );

			RegistroAtividadeValidator.validarDadosConsolidacao(obj, getUsuarioLogado(), isValidacao(), erros);
			if (hasErrors()) {
				return null;
			}
			
			if ( obj.getComponente().isNecessitaMediaFinal() ) {

				// Verificar se foi marcada a dispensa
				if ( isInformarDocentesEnvolvidos() || !isDispensa() ) {
					consolidarNota(dao);
				}

			} else {
				obj.setSituacaoMatricula( dao.refresh(obj.getSituacaoMatricula()) );
			}

		}
		
		if (operacao.equals(OperacaoRegistroAtividade.CONSOLIDACAO))
				prepareMovimento(SigaaListaComando.CONSOLIDAR_ATIVIDADE);

		
		return telaResumo();

	}

	/** Popula o m�todo de avalia��o da institui��o para o controle dos campos nota e conceito */
	private void populaMetodoAvaliacao(ComponenteCurricular componente) throws DAOException {
		
		/** caso seja curso de lato sensu o metodo de avalia��o est� associado diretamente ao curso */
		if(operacao.equals(OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU) ){
			obj.setMetodoAvaliacao( metodoAvaliacao );
		}else{
			ParametrosGestoraAcademica param = 
					ParametrosGestoraAcademicaHelper.getParametros( componente );
			if( !isEmpty(param) ){
				obj.setMetodoAvaliacao( param.getMetodoAvaliacao() );
			}
		}

	}

	/**
	 * Este m�todo defini o coordenador da atividade, caso tenha sido informado na view. 
	 * @param registro
	 * @throws DAOException
	 */
	private void populaCoordenador(RegistroAtividade registro)	throws DAOException {
		
		if ((isGraduacao() || isTecnico()) && !isAllEmpty( registro.getCoordenador() ) ) {
			GenericDAO dao = getGenericDAO();
			Servidor coor = new Servidor();
			coor = dao.findByPrimaryKey(registro.getCoordenador().getId(), Servidor.class );
			registro.setCoordenador(coor);
		}
		
	}

	/**
	 * Com base dos dados informados na view, cria o registro da Orienta��o
	 * 
	 * @param registro
	 * @throws DAOException
	 */
	private void populaOrientacaoAtividade(RegistroAtividade registro) throws DAOException {
		ListaMensagens erros = new ListaMensagens();
		GenericDAO dao = getGenericDAO();

		DocenteExterno orientadorExterno = null;
		Servidor orientadorInterno = null;
		
		DocenteExterno coorientadorExterno = null;
		Servidor coorientadorInterno = null;
		
		if( !isEmpty(idOrientador) ){
			String tipoDocente = getParameter("tipoAjaxDocente_1");
			if( "externo".equals(tipoDocente) || "externoLato".equals(tipoDocente) ){ 
				// Se for docente externo 
				orientadorExterno = new DocenteExterno();
				orientadorExterno = dao.findByPrimaryKey(idOrientador, DocenteExterno.class);
			} else{
				// No caso de atividades de est�gio apenas Servidores podem ser orientadores
				orientadorInterno = new Servidor();
				orientadorInterno = dao.findByPrimaryKey(idOrientador, Servidor.class);
			}
		}else if( !isEmpty(registro) ){
			boolean isDocente = !isEmpty(registro.getOrientador());
			if(isDocente && registro.getOrientador().isServidor()){
				orientadorInterno = new Servidor();
				orientadorInterno = dao.findByPrimaryKey(registro.getOrientador().getOrientador().getId(), Servidor.class);
			}else{
				orientadorExterno = new DocenteExterno();
				orientadorExterno =  dao.findByPrimaryKey(registro.getOrientador().getOrientadorExterno().getId(), DocenteExterno.class);
			}	
		}
		
		if( !isEmpty(idCoOrientador) ){
			String tipoDocente = getParameter("tipoAjaxDocente_2");
			if( "externo".equals(tipoDocente) || "externoLato".equals(tipoDocente) ){ 
				// Se for docente externo 
				coorientadorExterno = new DocenteExterno();
				coorientadorExterno = dao.findByPrimaryKey(idCoOrientador, DocenteExterno.class);
			} else{
				// No caso de atividades de est�gio apenas Servidores podem ser orientadores
				coorientadorInterno = new Servidor();
				coorientadorInterno = dao.findByPrimaryKey(idCoOrientador, Servidor.class);
			}
		}else if( !isEmpty(registro) && !isEmpty(registro.getCoOrientador()) ){
			if(registro.getCoOrientador().isServidor()){
				coorientadorInterno = new Servidor();
				coorientadorInterno = dao.findByPrimaryKey(registro.getCoOrientador().getOrientador().getId(), Servidor.class);
			}else{
				coorientadorExterno = new DocenteExterno();
				coorientadorExterno =  dao.findByPrimaryKey(registro.getCoOrientador().getOrientadorExterno().getId(), DocenteExterno.class);
			}	
			
		}
	
		//Caso o orientador selecionado n�o exista
		if(orientadorInterno == null && orientadorExterno == null)
			addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO,"Orientador");
		
		if (erros.size() > 0) 
			telaDadosRegistro();

		if ( isEmpty(chDedicada) ) 
			chDedicada = obj.getComponente().getDetalhes().getChDedicadaDocente();
		
		if( !isEmpty(orientadorExterno) || !isEmpty(orientadorInterno) ){
			//Atualiza o orientador e a carga hor�ria
			if( operacao.equals(OperacaoRegistroAtividade.ALTERACAO_GRADUACAO) || operacao.equals(OperacaoRegistroAtividade.ALTERACAO_VALIDACAO_LATO_SENSU) ){
				if( !isEmpty(orientadorInterno) ){ 
					if ( registro.getOrientador() != null ) {
						registro.getOrientador().setOrientador( orientadorInterno );
						registro.getOrientador().setOrientadorExterno( null );
					} else {
						registro.setOrientacoesAtividade(new HashSet<OrientacaoAtividade>());
						OrientacaoAtividade oAtiv = new OrientacaoAtividade();
						oAtiv.setOrientador(orientadorInterno);
						oAtiv.setOrientadorExterno(null);
						oAtiv.setRegistroAtividade(registro);
						registro.getOrientacoesAtividade().add(oAtiv);
					}
					
				}else if( !isEmpty(orientadorExterno) ){
					if ( registro.getOrientador() != null ) {
						registro.getOrientador().setOrientadorExterno( orientadorExterno );
						registro.getOrientador().setOrientador( null );
						idOrientador = null;
					} else {
						registro.setOrientacoesAtividade(new HashSet<OrientacaoAtividade>());
						OrientacaoAtividade oAtiv = new OrientacaoAtividade();
						oAtiv.setOrientador(null);
						oAtiv.setOrientadorExterno(orientadorExterno);
						oAtiv.setRegistroAtividade(registro);
						registro.getOrientacoesAtividade().add(oAtiv);
						idOrientador = null;
					}
				}
				
				if( !isEmpty(coorientadorInterno) ){
					if( registro.getCoOrientador() == null ){
						OrientacaoAtividade oAtiv = new OrientacaoAtividade();
						oAtiv.setOrientador(coorientadorInterno);
						oAtiv.setOrientadorExterno(null);
						oAtiv.setRegistroAtividade(registro);
						oAtiv.setTipo(OrientacaoAtividade.COORIENTADOR);
						registro.getOrientacoesAtividade().add(oAtiv);
					}					
					registro.getCoOrientador().setOrientador( coorientadorInterno );
					registro.getCoOrientador().setOrientadorExterno( null );
				}else if( !isEmpty(coorientadorExterno) ){
					if( registro.getCoOrientador() == null ){
						OrientacaoAtividade oAtiv = new OrientacaoAtividade();
						oAtiv.setOrientador(null);
						oAtiv.setOrientadorExterno(coorientadorExterno);
						oAtiv.setRegistroAtividade(registro);
						oAtiv.setTipo(OrientacaoAtividade.COORIENTADOR);
						registro.getOrientacoesAtividade().add(oAtiv);
					}					
					registro.getCoOrientador().setOrientadorExterno( coorientadorExterno );
					registro.getCoOrientador().setOrientador( null );
					idCoOrientador = null;
				}
				
				if( !isEmpty(coorientadorExterno) || !isEmpty(coorientadorInterno) ){
					registro.getOrientador().setCargaHoraria(chDedicada/2);
					registro.getCoOrientador().setCargaHoraria(chDedicada/2);
				} else {
					registro.getOrientador().setCargaHoraria(chDedicada);
				}
			//Adiciona uma nova orienta��o	
			}else{	
				registro.setOrientacoesAtividade(new HashSet<OrientacaoAtividade>());
				registro.addOrientador(orientadorInterno, orientadorExterno, chDedicada, coorientadorInterno, coorientadorExterno);
			}	
			setNomeOrientador( registro.getOrientador().getNome() );
			setIdOrientador( registro.getOrientador().getIdDocente() );
			setChDedicada( registro.getOrientador().getCargaHoraria() );
			if( !isEmpty(coorientadorExterno) || !isEmpty(coorientadorInterno) ){
				setNomeCoOrientador( registro.getCoOrientador().getNome() );
				setIdCoOrientador( registro.getCoOrientador().getIdDocente() );
			}
		}
		

	}
	
	/**
	 * Carrega as atividades matriculadas do docente 
	 * de acordo com o papel do usu�rio e a opera��o.
	 * @throws DAOException 
	 */
	private void populaAtividadesMatriculadas() throws DAOException{
		
		atividadesMatriculadas = null;
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		if (isUserInRole(SigaaPapeis.PPG) && operacao.equals(OperacaoRegistroAtividade.ALTERACAO_PERIODO)){
			atividadesMatriculadas = matriculaDao.findAtividades(obj.getDiscente(), SituacaoMatricula.MATRICULADO, SituacaoMatricula.APROVADO,
					SituacaoMatricula.APROVEITADO_CUMPRIU, SituacaoMatricula.APROVEITADO_DISPENSADO, SituacaoMatricula.APROVEITADO_TRANSFERIDO);
		} else if ( !operacao.equals(OperacaoRegistroAtividade.CONSOLIDACAO) && isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE)){
			atividadesMatriculadas = matriculaDao.findAtividades(obj.getDiscente(), 
					SituacaoMatricula.MATRICULADO, SituacaoMatricula.APROVADO);
		}else {
			atividadesMatriculadas = matriculaDao.findAtividades(obj.getDiscente(), SituacaoMatricula.MATRICULADO);
		}
			
	}

	/**
	 * Valida os dados informados na view
	 * 
	 * @param erros
	 * @throws DAOException 
	 */
	private void validarDadosAtividade(ListaMensagens erros) throws DAOException {
		
		CalendarioAcademico calendarioAtual = null;
		
		if (obj != null && obj.getDiscente() != null)
			calendarioAtual = CalendarioAcademicoHelper.getCalendario(obj.getDiscente());
		else 
			calendarioAtual = getCalendarioVigente();
		
		int periodo = 0;
		ValidatorUtil.validateRequired(obj.getAno(), "Ano", erros);
		if(!isEmpty(obj.getAno()) && obj.getAno() < 1900)
			erros.addErro("Ano Inv�lido: favor informar um ano superior a 1900.");
			
		try {
			periodo = Integer.parseInt(String.valueOf(obj.getPeriodo()));
			if (periodo < 1 || periodo > 4 ){
				erros.addErro("Per�odo inv�lido: deve ser fornecido um per�odo entre 1 e 4.");
			}
			if (!hasOnlyErrors() && (obj.getAno().intValue() < obj.getDiscente().getAnoIngresso().intValue()) || 
					(obj.getAno().intValue() == obj.getDiscente().getAnoIngresso().intValue() && 
							(obj.getPeriodo() == null ? periodo : obj.getPeriodo()) < obj.getDiscente().getPeriodoIngresso().intValue()) ) {
				erros.addErro("Ano-Per�odo: n�o pode ser inferior a de ingresso do Discente.");
			}	
		} catch(Exception e) {
			erros.addErro("Per�odo: Campo obrigat�rio n�o informado");
		}
		
		/**
		 * se for atividade complementar ou trabalho final Lato Sensu e tiver com o booleano temOrientador true � OBRIGATORIO informar o orientador
		 */
		if( (isValidacao() || isMatricula() )
				&& (obj.getComponente().isAtividadeComplementar() || operacao.equals(OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU)) 
				&& obj.getComponente().isTemOrientador()
				&& isEmpty(idOrientador)){
			erros.addErro("� obrigat�rio informar o orientador.");
		}
		
		// No caso de gradua��o, o ano de in�cio ser� o ano do per�odo.
		// caso contr�rio (stricto sensu), ser� verificado o ano.
		if (isGraduacao()) {
			if(obj.getAno() != null) obj.setAnoInicio((int) obj.getAno());
		} else if (obj.getAnoInicio() != null){
			if ((obj.getAnoInicio().intValue() < obj.getDiscente().getAnoIngresso().intValue()) || 
					(obj.getAnoInicio().intValue() == obj.getDiscente().getAnoIngresso().intValue() && 
							(obj.getPeriodo() == null ? periodo : obj.getPeriodo()) < obj.getDiscente().getPeriodoIngresso().intValue()) ){
				erros.addErro("Data de In�cio: n�o pode ser inferior a de ingresso do Discente.");
			}else if (!isConsolidacao() && !isEmpty(obj.getAno()) && obj.getAno().intValue() > obj.getAnoInicio().intValue()){
				erros.addErro("Data de In�cio: n�o pode ser inferior ao Ano-Per�odo.");
			}else if ((obj.getAno() > calendarioAtual.getAno()) || 
					(obj.getAno().intValue() == calendarioAtual.getAno() && 
							obj.getPeriodo() != null && (obj.getPeriodo() == null ? periodo : obj.getPeriodo()) > calendarioAtual.getPeriodo()) ){
				erros.addErro("Ano-Per�odo: n�o pode ser superior ao semestre vigente.");
			}
		}
		
		if (operacao.equals(OperacaoRegistroAtividade.ALTERACAO_PERIODO) || operacao.equals(OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU)){
			if ((obj.getAnoInicio().intValue() > obj.getAnoFim().intValue()) || 
					(obj.getAnoInicio().intValue() == obj.getAnoFim().intValue() && obj.getMes().intValue() > obj.getMesFim().intValue())){
				erros.addErro("Data de In�cio: n�o pode ser maior que a Data Final");
			}
		}
	}

	/**
	 * Realizar os c�lculos necess�rios para a consolida��o da nota do discente na atividade, quando 
	 * o componente precisa de m�dia final (caso n�o precise, o resultado � apenas aprovado ou reprovado).
	 * 
	 * @param dao
	 * @throws DAOException
	 */
	private void consolidarNota(GenericDAO dao) throws DAOException {
		ValidatorUtil.validateRequired(obj.getMediaFinal(), "M�dia Final", erros);
		if (!hasErrors()) { 
			
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(obj.getRegistroAtividade().getAtividade());
						
			if (obj.getDiscente().getDiscente().isRegular()) {
				String[] campos = {"curso.id","ativa"};
				Object[] valores = {obj.getDiscente().getCurso().getId(),true};			
			}
			
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			EstrategiaConsolidacao estrategia = factory.getEstrategia(obj.getDiscente(), param); 
			
			obj.setEstrategia(estrategia);
			
			populaMetodoAvaliacao( obj.getRegistroAtividade().getAtividade() );
			// 100% de frequencia na consolida��o de TCC
			obj.setPorcentagemFrequencia(100.0);
			obj.consolidar();
			
			obj.setSituacaoMatricula(dao.findByPrimaryKey(obj.getSituacaoMatricula().getId(), SituacaoMatricula.class));
		}
	}

	/**
	 * Confirma os dados do registro, persistindo as informa��es cadastradas pelo usu�rio. 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {

		if ( isEmpty(obj.getDiscente()) ) {
			addMensagemErro("Aten��o! Esta opera��o precisa ser iniciada novamente para que seja poss�vel registrar os dados de forma correta!");
			return cancelar();
		}
		
		if( isNecessarioConfirmacaoSenha() ){
			if( !confirmaSenha() )
				return null;
		}

		verificarPermissoes();

		persisteRegistroAtividade();
		
		if( hasErrors() )
			return null;

		if ( atividadesMBean != null ) {
			
			if ( !isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) 
				&& atividadesMBean.getClass().equals(EstagioMBean.class) ){
				atividadesMBean.afterAtualizar();
				return null;
			}
				
			atividadesMBean.resetBean();
		}

		return forward("/graduacao/registro_atividade/confirmacao.jsf");
	}

	/** Invoca o processado para persistir o Registro de Atividade.
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void persisteRegistroAtividade() throws DAOException, ArqException {
		RegistroAtividadeMov mov = new RegistroAtividadeMov();
		mov.setMatricula(obj);
		mov.setRegistroTutor( isTutorEad() );
		mov.setOrientador( isOrientadorAtividade() );

		if ( !isExclusao() && !isDispensa() && hasOrientacoes() 
				&& obj.getRegistroAtividade().isNecessariaDefinicaoProducaoIntelectual() ) {
			if (isEstagio())
				mov.setEstagio((Estagio) getAtividadesMBean().getObj());
			else if (isTrabalhoFimCurso())
				mov.setTrabalhoFimCurso( (TrabalhoFimCurso) getAtividadesMBean().getObj());
		} 

		try {
			switch(operacao) {
				case MATRICULA:
					mov.setCodMovimento(SigaaListaComando.MATRICULAR_ATIVIDADE);
					break;
				case CONSOLIDACAO:
					mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_ATIVIDADE);
					break;
				case VALIDACAO:
				case VALIDACAO_LATO_SENSU:
					mov.setCodMovimento(SigaaListaComando.VALIDAR_ATIVIDADE);
					break;
				case EXCLUSAO:
					mov.setCodMovimento(SigaaListaComando.EXCLUIR_MATRICULA_ATIVIDADE);
					break;
				case RENOVACAO:
					mov.setCodMovimento(SigaaListaComando.RENOVAR_MATRICULA_ATIVIDADE);
					break;
				case ALTERACAO_PERIODO:
					mov.setCodMovimento(SigaaListaComando.ALTERAR_PERIODO_ATIVIDADE);
					break;		
				case ALTERACAO_VALIDACAO_LATO_SENSU:
				case ALTERACAO_GRADUACAO:
					mov.setCodMovimento(SigaaListaComando.ALTERAR_ATIVIDADE_GRADUACAO);
					break;			
			}
			execute(mov, getCurrentRequest());

			// Recarrega os dados do discente para exibir na tela de confirma��o
			obj.setDiscente(getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()));			
			addMessage(getDescricaoOperacao() +
					" para " + obj.getComponenteDescricaoResumida() +
					" do(a) aluno(a) " + obj.getDiscente().getNome() +
					" realizada com sucesso!",
					TipoMensagemUFRN.INFORMATION);

		} catch (NegocioException e) {
			tratamentoErroPadrao(e , e.getMessage());
		}
	}

	/**
	 * Verifica as permiss�es do usu�rio para executar as opera��es 
	 * de registro de atividades acad�micas espec�ficas. 
	 * 
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	private void verificarPermissoes() throws SegurancaException, DAOException {
		if (!isTutorEad() && !isOrientadorAtividade() ) {
			checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.DAE,
					SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS,
					SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO,
					SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA,
					SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR});
		}
	}

	/**
	 * No caso de est�gios e trabalhos de fim de curso, � necess�rio cadastrar as informa��es
	 * da produ��o intelectual do orientador.
	 * Este m�todo valida os dados informados e redireciona o usu�rio para o formul�rio
	 * correspondente � produ��o intelectual.	 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String verDadosProducaoIntelectual() throws ArqException {

		if( isNecessarioConfirmacaoSenha() ){
			if( !confirmaSenha() )
				return null;
		}
		

		if ( operacao.equals(OperacaoRegistroAtividade.MATRICULA) || operacao.equals(OperacaoRegistroAtividade.EXCLUSAO) || isDispensa() 
				|| (!obj.getRegistroAtividade().isNecessariaDefinicaoProducaoIntelectual() && !operacao.equals(OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU ))) {
			return confirmar();
		}else if(operacao.equals(OperacaoRegistroAtividade.ALTERACAO_GRADUACAO)){
			if( isEmpty(getAtividadesMBean().getObj()) )
				return confirmar();
			if ( isTrabalhoFimCurso() ) {
				TrabalhoFimCursoMBean mbean = (TrabalhoFimCursoMBean) getAtividadesMBean();
				mbean.getObj().setServidor( obj.getRegistroAtividade().getOrientador().getOrientador() );
				mbean.setDiscente( obj.getDiscente() );
				mbean.setOrientador( mbean.getObj().getServidor() );
			}else if ( isEstagio() ) {
				EstagioMBean mbean = (EstagioMBean) getAtividadesMBean();
				mbean.getObj().setServidor( obj.getRegistroAtividade().getOrientador().getOrientador() );
				mbean.setDiscente( obj.getDiscente() );
			}
			getAtividadesMBean().setConfirmButton("Alterar");
			return getAtividadesMBean().atualizarParaGraduacao();
		} else if ( hasOrientacoes() ) {
					
			GenericDAO dao = getGenericDAO();
			String instituicaoEnsino = (dao.findByPrimaryKey(InstituicoesEnsino.UFRN, InstituicoesEnsino.class)).getNome();

			CalendarioAcademicoDao calendarioDao = getDAO(CalendarioAcademicoDao.class);
			CalendarioAcademico calendario = calendarioDao.findByAnoPeriodo(obj.getAno().intValue(), obj.getPeriodo().intValue(),
					dao.refresh(obj.getDiscente().getGestoraAcademica()).getId(),
					obj.getComponente().getNivel());

			// Popular dados j� conhecidos do est�gio
			if (isEstagio()) {
				Estagio estagio = (Estagio) getAtividadesMBean().getObj();
				estagio.setDiscenteExterno(false);
				estagio.setAluno(obj.getDiscente().getDiscente());
				estagio.setServidor( obj.getRegistroAtividade().getOrientacoesAtividade().iterator().next().getOrientador() );
				estagio.setInstituicao( instituicaoEnsino );
				// �rea de conhecimento
				if ( obj.getDiscente().getCurso() != null && obj.getDiscente().getCurso().getAreaCurso() != null ) {
					Curso curso = obj.getDiscente().getCurso();
					curso = dao.refresh( curso );
					estagio.setArea( curso.getAreaCurso().getGrandeArea() );
				}
//				estagio.setNomeProjeto(null);
//				estagio.setSubArea(null);
//				estagio.setEntidadeFinanciadora(null);
				// Datas
				estagio.setDepartamento( estagio.getServidor().getUnidade() );
				if (calendario != null) {
					estagio.setPeriodoInicio(calendario.getInicioPeriodoLetivo());
					estagio.setPeriodoFim(calendario.getFimPeriodoLetivo());
				}

				return getAtividadesMBean().preCadastrarParaGraduacao();

				//	Popular dados j� conhecidos do trabalho final de curso
			} else if (isTrabalhoFimCurso()){

				TrabalhoFimCursoMBean trabalhoMBean = (TrabalhoFimCursoMBean) getAtividadesMBean();
				TrabalhoFimCursoDao trabalhoDao = getDAO(TrabalhoFimCursoDao.class);
			
				TrabalhoFimCurso trabalho = null;
				
				if( obj.getId() > 0 ){
					trabalho = trabalhoDao.findByMatricula( obj.getId() );
				}
				
				if(trabalho == null){
					trabalhoMBean.clear();
					trabalho = trabalhoMBean.getObj();
				}
				
				trabalho.iniciarAtributosTransient();
				
				//Popula os atributos do TCC alterados no formul�rio dos dados do registro.
				trabalho.setOrientando( obj.getDiscente().getDiscente() );
				trabalho.setServidor( obj.getRegistroAtividade().getOrientador().getOrientador() );
				trabalho.setDocenteExterno( obj.getRegistroAtividade().getOrientador().getOrientadorExterno() );
				trabalho.setAno( new Integer(obj.getAno()));
				trabalho.setTitulo( StringUtils.unescapeHTML(trabalho.getTitulo()) );
				
				if ( isEmpty(trabalho) ){
					trabalho.setOrientacao( TrabalhoFimCurso.ORIENTADOR );
					trabalho.setDiscenteExterno(false);
					trabalho.setIes(trabalhoDao.findByPrimaryKey(InstituicoesEnsino.UFRN, InstituicoesEnsino.class));
					if( !isEmpty(calendario) )
						trabalho.setDataInicio( calendario.getInicioPeriodoLetivo() );
				}

				trabalho.setDepartamento( !isEmpty( trabalho.getServidor() ) ? trabalho.getServidor().getUnidade() : trabalho.getDocenteExterno().getUnidade() );
				if ( isEmpty( trabalho.getDepartamento() ) ){
					addMensagemErro("O docente n�o est� vinculado a um departamento. " +
							"Procure a administra��o do sistema para a atualiza��o.");
					return cancelar();
				}
				
				if ( !isEmpty( obj.getDiscente().getCurso() ) 
						&& !isEmpty( obj.getDiscente().getCurso().getAreaCurso() ) ) {
					Curso curso = obj.getDiscente().getCurso();
					curso = trabalhoDao.refresh( curso );
					trabalho.setArea( curso.getAreaCurso().getGrandeArea() );
				}
				
				trabalhoMBean.setObj( trabalho );
				
				if( operacao.equals(OperacaoRegistroAtividade.ALTERACAO_VALIDACAO_LATO_SENSU) ) {
					getAtividadesMBean().setConfirmButton("Alterar");
					return getAtividadesMBean().atualizarParaGraduacao();
				} else if (isGraduacao() && obj.isReprovado()) {
					return confirmar();
				} else {	
					return getAtividadesMBean().preCadastrarParaGraduacao();
				}
			}

		}
		return confirmar();
	}
	
	/**
	 * Verifica se o registro possui orienta��es associadas a ele.
	 * 
	 * @return
	 */
	private boolean hasOrientacoes() {		
		return (obj.getComponente().isTemOrientador() && obj.getRegistroAtividade().getOrientacoesAtividade() != null 
					&& !obj.getRegistroAtividade().getOrientacoesAtividade().isEmpty()) || obj.getComponente().isLato();
	}

	/**
	 * Busca os dados da matr�cula em atividade selecionada. Utilizado para
	 * consolida��es e exclus�es de registros em atividades.
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/selecao_atividade.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String selecionarMatriculaAtividade() throws DAOException, NegocioException {
		
		clearTransientObjectsFor();
		obj = getGenericDAO().findAndFetch(getParameterInt("id"), MatriculaComponente.class,
					"componente, situacaoMatricula, registroAtividade, curso" );
		obj.getComponente().getDetalhes().getCodigo();
		obj.getComponenteDescricao();
		obj.setDiscente(getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()));
		
		/** secret�rio de coordena��o de curso s� pode validar/consolidar atividades SEM NOTA */
		if( isUserInRole(SigaaPapeis.SECRETARIA_COORDENACAO) ){
			if( operacao.equals(OperacaoRegistroAtividade.CONSOLIDACAO) || operacao.equals(OperacaoRegistroAtividade.VALIDACAO) ){
				if( obj.getComponente().isNecessitaMediaFinal() ){
					throw new NegocioException("Secret�rios de coordena��o de curso s� podem consolidar e validar atividades acad�micas espec�ficas sem nota.");
				}
			}
		}
		// popula o m�todo de avalia��o da institui��o para o controle dos campos nota e conceito
		populaMetodoAvaliacao( obj.getComponente() );

		switch(operacao) {
			case CONSOLIDACAO:
				// verifica se a atividade de qualifica��o ou defesa possui banca cadastrada.
				if ( obj.getDiscente().isStricto() && operacao.equals(OperacaoRegistroAtividade.CONSOLIDACAO)
						&& (obj.getComponente().isQualificacao()
							|| obj.getComponente().isTese())) {
					// recupera as bancas cadastradas no tipo de atividade 
					BancaPosDao bpDao = getDAO(BancaPosDao.class);
					BancaPos banca = bpDao.findByMatriculaComponente(obj);
					if (banca == null){
						addMensagemErro("N�o � poss�vel consolidar a atividade sem cadastro da Banca de "+
								(obj.getComponente().isQualificacao() ? "qualifica��o." : "defesa."));
						return null;
					} else if (banca.isPendente()) {
						addMensagemErro("N�o � poss�vel consolidar a atividade porque a Banca de " + (obj.getComponente().isQualificacao() ? "qualifica��o" : "defesa") + " est� pendente de aprova��o pela coordena��o.");
						return null;
					}
				}
			case RENOVACAO:
				if (isEstagio())
					setAtividadesMBean((EstagioMBean) getMBean("estagio"));
				else if (isTrabalhoFimCurso())
					setAtividadesMBean((TrabalhoFimCursoMBean) getMBean("trabalhoFimCurso") );

				if ( obj.getRegistroAtividade() == null ) {
					// Criar novo registro de atividade
					setMigrado(true);
					obj.setRegistroAtividade( new RegistroAtividade() );
					obj.getRegistroAtividade().setCoordenador(new Servidor());
					obj.getRegistroAtividade().setMatricula(obj);
				} else {
					// Carregar dados gravados
					obj.getRegistroAtividade().getDocentesNomes();
				}

				// Inicializar proxy
				obj.setSituacaoMatricula( (SituacaoMatricula) HibernateUtils.getTarget(obj.getSituacaoMatricula()) );

				if ( isConsolidacao() ) {
					return telaDadosRegistro();
				} else if (isRenovacao()) {
					calendario = CalendarioAcademicoHelper.getCalendario(obj.getDiscente());  
					RegistroAtividadeValidator.validarRenovacao(obj, erros);
					return hasErrors() ? null : telaResumo();
				}

			case EXCLUSAO:
				if (isGraduacao()) {
					calendario = CalendarioAcademicoHelper.getCalendario(obj.getDiscente());  
					RegistroAtividadeValidator.validaUnicaMatricula(obj, obj.getDiscente(), getAcessoMenu().isCoordenadorCursoGrad(), erros);
					if (hasErrors() && getAcessoMenu().isCoordenadorCursoGrad())
						return null;
				}	
				obj.setSituacaoMatricula( SituacaoMatricula.EXCLUIDA );
				return telaResumo();
			case ALTERACAO_GRADUACAO:		
					GenericDAO dao = getGenericDAO();
				
					if (isEstagio()){
						EstagioMBean estagioMBean = getMBean("estagio");
						estagioMBean.setObj(dao.findByExactField(Estagio.class, "matricula.id", obj.getId(), true));
						if( !isEmpty(estagioMBean.getObj()) ) 
							estagioMBean.getObj().iniciarAtributosTransient();
						setAtividadesMBean(estagioMBean);
					}else if (isTrabalhoFimCurso()){
						TrabalhoFimCursoMBean trabalhoFimCursoMBean = getMBean("trabalhoFimCurso");
						trabalhoFimCursoMBean.setObj(dao.findByExactField(TrabalhoFimCurso.class, "matricula.id", obj.getId(), true));
						if( !isEmpty(trabalhoFimCursoMBean.getObj()) )
							trabalhoFimCursoMBean.getObj().iniciarAtributosTransient();
						setAtividadesMBean(trabalhoFimCursoMBean);
					}
					if( !isEmpty(obj.getRegistroAtividade()) ){
						/** Consulta os dados do coordenador e orientador evitando problema com o CACHE. */
						populaOrientacaoAtividade( obj.getRegistroAtividade() );
						populaCoordenador( obj.getRegistroAtividade() );
					}
					getAtividadesMBean().setAtualizacaoParaGraduacao(true);
				return telaDadosRegistro();
			case ALTERACAO_PERIODO:		
				getGenericDAO().refresh(obj);
				obj.setSituacaoMatricula( getGenericDAO().refresh(obj.getSituacaoMatricula()) );				
				return telaDadosRegistro();
		}
		return null;
	}

	/**
	 * Inicia a consolida��o realizada pelo orientador da atividade. 
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/atividades_orientandos.jsp</li>
	 * </ul> 
	 * @param evt
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String iniciarConsolidacaoOrientador() throws DAOException, NegocioException {

		Integer id = getParameterInt("id");
		if( id == null || id == 0 ){
			addMensagemErroPadrao();
			return null;
		}

		obj = getGenericDAO().findByPrimaryKey(id, MatriculaComponente.class);

		/** os orientadores das atividades s� podem consolidar atividades COM NOTA */
		if( !obj.getComponente().isNecessitaMediaFinal() ){
			throw new NegocioException("O orientador s� pode consolidar atividades acad�micas espec�ficas que exigem nota.");
		}

		setOperacao(OperacaoRegistroAtividade.CONSOLIDACAO);
		return selecionarMatriculaAtividade();
	}

	/**
	 * Visualiza os dados do registro de atividade selecionado de um dos orientandos
	 * do usu�rio.
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/atividades_orientandos.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public String viewAtividadeOrientando() throws DAOException{

		Integer id = getParameterInt("id");
		if( id == null || id == 0 ){
			addMensagemErroPadrao();
			return null;
		}

		visualizarResumo = true;
		obj = getGenericDAO().findByPrimaryKey(id, MatriculaComponente.class);
		obj.setDiscente(getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId()));

		return telaResumo();

	}

	/**
	 * Redireciona para a tela de lista de atividades dos orientandos do usu�rio logado. 
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String telaListaAtividadesOrientandos(){
		return forward(VIEW_ATIVIDADES_ORIENTANDOS);
	}

	/**
	 * Busca todas os registros de atividades cujo orientador � o usu�rio.
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/atividades_orientandos.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegistroAtividade> getAtividadesOrientandos() throws DAOException{
		setOperacao(OperacaoRegistroAtividade.CONSOLIDACAO);
		RegistroAtividadeDao dao = getDAO(RegistroAtividadeDao.class);
		Collection<RegistroAtividade> orientandos = new ArrayList<RegistroAtividade>();
		
		if( getServidorUsuario() == null )
			addMensagemErro("Voc� n�o tem permiss�o para executar esta opera��o.");
		
		if( hasErrors() ) return null;
		
		orientandos =  dao.findMatriculasNaoConsolidadasByOrientador(getServidorUsuario().getId());
		
		if (orientandos == null)
			addMensagemErro("N�o foram localizadas Orienta��es.");
		
		return orientandos;
	}
	/**
	 * Busca todas os registros de atividades cujo orientador � o usu�rio.
	 * M�todo n�o chamado por JSPs.
	 * @return
	 * @throws DAOException
	 */
	private Collection<RegistroAtividade> getRegistrosAtividadesOrientandos() throws DAOException{
		if( getServidorUsuario() == null )
			return null;
		
		RegistroAtividadeDao dao = getDAO(RegistroAtividadeDao.class);
		return dao.findNaoConsolidadasByOrientador(getServidorUsuario().getId(), null);
	}
	
	/**
	 * Retorna as atividades acad�micas espec�ficas nas quais o discente
	 * selecionado est� matriculado. 
	 * <br/> <br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/selecao_atividade.jsp</li>
	 * </ul> 
	 * @return
	 */
	public Collection<MatriculaComponente> getAtividadesMatriculadas() {
		return atividadesMatriculadas;
	}

	/**
	 * Retorna a descri��o da opera��o atual referente ao registro de atividades. 
	 * <br/> <br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/registro_atividade/busca_atividade.jsp</li>
	 *   <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 *   <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/graduacao/registro_atividade/selecao_atividade.jsp</li>
	 *   <li>/sigaa.war/lato/registro_atividade/dados_registro.jsp</li>
	 *   <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 *   <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 *   <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getDescricaoOperacao() {
		switch (operacao) {
		case MATRICULA:
			return "Matr�cula em Atividade";
		case CONSOLIDACAO:
			return "Consolida��o de Atividade";
		case VALIDACAO:
			return "Valida��o de Atividade";
		case EXCLUSAO:
			return "Exclus�o de Matr�cula em Atividade";
		case RENOVACAO:
			return "Renova��o de Matr�cula em Atividade";
		case VALIDACAO_LATO_SENSU:
			return "Trabalho Final de Curso";
		case ALTERACAO_VALIDACAO_LATO_SENSU:
		case ALTERACAO_GRADUACAO:
		case ALTERACAO_PERIODO:
			return "Altera��o de Matr�cula em Atividade";			
		default:
			return "";
		}
	}

	/** 
	 * Seta o discente selecionado.
	 */
	public void setDiscente(DiscenteAdapter discente) {
		obj.setDiscente(discente);
	}

	/** 
	 * Retorna o tipo da opera��o sendo executada 
	 * @return
	 */
	public OperacaoRegistroAtividade getOperacao() {
		return operacao;
	}

	/** 
	 * Seta o tipo da opera��o sendo executada
	 * @param op
	 */
	public void setOperacao(OperacaoRegistroAtividade op) {
		this.operacao = op;
	}

	/** Indica se a opera��o ser� de matr�cula. 
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/resumo.jsp</li>
	 * <li>/sigaa.war/ensino/orientacao_atividade/lista.jsp</li>
	 * <li>/sigaa.war/ensino/orientacao_atividade/view.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isMatricula() {
		return operacao.equals(OperacaoRegistroAtividade.MATRICULA);
	}

	/** Indica se a opera��o ser� de consolida��o.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/resumo.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isConsolidacao() {
		return operacao.equals(OperacaoRegistroAtividade.CONSOLIDACAO);
	}

	/** Indica se a opera��o ser� de valida��o (incluindo valida��o lato sensu).
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/resumo.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/busca_atividade.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isValidacao() {
		if (operacao == null) {
			addMensagemErro("O procedimento que voc� tentou realizar j� foi processado anteriormente." +
			" Para realiz�-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return false;
		}
		return operacao.equals(OperacaoRegistroAtividade.VALIDACAO) || operacao.equals(OperacaoRegistroAtividade.VALIDACAO_LATO_SENSU) || operacao.equals(OperacaoRegistroAtividade.ALTERACAO_VALIDACAO_LATO_SENSU);
	}

	/** Indica se a opera��o ser� de exclus�o.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/resumo.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExclusao() {
		if (operacao == null){
			addMensagemErro("O procedimento que voc� tentou realizar j� foi processado anteriormente." +
			" Para realiz�-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return false;
		}	
		return operacao.equals(OperacaoRegistroAtividade.EXCLUSAO);
	}

	/** Indica se a opera��o ser� de renova��o.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isRenovacao() {
		return operacao.equals(OperacaoRegistroAtividade.RENOVACAO);
	}
	
	/**
	 * Indica se a opera��o ser� de altera��o.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isAlteracao(){
		return operacao.equals(OperacaoRegistroAtividade.ALTERACAO_PERIODO);
	}
	
	/**
	 * Indica se a opera��o ser� de altera��o.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isAlteracaoGraduacao(){
		return operacao.equals(OperacaoRegistroAtividade.ALTERACAO_GRADUACAO);
	}

	/**
	 * Verifica se o registro de atividade corrente refere-se a um componente
	 * curricular do tipo ESTAGIO.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/resumo.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isEstagio() {
		return obj.getComponente() != null && obj.getComponente().isEstagio();
	}

	/**
	 * Verifica se o registro de atividade corrente refere-se a um componente
	 * curricular do tipo TRABALHO_FIM_CURSO.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/resumo.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isTrabalhoFimCurso() {
		return obj.getComponente() != null && obj.getComponente().isTrabalhoConclusaoCurso();
	}

	/**  Indica se deve filtrar a busca por departamento. 
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/registro_atividade/busca_atividade.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isBuscaDepartamento() {
		return buscaDepartamento;
	}

	/** Indica se deve filtrar a busca pelo nome da atividade.  
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/registro_atividade/busca_atividade.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isBuscaNomeAtividade() {
		return buscaNomeAtividade;
	}

	/** Indica se deve filtrar a busca por tipo de atividade. 
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/registro_atividade/busca_atividade.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isBuscaTipoAtividade() {
		return buscaTipoAtividade;
	}

	/** Seta se deve filtrar a busca por departamento. 
	 * @param buscaDepartamento
	 */
	public void setBuscaDepartamento(boolean buscaDepartamento) {
		this.buscaDepartamento = buscaDepartamento;
	}

	/** Seta se deve filtrar a busca pelo nome da atividade. 
	 * @param buscaNomeAtividade
	 */
	public void setBuscaNomeAtividade(boolean buscaNomeAtividade) {
		this.buscaNomeAtividade = buscaNomeAtividade;
	}

	/** Seta se deve filtrar a busca por tipo de atividade. 
	 * @param buscaTipoAtividade
	 */
	public void setBuscaTipoAtividade(boolean buscaTipoAtividade) {
		this.buscaTipoAtividade = buscaTipoAtividade;
	}

	/** Seta o Mbean respons�vel pelos dados da atividade no prodocente. Pode assumir o valor de EstagioMBean ou TrabalhoFimCursoMBean. 
	 * @param atividadesMBean the atividadesMBean to set
	 */
	public void setAtividadesMBean(AbstractControllerAtividades<?> atividadesMBean) {
		this.atividadesMBean = atividadesMBean;
	}

	/** Retorna o Mbean respons�vel pelos dados da atividade no prodocente. Pode assumir o valor de EstagioMBean ou TrabalhoFimCursoMBean. 
	 * @return the atividadesMBean
	 */
	public AbstractControllerAtividades<?> getAtividadesMBean() {
		return atividadesMBean;
	}

	/** Indica se a atividade � de gradua��o.
	 * M�todo n�o invocado por JSPs.
	 * @return
	 */
	public boolean isGraduacao() {
		return obj.getComponente().getNivel() == NivelEnsino.GRADUACAO;
	}
	
	/** Indica se a atividade � de lato sensu.
	 * M�todo n�o invocado por JSPs.
	 * @return
	 */
	public boolean isLato() {
		return obj.getComponente().getNivel() == NivelEnsino.LATO;
	}
	
	/** Indica se a atividade � do n�vel t�cnico.
	 * M�todo n�o invocado por JSPs.
	 * @return
	 */
	public boolean isTecnico() {
		return obj.getComponente().getNivel() == NivelEnsino.TECNICO;
	}

	/** Retorna uma cole��o de selectItem de resultados.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 * </ul>
	 * 
	 * @see SituacaoMatricula
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getResultados() throws DAOException {
		List<SituacaoMatricula> resultados = new ArrayList<SituacaoMatricula>();
		resultados.add(SituacaoMatricula.APROVADO);

		if ( obj.getDiscente().isGraduacao() && !isObrigatorioInformarDocentesEnvolvidos() || isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE )) {
			resultados.add(SituacaoMatricula.APROVEITADO_DISPENSADO);
		}

		resultados.add(SituacaoMatricula.REPROVADO);
		return toSelectItems(resultados, "id", "descricao");
	}

	/** Indica se a matr�cula na atividade foi migrada do PontoA 
	 * @return
	 */
	public boolean isMigrado() {
		return migrado;
	}

	/** Seta se a matr�cula na atividade foi migrada do PontoA
	 * @param migrado
	 */
	public void setMigrado(boolean migrado) {
		this.migrado = migrado;
	}

	/** Indica se � apenas visualiza��o da atividade pelo orientador. 
	 * @return
	 */
	public boolean isVisualizarResumo() {
		return visualizarResumo;
	}

	/** Seta se � apenas visualiza��o da atividade pelo orientador. 
	 * @param visualizarResumo
	 */
	public void setVisualizarResumo(boolean visualizarResumo) {
		this.visualizarResumo = visualizarResumo;
	}

	/** Indica a de dispensa de atividade (mesmo quando o componente necessita de nota) 
	 * @return
	 */
	public boolean isDispensa() {
		return dispensa;
	}

	/** Seta a de dispensa de atividade (mesmo quando o componente necessita de nota) 
	 * @param dispensa
	 */
	public void setDispensa(boolean dispensa) {
		this.dispensa = dispensa;
	}

	/**
	 * Indica se � obrigat�rio informar os docentes envolvidos na orienta��o/coordena��o
	 * da atividade acad�mica
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/resumo.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public boolean isInformarDocentesEnvolvidos() throws DAOException {
		if (getObj() == null || obj.getDiscente() == null) {
			return false;
		}

		if (getObj().getDiscente().isGraduacao()) {
			DiscenteGraduacao grad = (DiscenteGraduacao) getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId());
			boolean validarDocentesEnvolvidos = RegistroAtividadeValidator.validarDocentesEnvolvidos(getUsuarioLogado(), isValidacao(), grad);
			return validarDocentesEnvolvidos || (!validarDocentesEnvolvidos && isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) );
		} else if(getObj().getDiscente().isTecnico()){
			DiscenteTecnico discenteTecnico = (DiscenteTecnico) getDAO(DiscenteDao.class).findByPK(obj.getDiscente().getId());
			boolean validarDocentesEnvolvidos = RegistroAtividadeValidator.validarDocentesEnvolvidosTecnico(getUsuarioLogado(), isValidacao(), discenteTecnico);
			return validarDocentesEnvolvidos || (!validarDocentesEnvolvidos && isUserInRole(SigaaPapeis.GESTOR_TECNICO) );
		} else if (getObj().getDiscente().isStricto()) {
			return false;
		}

		return true;
	}

	/**
	 * Indica se � obrigat�rio informar os docentes envolvidos no formul�rio do registro de atividade
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/registro_atividade/dados_registro.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public boolean isObrigatorioInformarDocentesEnvolvidos() throws DAOException {
		if (obrigatorioInformarDocentesEnvolvidos == null) {
			GenericDAO dao = getGenericDAO();
			DiscenteGraduacao grad = dao.findByPrimaryKey(obj.getDiscente().getId(), DiscenteGraduacao.class);
			if (grad != null)
				obrigatorioInformarDocentesEnvolvidos = !isGraduacao()
						|| RegistroAtividadeValidator.validarDocentesEnvolvidos(getUsuarioLogado(), isValidacao(), grad);
		}
		return obrigatorioInformarDocentesEnvolvidos != null ? obrigatorioInformarDocentesEnvolvidos.booleanValue() : false;
	}

	/**
	 * Indica se o usu�rio possui permiss�o pra registrar matr�culas compuls�rias
	 * em atividades acad�micas espec�ficas.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isPermissaoMatriculaCompulsoria() {
		return getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE);
	}

	/**
	 * Indica se a opera��o de registro necessita de confirma��o de senha para sua homologa��o.
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/RegistroAtividade/resumo.jsp</li>
	 * <li>/sigaa.war/graduacao/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isNecessarioConfirmacaoSenha() {
		return (isExclusao() || isDispensa() || isAlteracao() || isMatricula() || !hasOrientacoes());
	}

	/**
	 * Indica se o ator da opera��o � um tutor de EAD.
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>M�todo n�o invocado por JSP.</li>
	 * </ul> 
	 * @return
	 */
	public boolean isTutorEad() {
		return getAcessoMenu().isTutorEad()
			&& SigaaSubsistemas.PORTAL_TUTOR.equals(getSubSistema());
	}

	/**
	 * Indica se o usu�rio ativo � orientador da atividade
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>M�todo n�o invocado por JSP.</li>
	 * </ul> 
	 * @return
	 * @throws DAOException
	 */
	private boolean isOrientadorAtividade() throws DAOException {
		
		Collection<RegistroAtividade> atividadesOrientandos = getRegistrosAtividadesOrientandos();
		
		if (atividadesOrientandos == null || atividadesOrientandos.isEmpty())
			return false;
		
		for (RegistroAtividade registroAtividade : atividadesOrientandos) {
			if (registroAtividade.getMatricula().equals(obj))
				return true;
		}
		
		return false;
	}
	/**
	 * Chama a tela de sele��o de tutoria.<br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>M�todo n�o invocado por JSP.</li>
	 * </ul> 
	 * @return
	 */
	public String telaSelecaoTutoria() {
		getCurrentRequest().setAttribute("matriculaAtividade", true);
		return forward("/graduacao/matricula/selecao_tutoria.jsp");
	}

	/**
	 * Verifica se a atividade necessita de avalia��o por nota
	 * <br />
	 * M�todo chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/lato/registro_atividade/dados_registro.jsp</li>
	 * <li>/sigaa.war/lato/registro_atividade/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isNota(){
		return metodoAvaliacao == MetodoAvaliacao.NOTA;
	}

	/** Retorna a carga hor�ria do orientador dedicada � orienta��o desta atividade. 
	 * @return
	 */
	public int getChDedicada() {
		return chDedicada;
	}

	/** Seta a carga hor�ria do orientador dedicada � orienta��o desta atividade.
	 * @param chDedicada
	 */
	public void setChDedicada(int chDedicada) {
		this.chDedicada = chDedicada;
	}

	public Collection<ComponenteCurricular> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<ComponenteCurricular> atividades) {
		this.atividades = atividades;
	}
	
	/**
	 * Diz se � permitido cadastrar a orienta��o da atividade com docente externo.
	 * 
	 * @return
	 */
	public boolean isPermiteOrientadoresExternos() {
		boolean permissaoDocenteExterno = ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.PERMITE_DOCENTE_EXTERNO_ORIENTAR_ATIVIDADE);
		return permissaoDocenteExterno;
	}

	public Integer getIdOrientador() {
		return idOrientador;
	}

	public void setIdOrientador(Integer idOrientador) {
		this.idOrientador = idOrientador;
	}

	public String getNomeOrientador() {
		return nomeOrientador;
	}

	public void setNomeOrientador(String nomeOrientador) {
		this.nomeOrientador = nomeOrientador;
	}

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public void setConsolidarMatriculas(boolean consolidarMatriculas) {
		this.consolidarMatriculas = consolidarMatriculas;
	}

	public boolean isConsolidarMatriculas() {
		return consolidarMatriculas;
	}

	public String getResolucaoValidacaoAtividadeGraduacao() {
		return ParametroHelper.getInstance().getParametro(ParametrosGraduacao.TEXTO_RESOLUCAO_VALIDACAO_ATIVIDADE_GRADUACAO);
	}

	public Integer getIdCoOrientador() {
		return idCoOrientador;
	}

	public void setIdCoOrientador(Integer idCoOrientador) {
		this.idCoOrientador = idCoOrientador;
	}

	public String getNomeCoOrientador() {
		return nomeCoOrientador;
	}

	public void setNomeCoOrientador(String nomeCoOrientador) {
		this.nomeCoOrientador = nomeCoOrientador;
	}
	
}
