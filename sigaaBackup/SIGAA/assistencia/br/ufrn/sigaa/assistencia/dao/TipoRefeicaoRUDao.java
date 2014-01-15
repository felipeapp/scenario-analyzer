package br.ufrn.sigaa.assistencia.dao;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;

/**
 * Classe responsável pela consultas nos horários das refeições cadastradas. 
 * 
 * @author Jean Guerethes
 */
public class TipoRefeicaoRUDao extends GenericSigaaDAO {

	/**
	 * Atualiza os horários das refeições do Restaurante Universitário 
	 */
	public void atualizarHorarios(Collection<TipoRefeicaoRU> horarios) throws DAOException {

		for (TipoRefeicaoRU tipoRefeicaoRU : horarios) {
			update(tipoRefeicaoRU);
		}
	}
	
}
