package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;

/**
 * DAO para centralizar as consultas referentes ao relatório de inventário do acervo.
 * 
 * @author Felipe
 *
 */
public class RelatorioInventarioAcervoDao extends GenericSigaaDAO {

	/** Filtro para ordenação dos resultados do relatório por código de barras. */
	public static final int ORDENAR_POR_CODIGO_BARRAS = 1;

	/** Filtro para ordenação dos resultados do relatório por Título */
	public static final int ORDENAR_POR_TITULO = 2;

	/** Filtro para ordenação dos resultados do relatório por localização */
	public static final int ORDENAR_POR_LOCALIZACAO = 3;

	/**
	 *    Recupera os dados dos materiais que são mostrados no relatório do inventário do acervo.
	 *
	 * @return uma lista de arrays, onde em cada posição do array tem os dados da projeção. <br/>
	 *               [0] número do sistema          			<br/>
	 *               [1] codigoBarras               			<br/>
	 *               [2] número patrimônio (apenas p/ exemplar)	<br/>
	 *               [3] título                     			<br/>
	 *               [4] autor                      			<br/>
	 *               [5] edição                     			<br/>
	 *               [6] ano                        			<br/>
	 *               [7] localização                			<br/>
	 */
	public List<Object[]> findInventarioMateriais(
			Integer biblioteca, InventarioAcervoBiblioteca inventario, Collection<Integer> idColecaoList, Collection<Integer> idTipoMaterialList, 
			Collection<Integer> idSituacaoMaterialInformacionalList )
			throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT c.numero_do_sistema, m.codigo_barras, e.numero_patrimonio, c.titulo, c.autor, c.edicao, c.ano, m.numero_chamada, t.id_titulo_catalografico ");
		sql.append(" FROM biblioteca.material_informacional m  ");
		sql.append(" INNER JOIN biblioteca.situacao_material_informacional s ON m.id_situacao_material_informacional = s.id_situacao_material_informacional ");
		sql.append(" LEFT JOIN biblioteca.exemplar e ON e.id_exemplar = m.id_material_informacional ");
		sql.append(" LEFT JOIN biblioteca.fasciculo f ON f.id_fasciculo = m.id_material_informacional ");
		sql.append(" LEFT JOIN biblioteca.assinatura a ON a.id_assinatura = f.id_assinatura ");
		sql.append(" INNER JOIN biblioteca.titulo_catalografico t ON t.id_titulo_catalografico = COALESCE(e.id_titulo_catalografico, a.id_titulo_catalografico) "); 
		sql.append(" INNER JOIN biblioteca.cache_entidades_marc c ON c.id_titulo_catalografico = t.id_titulo_catalografico ");
		sql.append(" INNER JOIN biblioteca.biblioteca b ON b.id_biblioteca = m.id_biblioteca ");

		if (idColecaoList != null && idColecaoList.size() > 0) {
			sql.append(" INNER JOIN biblioteca.colecao col ON col.id_colecao = m.id_colecao ");
		}

		if (idTipoMaterialList != null && idTipoMaterialList.size() > 0) {
			sql.append(" INNER JOIN biblioteca.tipo_material tipo ON tipo.id_tipo_material = m.id_tipo_material ");
		}
		
		if (idSituacaoMaterialInformacionalList != null && idSituacaoMaterialInformacionalList.size() > 0) {
			sql.append(" INNER JOIN biblioteca.situacao_material_informacional smi ON smi.id_situacao_material_informacional = m.id_situacao_material_informacional ");			
		}
		
		sql.append(" WHERE m.ativo = trueValue() AND s.situacao_de_baixa = falseValue() AND COALESCE(f.incluido_acervo, trueValue()) = trueValue() ");

		sql.append( " AND b.id_biblioteca = " + biblioteca + " " );

		if (idColecaoList != null && idColecaoList.size() > 0) {
			sql.append(" AND col.id_colecao IN (" + StringUtils.join(idColecaoList, ',') + ") ");
		}

		if (idTipoMaterialList != null && idTipoMaterialList.size() > 0) {
			sql.append(" AND tipo.id_tipo_material IN (" + StringUtils.join(idTipoMaterialList, ',') + ") ");
		}

		if (idSituacaoMaterialInformacionalList != null && idSituacaoMaterialInformacionalList.size() > 0) {
			sql.append(" AND smi.id_situacao_material_informacional IN (" + StringUtils.join(idSituacaoMaterialInformacionalList, ',') + ") ");
		}
		
		sql.append(" AND m.id_material_informacional NOT IN ( " +
					" SELECT " +
					" 	imr.id_material_informacional " + 
					" FROM " +
					" 	biblioteca.inventario_material_registrado imr " +
					" WHERE " +
					" 	imr.id_inventario_acervo_biblioteca = " + inventario.getId() + " " +
					" ) ");
		sql.append(" AND m.data_criacao <= :dataFechamento ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		q.setDate("dataFechamento", inventario.getDataFechamento());
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		return list;
	}
	
	
	/**
	 * <p>Método que retorna os registros realizados por um determinado usuário em um relatório.</p>
	 *
	 * <p>Esse método implementa a relatório:  RelatorioregistrosInevntarioAcervoUsuarioMBean.java.</p>
	 * @throws DAOException 
	 * @throws  
	 *
	 * @List<Object[]>
	 */
	public List<Object[]> findRegistrosRealizadosPeloUsuario(int idInventario, int idPessoa, int ordenacao) throws DAOException{
		
		String sql = " SELECT  c.numero_do_sistema, m.codigo_barras, e.numero_patrimonio, c.titulo, c.autor, c.edicao, c.ano, m.numero_chamada, t.id_titulo_catalografico "+ 
					" FROM biblioteca.inventario_acervo_biblioteca inventario "+
					" INNER JOIN biblioteca.inventario_material_registrado registro on registro.id_inventario_acervo_biblioteca  = inventario.id_inventario_acervo_biblioteca "+
					" INNER JOIN comum.usuario usuario on usuario.id_usuario = registro.id_usuario  "+
					" INNER JOIN comum.pessoa pessoa on pessoa.id_pessoa = usuario.id_pessoa  "+

					" INNER JOIN biblioteca.material_informacional m on m.id_material_informacional = registro.id_material_informacional "+

					" LEFT JOIN biblioteca.exemplar e on e.id_exemplar = m.id_material_informacional  "+
					" LEFT JOIN biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional  "+
					" LEFT JOIN biblioteca.assinatura a on a.id_assinatura = f.id_assinatura  "+
					" INNER JOIN biblioteca.titulo_catalografico t on t.id_titulo_catalografico = COALESCE(e.id_titulo_catalografico , a.id_titulo_catalografico ) "+ 
					" INNER JOIN biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = t.id_titulo_catalografico "+

					" WHERE m.ativo = :true "+
					" AND inventario.id_inventario_acervo_biblioteca = :idInventario and pessoa.id_pessoa = :idPessoa AND registro.ativo = :true"; 
		
			if(ordenacao == ORDENAR_POR_CODIGO_BARRAS)
				sql+=" ORDER BY  m.codigo_barras ";
			if(ordenacao == ORDENAR_POR_LOCALIZACAO)
				sql+=" ORDER BY  m.numero_chamada ";
			if(ordenacao == ORDENAR_POR_TITULO)
				sql+=" ORDER BY  c.titulo ";
		
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idInventario", idInventario);
		q.setInteger("idPessoa", idPessoa);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
	}
	
}
