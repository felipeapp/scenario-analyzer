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
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;

/**
 * Processa os vínculos de Concedente de estágio
 * 
 * @author Henrique André
 *
 */
public class ProcessarConcedente extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		VinculosDao dao = getDAO(VinculosDao.class, req); 
		
		try {
			List<ConcedenteEstagioPessoa> concedenteEstagio = dao.findConcedente(dados.getUsuario().getPessoa());
			if (isNotEmpty(concedenteEstagio)) {
				for (ConcedenteEstagioPessoa concedenteEstagioPessoa : concedenteEstagio) {
					dados.addVinculo(dados.getUsuario().getUnidade(), true, new TipoVinculoConcedenteEstagio(concedenteEstagioPessoa));
				}
			}					
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
