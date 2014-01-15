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
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoDocenteExterno;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;

/**
 * Cont�m implementa��o para popular o v�nculo de Docente Externo
 * 
 * @author Henrique Andr�
 *
 */
public class EstrategiaPopularVinculoDocenteExterno implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		DocenteExternoDao dao = DAOFactory.getInstance().getDAO(DocenteExternoDao.class);
		try {
			DocenteExterno docenteExterno = dao.findAndFetch(vinculo.getDocenteExterno().getId(), DocenteExterno.class, "unidade");
			
			return new VinculoUsuario(vinculo.getNumero(), docenteExterno.getUnidade(), vinculo.isAtivo(), new TipoVinculoDocenteExterno(docenteExterno));
		} finally {
			dao.close();
		}
	}

}
