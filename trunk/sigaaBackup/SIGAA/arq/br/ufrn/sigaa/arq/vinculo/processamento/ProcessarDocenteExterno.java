/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoDocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;

/**
 * Processa os vínculos de docente externo
 * 
 * @author Henrique André
 *
 */
public class ProcessarDocenteExterno extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		
		VinculosDao docDao = getDAO(VinculosDao.class, req);
		try {
			List<DocenteExterno> docentesExternos = docDao.findDocenteExternos(dados.getUsuario().getPessoa());
			if (!ValidatorUtil.isEmpty(docentesExternos)) {
				for (DocenteExterno de : docentesExternos) {
					dados.addVinculo(de.getUnidade(),  de.isAtivo(), new TipoVinculoDocenteExterno(de));
				}
			}
		} finally {
			if (docDao != null)
				docDao.close();
		}
	}

}
