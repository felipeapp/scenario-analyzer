/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/05/2010
 *
 */
package br.ufrn.sigaa.arq.dao.avaliacao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.avaliacao.dominio.ComentarioAvaliacaoModerado;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenteTurma;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesTrancamento;

/** Classe responsável por consultas específicas aos comentários moderados da Avaliação Institucional ({@link ComentarioAvaliacaoModerado}).
 * @author Édipo Elder F. Melo
 *
 */
public class ComentarioAvaliacaoModeradoDao extends GenericSigaaDAO {
	
	/**
	 * Busca docentes para os quais os comentários sobre docenteturma serão
	 * moderados, restringindo por um ano/período da avaliação, por
	 * departamento, ou por nome do docente.
	 * 
	 * @param nomeDocente
	 * @param idCentro
	 *            caso o valor seja 0, não será utilizado na busca.
	 * @param idDepartamento
	 *            caso o valor seja 0, não será utilizado na busca.
	 * @param ano
	 * @param periodo
	 * @param comentariosDiscente
	 *            Caso true, busca por comentários realizados por discentes para
	 *            o docenteTurma. Caso false, busca por comentários realizados
	 *            pelo docente da turma para a turma (discentes).
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findDocenteByAnoPeriodoNomeDepartamento(String nomeDocente, int idDepartamento, int ano, int periodo, int idFormulario, boolean comentariosDiscentes) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select distinct dep.id_unidade, dt.id_docente_turma, s.id_servidor,"
				+ " p.nome as nome_docente, dep.nome as departamento,"
				+ " t.id_turma, t.codigo as codigo_turma, ccd.codigo as codigo_componente, ccd.nome as nome_componente, t.ano, t.periodo,"
				+ " case when cam.id_docente_turma is null or cam.id_docente_turma = 0 then false else true end as docente_turma_finalizado,"
				+ " qtd_observacoes.count as qtd_observacoes_docente_turma"
				+ " from rh.servidor s"
				+ " inner join comum.unidade dep using (id_unidade)"
				+ " inner join comum.pessoa p using (id_pessoa)"
				+ " left join ensino.docente_turma dt on (dt.id_docente = s.id_servidor)"
				+ " inner join ensino.turma t using (id_turma)"
				+ " inner join ensino.componente_curricular cc using (id_disciplina)"
				+ " inner join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)"
				+ " left join ensino.docente_externo using (id_servidor)"
				+ " inner join avaliacao.resultado_avaliacao_docente rad using (id_docente_turma)"
				+ (idFormulario > 0 ? " inner join avaliacao.parametro_processamento_avaliacao ppa on (ppa.ano = rad.ano and ppa.periodo = rad.periodo and ppa.id_formulario_avaliacao = rad.id_formulario_avaliacao)":"")
				+ " left join ("
				+ "   select dt.id_docente_turma, count(odt.id)"
				+ "   from ensino.turma t"
				+ "   inner join ensino.docente_turma dt using (id_turma)"  
				+ "   inner join avaliacao.observacoes_docente_turma odt using (id_docente_turma)"
				+ "   inner join avaliacao.avaliacao_institucional ai on (ai.id = odt.id_avaliacao)"  
				+ "   where ai.id_discente is "+(comentariosDiscentes ? "not" : "")+" null"
				+ (ano != 0 && periodo != 0 ? 
				  "   and t.ano = :ano and t.periodo = :periodo" : "")
				+ "   group by dt.id_docente_turma"
				+ " ) qtd_observacoes using (id_docente_turma)"
				+ " left join avaliacao.comentario_avaliacao_moderado cam on (cam.id_docente_turma = dt.id_docente_turma and cam.observacao_discente = :comentariosDiscente)"
				+ " where 1=1");
		if (nomeDocente != null) sql.append(" and p.nome_ascii ilike " + UFRNUtils.toAsciiUTF8(":nomeDocente"));
		if (idDepartamento != 0) sql.append(" and dep.id_unidade = :idDepartamento");
		if (ano != 0 && periodo != 0) sql.append(" and t.ano = :ano and t.periodo = :periodo");
		if (idFormulario != 0 ) sql.append(" and ppa.id_formulario_avaliacao = :idFormulario");
		sql.append(" order by ano desc, periodo desc, dep.nome, p.nome, ccd.nome, t.codigo");
		Query q = getSession().createSQLQuery(sql.toString());
		if (nomeDocente != null) q.setString("nomeDocente", nomeDocente + "%");
		if (idDepartamento != 0) q.setInteger("idDepartamento", idDepartamento);
		if (ano != 0 && periodo != 0){
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}
		if (idFormulario != 0) q.setInteger("idFormulario", idFormulario);
		q.setBoolean("comentariosDiscente", comentariosDiscentes);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		int i;
		for (Object[] rs : lista) {
			i = 0;
			Map<String, Object> mapa = new HashMap<String, Object>();
			mapa.put("id_unidade", rs[i++]);
			mapa.put("id_docente_turma", rs[i++]);
			mapa.put("id_servidor", rs[i++]);
			mapa.put("nome_docente", rs[i++]);
			mapa.put("departamento", rs[i++]);
			mapa.put("id_turma", rs[i++]);
			mapa.put("codigo_turma", rs[i++]);
			mapa.put("codigo_componente", rs[i++]);
			mapa.put("nome_componente", rs[i++]);
			mapa.put("ano", rs[i++]);
			mapa.put("periodo", rs[i++]);
			mapa.put("docente_turma_finalizado", rs[i++]);
			mapa.put("qtd_observacoes_docente_turma", rs[i++]);
			resultado.add(mapa);
		}
		return resultado;
	}
	
	/** Busca turmas para os quais os comentários sobre trancamento serão moderados, restringindo por um ano/período da avaliação, por departamento, ou por nome do componente. 
	 * @param nomeDocente
	 * @param idCentro caso o valor seja 0, não será utilizado na busca.
	 * @param idDepartamento caso o valor seja 0, não será utilizado na busca.
	 * @param ano
	 * @param periodo
	 * @param ordernaPorTurma Caso true, ordena por ano, período, departamento, turma, docente. Caso false, ordena por ano, período, departamento, docente, turma. 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTurmasByAnoPeriodoNomeComponente(String nomeComponente, int idDepartamento, int ano, int periodo, int idFormulario) throws HibernateException, DAOException {
		StringBuilder sql = new StringBuilder("select distinct dep.id_unidade,"
				+ " dep.nome as departamento,"
				+ " t.id_turma, t.codigo as codigo_turma, ccd.codigo as codigo_componente, ccd.nome as nome_componente, t.ano, t.periodo,"
				+ " case when cam.id_turma is null or cam.id_turma = 0 then false else true end as trancamento_finalizado,"
				+ " qtd_trancamentos.count as qtd_observacoes_trancamento"
				+ " from ensino.turma t"
				+ " left join ensino.docente_turma dt using (id_turma)"
				+ " inner join ensino.componente_curricular cc using (id_disciplina)"
				+ " inner join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)"
				+ " inner join comum.unidade dep on (cc.id_unidade = dep.id_unidade)"
				+ " inner join avaliacao.resultado_avaliacao_docente rad using (id_docente_turma)"
				+ (idFormulario > 0 ? " inner join avaliacao.parametro_processamento_avaliacao ppa on (ppa.ano = rad.ano and ppa.periodo = rad.periodo and ppa.id_formulario_avaliacao = rad.id_formulario_avaliacao)":"")
				+ " left join ("
				+ "   select t.id_turma, count(ot.id)"
				+ "   from ensino.turma t"
				+ "   left join avaliacao.observacoes_trancamento ot using (id_turma)"
				+ (ano != 0 && periodo != 0 ? "   where ano = :ano and periodo = :periodo" : "")
				+ "   group by id_turma"
				+ " ) qtd_trancamentos on (t.id_turma = qtd_trancamentos.id_turma)"
				+ " left join avaliacao.comentario_avaliacao_moderado cam on (cam.id_turma = t.id_turma)"
				+ " where 1=1"
				+ (ano != 0 && periodo != 0 ? " and ppa.ano = :ano and ppa.periodo = :periodo" : ""));
		if (nomeComponente != null) sql.append(" and ccd.nome_ascii ilike " + UFRNUtils.toAsciiUTF8(":nomeComponente"));
		if (idDepartamento != 0) sql.append(" and dep.id_unidade = :idDepartamento");
		if (idFormulario != 0 ) sql.append(" and ppa.id_formulario_avaliacao = :idFormulario");
		sql.append(" order by ano desc, periodo desc, dep.nome, ccd.nome, t.codigo");
		Query q = getSession().createSQLQuery(sql.toString());
		if (nomeComponente != null) q.setString("nomeComponente", nomeComponente + "%");
		if (idDepartamento != 0) q.setInteger("idDepartamento", idDepartamento);
		if (ano != 0 && periodo != 0){
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}
		if (idFormulario != 0) q.setInteger("idFormulario", idFormulario);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		int i;
		for (Object[] rs : lista) {
			i = 0;
			Map<String, Object> mapa = new HashMap<String, Object>();
			mapa.put("id_unidade", rs[i++]);
			mapa.put("departamento", rs[i++]);
			mapa.put("id_turma", rs[i++]);
			mapa.put("codigo_turma", rs[i++]);
			mapa.put("codigo_componente", rs[i++]);
			mapa.put("nome_componente", rs[i++]);
			mapa.put("ano", rs[i++]);
			mapa.put("periodo", rs[i++]);
			mapa.put("trancamento_finalizado", rs[i++]);
			mapa.put("qtd_observacoes_trancamento", rs[i++]);
			resultado.add(mapa);
		}
		return resultado;
	}


	/** Retorna uma coleção de observações de uma avaliação de um docenteTurma.
	 * @param idDocenteTurma
	 * @param comentariosDiscentes
	 * @param somenteFinalizada
	 * @return
	 * @throws DAOException
	 */
	public Collection<ObservacoesDocenteTurma> findObservacoesDocenteTurmaByDocenteTurma(int idDocenteTurma, boolean comentariosDiscentes, boolean somenteFinalizada) throws DAOException {
		Criteria c = getSession().createCriteria(ObservacoesDocenteTurma.class);
		c.createCriteria("docenteTurma").add(Restrictions.eq("id", idDocenteTurma));
		Criteria a = c.createCriteria("avaliacao");
		if (comentariosDiscentes)
			a.add(Restrictions.isNotNull("discente"));
		else
			a.add(Restrictions.isNull("discente"));
		if (somenteFinalizada)
			a.add(Restrictions.eq("finalizada", somenteFinalizada));
		c.addOrder(Order.asc("id"));
		@SuppressWarnings("unchecked")
		Collection<ObservacoesDocenteTurma> lista = c.list();
		return lista;
	}

