package br.ufrn.sigaa.arq.dao.ensino;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.AlteracaoAtivacaoComponente;

/**
 * DAO responsável por consultas relacionadas a ativacao do componente curricular
 * 
 * @author henrique
 *
 */
public class AlteracaoAtivacaoComponenteDao extends GenericSigaaDAO {
	
	/**
	 * Localiza o registro de ativação de um componente
	 * 
	 * @param idComponente
	 * @return
	 * @throws DAOException
	 */
	public AlteracaoAtivacaoComponente findByComponenteCurricular(int idComponente) throws DAOException {
		
		Query query = getSession().createQuery("select ativacao from AlteracaoAtivacaoComponente ativacao where componenteCurricular.id = :id");
		query.setInteger("id", idComponente);
		
		return (AlteracaoAtivacaoComponente) query.uniqueResult();
	}
	
}
