/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 15/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.NAO_HA_OBJETO_REMOCAO;
import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.CollectionUtils.toList;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.medio.dao.CurriculoMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CurriculoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * MBean respons�vel por opera��es com a Turma anual do Ensino M�dio, que o aluno ser� vinculado (ex.: Turma A, B, C...).
 * 
 * @author Rafael Gomes
 *
 */
@Component("turmaSerie") @Scope("request")
public class TurmaSerieMBean extends SigaaAbstractController<TurmaSerie>{

	/** Filtro da busca pelo Curso */
	private boolean filtroCurso;
	/** Filtro da busca pela S�rie */
	private boolean filtroSerie;
	/** Filtro da busca por Ano */
	private boolean filtroAno;
	/** Filtro da busca por Turma Serie */
	private boolean filtroTurmaSerie;
	/** Filtro utilizado para incluir Turmas Inativas na busca de turmas.*/
	private boolean filtroInativos;
	/** Verificador utilizado para exibir a sele��o de curr�culos para o cadastro de nova turma de ensino m�dio.*/
	private boolean exibeCurriculos = false;
	/** Verifica se a turma possui alunos matriculados, acarretando a desabilita��o de alguns atributos para a altera��o.*/
	private boolean possuiAlunosMatriculados = false;
	
	/** Lista de DISCIPLINAS que comp�e uma Turma de ensino m�dio.*/
	private Collection<TurmaSerie> turmaDisciplinas = new ArrayList<TurmaSerie>();
	/** Lista de DISCIPLINAS em DataModel, que comp�e uma Turma de ensino m�dio.*/
	private DataModel turmaDisciplinasDataModel;
	
	/** Collection que ir� armazenar a listagem das turmas s�ries. */
	private Collection<TurmaSerie> listaTurmaSeries= new ArrayList<TurmaSerie>(); 
	
	/** Lista das s�ries por curso de ensino m�dio. */
	private List<SelectItem> seriesByCurso = new ArrayList<SelectItem>(0);
	
	/** Lista dos Curr�culos por s�rie de ensino m�dio. */
	private Collection<CurriculoMedio> curriculosBySerie = new ArrayList<CurriculoMedio>();
	
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_FORM_TURMA_SERIE = "/formTurmaSerie.jsp";
	
	/** Objeto auxiliar utilizado para a exibir o detalhe do curr�culo selecionado em tela.*/
	private CurriculoMedio curriculoMedio;
	
	/** Objeto respons�vel por manter o calend�rio acad�mico do curso ou do n�vel de ensino. */
	private CalendarioAcademico calendarioTurmaSerie;
	
	/**
	 * Construtor padr�o
	 */
	public TurmaSerieMBean() {
		initObj();
	}
	
	/** Inicializando das vari�veis utilizadas */
	private void initObj(){
		obj = new TurmaSerie();
		obj.setSerie(new Serie());
		obj.getSerie().setCursoMedio(new CursoMedio());
		obj.setTurno(new Turno());
		filtroInativos = false;
		possuiAlunosMatriculados = false;
	}
	
