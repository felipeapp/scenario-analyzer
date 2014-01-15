package br.ufrn.integracao.interfaces;

import java.util.List;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.MaterialInformacionalDTO;

/**
 * Interface que define os métodos a serem implementados para disponibilizar o acesso remoto a dados da biblioteca.
 * 
 * @author Fred_Castro
 *
 */
@WebService
public interface DadosBibliotecaRemoteService {
	/**
	 * Retorna a quantidade de materiais ativos para todos os títulos catalográficos.
	 * @return HashMap contendo as ids dos títulos e as quantidades de materiais.
	 */
	public List<MaterialInformacionalDTO> getQuantidadeMateriaisAtivosPorTitulo (List <Integer> ids);
	
	/**
	 * Método responsável por realizar a busca na unidade da coordenação do usuário logado caso o mesmo seja aluno.
	 * 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public int getUnidadeCoordenacaoServidor(int idServidor);
	
	/**
	 * Método responsável por realizar a busca na unidade da coordenação do usuário logado caso o mesmo seja aluno.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public int getUnidadeCoordenacaoAluno(int idCurso,char nivel);
}
