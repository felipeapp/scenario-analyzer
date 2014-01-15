/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/04/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Reconhecimento;

/**
 * DAO responsável por consultas específicas ao Reconhecimento de Matizes Curriculares.
 *
 */
public class ReconhecimentoDao extends GenericSigaaDAO {

	/**
	 * retorna uma coleção de matrizes ativas reconhecidas e válidas
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<Reconhecimento> findReconhecimentosByCurso(Integer idCurso)
	throws DAOException {

			StringBuffer hql = new StringBuffer();
			
			hql.append(" SELECT r.portariaDecreto,r.dataDecreto, r.validade,t.sigla ,h.nome, g.descricao, c.nome ");
			hql.append(" FROM Reconhecimento r JOIN r.matriz m JOIN m.curso c  ");
			hql.append(" LEFT JOIN m.turno t LEFT JOIN m.habilitacao h LEFT JOIN m.grauAcademico g ");
			hql.append(" WHERE c.id = :idCurso AND (r.validade is null OR r.validade >= NOW())");
			
			Query q = getSession().createQuery(hql.toString());
			
			q.setInteger("idCurso", idCurso);
			
			@SuppressWarnings("unchecked")
			List<Object> reconhecimentos = q.list();
			ArrayList<Reconhecimento> result = new ArrayList<Reconhecimento>();
			Iterator<Object> it = reconhecimentos.iterator();
		
			while (it.hasNext()) {
				int col = 0;
				Object[] colunas = (Object[]) it.next();
				
				Reconhecimento r = new Reconhecimento();
				r.setPortariaDecreto((String) colunas[col++]);
				r.setDataDecreto((Date) colunas[col++]);
				r.setValidade((Date) colunas[col++]); 
				r.setMatriz(new MatrizCurricular()); 
				r.getMatriz().setTurno(new Turno());
				r.getMatriz().getTurno().setSigla((String)colunas[col++]);
				r.getMatriz().setHabilitacao(new Habilitacao());
				r.getMatriz().getHabilitacao().setNome((String) colunas[col++]);
				r.getMatriz().setGrauAcademico(new GrauAcademico());
				r.getMatriz().getGrauAcademico().setDescricao((String) colunas[col++]);
				r.getMatriz().setCurso(new Curso());
				r.getMatriz().getCurso().setNome((String) colunas[col++]);
				result.add(r);
			}
			return result;

	}

	/**
	 * Retorna os Reconhecimentos de uma dada Portaria,
	 * de uma dada Matriz Curricular ou todos os reconhecimentos.  
	 * 
	 * @param portaria
	 * @param idMatriz
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */	
	public Collection<Reconhecimento> findByMatrizAndPortaria(Integer idMatriz, String portaria, Integer idCurso)
			throws DAOException {

		String projecao = "r.id, r.matriz.curso.nome, r.matriz.grauAcademico.descricao," +
					" r.matriz.turno.sigla, r.matriz.curso.municipio.nome, r.validade, r.portariaDecreto, " +
					" h.nome as r.matriz.habilitacao.nome, e.nome as r.matriz.enfase.nome";
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT distinct ")
			.append(HibernateUtils.removeAliasFromProjecao(projecao))
			.append(" FROM Reconhecimento r" +
					" LEFT JOIN r.matriz.habilitacao h" +
					" LEFT JOIN r.matriz.enfase e" +
					" WHERE 1=1 ");
		if (idMatriz != null)
			hql.append(" and r.matriz.id = :idMatriz  ");
		if (idCurso != null)
			hql.append(" and r.matriz.curso.id = :idCurso  ");
		if (portaria != null)
			hql.append(" and lower(r.portariaDecreto) like lower('%'||:portaria||'%') ");
		
		hql.append("ORDER BY r.matriz.curso.nome, r.matriz.curso.municipio.nome, r.matriz.grauAcademico.descricao, h.nome, e.nome, r.matriz.turno.sigla, r.portariaDecreto");
		
		Query query = getSession().createQuery(hql.toString());
		
		if (idMatriz != null)
			query.setInteger("idMatriz", idMatriz);
		if (idCurso != null)
			query.setInteger("idCurso", idCurso);
		if (portaria != null)
			query.setString("portaria", portaria);
		
		@SuppressWarnings("unchecked")
		Collection<Reconhecimento> result = HibernateUtils.parseTo(query.list(), projecao, Reconhecimento.class, "r");
		
		return result;
	}
	
}
