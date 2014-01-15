/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 26/09/2011
 */
package br.ufrn.sigaa.arq.vinculo.processamento;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.vinculo.dao.VinculosDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoFamiliar;
import br.ufrn.sigaa.ensino.medio.dominio.UsuarioFamiliar;

/**
 * Processa os vínculos do Familiar de discentes do ensino médio
 * 
 * @author Arlindo Rodrigues
 *
 */
public class ProcessarFamiliar extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req,
			DadosProcessamentoVinculos dados) throws ArqException {

		VinculosDao dao = getDAO(VinculosDao.class, req); 
		
		try {
			List<UsuarioFamiliar> usuarioFamiliar = dao.findFamiliarDiscentes(dados.getUsuario());
			if (isNotEmpty(usuarioFamiliar)) {
				for (UsuarioFamiliar uf : usuarioFamiliar) 
					dados.addVinculo(dados.getUsuario().getUnidade(), true, new TipoVinculoFamiliar(uf));
			}					
		} finally {
			if (dao != null)
				dao.close();
		}		
		
	}

}
