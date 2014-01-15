/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/09/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe de Dao com consultas sobre a entidade TurmaSerieAno do ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class TurmaSerieAnoDao extends GenericSigaaDAO{

	/**
	 * Busca as Disciplinas que possuem o discente com a situação de matrícula informados, 
	 * conforme os parâmetros passados: discente, ano e situação da turmas informados.
	 *
	 * @param discente
	 * @param situacaoMatricula
	 * @param ano
	 * @param situacoesTurma
	 * @return
	 * @throws DAOException
	 */
	public List<TurmaSerieAno> findByDiscenteAno(Discente discente, Collection<SituacaoMatricula> situacoesMatriculaDisciplina,
			Collection<SituacaoMatriculaSerie> situacaoMatriculaSerie, int ano, SituacaoTurma... situacoesTurma) throws DAOException {

		try {
			String projecao = "tsa.id as turmaSerieAno.id, " +
					" ts.id as turmaSerieAno.turmaSerie.id," +
					" ts.ano as turmaSerieAno.turmaSerie.ano," +
					" ts.nome as turmaSerieAno.turmaSerie.nome," +
					" s.id as turmaSerieAno.turmaSerie.serie.id," +
					" s.descricao as turmaSerieAno.turmaSerie.serie.descricao," +
					" s.numero as turmaSerieAno.turmaSerie.serie.numero," +
					" mds.dependencia as turmaSerieAno.dependencia, " +
					" md.turma.id as turmaSerieAno.turma.id," +
					" md.turma.disciplina.detalhes.nome as turmaSerieAno.turma.disciplina.detalhes.nome," +
					" md.turma.disciplina.codigo as turmaSerieAno.turma.disciplina.codigo," +
					
					" md.turma.disciplina.tipoComponente.id as turmaSerieAno.turma.disciplina.tipoComponente.id," +
					
					" md.turma.disciplina.detalhes.chTotal as turmaSerieAno.turma.disciplina.detalhes.chTotal, " +
					" md.turma.disciplina.detalhes.crAula as turmaSerieAno.turma.disciplina.detalhes.crAula," +
					" md.turma.disciplina.detalhes.crLaboratorio as turmaSerieAno.turma.disciplina.detalhes.crLaboratorio," +
					" md.turma.disciplina.detalhes.crEstagio as turmaSerieAno.turma.disciplina.detalhes.crEstagio, " +
					
					" md.turma.disciplina.detalhes.chAula as turmaSerieAno.turma.disciplina.detalhes.chAula," +
					
					" md.turma.disciplina.nivel as turmaSerieAno.turma.disciplina.nivel," +
					" md.turma.disciplina.unidade.id as turmaSerieAno.turma.disciplina.unidade.id," +
					" md.turma.descricaoHorario as turmaSerieAno.turma.descricaoHorario, " +
					" md.turma.distancia as turmaSerieAno.turma.distancia," +
					" md.turma.ano as turmaSerieAno.turma.ano," +
					" md.turma.codigo as turmaSerieAno.turma.codigo," +
					" md.turma.local as turmaSerieAno.turma.local," +
					" md.turma.descricaoHorario as turmaSerieAno.turma.descricaoHorario";
			
			StringBuffer hql = new StringBuffer();
			hql.append("select ");
			hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
			hql.append(" from MatriculaComponente md, MatriculaDiscenteSerie mds" +
					   " inner join md.turma t " +
					   " inner join mds.turmaSerie ts " +
					   " inner join ts.serie s " +
					   " inner join mds.discenteMedio.discente d " +
					   " , TurmaSerieAno tsa " +
					   " where tsa.turma.id = t.id and " +
					   " tsa.turmaSerie.id = ts.id and " +
					   " mds.discenteMedio.id = md.discente.id and " +
					   " mds.turmaSerie.id = ts.id and ");
			if (!situacoesMatriculaDisciplina.isEmpty())
				hql.append("md.situacaoMatricula.id in " + gerarStringIn(situacoesMatriculaDisciplina) + " and ");
			if (!situacaoMatriculaSerie.isEmpty())
				hql.append("mds.situacaoMatriculaSerie.id in " + gerarStringIn(situacaoMatriculaSerie) + " and ");
			if (ano > 0)
				hql.append("md.ano = " + ano + " and ");
			
			hql.append("d.id = :discenteId  and md.turma.situacaoTurma.id in " + gerarStringIn(situacoesTurma)
					+ " order by s.numero desc, md.turma.ano, md.turma.disciplina.detalhes.nome asc");
			
			Query q = getSession().createQuery(hql.toString());
			
			q.setInteger("discenteId", discente.getId());
			
			@SuppressWarnings("unchecked")
			Collection<TurmaSerieAno> turmas = HibernateUtils.parseTo(q.list(), projecao, TurmaSerieAno.class, "turmaSerieAno");
			
			for (TurmaSerieAno disciplina : turmas) {
				Query qtd = getSession().createQuery(
						"select count(*) from "
								+ "MatriculaComponente where turma.id = :idTurma and "
								+ "situacaoMatricula.id = :situacao");
				qtd.setInteger("idTurma", disciplina.getTurma().getId());
				qtd.setInteger("situacao", SituacaoMatricula.MATRICULADO.getId());
				disciplina.getTurma().setQtdMatriculados(((Number) qtd.uniqueResult()).intValue());
			}

			return (List<TurmaSerieAno>) turmas;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
}
