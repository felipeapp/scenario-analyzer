/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/05/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 *     Dao para consultar artigos de periódicos
 *
 * @author jadson
 * @since 07/05/2009
 * @version 1.0 criacao da classe
 *
 */
public class ArtigoDePeriodicoDao extends GenericSigaaDAO{

	/** O limite da busca de artigos no sistema */
	public static final Integer LIMITE_BUSCA_ARTIGOS = 300;

	
	/**   O sql padrão das buscas de artigos no acervo */
	public static final String SELECT_PADRAO = " SELECT distinct a.id_cache_entidades_marc, a.id_artigo_de_periodico, a.numero_do_sistema, " +
	"a.titulo, a.autor, a.assunto, a.local_publicacao, a.editora, a.ano, a.intervalo_paginas, a.resumo, a.catalogado "
	+" FROM biblioteca.cache_entidades_marc a ";
	
	/** O inner join padrão realizados com os fásciculos para saber se deve mostrar os artigos */
	public static final String INNER_JOIN_FASCICULOS = " INNER JOIN biblioteca.artigo_de_periodico artigo on artigo.id_artigo_de_periodico = a.id_artigo_de_periodico   "
		+" INNER JOIN biblioteca.fasciculo fasciculo on artigo.id_fasciculo = fasciculo.id_fasciculo   "
		+" INNER JOIN biblioteca.material_informacional material on fasciculo.id_fasciculo = material.id_material_informacional  "
		+" INNER JOIN biblioteca.situacao_material_informacional situacao on material.id_situacao_material_informacional = situacao.id_situacao_material_informacional    ";
	
	
	
