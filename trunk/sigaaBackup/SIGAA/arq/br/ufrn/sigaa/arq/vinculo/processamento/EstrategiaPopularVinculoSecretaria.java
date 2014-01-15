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
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoSecretario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;

/**
 * Contém implementação para popular o vínculo de Secretário
 * 
 * @author Henrique André
 *
 */
public class EstrategiaPopularVinculoSecretaria implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		SecretariaUnidadeDao dao = DAOFactory.getInstance().getDAO(SecretariaUnidadeDao.class);
		
		try {
			SecretariaUnidade secretariaUnidade = dao.findByPrimaryKey(vinculo.getSecretariaUnidade().getId(), SecretariaUnidade.class);
			secretariaUnidade.getUsuario().getPessoa().getCpfCnpjFormatado();
			
			return new VinculoUsuario(vinculo.getNumero(), 
					secretariaUnidade.getUnidade() != null ? secretariaUnidade.getUnidade() : secretariaUnidade.getCurso().getUnidade(), 
							vinculo.isAtivo(), 
							new TipoVinculoSecretario(secretariaUnidade));
		} finally {
			dao.close();
		}
	}

}
