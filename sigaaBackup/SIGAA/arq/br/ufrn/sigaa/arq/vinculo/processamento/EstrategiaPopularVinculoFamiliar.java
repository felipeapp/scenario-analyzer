/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 26/09/2011
 */
package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoFamiliar;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;

/**
 * Contém implementação para popular o vínculo de familiar de discente médio
 * 
 * @author Arlindo Rodrigues
 * 
 */
public class EstrategiaPopularVinculoFamiliar implements EstrategiaPopularVinculo  {

	/**
	 * Popula o usuário
	 */
	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		DiscenteDao dao = DAOFactory.getInstance().getDAO(DiscenteDao.class);
		try {
			DiscenteAdapter discente = dao.findDetalhesByDiscente(vinculo.getFamiliar().getDiscenteMedio().getDiscente());
			vinculo.getFamiliar().setDiscenteMedio((DiscenteMedio) discente);
			return new VinculoUsuario(vinculo.getNumero(), discente.getUnidade(), vinculo.isAtivo(), new TipoVinculoFamiliar(vinculo.getFamiliar()));
		} finally {
			dao.close();
		}
	}


}
