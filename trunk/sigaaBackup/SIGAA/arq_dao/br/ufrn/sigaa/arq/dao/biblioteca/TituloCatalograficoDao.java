/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/11/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import static br.ufrn.arq.util.StringUtils.toAsciiAndUpperCase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Editora;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EntidadesMarcadasParaExportacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoPesquisaAvancada;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoPesquisaPorListas;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *
 * DAO para as pesquisas que retornam um Título.
 *
 * @author Jadson
 * @since 21/08/2008
 * @version 1.0 Criação da classe
 *
 */
public class TituloCatalograficoDao extends GenericSigaaDAO {

	/**  Limite da busca pelos bibliotecários. */
	public static final Integer LIMITE_BUSCA_TITULOS = 1000;
	/**  Limite da busca pública. */
	public static final Integer LIMITE_BUSCA_TITULOS_PUBLICA = 300;
	/**  Limite da busca pública mobile ( não cabe muitos resultados na tela do celular). */
	public static final Integer LIMITE_BUSCA_TITULOS_MOBILE = 10;

	
	/**   O sql padrão das buscas de títulos no acervo */
	public static final String SELECT_PADRAO = " SELECT distinct t.id_cache_entidades_marc, t.id_titulo_catalografico, t.numero_do_sistema, " +
	"t.titulo, t.meio_publicacao, t.sub_titulo, t.formas_varientes_titulo, t.autor, t.autores_secundarios, t.assunto, t.local_publicacao, t.editora, t.ano, t.edicao, t.isbn, t.numero_chamada, " +
	"t.id_obra_digitalizada, t.notas_gerais, t.nota_de_conteudo, t.notas_locais, t.serie, t.classificacao_1, t.classificacao_2, t.classificacao_3, t.quantidade_materiais_ativos_titulo, " +
	"t.localizacao_endereco_eletronico, t.catalogado, t.quantidade_vezes_consultado, t.quantidade_vezes_visualizado, t.quantidade_vezes_emprestado "
	+" FROM biblioteca.cache_entidades_marc t ";
	
	/**   O sql padrão dos inner join com os exemplares  */
	public static final String INNER_JOIN_EXEMPLARES = " INNER JOIN biblioteca.exemplar exemplar on exemplar.id_titulo_catalografico = t.id_titulo_catalografico "
	+" INNER JOIN biblioteca.material_informacional material on exemplar.id_exemplar = material.id_material_informacional "
	+" INNER JOIN biblioteca.situacao_material_informacional situacao on material.id_situacao_material_informacional = situacao.id_situacao_material_informacional ";
	
	/**   O sql padrão dos inner join com os fascículos  */
	public static final String INNER_JOIN_FASCICULOS = " INNER JOIN biblioteca.assinatura assinatura on assinatura.id_titulo_catalografico = t.id_titulo_catalografico   "
				+" INNER JOIN biblioteca.fasciculo fasciculo on fasciculo.id_assinatura = assinatura.id_assinatura   "
				+" INNER JOIN biblioteca.material_informacional material on fasciculo.id_fasciculo = material.id_material_informacional  "
				+" INNER JOIN biblioteca.situacao_material_informacional situacao on material.id_situacao_material_informacional = situacao.id_situacao_material_informacional    ";
	
	
	/** Projeção HQL padrão para as buscas de títulos no acervo, normalmente quando a busca é por número do sistema */
	public static final String PROJECAO_PADRAO_HQL = " t.id, t.idTituloCatalografico, t.numeroDoSistema, " +
	" t.titulo, t.meioPublicacao, t.subTitulo, t.formasVarientesTitulo, t.autor, t.autoresSecundarios, t.assunto, t.localPublicacao, t.editora, t.ano, t.edicao, t.isbn, t.numeroChamada, " +
	" t.idObraDigitalizada, t.notasGerais, t.notaDeConteudo, t.notasLocais, t.serie, t.classificacao1, t.classificacao2, t.classificacao3, t.quantidadeMateriaisAtivosTitulo, " +
	" t.localizacaoEnderecoEletronico, t.catalogado ";
	

	
	/**
	 * <p>Método retorna os dados dos sub campos de um título, juntamente com a indicação de qual campo
	 * e sub campo esse dado pertence.</p>
	 * 
	 * <p>Usado quando se deseja obter todos os dados bibliográficos de um título, sem a necessidade de buscar tudo do banco</p>
	 *
	 * @return uma lista onde cada elemento possui as seguintes informações:
	 *        <ul>
     *        <li>Object[0] = id     (título)   </li>
     *        <li>Object[1] = 245     (campo)   </li>
	 *        <li>Object[2] = a       (subcampo)</li>
     *        <li>Object[3] = "...."  (dados)   </li>
     *	      <li>Object[4] = id do campo de dados, para saber se dois sub campo são do mesmo campo.</li>
     *        <li>Object[5] = posição do campo de dados.</li>
     *        <li>Object[6] = posição do sub campo .</li>
     *        </ul>
	 */
	public List<Object[]> findDadosTitulosCatalografico(List<Integer> idsTitulo) throws DAOException{
		StringBuilder  hql = new StringBuilder(" SELECT t.id, e.tag, sub.codigo, sub.dado, c.id, c.posicao, sub.posicao ");
		hql.append(" FROM TituloCatalografico t ");
		hql.append(" INNER JOIN t.camposDados c");
		hql.append(" INNER JOIN c.etiqueta e");
		hql.append(" INNER JOIN c.subCampos sub");
		hql.append(" WHERE t.id in "+UFRNUtils.gerarStringIn(idsTitulo));
		hql.append(" ORDER BY t.id, c.posicao, sub.posicao ");
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> campos = q.list();
		
		return campos;
		
	}
	
	
	/**
	 * <p>Método retorna os dados dos sub campos de um título, juntamente com a indicação de qual campo
	 * e sub campo esse dado pertence.</p>
	 * 
	 * <p>Usado quando se deseja obter todos os dados bibliográficos de um título, sem a necessidade de buscar tudo do banco</p>
	 *
	 * @return uma lista onde cada elemento possui as seguintes informações:
	 *        <ul>
     *        <li>Object[0] = 245     (campo)   </li>
	 *        <li>Object[1] = a       (subcampo)</li>
     *        <li>Object[2] = "...."  (dados)   </li>
     *	      <li>Object[3] = id do campo de dados, para saber se dois sub campo são do mesmo campo.</li>
     *        <li>Object[4] = posição do campo de dados.</li>
     *        <li>Object[5] = posição do sub campo .</li>
     *        </ul>
	 * 
	 */
	public List<Object[]> findDadosTituloCatalografico(int idTitulo) throws DAOException{
		StringBuilder  hql = new StringBuilder(" SELECT e.tag, sub.codigo, sub.dado, c.id, c.posicao, sub.posicao ");
		hql.append(" FROM TituloCatalografico t ");
		hql.append(" INNER JOIN t.camposDados c");
		hql.append(" INNER JOIN c.etiqueta e");
		hql.append(" INNER JOIN c.subCampos sub");
		hql.append(" WHERE t.id = "+idTitulo);
		hql.append(" ORDER BY c.posicao, sub.posicao ");
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> campos = q.list();
		
		return campos;
		
	}
	
	/**
	 * <p>Método retorna TODOS os dados MARC de um Título</p>
	 * 
	 * <p>Usado geralmente para exibir a catalogação completa do Título no formato MARC</p>
	 *
	 * @return Um título com as informações MARC dos seus campos de controle e campos de dados.
	 */
	public TituloCatalografico findAllDadosMARCTituloCatalografico(int idTitulo) throws DAOException{
		
		TituloCatalografico tituloRetorno = new TituloCatalografico();
		
		StringBuilder  hql = new StringBuilder(" SELECT t.id, cc.id, cc.dado, ec.tag, ec.descricao, cc.posicao, cd.id, ed.tag, ed.descricao, cd.indicador1, cd.indicador2, cd.posicao, sub.id, sub.codigo, sub.dado, sub.posicao, ");
		hql.append(" t.numeroDoSistema, f.sigla, t.formatoMaterial.descricao ");
		hql.append(" FROM TituloCatalografico t ");
		hql.append(" LEFT JOIN t.formatoMaterial f"); // catalogações incompletas não tem formato de material
		hql.append(" INNER JOIN t.camposControle cc");
		hql.append(" INNER JOIN t.camposDados cd");
		hql.append(" INNER JOIN cc.etiqueta ec");
		hql.append(" INNER JOIN cd.etiqueta ed");
		hql.append(" INNER JOIN cd.subCampos sub");
		hql.append(" WHERE t.id = "+idTitulo);
		hql.append(" ORDER BY cc.posicao, cc.posicao, sub.posicao ");
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> camposMARC = q.list(); // Traz as linhas repetidas para cada sub campo de o Título tenha
		
		List<Integer> idsCampoControleAdicionados = new ArrayList<Integer>();
		List<Integer> idsCampoDadosAdicionados = new ArrayList<Integer>();
		List<Integer> idsSubCamposAdicionados = new ArrayList<Integer>();
		
		for (Object[] objects : camposMARC) {
			
			if(tituloRetorno.getId() <= 0){
				tituloRetorno.setId( (Integer) objects[0] );
				tituloRetorno.setNumeroDoSistema((Integer) objects[16]);
				tituloRetorno.setFormatoMaterial( new FormatoMaterial((String) objects[17], (String) objects[18]));
			}
				
			if(  ! idsCampoControleAdicionados.contains( objects[1]) ){ // Se mudou o campo de controle
				tituloRetorno.addCampoControle( new CampoControle((String) objects[2], new Etiqueta((String) objects[3], TipoCatalogacao.BIBLIOGRAFICA, (String) objects[4]), (Integer) objects[5]));
				idsCampoControleAdicionados.add( (Integer) objects[1] );
			}
			
			if( ! idsCampoDadosAdicionados.contains( objects[6]) ){ // Se mudou o campo de dados
				CampoDados c = new CampoDados( new Etiqueta((String) objects[7], TipoCatalogacao.BIBLIOGRAFICA, (String) objects[8]), (Character) objects[9], (Character) objects[10], tituloRetorno, (Integer) objects[11]);
				c.setId((Integer) objects[6]);
				idsCampoDadosAdicionados.add((Integer) objects[6]);
			}
			
			boolean achouCampoDados = false;
			
			for (CampoDados dados : tituloRetorno.getCamposDados()) {
				
				if( ( (Integer)objects[6] ).equals(dados.getId()) && ! idsSubCamposAdicionados.contains( objects[12])){
					achouCampoDados = true;
					new SubCampo( (Character) objects[13], (String) objects[14], dados, (Integer) objects[15]);
					idsSubCamposAdicionados.add( (Integer)objects[12] );
					break;
				}
				
			}
			
			// O título ainda não possuía o campo de dados //
			if( ! achouCampoDados && ! idsSubCamposAdicionados.contains( objects[12]) ){
				CampoDados c = new CampoDados( new Etiqueta((String) objects[7], TipoCatalogacao.BIBLIOGRAFICA), tituloRetorno, (Character) objects[9], (Character) objects[10], (Character) objects[13], (String) objects[14], (Integer) objects[11], (Integer) objects[15]);
				c.setId((Integer) objects[6]);
				idsCampoDadosAdicionados.add((Integer) objects[6]);
				idsSubCamposAdicionados.add( (Integer)objects[12] );
			}
			
		}
		
		return tituloRetorno;
		
	}
	
