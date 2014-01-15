/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.dominio.Unidade;

/**
*
* DAO responsável pelas consultas relacionadas às classificações bibliográficas do sistema.
*
* @author Felipe Rivas
* @since 09/02/2012
* @version 1.0 criacao da classe
*
*/
public class InventarioAcervoBibliotecaDao extends GenericSigaaDAO {
	
	/**A projeção padrão com todas as informações do inventário.  Apenas se precisar algo mais otimizado não use essa projeção. */
	public static final String POJECAO_PADRAO_INVENTARIO  = " inventario.id, inventario.descricao, inventario.ano, inventario.aberto, inventario.dataFechamento, " 
				+" biblioteca.id, biblioteca.descricao, unidade.id, colecao.id, colecao.descricao " ; 
	
	
	///////////////////////////////////////////////////////////////////////
	/////////////////////// Métodos de Busca //////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	/**
	 * <p>Retorna os inevntários já cadastrados para uma biblioteca.</p>
	 * 
	 * <p>ESSA MÉTODO É O QUE IMPLEMENTA A BUSCA DOS INVENTÁRIOS </p>
	 * 
	 * @param biblioteca
	 * @return
	 * @throws DAOException
	 */
	public List<InventarioAcervoBiblioteca> findAllByBibliotecaAgrupado(int idBiblioteca) throws DAOException {
		
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT "+POJECAO_PADRAO_INVENTARIO	
				+" ,COUNT( DISTINCT materiais.id) ");
		hql.append(" FROM InventarioAcervoBiblioteca inventario ");
		hql.append(" LEFT JOIN inventario.inventarioMaterialRegistradoList materiais  WITH materiais.ativo = :true  ");
		hql.append(" LEFT JOIN inventario.biblioteca biblioteca  ");
		hql.append(" LEFT JOIN biblioteca.unidade unidade  ");
		hql.append(" LEFT JOIN inventario.colecao colecao  ");
		hql.append(" WHERE inventario.biblioteca.id = :idBiblioteca ");
		hql.append(" GROUP BY inventario.id, inventario.descricao, inventario.ano, inventario.aberto, inventario.dataFechamento, biblioteca.id, biblioteca.descricao, unidade.id, colecao.id, colecao.descricao ");
		hql.append(" ORDER BY ano DESC, aberto DESC, inventario.descricao ASC ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idBiblioteca", idBiblioteca);
		q.setBoolean("true", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = (List<Object[]>) q.list();
		
		List<InventarioAcervoBiblioteca> resultado = new ArrayList<InventarioAcervoBiblioteca>();
		
		
		int ponteiro;
		
		// Monta o mapa dos inventários da biblioteca agrupado por ano		
		for (Object[] objArray : lista) {
		
			InventarioAcervoBiblioteca  inventario = new InventarioAcervoBiblioteca();

			ponteiro = 0;
			
			inventario.setId((Integer) objArray[ponteiro++]);
			inventario.setDescricao((String) objArray[ponteiro++]);
			inventario.setAno((Integer) objArray[ponteiro++]);
			inventario.setAberto((Boolean) objArray[ponteiro++]);
			inventario.setDataFechamento((Date) objArray[ponteiro++]);
			
			inventario.setBiblioteca(new Biblioteca((Integer) objArray[ponteiro++], (String) objArray[ponteiro++] ));
			inventario.getBiblioteca().setUnidade(new Unidade( (Integer) objArray[ponteiro++]));// para validação das permissões
			
			if((Integer) objArray[8] != null)
				inventario.setColecao(new Colecao((Integer) objArray[ponteiro++], (String) objArray[ponteiro++] ));
			else
				ponteiro+=2; // anda os dados da coleção que não tem
			
			inventario.setQuantidadeMateriaisRegistrados((Long) objArray[ponteiro++]);
			
			
			resultado.add(inventario);
		}
		
		return resultado;
	}

	
	
	/**
	 * Retorna os inventários já fechados para a biblioteca passada.
	 * 
	 * @param biblioteca
	 * @return
	 * @throws DAOException
	 */
	public List<InventarioAcervoBiblioteca> findAllFechadoByBiblioteca(int idBiblioteca) throws DAOException {
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT "+POJECAO_PADRAO_INVENTARIO	);
		hql.append(" FROM InventarioAcervoBiblioteca inventario ");
		hql.append(" LEFT JOIN inventario.biblioteca biblioteca  ");
		hql.append(" LEFT JOIN biblioteca.unidade unidade  ");
		hql.append(" LEFT JOIN inventario.colecao colecao  ");
		hql.append(" WHERE biblioteca.id = :idBiblioteca AND inventario.aberto = :aberto ");
		hql.append(" ORDER BY inventario.descricao ASC ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idBiblioteca", idBiblioteca);
		q.setBoolean("aberto", false);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = (List<Object[]>) q.list();
		
		return  monstaInformacoesInventarioProjecaoPadrao(lista);
	
	}


