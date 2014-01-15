/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 11/08/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoDiscente;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Contém implementação para popular o vínculo de Discente
 * 
 * @author Henrique André
 *
 */
public class EstrategiaPopularVinculoDiscente implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		DiscenteDao dao = DAOFactory.getInstance().getDAO(DiscenteDao.class);
		try {
			DiscenteAdapter discente = dao.findDetalhesByDiscente(vinculo.getDiscente());
			
			if ( discente instanceof DiscenteGraduacao ) {
				DiscenteGraduacao grad = (DiscenteGraduacao) discente;
				if(grad.isEAD()) {
					grad.getPolo().getDescricao();
				}
			}
			
			return new VinculoUsuario(vinculo.getNumero(), discente.getUnidade(), vinculo.isAtivo(), new TipoVinculoDiscente(discente));
		} finally {
			dao.close();
		}
	}

}
