/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
* Criado em: 13/07/2007
*/

package br.ufrn.sigaa.ensino.jsf;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Managed bean para a gera��o do relat�rio de notas por aluno.
 * O aluno pode escolher se quer o relat�rio de notas completo,
 * mostrando todas as notas de todas as disciplinas, ou parcial,
 * mostrando apenas as disciplinas do ano-per�odo escolhido
 *
 * @author David Pereira
 *
 */
@Component("relatorioNotasAluno")
@Scope("request")
public class RelatorioNotasAlunoMBean extends SigaaAbstractController<Object> {

	/** Objeto respons�vel por manter o discente do relat�rio de notas conforme o seu tipo de discente.*/
	private DiscenteAdapter discente;
	
	/** Relat�rio completo ou n�o */
	private boolean completo;

	/** Per�odo do relat�rio parcial */
	private int periodo;

	/** Ano do relat�rio parcial */
	private int ano;

	/** Listagem de matr�culas do aluno. Cont�m as suas notas em cada disciplina. */
	private List<MatriculaComponente> matriculas;

	/** Objeto respons�vel por manipular os par�metros da gestora acad�mica utilizados no relat�rio de notas do aluno.  */
	private ParametrosGestoraAcademica parametros;

	/**
	 * M�todo respons�vel por exibir o relat�rio contendo as notas do discente.
	 * Chamado pela(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/cpolo/cpolo.jsp</li>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * * <li>/sigaa.war/portais/discente/include/ensino.jsp</li>
	 * <li>/sigaa.war/portais/tutor/tutor.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String gerarRelatorio() throws Exception {
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		EADDao eDao = getDAO(EADDao.class);
		TurmaVirtualDao tDao = getDAO(TurmaVirtualDao.class);
				
		String str = getParameter("discente");
		
		if (str == null) {
			discente = getUsuarioLogado().getDiscenteAtivo();
		}		
		else {
			discente = dao.findDiscenteAdapterById(Integer.parseInt(str));			
		}

		if (discente == null) {
			addMensagemErro("O seu usu�rio n�o est� associado a nenhum discente.");
			return null;
		}

		if (getParametros() == null) {
			throw new NegocioException("A sua unidade n�o est� com os par�metros configurados. Por favor, contacte o suporte do sistema.");
		}

		matriculas = dao.findRelatorioNotasAluno(discente, completo, ano, periodo);
		
		// cache de metodologias para o EaD
		Map<Integer, MetodologiaAvaliacao> metodologias = new TreeMap<Integer, MetodologiaAvaliacao>();
		if (isEad()) {
			for (MatriculaComponente matricula : matriculas) {
				int anoPeriodo = matricula.getAno()*10 + matricula.getPeriodo();
				if (!metodologias.containsKey(anoPeriodo)) {
					MetodologiaAvaliacao metodologia = MetodologiaAvaliacaoHelper.getMetodologia(discente.getCurso(), matricula.getAno(), matricula.getPeriodo());
					metodologias.put(anoPeriodo, metodologia);
				}
			}
		}
		
		// cache de notas por tutor: mapa de ano-per�odo/id_matricula_componente/nota para o EaD
		Map<Integer, Map<Integer, Double>> notasTutor1 = new TreeMap<Integer, Map<Integer,Double>>();
		Map<Integer, Map<Integer, Double>> notasTutor2 = new TreeMap<Integer, Map<Integer,Double>>();
		if (isEad()) {
			MetodologiaAvaliacao metodologia = null;
			for (MatriculaComponente matricula : matriculas) {
				int anoPeriodo = matricula.getAno()*10 + matricula.getPeriodo();
				metodologia = metodologias.get(anoPeriodo);
				if (metodologia == null) {
					throw new NegocioException("Metodologia de avalia��o para seu curso n�o foi definido.");
				}
				if (!notasTutor1.containsKey(anoPeriodo)) {
					if (metodologia.isDuasProvas() && metodologia.isPermiteTutor()) {
						int aulaInicio = 1;
						int aulaFim = metodologia.getNumeroAulasInt()[0];
						Map<Integer, Double> notas1 = eDao.findNotasTutorByIntervaloAulas(discente.getId(), matricula.getAno(), matricula.getPeriodo(), metodologia, aulaInicio, aulaFim);
						notasTutor1.put(anoPeriodo, notas1);
						// notas 2
						aulaInicio = metodologia.getNumeroAulasInt()[0]+1;
						aulaFim = metodologia.getNumeroAulasInt()[0]+metodologia.getNumeroAulasInt()[1];
						Map<Integer, Double> notas2 = eDao.findNotasTutorByIntervaloAulas(discente.getId(), matricula.getAno(), matricula.getPeriodo(), metodologia, aulaInicio, aulaFim);
						notasTutor2.put(anoPeriodo, notas2);
					} else if (metodologia.isUmaProva() && metodologia.isPermiteTutor()){
						int aulaInicio = 1;
						int aulaFim = matricula.getComponente().getChTotalAula() > 100 ? 8 : 4;
						Map<Integer, Double> notas1 = eDao.findNotasTutorByIntervaloAulas(discente.getId(), matricula.getAno(), matricula.getPeriodo(), metodologia, aulaInicio, aulaFim);
						notasTutor1.put(anoPeriodo, notas1);
					}
				}
			}
		}
		
		// define as notas de tutores
		for (MatriculaComponente matricula : matriculas) {
			
			MetodologiaAvaliacao metodologia = null;
			if (isEad()) {
				int anoPeriodo = matricula.getAno()*10 + matricula.getPeriodo();
				metodologia = metodologias.get(anoPeriodo);
				
				if (metodologia == null) {
					throw new NegocioException("Metodologia de avalia��o para seu curso n�o foi definido.");
				}
				
				matricula.setMetodologiaAvaliacao(metodologia);
				
				if (metodologia.isDuasProvas() && metodologia.isPermiteTutor()) {
					matricula.setNotaTutor(notasTutor1.get(anoPeriodo).get(matricula.getComponente().getId()));
					matricula.setNotaTutor2(notasTutor2.get(anoPeriodo).get(matricula.getComponente().getId()));
				} else if (metodologia.isUmaProva() && metodologia.isPermiteTutor()){
					matricula.setNotaTutor(notasTutor1.get(anoPeriodo).get(matricula.getComponente().getId()));
				}
			}
			
			// Seta quantidade de unidades da matr�cula
			matricula.setQtdNotas(getQtdNotas(metodologia));
		}

		for (MatriculaComponente matricula : matriculas) {
			if ( !matricula.isConsolidada() ){
				if (matricula.getTurma() != null && matricula.getTurma().getId() != 0){
					ConfiguracoesAva config = tDao.findConfiguracoes(matricula.getTurma());
					if (config == null)
						matricula.setOcultarNota(false);
					else
						matricula.setOcultarNota(config.isOcultarNotas());
				}
			}	
		}
		
		return forward("/ensino/relatorios/notas_aluno.jsp");
	}
	
	/**
	 * Retorna true se completo, false se falso
	 * @return the completo
	 */
	public boolean isCompleto() {
		return completo;
	}

