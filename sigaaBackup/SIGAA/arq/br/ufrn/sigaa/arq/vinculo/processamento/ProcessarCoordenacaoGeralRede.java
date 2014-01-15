package br.ufrn.sigaa.arq.vinculo.processamento;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenacaoGeralRede;

public class ProcessarCoordenacaoGeralRede extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {

		VinculosDao dao = getDAO(VinculosDao.class, req);

		try {
			/** Fazer projeção */
			List<CoordenacaoGeralRede> coords = dao.findCoordenacaoGeralRede(dados.getUsuario().getPessoa());
			if (isNotEmpty(coords)) {
				for (CoordenacaoGeralRede c : coords)
					dados.addVinculo(dados.getUsuario().getUnidade(), true,	new TipoVinculoCoordenacaoGeralRede(c));
			}
		} finally {
			if (dao != null)
				dao.close();
		}

	}

}
