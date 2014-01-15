/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/05/2010
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoReposicaoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.StatusReposicaoAvaliacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável por gerenciar o acesso a dados de Solicitações de Reposição de Prova.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class SolicitacaoReposicaoAvaliacaoDao extends GenericSigaaDAO  {
	
	/** Projeção utiliza nas consultas */
	private String projecao = "rp.id, rp.dataCadastro, rp.turma.id, rp.turma.disciplina.detalhes.nome, " +
	" rp.turma.disciplina.codigo, rp.turma.codigo, rp.turma.ano , rp.turma.periodo , rp.idArquivo, rp.turma.disciplina.unidade.id, " +
	" rp.dataAvaliacao.id, rp.dataAvaliacao.descricao, rp.dataAvaliacao.data," +
	" rp.discente.discente.matricula, rp.discente.discente.pessoa.nome, rp.discente.discente.curso.municipio.nome, " +
	" rp.discente.discente.curso.nome, rp.status.id, rp.status.descricao, rp.statusParecer.id, rp.statusParecer.descricao, " +
	" rp.ativo, rp.discente.discente.nivel, rp.justificativa, rp.dataProvaSugerida, rp.localProva, rp.observacaoDocente, rp.observacaoHomologacao ";	
	
	/**
	 * Retorna todos os registros de Solicitação de Reposição de Prova de um discente passado como parâmetro.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<SolicitacaoReposicaoAvaliacao> findByDiscente(DiscenteAdapter discente, DataAvaliacao avaliacao, Boolean somenteAtivos) throws DAOException{
		
		String hql = "select distinct "+projecao+" from SolicitacaoReposicaoAvaliacao rp " +
		" inner join rp.dataAvaliacao "+
		" inner join rp.discente.discente "+
		" inner join rp.discente.discente.pessoa " +
		" inner join rp.discente.discente.curso "+
		" inner join rp.discente.discente.curso.municipio "+
		" inner join rp.turma "+
		" inner join rp.turma.disciplina "+
		" inner join rp.turma.disciplina.detalhes "+
		" inner join rp.status "+
		" left join rp.statusParecer "+
		" where rp.discente.id = ? ";
		
		if (avaliacao != null && avaliacao.getId() > 0)
			hql += " and rp.dataAvaliacao.ativo = true and rp.dataAvaliacao.id = "+avaliacao.getId();
		
		if (somenteAtivos != null){
			hql += " and rp.ativo = "+(somenteAtivos?" trueValue() ":" falseValue()");
			if (somenteAtivos){
				hql += " and rp.status.id <> "+StatusReposicaoAvaliacao.CANCELADA;
			}
		}					
		
		hql += " order by rp.turma.disciplina.detalhes.nome, rp.status.descricao, rp.dataCadastro desc";
		
		Query q = getSession().createQuery(hql);
		q.setInteger(0, discente.getId());		
		
		@SuppressWarnings("unchecked")
		List<SolicitacaoReposicaoAvaliacao> lista = (List<SolicitacaoReposicaoAvaliacao>) HibernateUtils.parseTo(q.list(), 
				projecao, SolicitacaoReposicaoAvaliacao.class, "rp"); 		
		return lista;
	}
	
	/**
	 * Retorna todos os registros de Solicitação de Reposição de Prova de um docente ou unidade passados como parâmetro.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<SolicitacaoReposicaoAvaliacao> findAllByDocente(Servidor servidor, int idUnidade, int idTurma, Integer idAvaliacao, Integer ano , Integer periodo , Integer status , boolean parecerDocente , boolean checarPrazo) throws DAOException{
			
		String hql = "select distinct "+projecao+" from SolicitacaoReposicaoAvaliacao rp "+
		" inner join rp.dataAvaliacao da "+
		" inner join rp.discente.discente "+
		" inner join rp.discente.discente.pessoa " +
		" inner join rp.discente.discente.curso "+
		" inner join rp.discente.discente.curso.municipio "+
		" inner join rp.turma "+
		" inner join rp.turma.disciplina "+
		" inner join rp.turma.disciplina.detalhes "+
		" inner join rp.turma.docentesTurmas dt "+
		" inner join rp.turma.disciplina.unidade u "+
		" inner join rp.status "+
		" left join rp.statusParecer "+		
		" where rp.ativo = trueValue() "+
		" and rp.status.id not in ("+StatusReposicaoAvaliacao.CANCELADA+")";
		
		
		Date data = null;
		
		if (checarPrazo) {
			// O prazo para o chefe deferir ou indeferir é aquele de 3 dias úteis a partir do momento em que o finalizar o tempo do aluno fazer a solicitação
			int dias = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QUANTIDADE_MAXIMA_DIAS_REPOSICAO_PROVA);
			dias += ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QUANTIDADE_MAXIMA_DIAS_HOMOLOGACAO_REPOSICAO_PROVA);
			data = CalendarUtils.subtrairDiasUteis(new Date(), dias);
			hql +=" and da.data >= :data ";
		}
		
		if (servidor != null)
			hql +=" and dt.docente.id = "+servidor.getId();
		
		if (idUnidade > 0)
			hql +=" and u.id = "+ idUnidade + " ";
		
		if (idTurma > 0)
			hql +=" and rp.turma.id = "+idTurma;
		
		if (idAvaliacao != null && idAvaliacao > 0)
			hql +=" and da.id = "+idAvaliacao;

		if (ano != null && ano > 0)
			hql +=" and rp.turma.ano = "+ano;
		
		if (periodo != null && periodo > 0)
			hql +=" and rp.turma.periodo = "+periodo;
		
		if (parecerDocente)
			hql +=" and rp.statusParecer is not null ";
		
		if (status != null && status > 0)
			hql +=" and rp.status.id = "+status;
		
		if (servidor == null)
			hql +="  and rp.homologado = falseValue() ";
		
		if (servidor != null)
			hql +=" order by rp.turma.ano desc, rp.turma.periodo desc , rp.turma.disciplina.codigo, rp.turma.disciplina.detalhes.nome , rp.turma.codigo asc ";
		else
			hql +=" order by rp.turma.disciplina.codigo, rp.turma.disciplina.detalhes.nome , rp.turma.codigo asc";
		
		Query q = getSession().createQuery(hql);
		
		if (checarPrazo)
			q.setDate("data", CalendarUtils.descartarHoras( data ));
		
		@SuppressWarnings("unchecked")
		List<SolicitacaoReposicaoAvaliacao> lista = (List<SolicitacaoReposicaoAvaliacao>) HibernateUtils.parseTo(q.list(), 
				projecao, SolicitacaoReposicaoAvaliacao.class, "rp"); 		
		return lista;
	}	
	
	/**
	 * Retorna os Status que permitem realizar parecer
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<StatusReposicaoAvaliacao> findAllStatusParecer() throws HibernateException, DAOException{
		String projecao = "id, descricao ";
		String hql = "select "+projecao+" from StatusReposicaoAvaliacao s where s.permiteParecer = trueValue() ";
		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<StatusReposicaoAvaliacao> lista = (List<StatusReposicaoAvaliacao>) HibernateUtils.parseTo(q.list(), 
				projecao, StatusReposicaoAvaliacao.class, "s"); 		
		return lista;
	}		
	
	/**
	 * Busca as Turmas com avaliações do discente, ano e período informados
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTurmasAvaliacoes(int idDiscente, int ano, int periodo) throws HibernateException, DAOException{
		
		StringBuffer sql = new StringBuffer(); 
		
		int dias = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QUANTIDADE_MAXIMA_DIAS_REPOSICAO_PROVA);
				
		Date data = CalendarUtils.subtrairDiasUteis(new Date(), dias);
		
		String projecao = "select distinct t.id_turma, cc.id_disciplina, cc.codigo, cd.nome, " +
				" t.codigo as codturma, da.id_avaliacao_data, da.data, da.hora, da.descricao ";
			
		sql.append(" from ensino.matricula_componente mc ");
		sql.append(" inner join ensino.turma t on t.id_turma = mc.id_turma ");
		sql.append(" inner join ensino.componente_curricular cc on cc.id_disciplina = t.id_disciplina ");
		sql.append(" inner join ensino.componente_curricular_detalhes cd on cd.id_componente_detalhes = cc.id_detalhe ");
		sql.append(" left join ensino.turma ta on ta.id_turma = t.id_turma_agrupadora ");
		sql.append(" inner join ava.avaliacao_data da on da.id_turma = coalesce(ta.id_turma,t.id_turma) "); 
		sql.append(" where cc.nivel = '"+NivelEnsino.GRADUACAO+"'");
		sql.append(" and t.id_situacao_turma = "+SituacaoTurma.ABERTA);
		sql.append(" and mc.ano = "+ano);
		sql.append(" and mc.periodo = "+periodo);
		sql.append(" and mc.id_situacao_matricula = "+SituacaoMatricula.MATRICULADO.getId());
		sql.append(" and mc.id_discente = "+idDiscente);
		sql.append(" and da.data >= :data and da.data < :hoje ");
		
		StringBuffer sqlDocentes = new StringBuffer(); 
	
		sqlDocentes.append(" select distinct dt.id_turma, p.nome ");
		sqlDocentes.append(" from ensino.docente_turma dt ");
		sqlDocentes.append(" left join rh.servidor s on s.id_servidor = dt.id_docente ");
		sqlDocentes.append(" left join ensino.docente_externo de on de.id_docente_externo = dt.id_docente_externo ");
		sqlDocentes.append(" left join comum.pessoa p on p.id_pessoa = coalesce(s.id_pessoa, de.id_pessoa) ");
		sqlDocentes.append(" where dt.id_turma in ( ");		
		
		sqlDocentes.append("select distinct t.id_turma ");
		sqlDocentes.append(sql.toString());
		
		sqlDocentes.append(" ) ");		
		
		sql.insert(0, projecao);
		sql.append(" order by cd.nome, da.data, da.hora ");
		
		SQLQuery q = getSession().createSQLQuery(sql.toString());
		q.setDate("data", data);
		q.setDate("hoje", CalendarUtils.descartarHoras(new Date()));
		q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listaTurmas = q.list();
		
		q = getSession().createSQLQuery(sqlDocentes.toString());
		q.setDate("data", data);
		q.setDate("hoje", CalendarUtils.descartarHoras(new Date()));
		q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listaDocentes = q.list();
				
		Integer idTurma = 0;
		for (Map<String, Object> obj : listaTurmas){
			
			idTurma = (Integer) obj.get("id_turma");
			String docentes = null;
			for (Map<String, Object> docente : listaDocentes){
				
				Integer idTurmaDocente = (Integer) docente.get("id_turma");
				
				if (idTurma.equals(idTurmaDocente)){		
					if (!ValidatorUtil.isEmpty(docentes))
						docentes += ",";
						
					docentes = (String) docente.get("nome");					
				}
			}	
			
			obj.put("docentes", docentes);
			
		}
		
		return listaTurmas;
		
	}
}
