/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 18/09/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.negocio.MovimentoConsolidarSerie;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Managed Bean responsável pelo processamento da consolidação das matrículas do discente em série,
 * considerando o resultado de cada disciplina, necessário todas as disciplinas do discente
 * estarem consolidadas para realizar tal processamento. 
 * 
 * @author Rafael Gomes
 *
 */
@Component("processamentoMatriculaDiscenteSerie") @Scope("session")
public class ProcessamentoMatriculaDiscenteSerieMBean extends SigaaAbstractController<MatriculaDiscenteSerie>{

	/** Valor do ano a ser considerado para realizar o processamento de consolidação dos discentes na série. */
	private Integer ano;
	/** Curso selecionado para realizar a consolidação dos discentes nas séries deste curso.*/
	private CursoMedio curso = new CursoMedio();
	/** Mapa utilizado para tratar as matriculas do discente em série, considerando as formas de consolidação para cadas disciplina.*/
	Map<DiscenteMedio, List<MatriculaDiscenteSerie>> map = new LinkedHashMap <DiscenteMedio, List<MatriculaDiscenteSerie>>();
	/** Lista de Séries utilizada para o resultado do processamento de consolidação de série.*/
	List<Serie> listaSerie = new ArrayList<Serie>();
	/** Valor responsável por identificar a característica da operação.*/
	private boolean processamento;
	
	
	/** 
	 * Prepara e redireciona o usuário para a operação de processamento de matrículas de discentes em série.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\medio\menus\turma.jsp</li>
	 * </ul>
	 * */
	public String iniciar() throws Exception {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		setOperacaoAtiva(SigaaListaComando.CONSOLIDAR_MATRICULA_DISCENTE_SERIE.getId());
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = calendario.getAno();
		processamento = true;
		setConfirmButton("Processar");
		setLabelCombo("Processamento");
		
		return formProcessamento();
	}
	
	/** 
	 * Redireciona o usuário para a emissão do resultado do processamento de consolidação dos discentes por série.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\medio\menus\turma.jsp</li>
	 * </ul>
	 * */
	public String iniciarResultado() throws Exception {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = calendario.getAno();
		processamento = false;
		setConfirmButton("Emitir Resultado");
		setLabelCombo("Resultado");
		
		return formProcessamento();
	}
	
	@Override
	public String getDirBase() {
		return "/medio/processamentoMatriculaSerie";
	}
	
	/** 
	 * Método responsável por redirecionar o usuário para a página de processamento de séries.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\medio\processamentoMatriculaSerie\resultado.jsp</li>
	 * </ul>
	 * */
	public String formProcessamento(){
		return forward( getDirBase() + "/processamento.jsp");
	}
	 
	/** 
	 * Método responsável por redirecionar o usuário para a página de processamento de séries.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\medio\processamentoMatriculaSerie\processamento.jsp</li>
	 * </ul>
	 * */
	public String processar() throws Exception {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		
		if(hasErrors())
			return null;
		
 	
		if (!processamento)
			return emitirResultado();

		if (!confirmaSenha())
			return null;
			
		prepareMovimento(SigaaListaComando.CONSOLIDAR_MATRICULA_DISCENTE_SERIE);
	
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		if(curso.getId() > 0)
			dao.initialize(curso);
		
		MovimentoConsolidarSerie mov = new MovimentoConsolidarSerie();
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_MATRICULA_DISCENTE_SERIE);
		mov.setListMatriculaSerie(prepararMatriculasDiscenteSerie(ano, curso.getId()));
		mov.setAno(ano);

		try {
			execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		}
		listaSerie = new ArrayList<Serie>();
		listaSerie = (List<Serie>) dao.findSerieWithMatriculaByAno(ano, curso.getId(), 0);
		
		removeOperacaoAtiva();

