/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '21/12/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Dao que processa as consultas de Relatórios de planejamento de extensão.
 * 
 * @author Jean Guerethes
 *
 */
public class RelatorioPlanejamentoDao extends AbstractRelatorioSqlDao{

    /**
     * Lista dos membros cadastrados nas equipes de ações de extensão.
     * 
     * @param dataFim 
     * @param dataInicio 
     * @return
     * @throws DAOException
     * @throws SQLException 
     * @throws HibernateException 
     */
    @SuppressWarnings("unchecked")
    public List<HashMap<String,Object>> findDetalhesEquipeExtensaoByCategoriaPeriodoAcao(String categoriaMembroEquipe, Date dataInicio, Date dataFim, Integer idSituacaoAcao, Integer idTipoAcao) throws DAOException, HibernateException, SQLException {
	List result = new ArrayList<HashMap<String,Object>>();
	if (dataInicio != null && dataFim != null) {
        	StringBuilder sqlconsulta = new StringBuilder(
        		" select " +
        		
        		" ta.descricao as tipo_acao, " +
        		" cat.descricao as categoria, " +
        		" fun.descricao as funcao, " +
        		" count(distinct pes.id_pessoa) as total "+        		
        		" from projetos.membro_projeto mp "+
        		" join projetos.categoria_membro cat using(id_categoria_membro)"+
        		" join projetos.funcao_membro fun using(id_funcao_membro)"+
        		" join comum.pessoa pes using(id_pessoa)"+
        		" join extensao.atividade a using(id_projeto) "+
        		" join extensao.tipo_atividade_extensao ta using (id_tipo_atividade_extensao)" +
        		" join projetos.projeto p using(id_projeto)"+
        		" join projetos.tipo_situacao_projeto tsp on p.id_tipo_situacao_projeto = tsp.id_tipo_situacao_projeto "+        		
        		" where mp.ativo = trueValue() and p.ativo = trueValue() ");
        
        	    sqlconsulta.append(" and ((p.data_inicio >= " + "'" + dataInicio + "' and p.data_inicio <= " + "'" + dataFim + "'" + ") " +
        	    				" or (p.data_fim >= " + "'" + dataInicio + "' and p.data_fim <= " + "'" + dataFim + "'" + ")" +
        	    				" or (p.data_inicio <= " + "'" + dataInicio + "' and p.data_fim >= " + "'" + dataFim + "'" + "))");
        	    
        	    sqlconsulta.append(" and cat.descricao = '"+categoriaMembroEquipe+"'");
                    sqlconsulta.append(" and p.id_tipo_situacao_projeto = " + idSituacaoAcao);        	    
        	    if (idTipoAcao != null && idTipoAcao > 0) {
        		sqlconsulta.append(" and ta.id_tipo_atividade_extensao = " + idTipoAcao);
        	    }
        
        	sqlconsulta.append(" group by ta.descricao, cat.descricao, fun.descricao");
        	sqlconsulta.append(" order by ta.descricao, cat.descricao, fun.descricao");
        
        	result = executeSql(sqlconsulta.toString());
	}
	return result;
    }

    /**
     * Lista o total de discentes ativos que possuem planos de 
     * trabalho cadastrados.
     * 
     * 
     * @param gestora
     * @return
     * @throws DAOException
     * @throws SQLException 
     * @throws HibernateException 
     */
    @SuppressWarnings("unchecked")
    public List<HashMap<String,Object>> findDiscentesComPlanoTrabalhoByPeriodoAcao(Date dataInicio, Date dataFim, Integer idSituacaoAcao, Integer idTipoAcao) throws DAOException, HibernateException, SQLException {
	List result = new ArrayList<HashMap<String,Object>>();

	if (dataInicio != null && dataFim != null) {
        	StringBuilder sqlconsulta = new StringBuilder(
        		" select " +
        		" ta.descricao as tipo_acao, " +
        		" tvd.descricao as vinculo_discente, " +
        		" tsd.descricao as situacao_discente, " +
        		" count(distinct pes.id_pessoa) as total"+        		
        		" from extensao.discente_extensao de "+
        		" join discente d using(id_discente) " +
        		" join comum.pessoa pes using(id_pessoa) " +
        		" join extensao.tipo_situacao_discente_extensao tsd on tsd.id_tipo_situacao_discente_extensao = de.id_situacao_discente_extensao"+
        		" join extensao.atividade a using(id_atividade)"+
        		" join projetos.projeto p using(id_projeto)"+
        		" join projetos.tipo_situacao_projeto tsp on p.id_tipo_situacao_projeto = tsp.id_tipo_situacao_projeto"+
        		" join extensao.tipo_atividade_extensao ta using(id_tipo_atividade_extensao)"+
        		" join extensao.plano_trabalho_extensao pl using(id_plano_trabalho_extensao)"+
        		" inner join projetos.tipo_vinculo_discente tvd on(tvd.id_tipo_vinculo_discente = pl.id_tipo_vinculo_discente) " +
        		" where p.ativo = trueValue() and de.ativo = trueValue() ");        
        		sqlconsulta.append(" and ((p.data_inicio >= " + "'" + dataInicio + "' and p.data_inicio <= " + "'" + dataFim + "'" + ") " +
        				" or (p.data_fim >= " + "'" + dataInicio + "' and p.data_fim <= " + "'" + dataFim + "'" + ")"+
        				" or (p.data_inicio <= " + "'" + dataInicio + "' and p.data_fim >= " + "'" + dataFim + "'" + "))");
        	    sqlconsulta.append("  and p.id_tipo_situacao_projeto = " + idSituacaoAcao);        	    
        	    if (idTipoAcao != null && idTipoAcao > 0) {
        		sqlconsulta.append(" and ta.id_tipo_atividade_extensao = " + idTipoAcao);
        	    }
        
        	sqlconsulta.append(" group by ta.descricao, tvd.descricao, tsd.descricao");		
        	sqlconsulta.append(" order by ta.descricao, tvd.descricao, tsd.descricao");
        
        
        	result = executeSql(sqlconsulta.toString());
	}
	return result;
    }

