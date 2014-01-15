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
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Processa os vínculos de discente
 * 
 * @author Henrique André
 *
 */
public class ProcessarDiscente extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		
		VinculosDao dao = getDAO(VinculosDao.class, req);
		
		try {
			// DISCENTES
			List<DiscenteAdapter> discentes = dao.findDiscentes(dados.getUsuario().getPessoa());
			
			if (isNotEmpty(discentes)) {
				
				for (DiscenteAdapter d : discentes) {
					dados.addVinculo(d.getUnidade(),  d.isAtivo(), new TipoVinculoDiscente(d));
				}
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
