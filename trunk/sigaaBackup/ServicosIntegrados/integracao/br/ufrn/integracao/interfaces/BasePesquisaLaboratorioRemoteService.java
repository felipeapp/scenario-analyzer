package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.BasePesquisaLaboratorioDto;

/**
 * Interface que define os métodos necessários para disponibilização de acesso remoto aos Grupos de Pesquisa.
 *	<br/>
 * @author Mychell Teixeira
 */
@WebService
public interface BasePesquisaLaboratorioRemoteService extends Serializable {

		/**
		 * Retorna uma Coleção de Base de Pesquisa para que podem ser vinculadas a laboratórios.
		 * @param nome Nome utilizado no autocomplete 
		 * @return Lista de Base de Pesquisa.
		 * @throws DAOException
		 */
		public List<BasePesquisaLaboratorioDto>  getBasesPesquisaLaboratorio(  String nome );
		
		/**
		 * Recupera o Base de Pesquisa com base no identificador (Chave Primária).
		 * @param idGrupoPesquisa Chave Primária.
		 * @return Base de Pesquisa ligada ao laboatório.
		 * @throws DAOException
		 */
		public BasePesquisaLaboratorioDto findBasePesquisaLaboratorio( Integer idGrupoPesquisa );
}
