/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/04/2011
 */
package br.ufrn.integracao.interfaces;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.AlunoDTO;
import br.ufrn.integracao.dto.UnidadeDTO;

/**
 * Serviço remoto para buscar informações de alunos bolsistas no SIPAC.
 * 
 * @author David Pereira
 *
 */
@WebService
@Transactional(propagation=Propagation.REQUIRED)
public interface DadosBolsistasRemoteService {

	/** 
	 * Retorna todos os alunos com bolsa ativa da unidade especificada e que NÃO estejam dimensionados. 
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public List<AlunoDTO> findBolsistasByUnidade(UnidadeDTO unidade);


	/** 
	 * Retorna todos os alunos com bolsa ativa da unidade especificada e que estejam dimensionados. 
	 *
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public List<AlunoDTO> findBolsistasDimensionadosByUnidade(UnidadeDTO unidade);


	/**
	 * Busca as informações de um aluno associado à determinada bolsa
	 * @param idBolsa
	 * @return
	 * @throws DAOException
	 */
	public AlunoDTO findAlunoByIdBolsa(int idBolsa);


	/**
	 * Busca o identificador da bolsa de acordo com o aluno associado.
	 * @param idAluno
	 * @return
	 */
	public int findIdBolsaByIdAluno(int idAluno);

	/**
	 * Retorna o aluno com a sua matricula especificada caso este tenha bolsa ativa 
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public AlunoDTO findAlunoBolsistaByMatricula(String matricula);
	
	/**
	 * Retorna todos os bolsistas de acordo com o período informado.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public List<AlunoDTO> findAllDiscenteBolsistasPorPeriodoUnidadeMatricula(Date dataInicio, Date dataFim, Integer tipoBolsa, Integer idPessoa, Collection<Integer> unidades);

	/**
	 * Retorna todos os bolsistas de acordo com o período informado.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public List<AlunoDTO> findAllDiscenteBolsistasPorPeriodo(Date dataInicio, Date dataFim, Integer tipoBolsa);

}
