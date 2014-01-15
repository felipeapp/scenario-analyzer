package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Implementa o filtro de todos os docentes
 * @author Victor Hugo
 *
 */
public class FiltroDocenteTodos implements FiltroDocente {

	@Override
	public Collection<Servidor> getDocentes(List<Integer> editaisSelecionados) throws DAOException {
		
		ServidorDao dao = new ServidorDao();
		try {
			return dao.findAllDocentes();
		} finally { dao.close(); }
		
	}

}