		return forward( getDirBase() + "/resultado.jsf");
	}
	
	/** 
	 * Método responsável por redirecionar o usuário para a página de processamento de séries.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\medio\processamentoMatriculaSerie\processamento.jsp</li>
	 * </ul>
	 * */
	public String emitirResultado() throws Exception {
		
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		if(curso.getId() > 0)
			dao.initialize(curso);
	
		listaSerie = new ArrayList<Serie>();
		listaSerie = (List<Serie>) dao.findSerieWithMatriculaByAno(ano, curso.getId(), 0);
		
		return forward( getDirBase() + "/resultado.jsf");
	}
	
	/**
	 * Carrega as matrículas em disciplina e série de discentes, 
	 * para consolidar as matrículas do discente na série.  
	 * @param ano
	 * @throws DAOException
	 */
	private List<MatriculaDiscenteSerie> prepararMatriculasDiscenteSerie(int ano, int idCurso) throws DAOException{
		
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		map = new LinkedHashMap <DiscenteMedio, List<MatriculaDiscenteSerie>>();
		List<MatriculaDiscenteSerie> listMatriculaSerieUpdate = new ArrayList<MatriculaDiscenteSerie>();
		
		
		map = dao.findMatriculaDiscenteSerieByAno(ano, idCurso, 0);
		for (DiscenteMedio d : map.keySet()) {
			List<MatriculaDiscenteSerie> list = map.get(d);
			for (MatriculaDiscenteSerie mds : list) {
				mds.setSituacaoMatriculaSerie(situacaoSerie( mds ));
				listMatriculaSerieUpdate.add(mds);
			}
		}	
	
		return listMatriculaSerieUpdate;
	}
	
	/**
	 * Carrega uma lista de Serie para ser utilizada no resultado do processamento.
	 * @param ano
	 * @throws DAOException
	 */
	@SuppressWarnings("unused")
	private void prepararEmissaoResultado(){
		List<MatriculaDiscenteSerie> listMds = new ArrayList<MatriculaDiscenteSerie>();
		for (DiscenteMedio d : map.keySet()) {
			listMds.addAll(map.get(d));
		}	
		Serie serie = new Serie();
		TurmaSerie turmaSerie = new TurmaSerie();
		Set<TurmaSerie> listTurmaSerie = new HashSet<TurmaSerie>();
		for (MatriculaDiscenteSerie mds : listMds) {
			if (serie.getId() != mds.getTurmaSerie().getSerie().getId()){
				serie = mds.getTurmaSerie().getSerie();
				listTurmaSerie = new HashSet<TurmaSerie>();
			}
			listTurmaSerie.add(mds.getTurmaSerie());
		}
			
	}
	
	/**
	 * Método responsável pelo retorno da situação do discente na série 
	 * após análise das formas de consolidação de cada disciplina.
	 * @param matriculas
	 * @return
	 */
	private SituacaoMatriculaSerie situacaoSerie(MatriculaDiscenteSerie mds){
		
		Collection<MatriculaComponente> matriculas = mds.getMatriculasDisciplinas();
		boolean reprovada = false;
		boolean matriculada = false;
		int qtdeTrancados = 0, qtdeCancelados = 0;
		for (MatriculaComponente mc : matriculas) {
			if (SituacaoMatricula.getSituacoesReprovadas().contains(mc.getSituacaoMatricula())){
				reprovada = true;
			} else if (SituacaoMatricula.getSituacoesMatriculadas().contains(mc.getSituacaoMatricula())){
				matriculada = true;
			} else if (SituacaoMatricula.TRANCADO.equals(mc.getSituacaoMatricula())){
				qtdeTrancados++;
			} else if (SituacaoMatricula.CANCELADO.equals(mc.getSituacaoMatricula())){
				qtdeCancelados++;
			}
		}
		return (  matriculas.size() == qtdeTrancados ? SituacaoMatriculaSerie.TRANCADO 
				: matriculas.size() == qtdeCancelados ? SituacaoMatriculaSerie.CANCELADO 
				: matriculada ? SituacaoMatriculaSerie.MATRICULADO 
				: (reprovada ? SituacaoMatriculaSerie.REPROVADO 
				: SituacaoMatriculaSerie.APROVADO));
		
	}
	
	
	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public CursoMedio getCurso() {
		return curso;
	}

	public void setCurso(CursoMedio curso) {
		this.curso = curso;
	}

	public Map<DiscenteMedio, List<MatriculaDiscenteSerie>> getMap() {
		return map;
	}

	public void setMap(Map<DiscenteMedio, List<MatriculaDiscenteSerie>> map) {
		this.map = map;
	}

	public List<Serie> getListaSerie() {
		return listaSerie;
	}

	public void setListaSerie(List<Serie> listaSerie) {
		this.listaSerie = listaSerie;
	}

	public boolean isProcessamento() {
		return processamento;
	}

	public void setProcessamento(boolean processamento) {
		this.processamento = processamento;
	}
	
}