	/**
	 * Diret�rio que se encontra as view's
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>M�todo n�o invocado por JSP</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/medio/turmaSerie";
	}
	
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		boolean isDependencia = getParameterBoolean("isDependencia");
		if(isDependencia){
			TurmaSerieDependenciaMBean tsdMben = getMBean("turmaSerieDependencia");
			return tsdMben.atualizar();
			
		}
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class);
		setId();
		setObj(tsDao.findByPrimaryKey(obj.getId(), TurmaSerie.class));
		if( isEmpty(obj.getTurno()) ) obj.setTurno(new Turno());
		possuiAlunosMatriculados = tsDao.findQtdeAlunosByTurma(obj) > 0;
		carregarSeriesByCurso();
		carregarCurriculosBySerie();
		
		setConfirmButton("Alterar");
		setOperacaoAtiva(SigaaListaComando.ALTERAR_TURMA_SERIE.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_TURMA_SERIE);
		return forward(getFormTurmaSeriePage());
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setConfirmButton("Cadastrar");
		setLabelCombo("Cadastrar");
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_TURMA_SERIE.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA_SERIE);
		return forward(getFormTurmaSeriePage());
		
	}
	
	@Override
	public String listar() throws ArqException {
		checkChangeRole();
		setConfirmButton("Alterar");
		setLabelCombo("Listar/Alterar");
		setOperacaoAtiva(SigaaListaComando.ALTERAR_TURMA_SERIE.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_TURMA_SERIE);
		return forward(getListPage());
		
	}
	
	/**
	 * Efetua a remo��o l�gica da s�rie de ensino M�dio, desativando-a.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		int ano = obj.getAno();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), TurmaSerie.class));
		
		if ( obj.getCurriculo().getId() <= 0 ) 
			obj.setCurriculo(null);
		
		prepareMovimento(SigaaListaComando.REMOVER_TURMA_SERIE);
		setOperacaoAtiva(SigaaListaComando.REMOVER_TURMA_SERIE.getId());
		
		MovimentoCadastro mov = new MovimentoCadastro(obj, SigaaListaComando.REMOVER_TURMA_SERIE);

		if (obj == null || obj.getId() == 0) {
			addMensagem(NAO_HA_OBJETO_REMOCAO);
			return null;
		} else {

			try {
				execute(mov);
				addMensagem(OPERACAO_SUCESSO);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				obj.setAno(ano);
				listarTurmasAnual();
				return forward(getListPage());
			} 

			setResultadosBusca(null);
			afterRemover();
			return forward(getListPage());
		}
	}
	
	@Override
	public void afterRemover() {
		try {
			TurmaSerie objAux = obj;
			listarTurmasAnual(objAux);
			removeOperacaoAtiva();
		} catch (DAOException e) {
			addMensagemErro("Erros ao remover uma turma de ensino m�dio.");
		}
	}
	
	@Override
	protected String forwardRemover() {
		return getListPage();
	}
	
	/**
	 * M�todo respons�vel pelo cadastro de uma S�rie de Ensino M�dio.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class);
		
		checkChangeRole();
		if ("remover".equalsIgnoreCase(getConfirmButton()))
			return remover();
		
		Comando comando;
		if (obj.getId() > 0)
			comando = SigaaListaComando.ALTERAR_TURMA_SERIE;
		else
			comando = SigaaListaComando.CADASTRAR_TURMA_SERIE;
		
		setOperacaoAtiva(comando.getId());
		int idCurriculoSelecionado = getParameterInt("idCurriculo",0);
		if ( !possuiAlunosMatriculados ) {
			ValidatorUtil.validateRequiredId(idCurriculoSelecionado, "Estrutura Curricular", erros);
		}	
		
		if ( tsDao.existeTurmaSerieMesmoNomeByAno(obj) )
			erros.addErro("Nome: J� utilizado para esta s�rie e ano.");
		erros.addAll(obj.validate());
		if (hasErrors()) return null;
		
		Boolean alterarCurriculo = false;
		if ( comando.equals(SigaaListaComando.ALTERAR_TURMA_SERIE) && !possuiAlunosMatriculados 
			&& idCurriculoSelecionado > 0 && obj.getCurriculo() != null
			&& obj.getCurriculo().getId() != idCurriculoSelecionado ){
				alterarCurriculo = true;
				obj.setCurriculo(tsDao.findByPrimaryKey(idCurriculoSelecionado, CurriculoMedio.class));
		}
		if ( comando.equals(SigaaListaComando.CADASTRAR_TURMA_SERIE) )
			obj.setCurriculo(tsDao.findByPrimaryKey(idCurriculoSelecionado, CurriculoMedio.class));
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(alterarCurriculo);
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} 
		listarTurmasAnual();
		if (comando.equals(SigaaListaComando.CADASTRAR_TURMA_SERIE)) {
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Turma da S�rie");
		} else {
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Turma da S�rie");
		} 
		removeOperacaoAtiva();
		
		return forward(getListPage());
	}
	
	/**
	 * Efetua a remo��o l�gica da s�rie de ensino M�dio, desativando-a.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/form.jsp</li>
	 *  <li>/sigaa.war/medio/turmaSerie/lista.jsp</li>
	 * </ul>
	 */
	public String removerDisciplina() throws ArqException {
		checkChangeRole();
		setId();
		TurmaSerieAno disciplina = new TurmaSerieAno();
		disciplina.setId(getParameterInt("id", 0));
		disciplina = getGenericDAO().findByPrimaryKey(disciplina.getId(), TurmaSerieAno.class); 
		if (disciplina == null) {
			addMensagemErro("O objeto selecionado n�o existe mais.");
			return redirectJSF(getSubSistema().getLink());
		}
		TurmaSerie turma = new TurmaSerie();
		turma.setId(disciplina.getTurmaSerie().getId());
		turma = getGenericDAO().findByPrimaryKey(turma.getId(), TurmaSerie.class);
		
		if(turma != null && turma.getDisciplinas().size() == 1){
			try {
				throw new NegocioException("N�o � poss�vel remover esta disciplina, pois � a unica desta turma, neste caso voc� pode usar o link - Gerenciar Turma - para remover ou inativar a turma.");
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			}
		}
		prepareMovimento(SigaaListaComando.REMOVER_DISCIPLINA_MEDIO);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REMOVER_DISCIPLINA_MEDIO);
		mov.setObjMovimentado(disciplina);
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} 
		listarTurmasAnual();
		return forward(getListPage());
	}
	
	/**
	 * M�todo respons�vel por redirecionar o usu�rio para o formul�rio de atualiza��o da turma.
	 * @return
	 */
	public String getFormTurmaSeriePage() {
		return getDirBase() + JSP_FORM_TURMA_SERIE;
	}
	
	
	/** M�todo respons�vel por criar as turmas de disciplinas pertencentes a uma turma anual de ensino m�dio,
	 * com base as disciplinas do curr�culo da s�rie selecionada.
	 * 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * */
	public String listarTurmasAnual() throws DAOException{
		
		if (isEmpty(obj.getAno())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (hasErrors())
			return null;
		
		// Buscar as turma disponibilizadas para a s�rie e ano informados
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		turmaDisciplinas = dao.findByCursoSerieAno(obj.getSerie().getCursoMedio(), obj.getSerie(), obj.getAno(), filtroInativos, getNivelEnsino(), false);
		turmaDisciplinasDataModel = new ListDataModel(toList(turmaDisciplinas));
		
		if (turmaDisciplinas.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(getListPage());
	}
	
	/** M�todo respons�vel por criar as turmas de disciplinas pertencentes a uma turma anual de ensino m�dio,
	 * com base as disciplinas do curr�culo da s�rie selecionada.
	 * 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * */
	public String listarTurmasAnual(TurmaSerie objAux) throws DAOException{
		
		if (isEmpty(objAux.getAno())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (hasErrors())
			return null;
		
		// Buscar as turma disponibilizadas para a s�rie e ano informados
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		turmaDisciplinas = dao.findByCursoSerieAno(objAux.getSerie().getCursoMedio(), objAux.getSerie(), objAux.getAno(), filtroInativos, getNivelEnsino(), false);
		turmaDisciplinasDataModel = new ListDataModel(toList(turmaDisciplinas));
		
		return null;
	}
	
	/** M�todo respons�vel por criar as turmas de disciplinas pertencentes a uma turma anual de ensino m�dio,
	 * com base as disciplinas do curr�culo da s�rie selecionada.
	 * 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * */
	public String listarTurmasAnual(TurmaSerie objAux, String url) throws DAOException{
		
		if (isEmpty(objAux.getAno())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (hasErrors())
			return null;
		
		// Buscar as turma disponibilizadas para a s�rie e ano informados
		obj.setAno(objAux.getAno());
		if (objAux.getSerie() != null){
			obj.setSerie(objAux.getSerie());
		}	
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		turmaDisciplinas = dao.findByCursoSerieAno(objAux.getSerie().getCursoMedio(), objAux.getSerie(), objAux.getAno(), filtroInativos, getNivelEnsino(), false);
		turmaDisciplinasDataModel = new ListDataModel(toList(turmaDisciplinas));
		
		return forward(url);
	}
	
	/**
	 * M�todo respons�vel pela busca de s�ries de ensino M�dio.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String buscar() throws ArqException {
		
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		
		if (filtroCurso && isEmpty(obj.getSerie().getCursoMedio().getId())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if (filtroSerie && isEmpty(obj.getSerie().getId())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "S�rie");
		
		if (filtroAno && isEmpty(obj.getAno())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
		if (filtroTurmaSerie && isEmpty(obj.getId())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Turma");
		
		if (hasOnlyErrors()) {
			listaTurmaSeries.clear();
			return null;
		}
	
		listaTurmaSeries = dao.findByCursoSerieAno(obj.getSerie().getCursoMedio(), obj.getSerie(), obj.getAno(), filtroInativos, getNivelEnsino(), false);
		if (listaTurmaSeries.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(getListPage());
	}
	
	/** 
	 * Carrega as s�ries pertencentes ao curso de ensino m�dio selecionado na jsp..
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso(ValueChangeEvent e) throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		CalendarioAcademicoDao calDao = getDAO(CalendarioAcademicoDao.class);
		CursoMedio cursoMedio = null;
		
		if( e != null && (Integer)e.getNewValue() > 0 )
			cursoMedio = dao.findByPrimaryKey((Integer)e.getNewValue(), CursoMedio.class);
		else {
			seriesByCurso = new ArrayList<SelectItem>(0);
			curriculosBySerie = new ArrayList<CurriculoMedio>();
			return;
		}	
		
		cursoMedio.setNivel(getNivelEnsino());
		if (cursoMedio != null){
			seriesByCurso = toSelectItems(dao.findByCurso(cursoMedio), "id", "descricaoCompleta");
			obj.getSerie().setId(0);
			calendarioTurmaSerie = calDao.findByParametros(null, null, cursoMedio.getUnidade(), NivelEnsino.MEDIO,
					null, null, cursoMedio, null);
			if (calendarioTurmaSerie == null)
				calendarioTurmaSerie = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalMedio();	
			obj.setDataInicio(calendarioTurmaSerie.getInicioPeriodoLetivo());
			obj.setDataFim(calendarioTurmaSerie.getFimPeriodoLetivo());
		}	
	}
	
	/** 
	 * Carrega as s�ries pertencentes ao curso de ensino m�dio baseado no curso do objeto em quest�o.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso() throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		
		if ( obj.getCurriculo() != null && obj.getCurriculo().getId() > 0 )
			obj.setCurriculo(getGenericDAO().findByPrimaryKey(obj.getCurriculo().getId(), CurriculoMedio.class));
		else {
			obj.setCurriculo(new CurriculoMedio());
			obj.getCurriculo().setCodigo("Estrutura Curricular n�o localizada.");
		}	
		
		if( isNotEmpty(obj.getSerie().getCursoMedio()) )
			seriesByCurso = toSelectItems(dao.findByCurso(obj.getSerie().getCursoMedio()), "id", "descricaoCompleta");
		else {
			seriesByCurso = new ArrayList<SelectItem>(0);
			return;
		}	
	}
	
	/** 
	 * Carrega os curr�culos ativos pertencentes a s�rie de ensino m�dio selecionado na jsp.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarCurriculosBySerie(ValueChangeEvent e) throws DAOException {
		CurriculoMedioDao dao = getDAO( CurriculoMedioDao.class );
		
		Serie serie = null;
		
		if( e != null && e.getNewValue() != null && (Integer)e.getNewValue() > 0 )
			serie = dao.findByPrimaryKey((Integer)e.getNewValue(), Serie.class);
		else {
			curriculosBySerie = new ArrayList<CurriculoMedio>();
			return;
		}	
		
		boolean ativo = true;
		
		if (serie != null){
			curriculosBySerie = dao.findByCursoOrSerie(null, serie, null, ativo);
			exibeCurriculos = true;
		}	
	}
	

	/** 
	 * Carrega os curr�culos ativos pertencentes a s�rie de ensino m�dio selecionado na jsp.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> NENHUMA JSP</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarCurriculosBySerie() throws DAOException {
		CurriculoMedioDao dao = getDAO( CurriculoMedioDao.class );
		
		boolean curriculosAtivos = true;
		
		if (obj.getSerie() != null){
			curriculosBySerie = dao.findByCursoOrSerie(null, obj.getSerie(), null, curriculosAtivos);
			exibeCurriculos = true;
		} else {
			curriculosBySerie = new ArrayList<CurriculoMedio>();
			exibeCurriculos = false;
		}
	}
	
	/**
	 * M�todo respons�vel por exibir o detalhe da estrutura curricular selecionada na view.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void visualizarCurriculo(ActionEvent evt) throws DAOException{
		int id = getParameterInt("idViewCurriculo", 0);
		Integer idInt = new Integer(id);
		setCurriculoMedio(getGenericDAO().findByPrimaryKey(idInt, CurriculoMedio.class));
	}
	
	/** M�todo respons�vel por verificar se o usu�rio logado � um discente.*/
	public boolean isDiscente(){
		return getUsuarioLogado().getDiscenteAtivo() != null;
	}
	
	/** M�todo respons�vel por verificar se o usu�rio logado possui o papel
	 *  SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO ou SigaaPapeis.SECRETARIA_MEDIO. */
	public boolean isPemissaoMedio() {
		return isUserInRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO , SigaaPapeis.SECRETARIA_MEDIO);
	}

	/** M�todo respons�vel por verificar se o usu�rio � coordenador
	 *  SigaaPapeis.COORDENADOR_MEDIO . */
	public boolean isCoordenadorMedio() {
		return isUserInRole(SigaaPapeis.COORDENADOR_MEDIO);
	}
	
	/* Getters and Setters */
	
	
	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public boolean isFiltroSerie() {
		return filtroSerie;
	}

	public void setFiltroSerie(boolean filtroSerie) {
		this.filtroSerie = filtroSerie;
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroTurmaSerie() {
		return filtroTurmaSerie;
	}

	public void setFiltroTurmaSerie(boolean filtroTurmaSerie) {
		this.filtroTurmaSerie = filtroTurmaSerie;
	}

	public boolean isFiltroInativos() {
		return filtroInativos;
	}

	public void setFiltroInativos(boolean filtroInativos) {
		this.filtroInativos = filtroInativos;
	}

	public Collection<TurmaSerie> getTurmaDisciplinas() {
		return turmaDisciplinas;
	}

	public void setTurmaDisciplinas(Collection<TurmaSerie> turmaDisciplinas) {
		this.turmaDisciplinas = turmaDisciplinas;
	}
	
	public DataModel getTurmaDisciplinasDataModel() {
		return turmaDisciplinasDataModel;
	}

	public void setTurmaDisciplinasDataModel(DataModel turmaDisciplinasDataModel) {
		this.turmaDisciplinasDataModel = turmaDisciplinasDataModel;
	}

	public Collection<TurmaSerie> getListaTurmaSeries() {
		return listaTurmaSeries;
	}

	public void setListaTurmaSeries(Collection<TurmaSerie> listaTurmaSeries) {
		this.listaTurmaSeries = listaTurmaSeries;
	}

	public List<SelectItem> getSeriesByCurso() {
		return seriesByCurso;
	}

	public void setSeriesByCurso(List<SelectItem> seriesByCurso) {
		this.seriesByCurso = seriesByCurso;
	}

	public boolean isExibeCurriculos() {
		return exibeCurriculos;
	}

	public void setExibeCurriculos(boolean exibeCurriculos) {
		this.exibeCurriculos = exibeCurriculos;
	}

	public boolean isPossuiAlunosMatriculados() {
		return possuiAlunosMatriculados;
	}

	public void setPossuiAlunosMatriculados(boolean possuiAlunosMatriculados) {
		this.possuiAlunosMatriculados = possuiAlunosMatriculados;
	}

	public Collection<CurriculoMedio> getCurriculosBySerie() {
		return curriculosBySerie;
	}

	public void setCurriculosBySerie(Collection<CurriculoMedio> curriculosBySerie) {
		this.curriculosBySerie = curriculosBySerie;
	}

	public CurriculoMedio getCurriculoMedio() {
		return curriculoMedio;
	}

	public void setCurriculoMedio(CurriculoMedio curriculoMedio) {
		this.curriculoMedio = curriculoMedio;
	}

	public CalendarioAcademico getCalendarioTurmaSerie() {
		return calendarioTurmaSerie;
	}

	public void setCalendarioTurmaSerie(CalendarioAcademico calendarioTurmaSerie) {
		this.calendarioTurmaSerie = calendarioTurmaSerie;
	}
	
}
