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
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoServidor;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Contém implementação para popular o vínculo de Servidor
 * 
 * @author Henrique André
 *
 */
public class EstrategiaPopularVinculoServidor implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		ServidorDao dao = DAOFactory.getInstance().getDAO(ServidorDao.class);
		
		try {
			Servidor servidor = dao.findByPrimaryKey(vinculo.getServidor().getId(), Servidor.class);
			servidor.setColaboradorVoluntario( dao.isColaboradorVoluntario(servidor) );
			
			return new VinculoUsuario(vinculo.getNumero(), servidor.getUnidade(), vinculo.isAtivo(), new TipoVinculoServidor(servidor));	
		}
		finally {
			dao.close();
		}
	}
}
