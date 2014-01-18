package br.ufrn.sigaa.ensino_rede.academico.negocio;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ensino_rede.dao.AlteracaoStatusDiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoStatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;

/**
 * 
 * M�todos utilit�rios para opera��es sobre os discentes no m�dulo de ensino em redes.
 *
 * @author Andre M Dantas
 *
 */
public class DiscenteRedeHelper {
	
	/**
	 * Retorna o �ltimo status registrado para este discente em AlteracaoStatusDiscenteAssociado;
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static Integer getUltimoStatus(DiscenteAssociado discente) throws DAOException {
		AlteracaoStatusDiscenteAssociadoDao dao = new AlteracaoStatusDiscenteAssociadoDao();
		try {
			AlteracaoStatusDiscenteAssociado alteracao = dao.findUltimaAlteracaoByDiscente(discente.getId());
			if (alteracao == null)
				return null;
			return alteracao.getStatus();
		} finally {
			dao.close();
		}
	}	
	
}
