/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/12/2009
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;

/**
 * DAO responsável pelas consultas referente ao público atingido com base em todos os relatórios 
 * submetidos pelos coordenadores de ações de extensão.
 * 
 * @author Geyson
 */
public class PublicoAtingidoRelatorioDao extends AbstractRelatorioSqlDao {

	/**
	 * Relatório de público atingido com base em todos os relatórios submetidos pelos coordenadores de ações de extensão.
	 * @param congressoIniciacaoCientifica
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioPublicoAtingido(Date inicio, Date fim) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(" select distinct rae.id_relatorio_acao_extensao as idRelatorio, " +
				"pro.titulo as titulo,  (case when rae.discriminador_tipo_relatorio = 'CE' then 'CURSO/EVENTO' when rae.discriminador_tipo_relatorio = 'P' then 'PROJETO' end ) as discriminador, " +
				"rae.data_cadastro as data, rae.publico_real_atingido as publico, tr.descricao as tipo, " +
				"p.nome as coordenador, pro.data_inicio as ini, pro.data_fim as fi " +
				"from  extensao.relatorio_acao_extensao rae " +
				"inner join extensao.tipo_relatorio tr on (tr.id_tipo_relatorio = rae.id_tipo_relatorio) " +
				"inner join extensao.atividade ae on (rae.id_atividade = ae.id_atividade) " +
				"inner join projetos.projeto pro on (pro.id_projeto = ae.id_projeto) " +
				"inner join projetos.membro_projeto mp on (mp.id_projeto = pro.id_projeto) " +
				"inner join comum.registro_entrada re on (rae.id_registro_entrada_cadastro = re.id_entrada) " +
				"inner join comum.usuario u on (re.id_usuario = u.id_usuario) " +
				"inner join comum.pessoa p on (u.id_pessoa = p.id_pessoa)  ");
		
		if ((inicio != null) && (fim != null)) {
		    //sqlconsulta.append(" where pro.data_inicio >= '"+ inicio +"' and pro.data_fim <= '" + fim +"' " );
		    sqlconsulta.append(" AND " + HibernateUtils.generateDateIntersection("pro.data_inicio", "pro.data_fim", "'" + inicio.toString() +"'","'" + fim.toString() + "'"));
		}
		
		sqlconsulta.append(" order by tr.descricao ");
				
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
}
