/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 07/01/2009 
 *
 */

package br.ufrn.sigaa.processamento.dao;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;

/**
 * Consultas necess�rias para a realiza��o do processamento
 * de matr�culas. Interface que ter� v�rias implementa��es,
 * uma para cada tipo de processamento, a saber: gradua��o,
 * ensino a dist�ncia, t�cnico de m�sica.
 * 
 * @author David Pereira
 *
 */
public interface ProcessamentoMatriculaDao extends GenericDAO {

	/**
	 * Busca o n�mero de turmas que devem ser processadas pelo processamento
	 * de matr�cula.
	 */
	public int findCountTurmasProcessar(int ano, int periodo, boolean rematricula);

	/**
	 * Busca as turmas que devem ser processadas pelo processamento de matr�cula. O tamanho
	 * da cole��o retornada deve ser igual ao n�mero retornado pelo m�todo findCountTurmasProcessar().
	 */
	public List<Turma> findTurmasProcessar(int ano, int periodo, boolean rematricula);

	/**
	 * Utilizado para buscar solicita��es de matr�cula e transform�-las em matr�culas em espera.
	 */
	public List<SolicitacaoMatricula> findSolicitacoesMatricula(int ano, int periodo, boolean rematricula);

	/**
	 * Busca alunos que devem ter os seus c�lculos atualizados antes do processamento de matr�cula. 
	 */
	public List<Integer> findAlunosPreProcessamento(int ano, int periodo, boolean rematricula);

	/**
	 * Busca informa��o de quantas vagas existem na turma por matriz curricular.
	 */
	public Map<MatrizCurricular, ReservaCurso> findInformacoesVagasTurma(int id);

	/**
	 * Busca as matr�culas em espera de uma turma na ordem segundo as prioridades definidas pelo regulamento.
	 */
	public Map<Integer, List<MatriculaEmProcessamento>> findMatriculasEmOrdemParaProcessamento(Turma turma, Map<MatrizCurricular, ReservaCurso> vagasReservadas, boolean rematricula);

	/**
	 * Registra os dados do processamento de uma turma.
	 */
	public void registrarProcessamentoTurma(int ano, int periodo, boolean rematricula, Turma turma, List<MatriculaEmProcessamento> resultadoProcessamento);

	/**
	 * Busca o quantitativo do total de matriculados por turma.
	 * @param id
	 * @return
	 */
	public int findTotalMatriculadosTurma(int idTurma);

	/**
	 * Lista de Discentes Matriculados em blocos.
	 * 
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param rematricula
	 * @return
	 */
	public List<Integer> findDiscentesMatriculadosEmBlocos(int ano, int periodo, boolean rematricula);

	/**
	 *  Lista de identificadores de Discentes Matriculados em CoRequisitos..
	 * 
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param rematricula
	 * @return
	 */
	public List<Integer> findDiscentesMatriculadosEmCoRequisitos(int ano, int periodo, boolean rematricula);

	/**
	 * M�todo respons�vel pela atualiza��o do Motivo de um indeferimento.
	 * 
	 * @param key
	 * @param string
	 */
	public void atualizaMotivoIndeferimento(Integer key, String string);

	/**
	 * Busca de Matr�culas em blocos por Discente.
	 * 
	 * @param discente
	 * @param i
	 * @param j
	 * @return
	 */
	public List<MatriculaComponente> findMatriculasEmBlocoDiscente(
			Integer discente, int i, int j, boolean rematricula);

	/**
	 * Busca de Matr�culas em CoRequisitos por Discente.
	 * 
	 * @param discente
	 * @param i
	 * @param j
	 * @param b 
	 * @return
	 */
	public List<MatriculaComponente> findMatriculasEmCoRequisitoDiscente(
			Integer discente, int i, int j, boolean b);

	/**
	 * Busca de matr�culas por discente.
	 * 
	 * @param discente
	 * @param i
	 * @param j
	 * @param b 
	 * @return
	 */
	public List<MatriculaComponente> findMatriculasDiscente(Integer discente,
			int i, int j, boolean b);

	/**
	 * Busca resultado de processamento
	 * 
	 * @param id
	 * @return
	 */
	public List<MatriculaEmProcessamento> findResultadoProcessamento(int id);
	
	/**
	 * Busca as turmas que devem ser processadas pelo p�s-processamento de ensino individualizado. 
	 */
	public List<Turma> findTurmasEnsinoIndividualizadoProcessar(int ano, int periodo, boolean rematricula, boolean turmasRegular);
}