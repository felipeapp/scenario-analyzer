/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 03/06/2011
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.projetos.dominio.ModalidadeBolsaExterna;

/**
 * DAO para consultas relacionadas ao Relatório de discentes atendidos de Docência Assistida.
 * 
 * @author Arlindo
 *
 */
public class RelatorioDiscentesAtendidosDocenciaAssistidaDao extends GenericSigaaDAO {
	
	/**
	 * Retorna o quantitativo de discentes da graduação atendidos pela docência assistida agrupados por componente curricular
	 * @param ano
	 * @param periodo
	 * @param programa
	 * @param nivel
	 * @param modalidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoDiscentes(int ano, int periodo, Unidade programa, Character nivel, 
			ModalidadeBolsaExterna modalidade) throws HibernateException, DAOException{
		
		List<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>();
		situacoes.addAll(SituacaoMatricula.getSituacoesAtivas());
		situacoes.addAll(SituacaoMatricula.getSituacoesPagas());
		situacoes.addAll(SituacaoMatricula.getSituacoesReprovadas());
		situacoes.add(SituacaoMatricula.TRANCADO);
		
		/* Apenas as Situações Aprovadas */
		List<Integer> statusAprovados = new ArrayList<Integer>(0);
		statusAprovados.add(PlanoDocenciaAssistida.APROVADO);
		statusAprovados.add(PlanoDocenciaAssistida.ANALISE_RELATORIO);
		statusAprovados.add(PlanoDocenciaAssistida.CONCLUIDO);
		statusAprovados.add(PlanoDocenciaAssistida.SOLICITADO_ALTERACAO_RELATORIO);		

		String sql = "select distinct u.id_unidade as idunidade, u.nome as departamento, cc.codigo as codigo, " +
				" ccd.nome as componente, count(distinct d.id_discente) as total " +				
		" from ensino.matricula_componente mc "+ 
		" inner join ensino.componente_curricular cc on cc.id_disciplina = mc.id_componente_curricular "+ 
		" inner join ( "+ 
		" 		select distinct id_componente_curricular, ano, periodo "+ 
		" 		from stricto_sensu.plano_docencia_assistida p "+ 
		" 		inner join discente d using (id_discente) "+ 
		" 		where p.ativo = "+SQLDialect.TRUE+" and p.status in "+UFRNUtils.gerarStringIn(statusAprovados)+
		"        and ano = "+ano+" and periodo = "+periodo+
				((programa != null && programa.getId() > 0) ? " and d.id_gestora_academica = "+programa.getId() : "")+
				((!ValidatorUtil.isEmpty(nivel) && nivel != '0') ? " and d.nivel = '"+nivel+"'" : "")+
				((modalidade != null && modalidade.getId() > 0) ? " and id_modalidade_bolsa_externa = "+modalidade.getId() : "")+	
		" ) pd on pd.id_componente_curricular = cc.id_disciplina "+ 			
		" inner join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes "+  
		" inner join comum.unidade u on u.id_unidade = cc.id_unidade "+
		" inner join public.discente d on d.id_discente = mc.id_discente "+
		" where mc.ano = pd.ano and mc.periodo = pd.periodo "+		
		" and mc.id_situacao_matricula in "+UFRNUtils.gerarStringIn(situacoes)+
		" and cc.nivel = '"+NivelEnsino.GRADUACAO+"'";
		
		sql += " group by cc.id_disciplina, cc.codigo, ccd.nome, u.id_unidade, u.nome"+ 
			 " order by u.nome, ccd.nome ";
		
