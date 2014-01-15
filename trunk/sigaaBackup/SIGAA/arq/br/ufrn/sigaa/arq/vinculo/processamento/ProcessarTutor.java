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
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoTutor;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;

/**
 * Processar permissões de tutor
 * 
 * @author Henrique André
 *
 */
public class ProcessarTutor extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		
		VinculosDao dao = getDAO(VinculosDao.class, req);
		try {
			List<TutorOrientador> tutores = dao.findTutores(dados.getUsuario().getPessoa());
			if (isNotEmpty(tutores)) {
				for (TutorOrientador tutorOrientador : tutores) {
					dados.addVinculo(dados.getUsuario().getUnidade(), true, new TipoVinculoTutor(tutorOrientador));
				}
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
