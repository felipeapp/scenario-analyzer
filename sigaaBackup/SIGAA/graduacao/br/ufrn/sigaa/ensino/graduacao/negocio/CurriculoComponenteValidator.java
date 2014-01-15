/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.ensino.GrupoOptativasDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;

/**
 * Validador usado para operações envolvendo a entidade CurriculoComponente
 * 
 * @author Henrique André
 *
 */
public class CurriculoComponenteValidator {

	/**
	 * Verifica se um {@link CurriculoComponente} esta associado a um {@link GrupoOptativas}
	 * 
	 * @param idCurriculoComponente
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void verificarGrupoOptativa(CurriculoComponente cc) throws DAOException, NegocioException {
		
		GrupoOptativasDao dao = null;
		try {
			dao = DAOFactory.getInstance().getDAO(GrupoOptativasDao.class);
			List<GrupoOptativas> grupos = dao.findByCurriculoComponente(cc.getId());
			
			StringBuilder erros = new StringBuilder();
			
			if (isEmpty(grupos)) 
				return;
			
			erros.append(cc.getComponente().getNome() + " pertence aos seguintes Grupos de Optativas:");
			for (GrupoOptativas grupo : grupos) {
				erros.append("\n").append(" - " + grupo.getDescricao());
			}
			
			throw new NegocioException(erros.toString());
		} finally {
			if (dao != null)
				dao.close();
		}
			
	}
}
