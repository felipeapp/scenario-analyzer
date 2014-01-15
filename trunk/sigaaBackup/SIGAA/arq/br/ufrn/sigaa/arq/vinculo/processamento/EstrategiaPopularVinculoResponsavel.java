/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoResponsavel;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Contém implementação para popular o vínculo de Responsável
 * 
 * @author Henrique André
 *
 */
public class EstrategiaPopularVinculoResponsavel implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		
		ServidorDao dao = DAOFactory.getInstance().getDAO(ServidorDao.class);
		try {
			Servidor servidor = dao.findByPrimaryKey(vinculo.getServidor().getId(), Servidor.class);
			Unidade unidade =  dao.findByPrimaryKey(vinculo.getUnidade().getId(), Unidade.class);
			
			return new VinculoUsuario(vinculo.getNumero(), unidade, true, new TipoVinculoResponsavel(vinculo.getResponsavel(), servidor) ) ;
		} finally {
			dao.close();
		}
	}

}