	/**
	 *  <p>Encontra um Artigo (de um fascículo) pelo título, autor e palavraChave.</p>
	 *  
	 *  <p> <strong> É a consulta utilizada pelos usuários para consultar o acervo de artigos </strong> </p>
	 *  
	 * @param titulo
	 * @param autor
	 * @param palavraChave
	 * @return
	 * @throws DAOException
	 */
	public List <CacheEntidadesMarc> buscaSimplesArtigo(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao,
			String titulo, String autor, String palavraChave, boolean buscaPublica, List<Integer> idsBibliotecaPublicaAcervo ) throws DAOException{
	
		long tempo = System.currentTimeMillis();
		
		Connection con = null;
		
		List<CacheEntidadesMarc> artigos = new ArrayList<CacheEntidadesMarc>();
		
		List<String> listaParametos = new ArrayList<String>();
		
		try{
			con = Database.getInstance().getSigaaConnection();
		
			StringBuilder sqlComum = new StringBuilder( SELECT_PADRAO );		
			
			
			sqlComum.append(INNER_JOIN_FASCICULOS);
			
			
			sqlComum.append(" WHERE a.id_artigo_de_periodico is not null ");
			
			if(buscaPublica){
				if((idsBibliotecaPublicaAcervo != null && idsBibliotecaPublicaAcervo.size() > 0 )){
					sqlComum.append(" AND (   material.id_biblioteca in "+UFRNUtils.gerarStringIn(idsBibliotecaPublicaAcervo)+" ) ");
				}
			}
		
			sqlComum.append("  AND ( situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
				
			sqlComum.append(" AND ( 1 = 0 ");
		
			String[] titulosBusca = new String[0];
			String[] autoresBusca = new String[0];
			String[] palavrasChavesBusca = new String[0];
			
			if(! StringUtils.isEmpty(titulo))
				titulosBusca = BibliotecaUtil.retornaPalavrasBusca(titulo); //separa os nomes pelo espaço
			
			if(! StringUtils.isEmpty(autor))
				autoresBusca = BibliotecaUtil.retornaPalavrasBusca(autor); //separa os nomes pelo espaço
			
			if(! StringUtils.isEmpty(palavraChave))
				palavrasChavesBusca = BibliotecaUtil.retornaPalavrasBusca(palavraChave); //separa os nomes pelo espaço
			
			if(titulosBusca.length > 0){
				sqlComum.append(" OR (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.titulo_ascii") +" ) " );
				listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(titulosBusca) );
				
			}
			
			if(autoresBusca.length > 0){
				sqlComum.append(" OR (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.autor_ascii") +" ) " );
				listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(autoresBusca) );
				
			}
			
			if(palavrasChavesBusca.length > 0){
				sqlComum.append(" OR (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.assunto_ascii") +" ) " );
				listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(palavrasChavesBusca) );
			}
			
			sqlComum.append(" ) ");
				
			
			if(campoOrdenacao != null)
				sqlComum.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			sqlComum.append( BDUtils.limit( LIMITE_BUSCA_ARTIGOS ) );
			
			System.out.println("Parametros >>>");
			for (String  string: listaParametos) {
				System.out.println(string);
			}
			
			System.out.println(sqlComum.toString());
			
			PreparedStatement prepared = con.prepareStatement(sqlComum.toString());
			
			for (int posicaoParametro = 1; posicaoParametro <= listaParametos.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaParametos.get(posicaoParametro-1));
			}
	
			artigos =  montaResultadosConsulta(prepared.executeQuery());
			
			System.out.println("consulta de artigos demorou: >>>> "+( System.currentTimeMillis() - tempo)+" ms");
			
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}	
		}
		
			
		return artigos;
	}
	
	
	
	/**
	 *  <p>Encontra um Artigo (de um fascículo) pelo título, autor e palavraChave.</p>
	 *  
	 *  <p> <strong> É a consulta utilizada pelos usuários para consultar o acervo de artigos </strong> </p>
	 *  
	 * @param titulo
	 * @param autor
	 * @param palavraChave
	 * @return
	 * @throws DAOException
	 */
	public List <CacheEntidadesMarc> findAllArtigosResumidosAtivosByTituloAutorPalarvaChave(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao,
			String titulo, String autor, String palavraChave, boolean buscaPublica, List<Integer> idsBibliotecaPublicaAcervo ) throws DAOException{
	
		long tempo = System.currentTimeMillis();
		
		Connection con = null;
		
		List<CacheEntidadesMarc> artigos = new ArrayList<CacheEntidadesMarc>();
		
		List<String> listaParametos = new ArrayList<String>();
		
		try{
			con = Database.getInstance().getSigaaConnection();
		
			StringBuilder sqlComum = new StringBuilder( SELECT_PADRAO );		
			
			
			sqlComum.append(INNER_JOIN_FASCICULOS);
			
			
			sqlComum.append(" WHERE a.id_artigo_de_periodico is not null ");
			
			if(buscaPublica){
				if((idsBibliotecaPublicaAcervo != null && idsBibliotecaPublicaAcervo.size() > 0 )){
					sqlComum.append(" AND (   material.id_biblioteca in "+UFRNUtils.gerarStringIn(idsBibliotecaPublicaAcervo)+" ) ");
				}
			}
		
			sqlComum.append("  AND ( situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
			
			
			boolean possuiCriterioBusca = false;
			
			
			if(! StringUtils.isEmpty(titulo)){ 
				String[] titulosBusca = BibliotecaUtil.retornaPalavrasBusca(titulo); //separa os nomes pelo espaço
				
				if(titulosBusca.length == 0)
					sqlComum.append(" AND ( 1 = 0 ) ");   // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					sqlComum.append(" AND (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.titulo_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(titulosBusca) );
				}
				
				possuiCriterioBusca = true;
			}
			
			if(! StringUtils.isEmpty(autor)){
				String[] autoresBusca = BibliotecaUtil.retornaPalavrasBusca(autor); //separa os nomes pelo espaço
				
				if(autoresBusca.length == 0)
					sqlComum.append(" AND ( 1 = 0 ) ");   // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					sqlComum.append(" AND (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.autor_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(autoresBusca) );
				}
				
				possuiCriterioBusca = true;
			}
		
			
			if(! StringUtils.isEmpty(palavraChave)){
				String[] palavrasChavesBusca = BibliotecaUtil.retornaPalavrasBusca(palavraChave); //separa os nomes pelo espaço
				
				if(palavrasChavesBusca.length == 0)
					sqlComum.append(" AND ( 1 = 0 ) "); // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					sqlComum.append(" AND (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.assunto_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(palavrasChavesBusca) );
				}
				
				possuiCriterioBusca = true;
			}
			
			
			if(! possuiCriterioBusca){
				sqlComum.append(" AND 1 = 0");
			}
			
			if(campoOrdenacao != null)
				sqlComum.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			sqlComum.append( BDUtils.limit( LIMITE_BUSCA_ARTIGOS ) );
			
			System.out.println("Parametros >>>");
			for (String  string: listaParametos) {
				System.out.println(string);
			}
			
			System.out.println(sqlComum.toString());
			
			PreparedStatement prepared = con.prepareStatement(sqlComum.toString());
			
			for (int posicaoParametro = 1; posicaoParametro <= listaParametos.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaParametos.get(posicaoParametro-1));
			}
	
			artigos =  montaResultadosConsulta(prepared.executeQuery());
			
			System.out.println("consulta de artigos demorou: >>>> "+( System.currentTimeMillis() - tempo)+" ms");
			
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}	
		}
		
			
		return artigos;
	}
	
	
	
	
	
	/**
	 *   Encontra um artigo cache pelo número do sistema. Todo arquivo quando catalogado, recebe um 
	 *   número que o identifica, esse número de identificação é chamado "número do sistema".<br/>
	 *   Esse método recupera o arquivo que possui o número do sistema passado.<br/>
	 *   
	 *   <p> <strong> É a consulta utilizada pelos usuários para consultar o acervo de artigos, quando a busca é feita apenas pelo número de sistema. </strong> </p>
	 *
	 * @param numeroSistema
	 * @return
	 * @throws DAOException 
	 */
	public CacheEntidadesMarc findByNumeroSistema(Integer numeroDoSistema) throws DAOException{
		
		StringBuilder hqlSelect = new StringBuilder( SELECT_PADRAO );
		hqlSelect.append(INNER_JOIN_FASCICULOS);
		
		hqlSelect.append(" WHERE a.numero_do_sistema = :numeroDoSistema  AND a.id_artigo_de_periodico is not null " );
		hqlSelect.append("  AND ( situacao.situacao_de_baixa =  falseValue() AND material.ativo = trueValue() ) ");
		
		Query q = getSession().createSQLQuery(hqlSelect.toString());
		q.setInteger("numeroDoSistema", numeroDoSistema);

		@SuppressWarnings("unchecked")
		List<Object[]> lista =  q.list();
		List<CacheEntidadesMarc>  caches = montaResultadosConsulta(lista);
		if(caches.size() > 0)
			return caches.get(0);
		else
			return null;
	}
	
	
	
	
	
	/**
	 *    Método que encontra os artigos que não foram removidos (ativos) do fascículos passado
	 * e retorna apenas os ids desses artigos. 
	 * 
	 *   <p>Geralmente usado para pegar o objeto cache dos artigos a partir dos ids.</p> 
	 *
	 * @param idFasciculo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Long countArtigosDoFasciculoNaoRemovidos(int idFasciculo) throws DAOException{
		String hql = new String( " SELECT count(distinct a.id) FROM ArtigoDePeriodico a "
				+" WHERE a.fasciculo.id  = :idFasciculo AND a.ativo = trueValue() ");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idFasciculo", idFasciculo);
		return  (Long) q.uniqueResult();
	}
	
	
	
	/**
	 *    Método que encontra os artigos que não foram removidos (ativos) do fascículos passado
	 * e retorna apenas os ids desses artigos. 
	 * 
	 *   <p>Geralmente usado para pegar o objeto cache dos artigos a partir dos ids.</p> 
	 *
	 * @param idFasciculo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Integer> findIdArtigosDoFasciculoNaoRemovidos(int idFasciculo) throws DAOException{
		String hql = new String( " SELECT a.id FROM ArtigoDePeriodico a "
				+" WHERE a.fasciculo.id  = :idFasciculo AND a.ativo = trueValue() ");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idFasciculo", idFasciculo);
		
		@SuppressWarnings("unchecked")
		List<Integer> lista = q.list();
		return lista;
	}
	
	
	/**
	 * 
	 * Registra as alterações de um título na tabela historico_alteracao_titulo
	 * 
	 * @param titulo
	 * @param descricao
	 * @throws DAOException
	 */
	public void registraAlteracaoArtigo(ArtigoDePeriodico artigo, boolean atualizando) throws DAOException{
		
		String sql = " INSERT INTO biblioteca.historico_alteracao_artigos_de_periodicos "
			+"(id_historico_alteracao_artigos_de_periodicos, id_artigo_de_periodico, id_registro_entrada, descricao_operacao, data_operacao)"
			+" VALUES ( nextval('biblioteca.historicos_alteracao_sequence'), ?, ?, ?, ?)";
		
				int idArtigo = artigo.getId();
				int idRegistroEntrada = 0;
				
				if(atualizando)
					idRegistroEntrada = artigo.getRegistroUltimaAtualizacao().getId();
				else
					idRegistroEntrada = artigo.getRegistroCriacao().getId();
				
				update(sql, idArtigo, idRegistroEntrada, artigo.toString(), new java.sql.Timestamp( new Date().getTime() ));
				
	}
	
	
	
	
	/** Método que pega os campos  retornados da projeção das busca e monta a lista de objetos cache
	 * que é retornado para a visualização.
	 */
	private List<CacheEntidadesMarc> montaResultadosConsulta(ResultSet resultSet) throws SQLException{
		
		List<CacheEntidadesMarc> artigosResultadosTemp = new ArrayList<CacheEntidadesMarc>();

		int cont = 1;
		while (resultSet.next()) { 

			cont = 1;   // lendo um novo objeto cache

			CacheEntidadesMarc cache = new CacheEntidadesMarc();
			cache.setId( resultSet.getInt(cont++) );
			cache.setIdArtigoDePeriodico( resultSet.getInt(cont++) );
			cache.setNumeroDoSistema( resultSet.getInt(cont++) );
			cache.setTitulo( resultSet.getString(cont++) );
			cache.setAutor( resultSet.getString(cont++)  );
			cache.setAssunto( resultSet.getString(cont++)  );
			cache.setLocalPublicacao( resultSet.getString(cont++)  );
			cache.setEditora( resultSet.getString(cont++)  );
			cache.setAno( resultSet.getString(cont++)  );
			cache.setIntervaloPaginas( resultSet.getString(cont++)  );
			cache.setResumo( resultSet.getString(cont++)  );
			cache.setCatalogado( resultSet.getBoolean(cont++)  );
			artigosResultadosTemp.add(cache);
		}

		return artigosResultadosTemp;
	}
	
	/** Método que pega os campos  retornados da projeção das busca e monta a lista de objetos cache
	 * que é retornado para a visualização.
	 */
	private List<CacheEntidadesMarc> montaResultadosConsulta(List<Object[]> dadosArtigo) throws DAOException{
		
		List<CacheEntidadesMarc> artigosResultadosTemp = new ArrayList<CacheEntidadesMarc>();

		int cont = 0;
		for (Object[] dados : dadosArtigo) {
			
			cont = 0;   // lendo um novo objeto cache

			CacheEntidadesMarc cache = new CacheEntidadesMarc();
			cache.setId( (Integer) dados[cont++] );
			cache.setIdArtigoDePeriodico(  (Integer)dados[cont++] );
			cache.setNumeroDoSistema( (Integer) dados[cont++] );
			cache.setTitulo( (String) dados[cont++] );
			cache.setAutor( (String)dados[cont++]  );
			cache.setAssunto( (String) dados[cont++]  );
			cache.setLocalPublicacao( (String) dados[cont++]  );
			cache.setEditora( (String) dados[cont++]  );
			cache.setAno( (String) dados[cont++]  );
			cache.setIntervaloPaginas( (String) dados[cont++]  );
			cache.setResumo( (String) dados[cont++]  ); 
			cache.setCatalogado( (Boolean) dados[cont++]  );
			artigosResultadosTemp.add(cache);
		}

		return artigosResultadosTemp;
	}
	
	
	/**
	 *        Retorna uma lista de Modificações de um artigo de periódico, geralmente usado no histórico de
	 * alterações ou na visualização da alterações na página de catalogação.
	 * 
	 * @param idTitulo
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> findAlteracoesByArtigoDePeriodicoPeriodo (int idArtigo, Date dataInicio, Date dataFim) throws DAOException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String sql = "select p.nome, h.descricao_operacao, h.data_operacao " +
					"from biblioteca.historico_alteracao_artigos_de_periodicos h " +
					"join comum.registro_entrada r on r.id_entrada = h.id_registro_entrada " +
					"join comum.usuario u on u.id_usuario = r.id_usuario " +
					"join comum.pessoa p on p.id_pessoa = u.id_pessoa " +
					"where h.id_artigo_de_periodico = " + idArtigo;

		if (dataInicio != null)
			sql += " and data_operacao >= '"+sdf.format(dataInicio)+"' ";
		if (dataFim != null)
			sql += "and data_operacao <= '"+sdf.format(dataFim)+" 23:59:59' ";
					
		sql += "order by data_operacao desc";
		
		@SuppressWarnings("unchecked")
		List <Object[]> lista = getSession().createSQLQuery(sql).list();
		return lista;
		
	}
	
	
	/**
	 *        Retorna artigos de periódicos que foram migrados sem o fascículo, porque não foi 
	 *    possível identificar o fascículo dele no sistema antigo.
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> findInformacoesArtigos(List<Integer> idsArtigos) throws DAOException{
	
		if(idsArtigos == null || idsArtigos.size() == 0){
			return new ArrayList<CacheEntidadesMarc>();
		}
		
		final String projecao = "  c.id, c.numeroDoSistema, c.titulo, c.autor, c.assunto, c.intervaloPaginas  ";
		
		String hqlSelect = " SELECT " +projecao
		+" FROM CacheEntidadesMarc c "
		+" WHERE c.idArtigoDePeriodico in "+UFRNUtils.gerarStringIn(idsArtigos)		
		+" ORDER BY c.numeroDoSistema  ";
		
		Query q = getSession().createQuery(hqlSelect.toString());
		
	
		
		@SuppressWarnings("unchecked")
		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(HibernateUtils.parseTo(q.list(), projecao, CacheEntidadesMarc.class, "c") );
		return lista;
	}
	
}
