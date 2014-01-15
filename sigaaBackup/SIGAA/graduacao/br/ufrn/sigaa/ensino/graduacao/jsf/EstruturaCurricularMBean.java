/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 05/02/2007
 * 
 */

package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.AreaConcentracaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.OptativaCurriculoSemestre;
import br.ufrn.sigaa.ensino.graduacao.negocio.CurriculoComponenteValidator;
import br.ufrn.sigaa.ensino.graduacao.negocio.CurriculoHelper;
import br.ufrn.sigaa.ensino.graduacao.negocio.CurriculoValidator;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/**
 * MBean respons�vel pela manuten��o das {@link Curriculo Estruturas
 * Curriculares}.
 * 
 * @author Andr�
 * @author Victor Hugo
 */
@Component("curriculo") @Scope("session")
public class EstruturaCurricularMBean extends SigaaAbstractController<Curriculo> implements SeletorComponenteCurricular {

	/** ID ao qual se restringe o curso na busca por estruturas curriculares. */
	private int idCurso = 0;

	/** Select a ser populado para escolha de uma matriz do curr�culo. */
	private List<SelectItem> possiveisMatrizes = new ArrayList<SelectItem>(0);

	/** Select a ser populado para escolha de um curso do curr�culo. */
	private List<SelectItem> possiveisCursos = new ArrayList<SelectItem>(0);

	/**
	 * Select a ser populado para escolha de um curr�culo base para adicionar os
	 * componentes.
	 */
	private List<SelectItem> curriculosBase = new ArrayList<SelectItem>(0);

	/**
	 * Indica se permite exibe o formul�rio de aproveitamento de componentes a
	 * partir de outro curr�culo.
	 */
	private Boolean aproveitarComponentes;

	/**
	 * Indica se desabilita o formul�rio de aproveitamento de componentes a
	 * partir de outro curr�culo.
	 */
	private Boolean desabilitaAproveitamento = false;

	/** Lista de componentes do curr�culo. */
	private List<CurriculoComponente> curriculoComponentes;

	/**
	 * Cole��o de CurriculoComponente organizados por semestre a serem
	 * incorporados na estrutura curricular.
	 */
	private TreeMap<Integer, ArrayList<CurriculoComponente>> componentesAdicionados;
	
	/**
	 * Armazena as cargas-hor�rias separadas por semestre.
	 */
	private TreeMap<Integer, Integer> chPorSemestre = null;

	/**
	 * Lista de componentes encontrados na busca por componentes que se deseja
	 * incluir no curr�culo.
	 */
	private Collection<ComponenteCurricular> componentesEncontrados = new ArrayList<ComponenteCurricular>(0);

	/** CurriculoComponente a ser adicionado na cole��o. */
	private CurriculoComponente curriculoComponente;

	/** Unidade ao qual se restringe a busca de curr�culos de stricto sensu. */
	private Unidade programa;

	/** Cole��o de selectItem de cursos. */
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>(0);

	/** Carga Hor�ria de Componentes Curriculares optativas j� adicionadas. */
	private int tmpChOptativa;

	/** Semestre a ser exibido no formul�rio. */
	private int exibirSemestre;
	
	/** Indica se deve solicitar que o usu�rio digite a senha no formul�rio. */
	private boolean pedirSenha = false;

	/** Indica se deve-se exibir os filtros para busca. */
	private boolean exibirFiltrosBusca = true;

	// Filtros da consulta
	/** Indica se filtra a busca de curr�culos por programa. */
	private boolean filtroPrograma;
	/** Indica se filtra a busca de curr�culos por curso. */
	private boolean filtroCurso;
	/** Indica se filtra a busca de curr�culos por matriz curricular. */
	private boolean filtroMatriz;
	/** Indica se filtra a busca de curr�culos por c�digo. */
	private boolean filtroCodigo;
	
	// Filtros da consulta Componente
	/** Indica se filtra a busca de componentes por c�digo. */
	private boolean filtroCodigoComponente;
	/** Indica se filtra a busca de componentes por nome. */
	private boolean filtroNome;
	/** Indica se filtra a busca de componentes por tipo. */
	private boolean filtroTipo;
	/** Indica se filtra a busca de componentes por unidade acad�mica. */
	private boolean filtroUnidade;

	/**
	 * Indica se na altera��o da estrutura curricular pode alterar a carga
	 * hor�ria optativa e os componentes obrigat�rias. Somente ser� alterado se
	 * n�o houver nenhum aluno vinculado a estrutura.
	 */
	private boolean podeAlterarChEObrigatorias = true;

	/** Carga Hor�ria optativa no semestre. */
	private List<OptativaCurriculoSemestre> chOptativaSemestre;

	/** Grupo de componentes curriculares optativos. */
	private List<GrupoOptativas> gruposOptativas;

	/** Indica se o filtro � ativo ou n�o. */
	private boolean somenteAtivas = true;

	/** N�vel da Disciplina de resid�ncia */
	private int nivelDisciplinaResidencia;
	
	/** Indica o n�mero m�nimo de componentes Eletivos no curr�culo */
	private int minComponenteEletivo;
	
	/** Indica o n�mero m�ximo de componentes Eletivos no curr�culo */
	private int maxComponenteEletivo;
	
	/** Indica a aba atualmente selecionada na tela /graduacao/curriculo/componentes.jsp */
	private String abaSelecionada;

	/** CurriculoComponente que ser� substitu�do por outro no curr�culo. */
	private CurriculoComponente componenteCurriculoASubstituir;
	
	/** Componente Curricular que ser� substitu�do por outro no curr�culo. */
	private ComponenteCurricular substituidoPor;
	
	/** Lista de componentes curriculares pass�veis de serem escolhidos como TCC definitivo do curr�culo */
	private List<ComponenteCurricular> componentesTcc;
	
	/** Id do componente curricular escolhido como TCC definitivo do curr�culo */
	private Integer idTccDefinitivo;
	
