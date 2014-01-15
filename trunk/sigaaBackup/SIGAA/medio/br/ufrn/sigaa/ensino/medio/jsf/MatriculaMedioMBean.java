/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 22/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dominio.MovimentoAcademico;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.medio.dao.DiscenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.DisciplinaMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosMedio;

/**
 * Managed bean para cadastro de matr�culas. Utilizado nas matr�culas do ensino M�dio.
 * 
 * @author Rafael Gomes
 *
 */
@Component("matriculaMedio") @Scope("session")
public class MatriculaMedioMBean extends SigaaAbstractController<MatriculaDiscenteSerie> implements OperadorDiscente{
		
	/** Destina-se ao ano de matriculado discente.*/
	private Integer ano;
	/** Atributo utilizado para armazenar o comando da opera��o utilizada no caso de uso.*/
	private Comando comando;
	
	/** Collection que ir� armazenar a listagem das turmas s�ries. */
	private Collection<TurmaSerie> listaTurmaSeries = new ArrayList<TurmaSerie>(); 
	/** Lista das s�ries por curso de ensino m�dio. */
	private List<SelectItem> seriesByCurso = new ArrayList<SelectItem>(0);
	/** Todas as disciplinas de depend�ncia selecionadas pelo usu�rio */
	private List<TurmaSerieAno> disciplinas = new ArrayList<TurmaSerieAno>();
	
	/** Define o redirecionamento para o resumo dos dados da turma. */
	public static final String JSP_RESUMO = "/resumo.jsp";
	/** Define o redirecionamento para o formul�rio de matr�cula de depend�ncia para alunos de n�vel m�dio. */
	public static final String JSP_FORM_DEPENDENCIA = "/form_dependencia.jsp";
	/** Define o redirecionamento para o resumo dos dados das disciplinas de depend�ncia. */
	public static final String JSP_RESUMO_DEPENDENCIA = "/resumo_dependencia.jsp";
	
	/**
	 * Construtor padr�o
	 */
	public MatriculaMedioMBean() {
		initObj();
	}
	
	/** Inicializando das vari�veis utilizadas */
	private void initObj(){
		obj = new MatriculaDiscenteSerie();
		obj.setDiscenteMedio(new DiscenteMedio());
		obj.setTurmaSerie(new TurmaSerie());
		obj.getTurmaSerie().setSerie(new Serie());
		obj.getTurmaSerie().getSerie().setCursoMedio(new CursoMedio());
		ano = null;
		listaTurmaSeries = new ArrayList<TurmaSerie>();
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
		return "/medio/matriculaMedio";
	}
	
	/**
	 * M�todo respons�vel por redirecionar o usu�rio para a view contendo o resumo da matr�cula do discente.
	 * @return
	 */
	public String getJspResumo() {
		return getDirBase() + JSP_RESUMO;
	}
	
	/**
	 * M�todo respons�vel por redirecionar o usu�rio para a view contendo o resumo da matr�cula do discente.
	 * @return
	 */
	public String getJspFormDependencia() {
		return getDirBase() + JSP_FORM_DEPENDENCIA;
	}
	
	/**
	 * M�todo respons�vel por redirecionar o usu�rio para a view contendo o resumo das disciplinas de depend�ncia do discente.
	 * @return
	 */
	public String getJspResumoDependencia() {
		return getDirBase() + JSP_RESUMO_DEPENDENCIA;
	}
	
