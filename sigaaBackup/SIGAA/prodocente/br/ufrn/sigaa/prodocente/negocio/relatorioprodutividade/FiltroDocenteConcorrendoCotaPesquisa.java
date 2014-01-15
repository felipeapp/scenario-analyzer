package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Este filtro trás apenas os docentes concorrendo a cota de pesquisa nos editais selecionados
 * @author Victor Hugo
 *
 */
public class FiltroDocenteConcorrendoCotaPesquisa implements FiltroDocente {

	@Override
	public Collection<Servidor> getDocentes(List<Integer> editaisSelecionados) throws DAOException {
		PlanoTrabalhoDao dao = new PlanoTrabalhoDao();
		try{
			return dao.findDocentesConcorrendoCota(editaisSelecionados);
		}finally{ dao.close(); }
	}

}
