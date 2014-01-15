package br.ufrn.sigaa.mobile.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;

public class CursoDao extends GenericSigaaDAO {

	public Curso findOtimizado(Integer id) throws DAOException {
		String projecao = "c.id, c.nivel, c.unidade.id, c.unidade.gestoraAcademica.id ";
		
		String from = "from Curso c " +
						"join c.unidade u " +
						"join u.gestoraAcademica ga " +
						"where c.id = :curso";
		
		String hql = "select " + projecao + from;
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("curso", id);
		q.setMaxResults(1);
		
		Object[] linha = (Object[]) q.uniqueResult();
		int cont = 0;
		
		Curso curso = new Curso();
		
		curso.setId((Integer) linha[cont++]);
		curso.setNivel((Character) linha[cont++]);
		curso.setUnidade(new Unidade());
		curso.getUnidade().setId((Integer) linha[cont++]);
		curso.getUnidade().setGestoraAcademica(new Unidade());
		curso.getUnidade().getGestoraAcademica().setId((Integer) linha[cont++]);
		
		return curso;
	}
}
