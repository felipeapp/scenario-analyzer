/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/07/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;

/**
 * Classe de Dao com consultas sobre referentes a transferência de alunos entre turmas.
 * @author Rafael Gomes
 *
 */
public class TransferenciaTurmaMedioDao extends GenericSigaaDAO{

	/**
	 * Retornar um coleção de TurmaSerie, que sejam compatíveis às series de destino na transferência de turmas.
	 * 
	 * @param turmaSerieOrigem
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<TurmaSerie>findDestinosTransferencia(TurmaSerie turmaSerieOrigem) throws HibernateException, DAOException{
		
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		situacoes.addAll(SituacaoMatricula.getSituacoesAproveitadas());
		
		StringBuffer hql = new StringBuffer();
		String projecao = " ts.id, ts.ano, ts.nome, ts.serie.numero, " +
		" ts.serie.descricao, ts.serie.cursoMedio.id, ts.serie.cursoMedio.nome," +
		" ts.serie.cursoMedio.codigo, ts.turno.sigla "; 
		Serie serie = turmaSerieOrigem.getSerie();
		hql.append(
				"select "+projecao+
				" from TurmaSerie as ts " +
				" inner join ts.serie as s " +
				" inner join ts.serie.cursoMedio as c " +
				" left join ts.turno as t " +
				" where ts.ativo = "+ SQLDialect.TRUE + 
				" and ts.id != "+turmaSerieOrigem.getId() );		
		
		if (turmaSerieOrigem.getAno() != null && turmaSerieOrigem.getAno() > 0)
			hql.append(" and ts.ano = "+turmaSerieOrigem.getAno());
		
		if (serie.getNumero() != null && serie.getNumero() > 0)
			hql.append(" and s.numero = "+serie.getNumero());
		
		if (isNotEmpty(serie.getDescricao()))
			hql.append(" and s.descricao like '%"+serie.getDescricao()+"%'");
		
		if (isNotEmpty(serie.getId()))
			hql.append(" and s.id = "+serie.getId());
		
		hql.append(" order by s.numero, ts.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<TurmaSerie> lista = (List<TurmaSerie>) HibernateUtils.parseTo(q.list(), projecao, TurmaSerie.class, "ts");		
		
		TurmaSerieDao tsDao = new TurmaSerieDao();
		try {
			Map<Integer, Long> mapAlunosTurma = tsDao.findQtdeAlunosByTurmas(lista);
			for (TurmaSerie turma : lista) {
				if ( mapAlunosTurma.get(turma.getId()) != null )
					turma.setQtdMatriculados(mapAlunosTurma.get(turma.getId()));
			}
			return lista;	
		} finally {
			tsDao.close();
		}
	}
	
}
