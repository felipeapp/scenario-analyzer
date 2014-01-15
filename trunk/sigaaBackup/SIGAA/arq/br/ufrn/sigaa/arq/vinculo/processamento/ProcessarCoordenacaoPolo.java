/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorPolo;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;

/**
 * Processa os vínculos de coordenação de polo
 * 
 * @author Henrique André
 *
 */
public class ProcessarCoordenacaoPolo extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		
		VinculosDao dao = getDAO(VinculosDao.class, req);
		try {
			List<CoordenacaoPolo> coordenadoresPolo = dao.findCoordenadoresPolo(dados.getUsuario().getPessoa());
			if (isNotEmpty(coordenadoresPolo)) {
				for (CoordenacaoPolo coordenacaoPolo : coordenadoresPolo) {
					dados.addVinculo(dados.getUsuario().getUnidade(), true, new TipoVinculoCoordenadorPolo(coordenacaoPolo));
				}
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