    /**
     * Lista todos os discentes de extensão
     * cadastrados regularmente.
     * 
     * Desconsidera os cadastrados como membros da equipe do projeto.
     * 
     * @param dataInicio
     * @param dataFim
     * @param idSituacaoAcao
     * @return
     * @throws DAOException
     */
    public Map<String, Integer> findDiscentesExtensaoByPeriodoAcaoSituacaoAcao(Date dataInicio, Date dataFim, Integer idSituacaoAcao, Integer idTipoAcao) throws DAOException {

	StringBuilder sqlconsulta = new StringBuilder(
		"select count(distinct pes.id_pessoa) " +
		" from extensao.discente_extensao de " +
		" join extensao.tipo_situacao_discente_extensao tsde on tsde.id_tipo_situacao_discente_extensao = de.id_situacao_discente_extensao"+
		" join discente d using(id_discente) " +
		" join comum.pessoa pes using(id_pessoa) " +
		" join extensao.atividade a using(id_atividade) " +
		" join projetos.projeto p using(id_projeto) " +
		" join extensao.tipo_atividade_extensao ta using(id_tipo_atividade_extensao)" +
		" where de.ativo = trueValue() and p.ativo = trueValue() ");		
		sqlconsulta.append(" and ((p.data_inicio >= ? and p.data_inicio <= ?) or (p.data_fim >= ? and p.data_fim <= ?) or (p.data_inicio <= ? and p.data_fim >= ?))");
		sqlconsulta.append(" and p.id_tipo_situacao_projeto = ? "); 
	
	    if (idTipoAcao != null && idTipoAcao > 0) {
		sqlconsulta.append(" and a.id_tipo_atividade_extensao = ?");
	    }

	String[] titulos = {
		"DISCENTES DO ENSINO TÉCNICO",
		"DISCENTES DE GRADUAÇÃO",
		"DISCENTES DE PÓS-GRADUAÇÃO"};

	String[] appends = {
		" and d.nivel in ('T') ",
		" and d.nivel in ('G') ",
		" and d.nivel in ('S', 'E', 'D', 'L') "};
	
	Map<String, Integer> result = new HashMap<String, Integer>();
	JdbcTemplate template = getJdbcTemplate();
	for (int i = 0; i < appends.length; i++) {
	    if (idTipoAcao != null && idTipoAcao > 0) {
		result.put(titulos[i], template.queryForInt(sqlconsulta.toString() + appends[i], new Object[]{dataInicio, dataFim, dataInicio, dataFim, dataInicio, dataFim, idSituacaoAcao, idTipoAcao}));
	    }else {
		result.put(titulos[i], template.queryForInt(sqlconsulta.toString() + appends[i], new Object[]{dataInicio, dataFim,dataInicio, dataFim, dataFim, dataInicio, idSituacaoAcao}));
	    }
	}
	return result;	
    }
    
