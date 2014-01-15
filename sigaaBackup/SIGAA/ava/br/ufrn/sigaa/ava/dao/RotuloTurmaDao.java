/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/05/2011'
 *
 */
package br.ufrn.sigaa.ava.dao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.RotuloTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe de acesso a dados de rótulos de turmas virtuais.
 * 
 * @author ilueny santos.
 *
 */
public class RotuloTurmaDao extends GenericSigaaDAO {

	/**
	 * Lista todos os rótulos da turma. 
	 * 
	 * @throws  
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<RotuloTurma> findRotulosByTurma(Turma turma) throws DAOException  {
		String projecao = " id, ativo, descricao, visivel, dataCadastro, usuarioCadastro.id, aula.id, aula.descricao ";
		Query q = getSession().createQuery(
				"SELECT " + projecao + " FROM RotuloTurma rt WHERE rt.aula.turma.id = :idTurma and rt.ativo = trueValue()");
		q.setInteger("idTurma", turma.getId());
		return (List<RotuloTurma>) HibernateUtils.parseTo(q.list(), projecao, RotuloTurma.class);
	}


}
