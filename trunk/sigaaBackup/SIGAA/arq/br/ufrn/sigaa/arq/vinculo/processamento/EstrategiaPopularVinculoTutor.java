/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 11/08/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoTutor;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;

/**
 * Contém implementação para popular o vínculo de Tutor
 * 
 * @author Henrique André
 *
 */
public class EstrategiaPopularVinculoTutor implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {
		TutorOrientadorDao dao = DAOFactory.getInstance().getDAO(TutorOrientadorDao.class);
		
		try {
			TutorOrientador tutorOrientador = dao.findByPrimaryKey(vinculo.getTutor().getId(), TutorOrientador.class);
			if (tutorOrientador.isPresencial())
				tutorOrientador.getPoloCurso().getDescricao();
			return new VinculoUsuario(vinculo.getNumero(), vinculo.getUnidade(), vinculo.isAtivo(), new TipoVinculoTutor(tutorOrientador));
		} finally {
			dao.close();
		}
	}

}
