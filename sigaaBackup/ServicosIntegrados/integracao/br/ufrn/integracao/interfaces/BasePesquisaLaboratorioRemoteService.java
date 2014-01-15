package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.BasePesquisaLaboratorioDto;

/**
 * Interface que define os m�todos necess�rios para disponibiliza��o de acesso remoto aos Grupos de Pesquisa.
 *	<br/>
 * @author Mychell Teixeira
 */
@WebService
public interface BasePesquisaLaboratorioRemoteService extends Serializable {

		/**
		 * Retorna uma Cole��o de Base de Pesquisa para que podem ser vinculadas a laborat�rios.
		 * @param nome Nome utilizado no autocomplete 
		 * @return Lista de Base de Pesquisa.
		 * @throws DAOException
		 */
		public List<BasePesquisaLaboratorioDto>  getBasesPesquisaLaboratorio(  String nome );
		
		/**
		 * Recupera o Base de Pesquisa com base no identificador (Chave Prim�ria).
		 * @param idGrupoPesquisa Chave Prim�ria.
		 * @return Base de Pesquisa ligada ao laboat�rio.
		 * @throws DAOException
		 */
		public BasePesquisaLaboratorioDto findBasePesquisaLaboratorio( Integer idGrupoPesquisa );
}
