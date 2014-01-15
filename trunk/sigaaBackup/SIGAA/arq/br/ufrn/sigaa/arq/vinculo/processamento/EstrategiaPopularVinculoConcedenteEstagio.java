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
import br.ufrn.sigaa.arq.dao.estagio.ConcedenteEstagioDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoConcedenteEstagio;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;

/**
 * Contém implementação para popular o vínculo de Concedente de estágio
 * 
 * @author Henrique André
 *
 */
public class EstrategiaPopularVinculoConcedenteEstagio implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		ConcedenteEstagioDao dao = DAOFactory.getInstance().getDAO(ConcedenteEstagioDao.class);
		
		try {
			ConcedenteEstagioPessoa concedenteEstagioPessoa = dao.findAndFetch(vinculo.getConcedenteEstagioPessoa().getId(), ConcedenteEstagioPessoa.class, "concedente","concedente.pessoa", "pessoa");
			
			return new VinculoUsuario(vinculo.getNumero(), vinculo.getUnidade(), vinculo.isAtivo(), new TipoVinculoConcedenteEstagio(concedenteEstagioPessoa));
		} finally {
			dao.close();
		}
	}

}