    /**
     * Retorna todos os servidores que possuem o vínculo de discente de pós-graduação.
     * @return
     * @throws DAOException
     */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findDesempenhoServidoresDiscentesPos() throws DAOException {

		StringBuffer hql = new StringBuffer(" SELECT d.gestoraAcademica.nome, c.nome, c.nivel, s.siape, p.nome, cat.descricao,");
		hql.append(" us.nome, ROUND(i.valor,2), d.id, ");
		hql.append(" CASE WHEN d.tipo = " + Discente.REGULAR + " THEN 'R' ELSE 'E' END ");
		hql.append(" FROM DiscenteStricto ds INNER JOIN ds.discente d ");
		hql.append(" INNER JOIN d.pessoa p LEFT JOIN d.curso c LEFT JOIN c.unidade u , ");
		hql.append(" Servidor s INNER JOIN s.unidade us INNER JOIN s.categoria cat , ");
		hql.append(" IndiceAcademicoDiscente i ");
		hql.append(" WHERE s.pessoa.id = p.id AND i.discente.id = d.id ");
		hql.append(" AND  d.status IN " + UFRNUtils.gerarStringIn( StatusDiscente.getAtivos() ) );
		hql.append(" AND s.ativo.id = " + Ativo.SERVIDOR_ATIVO + " AND d.gestoraAcademica.ativo = true" );
		hql.append(" ORDER BY d.gestoraAcademica.nome,u.nome, c.nome, c.nivel, i.valor DESC ");

		Query q = getSession().createQuery(hql.toString());
		
		return q.list();
		
	}
 
    
    /**
     * Lista com os totais de discentes envolvidos em 
     * ações de extensão com seus respectivos níveis de ensino.
     * 
     * @param dataInicio
     * @param dataFim
     * @param idSituacaoAcao
     * @param idTipoAcao TODO
     * @return
     * @throws DAOException
     * @throws HibernateException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public List<HashMap<String,Object>> findDiscentesExtensaoByPeriodoAcaoSituacaoTipoAcao(Date dataInicio, Date dataFim, Integer idSituacaoAcao, Integer idTipoAcao) throws DAOException, HibernateException, SQLException {
	List result = new ArrayList<HashMap<String,Object>>();
	if (dataInicio != null && dataFim != null) {

	    String dataInicioSql = "'"+CalendarUtils.format(dataInicio, "dd/MM/yyyy")+"'";  
	    String dataFimSql = "'"+CalendarUtils.format(dataFim, "dd/MM/yyyy")+"'";

	    StringBuilder sqlconsulta = new StringBuilder(
		    " select " +		    
		    " tvd.descricao as vinculo, " +
		    " d.nivel as nivel, " +
		    " count(distinct pes.id_pessoa) as total"+		    
		    " from extensao.discente_extensao as de " +
		    " join extensao.tipo_situacao_discente_extensao tsde on tsde.id_tipo_situacao_discente_extensao = de.id_situacao_discente_extensao"+
		    " join projetos.tipo_vinculo_discente tvd using(id_tipo_vinculo_discente) " +
		    " join discente d using(id_discente) " +
		    " join comum.pessoa pes using(id_pessoa) " +
		    " join extensao.atividade a using(id_atividade) " +
		    " join projetos.projeto p using(id_projeto) " +
	    " where p.ativo = trueValue() and de.ativo = trueValue() ");
	    sqlconsulta.append(" and " + HibernateUtils.generateDateIntersection("p.data_inicio", "p.data_fim", dataInicioSql, dataFimSql ));
	    sqlconsulta.append(" and p.id_tipo_situacao_projeto = " + idSituacaoAcao);
	    if (idTipoAcao != null && idTipoAcao > 0) 
	    	sqlconsulta.append(" and a.id_tipo_atividade_extensao = " + idTipoAcao);
	    sqlconsulta.append(" group by tvd.descricao, d.nivel");		
	    sqlconsulta.append(" order by tvd.descricao, d.nivel");

	    result = executeSql(sqlconsulta.toString());
	}
	return result;
    }

    /**
     * Encontra os resumos de participantes de Ações de acordo com os parâmetros informados.
     * 
     * @param dataInicio
     * @param dataFim
     * @param idSituacaoAcao
     * @return
     * @throws DAOException
     * @throws HibernateException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")    
    public List<HashMap<String,Object>> findResumoParticipantesByPeriodoAreaTematica(Date dataInicio, Date dataFim, Integer idSituacaoAcao, Integer idTipoAreaTematica) throws DAOException, HibernateException, SQLException {
	List result = new ArrayList<HashMap<String,Object>>();
	if (dataInicio != null && dataFim != null) {
	    
	    String dataInicioSql = "'"+CalendarUtils.format(dataInicio, "dd/MM/yyyy")+"'";  
	    String dataFimSql = "'"+CalendarUtils.format(dataFim, "dd/MM/yyyy")+"'";
	    
	    StringBuilder sqlconsulta = new StringBuilder(
		    " select " +		    
		    " ta.descricao as tipoAcao, " +
		    " count(id_projeto) as totalAcoes, ");
	    
	    	sqlconsulta.append(
		    " (select count (distinct id_discente) from extensao.discente_extensao de " +
		    "   join extensao.plano_trabalho_extensao pl using (id_plano_trabalho_extensao) " +
		    "   join extensao.atividade a1 on a1.id_atividade = pl.id_atividade " +
		    "   join projetos.projeto p using(id_projeto) " +
		    "     where a1.id_tipo_atividade_extensao = a.id_tipo_atividade_extensao " +
		    " and a1.id_area_tematica_principal = " + idTipoAreaTematica);		    
		    sqlconsulta.append(" and " + HibernateUtils.generateDateIntersection("p.data_inicio", "p.data_fim", dataInicioSql, dataFimSql ));
		    sqlconsulta.append(" and p.id_tipo_situacao_projeto = " + idSituacaoAcao + 		    
		    "        and de.ativo = trueValue() and p.ativo = trueValue()) as totalDiscentes, ");
	    	
	    	sqlconsulta.append(
	    	    " (select count (distinct id_pessoa) from projetos.membro_projeto mp " +
	    	    "   join extensao.atividade a1 using(id_projeto) " +
	    	    "   join projetos.projeto p using (id_projeto)" +
	    	    "     where a1.id_tipo_atividade_extensao = a.id_tipo_atividade_extensao " +
	    	    " and a1.id_area_tematica_principal = " + idTipoAreaTematica );		    
		    sqlconsulta.append(" and " + HibernateUtils.generateDateIntersection("p.data_inicio", "p.data_fim", dataInicioSql, dataFimSql ));
		    sqlconsulta.append(" and p.id_tipo_situacao_projeto = " + idSituacaoAcao + 		    
	    	    "        and mp.id_categoria_membro = 1 " +
	    	    "        and mp.ativo = trueValue() and p.ativo = trueValue()) as totalDocentes, ");

	    	sqlconsulta.append(
	    	    " (select count (distinct id_pessoa) from projetos.membro_projeto mp" +
	    	    "	 join extensao.atividade a1 using(id_projeto)" +
	    	    "	 join projetos.projeto p using (id_projeto)" +
	    	    "	   where a1.id_tipo_atividade_extensao = a.id_tipo_atividade_extensao " +
	    	    " and a1.id_area_tematica_principal = " + idTipoAreaTematica );		    
		    sqlconsulta.append(" and " + HibernateUtils.generateDateIntersection("p.data_inicio", "p.data_fim", dataInicioSql, dataFimSql ));
		    sqlconsulta.append(" and p.id_tipo_situacao_projeto = " + idSituacaoAcao +		    
	    	    "	    	and mp.id_categoria_membro = 3" +
	    	    "	    	and mp.ativo = trueValue() and p.ativo = trueValue()) as totalTecnicosAdministrativo, ");

	    	sqlconsulta.append(
	    	    " (select count (distinct id_pessoa) from projetos.membro_projeto mp" +
	    	    "    join extensao.atividade a1 using(id_projeto)" +
	    	    "  	 join projetos.projeto p using (id_projeto)" +
	    	    "      where a1.id_tipo_atividade_extensao = a.id_tipo_atividade_extensao " +
	    	    " and a1.id_area_tematica_principal = " + idTipoAreaTematica );
		    sqlconsulta.append(" and " + HibernateUtils.generateDateIntersection("p.data_inicio", "p.data_fim", dataInicioSql, dataFimSql ));
		    sqlconsulta.append(" and p.id_tipo_situacao_projeto = " + idSituacaoAcao + 		    
	    	    "	    	and mp.id_categoria_membro = 4" +
	    	    "	    	and mp.ativo = trueValue() and p.ativo = trueValue()) as totalComunidadeExterna ");

	    	sqlconsulta.append(
	    	    "  from  extensao.atividade a " +
	    	    "     join extensao.tipo_atividade_extensao ta using(id_tipo_atividade_extensao)" +
	    	    "     join projetos.projeto p using(id_projeto)" +
	    	    "	    	where p.ativo = trueValue() ");
	    	
	    	sqlconsulta.append(" and a.id_area_tematica_principal = " + idTipoAreaTematica );	    	
	    	sqlconsulta.append(" and " + HibernateUtils.generateDateIntersection("p.data_inicio", "p.data_fim", dataInicioSql, dataFimSql ));
	    	sqlconsulta.append(" and p.id_tipo_situacao_projeto = " + idSituacaoAcao);	    	
	    	sqlconsulta.append(" group by ta.descricao, a.id_tipo_atividade_extensao, a.id_area_tematica_principal order by ta.descricao");
	    	
	    result = executeSql(sqlconsulta.toString());
	}
	return result;
    }
    
    
    /**
     * Lista Resumo de produtos de extensão (cds, livros, etc)
     * produzidos por situação, período e área temática.
     * 
     * @param dataInicio
     * @param dataFim
     * @param idSituacaoAcao
     * @param idTipoAreaTematica
     * @return
     * @throws DAOException
     * @throws HibernateException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public List<HashMap<String,Object>> findResumoProdutosByPeriodoAreaTematica(Date dataInicio, Date dataFim, Integer idSituacaoAcao, Integer idTipoAreaTematica) throws DAOException, HibernateException, SQLException {
	List result = new ArrayList<HashMap<String,Object>>();
	if (dataInicio != null && dataFim != null) {
	    
	    String dataInicioSql = "'"+CalendarUtils.format(dataInicio, "dd/MM/yyyy")+"'";  
	    String dataFimSql = "'"+CalendarUtils.format(dataFim, "dd/MM/yyyy")+"'";

	    StringBuilder sqlconsulta = new StringBuilder(
		    " select " +		    
		    " tp.descricao as tipoProduto, " +
		    " count(id_projeto) as totalProdutos ");
	    
	    sqlconsulta.append(
	    	    "  	from extensao.atividade a" +
	    	    "	    join extensao.tipo_atividade_extensao ta using(id_tipo_atividade_extensao)" +
	    	    "	    join extensao.produto pd using (id_produto)" +
	    	    "	    join extensao.tipo_produto tp using (id_tipo_produto)" +
	    	    "	    join projetos.projeto p using(id_projeto)" +	    	    
	    	    "	    	where p.ativo = trueValue() and ta.id_tipo_atividade_extensao = 6 ");
	    
	    	sqlconsulta.append(" and a.id_area_tematica_principal = " + idTipoAreaTematica );
	    	sqlconsulta.append(" and " + HibernateUtils.generateDateIntersection("p.data_inicio", "p.data_fim", dataInicioSql, dataFimSql ));
	    	sqlconsulta.append(" and p.id_tipo_situacao_projeto = " + idSituacaoAcao);	    	
	    	sqlconsulta.append(
	    	    "	    	group by tp.descricao, pd.id_tipo_produto" +
	    	    "	    	order by tp.descricao");
	    	
	    result = executeSql(sqlconsulta.toString());
	}
	return result;
    }

	/**
	 * Retorna uma lista a quantidade de servidores por formação. 
	 * @param situacaoAtividade
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public List<HashMap<String, Object>> relatorioDocentesPorNivel(Integer situacaoAtividade,
			Date dataInicio, Date dataFim) {
		
		StringBuilder hqlConsulta = new StringBuilder();
		StringBuilder hqlFiltros = new StringBuilder();		
		
		hqlConsulta.append(	" select f.denominacao, count( distinct servidor.id ) " +
							" from MembroProjeto mp " +
							" inner join mp.projeto proj " +
							" inner join proj.situacaoProjeto sitProj " +
							" inner join mp.servidor servidor " +
							" inner join servidor.formacao f " +
							" inner join servidor.categoria categoria  " +
							" where categoria.id = :DOCENTE and mp.ativo = trueValue() and proj.ativo = trueValue() " );		
		
			
		if( situacaoAtividade != null ) {
			hqlFiltros.append(" and sitProj = :situacaoAtividade ");
		}
		
		if( dataInicio != null && dataFim != null) {
			hqlFiltros.append(" and " + HibernateUtils.generateDateIntersection("proj.dataInicio", "proj.dataFim", ":dataInicio", ":dataFim"));
		}	
		
		
		try {
			
			hqlConsulta.append(hqlFiltros);
			hqlConsulta.append(" group by f.denominacao ");
			hqlConsulta.append(" order by f.denominacao ");
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());		
			
			queryConsulta.setInteger("DOCENTE", Categoria.DOCENTE);
			
			if( situacaoAtividade != null ) {
				queryConsulta.setInteger("situacaoAtividade", situacaoAtividade);
			}
		
			if( dataInicio != null && dataFim != null) {
				queryConsulta.setDate("dataInicio", dataInicio);
				queryConsulta.setDate("dataFim", dataFim);
			}				
			
			@SuppressWarnings("unchecked")
			List< HashMap<String,Object> > lista = queryConsulta.list();			
			return lista;
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;		
		
	}
    
    
        
    

    
    
}