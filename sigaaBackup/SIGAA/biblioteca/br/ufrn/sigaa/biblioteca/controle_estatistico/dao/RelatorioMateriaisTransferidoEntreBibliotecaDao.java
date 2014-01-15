/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/07/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 *
 * <p>Dao exclusivo para as consultas utilizadas no relatório de materiais tranferidos entre bibliotecas </p>
 * 
 * @author jadson
 *
 */
public class RelatorioMateriaisTransferidoEntreBibliotecaDao extends GenericSigaaDAO{

	
	
	/**
	 * 
	 * Método que recupera as informações sobre os materiais transferidos entre as biblioteca no sistema.
	 *  
	 *
	 * @param idBibliotecaOrigem
	 * @param idBibliotecaDestino
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<Object[]> findExemplaresTranferidosEntreBiblioteca( 
			Collection<Integer> idsBibliotecaOrigem, Collection<Integer> idsBibliotecaDestino,
			Collection<Integer> idsColecoes, Collection<Integer> idsTiposDeMaterial
			,  Date inicioPeriodo, Date fimPeriodo, FiltroClassificacoesRelatoriosBiblioteca classificacao, String classeInicial, String classeFinal, String sqlModalidadeAquisicaoExemplares) throws DAOException{
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT origem.id_biblioteca as id_origem, origem.descricao as bibliotecaOrigem, destino.id_biblioteca as id_destino, destino.descricao as bibliotecaDestino, material.codigo_barras, exemplar.numero_patrimonio, r.data_movimentacao, p.nome " ); 
		sql.append(", cache.numero_do_sistema, cache.id_titulo_catalografico, r.gerou_chamado_patrimonial ");  // recuperar apenas o id do Título para a partir daí gerar o formato de referência completo
		
		sql.append(" FROM biblioteca.registro_movimentacao_material_informacional r " );
		sql.append(" INNER JOIN biblioteca.material_informacional material on material.id_material_informacional = r.id_material_informacional " );
		
		sql.append(" INNER JOIN biblioteca.exemplar exemplar on material.id_material_informacional = exemplar.id_exemplar " );
		sql.append(" INNER JOIN biblioteca.titulo_catalografico titulo on titulo.id_titulo_catalografico = exemplar.id_titulo_catalografico " );
		sql.append(" INNER JOIN biblioteca.cache_entidades_marc cache on cache.id_titulo_catalografico = exemplar.id_titulo_catalografico " );
		
		sql.append(" INNER JOIN biblioteca.biblioteca origem on origem.id_biblioteca = r.id_biblioteca_origem " );
		sql.append(" INNER JOIN biblioteca.biblioteca destino on destino.id_biblioteca = r.id_biblioteca_destino " );
		sql.append(" INNER JOIN comum.usuario u on u.id_usuario = r.id_usuario_movimentou_material " );
		sql.append(" INNER JOIN comum.pessoa p on p.id_pessoa = u.id_pessoa " );
		sql.append(" WHERE pendente = falseValue() ");
		
		if(idsBibliotecaOrigem != null && idsBibliotecaOrigem.size() > 0){
			sql.append(" AND r.id_biblioteca_origem in  "+UFRNUtils.gerarStringIn(idsBibliotecaOrigem));
		}
		if(idsBibliotecaDestino != null && idsBibliotecaDestino.size() > 0){
			sql.append(" AND r.id_biblioteca_destino in "+UFRNUtils.gerarStringIn(idsBibliotecaDestino));
		}
		
		if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
			sql.append(" AND titulo."+classificacao.getColunaClassificacao()+" >= :classeInicial AND titulo."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
		}
		
		sql.append(sqlModalidadeAquisicaoExemplares);	
		
		sql.append(" AND ( data_movimentacao between :dataInicio AND :dataFim ) ");
		
		if(idsColecoes != null && idsColecoes.size() > 0){
			sql.append(" AND material.id_colecao in "+UFRNUtils.gerarStringIn(idsColecoes));
		}
		
		if(idsTiposDeMaterial != null && idsTiposDeMaterial.size() > 0){
			sql.append(" AND material.id_tipo_material in "+UFRNUtils.gerarStringIn(idsTiposDeMaterial));
		}
		
		
		sql.append(" ORDER BY r.id_biblioteca_origem, r.id_biblioteca_destino ");	
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
			q.setString("classeInicial", classeInicial );
			q.setString("classeFinal", classeFinal);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return lista;
	}
	
	
	/**
	 * 
	 * Método que recupera as informações sobre os materiais transferidos entre as biblioteca no sistema.
	 *  
	 *
	 * @param idBibliotecaOrigem
	 * @param idBibliotecaDestino
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<Object[]> findFasciculosTranferidosEntreBiblioteca( 
			Collection<Integer> idsBibliotecaOrigem, Collection<Integer> idsBibliotecaDestino,
			Collection<Integer> idsColecoes, Collection<Integer> idsTiposDeMaterial
			,  Date inicioPeriodo, Date fimPeriodo, FiltroClassificacoesRelatoriosBiblioteca classificacao, String classeInicial, String classeFinal, String sqlModalidadeAquisicao) throws DAOException{
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT origem.id_biblioteca as id_origem, origem.descricao as bibliotecaOrigem, destino.id_biblioteca as id_destino, destino.descricao as bibliotecaDestino, material.codigo_barras"
				   +", r.data_movimentacao, p.nome, r.data_autorizacao, pAutorizou.nome, assinaturaOrigem.codigo || ' - ' ||  assinaturaOrigem.titulo as assinaturaOrigem, assinaturaDestino.codigo || ' - ' || assinaturaDestino.titulo as assinaturaDestino " ); 
		sql.append(", cache.numero_do_sistema, cache.id_titulo_catalografico ");  // recuperar apenas o id do Título para a partir daí gerar o formato de referência completo
		
		sql.append(" FROM biblioteca.registro_movimentacao_material_informacional r " );
		sql.append(" INNER JOIN biblioteca.material_informacional material on material.id_material_informacional = r.id_material_informacional " );
		
		sql.append(" INNER JOIN biblioteca.fasciculo fasciculo on material.id_material_informacional = fasciculo.id_fasciculo " );
		sql.append(" INNER JOIN biblioteca.assinatura assinatura on assinatura.id_assinatura = fasciculo.id_assinatura " );
		sql.append(" INNER JOIN biblioteca.titulo_catalografico titulo on titulo.id_titulo_catalografico = assinatura.id_titulo_catalografico " );
		sql.append(" INNER JOIN biblioteca.cache_entidades_marc cache on cache.id_titulo_catalografico = assinatura.id_titulo_catalografico " );
		
		
		sql.append(" INNER JOIN biblioteca.assinatura assinaturaOrigem on assinaturaOrigem.id_assinatura = r.id_assinatura_origem " );
		sql.append(" INNER JOIN biblioteca.assinatura assinaturaDestino on assinaturaDestino.id_assinatura = r.id_assinatura_destino " );
		
		sql.append(" INNER JOIN biblioteca.biblioteca origem on origem.id_biblioteca = r.id_biblioteca_origem " );
		sql.append(" INNER JOIN biblioteca.biblioteca destino on destino.id_biblioteca = r.id_biblioteca_destino " );
		sql.append(" INNER JOIN comum.usuario u on u.id_usuario = r.id_usuario_movimentou_material " );
		sql.append(" INNER JOIN comum.pessoa p on p.id_pessoa = u.id_pessoa " );
		sql.append(" INNER JOIN comum.usuario uAutorizou on uAutorizou.id_usuario = r.id_usuario_autorizou_movimentacao_material " );
		sql.append(" INNER JOIN comum.pessoa pAutorizou on pAutorizou.id_pessoa = uAutorizou.id_pessoa " );
		sql.append(" WHERE pendente = falseValue() ");
		
		if(idsBibliotecaOrigem != null && idsBibliotecaOrigem.size() > 0){
			sql.append(" AND r.id_biblioteca_origem in  "+UFRNUtils.gerarStringIn(idsBibliotecaOrigem));
		}
		if(idsBibliotecaDestino != null && idsBibliotecaDestino.size() > 0){
			sql.append(" AND r.id_biblioteca_destino in "+UFRNUtils.gerarStringIn(idsBibliotecaDestino));
		}
		
		if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
			sql.append(" AND titulo."+classificacao.getColunaClassificacao()+" >= :classeInicial AND titulo."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
		}
		
	
		sql.append(sqlModalidadeAquisicao);	
			
	
		
		sql.append(" AND ( data_movimentacao between :dataInicio AND :dataFim ) ");
		
		if(idsColecoes != null && idsColecoes.size() > 0){
			sql.append(" AND material.id_colecao in "+UFRNUtils.gerarStringIn(idsColecoes));
		}
		
		if(idsTiposDeMaterial != null && idsTiposDeMaterial.size() > 0){
			sql.append(" AND material.id_tipo_material in "+UFRNUtils.gerarStringIn(idsTiposDeMaterial));
		}
		
		
		sql.append(" ORDER BY r.id_biblioteca_origem, r.id_biblioteca_destino ");	
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
			q.setString("classeInicial", classeInicial );
			q.setString("classeFinal", classeFinal);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return lista;
	}
	
}
