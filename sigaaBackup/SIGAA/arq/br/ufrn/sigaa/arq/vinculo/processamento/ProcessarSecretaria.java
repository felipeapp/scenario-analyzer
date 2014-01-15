/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoSecretario;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;

/**
 * Processar vínculos de secretário
 * 
 * @author Henrique André
 *
 */
public class ProcessarSecretaria extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		VinculosDao dao = getDAO(VinculosDao.class, req);
		
		try {
			Collection<SecretariaUnidade> secretarias = dao.findSecretarias(dados.getUsuario());
			
			for (SecretariaUnidade su : secretarias) {
				dados.addVinculo(su.getUnidade() != null ? su.getUnidade() : su.getCurso().getUnidade() , su.isAtivo(), new TipoVinculoSecretario(su));
			}		
		} finally {
			dao.close();
		}
	}

}