	/** Redireciona para a busca de discentes, 
	 * a fim de realizar a opera��o de matr�cula de discente em turma.
	 * <br><br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 */
	public String iniciarMatriculaDiscente() throws ArqException {
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		prepareMovimento( SigaaListaComando.MATRICULAR_DISCENTE_MEDIO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_DISCENTE_MEDIO.getId());
		initObj();
		comando = SigaaListaComando.MATRICULAR_DISCENTE_MEDIO;
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.MATRICULA_DISCENTE_MEDIO);
		return buscaDiscenteMBean.popular();
	}
	
	/** Redireciona para a busca de discentes, a fim de realizar a opera��o de matr�cula 
	 *  em disciplinas de depend�ncias de discente em turma.
	 * <br><br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 */
	public String iniciarMatriculaDependencia() throws ArqException {
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		prepareMovimento( SigaaListaComando.MATRICULAR_DEPENDENCIA_MEDIO);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_DEPENDENCIA_MEDIO.getId());
		initObj();
		comando = SigaaListaComando.MATRICULAR_DEPENDENCIA_MEDIO; 
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.MATRICULA_DEPENDENCIA_MEDIO);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * M�todo utilizado para retornar ao resultado da busca por discente.
	 * <br><br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaMedio/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String voltarBuscaDiscente(){
		return forward("/graduacao/busca_discente.jsp");
	}
	
	/**
	 * M�todo utilizado para carregas os dados da turma selecionada para matricular o aluno,
	 * redirecionando o usu�rio para a tela de confirma��o de matr�cula.
	 * <br><br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaMedio/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws NegocioException 
	 */
	public String submeterDadosMatriculaMedio() throws SegurancaException, NegocioException { 
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		int idTurmaSelecionada = getParameterInt("id",0);
		
		if ( idTurmaSelecionada == 0 ) 
			addMensagemErro("Favor Selecionar uma Turma para realizar a Matr�cula do Aluno.");
		
		if (hasErrors()) return null;

		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class);
		DisciplinaMedioDao dmDao = getDAO(DisciplinaMedioDao.class);
		try {
			obj.setTurmaSerie(getGenericDAO().findByPrimaryKey(idTurmaSelecionada, TurmaSerie.class));
		
			validarTurma(obj.getTurmaSerie());
		
			if (hasErrors()) return null;
			
			//Popular os docentes por disciplina
			List<Integer> turmaIds = new ArrayList<Integer>();
			for (Iterator<TurmaSerieAno> it = obj.getTurmaSerie().getDisciplinas().iterator(); it.hasNext(); ) {
				TurmaSerieAno tsa = it.next();
				turmaIds.add(tsa.getTurma().getId());
			}
			
			List<Turma> turmas = dmDao.findByPrimaryKeyOtimizado(turmaIds);
			Map<Integer, Set<DocenteTurma>> mapDocentesTurma = new HashMap<Integer, Set<DocenteTurma>>();
			mapDocentesTurma = tsDao.findDocentesByTurmaSerie(obj.getTurmaSerie().getDisciplinas()); 
			
			
			for (TurmaSerieAno turmaAno : obj.getTurmaSerie().getDisciplinas()) {
				for (Turma t : turmas) {
					turmaAno.setTurma(turmaAno.getTurma().getId() == t.getId() ? t : turmaAno.getTurma());
				}
				if ( isNotEmpty( mapDocentesTurma.get(turmaAno.getTurma().getId()) ) ){
					turmaAno.getTurma().setDocentesTurmas(mapDocentesTurma.get(turmaAno.getTurma().getId()));
				} else {
					turmaAno.getTurma().setDocentesTurmas(new HashSet<DocenteTurma>());
					turmaAno.getTurma().setSituacaoTurma(turmaAno.getTurma().getSituacaoTurma());
				}
			}
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return forward(getJspResumo());
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		checkRole(new int[] { SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO});
		
		MovimentoAcademico movimento = new MovimentoAcademico();
		movimento.setObjMovimentado(obj.getDiscenteMedio());
		movimento.setObjAuxiliar(obj.getTurmaSerie());
		movimento.setCalendarioAcademicoAtual(getCalendarioVigente());
		
		movimento.setCodMovimento(comando);
	
		// Chamar processador
		try {
			executeWithoutClosingSession(movimento, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			afterCadastrar();
			removeOperacaoAtiva();
			return cancelar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/**
	 * M�todo utilizado para carregas os dados das turmas e disciplinas selecionadas 
	 * para matricular o aluno em depend�ncia, redirecionando o usu�rio para a tela 
	 * de confirma��o de matr�cula em depend�ncias.
	 * <br><br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaMedio/form_dependencia.jsp</li>
	 * </ul>	 
	 *
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public String matricularDependencias() throws DAOException, NegocioException{
		String[] linhas = getCurrentRequest().getParameterValues("disciplinas");
		DiscenteMedioDao dao = getDAO(DiscenteMedioDao.class);
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class);
		MatriculaComponenteDao mcDao = getDAO(MatriculaComponenteDao.class);
		setConfirmButton("Matricular Depend�ncia(s)");

		if ( linhas == null || linhas.length == 0 ) {
			addMensagemErro("Ao menos uma disciplina deve ser escolhida");
			return null;
		}
		disciplinas = new ArrayList<TurmaSerieAno>();
		List<Integer> idDisciplinas = new ArrayList<Integer>();
		for (String linha : linhas) {
			idDisciplinas.add(new Integer(linha));
		}
		Collection<TurmaSerieAno> listTurmaSerieAno = getDAO(DisciplinaMedioDao.class).findTurmaSerieAnoByIds(idDisciplinas);
		MatriculaDiscenteSerie serieAnterior = mdsDao.findSerieAnteriorDiscente(obj.getDiscenteMedio(), ano);
		Collection<MatriculaComponente> disciplinasAnterioresReprovadas = mcDao.findByDiscente(
				obj.getDiscenteMedio().getDiscente(), ano-1, 0, 
				SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA
				);
		
		if (isNotEmpty(serieAnterior)) {
			for (TurmaSerieAno tsa : listTurmaSerieAno) {
				if (validarDependencias(tsa, serieAnterior, dao, null))
					disciplinas.add( tsa );
			}
		} else if (isNotEmpty(disciplinasAnterioresReprovadas)) {
			for (TurmaSerieAno tsa : listTurmaSerieAno) {
				if (validarDependencias(tsa, null, dao, disciplinasAnterioresReprovadas))
					disciplinas.add( tsa );
			}
		} else {
			addMensagemErro("S� � poss�vel matricular depend�ncia para alunos com registro de matr�cula em s�rie anterior a atual.");
		}
		
		validarDependenciasMesmaDisciplina(disciplinas);
		
		if (hasErrors()) 
			return null;
		
		return forward(getJspResumoDependencia());
	}
	
	/**
	 * M�todo respons�vel por realizar a valida��o das matr�culas de depend�ncia. 
	 * 
	 * @param tsa
	 * @param dao
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private boolean validarDependencias(TurmaSerieAno tsa, MatriculaDiscenteSerie serieAnterior, DiscenteMedioDao dao, Collection<MatriculaComponente> disciplinasAnteriores) throws DAOException, NegocioException{
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class);
		boolean validacao = true;
		Collection<Turma> disciplinasMatriculadas = dao.findDisciplinasMatriculadas(obj.getDiscenteMedio().getId(),true);

		// Validar choque de hor�rios
		if ( ParametroHelper.getInstance().getParametroBoolean(ParametrosMedio.VALIDA_CHOQUE_HORARIO_DEPENDENCIA_MEDIO) 
				&& HorarioTurmaUtil.hasChoqueHorarios(tsa.getTurma(), obj.getDiscenteMedio(), null) ) {
			addMensagemErro("O aluno n�o pode ser matriculado na disciplina "+ tsa.getTurma().getDisciplina().getNome() +
			" da turma Depend�ncia, pois h� choque de hor�rio.");
			validacao = false;
		} 
		// Validar matriculas em mesmo turma
		if ( disciplinasMatriculadas.contains(tsa.getTurma())) {
			addMensagemErro("O aluno n�o pode ser matriculado na disciplina "+ tsa.getTurma().getDisciplina().getNome() +" da turma Depend�ncia, pois se encontra matriculado na mesma.");
			validacao = false;
		}
		
		// validar as Reprova��es do discente na s�rie anterior.
		if ( isNotEmpty(serieAnterior) ){
			Collection<Turma> disciplinasReprovadas = mdsDao.findDisciplinas( obj.getDiscenteMedio(), serieAnterior, true, SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId() );
			List<ComponenteCurricular> componentesReprovados = new ArrayList<ComponenteCurricular>();
			for (Turma t : disciplinasReprovadas) {
				componentesReprovados.add(t.getDisciplina());
			}
			if ( !componentesReprovados.contains(tsa.getTurma().getDisciplina())) {
				addMensagemErro("N�o � poss�vel matricular o aluno na disciplina "+ tsa.getTurma().getDisciplina().getNome() +" da turma Depend�ncia, pois n�o possui reprova��o para a mesma na s�rie anterior.");
				validacao = false;
			}
		}
		
		// validar as Reprova��es do discente na s�rie anterior, por implanta��o de hist�rico.
		if ( isNotEmpty(disciplinasAnteriores) ){
			List<ComponenteCurricular> componentesReprovados = new ArrayList<ComponenteCurricular>();
			for (MatriculaComponente mc : disciplinasAnteriores) {
				componentesReprovados.add(mc.getComponente());
			}
			if ( !componentesReprovados.contains(tsa.getTurma().getDisciplina())) {
				addMensagemErro("N�o � poss�vel matricular o aluno na disciplina "+ tsa.getTurma().getDisciplina().getNome() +" da turma Depend�ncia, pois n�o possui reprova��o para a mesma na s�rie anterior.");
				validacao = false;
			}
		}
		
		
		// Valida se a disciplina � da s�rie regular do discente
		MatriculaDiscenteSerie matriculaSerieRegular = mdsDao.findSerieAtualDiscente(obj.getDiscenteMedio(), CalendarioAcademicoHelper.getCalendarioUnidadeGlobalMedio().getAno());
		if ( matriculaSerieRegular != null && tsa.getTurmaSerie().getSerie().equals(matriculaSerieRegular.getTurmaSerie().getSerie()) ){
			addMensagemErro("N�o � poss�vel matricular o aluno na disciplina "+ 
				tsa.getTurma().getDisciplina().getNome() +" da turma '"+tsa.getTurmaSerie().getNome() +"', pois o aluno est� matriculado nesta s�rie de forma regular.");
			validacao = false;
		}
		
		// Valida se a disciplina est� consolidada. 
		if ( tsa.getTurma().isConsolidada() ){
			addMensagemErro("N�o � poss�vel matricular o aluno na disciplina "+ 
				tsa.getTurma().getDisciplina().getNome() +" da turma Depend�ncia, pois a mesma est� consolidada.");
			validacao = false;
		}
		
		return validacao;
	}
	
	/**
	 * M�todo respons�vel por verificar se o discente est� sendo matriculado
	 * na mesma disciplina em turmas diferentes. 
	 * @param disciplinas
	 * @throws DAOException 
	 */
	private void validarDependenciasMesmaDisciplina(List<TurmaSerieAno> disciplinas) throws DAOException{
		boolean mensagemErro = false;
		
		String erroMatriculaRepedita = null;
		List<MatriculaComponente> matriculas =(List<MatriculaComponente>) getDAO(MatriculaComponenteDao.class)
				.findByDiscente(obj.getDiscenteMedio(), SituacaoMatricula.getSituacoesMedio());
		for (Iterator<TurmaSerieAno> it = disciplinas.iterator(); it.hasNext();) {
			TurmaSerieAno tsa = it.next();
			for (TurmaSerieAno tsaAux : disciplinas) {
				if ( tsa.getTurma().getDisciplina().equals(tsaAux.getTurma().getDisciplina()) 
						&& tsa.getId() != tsaAux.getId() ) {
					mensagemErro = true;
				}	
			}
			for(MatriculaComponente mc : matriculas)
				if(mc.getTurma() != null && tsa.getTurma().getDisciplina().equals(mc.getTurma().getDisciplina()) && tsa.getTurma().getAno() == mc.getTurma().getAno()
					&& !SituacaoMatricula.getSituacoesInativas().contains(mc.getSituacaoMatricula())){
					erroMatriculaRepedita = "O aluno "+obj.getDiscenteMedio().getNome()+" j� est� matriculado na disciplina "+tsa.getTurma().getDisciplina().getDescricaoResumida()
					+" na "+mc.getSerie().getDescricaoCompleta()+".";
					mensagemErro = true;
				}
					
		}
		if (mensagemErro)
			if(erroMatriculaRepedita != null){
				addMensagemErro(erroMatriculaRepedita);
			}else
			addMensagemErro("N�o � permitido matricular o discente na mesma disciplina de depend�ncia em turmas diferentes.");
		
	}
	
	/**
	 * M�todo respons�vel por realizar a valida��o de turmaSerie. 
	 * 
	 * @param tsa
	 * @param dao
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private boolean validarTurma(TurmaSerie ts) throws DAOException, NegocioException{
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class);
		boolean validacao = true;
		
		// Validar situa��o de disciplinas da turma
		if ( tsDao.existeDisciplinaBySituacao(ts, SituacaoTurma.CONSOLIDADA) ) {
			addMensagemErro("O aluno n�o pode ser matriculado na Turma "+ ts.getNome() +" da "+ts.getSerie().getDescricaoCompleta()+", pois existem disciplinas CONSOLIDADAS na turma.");
			validacao = false;
		}
		
		// Valida��o da capacidade da turmaSerie
		ts.setQtdMatriculados(tsDao.findQtdeAlunosByTurma(ts));
		if ( ts.getCapacidadeAluno() <= ts.getQtdMatriculados() ){
			addMensagemErro("N�o � poss�vel matricular aluno, pois excede capacidade da turma.");
			validacao = false;
		}	
		
		return validacao;
	}	
	
	/**
	 * M�todo utilizado para realizar o cadastro das matr�culas em depend�ncia do discente.
	 * 
	 * <br><br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/medio/matriculaMedio/resumo_dependencia.jsp</li>
	 * </ul>	 
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrarDependencias() throws SegurancaException, ArqException,
			NegocioException {
		
		if (getConfirmButton().equals("Fechar")){
			cancelar();
		}
		
		MovimentoAcademico movimento = new MovimentoAcademico();
		movimento.setObjMovimentado(obj.getDiscenteMedio());
		movimento.setObjAuxiliar(disciplinas);
		movimento.setCalendarioAcademicoAtual(getCalendarioVigente());
		movimento.setCodMovimento(comando);
	
		// Chamar processador
		try {
			execute(movimento, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			setConfirmButton("Fechar");
			return cancelar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/** 
	 * M�todo respons�vel por listar as turmas de s�rie e ano especificados na JSP.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/matriculaMedio/form.jsp</li>
	 *  <li> /sigaa.war/medio/matriculaMedio/form_dependencia.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public String buscarTurmas() throws DAOException{
		
		if (isEmpty(obj.getTurmaSerie().getSerie().getCursoMedio())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		if (isEmpty(obj.getTurmaSerie().getSerie())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "S�rie");
		if (isEmpty(ano)) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
		if (hasErrors())
			return null;
		
		// Buscar as turma disponibilizadas para a s�rie e ano informados
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		if(comando == SigaaListaComando.MATRICULAR_DISCENTE_MEDIO){
			listaTurmaSeries = dao.findByCursoSerieAno(obj.getTurmaSerie().getSerie().getCursoMedio(), 
				    obj.getTurmaSerie().getSerie(), ano, false, getNivelEnsino(), true);
		}else{
			listaTurmaSeries = dao.findByCursoSerieAno(obj.getTurmaSerie().getSerie().getCursoMedio(), 
					obj.getTurmaSerie().getSerie(), ano, false, getNivelEnsino(), false);
		}
		if (listaTurmaSeries.isEmpty())
			addMensagemErro("N�o foram encontradas turmas para os dados informados.");
		
		return null;
	}
	
	
	/** 
	 * Carrega as s�ries pertencentes ao curso de ensino m�dio selecionado na jsp..
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/matriculaMedio/form.jsp</li>
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
	 * Carrega comboBox utilizadas no formul�rio de matr�cula de ensino m�dio.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/matriculaMedio/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void popularCombos() throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		
		if( isNotEmpty(obj.getTurmaSerie().getSerie().getCursoMedio()) )
			seriesByCurso = toSelectItems(dao.findByCurso(obj.getTurmaSerie().getSerie().getCursoMedio()), "id", "descricaoCompleta");
		else {
			seriesByCurso = new ArrayList<SelectItem>(0);
			return;
		}	
	}
	
	// Getters and Setters
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public Comando getComando() {
		return comando;
	}
	public void setComando(Comando comando) {
		this.comando = comando;
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
	public List<TurmaSerieAno> getDisciplinas() {
		return disciplinas;
	}
	public void setDisciplinas(List<TurmaSerieAno> disciplinas) {
		this.disciplinas = disciplinas;
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		if ( comando.equals(SigaaListaComando.MATRICULAR_DISCENTE_MEDIO) ){
			return forward( getFormPage() );
		}	
		else if ( comando.equals(SigaaListaComando.MATRICULAR_DEPENDENCIA_MEDIO) ){
			listaTurmaSeries = new ArrayList<TurmaSerie>();
			return forward( getJspFormDependencia() );
		}	
		else 
			return null;
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj.setDiscenteMedio((DiscenteMedio) discente);
	}

}
