/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 11/08/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorPolo;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;

/**
 * Cont�m implementa��o para popular o v�nculo de Coordenador de Polo
 * 
 * @author Henrique Andr�
 *
 */
public class EstrategiaPopularVinculoCoordenadorPolo implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		PoloDao dao = DAOFactory.getInstance().getDAO(PoloDao.class);
		try {
			CoordenacaoPolo coordenacaoPolo = dao.findByPrimaryKey(vinculo.getCoordenacaoPolo().getId(), CoordenacaoPolo.class);
			
			return new VinculoUsuario(vinculo.getNumero(), vinculo.getUnidade(), vinculo.isAtivo(), new TipoVinculoCoordenadorPolo(coordenacaoPolo));
		} finally {
			dao.close();
		}
	}

}
