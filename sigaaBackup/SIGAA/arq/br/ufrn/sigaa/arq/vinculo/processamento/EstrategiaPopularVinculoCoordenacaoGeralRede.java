package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino_rede.dao.CoordenacaoGeralRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenacaoGeralRede;

public class EstrategiaPopularVinculoCoordenacaoGeralRede implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		CoordenacaoGeralRedeDao dao = DAOFactory.getInstance().getDAO(CoordenacaoGeralRedeDao.class);
		
		TipoVinculoCoordenacaoGeralRede tipoVinculo = (TipoVinculoCoordenacaoGeralRede) vinculo.getTipoVinculo();
		
		try {
			CoordenacaoGeralRede c = dao.findByPrimaryKey(tipoVinculo.getCoordenacao().getId(), CoordenacaoGeralRede.class);
			
			return new VinculoUsuario(vinculo.getNumero(), vinculo.getUnidade(), vinculo.isAtivo(), new TipoVinculoCoordenacaoGeralRede(c));
		} finally {
			dao.close();
		}
	}

}
