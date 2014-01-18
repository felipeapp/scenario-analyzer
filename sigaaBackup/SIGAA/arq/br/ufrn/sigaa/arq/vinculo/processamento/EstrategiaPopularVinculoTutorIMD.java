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
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoTutorIMD;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * Contém implementação para popular o vínculo de Tutor do IMD
 * 
 * @author Gleydson Lima
 * 
 */
public class EstrategiaPopularVinculoTutorIMD implements EstrategiaPopularVinculo {

	@Override
	public VinculoUsuario popular(VinculoUsuario vinculo) throws DAOException {

		TutoriaIMDDao dao = DAOFactory.getInstance().getDAO(TutoriaIMDDao.class);

		try {

			TutoriaIMD tutoria = dao.findByPrimaryKey(vinculo.getTutoriaIMD().getId(), TutoriaIMD.class);
			tutoria.getTurmaEntrada().getDescricao();
			tutoria.getTutor().getPessoa().getCpf_cnpj();
			
			Unidade imd = dao.findByPrimaryKey(ParametroHelper.getInstance().getParametroInt(
					ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL), Unidade.class);
			return new VinculoUsuario(vinculo.getNumero(), imd, vinculo.isAtivo(),
					new TipoVinculoTutorIMD(tutoria));
		} finally {
			dao.close();
		}

	}

}
