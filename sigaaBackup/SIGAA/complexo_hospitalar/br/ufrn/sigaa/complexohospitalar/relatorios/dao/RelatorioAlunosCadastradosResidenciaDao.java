/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 18/04/2011
 *
 */
package br.ufrn.sigaa.complexohospitalar.relatorios.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.complexohospitalar.relatorios.dominio.RelatorioAlunosCadastradosResidencia;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe que contém as consultas para geração do relatório de alunos cadastrados nos 
 * programas de residência médica.
 * 
 * @author arlindo
 *
 */
public class RelatorioAlunosCadastradosResidenciaDao extends GenericSigaaDAO {
	
	/**
	 * Retorna o total de alunos cadastrados agrupados por programa de residência médica
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<RelatorioAlunosCadastradosResidencia> findAlunosCadastrados() throws HibernateException, DAOException {
		
		StringBuffer hql = new StringBuffer(); 
			
		hql.append("select d.gestoraAcademica.id, d.gestoraAcademica.nome, cast( count(d.id) as int) from Discente d ");
		hql.append(" inner join d.gestoraAcademica g "); 
		hql.append(" where d.nivel = '"+NivelEnsino.RESIDENCIA+"'");
		hql.append(" and d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusComVinculo()));
		hql.append(" group by d.gestoraAcademica.id, d.gestoraAcademica.nome ");
		hql.append(" order by g.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		
		List<RelatorioAlunosCadastradosResidencia> resultado = new ArrayList<RelatorioAlunosCadastradosResidencia>();
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();
    	for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			RelatorioAlunosCadastradosResidencia item = new RelatorioAlunosCadastradosResidencia();
			
			Unidade u = new Unidade((Integer) colunas[col++]);
			u.setNome((String) colunas[col++]);
			
			item.setUnidade(u);
			item.setTotal((Integer) colunas[col++]);
			
			resultado.add(item);
    	}
    	
    	return resultado;
	}
	
	/**
	 * Busca os discente do programa de residência informado
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Discente> findDiscentesByPrograma(int idUnidade) throws HibernateException, DAOException{
		
		String projecao = " d.matricula, d.pessoa.nome, d.status, d.anoIngresso, " +
				" d.periodoIngresso, d.gestoraAcademica.id, d.gestoraAcademica.nome ";
		
		StringBuffer hql = new StringBuffer();
		
		hql.append("select "+projecao+" from Discente d ");
		hql.append("inner join d.pessoa p ");
		hql.append("inner join d.gestoraAcademica g ");
		hql.append(" where d.nivel = '"+NivelEnsino.RESIDENCIA+"'");
		hql.append(" and d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusComVinculo()));
		
		if (idUnidade > 0)
			hql.append(" and d.gestoraAcademica.id = :idUnidade ");

		hql.append(" order by d.gestoraAcademica.nome, d.pessoa.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		
		if (idUnidade > 0)
			q.setInteger("idUnidade", idUnidade);
		
		@SuppressWarnings("unchecked")
		List<Discente> lista = (List<Discente>) HibernateUtils.parseTo( q.list(), projecao, Discente.class, "d");
		return lista;
	}
	
	

}