	/**
	 * Retorna os inventários fechados para a biblioteca passada com as informações extritamente necessárias.
	 *
	 * Retorna apenas as inforamções execiais do inventário
	 * @InventarioAcervoBiblioteca
	 */
	public List<InventarioAcervoBiblioteca> findAllFechadosOtimizadoByBiblioteca(int idBiblioteca) throws DAOException {
		StringBuilder hql = new StringBuilder(); 
		
		String projecao = "iab.id, iab.descricao, iab.ano";
		
		hql.append(" SELECT " + projecao + " ");
		hql.append(" FROM InventarioAcervoBiblioteca iab ");
		hql.append(" WHERE iab.biblioteca.id = :idBiblioteca AND iab.aberto = :aberto ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idBiblioteca", idBiblioteca);
		q.setBoolean("aberto", false);
		
		@SuppressWarnings("unchecked")
		List<InventarioAcervoBiblioteca> lista = (List<InventarioAcervoBiblioteca>) HibernateUtils.parseTo(q.list(), projecao, InventarioAcervoBiblioteca.class, "iab");
		
		return lista;
	}
	

	/**
	 * Retorna o inventário aberto para a biblioteca passada. (Só pode existir 1.  Existe um índice no banco que agarante isso!)
	 *
	 * @InventarioAcervoBiblioteca
	 */
	public InventarioAcervoBiblioteca findAbertoByBiblioteca(int idbiblioteca) throws DAOException {
		
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT " + POJECAO_PADRAO_INVENTARIO + " ");
		hql.append(" FROM InventarioAcervoBiblioteca inventario ");
		hql.append(" LEFT JOIN inventario.biblioteca biblioteca  ");
		hql.append(" LEFT JOIN biblioteca.unidade unidade  ");
		hql.append(" LEFT JOIN inventario.colecao colecao  ");
		hql.append(" WHERE inventario.biblioteca.id = :biblioteca AND inventario.aberto = :aberto ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("biblioteca", idbiblioteca);
		q.setBoolean("aberto", true);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = (List<Object[]>) q.list();
		List<InventarioAcervoBiblioteca> resultado = monstaInformacoesInventarioProjecaoPadrao(lista);
		
		if(resultado.size() > 0)
			return resultado.get(0);
		else
			return null;
	}
	
	
	
	/**
	 * Retorna o inventário aberto para a biblioteca passada. (Só pode existir 1.  Existe um índice no banco que agarante isso!)
	 *
	 * Retorna apenas as inforamções execiais do inventário
	 * @InventarioAcervoBiblioteca
	 */
	public InventarioAcervoBiblioteca findAbertoOtimizadoByBiblioteca(int idBiblioteca) throws DAOException {
		StringBuilder hql = new StringBuilder(); 
		
		String projecao = "iab.id, iab.descricao, iab.ano";
		
		hql.append(" SELECT " + projecao + " ");
		hql.append(" FROM InventarioAcervoBiblioteca iab ");
		hql.append(" WHERE iab.biblioteca.id = :idBiblioteca AND iab.aberto = :aberto ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idBiblioteca", idBiblioteca);
		q.setBoolean("aberto", true);
		
		@SuppressWarnings("unchecked")
		List<InventarioAcervoBiblioteca> lista = (List<InventarioAcervoBiblioteca>) HibernateUtils.parseTo(q.list(), projecao, InventarioAcervoBiblioteca.class, "iab");
		
		return lista.size() > 0 ? lista.get(0) : null;
	}

	
	
	///////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 * Retorna a quantidade de materiais registrado para o inventário.
	 * 
	 * @param biblioteca
	 * @return
	 * @throws DAOException
	 */
	public Long countMateriaisRegistradosInventario(int idInventario) throws DAOException {
		
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT COUNT( DISTINCT materiais.id ) ");
		hql.append(" FROM InventarioAcervoBiblioteca inventario ");
		hql.append(" LEFT JOIN inventario.inventarioMaterialRegistradoList materiais ");
		hql.append(" WHERE inventario.id = :idInventario AND materiais.ativo = :true ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idInventario", idInventario);
		q.setBoolean("true", true);
		
		return (Long) q.uniqueResult();
	}
	
	
	
	/**
	 * Desativa o registro do material no inventário retornando se foi feito com sucesso ou não.
	 */
	public boolean removeRegistroMaterialInventario(int idMaterial, int idInventario) throws DAOException{
		
		Query q = getSession().createSQLQuery("UPDATE biblioteca.inventario_material_registrado set ativo = :false "
			            +" WHERE id_inventario_acervo_biblioteca = :idInventario AND id_material_informacional = :idMaterial AND ativo = :true  ");
		
		q.setInteger("idInventario", idInventario);
		q.setInteger("idMaterial", idMaterial);
		q.setBoolean("false", false);
		q.setBoolean("true", true);
		
		if (q.executeUpdate() < 1)
			return false;
		else
			return true;
	}
	
	

	

	/**
	 * Retorna o inventário ativo (se houver) cuja biblioteca contém a unidade passada;
	 * @throws DAOException 
	 */
	public InventarioAcervoBiblioteca findInventarioAtivoByUnidadeBiblioteca(UnidadeGeral unidade) throws DAOException {
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT iab.id, iab.descricao, iab.biblioteca.id, iab.biblioteca.descricao, iab.biblioteca.identificador ");
		hql.append(" FROM InventarioAcervoBiblioteca iab ");
		hql.append(" WHERE iab.aberto = :aberto AND iab.biblioteca.unidade.id = :unidade ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setBoolean("aberto", true);
		q.setInteger("unidade", unidade.getId());

		Object[] obj = (Object[]) q.uniqueResult();
		InventarioAcervoBiblioteca inventario = null;
		
		if (obj != null) {
			inventario = new InventarioAcervoBiblioteca();
			Biblioteca biblioteca = new Biblioteca();
			int cont = 0;

			inventario.setId((Integer)obj[cont++]);
			inventario.setDescricao((String)obj[cont++]);
			biblioteca.setId((Integer)obj[cont++]);
			biblioteca.setDescricao((String)obj[cont++]);
			biblioteca.setIdentificador((String)obj[cont++]);
			
			inventario.setBiblioteca(biblioteca);
		}
		
		// só era para ter uma unidade por biblioteca.
		return inventario;
	}

	/**
	 * Retorna o id de um material a partir do seu código de barras.
	 * 
	 * @param codigoBarras
	 * @return
	 * @throws DAOException
	 */
	public Integer findIdMaterialAtivoByCodigoBarras(String codigoBarras) throws DAOException {
		String sql = " SELECT m.id_material_informacional FROM biblioteca.material_informacional m "
			+" INNER JOIN biblioteca.situacao_material_informacional situacao ON (m.id_situacao_material_informacional = situacao.id_situacao_material_informacional)"
			+" WHERE m.codigo_barras = :codigoBarras AND situacao.situacao_de_baixa = falseValue() AND m.ativo = trueValue() ";
	
		Query q = getSession().createSQLQuery(sql);
		q.setString("codigoBarras", codigoBarras);
		
		return (Integer) q.uniqueResult();
	}

	/**
	 * Retorna todos os inventários do sistema que se encontram abertos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<InventarioAcervoBiblioteca> findAllAbertos() throws DAOException {
		return (List<InventarioAcervoBiblioteca>) this.findByExactField(InventarioAcervoBiblioteca.class, "aberto", true);
	}

	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	///////// Buscas Usadas durante o registro do material no acervo                ////////
	////////  Deve ser otimizadas para pode registrar uma quantidade grande por vez ////////
	////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Não não registar o material duas vezes no mesmo inventári.
	 */
	public boolean existeMaterialInventario(String codigoBarrasMaterial, int idInventario) throws DAOException {
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT count(imr) ");
		hql.append(" FROM InventarioMaterialRegistrado imr ");
		hql.append(" WHERE imr.material.codigoBarras = :codigoBarras AND imr.inventario.id = :idInventario AND imr.ativo = :ativo ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setString("codigoBarras", codigoBarrasMaterial);
		q.setInteger("idInventario", idInventario);
		q.setBoolean("ativo", true);
		
		return ((Long) q.uniqueResult()) > 0;
	}
	
	/**
	 * Retorna as informações do inventário necessárias paa validação. 
	 *
	 * @InventarioAcervoBiblioteca
	 */
	public InventarioAcervoBiblioteca findInformacoesInventarioParaValidacaoRegistro(int idInventario) throws DAOException {
		
		StringBuilder hql = new StringBuilder(); 
		
		hql.append(" SELECT inventario.id, inventario.aberto, biblioteca.id, biblioteca.descricao, unidade.id, colecao.id, colecao.descricao ");
		hql.append(" FROM InventarioAcervoBiblioteca inventario ");
		hql.append(" LEFT JOIN inventario.biblioteca biblioteca  ");
		hql.append(" LEFT JOIN biblioteca.unidade unidade  ");
		hql.append(" LEFT JOIN inventario.colecao colecao  ");
		hql.append(" WHERE inventario.id = :idInventario ");
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idInventario", idInventario);
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = (List<Object[]>) q.list();
		
		List<InventarioAcervoBiblioteca> resultado = new ArrayList<InventarioAcervoBiblioteca>();
		
		int ponteiro;
		
		// Monta o mapa dos inventários da biblioteca agrupado por ano		
		for (Object[] objArray : lista) {
		
			InventarioAcervoBiblioteca  inventario = new InventarioAcervoBiblioteca();

			ponteiro = 0;
			
			inventario.setId((Integer) objArray[ponteiro++]);
			inventario.setAberto((Boolean) objArray[ponteiro++]);
			
			inventario.setBiblioteca(new Biblioteca((Integer) objArray[ponteiro++], (String) objArray[ponteiro++] ));
			inventario.getBiblioteca().setUnidade(new Unidade( (Integer) objArray[ponteiro++]));// para validação das permissões
			
			if((Integer) objArray[5] != null)
				inventario.setColecao(new Colecao((Integer) objArray[ponteiro++], (String) objArray[ponteiro++] ));
			else
				ponteiro+=2; // anda os dados da coleção que não tem	
			
			resultado.add(inventario);
		}
		
		
		if(resultado.size() > 0)
			return resultado.get(0);
		else
			return null;
	}
	
	
	
	/**
	 * Retorna apenas as informações EXTRITAMENTE necessárias para validação do material no registro do inventário
	 *
	 * @MaterialInformacional
	 */
	public List<MaterialInformacional> findInformacoesMateriaisValidacaoRegistroInventario(List<String> codigosBarras) throws DAOException {	
		String projecao = " m.id, m.codigoBarras, m.biblioteca.id, m.colecao.id ";		
		
		String hql = "SELECT "+projecao+
				" FROM MaterialInformacional m " +
				" INNER JOIN m.situacao situacao "+
				" WHERE m.codigoBarras IN ( :codigosBarras ) " +
				" AND situacao.situacaoDeBaixa = :false AND m.ativo = :true "; // So pode ser registrados materiais ativos não baixados
		
		Query q = getSession().createQuery(hql);
		q.setParameterList("codigosBarras", codigosBarras);
		q.setBoolean("false", false);
		q.setBoolean("true", true);
	
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
		
		for (Object[] objects : result) {
			
			MaterialInformacional material = new Exemplar(); // Aqui tanto faz exemplar ou fascículos as informações são as mesmas
			
			material.setId( (Integer) objects[0]);
			material.setCodigoBarras( (String) objects[1]);
			
			material.setBiblioteca(new Biblioteca( (Integer) objects[2] ));
			material.setColecao(new Colecao( (Integer) objects[3] ));
			
			materiais.add(material);
		}
		
		
		return materiais;
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	
	
	
	
	/** Monta as inforamções da projeção padrão do inventário. */
	private List<InventarioAcervoBiblioteca> monstaInformacoesInventarioProjecaoPadrao( List<Object[]> lista) {
		
		List<InventarioAcervoBiblioteca> resultado = new ArrayList<InventarioAcervoBiblioteca>();
		
		int ponteiro;
		
		// Monta o mapa dos inventários da biblioteca agrupado por ano		
		for (Object[] objArray : lista) {
		
			InventarioAcervoBiblioteca  inventario = new InventarioAcervoBiblioteca();

			ponteiro = 0;
			
			inventario.setId((Integer) objArray[ponteiro++]);
			inventario.setDescricao((String) objArray[ponteiro++]);
			inventario.setAno((Integer) objArray[ponteiro++]);
			inventario.setAberto((Boolean) objArray[ponteiro++]);
			inventario.setDataFechamento((Date) objArray[ponteiro++]);
			
			inventario.setBiblioteca(new Biblioteca((Integer) objArray[ponteiro++], (String) objArray[ponteiro++] ));
			inventario.getBiblioteca().setUnidade(new Unidade( (Integer) objArray[ponteiro++]));// para validação das permissões
			
			if((Integer) objArray[8] != null)
				inventario.setColecao(new Colecao((Integer) objArray[ponteiro++], (String) objArray[ponteiro++] ));
			else
				ponteiro+=2; // anda os dados da coleção que não tem	
			
			resultado.add(inventario);
		}
		return resultado;
	}
	
	
}
