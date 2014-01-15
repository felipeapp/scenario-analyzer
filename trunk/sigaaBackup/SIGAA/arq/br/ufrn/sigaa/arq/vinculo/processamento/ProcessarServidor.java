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
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoServidor;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processar o vínculo de Servidor
 * 
 * @author Henrique André
 *
 */
public class ProcessarServidor extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		
		VinculosDao dao = getDAO(VinculosDao.class, req);

		try {
			List<Servidor> servidores = dao.findServidores(dados.getUsuario().getPessoa());
			
			for( Servidor s : servidores ) {
				if (s.getAtivo().getId() == Ativo.SERVIDOR_ATIVO) 
					dados.addVinculo( s.getUnidade(), true,  new TipoVinculoServidor(s) );
				 else if (s.getAtivo().getId() != Ativo.BEN_PENSAO)
					dados.addVinculo( s.getUnidade(), s.isColaboradorVoluntario(), new TipoVinculoServidor(s) );
			}
		} finally {
			if (dao != null)
				dao.close();
		}
			
	}

}