	/**
	 * Seta a vari�vel completo
	 * @param completo the completo to set
	 */
	public void setCompleto(boolean completo) {
		this.completo = completo;
	}

	/**
	 * Retorna o per�odo
	 * @return the periodo
	 */
	public int getPeriodo() {
		return periodo;
	}

	/**
	 * Seta a vari�vel per�odo
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/**
	 * Retorna o ano
	 * @return the ano
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * Seta a vari�vel ano
	 * @param ano the ano to set
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}


	/**
	 * Retorna as matr�culas
	 * @return the matriculas
	 */
	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}


	/**
	 * Seta as matr�culas
	 * @param matriculas the matriculas to set
	 */
	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	/**
	 * Retorna true se tem nota, false se n�o
	 * @return
	 * @throws Exception
	 */
	public boolean isNota() throws Exception {
		return getParametros().getMetodoAvaliacao() == MetodoAvaliacao.NOTA;
	}

	/**
	 * Retorna true se tem conceito, false se n�o
	 * @return
	 * @throws Exception
	 */
	public boolean isConceito() throws Exception {
		return getParametros().getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO;
	}

	/**
	 * Retorna true se tem competencia, false se n�o
	 * @return
	 * @throws Exception
	 */
	public boolean isCompetencia() throws Exception {
		return getParametros().getMetodoAvaliacao() == MetodoAvaliacao.COMPETENCIA;
	}

	/**
	 * Retorna a quantidade de de unidades dependendo da metodologia ou dos parametros da gestora.
	 * @return
	 */
	public int getQtdNotas(MetodologiaAvaliacao metodologia) throws Exception {
		if (isEad()) {
			if (metodologia != null && metodologia.isUmaProva()) return 1;
			else return 2;
		} else {
			return getParametros().getQtdAvaliacoes();
		}
	}	
	
	/**
	 * Retorna os par�metros
	 * @return
	 * @throws Exception
	 */
	public ParametrosGestoraAcademica getParametros() throws Exception {
		if (parametros == null) {
			parametros = ParametrosGestoraAcademicaHelper.getParametros( discente );
		}
		return parametros;
	}

	public boolean isEad() {
		return discente.isDiscenteEad(); 
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}


	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}
	
}
