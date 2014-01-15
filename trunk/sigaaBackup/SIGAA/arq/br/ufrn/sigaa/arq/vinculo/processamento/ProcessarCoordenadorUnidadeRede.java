package br.ufrn.sigaa.arq.vinculo.processamento;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;

public class ProcessarCoordenadorUnidadeRede extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {

		VinculosDao dao = getDAO(VinculosDao.class, req);

		try {
			/** Fazer projeção */
			List<CoordenadorUnidade> coords = dao.findCoordenacaoUnidadeRede(dados.getUsuario().getPessoa());
			if (isNotEmpty(coords)) {
				for (CoordenadorUnidade c : coords)
					dados.addVinculo(dados.getUsuario().getUnidade(), true,	new TipoVinculoCoordenadorUnidadeRede(c));
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
