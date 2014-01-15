package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioNovosPesquisadoresDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Esse filtro irá retornar apenas os docentes que possuem Projeto de Apoio a Novos Pesquisadores 
 * cadastrado para o Edital em questão. 
 * 
 * @author Jean Guerethes
 */
public class FiltroDocenteConcorrendoProjApoioNovosPesquisadores implements FiltroDocente {

	@Override
	public Collection<Servidor> getDocentes(List<Integer> editaisSelecionados) throws DAOException {
		ProjetoApoioNovosPesquisadoresDao dao = DAOFactory.getInstance().getDAO(ProjetoApoioNovosPesquisadoresDao.class);
		try{
			return dao.findDocentesProjetoApoio(editaisSelecionados);
		}finally{ 
			dao.close(); 
		}
	}
	
}