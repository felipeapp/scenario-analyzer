/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Managed Bean respons�vel pelo processamento da consolida��o das matr�culas do discente em s�rie,
 * considerando o resultado de cada disciplina, necess�rio todas as disciplinas do discente
 * estarem consolidadas para realizar tal processamento. 
 * 
 * @author Rafael Gomes
 *
 */
@Component("processamentoMatriculaDiscenteSerie") @Scope("session")
public class ProcessamentoMatriculaDiscenteSerieMBean extends SigaaAbstractController<MatriculaDiscenteSerie>{

	/** Valor do ano a ser considerado para realizar o processamento de consolida��o dos discentes na s�rie. */
	private Integer ano;
	/** Curso selecionado para realizar a consolida��o dos discentes nas s�ries deste curso.*/
	private CursoMedio curso = new CursoMedio();
	/** Mapa utilizado para tratar as matriculas do discente em s�rie, considerando as formas de consolida��o para cadas disciplina.*/
	Map<DiscenteMedio, List<MatriculaDiscenteSerie>> map = new LinkedHashMap <DiscenteMedio, List<MatriculaDiscenteSerie>>();
	/** Lista de S�ries utilizada para o resultado do processamento de consolida��o de s�rie.*/
	List<Serie> listaSerie = new ArrayList<Serie>();
	/** Valor respons�vel por identificar a caracter�stica da opera��o.*/
	private boolean processamento;
	
	
	/** 
	 * Prepara e redireciona o usu�rio para a opera��o de processamento de matr�culas de discentes em s�rie.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Redireciona o usu�rio para a emiss�o do resultado do processamento de consolida��o dos discentes por s�rie.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo respons�vel por redirecionar o usu�rio para a p�gina de processamento de s�ries.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war\medio\processamentoMatriculaSerie\resultado.jsp</li>
	 * </ul>
	 * */
	public String formProcessamento(){
		return forward( getDirBase() + "/processamento.jsp");
	}
	 
	/** 
	 * M�todo respons�vel por redirecionar o usu�rio para a p�gina de processamento de s�ries.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo respons�vel por redirecionar o usu�rio para a p�gina de processamento de s�ries.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Carrega as matr�culas em disciplina e s�rie de discentes, 
	 * para consolidar as matr�culas do discente na s�rie.  
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
	 * M�todo respons�vel pelo retorno da situa��o do discente na s�rie 
	 * ap�s an�lise das formas de consolida��o de cada disciplina.
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