	/**
	 *   <p>Retorna as informações resumidas do Título (número do sistema, autor e título) do Material passado.</p>
	 *   <p>Utilizado pelo método {@link BibliotecaUtil#obtemDadosResumidosTitulo(int)}</p>
	 */
	public Object findInformacoesResumidasDoTituloDoMaterial(int idMaterial) throws DAOException{

		String hqlExemplar = " Select e.id From Exemplar e where e.id = "+idMaterial;

		Query q1 = getSession().createQuery(hqlExemplar);
		Integer idExemplar = (Integer) q1.uniqueResult();

		if(idExemplar != null){  // Existe exemplar com o id do Material, então o material é exemplar

			hqlExemplar = " SELECT cache.numeroDoSistema, cache.autor, cache.titulo, cache.ano, cache.edicao, cache.localPublicacao, cache.editora "
				+" FROM CacheEntidadesMarc cache "
				+" WHERE cache.idTituloCatalografico = ( SELECT e.tituloCatalografico.id FROM Exemplar e where e.id = "+idMaterial+") ";

			Query q2 = getSession().createQuery(hqlExemplar);
			
			return q2.uniqueResult();

		}else{ // Material é um fascículos

			hqlExemplar = " SELECT cache.numeroDoSistema, cache.autor, cache.titulo, cache.ano, cache.edicao, cache.localPublicacao, cache.editora "
				+" FROM CacheEntidadesMarc cache "
				+" WHERE cache.idTituloCatalografico = (SELECT f.assinatura.tituloCatalografico.id FROM Fasciculo f where f.id = "+idMaterial+") ";

			Query q3 = getSession().createQuery(hqlExemplar);
			return q3.uniqueResult();

		}

	}
	
	
	/**
	 *   <p>Retorna o id do Título do Materail passado.</p>
	 *   <p>Utilizado pelo método {@link BibliotecaUtil#obtemDadosTituloDoMaterial(int)}</p>
	 */
	public Integer findIdTituloDoMaterial(int idMaterial) throws DAOException{

		String hqlExemplar = " Select e.tituloCatalografico.id From Exemplar e where e.id = "+idMaterial;

		Query q1 = getSession().createQuery(hqlExemplar);
		Integer idTituloExemplar = (Integer) q1.uniqueResult();

		if(idTituloExemplar != null){  // Existe exemplar com o id do Material, então o material é exemplar

			return idTituloExemplar;

		}else{ // Material é um fascículos

			hqlExemplar = " SELECT f.assinatura.tituloCatalografico.id FROM Fasciculo f where f.id = "+idMaterial+") ";

			Query q3 = getSession().createQuery(hqlExemplar);
			return (Integer) q3.uniqueResult();

		}
	}

	
	/**
	 *   <p>Retorna as informações resumidas do Título (número do sistema, autor e título) do Material passado.</p>
	 *   <p>Utilizado pelo método {@link BibliotecaUtil#obtemDadosResumidosTitulo(int)}</p>
	 */
	public Object findInformacoesResumidasDoTitulo(int idTitulo) throws DAOException{

		String hql= " Select c.numeroDoSistema, c.autor, c.titulo,  c.ano, c.edicao, c.localPublicacao, c.editora FROM CacheEntidadesMarc c "
			+"WHERE c.idTituloCatalografico = "+idTitulo;

		Query q = getSession().createQuery(hql);
		return q.uniqueResult();
	}

	
	
	/**
	 * Encontra todos os títulos pendentes de exportação
	 */
	public List<EntidadesMarcadasParaExportacao> encontraTitulosPendentesExportacao() throws DAOException{
		String hql = new String( " SELECT e FROM EntidadesMarcadasParaExportacao e "
				+" WHERE e.idTituloCatalografico is not null ");

		Query q = getSession().createQuery(hql);
		q.setMaxResults(100); // Se a quantidade de Títulos pendentes for grande,
		// trás apenas os 100 primeiros

		@SuppressWarnings("unchecked")
		List<EntidadesMarcadasParaExportacao> lista = q.list();
		return lista;
	}

	
	/**
	 * Encontra os títulos pendentes de exportação do usuário passado
	 */
	public List<EntidadesMarcadasParaExportacao> encontraTitulosPendentesExportacaoByUsuario(int idUsusario) throws DAOException{
		String hql = new String( " SELECT e FROM EntidadesMarcadasParaExportacao e "
				+" WHERE e.idTituloCatalografico is not null AND e.idUsuarioMarcouExportacao  = :idUsusario");

		Query q = getSession().createQuery(hql);
		q.setInteger("idUsusario", idUsusario);

		@SuppressWarnings("unchecked")
		List<EntidadesMarcadasParaExportacao> lista = q.list();
		return lista;
	}

	/**
	 *   <p>Método usado para buscar um título e trazer todas as suas informações. Normalmente usado quando
	 * é preciso visualizar as informações completas do título.</p>
	 * 
	 * <p>Reduz o número de conexão que o hibernate precisa fazer com o banco, otimizando assim a consulta.</p>
	 * 
	 * <p>Mesmo assim consulta é pesada e só deve ser usada para editar as informações do Título, onde realmente precisa trazer tudo.</p>
	 */
	public TituloCatalografico findTituloByIdInicializandoDados(Integer id) throws DAOException{

		String hql = new String( " SELECT t FROM TituloCatalografico t "
				+" LEFT JOIN FETCH t.formatoMaterial as formato "  // quando vários títulos são importados, eles são salvos sem formato material
				+" INNER JOIN FETCH t.camposControle as controle "
				+" INNER JOIN FETCH t.camposDados as dados "
				+" INNER JOIN FETCH controle.etiqueta as etiquetaControle "
				+" INNER JOIN FETCH dados.etiqueta as etiqueta "
				+" INNER JOIN FETCH dados.subCampos as subCampos "
				+" WHERE t.id  = :idTitulo");

		Query q = getSession().createQuery(hql);
		q.setInteger("idTitulo", id);
		return  (TituloCatalografico) q.uniqueResult();
	}

	/**
	 *   Método que retorna apenas se o título está ativo ou não no banco.
	 */
	public boolean findSeTituloEstaAtivo(Integer id) throws DAOException{

		String hql = new String( " SELECT t.ativo FROM TituloCatalografico t "
				+" WHERE t.id  = :idTitulo");

		Query q = getSession().createQuery(hql);
		q.setInteger("idTitulo", id);
		return   (Boolean) q.uniqueResult();

	}

	/**
	 *   Retorna o formato de material do Título.
	 */
	public FormatoMaterial findFormatoMaterialTitulo(Integer id) throws DAOException{

		String hql = new String( " SELECT t.formatoMaterial FROM TituloCatalografico t "
				+" WHERE t.id  = :idTitulo");

		Query q = getSession().createQuery(hql);
		q.setInteger("idTitulo", id);
		return  (FormatoMaterial) q.uniqueResult();
	}


	/**
	 *    Encontra todos os títulos com catalogações incompletas do sistema.
	 */
	public List<CacheEntidadesMarc> findAllTitulosComCatalogacaoIncompleta() throws DAOException{

		return findAllTitulosComCatalogacaoIncompletaDoUsuario(null);

	}

	/**
	 *    Encontra todos os títulos com catalogações incompletas do sistema realizadas pelo
	 *    usuário passado.
	 */
	public List<CacheEntidadesMarc> findAllTitulosComCatalogacaoIncompletaDoUsuario(int idUsuario) throws DAOException{
		return findAllTitulosComCatalogacaoIncompletaDoUsuario((Integer)idUsuario);
	}

	/**
	 *    Encontra todos os títulos com catalogações incompletas do sistema realizadas pelo
	 *    usuário passado.
	 */
	private List<CacheEntidadesMarc> findAllTitulosComCatalogacaoIncompletaDoUsuario(Integer idUsuario) throws DAOException{

		StringBuilder sql = new StringBuilder( 
				" SELECT  " +
				" 	 cem.id_cache_entidades_marc, " +
				"	 cem.id_titulo_catalografico, " +
				"	 cem.numero_do_sistema, " +
				"	 cem.titulo, " +
				"	 cem.sub_titulo, " +
				"	 cem.autor, " +
				"	 cem.numero_chamada, " +
				"	 cem.id_obra_digitalizada, " +
				"	 cem.ano_publicacao, " +
				"	 cem.quantidade_materiais_ativos_titulo, " +
				"	 cem.catalogado, " +
				"	 t.importado,  " +
				"	 pessoa.nome  " +
				" FROM " +
				"	 biblioteca.cache_entidades_marc cem " +
				"	 INNER JOIN biblioteca.titulo_catalografico t ON cem.id_titulo_catalografico = t.id_titulo_catalografico " +
				"    INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = t.id_registro_criacao " + 
				"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " + 
				"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " + 
				" WHERE " +
				"	 cem.catalogado = falseValue() ");
		
		if (idUsuario != null) {
			sql.append("	 AND usuario.id_usuario = :idUsuario ");
		}

		Query q = getSession().createSQLQuery(sql.toString());

		if (idUsuario != null) {
			q.setInteger("idUsuario", idUsuario);
		}

		@SuppressWarnings("unchecked")
		List<Object> dados = q.list();
		
		List<CacheEntidadesMarc> caches = new ArrayList<CacheEntidadesMarc>();
		
		for (Object dado : dados) {
			
			Object[] cacheTemp = (Object[]) dado;
 			
			CacheEntidadesMarc cache = new CacheEntidadesMarc();
			
			cache.setId( (Integer) cacheTemp[0]);
			cache.setIdTituloCatalografico( (Integer) cacheTemp[1]);
			cache.setNumeroDoSistema( (Integer) cacheTemp[2]);
			cache.setTitulo( (String) cacheTemp[3]);
			cache.setSubTitulo( (String) cacheTemp[4]);
			cache.setAutor( (String) cacheTemp[5]);
			cache.setNumeroChamada( (String) cacheTemp[6]);
			cache.setIdObraDigitalizada( (Integer) cacheTemp[7]);
//			cache.setAnoPublicacao( (Integer) cacheTemp[8]);
			cache.setQuantidadeMateriaisAtivosTitulo( (Integer) cacheTemp[9]);
			cache.setCatalogado( (Boolean) cacheTemp[10]);
			cache.setImportado( (Boolean) cacheTemp[11]);
			cache.setNomeUsuario( (String) cacheTemp[12]);
			
			caches.add(cache );
			
		}

		return  caches;
	}


	/**
	 *    Encontra todos os títulos com catalogações incompletas do sistema.
	 */
	public long countAllTitulosComCatalogacaoIncompleta() throws DAOException{

		StringBuilder hql = new StringBuilder( " SELECT count (t.id) ");
		hql.append(" FROM CacheEntidadesMarc t ");
		hql.append(" WHERE t.idTituloCatalografico is not null ");
		hql.append(" AND t.catalogado = falseValue() ");

		Query q = getSession().createQuery(hql.toString());
		return  (Long) q.uniqueResult();
	}

	
	/**
	 *    Método que busca as editoras cadastras no SIPAC.
	 */
	public List<Editora> buscaEditorasSIPAC(String denominacaoEditora) throws SQLException {
		String sql = " SELECT id, denominacao FROM requisicoes.editora WHERE denominacao like ? AND ativa = trueValue() ORDER BY denominacao asc "
			+BDUtils.limit(100);

		ArrayList<Editora> editoras = new ArrayList<Editora>();

		Connection con = null;

		try{

			con = Database.getInstance().getSipacConnection();

			PreparedStatement prepared = con.prepareStatement(sql);
			prepared.setString(1, "%"+denominacaoEditora.toUpperCase()+"%");

			ResultSet resul = prepared.executeQuery();


			while(resul.next()){
				int id = resul.getInt(1);

				String temp = resul.getString(2);
				StringBuilder denominacaoTemp = new StringBuilder();
				if(StringUtils.notEmpty(temp)){

					temp = temp.toLowerCase();  // todo par minúsculo

					String[] string = temp.split("\\s"); // separa pelos espaços

					for (String s : string) {

						if(s.length() > 2)
							denominacaoTemp.append(org.apache.commons.lang.StringUtils.capitalize(s)+" ");
						else{
							denominacaoTemp.append(s+" ");
						}
					}

				}
				editoras.add(new Editora(id, denominacaoTemp.toString()));
			}

			return editoras;

		}finally{
			if(con != null) con.close();
		}
	}



	
	/**
	 * Registra as alterações de um título na tabela historico_alteracao_titulo
	 */
	public void registraAlteracaoTitulo(TituloCatalografico titulo, boolean atualizando) {

		String sql = " INSERT INTO biblioteca.historico_alteracao_titulo "
			+"(id_historico_alteracao_titulo, id_titulo, id_registro_entrada, descricao_operacao, data_operacao)"
			+" VALUES ( nextval('biblioteca.historicos_alteracao_sequence'), ?, ?, ?, ?)";

				int idTitulo = titulo.getId();
				int idRegistroEntrada = 0;

				if(atualizando)
					idRegistroEntrada = titulo.getRegistroUltimaAtualizacao().getId();
				else
					idRegistroEntrada = titulo.getRegistroCriacao().getId();

				update(sql, idTitulo, idRegistroEntrada, titulo.toString(), new java.sql.Timestamp( new Date().getTime()) );
	}

