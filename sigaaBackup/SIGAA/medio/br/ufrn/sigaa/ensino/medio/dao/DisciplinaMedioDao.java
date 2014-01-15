/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 15/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;

/**
 * Classe de Dao com consultas sobre as entidade de rela��o entre Turma e disciplinas do ensino m�dio.
 * 
 * @author Rafael Gomes
 *
 */
public class DisciplinaMedioDao extends GenericSigaaDAO{

	/**
	 * Busca uma turma pela chave prim�ria de forma otimizada, utilizando uma proje��o
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public List <Turma> findByPrimaryKeyOtimizado(List <Integer> ids) throws DAOException {
		StringBuffer hql = new StringBuffer();
		String projecao = " t.id, t.disciplina.id, t.disciplina.detalhes.id, t.disciplina.detalhes.nome, t.disciplina.codigo, t.disciplina.detalhes.chTotal, "
				+ " t.disciplina.nivel, t.disciplina.unidade.id, t.ano, t.periodo, t.descricaoHorario, t.local, t.codigo, "
				+ " t.situacaoTurma.id, t.situacaoTurma.descricao, t.dataInicio, " 
				+ " t.dataFim, t.agrupadora, t.turmaAgrupadora.id "; 
		
		hql.append("select "+ projecao 
				+ " from Turma t " 
				+ " left join t.disciplina cc " 
				+ " left join cc.detalhes ccd "
				+ " where t.id in " + UFRNUtils.gerarStringIn(ids));
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Turma> lista = (List<Turma>) HibernateUtils.parseTo(q.list(), projecao, Turma.class, "t");	
		return lista;
	}
	
	/**
	 * Retorna a cole��o de disciplina(TurmaSerieAno) referentes aos identificadores informados por meio de par�metro.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Collection<TurmaSerieAno> findTurmaSerieAnoByIds(List<Integer> ids) throws DAOException {
		Criteria c = getSession().createCriteria(TurmaSerieAno.class);
        c.add( Restrictions.in("id", ids ) );
        
        @SuppressWarnings("unchecked")        
        Collection<TurmaSerieAno> resultado = c.list();
        return resultado;		
	}
}
