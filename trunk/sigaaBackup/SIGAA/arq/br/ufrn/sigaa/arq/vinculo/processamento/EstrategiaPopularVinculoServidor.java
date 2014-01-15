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
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoServidor;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Cont�m implementa��o para popular o v�nculo de Servidor
 * 
 * @author Henrique Andr�
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