	/**
	 * Construtor padr�o
	 * 
	 * @throws DAOException
	 */
	public EstruturaCurricularMBean() throws DAOException {
		
		setReadOnly(true);
		initObj();

		if (SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema())) {
			exibirFiltrosBusca = false;
			EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
			setResultadosBusca(dao.findCompleto(null, null, getProgramaStricto().getId(), null));
		}
	}

	/**
	 * Inicializa os atributos do controller.
	 * <br>M�todo n�o invocado por JSP�s
	 * 
	 * @throws DAOException
	 */
	public void initObj() throws DAOException {
		obj = new Curriculo();
		obj.setCurso(new Curso());
		obj.setMatriz(new MatrizCurricular());
		obj.getMatriz().setCurso(new Curso());
		this.resultadosBusca = null;
		initFiltros();
		curriculoComponente = new CurriculoComponente();
		curriculoComponente.setObrigatoria(false);

		if (getUsuarioLogado() != null) {
			if (isGraduacao()) {
				obj.setTccDefinitivo(new ComponenteCurricular());
				obj.setMatriz(new MatrizCurricular());
				obj.getMatriz().setCurso(new Curso());
				cursosCombo = (List<SelectItem>) (new CursoGraduacaoMBean()).getAllCombo();
			} else if(isStricto() || isResidencia()) {
				desabilitaAproveitamento = true;
				obj.getCurso().setUnidade(new Unidade());
				if (getAcessoMenu() != null){
					if (getAcessoMenu().isPpg() 
							|| getAcessoMenu().isGestorComplexoHospitalar() 
							|| getAcessoMenu().isGestorResidenciaMedica()) {
						selecionarPrograma(null);
					} else {
						getCarregarCursosStrictoCombo();
					}
				}

				curriculoComponente.setAreaConcentracao(new AreaConcentracao());
				curriculoComponente.getAreaConcentracao().setId(-1);
				programa = new Unidade();
			}
		}
		tmpChOptativa = 0;
		
		
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(getNivelEnsino());
		if ( !ValidatorUtil.isEmpty(parametros) && !ValidatorUtil.isEmpty(parametros.getMinCreditosExtra()) && !ValidatorUtil.isEmpty(parametros.getHorasCreditosAula())){
			minComponenteEletivo = parametros.getMinCreditosExtra() * parametros.getHorasCreditosAula();
		}
		
		if ( !ValidatorUtil.isEmpty(parametros) && !ValidatorUtil.isEmpty(parametros.getMaxCreditosExtra()) && !ValidatorUtil.isEmpty(parametros.getHorasCreditosAula())){
			maxComponenteEletivo = parametros.getMaxCreditosExtra() * parametros.getHorasCreditosAula();			
		}		
	}
	
	/**
	 * Decide se mostrar� ou n�o para o coordenador de p�s a op��o de Alterar uma estrutura curricular.
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/curriculo/curso_estrutura.jsp
	 * 
	 * @return
	 */
	public boolean isMostrarCoordenadorOpcaoAlterarEstruturaCurricular() {
		String param = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.PERMITE_COORDENACAO_CADASTRAR_ALTERAR_ATIVAR_INATIVAR_ESTRUTURA_CURRICULAR);
		if(param.contains("A")) {
			return true;
		}
		return false;
	}
	/**
	 * Decide se mostrar� ou n�o para o coordenador de p�s a op��o de Cadastrar uma estrutura curricular.
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/curriculo/curso_estrutura.jsp
	 * 
	 * @return
	 */
	public boolean isMostrarCoordenadorOpcaoCadastrarEstruturaCurricular() {
		String param = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.PERMITE_COORDENACAO_CADASTRAR_ALTERAR_ATIVAR_INATIVAR_ESTRUTURA_CURRICULAR);
		if(param.contains("C")) {
			return true;
		}
		return false;
	}
	/**
	 * Decide se mostrar� ou n�o para o coordenador de p�s a op��o de Ativar uma estrutura curricular.
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/curriculo/curso_estrutura.jsp
	 * 
	 * @return
	 */
	public boolean isMostrarCoordenadorOpcaoAtivarEstruturaCurricular() {
		String param = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.PERMITE_COORDENACAO_CADASTRAR_ALTERAR_ATIVAR_INATIVAR_ESTRUTURA_CURRICULAR);
		if(param.contains("+")) {
			return true;
		}
		return false;
	}
	/**
	 * Decide se mostrar� ou n�o para o coordenador de p�s a op��o de Desativar uma estrutura curricular.
	 * 
	 * Chamado por:
	 * sigaa.war/stricto/curriculo/curso_estrutura.jsp
	 * 
	 * @return
	 */
	public boolean isMostrarCoordenadorOpcaoDesativarEstruturaCurricular() {
		String param = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.PERMITE_COORDENACAO_CADASTRAR_ALTERAR_ATIVAR_INATIVAR_ESTRUTURA_CURRICULAR);
		if(param.contains("-")) {
			return true;
		}
		return false;
	}
	
	
		
	

	/**
	 * Seleciona os cursos com base na unidade. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/geral.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarPrograma(ValueChangeEvent e) throws DAOException {
		Integer unidade = null;
		if (e != null && e.getNewValue() != null) {
			unidade = (Integer) e.getNewValue();
		} else {
			if (obj.getCurso() != null && obj.getCurso().getUnidade() != null)
				unidade = obj.getCurso().getUnidade().getId();
		}
		if (unidade != null && unidade > 0) {
			CursoDao dao = getDAO(CursoDao.class);
			Character nivel = ' ';
			if (NivelEnsino.isValido(getNivelEnsino())) {
				nivel = getNivelEnsino();
			}
			Collection<Curso> cursos = dao.findByUnidade(unidade, nivel);
			if (isUserInRole(SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA,SigaaPapeis.COORDENADOR_CURSO_STRICTO))
				cursosCombo = toSelectItems(cursos, "id", "descricao");
			else
				cursosCombo = toSelectItems(cursos, "id", "tipoCursoStricto.descricao");
		}
	}

	/**
	 * Limpa os filtros de busca
	 */
	private void initFiltros() {
		this.possiveisMatrizes.clear();
		this.filtroCodigo = false;
		this.filtroCurso = false;
		this.filtroMatriz = false;
		this.filtroPrograma = false;
	}

	/**
	 * Submete os dados gerais do curr�culo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String submeterDadosGerais() throws ArqException {		
		erros = new ListaMensagens(); 
		if (!checkOperacaoAtiva(1))
			return cancelar();
		
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		
		validarDadosGerais();
		
		if (hasErrors())
			return null;
		
		if (obj.getCurso() != null && obj.getCurso().getId() > 0) {
			Curso curso = dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class);
			obj.setCurso(curso);
		}

		if (dao.existeCodigo(obj, getNivelEnsino())) {
			erros.addErro("J� existe um curr�culo com esse c�digo");
		}
		if (hasErrors())
			return null;

		setIdCurso(obj.getCurso().getId());
		curriculoComponentes = null;
		if (aproveitarComponentes != null && aproveitarComponentes) {
			// selecionando o curso na tela de aproveitamento com o curso
			// escolhido na primeira tela e carregando curr�culos do curso
			// selecionado
			curriculosBase = new ArrayList<SelectItem>(0);
			if (getIdCurso() > 0) {
				Collection<Curriculo> col = dao.findByCurso(idCurso, getNivelEnsino());
				if (col != null && !col.isEmpty())
					curriculosBase = toSelectItems(col, "id", "matrizDescricao");
			}
		}

		if (isGraduacao()) {
			obj.setMatriz(dao.findByPrimaryKey(obj.getMatriz().getId(), MatrizCurricular.class));
			
			if(isEmpty(obj.getId()))
				prepararEstrutura(obj.getSemestreConclusaoIdeal());				
			else
				prepararEstrutura(dao.findMaxSemestreOferta(obj) > obj.getSemestreConclusaoIdeal() ? dao.findMaxSemestreOferta(obj) : obj.getSemestreConclusaoIdeal());
			
			int numSemestres = obj.getSemestreConclusaoIdeal();
			
			Set<Integer> keys = componentesAdicionados.keySet();
			for (Integer key : keys) {
				if (key > numSemestres && isEmpty(componentesAdicionados.get(key)) && isEmpty(componentesAdicionados.get(key + 1)))
					componentesAdicionados.remove(key);
			}

			if (aproveitarComponentes) {
				desabilitaAproveitamento = true; // s� entra na primeira vez!
				return telaAproveitamentoComponentes();
			} else {
				desabilitaAproveitamento = true;
				if(hasNiveisxcedentes()){
					gerarWarningNiveisExcedentes();
				}
				verificarCreditosExcedentes();
				return telaComponentes();
			}
		} else if (isStricto()){

			if (obj.getCurso().getTipoCursoStricto() != null) {
				// addMensagemErro("N�o � poss�vel cadastrar curr�culos para cursos sem tipo de stricto definido");
				// return null;
				if (isEmpty(obj.getSemestreConclusaoMaximo())) {
					if (obj.getCurso().getTipoCursoStricto().equals(TipoCursoStricto.DOUTORADO))
						obj.setSemestreConclusaoMaximo(Curriculo.PRAZO_MAXIMO_CONCLUSAO_DOUTORADO);
					else if (obj.getCurso().getTipoCursoStricto().equals(TipoCursoStricto.MESTRADO_ACADEMICO))
						obj.setSemestreConclusaoMaximo(Curriculo.PRAZO_MAXIMO_CONCLUSAO_MESTRADO);
					else if (obj.getCurso().getTipoCursoStricto().equals(TipoCursoStricto.MESTRADO_PROFISSIONAL))
						obj.setSemestreConclusaoMaximo(Curriculo.PRAZO_MAXIMO_CONCLUSAO_MESTRADO);
				}
			}
			if (obj != null && obj.getCurriculoComponentes() != null)
				Collections.sort(obj.getCurriculoComponentes());
			return telaComponentes();
		} else if (isResidencia()) {
			prepararEstrutura(5);
			return telaComponentes();
		}
		addMensagemErro("O n�vel de ensino n�o est� definido corretamente.");
		return null;
	}

	/**
	 * Realiza a valida��o dos Dadois Gerais antes de avan�ar.
	 */
	private void validarDadosGerais() {
		erros.addAll(obj.validate().getMensagens());
		
		if (isGraduacao()){
			if (obj.getMaxEletivos() < minComponenteEletivo || obj.getMaxEletivos() > maxComponenteEletivo){
				erros.addErro("O valor m�ximo de componentes eletivos deve estar entre ("+minComponenteEletivo +" - "+ maxComponenteEletivo +")");
			}			
		}
		
		if(!isResidencia()){
			if(obj.getCrMinimoSemestre() <= 0){
				erros.addErro("Os Cr�ditos M�nimos Por Per�odo Letivo n�o podem ser menores ou iguais a zero");
			}
			
			if(obj.getCrIdealSemestre() <= 0){
				erros.addErro("Os Cr�ditos M�dios Por Per�odo Letivo n�o podem ser menores ou iguais a zero");
			}
			
			if(obj.getCrMaximoSemestre() <= 0){
				erros.addErro("Os Cr�ditos M�ximos Por Per�odo Letivo n�o podem ser menores ou iguais a zero");
			}
		}
		
		if(obj.getSemestreConclusaoMinimo() != null){
			if(obj.getSemestreConclusaoMinimo() <= 0)
				erros.addErro("O Prazo M�nimo Para Conclus�o n�o pode ser menor ou igual a zero");
		}
		if(obj.getSemestreConclusaoIdeal() != null){
			if(obj.getSemestreConclusaoIdeal() <= 0)
				erros.addErro("O Prazo M�dio Para Conclus�o n�o pode ser menor ou igual a zero");
		}
		if(obj.getSemestreConclusaoMaximo() != null){
			if(obj.getSemestreConclusaoMaximo() <= 0)
				erros.addErro("O Prazo M�ximo Para Conclus�o n�o pode ser menor ou igual a zero");
		}
	}

	/**
	 * Gera a mensagem de warning a ser mostrada para o usu�rio, ao acessar a p�gina de componentes, caso
	 * existam mais n�veis cadastrados do que o permitido para a determinada estrutura curricular.
	 */
	private void gerarWarningNiveisExcedentes() {
		StringBuffer sb = new StringBuffer();
		for(int i = componentesAdicionados.keySet().size(); i >= obj.getSemestreConclusaoIdeal()+1;i--)
			sb.append("- "+i+"�<br />");
		addMensagemWarning("Existem n�veis excedentes para a estrutura curricular escolhida. Para continuar, os n�veis excedentes devem ser exclu�dos ou o prazo m�dio para conclus�o da estrutura deve ser aumentado.<br />" +
				"Os seguintes n�veis s�o excedentes:<br />" +
				sb.toString());
	}

	/**
	 * Direciona o usu�rio para a tela de aproveitamento de componentes. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Nenhuma</li>
	 * </ul>
	 */
	public String telaAproveitamentoComponentes() {
		return forward("/graduacao/curriculo/aproveitamento.jsf");
	}

	/**
	 * Lista os curr�culos de um programa de p�s-gradua��o. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 */
	public String listarCurriculosPrograma() throws Exception {
		filtroPrograma = true;
		programa = getProgramaStricto();
		buscar();
		return telaBusca();
	}

	/**
	 * Direciona o usu�rio para a tela de busca de curr�culos. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Nenhuma</li>
	 * </ul>
	 * 
	 */
	public String telaBusca() {
		if (isGraduacao())
			return forward("/graduacao/curriculo/lista.jsf");
		else
			return forward("/stricto/curriculo/lista.jsf");
	}

	/**
	 * Prepara a estrutura de componentes conforme o n�mero de semestres passado
	 * @param numSemestres
	 * @throws DAOException
	 */
	private void prepararEstrutura(int numSemestres) throws DAOException {
		
		if (componentesAdicionados == null) {
			componentesAdicionados = new TreeMap<Integer, ArrayList<CurriculoComponente>>();
			for (int i = 1; i <= numSemestres; i++) {
				componentesAdicionados.put(i,
						new ArrayList<CurriculoComponente>(0));
			}
		} else {
			int semestresAdicionados = componentesAdicionados.size();
			int diferenca = numSemestres - semestresAdicionados;
			if (diferenca > 0) {
				for (int i = semestresAdicionados + 1; i <= semestresAdicionados
						+ diferenca; i++) {
					componentesAdicionados.put(i,
							new ArrayList<CurriculoComponente>(0));
				}
			} else if (diferenca < 0) {
				for (int i = semestresAdicionados; i > semestresAdicionados
						+ diferenca; i--) {
					if(isEmpty(componentesAdicionados.get(i)) && !componentesAdicionados.containsKey(i+1))
						componentesAdicionados.remove(i);
				}
			}
		}
	}
	
	
	/**
	 * Verifica se j� existe um componente curricular adicionado no curr�culo
	 * 
	 * @param ccc
	 * @return
	 */
	private boolean jaAdicionou(ComponenteCurricular ccc) {
		if (isGraduacao()) {
			for (Integer s : componentesAdicionados.keySet()) {
				for (CurriculoComponente cc : componentesAdicionados.get(s)) {
					if (cc.getComponente().getId() == ccc.getId())
						return true;
				}
			}
		} else {
			CurriculoComponente cur = new CurriculoComponente();
			cur.setComponente(ccc);
			for (CurriculoComponente cc : obj.getCurriculoComponentes()) {
				if (cc.getId() == ccc.getId())
					return true;
			}
		}
		return false;
	}

	/**
	 * Limpa o Per�odo Letivo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws DAOException 
	 */
	public void limparSemestre(ActionEvent evt) throws DAOException {
		ArrayList<CurriculoComponente> lista = null;
		int semestre = getParameterInt("semestre");
		try{
			lista = componentesAdicionados.get(semestre);
		}catch(NullPointerException e){
			validarLista(null);
			return;
		}
		
		validarLista(lista);
		
		if(hasErrors())
			cancelar();
		
		List<CurriculoComponente> sinc = new ArrayList<CurriculoComponente>(lista);

		for (CurriculoComponente cc : sinc) {
			removeCurriculoComponente(lista, cc);			
		}
		
		if(hasErrors()){
			redirectJSF("graduacao/curriculo/componentes.jsp");
			return;
		}
		
		componentesAdicionados.put(semestre, new ArrayList<CurriculoComponente>(0));
		
		if (semestre > obj.getSemestreConclusaoIdeal() && semestre == componentesAdicionados.size()){
			componentesAdicionados.remove(semestre);
			abaSelecionada = "tab1";
			addMensagem( MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, semestre+"� N�vel" );
		}
		else {
			if(semestre > obj.getSemestreConclusaoIdeal())
				addMensagemWarning("Os componentes foram exclu�dos, mas um n�vel s� pode ser removido caso seja o �ltimo excedente da lista. Favor realizar a exclus�o de n�veis em ordem decrescente.");
			else
				addMensagemInformation("Todas os componentes do "+semestre+"� N�vel foram removidos com sucesso.");
		}
		
		recriarTabelaComponente();
		redirectJSF("graduacao/curriculo/componentes.jsp");
		return;
	}
	
	/**
	 * Utilizada para validar a lista de n�veis passada como par�metro.
	 * Caso esta seja nula o usu�rio ser� redirecionado para a p�gina inicial para que possa reiniciar o caso de uso.
	 * 
	 * @param lista
	 */
	private void validarLista(List<CurriculoComponente> lista){
		if(lista == null){
			addMensagemErro("N�o foi poss�vel recuperar o n�vel selecionado. Favor reiniciar a opera��o.");
		}
	}

	/**
	 * Redireciona para o passo de busca de componentes. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String selecionarComponente() {
		if (isGraduacao()) {
			if (getParameter("sem") != null) {
				int semestre = getParameterInt("sem");

				List<CurriculoComponente> lista = obj.getCurriculoComponentes();
				try {
					lista = componentesAdicionados.get(semestre);
				} catch (NullPointerException e) {
					validarLista(null);
					return cancelar();
				}

				validarLista(lista);

				curriculoComponente.setSemestreOferta(semestre);
			}
		}
		if (hasErrors())
			return cancelar();
		
		curriculoComponente.setCurriculo(obj);
		curriculoComponente.setSemestreOferta(getParameterInt("semestre"));
		return telaBuscaComponentes();
	}

	/**
	 * Direciona o usu�rio para a tela de busca de componentes. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 */
	public String telaBuscaComponentes() {
		return forward("/graduacao/curriculo/busca_componente.jsf");
	}

	/**
	 * Adiciona um componente qualquer em um semestre, dizendo se � obrigat�ria.
	 * Nesse m�todo s�o testadas as condi��es v�lidas para adi��o, como: limites de
	 * cr�ditos por semestre e pr�-requisitos necess�rios.
	 * 
	 * @param cc
	 * @param semestre
	 * @param obrigatoria
	 * @return mensagem de erros
	 * @throws ArqException
	 */
	private String addComponente(ComponenteCurricular cc, int semestre, boolean obrigatoria) throws ArqException {
		CurriculoComponente ccc = new CurriculoComponente();
		ccc.setCurriculo(obj);
		ccc.setSemestreOferta(semestre);
		ccc.setObrigatoria(obrigatoria);
		ccc.setComponente(cc);
		// Valida��es
		if (jaAdicionou(cc)) // se j� existe
			return "O componente " + cc.getDescricaoResumida() + " j� foi adicionado";

		// Teste de requisitos e equival�ncia
		String msg = validarRequisitosEquivalencia(ccc);
		if (msg != null)
			return msg;

		if (!obrigatoria)
			tmpChOptativa += cc.getChTotal();

		List<CurriculoComponente> lista = obj.getCurriculoComponentes();
		if (isGraduacao())
			lista = componentesAdicionados.get(semestre) == null ? new ArrayList<CurriculoComponente>() : componentesAdicionados.get(semestre);

		lista.add(ccc);
		
		recriarTabelaComponente();
		
		calcularCh();
		
		abaSelecionada = "tab"+semestre;

		return null;
	}

	/**
	 * Verifica as regras de equival�ncia, pr� e co-requisito do componente a
	 * ser adicionado no conjunto de componentes que j� pertencem ao curr�culo.
	 * 
	 * @param ccc
	 * @return
	 * @throws ArqException
	 */
	private String validarRequisitosEquivalencia(CurriculoComponente ccc) throws ArqException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		ComponenteCurricular cc = ccc.getComponente();
		// Analisar equival�ncias
		String equivalencia = cc.getEquivalencia();
		Map<String, Object> componenteInvalido = new HashMap<String, Object>();
		if (!isEmpty(equivalencia)) {
			if (!verificaExpressao(componenteInvalido, equivalencia, dao, "equivalencia", null, false))
				return "O componente curricular " + cc.getDescricao() + " possui express�o de equival�ncia mal formada.";
		}
		
		// Analisar pr�-requisitos
		String preRequisito = cc.getPreRequisito();
		if (!isEmpty(preRequisito)) {
			if (!verificaExpressao(componenteInvalido, preRequisito, dao, "preRequisito", null, true))
				return "O componente curricular " + cc.getDescricao() + " possui express�o de pr�-requisito mal formada.";
		}
		
		String coRequisito = cc.getCoRequisito();
		// Analisar co-requisitos
		if (!isEmpty(coRequisito)) {
			if (!verificaExpressao(componenteInvalido, coRequisito, dao, "coRequisito", null, true))
				return "O componente curricular " + cc.getDescricao() + " possui express�o de co-requisito mal formada.";
		}
		
		// n�o pode existir nenhum componente equivalente j� no curr�culo
		if (cc.getEquivalencia() != null
				&& !cc.getEquivalencia().trim().equals("")) {
			boolean possuiEquivalencia = ExpressaoUtil.eval(cc
					.getEquivalencia(), mapToListComponentesCurriculares());
			if (possuiEquivalencia) {
				Collection<ComponenteCurricular> componentes = ArvoreExpressao.getMatchesComponentes(cc.getEquivalencia(), mapToListComponentesCurriculares());
				List<ComponenteCurricular> equivalentes = new ArrayList<ComponenteCurricular>();
				for (ComponenteCurricular equivalente : componentes) {
					equivalente = dao.findCodigoNomeById(equivalente.getId(), true);
					equivalentes.add(equivalente);
				}
				StringBuilder sb = new StringBuilder("O componente " + cc.getDescricaoResumida() + " possui equival�ncia com o(s) componente(s) ");
				sb.append(StringUtils.transformaEmLista(equivalentes));
				sb.append(", que j� est�(�o) presente(s) no curr�culo.");
				return sb.toString();
			}
		}

		// Componentes dos semestres anteriores
		ArrayList<ComponenteCurricular> compSemAnteriores = new ArrayList<ComponenteCurricular>(
				0);
		for (int s = 1; s <= ccc.getSemestreOferta(); s++) {			
			ArrayList<CurriculoComponente> componentes = componentesAdicionados.get(s);
			if(isEmpty(componentes))
				continue;
			for (CurriculoComponente currComp : componentes) {
				compSemAnteriores.add(currComp.getComponente());
			}
		}

		/*
		 * O componente a ser adicionado deve ter seus pr�-requisitos nos
		 * semestres anteriores, mas nenhum pr�-requisito nos semestres
		 * posteriores e no mesmo semestre
		 */
		if (cc.getPreRequisito() != null && !ExpressaoUtil.eval(cc.getPreRequisito(), compSemAnteriores) && ccc.getSemestreOferta() <= obj.getSemestreConclusaoIdeal())
			return "O componente " + cc.getDescricaoResumida() + " deve ter todos os pr�-requisitos nos n�veis anteriores";

		return null;
	}
	
	/** Verifica se a express�o � v�lida.
	 * @param componenteInvalido
	 * @param preRequisito
	 * @param componenteDao
	 * @param tipoExpressao
	 * @param cacheComponente 
	 * @return
	 */
	private boolean verificaExpressao(Map<String, Object> componenteInvalido, String preRequisito, ComponenteCurricularDao componenteDao, String tipoExpressao, Map<Integer, String> cacheComponente, boolean ativos) {
		String expressao = null;
		boolean valido = true;
		try {
			expressao = ExpressaoUtil.buildExpressaoFromDB(preRequisito, componenteDao, ativos, cacheComponente);
			componenteInvalido.put(tipoExpressao, expressao);
			ArvoreExpressao.fromExpressao(preRequisito);
		} catch (Exception e) {
			valido = false;
			if (expressao == null)
				componenteInvalido.put(tipoExpressao, "Express�o com componente curricular inativo/inv�lido.");
			componenteInvalido.put(tipoExpressao + "Invalido", true);
		}		
		return valido;
	}

	/**
	 * Adiciona componentes no curr�culo de cursos STRICTO ou altera os dados do
	 * CurriculoComponente, caso seja altera��o.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String adicionarDisciplina() throws ArqException {
		erros = new ListaMensagens();
		erros.addAll(curriculoComponente.validate(getNivelEnsino()));
		if (hasErrors())
			return null;

		if (!podeAlterarChEObrigatorias) {
			curriculoComponente.setObrigatoria(false);
		}

		if (curriculoComponente.getObrigatoria() && !podeAlterarChEObrigatorias) {
			addMensagemErro("J� existe alunos associados a esta estrutura curricular. N�o � poss�vel adicionar componente obrigat�rias a esta estrutura curricular.");
			return null;
		}

		GenericDAO dao = getGenericDAO();
		// Popular , adicionar e ordenar
		CurriculoComponente cc = new CurriculoComponente();
		cc.setComponente(dao.findByPrimaryKey(curriculoComponente.getComponente().getId(), ComponenteCurricular.class));
		cc.setObrigatoria(curriculoComponente.getObrigatoria());
		cc.setAreaConcentracao(dao.findByPrimaryKey(curriculoComponente.getAreaConcentracao().getId(), AreaConcentracao.class));
		cc.setCurriculo(obj);
		if (obj.getCurriculoComponentes() == null)
			obj.setCurriculoComponentes(new ArrayList<CurriculoComponente>(0));
		
		boolean componentesIguais = false;
		for (CurriculoComponente curriculo : obj.getCurriculoComponentes()){
			if (curriculo.equals(cc) && (curriculo.getAreaConcentracao() == null || curriculo.getAreaConcentracao().equals(cc.getAreaConcentracao()))){
				componentesIguais = true;
				break;
			}
		}		
		if (componentesIguais) {
			addMensagemErro("Disciplina j� foi incluida.");
			return null;
		}
		obj.getCurriculoComponentes().add(cc);
		Collections.sort(obj.getCurriculoComponentes());
		curriculoComponente.setComponente(new ComponenteCurricular());
		return null;
	}
	
	/**
	 * Adiciona componentes no curr�culo de Resid�ncia M�dica <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String adicionarDisciplinaResidenciaMedica() throws ArqException {
		ListaMensagens lista = new ListaMensagens();

		if (ValidatorUtil.isEmpty(nivelDisciplinaResidencia))
			lista.addErro("N�vel da Disciplina: Campo obrigat�rio n�o informado.");
		if (ValidatorUtil.isEmpty(curriculoComponente.getComponente().getId()))
			lista.addErro("Disciplina � um campo obrigat�rio.");

		addMensagens(lista);

		if (lista != null && !lista.isEmpty())
			return null;

		GenericDAO dao = getGenericDAO();
		// Popular , adicionar e ordenar
		CurriculoComponente cc = new CurriculoComponente();
		cc.setComponente(dao.findByPrimaryKey(curriculoComponente.getComponente().getId(), ComponenteCurricular.class));
		cc.setObrigatoria(curriculoComponente.getObrigatoria());
		cc.setSemestreOferta(nivelDisciplinaResidencia);
		cc.setCurriculo(obj);
		if (obj.getCurriculoComponentes() == null)
			obj.setCurriculoComponentes(new ArrayList<CurriculoComponente>(0));

		if (obj.getCurriculoComponentes().contains(cc)) {
			addMensagemErro("N�o foi poss�vel adicionar essa disciplina.");
			return null;
		}
		obj.getCurriculoComponentes().add(cc);
		componentesAdicionados.get(cc.getSemestreOferta()).add(cc);
		Collections.sort(obj.getCurriculoComponentes());
		curriculoComponente.setComponente(new ComponenteCurricular());
		return null;
	}

	/**
	 * Adiciona os componentes escolhidos como obrigat�rios e complementares
	 * PARA GRADUA��O. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/busca_componente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String adicionarComponente() throws ArqException {
		GenericDAO dao = getGenericDAO();
		String[] obrigatorias = getCurrentRequest().getParameterValues("obrigatorias");
		String[] optativas = getCurrentRequest().getParameterValues("optativas");
		int adicionados = 0;
		StringBuffer ccErros = new StringBuffer();
		if (obrigatorias != null) {
			if (obj.getId() > 0 && !podeAlterarChEObrigatorias) {
				addMensagemErro("J� existe alunos associados a esta estrutura curricular. N�o � poss�vel adicionar componente obrigat�rias a esta estrutura curricular.");
				return null;
			}
			adicionados += obrigatorias.length;
			for (String obr : obrigatorias) {
				ComponenteCurricular cc = dao.findByPrimaryKey(new Integer(obr), ComponenteCurricular.class);
				String msg = addComponente(cc, curriculoComponente.getSemestreOferta(), true);
				if (msg != null)
					ccErros.append(msg + "<br>");
			}
		}
		if (optativas != null) {
			adicionados += optativas.length;
			for (String opt : optativas) {
				ComponenteCurricular cc = dao.findByPrimaryKey(new Integer(opt), ComponenteCurricular.class);
				String msg = addComponente(cc, curriculoComponente.getSemestreOferta(), false);
				if (msg != null)
					ccErros.append(msg + "<br>");
			}
		}
		if (ccErros.length() > 0) {
			addMensagemErro("N�o foi poss�vel adicionar tais componentes: <br>"
					+ ccErros);
		}
		if (isGraduacao()) {
			verificarCreditosExcedentes();
		}

		if (adicionados > 0 && ccErros.length() == 0)
			addMensagemInformation("Todos os Componentes foram Adicionados com Sucesso.");
		if (adicionados == 0)
			addMensagemInformation("Nenhum componente foi selecionado.");
		else if (ccErros.length() > 0 && ccErros.length() < adicionados)
			addMessage("Apenas Alguns Componentes foram Adicionados com Sucesso.", TipoMensagemUFRN.WARNING);
		curriculoComponente = new CurriculoComponente();
		return telaComponentes();
	}

	/**
	 * Verifica se existem mais cr�ditos cadastrados do que o permitido para a etrutura curricular e, caso existam,
	 * adiciona uma mensagem de warning informando ao usu�rio.
	 * @throws DAOException 
	 */
	private void verificarCreditosExcedentes() throws DAOException {
		int crSemestre = 0;
		boolean hasExcedentes = false;
		StringBuilder sb = new StringBuilder();
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(getNivelEnsino());
		for(int semestre = 1;semestre <= componentesAdicionados.size();semestre++){
			List<CurriculoComponente> lista = componentesAdicionados.get(semestre);
			if (lista != null) {
				for (CurriculoComponente ccc : lista) {
					if(ccc.getObrigatoria()) {
						if(ccc.getComponente().isDisciplina() || ccc.getComponente().isBloco())
							crSemestre += ccc.getComponente().getCrTotal();
						else {
							crSemestre += ccc.getComponente().getChTotal() / parametros.getHorasCreditosAula();
						}
					}
				}
			}
	
			if (crSemestre > obj.getCrMaximoSemestre()){
				if(!hasExcedentes)
					sb.append("Aten��o!<br>");
				sb.append("<p>O "
						+ semestre
						+ "� per�odo possui " + crSemestre
						+ " cr�ditos obrigat�rios, quando o n�mero m�ximo informado � de "
						+ obj.getCrMaximoSemestre() + " cr�ditos por per�odo.</p>");
				hasExcedentes = true;
			}
			
			crSemestre = 0;
		}
		if(hasExcedentes){
			addMensagemWarning(sb.toString());
		}
	}

	/**
	 * Lista componentes de acordo com os crit�rios passados.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/busca_componente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscarComponentes() throws DAOException {
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		ComponenteCurricular cc = curriculoComponente.getComponente();
		String codigo = null;
		String nome = null;
		TipoComponenteCurricular tipo = null;
		Unidade unidade = null;
		boolean filtroTotal = true;
		if (isFiltroCodigoComponente()) {
			ValidatorUtil.validateRequired(cc.getCodigo(), "C�digo", erros);
			codigo = cc.getCodigo();
		}	
		if (isFiltroNome()) {
			ValidatorUtil.validateRequired(cc.getNome(), "Nome", erros);
			nome = cc.getNome();
		}	
		if (isFiltroTipo()) {
			ValidatorUtil.validateRequired(cc.getTipoComponente(), "Tipo", erros);
			tipo = cc.getTipoComponente();
		}
		if (isFiltroUnidade()) {
			ValidatorUtil.validateRequired(cc.getUnidade(), "Unidade Acad�mica", erros);
			unidade = cc.getUnidade();
		}
		if (hasErrors()) return null;
		if (codigo == null && nome == null && tipo == null && unidade == null)
			addMensagemErro("Por favor, escolha algum crit�rio de busca");
		else
			try {
				if (getUsuarioLogado() != null) {
					if (isUserInRole(SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG))
						filtroTotal = false;
				}
				componentesEncontrados = dao.findCompleto(codigo, nome, tipo, unidade, getNivelEnsino(), filtroTotal);
				
				if (componentesEncontrados.isEmpty())
					addMensagemErro("Nenhum componente curricular encontrado de acordo com os crit�rios de busca informados.");
				
				if (componentesEncontrados.size() == 2000)
					addMensagemErro("A busca ultrapasso o limite de 2000 registros, refine mais sua busca");

			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				componentesEncontrados = new ArrayList<ComponenteCurricular>(0);
				return null;
			}
		return null;
	}

	/**
	 * Retira um componente de semestre, e atualiza a soma dos cr�ditos.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws ArqException 
	 * @throws
	 * @throws DAOException
	 * @throws Exception
	 */
	public void removerComponente(ActionEvent evt) throws ArqException {
		List<CurriculoComponente> lista = obj.getCurriculoComponentes();
		if (isGraduacao()) {
			try{
				int semestre = getParameterInt("semestre");
				lista = componentesAdicionados.get(semestre);
				validarLista(lista);
			}catch(NullPointerException e){
				validarLista(null);
			}
			if(hasErrors())
				cancelar();
		}
		
		int idComp = getParameterInt("idComponente");

		if (!lista.isEmpty()){
			
			CurriculoComponente cc = recuperarComponente(idComp, lista);
			
			if(isGraduacao()){
				cc.getComponente().preencherInversos(mapToListComponentesCurriculares());
				
				if(cc.getComponente().getInversosPreRequisitos() != null){
					ArrayList<ComponenteCurricular> compSemPosteriores = new ArrayList<ComponenteCurricular>(0);
					for (int s = cc.getSemestreOferta(); s <= componentesAdicionados.size(); s++) {			
						ArrayList<CurriculoComponente> componentes = componentesAdicionados.get(s);
						if(isEmpty(componentes))
							continue;
						for (CurriculoComponente currComp : componentes) {
							compSemPosteriores.add(currComp.getComponente());
						}
					}
			
					for (ComponenteCurricular inversoPre : cc.getComponente().getInversosPreRequisitos()) {
						if(compSemPosteriores.contains(inversoPre))
							addMensagemErro("O componente "+cc.getDescricao()+" � pr�-requisito para o componente "+inversoPre.getDescricao()+
									" e s� pode ser removido caso "+inversoPre.getDescricao()+" seja removido primeiro.");
					}
				}
				if(hasErrors()){
					redirectTelaComponentes();
					return;
				}
			}
			
			
			removeCurriculoComponente(lista, cc);
			
			if (obj != null && obj.getCurriculoComponentes() != null)
				Collections.sort(obj.getCurriculoComponentes());
			
			if(hasErrors()){
				redirectTelaComponentes();
				return;
			}
			
			addMensagem( MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Componente "+cc.getDescricao() );
			
			if(isGraduacao()) {
				recriarTabelaComponente();
				verificarCreditosExcedentes();
			}
		}
		
		redirectTelaComponentes();
	}
	
	/**
	 * Retira um componente de semestre, e atualiza a soma dos cr�ditos.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws ArqException 
	 * @throws
	 * @throws DAOException
	 * @throws Exception
	 */
	public String escolherComponenteSubstituto() throws ArqException {
		int idComp  = 0;
		if (componenteCurriculoASubstituir  != null)
			idComp = componenteCurriculoASubstituir.getComponente().getId();
		else
			idComp = getParameterInt("idComponente");
		if (!obj.getCurriculoComponentes().isEmpty()){
			componenteCurriculoASubstituir = recuperarComponente(idComp, obj.getCurriculoComponentes());
			if (obj.getId() > 0 && !podeAlterarChEObrigatorias && componenteCurriculoASubstituir.getObrigatoria() != null && componenteCurriculoASubstituir.getObrigatoria().booleanValue()) {
				addMensagemErro("J� existe alunos associados a esta estrutura curricular. N�o � poss�vel adicionar componente obrigat�rias a esta estrutura curricular.");
				return null;
			}
			ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
			return mBean.buscarComponente(this, "Substituir Componente Curricular", null, false, false);
		} else {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
	}
	
	/** Retorna � lista de componentes a subsitituir.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/confirma_substituicao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaBuscaComponenteSubstituto() {
		return redirectJSF("graduacao/componente/lista.jsf");
	}
	
	/**
	 * Recupera o {@link CurriculoComponente} de id passado como par�metro presente na lista tamb�m passada como par�metro.
	 * Caso n�o seja encontrado tal componente, � retornado valor nulo.
	 * 
	 * @param id
	 * @param lista
	 * @return
	 */
	private CurriculoComponente recuperarComponente(int id, List<CurriculoComponente> lista){
		CurriculoComponente cc = null;
		for (CurriculoComponente curriculoComponente : lista) {
			if(curriculoComponente.getComponente().getId() == id){
				cc = curriculoComponente;
				break;
			}
		}
		return cc;
	}

	/**
	 * Remove o {@link CurriculoComponente} da lista passada como par�metro.
	 * 
	 * @param lista
	 * @param cc
	 * @throws DAOException
	 */
	private void removeCurriculoComponente(List<CurriculoComponente> lista, CurriculoComponente cc) throws DAOException {
		try {
			CurriculoComponenteValidator.verificarGrupoOptativa(cc);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			addMensagemWarning("� necess�rio remover antes do Grupo de Optativas.");
			return ;
		}

		if (obj.getId() > 0 && cc.getObrigatoria() && !podeAlterarChEObrigatorias) {
			addMensagemErro("J� existe alunos associados a esta estrutura curricular. N�o � poss�vel adicionar componente obrigat�rias a esta estrutura curricular.");
			return ;
		}
		
		if (lista.remove(cc) == false) {
			addMensagemErro("N�o foi poss�vel remover componente:" + cc.getComponente().getDescricao());
			return ;
		}
		if (isGraduacao() && !cc.getObrigatoria()) {
			tmpChOptativa = tmpChOptativa - cc.getComponente().getChTotal();
		}
	}
	
	/**
	 * Reinicializa a lista de componentes do curriculo
	 * 
	 * @throws DAOException
	 */
	private void recriarTabelaComponente() throws DAOException {
		List<CurriculoComponente> listaAtualizada = new ArrayList<CurriculoComponente>();
		
		for (Integer k : componentesAdicionados.keySet()) {
			listaAtualizada.addAll( componentesAdicionados.get(k) );
		}		
		
		int numSemestres = componentesAdicionados.size();
		
		componentesAdicionados = null;
		
		converterParaTreeMap(listaAtualizada, numSemestres);
		
		calcularCh();
	}

	/**
	 * Altera um CurriculoComponete para obrigat�rio ou n�o obrigat�rio.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/curriculo/componentes.jsp</li>
	 * <li>sigaa.war/complexo_hospitalar/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws Exception
	 */
	public void alterarDisciplinaOptativaObrigatoria(ActionEvent evento)
			throws Exception {
		List<CurriculoComponente> lista = obj.getCurriculoComponentes();

		if (isGraduacao()) {
			int semestre = getParameterInt("sem");
			lista = componentesAdicionados.get(semestre);
		}

		int linha = getParameterInt("linhaComponente");

		if(!lista.isEmpty()){
			CurriculoComponente temporario = lista.get(linha - 1);
	
			if (lista.get(linha - 1).getObrigatoria()) {
				temporario.setObrigatoria(false);
			} else {
				temporario.setObrigatoria(true);
			}
	
			lista.remove(linha - 1);
			lista.add(linha - 1, temporario);
		}
		redirectTelaComponentes();
	}

	/**
	 * M�todo respons�vel por alternar o tipo de integraliza��o do CurriculoComponete entre obrigat�rio ou n�o obrigat�rio.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @throws ArqException 
	 * @throws
	 * @throws DAOException
	 * @throws Exception
	 */
	public void alternarIntegralizacaoOptativaObrigatoria(ActionEvent evt) throws ArqException {
		List<CurriculoComponente> lista = obj.getCurriculoComponentes();
		if (isGraduacao()) {
			try{
				int semestre = getParameterInt("semestre");
				lista = componentesAdicionados.get(semestre);
				validarLista(lista);
			}catch(NullPointerException e){
				validarLista(null);
			}
			if(hasErrors()) cancelar();
		}
		int idComp = getParameterInt("idComponente");
		if (!lista.isEmpty()){
			CurriculoComponente cc = recuperarComponente(idComp, lista);
			if(isGraduacao()){
				cc.getComponente().preencherInversos(mapToListComponentesCurriculares());
				cc.setObrigatoria(!cc.getObrigatoria());
			}
			if (obj != null && obj.getCurriculoComponentes() != null)
				Collections.sort(obj.getCurriculoComponentes());
			if(hasErrors()){
				redirectTelaComponentes();
				return;
			}
			if(isGraduacao()) {
				recriarTabelaComponente();
				verificarCreditosExcedentes();
			}
		}
		redirectTelaComponentes();
	}
	
	/**
	 * Valida todo passo de preenchimento dos componentes optativos e
	 * obrigat�rios, depois de adicionados.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * <li>sigaa.war/stricto/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterComponentes() throws DAOException {
		if (!checkOperacaoAtiva(1))
			return cancelar();

		if (isGraduacao()) {
			if (componentesAdicionados != null && componentesAdicionados.size() == 0) {
				addMensagemErro("Adicione pelo menos um componente curricular");
				return null;
			}
		} else {
			if (obj.getCurriculoComponentes() == null || obj.getCurriculoComponentes().isEmpty()) {
				addMensagemErro("Adicione pelo menos um componente curricular");
				return null;
			}
		}

		if (isGraduacao()) {
			if (isEmpty(chOptativaSemestre)) {
				chOptativaSemestre = new ArrayList<OptativaCurriculoSemestre>();
				for (Integer semestre : getSemestres()) {
					OptativaCurriculoSemestre o = new OptativaCurriculoSemestre();
					o.setCurriculo(obj);
					o.setSemestre(semestre);
					chOptativaSemestre.add(o);
				}
			}
		}
		
		if(!isResidencia() && !isStricto())
			validarNiveisCadastrados();
		EstruturaCurricularDao estruturaCurriculardao = getDAO(EstruturaCurricularDao.class);
		ComponenteCurricularDao componenteCurriculardao = getDAO(ComponenteCurricularDao.class);
		
		if (hasErrors())
			return null;
		
		/** executando os c�lculos do curr�culo para exibir na p�gina de resumo */
		if (isGraduacao()) {
			obj.setCurriculoComponentes(mapToListCurriculoComponentes());
		} else if (isResidencia()){
			//Ainda ser� verificado essa quest�o com o usu�rio final
			if(obj.getChOptativasMinima() == null)
				obj.setChOptativasMinima(new Integer(0));
			if(obj.getChMinimaSemestre() == null)
				obj.setChMinimaSemestre(new Integer(0));
		}
		CurriculoHelper.calcularTotaisCurriculo(obj);

		erros.addAll(CurriculoValidator.validacaoGeral(obj, estruturaCurriculardao, componenteCurriculardao));
		
		if (hasErrors())
			return null;
		
		if (isGraduacao()){
			if(isEmpty(obj.getTccDefinitivo()))
				obj.setTccDefinitivo(new ComponenteCurricular());
			
			idTccDefinitivo = obj.getTccDefinitivo().getId();
			componentesTcc = new ArrayList<ComponenteCurricular>();
			for(ComponenteCurricular cc: obj.getComponentesCurriculares())
				if(cc.isAtividade() && cc.isTrabalhoConclusaoCurso())
					componentesTcc.add(cc);
			return telaTccDefinitivo();
		}
		else
			return telaResumo();
	}
	
	/**
	 * Valida a escolha do componente curricular determinado para ser
	 * o Trabalho de Conclus�o de Curso definitivo para o curr�culo.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/tcc_definitivo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterTccDefinitivo() throws DAOException {
		
		if(idTccDefinitivo != null) {
			if (isNotEmpty(idTccDefinitivo) && !idTccDefinitivo.equals(obj.getTccDefinitivo().getId())) 
				obj.setTccDefinitivo(getGenericDAO().findByPrimaryKey(idTccDefinitivo, ComponenteCurricular.class));
			 else if (isEmpty(idTccDefinitivo)) 
				obj.setTccDefinitivo(null);
		}
		
		exibirSemestre = 1;
		return telaChOptativaSemestre();
	}

	/**
	 * Verifica se existem mais niveis cadastrados do que o permitido para a determinada estrutura curricular ou se existem n�veis vazios.
	 */
	private void validarNiveisCadastrados() {
		if (componentesAdicionados!=null && hasNiveisxcedentes()) {
			gerarErroNiveisExcedentes();
		}
	}

	/**
	 * Gera a mensagem de erro a ser mostrada para o usu�rio caso ele tente avan�ar da p�gina de componentes
	 * com um ou mais n�vel(is) excedentes cadastrados para a estrutura.
	 */
	private void gerarErroNiveisExcedentes() {
		StringBuffer sb = new StringBuffer();
		for(int i = componentesAdicionados.keySet().size(); i >= obj.getSemestreConclusaoIdeal()+1;i--)
			sb.append("- "+i+"�<br />");
		addMensagemErro("Existem n�veis excedentes para a estrutura curricular escolhida. " +
				"Para continuar, os seguintes n�veis devem ser removidos:<br />"+
				sb.toString());
	}
	
	/**
	 * Retorna <code>true</code> caso existam n�veis excedentes cadastrados e <code>false</code> caso contr�rio.
	 * @return
	 */
	private boolean hasNiveisxcedentes(){
		boolean result = false;
		if(componentesAdicionados.keySet().size() > obj.getSemestreConclusaoIdeal())
			result = true;
		return result;
	}

	/**
	 * Redireciona para o formul�rio de dados gerais.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/aproveitamento.jsp</li>
	 * <li>sigaa.war/graduacao/curriculo/tcc_definitivo.jsp</li>
	 * <li>sigaa.war/graduacao/curriculo/ch_optativa_semestre.jsp</li>
	 * <li>sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * <li>sigaa.war/graduacao/curriculo/resumo.jsp</li>
	 * </ul>
	 */
	public String voltarDadosGerais() {
		desabilitaAproveitamento = false;
		return telaDadosGerais();
	}

	/**
	 * Redireciona para o formul�rio de componentes. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/ch_optativa_semestre.jsp</li>
	 * <li>sigaa.war/graduacao/curriculo/resumo.jsp</li>
	 * </ul>
	 * 
	 */
	public String voltarComponentes() {
		return telaComponentes();
	}

	/**
	 * Redireciona para o formul�rio de cadastro de carga hor�ria optativa por
	 * n�vel.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/resumo.jsp</li>
	 * </ul>
	 */
	public String voltarChOptativa() {
		return telaChOptativaSemestre();
	}

	/**
	 * Redireciona para o formul�rio de dados gerais verificando a opera��o
	 * ativa.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/componentes.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/resumo.jsp</li>
	 * </ul>
	 * 
	 */
	public String telaDadosGerais() {
		if (!checkOperacaoAtiva(1))
			return cancelar();
		return forward("/graduacao/curriculo/geral.jsf");
	}

	/**
	 * Redireciona para o formul�rio de componentes curriculares. <br />
	 * M�todo n�o invocado por JSP.
	 */
	private void redirectTelaComponentes() {
		if (isGraduacao())
			redirectJSF("/graduacao/curriculo/componentes.jsf");
		else if (isStricto())
			redirectJSF("/stricto/curriculo/componentes.jsf");
		else if (isResidencia())
			forward("/complexo_hospitalar/curriculo/componentes.jsf");
	}
	
	/**
	 * Redireciona para o formul�rio de componentes curriculares. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/busca_componente.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/resumo.jsp</li>
	 * </ul>
	 */
	public String telaComponentes() {

		if (!checkOperacaoAtiva(1))
			return cancelar();
		if (isGraduacao())
			return forward("/graduacao/curriculo/componentes.jsf");
		else if (isStricto())
			return forward("/stricto/curriculo/componentes.jsf");
		else if (isResidencia())
			return forward("/complexo_hospitalar/curriculo/componentes.jsf");
		addMensagemErro("N�o foi poss�vel determinar o n�vel de ensino do curr�culo. Por favor, reinicie a opera��o.");
		return cancelar();
	}

	/**
	 * Inicia o caso de uso de cadastro de curr�culo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		setReadOnly(false);
		checkRole();
		setOperacaoAtiva(1);
		componentesAdicionados = null;
		setConfirmButton("Cadastrar");
		initObj();
		prepareMovimento(SigaaListaComando.CADASTRAR_CURRICULO);
		return telaDadosGerais();
	}

	/**
	 * Redireciona para o formul�rio de optativas verificando a opera��o ativa. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Nenhuma</li>
	 * </ul>
	 */
	public String telaChOptativaSemestre() {
		if (!checkOperacaoAtiva(1))
			return cancelar();
		return forward("/graduacao/curriculo/ch_optativa_semestre.jsf");
	}
	
	/**
	 * Redireciona para o formul�rio de optativas verificando a opera��o ativa. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/ch_optativa_semestre.jsp</li>
	 * <li>sigaa.war/graduacao/curriculo/resumo.jsp</li>
	 * </ul>
	 */
	public String telaTccDefinitivo() {
		if (!checkOperacaoAtiva(1))
			return cancelar();
		return forward("/graduacao/curriculo/tcc_definitivo.jsf");
	}

	/**
	 * Submete a p�gina de carga hor�ria optativas e Redireciona para o
	 * formul�rio de resumo do curr�culo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/ch_optativa_semestre
	 * .jsp</li>
	 * </ul>
	 */
	public String submeterChOptativaSemestre() {

		int total = 0;
		for (OptativaCurriculoSemestre optativa : chOptativaSemestre)
			total += optativa.getCh();

		if ( obj.getAtivo() && total != obj.getChOptativasMinima() ) {
			addMensagemErro("A quantidade de horas distribu�das (" + total
					+ ") deve ser igual a quantidade de horas dispon�veis ("
					+ obj.getChOptativasMinima() + ")");
			return null;
		}

		if (!checkOperacaoAtiva(1))
			return cancelar();
		return telaResumo();
	}

	/**
	 * Retorna a quantidade de semestres. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/estrutura_curricular/resumo.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/componentes.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/relatorio.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/resumo.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/curso/relatorio_curriculo.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/curso/resumo_curriculo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<Integer> getSemestres() {
		if (componentesAdicionados != null) {
			ArrayList<Integer> keys = new ArrayList<Integer>();
			for (Integer k : componentesAdicionados.keySet()) {
				keys.add(k);
			}
			return keys;
		} else
			return null;
	}

	/**
	 * Converte o mapa de componentes (organizados por semestre) em uma �nica
	 * lista de CurriculoComponente.
	 * 
	 * @return
	 */
	private List<CurriculoComponente> mapToListCurriculoComponentes() {
		ArrayList<CurriculoComponente> list = new ArrayList<CurriculoComponente>(
				0);
		if (componentesAdicionados != null) {
			for (Integer k : componentesAdicionados.keySet()) {
				list.addAll(componentesAdicionados.get(k));
			}
		}
		return list;
	}

	/**
	 * Converte o mapa de componentes (organizados por semestre) em uma �nica
	 * lista de ComponenteCurricular.
	 * 
	 * @return
	 */
	private List<ComponenteCurricular> mapToListComponentesCurriculares() {
		ArrayList<ComponenteCurricular> list = new ArrayList<ComponenteCurricular>(
				0);
		for (Integer k : componentesAdicionados.keySet()) {
			for (CurriculoComponente ccc : componentesAdicionados.get(k)) {
				list.add(ccc.getComponente());
			}
		}
		return list;
	}

	/**
	 * Cadastra e altera curr�culo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/resumo.jsp</li>
	 * <li>sigaa.war/stricto/curriculo/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarCurriculo() throws ArqException {
		
		if(!isOperacaoAtiva(1)){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			redirectJSF(getSubSistema().getLink());
			return null;
		}
		
		if (isGraduacao()) {
			obj.setCurriculoComponentes(mapToListCurriculoComponentes());
		} else {
			obj.setMatriz(null);
		}

		if (!confirmaSenha())
			return null;

		if ("Inativar".equalsIgnoreCase(getConfirmButton()) || "Ativar".equalsIgnoreCase(getConfirmButton())) {
			return inativarOuAtivarCurriculo();
		}		
		
		if (!isResidencia() && !isStricto())
			validarNiveisCadastrados();
		
		if (hasErrors())
			return null;
				
		Comando comando = SigaaListaComando.CADASTRAR_CURRICULO;
		if (obj.getId() != 0)
			comando = SigaaListaComando.ALTERAR_CURRICULO;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		mov.setColObjMovimentado(chOptativaSemestre);

		try {
			execute(mov);
			if (comando.equals(SigaaListaComando.CADASTRAR_CURRICULO)) {
				addMessage("Estrutura Curricular cadastrada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			} else {
				addMessage("Estrutura Curricular alterada com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			}
			return cancelar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} finally {
			initObj();
			removeOperacaoAtiva();
		}
	}

	/**
	 * Coloca o curr�culo como inativo, n�o exclui. N�o se
	 * deve cadastrar discentes em curr�culos desativados.
	 * 
	 * @return
	 * @throws ArqException
	 */
	private String inativarOuAtivarCurriculo() throws ArqException {

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.INATIVAR_ATIVAR_CURRICULO);
		mov.setObjMovimentado(obj);
		try {
			execute(mov);
			addMensagemInformation("Curr�culo foi "+ (obj.getAtivo() ? "a" : "ina")+"tivado com sucesso.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e, "N�o foi possivel inativar este curriculo.");
			return null;
		}

		return cancelar();
	}

	/**
	 * Verifica se o usu�rio tem permiss�o para executar a a��o.
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 * @throws SegurancaException
	 */
	public void checkRole() throws SegurancaException {
		if (isGraduacao())
			checkRole(SigaaPapeis.CDP);
		else if(isMostrarCoordenadorOpcaoCadastrarEstruturaCurricular()) {
			checkRole(SigaaPapeis.PPG, SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, 
					SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA,SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		}
		else {
			checkRole(SigaaPapeis.PPG, SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, 
					SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
		}
	}

	/**
	 * Atualiza um curr�culo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {

		try {
			checkRole(SigaaPapeis.CDP, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
			EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
			setConfirmButton("Alterar");
			prepareMovimento(SigaaListaComando.ALTERAR_CURRICULO);
			setId();
			setReadOnly(false);
			obj = dao.findByPrimaryKey(obj.getId(), Curriculo.class);
			obj.getCurso().setUnidade(dao.refresh(obj.getCurso().getUnidade()));
			if (!isEmpty( obj.getCurriculoComponentes() )) {
				if (obj.getCurriculoComponentes().iterator().hasNext())
					obj.getCurriculoComponentes().iterator().next();
			}
			
			if (isGraduacao()) {
				// tmpChOptativa = obj.getChOptativasMinima();
				carregarMatrizes(obj.getMatriz().getCurso().getId(), null);
				desabilitaAproveitamento = true;
				aproveitarComponentes = false;
				componentesAdicionados = null;
				converterParaTreeMap(dao.findCurriculoComponentesByCurriculo(obj.getId()), dao.findMaxSemestreOferta(obj));

				List<CurriculoComponente> lista = mapToListCurriculoComponentes();
				for (CurriculoComponente curriculoComponente : lista) {
					if (!curriculoComponente.getObrigatoria())
						tmpChOptativa += curriculoComponente.getComponente().getChTotal();
				}

			} else {
				List<CurriculoComponente> ccs = (List<CurriculoComponente>) dao.findCurriculoComponentesByCurriculo(obj.getId());
				obj.setCurriculoComponentes(ccs);
				selecionarPrograma(null);
			}

			podeAlterarChEObrigatorias = true;
			if (isUserInRole(SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO)) {
				podeAlterarChEObrigatorias = true;
			} else {
				if (obj.getId() > 0) {
					int totalDiscentes = dao.countDiscentesByCurriculo(obj.getId());
					/**
					 * se a opera��o � alterar curr�culo e existe alguma
					 * discente vinculado a este curr�culo n�o pode alterar ch
					 * optativa nem as componentes obrigat�rias
					 */
					if (totalDiscentes > 0) {
						if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP))
							addMensagemWarning("H� discentes vinculados ao curr�culo.");
						else
							podeAlterarChEObrigatorias = false;
					}
				}
				if (!podeAlterarChEObrigatorias) {
					podeAlterarChEObrigatorias = obj.isAberto();
				}
			}

			// Carrega CH optativa por semestre
			if (isGraduacao()) {
				chOptativaSemestre = getDAO(EstruturaCurricularDao.class).findOptativasCurriculoSemestreByCurriculo(obj);

				if (isEmpty(chOptativaSemestre)) {
					chOptativaSemestre = new ArrayList<OptativaCurriculoSemestre>();
					for (Integer semestre : getSemestres()) {
						OptativaCurriculoSemestre o = new OptativaCurriculoSemestre();
						o.setCurriculo(obj);
						o.setSemestre(semestre);
						chOptativaSemestre.add(o);
					}
				}
			}

			setOperacaoAtiva(1);
			return telaDadosGerais();
		} catch (SegurancaException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
	}

	/**
	 * Converte uma cole��o de componentes curriculares em um tree map onde a
	 * chave � o n�vel da oferta e o valor � uma cole��o dos curr�culos que
	 * est�o no n�vel especificado.
	 * @throws DAOException 
	 */
	private void converterParaTreeMap(Collection<CurriculoComponente> curriculoComponentes, int numSemestres) throws DAOException {
		prepararEstrutura(numSemestres);
		if(!isEmpty(curriculoComponentes)) {
			for (CurriculoComponente cc : curriculoComponentes) {
				try {
					//Pode por algum motivo estar nulo e causar NP
					if(isEmpty(componentesAdicionados)) {
						componentesAdicionados = new TreeMap<Integer, ArrayList<CurriculoComponente>>();
					}
					
					if (componentesAdicionados.get(cc.getSemestreOferta()) == null)
						componentesAdicionados.put(cc.getSemestreOferta(), new ArrayList<CurriculoComponente>(0));
					
					componentesAdicionados.get(cc.getSemestreOferta()).add(cc);
				} catch (Exception e) {
					e.printStackTrace();
					addMensagemErro("Essa estrutura curricular est� mal formada.<br>"
							+ "Foram cadastrados mais componentes do que a quantidade de semestre comporta");
				}
			}
		}		
	}
	
	/**
	 * A��o executada antes de chamar a tela de remo��o. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String preRemover() {
		try {
			checkRole();
		} catch (SegurancaException e) {
			addMensagemErro(e.getMessage());
			return forward(getListPage());
		}

		setId();

		Curriculo curriculoTemp = null;
		try {
			curriculoTemp = getGenericDAO().findByPrimaryKey(obj.getId(), Curriculo.class);
		} catch (DAOException e) {
			tratamentoErroPadrao(e, "Nenhum registro localizado.");
			return null;
		}

		if (isEmpty(curriculoTemp)) {
			addMensagemErro("Registro n�o localizado.");
			return null;
		}

		setOperacaoAtiva(1);
		super.preRemover();
		componentesAdicionados = null;
		setConfirmButton("Remover");
		if (isGraduacao()) {
			try {
				converterParaTreeMap(obj.getCurriculoComponentes(), getDAO(EstruturaCurricularDao.class).findMaxSemestreOferta(obj));
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
				return null;
			}
		} else
			obj.getCurriculoComponentes().iterator();
		return telaResumo();
	}

	/**
	 * Remove o curr�culo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/componentes.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		if (obj == null || obj.getId() == 0) {
			return redirectJSF(getListPage());
		}

		try {
			super.inativar();
			return buscar();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Popula o curr�culo que vai sofrer altera��o de situa��o para Ativo ou Inativo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/curriculo/lista.jsp</li>
	 * <li>/sigaa.war/complexo_hospitalar/curriculo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preInativarOuAtivar() throws ArqException {

		obj = new Curriculo();
		setId();
		obj = getGenericDAO().refresh(obj);
		exibirSemestre = 1;

		if (obj == null) {
			addMensagemErro("Registro n�o foi localizado.");
			return null;
		}
		
		if( obj.getAtivo() == null || obj.getAtivo() ){
			obj.setAtivo(false);
			setConfirmButton("Inativar");
		}else{
			obj.setAtivo(true);
			setConfirmButton("Ativar");
		}
		
		if (isGraduacao()) {
			try {
				converterParaTreeMap(obj.getCurriculoComponentes(), getDAO(EstruturaCurricularDao.class).findMaxSemestreOferta(obj));
			} catch (DAOException e) {
				tratamentoErroPadrao(e);
				return null;
			}
		}
		
		setOperacaoAtiva(1);
		setReadOnly(true);
		setPedirSenha(true);
		prepareMovimento(SigaaListaComando.INATIVAR_ATIVAR_CURRICULO);
		prepareMovimento(ArqListaComando.DESATIVAR);

		return telaResumo();
	}

	/**
	 * Inicia a busca de estruturas curriculares. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/discente/menu_discente.jsp</li>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 */
	public String popularBuscaGeral() throws DAOException {
		initObj();
		initFiltros();
		setResultadosBusca(new ArrayList<Curriculo>(0));
		return forward("/geral/estrutura_curricular/busca_geral.jsf");
	}

	/**
	 * Executa a busca de curr�culos.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/curriculo/busca_componente.jsp</li>
	 * <li>/graduacao/curriculo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		Integer idCurso = null;
		Integer idMatriz = null;
		Integer idPrograma = null;
		String codigo = null;
		if (resultadosBusca != null)
			resultadosBusca.clear();
		
		if (filtroCurso) {
			if (isGraduacao()) {
				if(obj != null && obj.getMatriz() != null && !isEmpty(obj.getMatriz().getCurso())) {					
					idCurso = obj.getMatriz().getCurso().getId(); 
				}
			}
			else {
				if(obj != null && !isEmpty(obj.getCurso())) {				
					idCurso = obj.getCurso().getId();
				}
			}
			
			if(isEmpty(idCurso))
				addMensagemErro("Favor selecionar um Curso.");
		}
		if (filtroPrograma) {
			if(!isEmpty(programa)) {
				idPrograma = programa.getId();
			} else {
				addMensagemErro("Favor selecionar um Programa.");
			}				
		}
		if (filtroMatriz) {
			if( obj != null && !isEmpty(obj.getMatriz())) {
				idMatriz = obj.getMatriz().getId();
			} else {
				addMensagemErro("Favor selecionar uma Matriz.");
			}				
		}
		if (filtroCodigo) {
			
			if( obj != null && !isEmpty(obj.getCodigo())) {
				codigo = obj.getCodigo();
			} else {
				addMensagemErro("Favor digitar um C�digo.");
			}	
		}
		
		if(hasErrors())
			return null;
		
		if (isEmpty(idCurso) && isEmpty(idMatriz) && isEmpty(codigo)
				&& isEmpty(idPrograma)) {
			addMensagemErro("Selecione um filtro de busca e informe o par�metro da busca");
			return null;
		}
		try {
			Collection<Curriculo> curriculos = dao.findCompletoAtivo(idCurso, idMatriz, idPrograma, codigo, somenteAtivas);
			
			if (isEmpty(curriculos))
				addMensagemErro("Nenhuma estrutura curricular encontrada de acordo com os crit�rios de busca informados.");
			else
				setResultadosBusca(curriculos);
		} catch (Exception e) {
			if(e.getCause() instanceof LimiteResultadosException) {
				addMensagemErro(e.getMessage());
			}
			else {
				notifyError(e);
				addMensagemErroPadrao();
				setResultadosBusca(new ArrayList<Curriculo>(0));
			}
		}
		
		if(isPortalCoordenadorStricto()) {
			return redirectJSF("/stricto/curriculo/curso_estrutura.jsf");
		} else{
			return redirectJSF(getListPage());
		}
	}
	
	/**
	 * Usado no cancelamento das opera��es de busca e cadastro de uma estrutura curricular.
	 * Volta para a listagem anteriormente encontrada caso o fluxo seja de buscar/alterar uma estrutura
	 * ou para a p�gina inicial caso o fluxo seja de cadastro.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/curriculo/geral.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception 
	 */
	public String cancelarOperacao() throws Exception {
		if(filtroCurso || filtroCodigo || filtroMatriz || filtroPrograma)
			return buscar();
		return cancelar();
	}

	/**
	 * Redireciona para o formul�rio de listagem de dados
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Nenhuma</li>
	 * </ul>
	 */
	@Override
	public String getListPage() {
		if (isGraduacao() || getNivelEnsino() == ' ' || getNivelEnsino() == NivelEnsino.TECNICO)
			return "/graduacao/curriculo/lista.jsf";
		else if(isStricto())
			return "/stricto/curriculo/lista.jsf";
		else if(isResidencia())
			return "/complexo_hospitalar/curriculo/lista.jsf";
		else  
		return null;
	}

	/**
	 * Redireciona para o formul�rio de cadastro. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Nenhuma</li>
	 * </ul>
	 */
	@Override
	public String getFormPage() {

		return getListPage();
	}

	/**
	 * Redireciona para o formul�rio que mostra informa��es resumidas do
	 * curr�culo.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Nenhuma</li>
	 * </ul>
	 */
	public String telaResumo() {
		if (!checkOperacaoAtiva(1))
			return cancelar();
		if (isGraduacao())
			return forward("/graduacao/curriculo/resumo.jsf");
		else if (isStricto()){
			if (obj != null && obj.getCurriculoComponentes() != null)
				Collections.sort(obj.getCurriculoComponentes());			
			return forward("/stricto/curriculo/resumo.jsf");
		} else if (isResidencia()){
			if (obj != null && obj.getCurriculoComponentes() != null)
				Collections.sort(obj.getCurriculoComponentes());			
			return forward("/complexo_hospitalar/curriculo/resumo.jsf");
		}
		addMensagemErro("N�o foi poss�vel determinar o n�vel de ensino do curr�culo. Por favor, reinicie a opera��o.");
		return cancelar();
	}

	/**
	 * Lista os curr�culos cadastrados. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/menus/cadastro.jsp</li>
	 * <li>sigaa.war/complexo_hospitalar/index.jsp</li>
	 * </ul>
	 */
	public String preListar() throws DAOException {
		initObj();
		if((isStricto() && isUserInRole(SigaaPapeis.PPG)) || (isResidencia() 
				&& isUserInRole(SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA))){
			somenteAtivas = false ;
		}	
		return forward(getListPage());
	}
	
	
	/**
	 * Manda para caso de uso onde o coordenador de curso stricto sensu pode realizar opera��es sobre uma estrutura curricular.
	 * 
	 * sigaa.war/stricto/menu_coordenador.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String listarEstruturasCoordenador() throws DAOException, SegurancaException {
		initObj();
		checkRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO);	
		somenteAtivas = false;
		return forward("/stricto/curriculo/curso_estrutura.jsf");
	}
	

	/**
	 * Cria combo com os cursos de stricto. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCarregarCursosStrictoCombo() throws DAOException {
		if (getProgramaStricto() != null) {
			CursoDao dao = getDAO(CursoDao.class);
			cursosCombo = toSelectItems(dao.findByUnidade(getProgramaStricto()
					.getId(), ' '), "id", "descricao");
		}
		return "";
	}
	
	/**
	 * Carrega informa��es dos cursos de resid�ncia m�dica para utiliza��o na view. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCarregarCursosResidenciaCombo() throws DAOException {
		if (getProgramaResidencia() != null) {
			CursoDao dao = getDAO(CursoDao.class);
			cursosCombo = toSelectItems(dao.findByUnidade(getProgramaResidencia().getId(), ' '), "id", "descricao");
		}
		return "";
	}

	/**
	 * Redireciona para o formul�rio de resumo do curr�culo. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /SIGAA/app/sigaa.ear/sigaa.war/geral/estrutura_curricular/busca_geral.jsp
	 * </li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/curriculo/lista.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public String detalharCurriculo() throws DAOException {
		setOperacaoAtiva(1);
		setId();
		popularCurriculo();
		
		setConfirmButton("");
		setReadOnly(true);
		setPedirSenha(false);

		if (isGraduacao())
			return forward("/geral/estrutura_curricular/resumo.jsp");
		else
			return telaResumo();
	}

	/**
	 * Popula os dados de um curr�culo.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Nenhuma</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public void popularCurriculo() throws DAOException {
		if (obj == null)
			obj = new Curriculo();
		if ( obj.getId() == 0 )
			setId();

		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		setReadOnly(true);
		obj = dao.findByPrimaryKey(obj.getId(), Curriculo.class);
		if (obj != null) {
			Collection<CurriculoComponente> componentes = dao.findCurriculoComponentesByCurriculo(obj.getId());
			obj.setCurriculoComponentes((List<CurriculoComponente>) componentes);

			Curriculo curriculoTotais = dao.countTotalCrChCurriculo(obj.getId());
			obj.setChTeoricos(curriculoTotais.getChTeoricos());
			obj.setChPraticos(curriculoTotais.getChPraticos());
			obj.setCrTeoricos(curriculoTotais.getCrTeoricos());
			obj.setCrPraticos(curriculoTotais.getCrPraticos());
			obj.setChAAE(curriculoTotais.getChAAE());

			componentesAdicionados = null;

			if (obj.getCurso() != null && obj.getCurso().getNivel() == NivelEnsino.GRADUACAO) {
				converterParaTreeMap(obj.getCurriculoComponentes(), obj.getSemestreConclusaoIdeal());
			} else
				obj.getCurriculoComponentes().iterator();
		} else {
			addMensagemErro("N�o foi poss�vel identificar a estrutura curricular, por favor, reinicie o procedimento.");
			initObj();
		}
	}

	/**
	 * Gera o relat�rio com o curr�culo informado.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /SIGAA/app/sigaa.ear/sigaa.war/geral/estrutura_curricular/busca_geral.jsp
	 * </li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/lista.jsp</li>
	 * <li>
	 * /SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/solicitacoes_matricula
	 * .jsp</li>
	 * <li>
	 * /SIGAA/app/sigaa.ear/sigaa.war/graduacao/matricula/turmas_curriculo.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/matricula/
	 * turmas_equivalentes_curriculo.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/solicitacao_matricula/
	 * solicitacoes.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/stricto/matricula/turmas_programa.jsp</li>
	 * </ul>
	 * 
	 */
	public String gerarRelatorioCurriculo() throws DAOException {
		setId();
		popularCurriculo();
		return forward("/graduacao/curriculo/relatorio.jsp");
	}

	/**
	 * Carrega as matrizes curriculares. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /SIGAA/app/sigaa.ear/sigaa.war/geral/estrutura_curricular/busca_geral.jsp
	 * </li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/geral.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/curriculo/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/curso/
	 * seleciona_indice_trancamento.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/discente/
	 * seleciona_matriculados.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/discente/
	 * seleciona_motivo_trancamento.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/discente/
	 * seleciona_reprovados_trancados.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/discente/
	 * seleciona_tipo_saida.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/discente/
	 * seleciona_vinculados_estrutura.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/discente/
	 * selecionaq_motivo_trancamento.jsp</li>
	 * </ul>
	 */
	public void carregarMatrizes(ValueChangeEvent e) throws DAOException {
		carregarMatrizes(e, true);
	}
	
	/**
	 * 
	 * Utilizado para carregar as matrizes curriculares para um curso e tamb�m os seus par�metros.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/curriculo/geral.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMatrizesParametrosByCurso(ValueChangeEvent e) throws DAOException {
		carregarMatrizes(e, true);
	
		ParametrosGestoraAcademica parametros = carregarParametrosByCurso((Integer)e.getNewValue());
		if ( !ValidatorUtil.isEmpty(parametros) && !ValidatorUtil.isEmpty(parametros.getMinCreditosExtra()) && !ValidatorUtil.isEmpty(parametros.getHorasCreditosAula())){
			minComponenteEletivo = parametros.getMinCreditosExtra() * parametros.getHorasCreditosAula();
		}
		
		if ( !ValidatorUtil.isEmpty(parametros) && !ValidatorUtil.isEmpty(parametros.getMaxCreditosExtra()) && !ValidatorUtil.isEmpty(parametros.getHorasCreditosAula())){
			maxComponenteEletivo = parametros.getMaxCreditosExtra() * parametros.getHorasCreditosAula();			
		}		
		
	}
	
	
	/**
	 * Retorna os parametros a serem utilizados para um curso em quest�o.
	 * Se o curso tiver parametros definidos para a unidade gestora academica da unidade do curso e 
	 * esses valores estiverem definidos, usamos estes valores.
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	private ParametrosGestoraAcademica carregarParametrosByCurso(Integer idCurso) throws DAOException {
		ParametrosGestoraAcademica paramCurso = ParametrosGestoraAcademicaHelper.getParametros(new Curso(idCurso));
		//Se o curso tiver parametros definidos para a unidade gestora academica da unidade do curso e 
		//esses valores estiverem definidos, usamos estes valores.
		if(!ValidatorUtil.isEmpty(paramCurso.getMinCreditosExtra()) && !ValidatorUtil.isEmpty(paramCurso.getMaxCreditosExtra())
				&& !ValidatorUtil.isEmpty(paramCurso.getHorasCreditosAula()) ) {
			return paramCurso;
		}		 
		//Caso contr�rio utilizamos os valores definidos globalmente para o n�vel de ensino.
		return ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(getNivelEnsino());
	}

	/**
	 * Carrega todas as matrizes curriculares.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/relatorios/discente/seleciona_eleicao.jsp</li>
	 * </ul>
	 */
	public void carregarAllMatrizes(ValueChangeEvent e) throws DAOException {
		carregarMatrizes(e, null);
	}

	/**
	 * Carrega matrizes curriculares.
	 */
	private void carregarMatrizes(ValueChangeEvent e, Boolean ativas)
			throws DAOException {

		if (e.getNewValue() != null && (isGraduacao() || !NivelEnsino.isValido(getNivelEnsino()) || getNivelEnsino() == NivelEnsino.TECNICO))
			carregarMatrizes(new Integer(e.getNewValue().toString()), ativas);
		if (e.getNewValue() != null && !isGraduacao()) {
			Integer id = (Integer) e.getNewValue();
			if (isEmpty(id)) {
				addMensagemErro("Selecione uma Estrutura Curricular.");
				return;
			}
			obj.setCurso(getGenericDAO().findByPrimaryKey(id, Curso.class));
			if (!NivelEnsino.isResidenciaMedica(getNivelEnsino())) // se n�o for
																	// Resid�ncia
																	// m�dica,
																	// faz o
																	// fluxo
																	// abaixo
				if (obj.getCurso().getTipoCursoStricto() == null && NivelEnsino.isValido(getNivelEnsino()) && getNivelEnsino() != NivelEnsino.TECNICO) {
					addMensagemErro("N�o � poss�vel cadastrar curr�culos para cursos sem tipo de stricto definido");
				}
		}
	}

	/**
	 * Carrega matrizes curriculares de acordo com o curso e se est�o ativas.
	 * <br/><br/>
	 * M�todo n�o chamado por JSP.
	 */
	public void carregarMatrizes(Integer idCurso, Boolean ativas) throws DAOException {
		if (idCurso > 0) {
			MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
			possiveisMatrizes = toSelectItems(dao.findByCurso(idCurso, ativas), "id", "descricao");
		} else {
			possiveisMatrizes = new ArrayList<SelectItem>(0);
		}
	}

	/**
	 * Carrega matrizes ativas de um curso.
	 */
	private void carregarMatrizes(Integer idCurso) throws DAOException {
		carregarMatrizes(idCurso, true);
	}

	/**
	 * Carrega os cursos de um centro.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/relatorios/curso/seleciona_indice_trancamento.jsp</li>
	 * <li>/graduacao/relatorios/discente/seleciona_eleicao.jsp</li>
	 * <li>/graduacao/relatorios/discente/seleciona_ingressantes.jsp</li>
	 * <li>/graduacao/relatorios/discente/seleciona_motivo_trancamento.jsp</li>
	 * <li>/graduacao/relatorios/discente/seleciona_reprovados_trancados.jsp</li>
	 * <li>/graduacao/relatorios/discente/seleciona_tipo_saida.jsp</li>
	 * <li>/graduacao/relatorios/discente/selecionaq_motivo_trancamento.jsp</li>
	 * </ul>
	 * 
	 */
	public void carregarCursosCentro(ValueChangeEvent e) throws DAOException {
		if (e.getNewValue() != null)
			carregarCursosCentro(new Integer(e.getNewValue().toString()));
	}

	/**
	 * Busca todos os cursos de um programa de p�s-gradua��o. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/relatorios/discente/seleciona_eleicao.jsp</li>
	 * </ul>
	 */
	public void carregarCursosPrograma(ValueChangeEvent e) throws DAOException {
		possiveisCursos = new ArrayList<SelectItem>();
		if (e.getNewValue() != null) {
			int idPrograma = new Integer(e.getNewValue().toString());
			Collection<Curso> cursos = getDAO(CursoDao.class).findByUnidade(idPrograma, NivelEnsino.STRICTO);
			if (!isEmpty(cursos)) {
				for (Curso curso : cursos) {
					possiveisCursos.add(new SelectItem(curso.getId(), curso.getNomeCompleto() + " - " + curso.getNivelDescricao()));
				}
			}
		}
	}

	/**
	 * Carrega os cursos de um centro.
	 * 
	 * @param idCentro
	 * @throws DAOException
	 */
	private void carregarCursosCentro(Integer idCentro) throws DAOException {
		if (idCentro > 0) {
			CursoDao dao = getDAO(CursoDao.class);
			possiveisCursos = toSelectItems(dao.findByCentro(idCentro, 'G'), "id", "descricao");
			possiveisMatrizes = new ArrayList<SelectItem>(0);
		} else {
			possiveisCursos = new ArrayList<SelectItem>(0);
			possiveisMatrizes = new ArrayList<SelectItem>(0);
		}
	}

	/**
	 * Carrega os curr�culos de um curso.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/aproveitamento.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarCurriculos(ValueChangeEvent e) throws DAOException {
		Integer idCurso = new Integer(e.getNewValue().toString());
		carregarCurriculos(idCurso, getNivelEnsino());
	}
	
	/**
	 * Carrega os curr�culos de acordo com o curso e n�vel selecionado
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/aproveitamento.jsp</li>
	 * </ul>
	 * 
	 * @param idCurso
	 * @param nivel
	 * @throws DAOException
	 */
	public void carregarCurriculos(int idCurso, char  nivel) throws DAOException {
		curriculosBase = new ArrayList<SelectItem>(0);
		if (idCurso > 0) {
			EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
			Collection<Curriculo> col = dao.findByCurso(idCurso, nivel);
			if (col != null && !col.isEmpty()){
				if (isResidencia())
					curriculosBase = toSelectItems(col, "id", "descricao");
				else
					curriculosBase = toSelectItems(col, "id", "matrizDescricao");
			}
		}
	}

	/**
	 * Carrega os curr�culos compenentes de um curr�culo.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/curriculo/aproveitamento.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarComponentesDeCurriculoBase(ValueChangeEvent e) throws DAOException {
		Integer id = new Integer(e.getNewValue().toString());
		if (id > 0) {
			EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
			curriculoComponentes = (List<CurriculoComponente>) dao.findCurriculoComponentesByCurriculo(id);
		} else {
			curriculoComponentes = null;
		}
	}

	/**
	 * Retorna as �reas de concentra��o.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/curriculo/componentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPossiveisAreas() throws DAOException {
		AreaConcentracaoDao dao = getDAO(AreaConcentracaoDao.class);
		return toSelectItems(dao.findByProgramaNivel(obj.getCurso().getUnidade().getId(), obj.getCurso().getNivel()), "id", "denominacao");
	}

	/**
	 * Adiciona os componentes em uma lista para lan�ar o aproveitamento.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/curriculo/aproveitamento.jsp</li>
	 * <li>sigaa.war/stricto/discente/form.jsp</li>
	 * <li>sigaa.war/stricto/linha_pesquisa/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String aproveitarComponentes() throws ArqException {
		String[] obrigatorias = getCurrentRequest().getParameterValues(
				"obrigatorias");
		String[] optativas = getCurrentRequest().getParameterValues("optativas");
		StringBuffer ccErros = new StringBuffer();
		if (obrigatorias != null) {
			for (String obr : obrigatorias) {
				CurriculoComponente ccc = curriculoComponentes.get(new Integer(obr));
				String msg = addComponente(ccc.getComponente(), ccc.getSemestreOferta(), true);
				//Verifica se a tentativa de aproveitamento � de um n�vel superior ao ideal
				if(ccc.getSemestreOferta() == obj.getSemestreConclusaoIdeal() + 1 && !hasErrors())
					addMensagemErro("N�o foi poss�vel adicionar os componentes de n�vel igual ou superior a "+ccc.getSemestreOferta()+" para este Curr�culo pois s� s�o permitidos "+obj.getSemestreConclusaoIdeal()+" n�veis.");
				if (msg != null)
					ccErros.append(msg + "<br>");
			}
		}
		if (optativas != null) {
			for (String opt : optativas) {
				CurriculoComponente ccc = curriculoComponentes.get(new Integer(opt));
				String msg = addComponente(ccc.getComponente(), ccc.getSemestreOferta(), false);
				if(ccc.getSemestreOferta() == obj.getSemestreConclusaoIdeal() + 1 && !hasErrors())
					addMensagemErro("N�o foi poss�vel aproveitar os componentes de n�vel igual ou superior a "+ccc.getSemestreOferta()+" para este Curr�culo pois s� s�o permitidos "+obj.getSemestreConclusaoIdeal()+" n�veis.");
				if (msg != null)
					ccErros.append(msg + "<br>");
			}
		}
		if (ccErros.length() > 0 || !hasErrors())
			addMensagemErro("N�o foi poss�vel adicionar tais componentes:<br />"	+ ccErros);
		else
			addMessage("Componentes adicionados com sucesso.", TipoMensagemUFRN.WARNING);
		
		verificarCreditosExcedentes();
		return telaComponentes();
	}

	/**
	 * Carrega os cursos por n�vel.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/curriculo/lista.jsp</li>
	 * <li>/sigaa.war/stricto/discente/form.jsp</li>
	 * <li>sigaa.war/graduacao/relatorios/curso/seleciona_indice_trancamento.jsp
	 * </li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarCursos(ValueChangeEvent e) throws DAOException {
		char nivel = e.getNewValue().toString().charAt(0);
		CursoDao dao = getDAO(CursoDao.class);
		if (nivel == NivelEnsino.MESTRADO) {
			possiveisCursos = toSelectItems(dao.findByNivel(NivelEnsino.MESTRADO, true), "id", "nome");
		} else if (nivel == NivelEnsino.DOUTORADO) {
			possiveisCursos = toSelectItems(dao.findByNivel(NivelEnsino.DOUTORADO, true), "id", "nome");
		} else
			possiveisCursos = new ArrayList<SelectItem>(0);
	}
	
	/**
	 * M�todo para calcular a carga hor�ria total cadastrada no momento em cada semestre.
	 */
	private void calcularCh(){
		chPorSemestre = new TreeMap<Integer, Integer>();
		for(int i = 1;i <= componentesAdicionados.size();i++){
			ArrayList<CurriculoComponente> cc = componentesAdicionados.get(i);
			int currentSum = 0;
			if(!isEmpty(cc)){
				for (CurriculoComponente curriculoComponente : cc) {
					currentSum += curriculoComponente.getComponente().getChTotal();
				}
			}
			chPorSemestre.put(i, currentSum);
		}
	}

	/**
	 * Indica se desabilita o formul�rio de aproveitamento de componentes a
	 * partir de outro curr�culo.
	 * 
	 * @return
	 */
	public Boolean getDesabilitaAproveitamento() {
		return desabilitaAproveitamento;
	}

	/**
	 * Seta se desabilita o formul�rio de aproveitamento de componentes a partir
	 * de outro curr�culo.
	 * 
	 * @param desabilitaAproveitamento
	 */
	public void setDesabilitaAproveitamento(Boolean desabilitaAproveitamento) {
		this.desabilitaAproveitamento = desabilitaAproveitamento;
	}

	/**
	 * Retorna o select a ser populado para escolha de um curso do curr�culo.
	 * 
	 * @return
	 */
	public List<SelectItem> getPossiveisCursos() {
		return possiveisCursos;
	}

	/**
	 * Seta o select a ser populado para escolha de um curso do curr�culo.
	 * 
	 * @param possiveisCursos
	 */
	public void setPossiveisCursos(List<SelectItem> possiveisCursos) {
		this.possiveisCursos = possiveisCursos;
	}

	/**
	 * Indica se filtra a busca de curr�culos por c�digo.
	 * 
	 * @return
	 */
	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	/**
	 * Seta se filtra a busca de curr�culos por c�digo.
	 * 
	 * @param filtroCodigo
	 */
	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	/**
	 * Indica se filtra a busca de curr�culos por curso.
	 * 
	 * @return
	 */
	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	/**
	 * Seta se filtra a busca de curr�culos por curso.
	 * 
	 * @param filtroCurso
	 */
	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	/**
	 * Indica se filtra a busca de curr�culos por matriz curricular.
	 * 
	 * @return
	 */
	public boolean isFiltroMatriz() {
		return filtroMatriz;
	}

	/**
	 * Seta se filtra a busca de curr�culos por matriz curricular.
	 * 
	 * @param filtroMatriz
	 */
	public void setFiltroMatriz(boolean filtroMatriz) {
		this.filtroMatriz = filtroMatriz;
	}

	public boolean isFiltroCodigoComponente() {
		return filtroCodigoComponente;
	}

	public void setFiltroCodigoComponente(boolean filtroCodigoComponente) {
		this.filtroCodigoComponente = filtroCodigoComponente;
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroTipo() {
		return filtroTipo;
	}

	public void setFiltroTipo(boolean filtroTipo) {
		this.filtroTipo = filtroTipo;
	}

	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	public void setFiltroUnidade(boolean filtroUnidade) {
		this.filtroUnidade = filtroUnidade;
	}

	/**
	 * Retorna o select a ser populado para escolha de uma matriz do curr�culo.
	 * 
	 * @param possiveisMatrizes
	 */
	public void setPossiveisMatrizes(List<SelectItem> possiveisMatrizes) {
		this.possiveisMatrizes = possiveisMatrizes;
	}

	/**
	 * Seta o select a ser populado para escolha de uma matriz do curr�culo.
	 * 
	 * @return
	 */
	public List<SelectItem> getPossiveisMatrizes() {
		return possiveisMatrizes;
	}

	/**
	 * Retorna a cole��o de selectItem de cursos.
	 * 
	 * @return
	 */
	public List<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	/**
	 * Seta a cole��o de selectItem de cursos.
	 * 
	 * @param cursosCombo
	 */
	public void setCursosCombo(List<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	/**
	 * Indica se na altera��o da estrutura curricular pode alterar a carga
	 * hor�ria optativa e os componentes obrigat�rias. Somente ser� a lterado se
	 * n�o houver nenhum aluno vinculado a estrutura.
	 * 
	 * @return
	 */
	public boolean isPodeAlterarChEObrigatorias() {
		return podeAlterarChEObrigatorias;
	}

	/**
	 * Seta se na altera��o da estrutura curricular pode alterar a carga hor�ria
	 * optativa e os componentes obrigat�rias. Somente ser� a lterado se n�o
	 * houver nenhum aluno vinculado a estrutura.
	 * 
	 * @param podeAlterarChEObrigatorias
	 */
	public void setPodeAlterarChEObrigatorias(boolean podeAlterarChEObrigatorias) {
		this.podeAlterarChEObrigatorias = podeAlterarChEObrigatorias;
	}

	/**
	 * Retorna a unidade ao qual se restringe a busca de curr�culos de stricto
	 * sensu.
	 * 
	 * @return
	 */
	public Unidade getPrograma() {
		return programa;
	}
	
	/**
	 * Seta a unidade ao qual se restringe a busca de curr�culos de stricto
	 * sensu.
	 * 
	 * @param programa
	 */
	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	/**
	 * Indica se filtra a busca de curr�culos por programa.
	 * 
	 * @return
	 */
	public boolean isFiltroPrograma() {
		return filtroPrograma;
	}

	/**
	 * Seta se filtra a busca de curr�culos por programa.
	 * 
	 * @param filtroPrograma
	 */
	public void setFiltroPrograma(boolean filtroPrograma) {
		this.filtroPrograma = filtroPrograma;
	}

	/**
	 * Indica se deve-se exibir os filtros para busca.
	 * 
	 * @return
	 */
	public boolean isExibirFiltrosBusca() {
		return exibirFiltrosBusca;
	}

	/**
	 * Seta se deve-se exibir os filtros para busca.
	 * 
	 * @param exibirFiltrosBusca
	 */
	public void setExibirFiltrosBusca(boolean exibirFiltrosBusca) {
		this.exibirFiltrosBusca = exibirFiltrosBusca;
	}

	/**
	 * Retorna a Carga Hor�ria optativa no semestre.
	 * 
	 * @return
	 */
	public List<OptativaCurriculoSemestre> getChOptativaSemestre() {
		return chOptativaSemestre;
	}

	/**
	 * Seta a Carga Hor�ria optativa no semestre.
	 * 
	 * @param chOptativaSemestre
	 */
	public void setChOptativaSemestre(
			List<OptativaCurriculoSemestre> chOptativaSemestre) {
		this.chOptativaSemestre = chOptativaSemestre;
	}

	/**
	 * Retorna o grupo de componentes curriculares optativos.
	 * 
	 * @return
	 */
	public List<GrupoOptativas> getGruposOptativas() {
		return gruposOptativas;
	}

	/**
	 * Seta o grupo de componentes curriculares optativos.
	 * 
	 * @param gruposOptativas
	 */
	public void setGruposOptativas(List<GrupoOptativas> gruposOptativas) {
		this.gruposOptativas = gruposOptativas;
	}

	/**
	 * Indica se deve solicitar que o usu�rio digite a senha no formul�rio.
	 * 
	 * @return
	 */
	public boolean isPedirSenha() {
		return pedirSenha;
	}

	/**
	 * Seta se deve solicitar que o usu�rio digite a senha no formul�rio.
	 * 
	 * @param pedirSenha
	 */
	public void setPedirSenha(boolean pedirSenha) {
		this.pedirSenha = pedirSenha;
	}

	/**
	 * Retorna a lista de componentes do curr�culo.
	 * 
	 * @return
	 */
	public List<CurriculoComponente> getCurriculoComponentes() {
		return curriculoComponentes;
	}

	/**
	 * Seta a lista de componentes do curr�culo.
	 * 
	 * @param curriculoComponentes
	 */
	public void setCurriculoComponentes(
			List<CurriculoComponente> curriculoComponentes) {
		this.curriculoComponentes = curriculoComponentes;
	}

	/**
	 * Retorna o select a ser populado para escolha de um curr�culo base para
	 * adicionar os componentes.
	 * 
	 * @return
	 */
	public List<SelectItem> getCurriculosBase() {
		return curriculosBase;
	}

	/**
	 * Retorna a Carga Hor�ria de Componentes Curriculares optativas j�
	 * adicionadas.
	 * 
	 * @return
	 */
	public int getTmpChOptativa() {
		return tmpChOptativa;
	}

	/**
	 * Seta a Carga Hor�ria de Componentes Curriculares optativas j�
	 * adicionadas.
	 * 
	 * @param tmpChComplementar
	 */
	public void setTmpChOptativa(int tmpChComplementar) {
		tmpChOptativa = tmpChComplementar;
	}

	/**
	 * Retorna a lista de componentes encontrados na busca por componentes que
	 * se deseja incluir no curr�culo.
	 * 
	 * @return
	 */
	public Collection<ComponenteCurricular> getComponentesEncontrados() {
		return componentesEncontrados;
	}

	/**
	 * Seta a lista de componentes encontrados na busca por componentes que se
	 * deseja incluir no curr�culo.
	 * 
	 * @param componentesEcontrados
	 */
	public void setComponentesEncontrados(
			Collection<ComponenteCurricular> componentesEcontrados) {
		componentesEncontrados = componentesEcontrados;
	}

	/**
	 * Retorna o curriculoComponente a ser adicionado na cole��o.
	 * 
	 * @return
	 */
	public CurriculoComponente getCurriculoComponente() {
		return curriculoComponente;
	}

	/**
	 * Seta o curriculoComponente a ser adicionado na cole��o.
	 * 
	 * @param curriculoComponente
	 */
	public void setCurriculoComponente(CurriculoComponente curriculoComponente) {
		this.curriculoComponente = curriculoComponente;
	}

	/**
	 * Retorna a Cole��o de CurriculoComponente organizados por semestre a serem
	 * incorporados na estrutura curricular.
	 * 
	 * @return
	 */
	public TreeMap<Integer, ArrayList<CurriculoComponente>> getComponentesAdicionados() {
		return componentesAdicionados;
	}

	/**
	 * Seta a Cole��o de CurriculoComponente organizados por semestre a serem
	 * incorporados na estrutura curricular.
	 * 
	 * @param componentesAdicionados
	 */
	public void setComponentesAdicionados(
			TreeMap<Integer, ArrayList<CurriculoComponente>> componentesAdicionados) {
		this.componentesAdicionados = componentesAdicionados;
	}

	/**
	 * Indica se permite exibe o formul�rio de aproveitamento de componentes a
	 * partir de outro curr�culo.
	 * 
	 * @return
	 */
	public Boolean getAproveitarComponentes() {
		return aproveitarComponentes;
	}

	/**
	 * Seta se permite exibe o formul�rio de aproveitamento de componentes a
	 * partir de outro curr�culo.
	 * 
	 * @param aproveitarComponentes
	 */
	public void setAproveitarComponentes(Boolean aproveitarComponentes) {
		this.aproveitarComponentes = aproveitarComponentes;
	}

	/**
	 * Retorna o ID ao qual se restringe o curso na busca por estruturas
	 * curriculares.
	 * 
	 * @return
	 */
	public int getIdCurso() {
		return idCurso;
	}

	/**
	 * Seta o ID ao qual se restringe o curso na busca por estruturas
	 * curriculares.
	 * 
	 * @param idCurso
	 */
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	/**
	 * Retorna a ser exibido no formul�rio.
	 * 
	 * @return
	 */
	public int getExibirSemestre() {
		return exibirSemestre;
	}

	/**
	 * Seta a ser exibido no formul�rio.
	 * 
	 * @param exibirSemestre
	 */
	public void setExibirSemestre(int exibirSemestre) {
		this.exibirSemestre = exibirSemestre;
	}

	/**
	 * Indica se o n�vel de ensino do curr�culo � gradua��o.
	 * 
	 * @return
	 */
	public boolean isGraduacao() {
		return getNivelEnsino() == NivelEnsino.GRADUACAO
				|| obj.getCurso().getNivel() == NivelEnsino.GRADUACAO;
	}
	
	/**
	 * Indica se o n�vel de ensino do curr�culo � gradua��o.
	 * 
	 * @return
	 */
	public boolean isStricto() {
		return NivelEnsino.isAlgumNivelStricto(getNivelEnsino())
				|| NivelEnsino.isAlgumNivelStricto(obj.getCurso().getNivel());
	}

	/**
	 * Indica se o n�vel de ensino do curr�culo � de Resid�ncia em Sa�de
	 * 
	 * @return
	 */
	public boolean isResidencia() {
		return getNivelEnsino() == NivelEnsino.RESIDENCIA
				|| obj.getCurso().getNivel() == NivelEnsino.RESIDENCIA;
	}

	/**
	 * Retorna uma cole��o de SelectItem de estruturas curriculares cadastradas.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCurriculosGraduacaoCombo()
			throws DAOException {
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		return toSelectItems(dao.findAll(NivelEnsino.GRADUACAO), "id", "descricaoCompleta");
	}

	public boolean isSomenteAtivas() {
		return somenteAtivas;
	}

	public void setSomenteAtivas(boolean somenteAtivas) {
		this.somenteAtivas = somenteAtivas;
	}

	public int getNivelDisciplinaResidencia() {
		return nivelDisciplinaResidencia;
	}

	public void setNivelDisciplinaResidencia(int nivelDisciplinaResidencia) {
		this.nivelDisciplinaResidencia = nivelDisciplinaResidencia;
	}

	public int getMinComponenteEletivo() {
		return minComponenteEletivo;
	}

	public void setMinComponenteEletivo(int minComponenteEletivo) {
		this.minComponenteEletivo = minComponenteEletivo;
	}

	public int getMaxComponenteEletivo() {
		return maxComponenteEletivo;
	}

	public void setMaxComponenteEletivo(int maxComponenteEletivo) {
		this.maxComponenteEletivo = maxComponenteEletivo;
	}

	public String getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(String abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}	

	/**
	 * Retorna a Carga Hor�ria por Semestre
	 * @return
	 */
	public TreeMap<Integer, Integer> getChPorSemestre() {
		if(isEmpty(chPorSemestre))
			calcularCh();
		return chPorSemestre;
	}

	public void setChPorSemestre(TreeMap<Integer, Integer> chPorSemestre) {
		this.chPorSemestre = chPorSemestre;
	}
	
	public int getQuantidadeResultados() {
		return resultadosBusca.size();
	}

	@Override
	public String selecionaComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		if (componente != null && componenteCurriculoASubstituir != null && !isEmpty(obj.getCurriculoComponentes())) {
			substituidoPor = getGenericDAO().refresh(componente);
			
			return forward("/graduacao/curriculo/confirma_substituicao.jsp");
		}
		addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
		return null;
	}
	
	/** Confirma a substitui��o do componente curricular por outro.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war//graduacao/curriculo/confirma_substituicao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String confirmaSubstituicaoComponente() throws DAOException {
		for (CurriculoComponente cc : obj.getCurriculoComponentes()) {
			if (cc.getComponente().getId() == componenteCurriculoASubstituir.getComponente().getId()) {
				addMensagemWarning("O componente curricular " + componenteCurriculoASubstituir.getComponente().getCodigoNome() + " foi substitu�do por " + substituidoPor.getCodigoNome());
				cc.setComponente(substituidoPor);
				componenteCurriculoASubstituir = null;
				recriarTabelaComponente();
				calcularCh();
				return telaComponentes();
			}
		}
		addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
		return telaComponentes();
	}

	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		ListaMensagens lista = new ListaMensagens();
		if (componente.getId() == componenteCurriculoASubstituir.getComponente().getId()) {
			lista.addErro("N�o � poss�vel selecionar o mesmo componente para substitui��o.");
		} else {
			if (jaAdicionou(componente)) // se j� existe
				lista.addErro("O Componente Curricular j� est� inclu�do no curr�culo.");
			// Componentes dos semestres anteriores
			ArrayList<ComponenteCurricular> compSemAnteriores = new ArrayList<ComponenteCurricular>(0);
			for (int s = 1; s <= componenteCurriculoASubstituir.getSemestreOferta(); s++) {			
				ArrayList<CurriculoComponente> componentes = componentesAdicionados.get(s);
				if(isEmpty(componentes))
					continue;
				for (CurriculoComponente currComp : componentes) {
					compSemAnteriores.add(currComp.getComponente());
				}
			}
	
			/*
			 * O componente a ser adicionado deve ter seus pr�-requisitos nos
			 * semestres anteriores, mas nenhum pr�-requisito nos semestres
			 * posteriores e no mesmo semestre
			 */
			if (componente.getPreRequisito() != null && !ExpressaoUtil.eval(componente.getPreRequisito(), compSemAnteriores) && componenteCurriculoASubstituir.getSemestreOferta() <= obj.getSemestreConclusaoIdeal())
				lista.addErro("O componente " + componente.getDescricaoResumida() + " deve ter todos os pr�-requisitos nos n�veis anteriores");
		}
		return lista;
	}

	@Override
	public String retornarSelecaoComponente() {
		return telaComponentes();
	}

	public ComponenteCurricular getSubstituidoPor() {
		return substituidoPor;
	}

	public void setSubstituidoPor(ComponenteCurricular substituidoPor) {
		this.substituidoPor = substituidoPor;
	}

	public CurriculoComponente getComponenteCurriculoASubstituir() {
		return componenteCurriculoASubstituir;
	}
	
	/**
	 * Retorna a Carga Hor�ria total da lista de componentes.
	 * 
	 * @return
	 */
	public int getChTotalComponentes() {
		int chTotal = 0;
		for (CurriculoComponente cc : obj.getCurriculoComponentes()){
			chTotal += cc.getComponente().getChTotal();
		}
		return chTotal;
	}
	
	/**
	 * Retorna a n�mero de cr�ditos total da lista de componentes.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public int getCrTotalComponentes() throws DAOException {
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(getNivelEnsino());
		return getChTotalComponentes() / parametros.getHorasCreditosAula();
	}

	/**
	 * Retorna a lista de componentes curriculares do tipo TCC presentes no curr�culo
	 * @return
	 */
	public List<ComponenteCurricular> getComponentesTcc() {
		return componentesTcc;
	}

	public void setComponentesTcc(List<ComponenteCurricular> componentesTcc) {
		this.componentesTcc = componentesTcc;
	}

	public List<SelectItem> getComponentesTccItemList() {
		return toSelectItems(componentesTcc, "id", "descricao");
	}

	public Integer getIdTccDefinitivo() {
		return idTccDefinitivo;
	}

	public void setIdTccDefinitivo(Integer idTccDefinitivo) {
		this.idTccDefinitivo = idTccDefinitivo;
	}	
}
