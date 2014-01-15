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

import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoResponsavel;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.VinculoUsuario;

/**
 * Processa os vínculos sobre a unidades que o usuário tem uma responsabilidade
 * 
 * @author Henrique André
 *
 */
public class ProcessarResponsavel extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		UnidadeDAOImpl undDao = new UnidadeDAOImpl(Sistema.COMUM);
		ServidorDao sDao = getDAO(ServidorDao.class, req);	
		
		try {
			for (VinculoUsuario vinculo : dados.getVinculosServidor()) {
				Collection<Responsavel> responsabilidades = undDao.findResponsabilidadeUnidadeAcademicaByServidor(vinculo.getServidor().getId(), new Character[] {NivelResponsabilidade.CHEFE, NivelResponsabilidade.VICE, NivelResponsabilidade.SUPERVISOR_DIRETOR_ACADEMICO});
				
				for (Responsavel resp : responsabilidades) {
					dados.addVinculo(sDao.findByPrimaryKey(resp.getUnidade().getId(), Unidade.class), true, new TipoVinculoResponsavel(resp, vinculo.getServidor()));
				}
			}
		} finally {
			if (undDao != null)
				undDao.close();
			if (sDao != null)
				sDao.close();
		}
	}

}
