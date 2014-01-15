package br.ufrn.sigaa.assistencia.dao;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;

/**
 * Classe respons�vel pela consultas nos hor�rios das refei��es cadastradas. 
 * 
 * @author Jean Guerethes
 */
public class TipoRefeicaoRUDao extends GenericSigaaDAO {

	/**
	 * Atualiza os hor�rios das refei��es do Restaurante Universit�rio 
	 */
	public void atualizarHorarios(Collection<TipoRefeicaoRU> horarios) throws DAOException {

		for (TipoRefeicaoRU tipoRefeicaoRU : horarios) {
			update(tipoRefeicaoRU);
		}
	}
	
}
