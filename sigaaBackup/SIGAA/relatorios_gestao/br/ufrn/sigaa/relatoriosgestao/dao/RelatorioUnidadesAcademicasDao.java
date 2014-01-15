/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 23/03/2011
 */

package br.ufrn.sigaa.relatoriosgestao.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Dao Responsável por realizar consultas que serão utilizadas para geração de relatórios 
 * de unidades acadêmicas.
 * 
 * @author Arlindo
 *
 */
public class RelatorioUnidadesAcademicasDao extends GenericSigaaDAO {
	
	/**
	 * Retorna o total das Unidades Acadêmicas (Departamento, Escola e Unidade Acadêmica Especializada)
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotalUnidadeAcademica() throws HibernateException, DAOException{
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT  tu.id_tipo_unidade, ");
		sql.append("     tu.descricao AS tipo_unidade, cast(count(*) as integer) as total ");
		sql.append("FROM comum.unidade u ");
		sql.append("JOIN comum.tipo_unidade tu ON tu.id_tipo_unidade = u.tipo_academica ");
		sql.append("WHERE u.tipo_academica IN ("
				+TipoUnidadeAcademica.DEPARTAMENTO+","
				+TipoUnidadeAcademica.ESCOLA
				+","+TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA
			+")");
		sql.append("GROUP BY tu.id_tipo_unidade, tu.descricao ");
		sql.append("ORDER BY tipo_unidade ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();

    	for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			Map<String, Object> item = new HashMap<String, Object>();
			
			item.put("id_tipo_unidade", (Integer) colunas[col++] );
			item.put("descricao", (String) colunas[col++] );
			item.put("total", (Integer) colunas[col++] );

			resultado.add(item);
    	}
		
		return resultado;
	}
	
	/**
	 * Retorna as unidade acadêmica por conforme o tipo informado
	 * @param tipo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Unidade> findUnidadesByTipo(int tipo) throws HibernateException, DAOException{
		
		StringBuffer hql = new StringBuffer();
		
		String projecao = "u.id, u.sigla, u.nome, gestora.id, gestora.nome, gestora.sigla ";
		
		hql.append("select "+projecao);
		
		hql.append(" FROM Unidade u ");
		hql.append(" join u.gestora gestora ");
		
		if (tipo > 0)
			hql.append(" where u.tipoAcademica = "+tipo);
		else {
			hql.append(" where u.tipoAcademica IN ("
					+TipoUnidadeAcademica.DEPARTAMENTO+","
					+TipoUnidadeAcademica.ESCOLA
					+","+TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA
				+") ");			
		}
			
		hql.append("order by gestora.nome, u.nome ");

		List<Unidade> resultado = new ArrayList<Unidade>();
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();

    	for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			Unidade u = new Unidade();
			
			u.setId((Integer) colunas[col++] );
			u.setSigla((String) colunas[col++] );
			u.setNome((String) colunas[col++] );
			
			u.setGestora(new Unidade());
			u.getGestora().setId((Integer) colunas[col++] );
			u.getGestora().setNome((String) colunas[col++] );
			u.getGestora().setSigla((String) colunas[col++] ); 
			
			resultado.add(u);
    	}
		
		return resultado;
	}
	
	

}