		Query q = getSession().createSQLQuery(sql.toString());		
   		
   		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();   		
   		return lista;
		
	}
	
	/**
	 * Retorna o quantitativo de discentes da graduação atendidos pela docência assistida agrupados por componente curricular
	 *   e por índice de aprovação
	 * @param ano
	 * @param periodo
	 * @param programa
	 * @param nivel
	 * @param modalidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoComponente(int ano, int periodo, Unidade programa, Character nivel, 
			ModalidadeBolsaExterna modalidade) throws HibernateException, DAOException{
		
		List<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>();
		situacoes.addAll(SituacaoMatricula.getSituacoesAtivas());
		situacoes.addAll(SituacaoMatricula.getSituacoesPagas());
		situacoes.addAll(SituacaoMatricula.getSituacoesReprovadas());
		situacoes.add(SituacaoMatricula.TRANCADO);
		
		/* Apenas as Situações Aprovadas */
		List<Integer> statusAprovados = new ArrayList<Integer>(0);
		statusAprovados.add(PlanoDocenciaAssistida.APROVADO);
		statusAprovados.add(PlanoDocenciaAssistida.ANALISE_RELATORIO);
		statusAprovados.add(PlanoDocenciaAssistida.CONCLUIDO);
		statusAprovados.add(PlanoDocenciaAssistida.SOLICITADO_ALTERACAO_RELATORIO);		

		String sql = "select distinct cc.id_disciplina, cc.codigo, ccd.nome as disciplina, u.id_unidade, u.nome as departamento, "+
			"      sum(case when mc.id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " then 1 else 0 end) as ativos, "+
			"      sum(case when mc.id_situacao_matricula in (" + SituacaoMatricula.APROVADO.getId() + ") then 1 else 0 end) as aprovados, "+
			"      sum(case when mc.id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesReprovadas() ) + " then 1 else 0 end) as reprovados, "+
			"      sum(case when mc.id_situacao_matricula in (" + SituacaoMatricula.TRANCADO.getId() + ") then 1 else 0 end ) as trancados " +				
		" from ensino.matricula_componente mc "+ 
		" inner join ensino.componente_curricular cc on cc.id_disciplina = mc.id_componente_curricular "+ 
		" inner join ( "+ 
		" 		select distinct id_componente_curricular, ano, periodo "+ 
		" 		from stricto_sensu.plano_docencia_assistida p "+ 
		" 		inner join discente d using (id_discente) "+ 
		" 		where p.ativo = "+SQLDialect.TRUE+" and p.status in "+UFRNUtils.gerarStringIn(statusAprovados)+
		"        and ano = "+ano+" and periodo = "+periodo+
				((programa != null && programa.getId() > 0) ? " and d.id_gestora_academica = "+programa.getId() : "")+
				((!ValidatorUtil.isEmpty(nivel) && nivel != '0') ? " and d.nivel = '"+nivel+"'" : "")+
				((modalidade != null && modalidade.getId() > 0) ? " and id_modalidade_bolsa_externa = "+modalidade.getId() : "")+	
		" ) pd on pd.id_componente_curricular = cc.id_disciplina "+ 			
		" inner join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes "+  
		" inner join comum.unidade u on u.id_unidade = cc.id_unidade "+
		" where mc.ano = pd.ano and mc.periodo = pd.periodo "+		
		" and mc.id_situacao_matricula in "+UFRNUtils.gerarStringIn(situacoes)+
		" and cc.nivel = '"+NivelEnsino.GRADUACAO+"'";
		
		sql += " group by cc.id_disciplina, cc.codigo, ccd.nome, u.id_unidade, u.nome"+ 
			 " order by u.nome, ccd.nome ";
		
		Query q = getSession().createSQLQuery(sql.toString());
		
   		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();   		
   		return lista;
		
	}	
	
	/**
	 * Retorna o quantitativo de planos de docência assistida por status
	 * @param ano
	 * @param periodo
	 * @param programa
	 * @param nivel
	 * @param modalidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoStatus(int ano, int periodo, Unidade programa, Character nivel, 
			ModalidadeBolsaExterna modalidade) throws HibernateException, DAOException{
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" select p.status as status, count(p.id) as total from PlanoDocenciaAssistida p ");
		hql.append(" INNER JOIN p.discente.discente as d ");
		hql.append(" LEFT JOIN p.modalidadeBolsa as mb ");
		hql.append(" INNER JOIN d.gestoraAcademica as ga ");		
		
		hql.append(" where p.ano = :ano ");
		hql.append(" and p.periodo = :periodo ");
		
		if (programa != null && programa.getId() > 0)
			hql.append(" and ga.id = "+programa.getId());
		
		if (!ValidatorUtil.isEmpty(nivel) && nivel != '0')
			hql.append(" and d.nivel = '"+nivel+"'");
		
		if (modalidade != null && modalidade.getId() > 0)
			hql.append(" and mb.id = "+modalidade.getId());		
		
		hql.append(" group by p.status ");
		hql.append(" order by count(p.id) desc ");
		
		Query q = getSession().createQuery(hql.toString());
		
   		q.setInteger("ano", ano);    
   		q.setInteger("periodo", periodo);
   		
   		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();   		
   		
		for ( Map<String,Object> item : lista){
			Integer status = (Integer) item.get("status");
			String descricao = "";
			if (status != null && status > 0)
				descricao = PlanoDocenciaAssistida.getDescricaoStatus(status);
			else 
				descricao = "NÃO DEFINIDO";
			item.put("status", descricao);
		}
   		
   		return lista;
	}		
	
}
