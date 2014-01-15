package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino_rede.dao.CoordenadorUnidadeDao;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;

public class EstrategiaPopularVinculoCoordenadorUnidadeRede implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		CoordenadorUnidadeDao dao = DAOFactory.getInstance().getDAO(CoordenadorUnidadeDao.class);
		
		TipoVinculoCoordenadorUnidadeRede tipoVinculo = (TipoVinculoCoordenadorUnidadeRede) vinculo.getTipoVinculo();
		
		try {
			CoordenadorUnidade c = dao.findByPrimaryKey(tipoVinculo.getCoordenacao().getId(), CoordenadorUnidade.class);
			
			return new VinculoUsuario(vinculo.getNumero(), vinculo.getUnidade(), vinculo.isAtivo(), new TipoVinculoCoordenadorUnidadeRede(c));
		} finally {
			dao.close();
		}
	}

}