	/**
	 * Método que retorna o número de chamada do título. ( o dado do campo 090$a, 090$a, 090$c, 090$d)
	 */
	public String encontraNumeroChamadaTitulo(int idTitulo) throws DAOException{

		StringBuilder hql = new StringBuilder( " SELECT t.numeroChamada FROM CacheEntidadesMarc t WHERE  "
				+" t.idTituloCatalografico is not null AND t.idTituloCatalografico = :idTitulo");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idTitulo", idTitulo);
		return (String) q.uniqueResult();
	}




	/**
	 * Retorna uma lista de Modificações de um título, geralmente usado no histórico de
	 * alterações ou na visualização da alterações na página de catalogação.
	 */
	public List <Object []> findAlteracoesByTituloPeriodo (int idTitulo, Date dataInicio, Date dataFim, Integer limiteResultados) throws DAOException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String sql = "select p.nome, h.descricao_operacao, h.data_operacao " +
		"from biblioteca.historico_alteracao_titulo h " +
		"join comum.registro_entrada r on r.id_entrada = h.id_registro_entrada " +
		"join comum.usuario u on u.id_usuario = r.id_usuario " +
		"join comum.pessoa p on p.id_pessoa = u.id_pessoa " +
		"where h.id_titulo = " + idTitulo;

		if (dataInicio != null)
			sql += " and data_operacao >= '"+sdf.format(dataInicio)+"' ";
		if (dataFim != null)
			sql += "and data_operacao <= '"+sdf.format(dataFim)+" 23:59:59' ";

		sql += "order by data_operacao desc";

		if(limiteResultados != null){
			sql += BDUtils.limit(limiteResultados);
		}
		