	/** Retorna uma coleção de observações de uma avaliação de uma turma.
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public Collection<ObservacoesTrancamento> findObservacoesTrancamentosByDocenteTurma(int idTurma) throws DAOException {
		Criteria c = getSession().createCriteria(ObservacoesTrancamento.class);
		c.createCriteria("turma").add(Restrictions.eq("id", idTurma));
		c.createCriteria("avaliacao").add(Restrictions.isNotNull("discente")).add(Restrictions.eq("finalizada", true));
		@SuppressWarnings("unchecked")
		Collection<ObservacoesTrancamento> lista = c.list();
		return lista;
	}
	
	/** Retorna uma coleção de observações realizada por docentes às turmas de um discente.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ObservacoesDocenteTurma> findObservacoesModeradasTurmasDiscente(int idDiscente, int ano, int periodo) throws DAOException {
		String hql = "select observacoes" +
				" from ObservacoesDocenteTurma observacoes," +
				" ComentarioAvaliacaoModerado moderacao," +
				" MatriculaComponente matricula" +
				" where matricula.discente.id = :idDiscente" +
				" and observacoes.avaliacao.ano = :ano" +
				" and observacoes.avaliacao.periodo = :periodo" +
				" and moderacao.docenteTurma.id = observacoes.docenteTurma.id" +
				" and moderacao.docenteTurma.turma.id = matricula.turma.id" +
				" and moderacao.observacaoDiscente = falseValue()" +
				" and observacoes.avaliacao.discente is null";
		Query q = getSession().createQuery(hql);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idDiscente", idDiscente);
		@SuppressWarnings("unchecked")
		List<ObservacoesDocenteTurma> lista = q.list();
		if (lista != null) {
			// remove as obervações em branco que foram inseridas.
			Iterator<ObservacoesDocenteTurma> iterator = lista.iterator();
			while (iterator.hasNext()){
				ObservacoesDocenteTurma next = iterator.next();
				if (next.getObservacoesModeradas() == null  || next.getObservacoesModeradas().trim().isEmpty())
					iterator.remove();
			}
			// Orderna a lista de observações por turma e nome do docente.
			Collections.sort(lista, new Comparator<ObservacoesDocenteTurma>() {
				@Override
				public int compare(ObservacoesDocenteTurma o1, ObservacoesDocenteTurma o2) {
					// compara a turma
					int cmp = o1.getTurma().getDescricaoCodigo().compareTo(o2.getTurma().getDescricaoCodigo());
					// compara os docentes
					if (cmp == 0) cmp = o1.getDocenteTurma().getDocenteNome().compareTo(o2.getDocenteTurma().getDocenteNome());
					return cmp;
				}
			});
		}
		return lista;
	}
}
