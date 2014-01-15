/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 12/02/2008
 */
package br.ufrn.comum.dao;

import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.ProcessoInteressado;
import br.ufrn.integracao.dto.InteressadoDTO;
import br.ufrn.integracao.dto.MovimentoDTO;
import br.ufrn.integracao.dto.ProcessoDTO;

/**
 * @author Itamir Filho
 *
 */
public interface ProcessoDAO extends GenericDAO{

	/**
	 * Busca o processo por n�mero, ano e dv.
	 * @param numero
	 * @param ano
	 * @param dv
	 * @return
	 * @throws DAOException
	 */
	public ProcessoDTO findByIdentificador(int numero, int ano, int dv) throws DAOException;
	
	/**
	 * Busca os processos por interessado.
	 *  
	 * @param identificador
	 * @return
	 * @throws DAOException
	 */
	public List<ProcessoDTO> findByInteressado(String identificador) throws DAOException;	
	
	/**
	 * Busca os processos por interessado.
	 * @param identificador
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public List<ProcessoDTO> findByInteressado(String identificador, int tipo) throws DAOException;
	
	/**
	 * Busca processos por aluno
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public List<ProcessoDTO> findByAluno(long matricula, char nivel) throws DAOException;
	
	/**
	 * Busca o pr�ximo n�mero do processo.
	 * @return
	 * @throws DAOException
	 */
	public int findNextNumero(int radical, int ano) throws DAOException;
	
	/**
	 * Realiza a cria��o do um processo.
	 * @param processo
	 * @return
	 */
	public int criarProcesso(ProcessoDTO processo);
	
	/**
	 * realiza a cria��o de um movimento de um processo.
	 * @param movimento
	 * @return
	 */
	public int criarMovimento(MovimentoDTO movimento);
	
	/**
	 * Atualiza  o movimento atual do processo.
	 * @param processo
	 */
	public void atualizarMovimentoAtual(ProcessoDTO processo);

	/**
	 * Cria um interessado de um processo.
	 * @param interessado
	 * @return
	 */
	public int criarInteressado(InteressadoDTO interessado);
	
	/**
	 * Cria uma associa��o entre um interessado e um processo.
	 * @param interessadoProcesso
	 * @return
	 */
	public int criaInteressadoProcesso(ProcessoInteressado interessadoProcesso);
	
	/**
	 * Recebe um processo. Esse movimento recebido como par�metro refere-se
	 * ao movimento atual do processo.
	 * @param movimento
	 */
	public void receberProcesso(MovimentoDTO movimento);
	
	/**
	 * Enviar um processo. ao enviar um processo cria-se o movimento.
	 * @param movimento
	 */
	public int enviarProcesso(MovimentoDTO movimento);
	
}