		@SuppressWarnings("unchecked")
		List <Object []> lista = getSession().createSQLQuery(sql).list();
		return lista;
	}




	/**
	 *     Encontra a última data na qual o título foi alterado.
	 * 
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public Date findUltimaDataAlteracaoTitulo (int idTitulo) throws DAOException {

		String sql = "select data_operacao from biblioteca.historico_alteracao_titulo where id_titulo = "+idTitulo+" order by data_operacao desc " + BDUtils.limit(1);

		return (Date) getSession().createSQLQuery(sql).uniqueResult();
	}


	/**
	 * Encontra os títulos entre os número do sistema passado
	 *
	 * @param numeroSistema
	 * @return
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> findByNumerosSistema(Integer primeiroNumero, Integer ultimoNumero) throws DAOException{

		StringBuilder hqlSelect = new StringBuilder( " SELECT DISTINCT "+PROJECAO_PADRAO_HQL+ " FROM CacheEntidadesMarc t WHERE t.numeroDoSistema between :primeiroNumero AND :ultimoNumero AND idTituloCatalografico is not null " );

		Query q = getSession().createQuery(hqlSelect.toString());
		q.setInteger("primeiroNumero", primeiroNumero);
		q.setInteger("ultimoNumero", ultimoNumero);

		
		
		@SuppressWarnings("unchecked")
		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(HibernateUtils.parseTo( q.list(), PROJECAO_PADRAO_HQL, CacheEntidadesMarc.class, "t")  );
		return lista;
	}

	/**
	 * Conta quantos títulos existem entre a faixa de números do sistema passados
	 *
	 * @param numeroSistema
	 * @return
	 * @throws DAOException
	 */
	public Long countByNumerosSistema(Integer primeiroNumero, Integer ultimoNumero) throws DAOException{

		StringBuilder hqlSelect = new StringBuilder( " SELECT  COUNT(DISTINCT t) FROM CacheEntidadesMarc t WHERE t.numeroDoSistema between :primeiroNumero AND :ultimoNumero AND idTituloCatalografico is not null " );

		Query q = getSession().createQuery(hqlSelect.toString());
		q.setInteger("primeiroNumero", primeiroNumero);
		q.setInteger("ultimoNumero", ultimoNumero);

		return (Long) q.uniqueResult();

	}

	/**
	 * Encontra um título resumido pelo número do sistema
	 *
	 * @param numeroSistema
	 * @return
	 * @throws DAOException
	 */
	public CacheEntidadesMarc findByNumeroSistema(Integer numeroDoSistema) throws DAOException{

		StringBuilder hqlSelect = new StringBuilder( " SELECT DISTINCT "+PROJECAO_PADRAO_HQL + " FROM CacheEntidadesMarc t  WHERE t.numeroDoSistema = :numeroDoSistema "
				+" AND idTituloCatalografico is not null");

		Query q = getSession().createQuery(hqlSelect.toString());
		q.setInteger("numeroDoSistema", numeroDoSistema);

		@SuppressWarnings("unchecked")
		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(HibernateUtils.parseTo( q.list(), PROJECAO_PADRAO_HQL, CacheEntidadesMarc.class, "t")  );

		if(lista.size() > 0){
			return lista.get(0);
		}else{
			return null;
		}
		
	}
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// BUSCA MULTI-CAMPO ///////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Método que implementa a busca que o Aleph chama de multi-campo.
	 * 
	 * @param titulo
	 * @param assunto
	 * @param autor
	 * @param Local
	 * @param Editora
	 * @param anoInicial
	 * @param anoFinal
	 * @param publica indica se a busca é publica ou não. se for deve trazer apenas título que possuam materiais ativos.
	 * @param idsBibliotecasAcervoPublico  ids das biblioteca que possuem acervo publica, apenas materiais dessas biblioteca devem ser retornados
	 * @return uma lista de títulos resumidos
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> buscaMultiCampo(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao, 
			String titulo, String assunto,
			String autor, String localPublicacao, String editora, String classificacao1, String classificacao2, String classificacao3, Integer anoInicial, Integer anoFinal,
			Integer idBiblioteca, Integer idColecao, Integer idTipoMaterial, Integer idStatus,
			boolean buscaPublica, List<Integer> idsBibliotecasAcervoPublico, boolean buscaMobile,  boolean utilizarBuscaRemissiva) throws DAOException{
		
		Connection con = null;

		List<CacheEntidadesMarc> titulosResultadosTemp = new ArrayList<CacheEntidadesMarc>();

		List<String> listaDeParametros = new ArrayList<String>(); // Usada para guardar os parâmetros da consulta sql

		long tempo = System.currentTimeMillis();
		
		if( idBiblioteca == null ) idBiblioteca = -1;
		if( idColecao == null ) idColecao = -1;
		if( idTipoMaterial == null ) idTipoMaterial = -1;
		if( idStatus == null ) idStatus = -1;
		
		boolean filtroSobreMateriais = idBiblioteca != -1  || idColecao != -1 || idTipoMaterial != -1 || idStatus != -1 || (idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0 ) ;
		
		boolean naoInformouNenhumCriterio = StringUtils.isEmpty(titulo) && StringUtils.isEmpty(assunto)
		&&StringUtils.isEmpty(autor) && StringUtils.isEmpty(localPublicacao) &&  StringUtils.isEmpty(editora)
		&& StringUtils.isEmpty(classificacao1) && StringUtils.isEmpty(classificacao2) && StringUtils.isEmpty(classificacao3) &&  anoInicial == null &&  anoFinal == null
		&& idBiblioteca <= 0 && idColecao<= 0 && idTipoMaterial<= 0 && idStatus <= 0;
		
		try{

			con = Database.getInstance().getSigaaConnection();


			StringBuilder sqlSelect = new StringBuilder();
			sqlSelect.append( SELECT_PADRAO  );

			if(filtroSobreMateriais){
				sqlSelect.append(INNER_JOIN_EXEMPLARES);
			}

			
			sqlSelect.append(" WHERE t.id_titulo_catalografico is not null ");

			
			// BUSCA PUBLICA DO SISTEMA
			if(buscaPublica){
				sqlSelect.append(" AND  t.quantidade_materiais_ativos_titulo > 0 ");
			
				
				
				
				if((idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0 )){
					sqlSelect.append(" AND (   material.id_biblioteca in "+UFRNUtils.gerarStringIn(idsBibliotecasAcervoPublico)+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
				}
			}
			
			
			
			sqlSelect.append(montaSqlComumBuscaMultiCampo( con, geradorPesquisa, titulo, assunto, autor, localPublicacao,  editora, classificacao1, classificacao2, classificacao3, anoInicial
															,  anoFinal, idBiblioteca,  idColecao,  idTipoMaterial,   idStatus, listaDeParametros, utilizarBuscaRemissiva, false));


			if(naoInformouNenhumCriterio){
				sqlSelect.append(" AND 1 = 0 ");
			}
			
			/* *****************************************************************************************
			 *    Se usuário escolheu algum filtro sobre material tem que realizar a mesma busca para  *
			 * os Fascículos                                                                           *
			 *******************************************************************************************/
			if(filtroSobreMateriais){
				
				sqlSelect.append( " UNION ALL ( " );

				sqlSelect.append( SELECT_PADRAO);

				sqlSelect.append( INNER_JOIN_FASCICULOS );

				sqlSelect.append(" WHERE t.id_titulo_catalografico is not null ");

				// BUSCA PUBLICA DO SISTEMA
				if(buscaPublica){
					sqlSelect.append(" AND  t.quantidade_materiais_ativos_titulo > 0 ");
				
					if((idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0 )){
						sqlSelect.append(" AND (  material.id_biblioteca in "+ UFRNUtils.gerarStringIn(idsBibliotecasAcervoPublico)+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
					}
					
				}
				
				
				sqlSelect.append(montaSqlComumBuscaMultiCampo( con, geradorPesquisa, titulo, assunto, autor, localPublicacao,  editora, classificacao1, classificacao2, classificacao3, anoInicial
											,  anoFinal, idBiblioteca,  idColecao,  idTipoMaterial,  idStatus, listaDeParametros, utilizarBuscaRemissiva, false));

				if(naoInformouNenhumCriterio){
					sqlSelect.append(" AND 1 = 0 ");
				}
				
				sqlSelect.append( " ) " ); // fecha o UNION
			}

			
			
			/**	A busca pública  deve trazer as catalogações que não possuirem materiais mais, que tenham algum endereço eletrônico.
 			 * 
			 * Isso é uma solução provisória para recuperar as catalogações que só possuem materiais digitais. Sem nenhum material físico. Num futuro deve ser criado
			 * no sistema o contexto de material digital. */
			if(buscaPublica){
				
				sqlSelect.append( " UNION ALL ( " );

				sqlSelect.append( SELECT_PADRAO);
				
				sqlSelect.append(" WHERE t.id_titulo_catalografico is not null ");
				sqlSelect.append(" AND ( t.quantidade_materiais_ativos_titulo = 0 AND ( t.localizacao_endereco_eletronico IS NOT NULL AND t.localizacao_endereco_eletronico != '' )  ) ");
				
				sqlSelect.append(montaSqlComumBuscaMultiCampo( con, geradorPesquisa, titulo, assunto, autor, localPublicacao,  editora, classificacao1, classificacao2, classificacao3, anoInicial
						,  anoFinal, idBiblioteca,  idColecao,  idTipoMaterial,  idStatus, listaDeParametros, utilizarBuscaRemissiva, true));

				if(naoInformouNenhumCriterio){
				sqlSelect.append(" AND 1 = 0 ");
				}
				
				sqlSelect.append( " ) " ); // fecha o UNION
			}	
			
			// ORDENA OR RESULTADOS
			if(campoOrdenacao != null)
				sqlSelect.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			// LIMITE NOS RESULTADOS DAS PESQUISAS
			sqlSelect.append( BDUtils.limit(    ( buscaMobile ? LIMITE_BUSCA_TITULOS_MOBILE :   ( buscaPublica ? LIMITE_BUSCA_TITULOS_PUBLICA : LIMITE_BUSCA_TITULOS )    )   ) );
	
			System.out.println("Parametros >>>");
			for (String  string: listaDeParametros) {
				System.out.println(string);
			}
			
			System.out.println(sqlSelect.toString());
			
			PreparedStatement prepared = con.prepareStatement(sqlSelect.toString());

			for (int posicaoParametro = 1; posicaoParametro <= listaDeParametros.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaDeParametros.get(posicaoParametro-1));
			}

			titulosResultadosTemp =  montaResultadosConsulta(prepared.executeQuery());

			System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
			System.out.println("Consulta demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");

		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}
		}

		return titulosResultadosTemp;

	}


	
	/**
	 * Monta a parte comum do SQL a ser usado na busca multi campo, é comum pois caso o usuário escolha
	 * algum filtro sobre os materiais é preciso realizar uma busca para exemplares e outra para fascículos.
	 */
	private String montaSqlComumBuscaMultiCampo(Connection con, GeraPesquisaTextual gerador, String titulo, String assunto,
						String autor, String localPublicacao, String editora, String classificacao1, String classificacao2, String classificacao3, Integer anoInicial, Integer anoFinal,
						Integer idBiblioteca, Integer idColecao, Integer idTipoMaterial, Integer idStatus, List<String> listaParametos,  boolean utilizarBuscaRemissiva, boolean buscandoCatalogacoesSemMateriais) throws DAOException{

		StringBuilder sqlSelectTemp = new StringBuilder();

		boolean filtraSobreBiblioteca = ! new Integer(-1).equals(idBiblioteca);
		boolean filtraSobreColecao = ! new Integer(-1).equals(idColecao);
		boolean filtraSobreTipoMaterial = ! new Integer(-1).equals(idTipoMaterial);
		boolean filtraSobreStatus = ! new Integer(-1).equals(idStatus);
		
		if(! StringUtils.isEmpty(titulo)){
			String[] titulosBusca = BibliotecaUtil.retornaPalavrasBusca(titulo); //separa os nomes pelo espaço

			if(titulosBusca.length == 0)
				sqlSelectTemp.append(" AND ( 1 = 0 ) "); // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
			else{
				listaParametos.add( gerador.formataTextoPesquisaTextual(titulosBusca) );
				sqlSelectTemp.append(" AND (  "+ gerador.gerarMecanismoPesquisaTextual("outras_informacoes_titulo_ascii") +" ) " );
			}
		}

		if(! StringUtils.isEmpty(assunto)){
			sqlSelectTemp.append(  montaAssuntoBuscaMultiCampo(con, gerador, assunto, listaParametos, utilizarBuscaRemissiva));
		}

		if(! StringUtils.isEmpty(autor)){
			sqlSelectTemp.append(  montaAutorBuscaMultiCampo(con, gerador, autor, listaParametos, utilizarBuscaRemissiva));
		}

		if(! StringUtils.isEmpty(localPublicacao)){
			String[] locaisBusca = BibliotecaUtil.retornaPalavrasBusca(localPublicacao); //separa os nomes pelo espaço

			if(locaisBusca.length == 0)
				sqlSelectTemp.append(" AND ( 1 = 0 ) "); // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
			else{
				listaParametos.add( gerador.formataTextoPesquisaTextual(locaisBusca) );
				sqlSelectTemp.append(" AND (  "+ gerador.gerarMecanismoPesquisaTextual("local_publicacao_ascii") +" ) " );
			}
			
		}

		if(! StringUtils.isEmpty(editora)){
			String[] editorasBusca = BibliotecaUtil.retornaPalavrasBusca(editora); //separa os nomes pelo espaço

			if(editorasBusca.length == 0)
				sqlSelectTemp.append(" AND ( 1 = 0 ) "); // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
			else{
				listaParametos.add( gerador.formataTextoPesquisaTextual(editorasBusca) );
				sqlSelectTemp.append(" AND (  "+ gerador.gerarMecanismoPesquisaTextual("editora_ascii") +" ) " );
			}
			
		}

		if(! StringUtils.isEmpty(classificacao1)){
			listaParametos.add(  gerador.formataTextoPesquisaInicioPalavra(classificacao1));
			sqlSelectTemp.append(" AND ( "+gerador.gerarMecanismoPesquisaInicioPalavra("classificacao_1_ascii")+" ) " );
		}
		
		if(! StringUtils.isEmpty(classificacao2)){
			listaParametos.add(  gerador.formataTextoPesquisaInicioPalavra(classificacao2));
			sqlSelectTemp.append(" AND ( "+gerador.gerarMecanismoPesquisaInicioPalavra("classificacao_2_ascii")+" ) " );
		}
		
		if(! StringUtils.isEmpty(classificacao3)){
			listaParametos.add(  gerador.formataTextoPesquisaInicioPalavra(classificacao3));
			sqlSelectTemp.append(" AND ( "+gerador.gerarMecanismoPesquisaInicioPalavra("classificacao_3_ascii")+" ) " );
		}
		
		
		if(anoInicial != null && anoFinal != null){
			sqlSelectTemp.append(" AND ( t.ano_publicacao between "+anoInicial+" AND "+anoFinal+" ) ");
		}else{
			if(anoInicial != null){
				sqlSelectTemp.append(" AND ( t.ano_publicacao >= "+anoInicial+") ");
			}else{
				if(anoFinal != null){
					sqlSelectTemp.append(" AND ( t.ano_publicacao <= "+anoFinal+" ) ");
				}
			}
		}

		/////////////////////// Filtros Sobre os Materiais ///////////////////////////

		if(! buscandoCatalogacoesSemMateriais){
			if(filtraSobreBiblioteca) sqlSelectTemp.append(" AND (      ( material.id_biblioteca = "+idBiblioteca+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
			if(filtraSobreColecao) sqlSelectTemp.append(" AND (         ( material.id_colecao = "+idColecao+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
			if(filtraSobreTipoMaterial) sqlSelectTemp.append(" AND (    ( material.id_tipo_material = "+idTipoMaterial+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
			if(filtraSobreStatus) sqlSelectTemp.append(" AND (    ( material.id_status_material_informacional = "+idStatus+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
			
			
			if(filtraSobreBiblioteca)   sqlSelectTemp.append(" ) ");
			if(filtraSobreColecao)      sqlSelectTemp.append(" ) ");
			if(filtraSobreTipoMaterial) sqlSelectTemp.append(" ) ");
			if(filtraSobreStatus) sqlSelectTemp.append(" ) ");

		}
		////////////////////////////////////////////////////////////////////////////////

		return sqlSelectTemp.toString();
	}
	
	
	
	
	/**
	 * Monta o SQL para a consulta de autores usado na busca multi-campo, já considerando as entradas remissivas
	 */
	private String  montaAutorBuscaMultiCampo(Connection con, GeraPesquisaTextual gerador, String autor,  List<String> listaParametos, boolean utilizarBuscaRemissiva) throws DAOException{

		StringBuilder sqlSelectTemp = new StringBuilder();

		List<String> autoresAutorizados = new ArrayList<String>();



		// REALIZA A BUSCA POR AUTORES REMISSIVOS //

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Palavras muito pequenas não faz a busca remissiva. Como a busca é por 'like' vai trazer  //
		// praticamente toda a base de autoridades.             //
		//////////////////////////////////////////////////////////////////////////////////////////////
		if( autor.length() > 3 && utilizarBuscaRemissiva){
			autoresAutorizados = buscaTitulosPorAutoresRemissivos(con, gerador, autor); // entradas autorizadas do nome digitado
		}


		/* **************************************************************************************
		 * vamos supor:
		 *    buscou por : "João da Regras"
		 *
		 *    achou como entrada autorizadas: "Assis, Machado de."
		 *                                    "Tal, Fulano de."
		 *
		 *  então vai gerar a consulta:
		 *    AND(
		 *        (  t.autorAscii  like "Assis, Machado de."  )
		 *          OR ( t.autorAscii  like "Tal, Fulano de." )
		 *         )
		 * 
		 * 
		 *   OBs.: busca pela entrada autorizada inteira, não quebra em palavras
		 * 
		 *   E mais o próprio autor digitado:
		 * 
		 *  	   OR (
		 *         (t.autorAscii like "João") AND (t.autorAscii like "Regras")
		 *      )
		 *   )
		 *
		 *   OBs.: aqui quebra em palavras e faz um 'AND' entre elas
		 *
		 ************************************************************************************** */


		String[] autorBusca = BibliotecaUtil.retornaPalavrasBusca(autor); //separa os nomes pelo espaço

		if(autorBusca.length == 0) {
			sqlSelectTemp.append(" AND ( 1 = 0 ) "); // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
			return sqlSelectTemp.toString();
		}
		
		
		
		if(autorBusca.length > 0)
			sqlSelectTemp.append(" AND ( ");  // abre toda a busca de autores

		
		////////////////////////////// busca autores autorizados //////////////////////////////
		
		if(autoresAutorizados.size() > 0){

			for (int i = 0; i < autoresAutorizados.size(); i++) {
				
				String autorAutorizado = autoresAutorizados.get(i);
				
				autorAutorizado = CatalogacaoUtil.retiraPontuacaoCamposBuscas(autorAutorizado);
			
				if(i != 0 )
					sqlSelectTemp.append(" OR ( ");
				
				listaParametos.add( gerador.formataTextoPesquisaTextual(autorAutorizado));
				sqlSelectTemp.append( gerador.gerarMecanismoPesquisaTextual("autor_ascii") );
				
				if(i != 0 )
					sqlSelectTemp.append(" ) ");
				
			}
			
			for (int i = 0; i < autoresAutorizados.size(); i++) {
				
				String autorAutorizado = autoresAutorizados.get(i);
				
				autorAutorizado = CatalogacaoUtil.retiraPontuacaoCamposBuscas(autorAutorizado);
			
				sqlSelectTemp.append(" OR ( "); // Sempre tem or porque se tem essa busca, sempre tem antes a busca de autor autorizado
				
				listaParametos.add( gerador.formataTextoPesquisaTextual(autorAutorizado));
				sqlSelectTemp.append( gerador.gerarMecanismoPesquisaTextual("autores_secundarios_ascii") );
				
		
				sqlSelectTemp.append(" ) ");
				
			}
			
		}
		/////////////////////////////////////////////////////////////////////////////////
		
		
		
		////////////////////// busca dos autores  ////////////////////

		if(autoresAutorizados.size() > 0 && autorBusca.length > 0)   // se tiver autor autorizado faz um "OR" com o autor normal da busca
			sqlSelectTemp.append(" OR ( ");
		else
			if(autorBusca.length > 0 )
				sqlSelectTemp.append(" ( ");

			listaParametos.add( gerador.formataTextoPesquisaTextual(autorBusca) );
			sqlSelectTemp.append(gerador.gerarMecanismoPesquisaTextual("autor_ascii"));
		
		if(autorBusca.length > 0 )
			sqlSelectTemp.append(" ) ");  // fecha o próprio autor digitado
		
		////////////////////////////////////////////////////////////////////////
		
		
		
		
		////////////////// busca os autores secundários também ///////////////////////
		if(autorBusca.length > 0 )
			sqlSelectTemp.append(" OR ( ");   // sempre tem alguma coisa antes


		listaParametos.add( gerador.formataTextoPesquisaTextual(autorBusca) );
		sqlSelectTemp.append( gerador.gerarMecanismoPesquisaTextual("autores_secundarios_ascii"));
		
		if(autorBusca.length > 0 )
			sqlSelectTemp.append(" ) ");

		////////////////////////////////////////////////////////////////////////
		
		

		if(autorBusca.length > 0){
			sqlSelectTemp.append(" ) ");  // fecha toda a busca de autores
		}

		return sqlSelectTemp.toString();
	}

	
	
	
	/**
	 * Monta o SQL para a consulta de assuntos usado na busca multicampo, já considerando as entradas remissivas
	 */
	private String montaAssuntoBuscaMultiCampo(Connection con, GeraPesquisaTextual gerador, String assunto,  List<String> listaParametos,  boolean utilizarBuscaRemissiva) throws DAOException{

		List<String> assuntosAutorizados = new ArrayList<String>();

		StringBuilder sqlSelectTemp = new StringBuilder();

		// REALIZA A BUSCA POR ASSUNTOS REMISSIVOS //

		//////////////////////////////////////////////////////////////////////////////////////////////
		// palavras muito pequenas não faz a busca remissiva. Como a busca é por 'like' vai trazer  //
		// praticamente toda a base de autoridades.           //
		//////////////////////////////////////////////////////////////////////////////////////////////
		if( assunto.length() > 3 && utilizarBuscaRemissiva){
			assuntosAutorizados = buscaTitulosPorAssuntoRemissivos(con, gerador, assunto); // entradas autorizadas do nome digitado
		}


		/* **************************************************************************************
		 * vamos supor:
		 *    buscou por : "City crime"
		 *
		 *    achou como entrada autorizadas: "Crime e criminosos."
		 *                                    "cidade do crime."
		 *
		 *  então vai gerar a consulta:
		 *    AND(
		 *       (   t.assuntoAscii  like "Crime e criminosos."  )
		 *          OR ( t.assuntoAscii  like "cidade do crime." )
		 *       )
		 * 
		 *   OBs.: busca pela entrada autorizada inteira, não quebra em palavras
		 * 
		 *   E mais o próprio assunto digitado:
		 * 
		 *     ( OR (
		 *       (t.assuntoAscii like "City")
		 *       AND (t.assuntoAscii like "crime")
		 *     )
		 *   )
		 *
		 *   OBs.: aqui quebra em palavras e faz um 'AND' entre elas
		 *
		 ************************************************************************************** */


		String[] assuntoBusca = BibliotecaUtil.retornaPalavrasBusca(assunto); //separa os nomes pelo espaço

		if(assuntoBusca.length == 0){
			sqlSelectTemp.append(" AND ( 1 = 0) "); // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
			return sqlSelectTemp.toString();
		}

		if(assuntoBusca.length > 0)
			sqlSelectTemp.append(" AND ( ");  // abre toda a busca de assuntos
			
		
	
		////////////////////////////// busca assuntos autorizados //////////////////////////////
		
		if(assuntosAutorizados.size() > 0){

			for (int i = 0; i < assuntosAutorizados.size(); i++) {
				
				String assuntoAutorizado = assuntosAutorizados.get(i);
				
				assuntoAutorizado = CatalogacaoUtil.retiraPontuacaoCamposBuscas(assuntoAutorizado);
			
				if(i != 0 )
					sqlSelectTemp.append(" OR ( ");
				
				listaParametos.add( gerador.formataTextoPesquisaTextual(assuntoAutorizado));
				sqlSelectTemp.append( gerador.gerarMecanismoPesquisaTextual("assunto_ascii") );
				
				if(i != 0 )
					sqlSelectTemp.append(" ) ");
				
			}
			
		}
		/////////////////////////////////////////////////////////////////////////////////
		
		
		
		
		
		//////////////////////busca dos autores  ////////////////////

		if(assuntosAutorizados.size() > 0 && assuntoBusca.length > 0)   // se tiver autor autorizado faz um "OR" com o autor normal da busca
			sqlSelectTemp.append(" OR ( ");
		else
			if(assuntoBusca.length > 0 )
				sqlSelectTemp.append(" ( ");

			listaParametos.add( gerador.formataTextoPesquisaTextual(assuntoBusca) );
			sqlSelectTemp.append(gerador.gerarMecanismoPesquisaTextual("assunto_ascii"));
		
		if(assuntoBusca.length > 0 )
			sqlSelectTemp.append(" ) ");  // fecha o próprio autor digitado
		
		////////////////////////////////////////////////////////////////////////
		
		
		
		if(assuntoBusca.length > 0){
			sqlSelectTemp.append(" ) ");  // fecha toda a busca de assunto
		}

		return sqlSelectTemp.toString();
	}
	
	
	
	
	
	/**
	 *    Método que implementa a busca pública do sistema. A diferença aqui é que títulos que não
	 * possuem materiais não devem aparecer nos resultados.
	 * 
	 * @param titulo
	 * @param assunto
	 * @param autor
	 * @param Local
	 * @param Editora
	 * @param anoInicial
	 * @param anoFinal
	 * @param idsBibliotecasAcervoPublico  ids das biblioteca que possuem acervo publica, apenas materiais dessas biblioteca devem ser retornados
	 * @return uma lista de títulos resumidos
	 */
	public List<CacheEntidadesMarc> buscaMultiCampoPublica(GeraPesquisaTextual geradorPesquisa,  CampoOrdenacaoConsultaAcervo campoOrdenacao,
			String titulo, String assunto, String autor, String localPublicacao, String editora, Integer anoInicial, Integer anoFinal
			, Integer idBiblioteca, Integer idColecao, Integer idTipoMaterial, boolean buscaMobile, List<Integer> idsBibliotecasAcervoPublico) throws DAOException{

		return buscaMultiCampo(geradorPesquisa, campoOrdenacao, titulo, assunto, autor, localPublicacao, editora, null, null, null, anoInicial, anoFinal, idBiblioteca, idColecao, idTipoMaterial, -1, true, idsBibliotecasAcervoPublico, buscaMobile, true);

	}

	/**
	 *     Implementa uma busca simplificada por títulos para a parte Mobile da biblioteca.
	 *  Diferente da busca multi campo do sistema web, o título e autor são obrigatórios para limitar o número
	 *  de resultados porque a tela do celular é pequena.
	 * 
	 *     Os campos são Título (245$a),  Autor (100$a).
	 * 
	 *     Para autor
	 *
	 * @return uma lista de títulos resumidos
	 */
	public List<CacheEntidadesMarc> buscaMultiCampoMobile(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao, 
			String titulo, String autor,  List<Integer> idsBibliotecasAcervoPublico) throws DAOException{

		return buscaMultiCampoPublica(geradorPesquisa, campoOrdenacao, titulo, null, autor, null, null, null, null, -1, -1, -1, true, idsBibliotecasAcervoPublico);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////  BUSCA AVANÇADA   ///////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 *        Método que implementa a busca avançada do sistema.
	 *
	 * @param campo1  o valor digitado pelo usuário no campo 1
	 * @param campo2  o valor digitado pelo usuário no campo 2
	 * @param campo3  o valor digitado pelo usuário no campo 3
	 * @param conexao1  se é para fazer um E OU ou NOR com o campo 2
	 * @param conexao2  se é para fazer um E OU ou NOR com o campo 2
	 * @param tipoCampo1 qual o tipo do campo da busca (autor, título, edição, id da biblioteca, id da coleção, etc...)
	 * @param tipoCampo2 qual o tipo do campo da busca (autor, título, edição, id da biblioteca, id da coleção, etc...)
	 * @param tipoCampo3 qual o tipo do campo da busca (autor, título, edição, id da biblioteca, id da coleção, etc...)
	 * @return
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> buscaAvancada(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao
			, List<CampoPesquisaAvancada> campos, boolean buscaPublica,  List<Integer> idsBibliotecasAcervoPublico, boolean utilizarBuscaRemissiva) throws DAOException{

		Connection con = null;

		List<CacheEntidadesMarc> titulosResultadosTemp = new ArrayList<CacheEntidadesMarc>();

		
		
		List<String> listaDeParametros = new ArrayList<String>();     // Usada para guardar os parâmetros da consulta sql

		long tempo = System.currentTimeMillis();

		//final int QUANTIDADE_CAMPOS = 13;  // Tem que saber a quantidade de campos para poder gerar a quantidade de parâmetros certo.

		try{

			con = Database.getInstance().getSigaaConnection();

			StringBuilder sqlSelect = new StringBuilder();

			sqlSelect.append( SELECT_PADRAO );

			boolean escolheuFiltroSobreMaterias = false;
			
			for (CampoPesquisaAvancada campo : campos) {
				if(campo.isCampoBuscaInformacoesMateriais()){
					escolheuFiltroSobreMaterias = true;
					break;
				}
			}

			escolheuFiltroSobreMaterias = escolheuFiltroSobreMaterias || (idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0 );
			
			if( escolheuFiltroSobreMaterias )
				sqlSelect.append(INNER_JOIN_EXEMPLARES);
			

			sqlSelect.append(" WHERE t.id_titulo_catalografico is not null ");


			// BUSCA PUBLICA DO SISTEMA
			if(buscaPublica){
				sqlSelect.append(" AND  t.quantidade_materiais_ativos_titulo > 0 ");

				if((idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0 )){
					sqlSelect.append(" AND (   material.id_biblioteca in "+UFRNUtils.gerarStringIn(idsBibliotecasAcervoPublico)+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
				}
				
			}
				
			sqlSelect.append(montaParteComumBuscaAvancada( con, geradorPesquisa, campos, listaDeParametros, utilizarBuscaRemissiva, false));

			
			/* *****************************************************************************************
			 *    Se usuário escolheu algum filtro sobre material tem que realizar a mesma busca para  *
			 * os Fascículos                                                                          *
			 *******************************************************************************************/
			if(escolheuFiltroSobreMaterias ){
				
				sqlSelect.append( " UNION ALL  ( " );

				sqlSelect.append( SELECT_PADRAO );
				sqlSelect.append(INNER_JOIN_FASCICULOS);

				sqlSelect.append(" WHERE t.id_titulo_catalografico is not null ");

				// BUSCA PUBLICA DO SISTEMA
				if(buscaPublica){
					sqlSelect.append(" AND  t.quantidade_materiais_ativos_titulo > 0 ");

					if((idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0 )){
						sqlSelect.append(" AND (   material.id_biblioteca in "+UFRNUtils.gerarStringIn(idsBibliotecasAcervoPublico)+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
					}
				}
				
				sqlSelect.append(montaParteComumBuscaAvancada( con, geradorPesquisa, campos, listaDeParametros, utilizarBuscaRemissiva, false));

				sqlSelect.append( " ) " ); // fecha o union.
			}


			
			/**	A busca pública  deve trazer as catalogações que não possuirem materiais mais, que tenham algum endereço eletrônico.
 			 * 
			 * Isso é uma solução provisória para recuperar as catalogações que só possuem materiais digitais. Sem nenhum material físico. Num futuro deve ser criado
			 * no sistema o contexto de material digital. */
			if(buscaPublica){
				
				sqlSelect.append( " UNION ALL  ( " );

				sqlSelect.append( SELECT_PADRAO);
				
				sqlSelect.append(" WHERE t.id_titulo_catalografico is not null ");
				sqlSelect.append(" AND ( t.quantidade_materiais_ativos_titulo = 0 AND ( t.localizacao_endereco_eletronico IS NOT NULL AND t.localizacao_endereco_eletronico != '' )  ) ");
				
				sqlSelect.append(montaParteComumBuscaAvancada( con, geradorPesquisa, campos, listaDeParametros, utilizarBuscaRemissiva, true));
				
				sqlSelect.append( " ) " ); // fecha o UNION
			}	
			
			// ORDENA OR RESULTADOS
			if(campoOrdenacao != null)
				sqlSelect.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			
			// LIMITE NOS RESULTADOS DAS PESQUISAS
			sqlSelect.append( BDUtils.limit( ( buscaPublica ? LIMITE_BUSCA_TITULOS_PUBLICA : LIMITE_BUSCA_TITULOS ) ) );

			System.out.println( sqlSelect.toString() );

			for (int posicaoParametro = 1; posicaoParametro <= listaDeParametros.size(); posicaoParametro++) {
				System.out.println( listaDeParametros.get(posicaoParametro-1) );
			}
			
			PreparedStatement prepared = con.prepareStatement(sqlSelect.toString());

			for (int posicaoParametro = 1; posicaoParametro <= listaDeParametros.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaDeParametros.get(posicaoParametro-1));
			}

			titulosResultadosTemp =  montaResultadosConsulta(prepared.executeQuery());

			System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
			System.out.println("Consulta demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");


		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}
		}

		return titulosResultadosTemp;
	}

	
	/**
	 * Monta a parte comum da busca avançada, usada nos dois lados da busca do Union
	 */
	private String montaParteComumBuscaAvancada(Connection con, GeraPesquisaTextual geradorPesquisa, List<CampoPesquisaAvancada> campos, List<String> listaParametos, boolean utilizarBuscaRemissiva, boolean buscandoCatalogacoesSemMateriais) throws DAOException{

		StringBuilder sqlSelect = new StringBuilder();

		/* ***********************************************************
		 *   Monta o sql para cada campo da pesquisa avançada
		 * ***********************************************************/
		
		boolean algumCampoInformacaoValida = false;
		
		for (CampoPesquisaAvancada campo : campos) {
			
			if(campo.pequisaGerouParametro() && campo.contemInformacoes() && gerarConsultaMaterial(campo, buscandoCatalogacoesSemMateriais)){  // se tem alguma palavra para pesquisar
			
				algumCampoInformacaoValida = true;
				
				if(campo.isConectorE())
					sqlSelect.append(" AND ( ");
				if(campo.isConectorOU())
					sqlSelect.append(" OR ( ");
				if(campo.isConectorNAO()){
					if(campo.isCampoBuscaInformacoesMateriais())
						sqlSelect.append("  AND ( ");   // Não vai usar sub consultas nesse caso senão fica mais lento.
					else
						sqlSelect.append("  AND ( t.id_cache_entidades_marc NOT IN ( SELECT t.id_cache_entidades_marc FROM biblioteca.cache_entidades_marc t WHERE ");
				}

					
					List<String> autoresAutorizados = new ArrayList<String>();
				
					// REALIZA A BUSCA POR AUTORES REMISSIVOS //
					if(campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.AUTOR){

						//////////////////////////////////////////////////////////////////////////////////////////////
						// Palavras muito pequenas não faz a busca remissiva. Como a busca é por 'like' vai trazer  //
						// praticamente toda a base de autoridades.             //
						//////////////////////////////////////////////////////////////////////////////////////////////
						if( campo.getValorCampo().length() > 3 && utilizarBuscaRemissiva){
							autoresAutorizados = buscaTitulosPorAutoresRemissivos(con, geradorPesquisa, campo.getValorCampo()); // entradas autorizadas do nome digitado
						}
						
						sqlSelect.append( campo.gerarConsulta(geradorPesquisa, autoresAutorizados.toArray(new  String[autoresAutorizados.size()])) );
						
					}
				
					List<String> assuntosAutorizados = new ArrayList<String>();
					
					// REALIZA A BUSCA POR ASSUNTOS REMISSIVOS //
					if( campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.ASSUNTO ){

						
						
						//////////////////////////////////////////////////////////////////////////////////////////////
						// Palavras muito pequenas não faz a busca remissiva. Como a busca é por 'like' vai trazer  //
						// praticamente toda a base de autoridades.             //
						//////////////////////////////////////////////////////////////////////////////////////////////
						if( campo.getValorCampo().length() > 3 && utilizarBuscaRemissiva){
							assuntosAutorizados = buscaTitulosPorAssuntoRemissivos(con, geradorPesquisa, campo.getValorCampo()); // entradas autorizadas do nome digitado
						}
						
						sqlSelect.append( campo.gerarConsulta(geradorPesquisa, assuntosAutorizados.toArray(new  String[assuntosAutorizados.size()])) );
						
					}
					
					if( campo.getTipoCampo() != CampoPesquisaAvancada.TipoCampoBuscaAvancada.AUTOR && campo.getTipoCampo() != CampoPesquisaAvancada.TipoCampoBuscaAvancada.ASSUNTO  ){
							sqlSelect.append( campo.gerarConsulta(geradorPesquisa) );
					}
					
					String parametro = campo.getParametrosPesquisaGerada(geradorPesquisa);
					
					
					if(StringUtils.notEmpty(parametro)){
						
						int qtdParametros = campo.getQuantidadeTotalCampos();
								
						for (int i = 0;  i < qtdParametros ; i++) {
							listaParametos.add( parametro );
						}
						
						// se pesquisa de autor adicionar os parâmetros da pesquisa autorizada 2 x por qualsa dos autores secundários
						if(campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.AUTOR){
							for (int index = 0; index < autoresAutorizados.size(); index++) {
								
								String parametroTemp = toAsciiAndUpperCase(autoresAutorizados.get(index));
								
								if(parametroTemp != null && parametroTemp.contains("'"))  parametroTemp = parametroTemp.replace("'", " ");
								
								listaParametos.add(" ' "+ parametroTemp +" ' "  ); // AQUI BUSCA PELO NOME AUTORIZADO COMPLETO
							}
							
							for (int index = 0; index < autoresAutorizados.size(); index++) { // os autores secundários
								
								String parametroTemp = toAsciiAndUpperCase(autoresAutorizados.get(index));
								
								if(parametroTemp != null && parametroTemp.contains("'"))  parametroTemp = parametroTemp.replace("'", " ");
								
								listaParametos.add( " ' "+ parametroTemp +" ' " ); // AQUI BUSCA PELO NOME AUTORIZADO COMPLETO
							}
						}
						
						// se pesquisa assunto autor adicionar os parâmetros da pesquisa autorizada
						
						if(campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.ASSUNTO){
							for (int index = 0; index< assuntosAutorizados.size(); index++) {
								
								String parametroTemp = toAsciiAndUpperCase(assuntosAutorizados.get(index));
								
								if(parametroTemp != null && parametroTemp.contains("'")) parametroTemp = parametroTemp.replace("'", " ");
								
								listaParametos.add( " ' "+parametroTemp+" ' "  ); // AQUI BUSCA PELO NOME AUTORIZADO COMPLETO
							}
						}
						
					}
					
			
				if(campo.isConectorNAO() && ! campo.isCampoBuscaInformacoesMateriais())  // Fecha a consulta interna
					sqlSelect.append(BDUtils.limit(1000)+" ) " ); // Tem que limitar a consulta interna a 1000 senão fica muito pesado fazer um NOT IN em um quantidade muito grande
					
				sqlSelect.append(" )");  // Fecha o conector lógico
			}
		}
		
		
		if(! algumCampoInformacaoValida)         // se usuário não digitar nada nos campos.
			sqlSelect.append(" AND ( 1 = 0 ) ");
		
	
		return sqlSelect.toString();
	}
	
	
	/**
	 * Verifica se algum campo de material foi escolhido pelo usuário, nesse caso deve gerar os "inner joins" com os materiais.
	 *
	 * @param campo
	 * @param buscandoCatalogacoesSemMateriais
	 * @return
	 */
	private boolean gerarConsultaMaterial(CampoPesquisaAvancada campo, boolean buscandoCatalogacoesSemMateriais){
		
		if( buscandoCatalogacoesSemMateriais && (campo.getTipoCampo() ==  CampoPesquisaAvancada.TipoCampoBuscaAvancada.BIBLIOTECA 
				|| campo.getTipoCampo() ==  CampoPesquisaAvancada.TipoCampoBuscaAvancada.COLECAO 
				|| campo.getTipoCampo() ==  CampoPesquisaAvancada.TipoCampoBuscaAvancada.TIPO_MATERIAL ) ){
			return false;
		}else
			return true;
		
	}
	
	
	
	/**
	 *    Método que implementa a busca avançada pública do sistema. <br/>
	 *    A diferença aqui é que títulos que não possuem materiais não devem aparecer nos resultados.
	 * 
	 * @param titulo
	 * @param assunto
	 * @param autor
	 * @param Local
	 * @param Editora
	 * @param anoInicial
	 * @param anoFinal
	 * @return uma lista de títulos resumidos
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> buscaAvancadaPublica(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao, List<CampoPesquisaAvancada> campos,  List<Integer> idsBibliotecasAcervoPublico) throws DAOException{

		return buscaAvancada(geradorPesquisa, campoOrdenacao, campos, true, idsBibliotecasAcervoPublico, true);
	}
	
	

	////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////  BUSCA POR LISTAS ///////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Método que implementa a busca que o Aleph chama de multi-campo.
	 * 
	 * @param titulo
	 * @param assunto
	 * @param autor
	 * @param Local
	 * @param Editora
	 * @param anoInicial
	 * @param anoFinal
	 * @param publica indica se a busca é publica ou não. se for deve trazer apenas título que possuam materiais ativos.
	 * @return uma lista de títulos resumidos
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> buscaPorListas(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao, CampoPesquisaPorListas campo, int  idBiblioteca, int idColecao, int idTipoMaterial, int idStatus, boolean utilizarBuscaRemissiva) throws DAOException{
		
		Connection con = null;

		List<CacheEntidadesMarc> titulosResultadosTemp = new ArrayList<CacheEntidadesMarc>();

		List<String> listaDeParametros = new ArrayList<String>(); // Usada para guardar os parâmetros da consulta sql

		long tempo = System.currentTimeMillis();
		
		boolean filtroSobreMateriais = idBiblioteca != -1  || idColecao != -1 || idTipoMaterial != -1 || idStatus != -1 ;
		
		try{

			con = Database.getInstance().getSigaaConnection();


			StringBuilder sqlSelect = new StringBuilder();
			sqlSelect.append( SELECT_PADRAO  );

			if(filtroSobreMateriais){
				sqlSelect.append(INNER_JOIN_EXEMPLARES);
			}

			sqlSelect.append(" WHERE t.id_titulo_catalografico is not null ");
			
			sqlSelect.append(montaSqlComumBuscaPorListas(con, geradorPesquisa, campo, idBiblioteca, idColecao, idTipoMaterial, idStatus, listaDeParametros, utilizarBuscaRemissiva));
			
			
			if(filtroSobreMateriais ){
				
				sqlSelect.append( " UNION ALL ( " );

				sqlSelect.append( SELECT_PADRAO );
				sqlSelect.append(INNER_JOIN_FASCICULOS);

				sqlSelect.append(" WHERE t.id_titulo_catalografico is not null ");
				
				sqlSelect.append(montaSqlComumBuscaPorListas(con, geradorPesquisa, campo, idBiblioteca, idColecao, idTipoMaterial, idStatus, listaDeParametros, utilizarBuscaRemissiva));

				sqlSelect.append( " ) " ); // fecha o union.
			}
			
			// ORDENA OR RESULTADOS
			if(campoOrdenacao != null)
				sqlSelect.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			// LIMITE NOS RESULTADOS DAS PESQUISAS
			sqlSelect.append( BDUtils.limit( LIMITE_BUSCA_TITULOS  ) );
			
			System.out.println( sqlSelect.toString() );

			for (int posicaoParametro = 1; posicaoParametro <= listaDeParametros.size(); posicaoParametro++) {
				System.out.println( listaDeParametros.get(posicaoParametro-1) );
			}

			PreparedStatement prepared = con.prepareStatement(sqlSelect.toString());

			for (int posicaoParametro = 1; posicaoParametro <= listaDeParametros.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaDeParametros.get(posicaoParametro-1));
			}

			titulosResultadosTemp =  montaResultadosConsulta(prepared.executeQuery());
			
			System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
			System.out.println("Consulta demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");
			
			
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}
		}
	
		return titulosResultadosTemp;
	}
	
	
	
	/**
	 * Monta a parte comum do SQL a ser usado na busca multi campo, é comum pois caso o usuário escolha
	 * algum filtro sobre os materiais é preciso realizar uma busca para exemplares e outra para fascículos.
	 */
	private String montaSqlComumBuscaPorListas(Connection con, GeraPesquisaTextual gerador, CampoPesquisaPorListas campo,
						Integer idBiblioteca, Integer idColecao, Integer idTipoMaterial, Integer idStatus, List<String> listaParametos, boolean utilizarBuscaRemissiva) throws DAOException{

		StringBuilder sqlSelectTemp = new StringBuilder();

		boolean filtraSobreBiblioteca = ! new Integer(-1).equals(idBiblioteca);
		boolean filtraSobreColecao = ! new Integer(-1).equals(idColecao);
		boolean filtraSobreTipoMaterial = ! new Integer(-1).equals(idTipoMaterial);
		boolean filtraSobreStatus = ! new Integer(-1).equals(idStatus);
		
		if(StringUtils.notEmpty(campo.getValorCampo())){
			String[] camposBusca = BibliotecaUtil.retornaPalavrasBusca(campo.getValorCampo()); //separa os nomes pelo espaço

			if(camposBusca.length == 0 &&  campo.getTipoCampoEscolhido() != CampoPesquisaPorListas.CLASSIFICACAO_1 &&  campo.getTipoCampoEscolhido() != CampoPesquisaPorListas.CLASSIFICACAO_2 &&  campo.getTipoCampoEscolhido() != CampoPesquisaPorListas.CLASSIFICACAO_3)
				sqlSelectTemp.append(" AND ( 1 = 0 ) "); // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
			else{
				
				List<String> autoresAutorizados = new ArrayList<String>();
				List<String> assuntosAutorizados = new ArrayList<String>();
				
				// REALIZA A BUSCA POR AUTORES REMISSIVOS //
				if(campo.getTipoCampoEscolhido() == CampoPesquisaPorListas.AUTOR){

					//////////////////////////////////////////////////////////////////////////////////////////////
					// Palavras muito pequenas não faz a busca remissiva. Como a busca é por 'like' vai trazer  //
					// praticamente toda a base de autoridades.             //
					//////////////////////////////////////////////////////////////////////////////////////////////
					if( campo.getValorCampo().length() > 3 && utilizarBuscaRemissiva){
						autoresAutorizados = buscaTitulosPorAutoresRemissivos(con, gerador, campo.getValorCampo()); // entradas autorizadas do nome digitado
					}
					
					sqlSelectTemp.append( campo.gerarConsulta(gerador, autoresAutorizados.toArray(new  String[autoresAutorizados.size()])) );
					
					for (int i = 0; i < campo.getQuantidadeTotalCampos(); i++) {
						listaParametos.add( gerador.formataTextoPesquisaTextual(camposBusca) );
					}
					
					
					// se pesquisa de autor adicionar os parâmetros da pesquisa autorizada 2 x por qualsa dos autores secundários
					
					for (int i = 0; i < autoresAutorizados.size(); i++) {
						listaParametos.add(" ' "+  toAsciiAndUpperCase(autoresAutorizados.get(i))+" ' "  ); // AQUI BUSCA PELO NOME AUTORIZADO COMPLETO
					}
					
					for (int i = 0; i < autoresAutorizados.size(); i++) { // os autores secundários
						listaParametos.add( " ' "+ toAsciiAndUpperCase( autoresAutorizados.get(i))+" ' " ); // AQUI BUSCA PELO NOME AUTORIZADO COMPLETO
					}
					
					
				}else{
					if(campo.getTipoCampoEscolhido() == CampoPesquisaPorListas.ASSUNTO){
						
						//////////////////////////////////////////////////////////////////////////////////////////////
						// Palavras muito pequenas não faz a busca remissiva. Como a busca é por 'like' vai trazer  //
						// praticamente toda a base de autoridades.             //
						//////////////////////////////////////////////////////////////////////////////////////////////
						if( campo.getValorCampo().length() > 3 && utilizarBuscaRemissiva){
							assuntosAutorizados = buscaTitulosPorAssuntoRemissivos(con, gerador, campo.getValorCampo()); // entradas autorizadas do nome digitado
						}
						
						sqlSelectTemp.append( campo.gerarConsulta(gerador, assuntosAutorizados.toArray(new  String[assuntosAutorizados.size()])) );
						
						for (int i = 0; i < campo.getQuantidadeTotalCampos(); i++) {
							listaParametos.add( gerador.formataTextoPesquisaTextual(camposBusca) );
						}
						
						// 	se pesquisa assunto autor adicionar os parâmetros da pesquisa autorizada
						
						for (int i = 0; i < assuntosAutorizados.size(); i++) {
							listaParametos.add( " ' "+toAsciiAndUpperCase( assuntosAutorizados.get(i))+" ' "  ); // AQUI BUSCA PELO NOME AUTORIZADO COMPLETO
						}
						
						
					}else{
						
						sqlSelectTemp.append( campo.gerarConsulta(gerador) );
						
						if(campo.getTipoCampoEscolhido() == CampoPesquisaPorListas.CLASSIFICACAO_1 
								|| campo.getTipoCampoEscolhido() == CampoPesquisaPorListas.CLASSIFICACAO_2
								|| campo.getTipoCampoEscolhido() == CampoPesquisaPorListas.CLASSIFICACAO_3){
							listaParametos.add( gerador.formataTextoPesquisaInicioPalavra(campo.getValorCampo()) );
						}else{
							for (int i = 0; i < campo.getQuantidadeTotalCampos(); i++) {
								listaParametos.add( gerador.formataTextoPesquisaTextual(camposBusca) );
							}
						}
					}
				}
				
				
			}
		}else{
			if(! filtraSobreBiblioteca && ! filtraSobreColecao && ! filtraSobreTipoMaterial && ! filtraSobreStatus)
				sqlSelectTemp.append(" AND ( 1 = 0 ) ");
		}

		/////////////////////// Filtros Sobre os Materiais ///////////////////////////

		
		if(filtraSobreBiblioteca) sqlSelectTemp.append(" AND (      ( material.id_biblioteca = "+idBiblioteca+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
		if(filtraSobreColecao) sqlSelectTemp.append(" AND (         ( material.id_colecao = "+idColecao+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
		if(filtraSobreTipoMaterial) sqlSelectTemp.append(" AND (    ( material.id_tipo_material = "+idTipoMaterial+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
		if(filtraSobreStatus) sqlSelectTemp.append(" AND (    ( material.id_status_material_informacional = "+idStatus+" AND situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");

		if(filtraSobreBiblioteca)   sqlSelectTemp.append(" ) ");
		if(filtraSobreColecao)      sqlSelectTemp.append(" ) ");
		if(filtraSobreTipoMaterial) sqlSelectTemp.append(" ) ");
		if(filtraSobreStatus) sqlSelectTemp.append(" ) ");

		////////////////////////////////////////////////////////////////////////////////

		return sqlSelectTemp.toString();
	}
	
	
			
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Método que pega os campos  retornados da projeção das busca e monta a lista de objetos cache
	 * que é retornado para a visualização.
	 */
	private List<CacheEntidadesMarc> montaResultadosConsulta(ResultSet resultSet) throws SQLException{

		List<CacheEntidadesMarc> titulosResultadosTemp = new ArrayList<CacheEntidadesMarc>();

		int cont = 1;
		while (resultSet.next()) {

			cont = 1;   // lendo um novo objeto cache

			CacheEntidadesMarc cache = new CacheEntidadesMarc();
			cache.setId( resultSet.getInt(cont++) );
			cache.setIdTituloCatalografico( resultSet.getInt(cont++) );
			cache.setNumeroDoSistema( resultSet.getInt(cont++) );
			cache.setTitulo( resultSet.getString(cont++) );
			cache.setMeioPublicacao( resultSet.getString(cont++)  );
			cache.setSubTitulo( resultSet.getString(cont++)  );
			cache.setFormasVarientesTitulo( resultSet.getString(cont++)  );
			cache.setAutor( resultSet.getString(cont++)  );
			cache.setAutoresSecundarios( resultSet.getString(cont++)  );
			cache.setAssunto( resultSet.getString(cont++)  );
			cache.setLocalPublicacao( resultSet.getString(cont++)  );
			cache.setEditora( resultSet.getString(cont++)  );
			cache.setAno( resultSet.getString(cont++)  );
			cache.setEdicao( resultSet.getString(cont++)  );
			cache.setIsbn( resultSet.getString(cont++)  );
			cache.setNumeroChamada( resultSet.getString(cont++)  );

			int idObraDigitalizada = resultSet.getInt(cont++);
			if(idObraDigitalizada > 0)
				cache.setIdObraDigitalizada(  idObraDigitalizada );
			
			cache.setNotasGerais( resultSet.getString(cont++)  );
			cache.setNotaDeConteudo( resultSet.getString(cont++)  );
			cache.setNotasLocais( resultSet.getString(cont++)  );
			cache.setSerie( resultSet.getString(cont++)  );
			cache.setClassificacao1( resultSet.getString(cont++)  );
			cache.setClassificacao2( resultSet.getString(cont++)  );
			cache.setClassificacao3( resultSet.getString(cont++)  );
			cache.setQuantidadeMateriaisAtivosTitulo( resultSet.getInt(cont++) );
			cache.setLocalizacaoEnderecoEletronico( resultSet.getString(cont++)  );
			cache.setCatalogado( resultSet.getBoolean(cont++)  );
			titulosResultadosTemp.add(cache);
		}

		return titulosResultadosTemp;
	}
	

	
	
	/**
	 *   Método que realiza um busca separada nos título pelos autores remissivos.
	 * 
	 * @param autor o autor que o usuário digitou na pesquisa
	 */
	private List<String> buscaTitulosPorAutoresRemissivos(Connection con, GeraPesquisaTextual gerador, String autor) throws DAOException{

		long tempo = System.currentTimeMillis();

		String[] autoresBusca = BibliotecaUtil.retornaPalavrasBusca(autor); //separa os nomes pelo espaço

		List<String> listaParametos = new ArrayList<String>();
		
		List<String> entradasAutorizadas = new ArrayList<String>();

		
		if(autoresBusca.length > 0){

			try{
		
				StringBuilder sqlRemisivo = new StringBuilder(" SELECT a.entrada_autorizada_autor FROM biblioteca.cache_entidades_marc a "
						+" WHERE a.id_autoridade is not null ");
				
				listaParametos.add( gerador.formataTextoPesquisaTextual(autoresBusca) );
				sqlRemisivo.append(" AND ( "+gerador.gerarMecanismoPesquisaTextual("nomes_remissivos_autor_ascii")+" ) ");
				
				PreparedStatement prepared = con.prepareStatement(sqlRemisivo.toString());
	
				for (int posicaoParametro = 1; posicaoParametro <= listaParametos.size(); posicaoParametro++) {
					prepared.setString(posicaoParametro, listaParametos.get(posicaoParametro-1));
				}
	
				ResultSet resultSet =  prepared.executeQuery();
				
				 while (resultSet.next()) {
					 entradasAutorizadas.add(resultSet.getString(1));
				 }
			 
			}catch(SQLException sqlEx){
				throw new DAOException(sqlEx.getMessage());
			}
		}

		System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
		System.out.println("Consulta remissiva autor demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");

		System.out.println("Resultado consulta remissiva? "+entradasAutorizadas);

		return entradasAutorizadas;

	}
	



	/**
	 * Método que realiza um busca separada nos título pelos assuntos remissivos.
	 * 
	 * @param assunto o assunto que o usuário digitou na pesquisa
	 */
	private List<String> buscaTitulosPorAssuntoRemissivos(Connection con, GeraPesquisaTextual gerador, String assunto) throws DAOException{

		long tempo = System.currentTimeMillis();

		String[] assuntoBusca = BibliotecaUtil.retornaPalavrasBusca(assunto); //separa os nomes pelo espaço

		List<String> listaParametos = new ArrayList<String>();
		
		List<String> entradasAutorizadas = new ArrayList<String>();
	
		if(assuntoBusca.length > 0){

			try{
				
				StringBuilder sqlRemisivo = new StringBuilder(" SELECT a.entrada_autorizada_assunto FROM biblioteca.cache_entidades_marc a "
						+" WHERE a.id_autoridade is not null ");
				
				listaParametos.add( gerador.formataTextoPesquisaTextual(assuntoBusca) );
				sqlRemisivo.append(" AND ( "+gerador.gerarMecanismoPesquisaTextual("nomes_remissivos_assunto_ascii")+" ) ");
				
				PreparedStatement prepared = con.prepareStatement(sqlRemisivo.toString());
	
				for (int posicaoParametro = 1; posicaoParametro <= listaParametos.size(); posicaoParametro++) {
					prepared.setString(posicaoParametro, listaParametos.get(posicaoParametro-1));
				}
	
				ResultSet resultSet =  prepared.executeQuery();
				
				 while (resultSet.next()) {
					 entradasAutorizadas.add(resultSet.getString(1));
				 }
			 
			}catch(SQLException sqlEx){
				throw new DAOException(sqlEx.getMessage());
			}
		}

		System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
		System.out.println("Consulta remissiva assunto demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");

		System.out.println("Resultado consulta remissiva? "+entradasAutorizadas);

		return entradasAutorizadas;

	}
	

	/**
	 * 
	 *    Método que procura se já existe na base algum Título Catalográfico com o mesmo
	 * Título (245$a) Autor(100$a) Ano(260$c) e Edição(250$a) que não seja ele próprio
	 * , se existir retorna a quantidade de títulos.
	 * 
	 *    Tem que verificar se não é ele, pois a verificação não pode ser na momento da criação,
	 *    visto que títulos importados são salvos diretamente sem validação.
	 * 
	 *    Esse método serve para verificar se já existe um título com essa característica na base antes
	 * de salvar um novo título, porque não era para existir.
	 */
	public int countTitulosDuplicados(String titulo, String subTitulo, String autor, String autoresSecundarios, String ano, String edicao,
			String editora, Integer numeroDoSistema) throws DAOException{

		StringBuilder hql = new StringBuilder(" SELECT count(DISTINCT tituloCache.id)  FROM CacheEntidadesMarc as tituloCache ");

		hql.append(" WHERE 1 = 1 AND tituloCache.idTituloCatalografico is not null AND tituloCache.numeroDoSistema != :numeroDoSistema "
				+" AND tituloCache.catalogado = trueValue() ");

		if( StringUtils.notEmpty(titulo)){
			hql.append(" AND  tituloCache.titulo =  :titulo ");
		}else{           //  O título que está sendo salvo NÃO possui título
			hql.append(" AND  ( tituloCache.titulo is null OR tituloCache.titulo = '' ) ");
		}

		if( StringUtils.notEmpty(subTitulo)){
			hql.append(" AND  tituloCache.subTitulo =  :subTitulo ");
		}else{           //  O título que está sendo salvo NÃO possui edição
			hql.append(" AND  ( tituloCache.subTitulo is null  OR tituloCache.subTitulo = '' ) ");
		}

		if(StringUtils.notEmpty(autor)){
			hql.append(" AND tituloCache.autor =  :autor ");
		}else{           //  O título que está sendo salvo NÃO possui autor
			hql.append(" AND ( tituloCache.autor is null OR tituloCache.autor = '' ) ");
		}
		
		if(StringUtils.notEmpty(autoresSecundarios)){
			hql.append(" AND tituloCache.autoresSecundarios =  :autoresSecundarios ");
		}else{           //  O título que está sendo salvo NÃO possui autor
			hql.append(" AND ( tituloCache.autoresSecundarios is null OR tituloCache.autoresSecundarios = '' ) ");
		}

		if(StringUtils.notEmpty(ano)){
			hql.append(" AND tituloCache.ano =  :ano ");
		}else{           //  O título que está sendo salvo NÃO possui ano
			hql.append(" AND ( tituloCache.ano is null OR tituloCache.ano = '' )  ");
		}

		if(StringUtils.notEmpty(edicao)){
			hql.append(" AND tituloCache.edicao = :edicao ");
		}else{           //  O título que está sendo salvo NÃO possui edição
			hql.append(" AND ( tituloCache.edicao is null OR tituloCache.edicao = '' )   ");
		}

		if(StringUtils.notEmpty(editora)){
			hql.append(" AND tituloCache.editora = :editora ");
		}else{           //  O título que está sendo salvo NÃO possui editora
			hql.append(" AND ( tituloCache.editora is null OR tituloCache.editora = '' ) ");
		}

		Query q = getSession().createQuery(hql.toString());



		if(StringUtils.notEmpty(titulo))                 { q.setString("titulo", titulo ); }
		if(StringUtils.notEmpty(subTitulo))              { q.setString("subTitulo", subTitulo ); }
		if(StringUtils.notEmpty(autor))                  { q.setString("autor", autor );    }
		if(StringUtils.notEmpty(autoresSecundarios))     { q.setString("autoresSecundarios", autoresSecundarios );    }
		if(StringUtils.notEmpty(ano))                    { q.setString("ano",  ano );                            }
		if(StringUtils.notEmpty(edicao))                 { q.setString("edicao", edicao  );                      }
		if(StringUtils.notEmpty(editora))                { q.setString("editora", editora );                     }

		q.setInteger("numeroDoSistema", numeroDoSistema);

		return  ((Long) q.uniqueResult()).intValue();

	}
	


	/**
	 * Retorna uma lista de materiais informacionais adquiridos por compra no SIPAC
	 */
	public Collection<Object[]> findAllAquisicaoSIPAC(String titulo,String autor,Integer ano,Integer limite)
	throws ArqException{

		Connection con = null;
		// Representa o TipoMaterialInformacional.LIVRO mapeado no SIPAC.
		int livro = 1;
		try {
			con = Database.getInstance().getSipacConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com SIPAC!");

			StringBuilder sql = new StringBuilder();

			sql.append("select  l.titulo,  l.edicao, l.ano, l.autor, ug.nome");
			sql.append("	 , count (bem.id) as volumes, u.nome, r.ano from comum.unidade u ");
			sql.append("     join comum.unidade ug on (u.id_gestora = ug.id_unidade) ");
			sql.append("     join requisicoes.requisicao_biblioteca r on (u.id_unidade = r.id_unidade) ");
			sql.append("     join requisicoes.item_material_informacional il on (r.id = il.id_requisicao) ");
			sql.append("     join requisicoes.material_informacional l on (il.id_material_informacional = l.id) ");
			sql.append("     left join requisicoes.finalidade_pedido fp on( r.id_finalidade_pedido = fp.id_finalidade_pedido) ");
			sql.append("     join comum.material m on (l.id_material = m.id_material) ");
			sql.append("     join patrimonio.bem bem on (bem.id_material = m.id_material) ");
			sql.append("     join patrimonio.termo_responsabilidade t on (bem.id_termo_responsabilidade = t.id) ");
			sql.append("     join comum.unidade up on (t.id_unidade_responsavel = up.id_unidade) ");
			sql.append("where bem.anulado = falseValue() AND il.tipo = "+ livro + " ");

			if(StringUtils.notEmpty(titulo))
				sql.append(" and upper(l.titulo_ascii) like '%" + StringUtils.toAscii(titulo.toUpperCase().trim())+ "%'");

			if(StringUtils.notEmpty(autor))
				sql.append(" and upper(l.autor) like '%" + autor.toUpperCase().trim()+ "%'");

			if(ano != null)
				sql.append(" and r.ano = " + ano);

			sql.append(" group by l.titulo,  l.edicao, l.ano, l.autor, r.ano,  ug.nome, u.nome");
			sql.append(" order by r.ano DESC, ug.nome ASC, u.nome ASC");

			if(limite != null)
				sql.append( " "+BDUtils.limit(limite)+" ");


			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql.toString());

			ArrayList<Object[]> itens = new ArrayList<Object[]>();

			while(rs.next()){

				Object[] item = new Object[8];
				item[0] = rs.getString(1);
				item[1] = rs.getString(2);
				item[2] = rs.getInt(3);
				item[3] = rs.getString(4);
				item[4] = rs.getString(5);
				item[5] = rs.getInt(6);
				item[6] = rs.getString(7);
				item[7] = rs.getString(8);


				itens.add(item);
			}
			return itens;
		}catch(Exception e){
			throw new DAOException(e);
		}
		finally{
			Database.getInstance().close(con);
		}
	}
	
	
	/**
	 *     Se a defesa passada já foi catalogada na biblioteca, este método retorna
	 * o id do título referente a ela. Senão, retorna <em>null</em>.
	 * <p>
	 * <em>Observação</em>: a informação da defesa que o título representa
	 * foi criada no final de 2010. Assim, nem todos títulos referentes a defesas
	 * estão ligados a elas.
	 */
	public Integer findTituloReferenteADefesa( int idDadosDefesa )
			throws DAOException {
		String hql =
				"SELECT t.id " +
				"FROM TituloCatalografico t " +
				"WHERE " +
				"	t.idDadosDefesa = " + idDadosDefesa + " " +
				"	AND t.ativo = true ";
		
		Query q = getSession().createQuery(hql);
		return (Integer) q.uniqueResult();
	}
	
	
	/**
	 * <p>Atualiza a quantidade de títulos que foram consultos na última hora no sistema</p>
	 * 
	 * @throws DAOException 
	 */
	public void atualizaQuantidadeTitulosConsultados(Integer idTitulo, long quantidadeEstimadaBusca) throws DAOException {
		Query q = getSession().createSQLQuery("UPDATE biblioteca.cache_entidades_marc SET quantidade_vezes_consultado = quantidade_vezes_consultado + :quantidade WHERE id_titulo_catalografico = :idTitulo ");
		q.setLong("quantidade", quantidadeEstimadaBusca);
		q.setInteger("idTitulo", idTitulo);
		q.executeUpdate();
	}
	
	/**
	 * <p>Atualiza a quantidade de títulos que foram consultos na última hora no sistema</p>
	 * 
	 * @throws DAOException 
	 */
	public void atualizaQuantidadeTitulosVisualizados(Integer idTitulo, long quantidadeEstimadaBusca) throws DAOException {
		Query q = getSession().createSQLQuery("UPDATE biblioteca.cache_entidades_marc SET quantidade_vezes_visualizado = quantidade_vezes_visualizado + :quantidade WHERE id_titulo_catalografico = :idTitulo ");
		q.setLong("quantidade", quantidadeEstimadaBusca);
		q.setInteger("idTitulo", idTitulo);
		q.executeUpdate();
	}
	
	/**
	 * <p>Atualiza a quantidade de títulos que foram consultos na última hora no sistema</p>
	 * 
	 * @throws DAOException 
	 */
	public void atualizaQuantidadeTitulosEmprestados(Integer idTitulo, long quantidadeEstimadaBusca) throws DAOException {
		Query q = getSession().createSQLQuery("UPDATE biblioteca.cache_entidades_marc SET quantidade_vezes_emprestado = quantidade_vezes_emprestado + :quantidade WHERE id_titulo_catalografico = :idTitulo ");
		q.setLong("quantidade", quantidadeEstimadaBusca);
		q.setInteger("idTitulo", idTitulo);
		q.executeUpdate();
	}
	
}

