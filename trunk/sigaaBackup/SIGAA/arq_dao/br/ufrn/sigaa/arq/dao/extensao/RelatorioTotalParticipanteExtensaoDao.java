package br.ufrn.sigaa.arq.dao.extensao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * DAO responsável pelas consultas referentes ao total de participantes de ações de Extensão
 *  
 * @author julio
 */
public class RelatorioTotalParticipanteExtensaoDao extends AbstractRelatorioSqlDao{
	
	/**
	 * Relatório do Total de Docentes por unidade Participantes de uma Ação de Extensão 
	 * 
	 * @param unidade
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioTotalParticipantesDocentesExtensao(Integer unidade, Date inicio, Date fim) throws DAOException {
		StringBuilder sqlConsulta = 
			new StringBuilder(
				"select	upper(u.nome) as nome, upper(u.sigla) as sigla, count(distinct id_servidor) as quantidade, " +
				" unidade_gestora.id_unidade as id_gestora, unidade_gestora.nome as nome_gestora " +
				"from projetos.membro_projeto mp " +
				" join rh.servidor s using(id_servidor) " +
				" join comum.unidade u using(id_unidade) " +
				" join comum.unidade unidade_gestora on unidade_gestora.id_unidade = u.id_gestora " +
				" join projetos.projeto p using(id_projeto) " +
				" join extensao.atividade a using(id_projeto) " +
				"where mp.id_servidor is not null " +
				" and mp.id_categoria_membro = 1 " +
				" and mp.ativo " +
				" and p.ativo " +
				" and a.ativo " );
		
				sqlConsulta.append(" and (p.id_tipo_situacao_projeto in (" + TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO + "," + TipoSituacaoProjeto.EXTENSAO_CONCLUIDO + ") or " +
						"(p.id_tipo_situacao_projeto in (" + TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO + "," + TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO + ") and p.extensao)) ");
		
				if( (inicio != null) && (fim != null) ){
					sqlConsulta.append( " and (p.data_inicio >= '" + inicio + "' and p.data_inicio <= '" + fim +"') ");
				}
				
				if( unidade > 0 ){
					sqlConsulta.append(" and (unidade_gestora.id_unidade = " + unidade + " or u.id_unidade = "+ unidade +") ");
				}
				
				sqlConsulta.append(" group by unidade_gestora.nome, unidade_gestora.id_unidade, u.nome, u.sigla " );
				sqlConsulta.append(" order by unidade_gestora.nome");
				
				List<Map<String,Object>> result;
				
				try{
					result = executeSql(sqlConsulta.toString());
				}catch(Exception e){
					e.printStackTrace();
					throw new DAOException(e);
				}
				return result;
	}
	
	/**
	 * Relatório do Total de Discentes nas equipes de projetos, participantes de Ação de Extensão
	 * 
	 * @param unidade
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioTotalParticipantesDiscentesProjeto(Integer unidade, Date inicio, Date fim) throws DAOException{
		StringBuilder sqlConsulta =
			new StringBuilder( 
				"select u.sigla as sigla, c.nome as nome, count(distinct mp.id_discente) as quantidade, " +
				"	u.id_unidade as id_gestora_unidade, u.nome as nome_gestora_academica " +
				"from projetos.membro_projeto mp " +
				"	join discente dis on dis.id_discente = mp.id_discente " +
				"	join curso c on c.id_curso = dis.id_curso " +
				"	join comum.unidade u on u.id_unidade = c.id_unidade " +
				"	join projetos.projeto p using(id_projeto) " +
				"	join extensao.atividade a using(id_projeto) " +
				"where mp.id_discente is not null " +
				"	and mp.id_categoria_membro = 2 " +
				"	and mp.ativo " +
				"	and p.ativo " +
				"	and a.ativo " );
	
				sqlConsulta.append(" and (p.id_tipo_situacao_projeto in (" + TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO + "," + TipoSituacaoProjeto.EXTENSAO_CONCLUIDO + ") or " +
						"(p.id_tipo_situacao_projeto in (" + TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO + "," + TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO + ") and p.extensao)) ");
		
				if( (inicio != null) && (fim != null) ){
					sqlConsulta.append( " and (p.data_inicio >= '" + inicio + "' and p.data_inicio <= '" + fim +"') ");
				}
				
				if( unidade > 0 ){
					sqlConsulta.append(" and u.id_unidade = " + unidade + "");
				}
				
				sqlConsulta.append(" group by u.nome, u.id_unidade, c.nome, u.sigla " );
				sqlConsulta.append(" order by u.nome ");
				
				List<Map<String,Object>> result;
				
				try{
					result = executeSql(sqlConsulta.toString());
				}catch(Exception e){
					e.printStackTrace();
					throw new DAOException(e);
				}
				return result;
	}
	
	/**
	 * Relatório do Total de Discentes com planos de trabalho, participantes de Ação de Extensão
	 * 
	 * @param unidade
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioTotalParticipantesDiscentesPlanoTrabalhoExtensao(Integer unidade, Date inicio, Date fim) throws DAOException{
		StringBuilder sqlConsulta =
			new StringBuilder( 
				"select u.sigla as sigla, c.nome as nome, count(distinct id_discente) as quantidade, " +
				"	gestora.id_unidade as id_gestora_unidade, gestora.nome as nome_gestora_academica " +
				"from extensao.discente_extensao d " +
				"	join discente dis using(id_discente) " +
				"	join curso c using (id_curso) " +
				"	join comum.unidade u using(id_unidade) " +
				" 	join comum.unidade gestora on gestora.id_unidade = u.id_gestora " +
				"	join extensao.atividade a using(id_atividade) " +
				"	join projetos.projeto p using(id_projeto) " +
				"where p.ativo " +
				"	and a.ativo " +
				"	and d.ativo " );
	
				sqlConsulta.append(" and (p.id_tipo_situacao_projeto in (" + TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO + "," + TipoSituacaoProjeto.EXTENSAO_CONCLUIDO + ") or " +
						"(p.id_tipo_situacao_projeto in (" + TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO + "," + TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO + ") and p.extensao)) ");
		
				if( (inicio != null) && (fim != null) ){
					sqlConsulta.append( " and (p.data_inicio >= '" + inicio + "' and p.data_inicio <= '" + fim +"') ");
				}
				
				if( unidade > 0 ){
					sqlConsulta.append(" and gestora.id_unidade = " + unidade + "");
				}
				
				sqlConsulta.append(" group by gestora.nome, gestora.id_unidade, c.nome, u.sigla " );
				sqlConsulta.append(" order by gestora.nome ");
				
				List<Map<String,Object>> result;
				
				try{
					result = executeSql(sqlConsulta.toString());
				}catch(Exception e){
					e.printStackTrace();
					throw new DAOException(e);
				}
				return result;
	}

}
