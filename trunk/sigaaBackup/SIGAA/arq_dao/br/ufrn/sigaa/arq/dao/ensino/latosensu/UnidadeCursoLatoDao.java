package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.UnidadeCursoLato;

public class UnidadeCursoLatoDao extends GenericSigaaDAO {
	
		@SuppressWarnings("unchecked")
		public Collection<UnidadeCursoLato> findAllUnidadeCursoLato(CursoLato curso) throws DAOException {

			String hql = "from UnidadeCursoLato ucl left join ucl.curso c left join ucl.unidade u "+
					"where c.id = " + curso.getId();
			
			Collection<UnidadeCursoLato> undsCursoLato = new ArrayList<UnidadeCursoLato>();
			
			List<Object[]> list = getSession().createQuery(hql).list(); 

			for (Object[] item : list) {
				UnidadeCursoLato undCursoLato = new UnidadeCursoLato();
				undCursoLato = (UnidadeCursoLato) item[0];
				undCursoLato.setCurso((Curso) item[1]);
				undCursoLato.setUnidade((Unidade) item[2]);
				undsCursoLato.add(undCursoLato);
			}
			return undsCursoLato;
		}

}