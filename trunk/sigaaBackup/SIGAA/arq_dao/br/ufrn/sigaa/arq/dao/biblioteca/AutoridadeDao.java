/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/04/2009
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
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EntidadesMarcadasParaExportacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * Dao de autoridades para a biblioteca.
 *
 * @author Jadson
 * @since 30/04/2009
 * @version 1.0 Criação da classe
 *
 */
public class AutoridadeDao extends GenericSigaaDAO{

	/**
	 * A quantidade máxima de autoridades recuperadas.
	 */
	public static final Integer LIMITE_BUSCA_AUTORIDADE = 300;
	
	/** A projeção comum utilizada na buscas do acervo de autoridades */
	public static final String PROJECAO_COMUM_BUSCA_ACERVO = " a.id_cache_entidades_marc, a.id_autoridade, a.numero_do_sistema, a.entrada_autorizada_autor, a.nomes_remissivos_autor, a.entrada_autorizada_assunto, a.nomes_remissivos_assunto, a.campo_entrada_autorizada, a.catalogado  ";
	
	
	/**
	 * Encontra as autoridades pendentes de exportação do usuário passado
	 */
	public List<EntidadesMarcadasParaExportacao> encontraAutoridadesPendentesExportacao() throws DAOException{
		String hql = new String( " SELECT e FROM EntidadesMarcadasParaExportacao e "
				+" WHERE e.idAutoridade is not null ");
		
		Query q = getSession().createQuery(hql); // Se a quantidade de Títulos pendentes for grande, 
		 										// trás apenas os 100 primeiros
		q.setMaxResults(100);
		
		@SuppressWarnings("unchecked")
		List<EntidadesMarcadasParaExportacao> lista = q.list();
		return lista;
	}
	
	
	/**
	 * Encontra as autoridades pendentes de exportação do usuário passado
	 */
	public List<EntidadesMarcadasParaExportacao> encontraAutoridadesPendentesExportacaoByUsuario(int idUsusario) throws DAOException{
		String hql = new String( " SELECT e FROM EntidadesMarcadasParaExportacao e "
				+" WHERE e.idAutoridade is not null AND e.idUsuarioMarcouExportacao  = :idUsusario");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsusario", idUsusario);
		
		@SuppressWarnings("unchecked")
		List<EntidadesMarcadasParaExportacao> lista = q.list();
		return lista;
	}
	
	
	
	
	
	/**
	 *   Método usado para buscar uma autoridade e trazer todas as suas informações. Normalmente usado quando 
	 * é preciso visualizar as informações completas da autoridade.
	 * 
	 * Reduz o número de conexão que o hibernate precisa fazer com o banco, otimizando assim a consulta.
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Autoridade findAutoriadeByIdInicializandoDados(Integer id) throws DAOException{
		
		String hql = new String( " SELECT a FROM Autoridade a "
				+" INNER JOIN FETCH a.camposControle as controle "
				+" INNER JOIN FETCH a.camposDados as dados "
				+" INNER JOIN FETCH dados.etiqueta as etiqueta "
				+" INNER JOIN FETCH controle.etiqueta as etiquetaControle "
				+" INNER JOIN FETCH dados.subCampos as subCampos " 
				+" WHERE a.id  = :idAutoridade");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAutoridade", id);
		return  (Autoridade) q.uniqueResult();
		
	}
	
	/**
	 *   Método que retorna apenas se a autoridade está ativo ou não no banco.
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public boolean findSeAutoridadeEstaAtivo(Integer id) throws DAOException{
		
		String hql = new String( " SELECT a.ativo FROM Autoridade a "
				+" WHERE a.id  = :idAutoridade");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAutoridade", id);
		return   (Boolean) q.uniqueResult();
		
	}
	
	
	/**
	 *     Encontra a última data na qual a autoridade foi alterado.
	 *     
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 */
	public Date findUltimaDataAlteracaoAutoridade (int idAutoridade) throws DAOException {
		
		String sql = "select data_operacao from biblioteca.historico_alteracao_autoridade where id_autoridade = "+idAutoridade+" order by data_operacao desc " + BDUtils.limit(1);
		
		return (Date) getSession().createSQLQuery(sql).uniqueResult();
	}
	
	
	/**
	 *    Encontra todas as autoridades com catalogações incompletas do sistema.
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> findAllAutoridadesComCatalogacaoIncompleta() throws DAOException{
		
		return  findAllAutoridadesComCatalogacaoIncompletaDoUsuario(null);
		
	}
	

	/**
	 *    Encontra todos as autoridades com catalogações incompletas do sistema realizadas pelo
	 *    usuário passado.
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public List<CacheEntidadesMarc> findAllAutoridadesComCatalogacaoIncompletaDoUsuario(int idUsuario) throws DAOException{
		return findAllAutoridadesComCatalogacaoIncompletaDoUsuario((Integer)idUsuario);
	}
	
	
	/**
	 *    Encontra todos as autoridades com catalogações incompletas do sistema realizadas pelo
	 *    usuário passado.
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	private List<CacheEntidadesMarc> findAllAutoridadesComCatalogacaoIncompletaDoUsuario(Integer idUsuario) throws DAOException{

		StringBuilder sql = new StringBuilder( 
				" SELECT  " +
				" 	 cem.id_cache_entidades_marc, " +
				"	 cem.id_autoridade, " +
				"	 cem.numero_do_sistema, " +
				"	 cem.entrada_autorizada_autor, " +
				"	 cem.nomes_remissivos_autor, " +
				"	 cem.entrada_autorizada_assunto, " +
				"	 cem.nomes_remissivos_assunto, " +
				"	 cem.campo_entrada_autorizada, " +
				"	 cem.catalogado, " +
				"	 a.importada,  " +
				"	 pessoa.nome  " +
				" FROM " +
				"	 biblioteca.cache_entidades_marc cem " +
				"	 INNER JOIN biblioteca.autoridade a ON cem.id_autoridade = a.id_autoridade " +
				"    INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = a.id_registro_criacao " + 
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
			cache.setIdAutoridade( (Integer) cacheTemp[1]);
			cache.setNumeroDoSistema( (Integer) cacheTemp[2]);
			cache.setEntradaAutorizadaAutor( (String) cacheTemp[3]);
			cache.setNomesRemissivosAutor( (String) cacheTemp[4]);
			cache.setEntradaAutorizadaAssunto( (String) cacheTemp[5]);
			cache.setNomesRemissivosAssunto( (String) cacheTemp[6]);
			cache.setCampoEntradaAutorizada( String.valueOf(cacheTemp[7]));
			cache.setCatalogado( (Boolean) cacheTemp[8]);
			cache.setImportado( (Boolean) cacheTemp[9]);
			cache.setNomeUsuario( (String) cacheTemp[10]);
			
			caches.add(cache );
			
		}
		
		return  caches;
		
	}
	
	
	
	
	
	/**
	 *    Encontra todas as autoridades com catalogações incompletas do sistema.
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Long countAllAutoridadesComCatalogacaoIncompleta() throws DAOException{
		
		StringBuilder hql = new StringBuilder( " SELECT count(a.id) " );
		hql.append(" FROM CacheEntidadesMarc a ");
		hql.append(" WHERE a.idAutoridade is not null ");
		hql.append(" AND a.catalogado = falseValue() ");
		
		Query q = getSession().createQuery(hql.toString());
		return  (Long) q.uniqueResult();
	}
	
	
	
	
	/**
	 * Registra as alterações de uma autoridade na tabela historico_alteracao_autoridade
	 * 
	 * @param titulo
	 * @param descricao
	 * @throws DAOException
	 */
	public void registraAlteracaoAutoridade(Autoridade autoridade, boolean atualizando) throws DAOException{
		
		String sql = " INSERT INTO biblioteca.historico_alteracao_autoridade "
			+"(id_historico_alteracao_autoridade, id_autoridade, id_registro_entrada, descricao_operacao, data_operacao)"
			+" VALUES ( nextval('biblioteca.historicos_alteracao_sequence'), ?, ?, ?, ?)";
		
				int idAutoridade = autoridade.getId();
				int idRegistroEntrada = 0;
				
				if(atualizando)
					idRegistroEntrada = autoridade.getRegistroUltimaAtualizacao().getId();
				else
					idRegistroEntrada = autoridade.getRegistroCriacao().getId();
				
				
				update(sql, idAutoridade, idRegistroEntrada, autoridade.toString(), new java.sql.Timestamp( new Date().getTime()) );
				
	}
	
	
	/**
	 * Encontra os títulos entre os número do sistema passado
	 *
	 * @param numeroSistema
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<CacheEntidadesMarc> findByNumerosSistema(Integer primeiroNumero, Integer ultimoNumero) throws DAOException{
		
		StringBuilder hqlSelect = new StringBuilder( " SELECT DISTINCT t FROM CacheEntidadesMarc t WHERE t.numeroDoSistema between :primeiroNumero AND :ultimoNumero AND idAutoridade is not null " );
		
		Query q = getSession().createQuery(hqlSelect.toString());
		q.setInteger("primeiroNumero", primeiroNumero);
		q.setInteger("ultimoNumero", ultimoNumero);
		
		@SuppressWarnings("unchecked")
		List<CacheEntidadesMarc> lista = q.list();
		return lista;
		
	}
	
	
	/**
	 * Conta quantas autoridades existem entre a faixa de números do sistema passados
	 *
	 * @param numeroSistema
	 * @return
	 * @throws DAOException 
	 */
	public Long countByNumerosSistema(Integer primeiroNumero, Integer ultimoNumero) throws DAOException{
		
		StringBuilder hqlSelect = new StringBuilder( " SELECT  COUNT(DISTINCT t) FROM CacheEntidadesMarc t WHERE t.numeroDoSistema between :primeiroNumero AND :ultimoNumero AND idAutoridade is not null " );
		
		Query q = getSession().createQuery(hqlSelect.toString());
		q.setInteger("primeiroNumero", primeiroNumero);
		q.setInteger("ultimoNumero", ultimoNumero);
		
		return (Long) q.uniqueResult();
		
	}
	
	/**
	 *   Encontra uma autoridade cache pelo número do sistema
	 *
	 * @param numeroSistema
	 * @return
	 * @throws DAOException 
	 */
	public CacheEntidadesMarc findByNumeroSistema(Integer numeroDoSistema) throws DAOException{
		StringBuilder sqlSelect = new StringBuilder( " SELECT  ");
		sqlSelect.append(PROJECAO_COMUM_BUSCA_ACERVO);
		sqlSelect.append(" FROM biblioteca.cache_entidades_marc a ");
		sqlSelect.append(" WHERE a.numero_do_sistema = ? AND a.id_autoridade is not null ");
		
		Connection con = null;
		List<CacheEntidadesMarc> autoridades = new ArrayList<CacheEntidadesMarc>();
		
		try{
		
			con = Database.getInstance().getSigaaConnection();
	
			PreparedStatement prepared = con.prepareStatement(sqlSelect.toString());
			prepared.setInt(1, numeroDoSistema);
		
			autoridades  =  montaResultadosConsulta(prepared.executeQuery());
			
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}	
		}
		
		if(autoridades.size() > 0)
			return (CacheEntidadesMarc) autoridades.get(0);
		else
			return null;
	}
	
	
	
	
	/**
	 * Conta as autoridades de autor existentes no banco com o mesmo nome para não deixar inserir duplicado.
	 *
	 *  <p>Tem que verificar se não o mesmo número do sistema, pois a verificação não pode ser na momento da criação, 
	 *    visto que autoridades importadas são salvas diretamente sem validação.</p>
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param nomePricipalAutoridade
	 * @return
	 * @throws DAOException
	 */
	public int countAutoridadesAutorDuplicadas(String autorAutoridadoAutoridade, Integer numeroDoSistema) throws DAOException{
			StringBuilder hql = new StringBuilder(" SELECT count(DISTINCT autoridadeCache.id)  FROM CacheEntidadesMarc as autoridadeCache ");
		
			hql.append(" WHERE autoridadeCache.idAutoridade is not null AND autoridadeCache.numeroDoSistema != :numeroDoSistema "
					+" AND autoridadeCache.catalogado = trueValue() "); 
	
			if( StringUtils.notEmpty(autorAutoridadoAutoridade)){
				hql.append(" AND autoridadeCache.entradaAutorizadaAutor = :autorAutorizado "); 
			}
			
			Query q = getSession().createQuery(hql.toString());
			q.setString("autorAutorizado", autorAutoridadoAutoridade);
			q.setInteger("numeroDoSistema", numeroDoSistema);
			return  ((Long) q.uniqueResult()).intValue();
	}
	
	
	
	/**
	 * 
	 * Conta as autoridades de assuntos existentes no banco com o mesmo nome para não deixar inserir duplicado.
	 *
	 * <p>Tem que verificar se não o mesmo número do sistema, pois a verificação não pode ser na momento da criação, 
	 *    visto que autoridades importadas são salvas diretamente sem validação.</p>
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param nomePricipalAutoridade
	 * @return
	 * @throws DAOException
	 */
	public int countAutoridadesAssuntoDuplicadas(String assuntoAutorizadoAutoridade, Integer numeroDoSistema) throws DAOException{
			StringBuilder hql = new StringBuilder(" SELECT count(DISTINCT autoridadeCache.id)  FROM CacheEntidadesMarc as autoridadeCache ");
		
			hql.append(" WHERE 1 = 1 AND autoridadeCache.idAutoridade is not null AND autoridadeCache.numeroDoSistema != :numeroDoSistema " 
					+" AND autoridadeCache.catalogado = trueValue() "); 
	
			if( StringUtils.notEmpty(assuntoAutorizadoAutoridade)){
				hql.append(" AND autoridadeCache.entradaAutorizadaAssunto = :assuntoAutorizado "); 
			}
			
			Query q = getSession().createQuery(hql.toString());
			q.setString("assuntoAutorizado",  assuntoAutorizadoAutoridade);
			q.setInteger("numeroDoSistema", numeroDoSistema);
			return  ((Long) q.uniqueResult()).intValue();	
	}
	
	
	/**
	 * <p>Método que implementa a busca simples do acervo.<p>
	 * 
	 * <p>Busca uma autoridade cadastrada pelo nome do autor na entrada autorizada ou remissiva.<p>
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param nomeAutorizado
	 * @param nomeRemissivo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<CacheEntidadesMarc> buscaSimplesAutorAcervoAutoridades(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao, String autor) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		StringBuilder sql = new StringBuilder( " SELECT ");
		sql.append(PROJECAO_COMUM_BUSCA_ACERVO);
		sql.append(" FROM biblioteca.cache_entidades_marc a ");
		
		Connection con = null;
		List<CacheEntidadesMarc> autoridades = new ArrayList<CacheEntidadesMarc>();
		
		try{
		
			con = Database.getInstance().getSigaaConnection();
			
			List<String> listaParametos = new ArrayList<String>();
			
			if(StringUtils.isEmpty(autor) )
				sql.append(" WHERE 1 = 0 ");  // não traga nenhum resultado
			else
				sql.append(" WHERE 1 = 1 ");
			
			sql.append(" AND a.id_autoridade is not null ");
			
			
			
			if(StringUtils.notEmpty(autor)){
				
				String[] nomesAutoresBusca = BibliotecaUtil.retornaPalavrasBusca(autor); //separa os nomes pelo espaço
				
				if(nomesAutoresBusca.length == 0)
					sql.append(" AND ( 1 = 0 ) ");   // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					
					// 100, 110 ou 111 $a de autoridades
					sql.append(" AND (  (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.entrada_autorizada_autor_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(nomesAutoresBusca) );
					
					// ou 400, 410, 411 $a de autoridades
					sql.append(" OR (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.nomes_remissivos_autor_ascii") +" ) ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(nomesAutoresBusca) );
				}
				
			}	
				
			
			if(campoOrdenacao != null)
				sql.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			sql.append( BDUtils.limit( LIMITE_BUSCA_AUTORIDADE ) );
			
			System.out.println("Parametros >>>");
			for (String  string: listaParametos) {
				System.out.println(string);
			}
			System.out.println(sql.toString());
			
			PreparedStatement prepared = con.prepareStatement(sql.toString());
			
			for (int posicaoParametro = 1; posicaoParametro <= listaParametos.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaParametos.get(posicaoParametro-1));
			}
			
			autoridades  =  montaResultadosConsulta(prepared.executeQuery());
			
			System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
			System.out.println("Consulta altoridades demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");
			
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}	
		}
		
		return autoridades;
	}
	
	
	/**
	 * Busca uma autoridade cadastrada pelo nome autorizado ou remissivo 
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param nomeAutorizado
	 * @param nomeRemissivo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<CacheEntidadesMarc> buscaMultiCampoAutorAcervoAutoridades(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao, String nomeAutorizado, String nomeRemissivo) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		StringBuilder sql = new StringBuilder( " SELECT ");
		sql.append(PROJECAO_COMUM_BUSCA_ACERVO);
		sql.append(" FROM biblioteca.cache_entidades_marc a ");
		
		Connection con = null;
		List<CacheEntidadesMarc> autoridades = new ArrayList<CacheEntidadesMarc>();
		
		try{
		
			con = Database.getInstance().getSigaaConnection();
			
			List<String> listaParametos = new ArrayList<String>();
			
			if(StringUtils.isEmpty(nomeAutorizado) && StringUtils.isEmpty(nomeRemissivo))
				sql.append(" WHERE 1 = 0 ");  // não traga nenhum resultado
			else
				sql.append(" WHERE 1 = 1 ");
			
			sql.append(" AND a.id_autoridade is not null ");
			
			// 100, 110 ou 111 $a de autoridades
			
			if(StringUtils.notEmpty(nomeAutorizado)){
				
				String[] nomesAutorizadosBusca = BibliotecaUtil.retornaPalavrasBusca(nomeAutorizado); //separa os nomes pelo espaço
				
				if(nomesAutorizadosBusca.length == 0)
					sql.append(" AND ( 1 = 0 ) ");   // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					sql.append(" AND (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.entrada_autorizada_autor_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(nomesAutorizadosBusca) );
				}
				
			}	
				
			// ou 400, 410, 411 $a de autoridades
			if(StringUtils.notEmpty(nomeRemissivo)){
				
				String[] nomesRemissivosBusca = BibliotecaUtil.retornaPalavrasBusca(nomeRemissivo); //separa os nomes pelo espaço
				
				if(nomesRemissivosBusca.length == 0)
					sql.append(" AND ( 1 = 0 ) ");   // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					sql.append(" AND (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.nomes_remissivos_autor_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(nomesRemissivosBusca) );
				}
				
			}
			
			if(campoOrdenacao != null)
				sql.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			sql.append( BDUtils.limit( LIMITE_BUSCA_AUTORIDADE ) );
			
			System.out.println("Parametros >>>");
			for (String  string: listaParametos) {
				System.out.println(string);
			}
			System.out.println(sql.toString());
			
			PreparedStatement prepared = con.prepareStatement(sql.toString());
			
			for (int posicaoParametro = 1; posicaoParametro <= listaParametos.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaParametos.get(posicaoParametro-1));
			}
			
			autoridades  =  montaResultadosConsulta(prepared.executeQuery());
			
			System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
			System.out.println("Consulta altoridades demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");
			
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}	
		}
		
		return autoridades;
	}
	
	/**
	 * Busca uma autoridade de assunto pelo nome autorizado ou remissivo.
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param nomeAutorizado
	 * @param nomeRemissivo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<CacheEntidadesMarc> buscaSimplesAssuntoAcervoAutoridades(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao, String assunto) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		StringBuilder sql = new StringBuilder( " SELECT  ");
		sql.append(PROJECAO_COMUM_BUSCA_ACERVO);
		sql.append(" FROM biblioteca.cache_entidades_marc a ");
		
		Connection con = null;
		List<CacheEntidadesMarc> autoridades = new ArrayList<CacheEntidadesMarc>();
		
		try{
		
			con = Database.getInstance().getSigaaConnection();
			
			List<String> listaParametos = new ArrayList<String>();
			
			if(StringUtils.isEmpty(assunto))
				sql.append(" WHERE 1 = 0 ");  // não traga nenhum resultado
			else
				sql.append(" WHERE 1 = 1 ");
			
			sql.append(" AND a.id_autoridade is not null ");
			
			
			
			if(StringUtils.notEmpty(assunto)){
				
				String[] assuntosBusca = BibliotecaUtil.retornaPalavrasBusca(assunto); //separa os nomes pelo espaço
				
				
				if(assuntosBusca.length == 0)
					sql.append(" AND ( 1 = 0 ) ");   // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					
					// 100, 110 ou 111 $a de autoridades
					sql.append(" AND ( (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.entrada_autorizada_assunto_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(assuntosBusca) );
					// ou 400, 410, 411 $a de autoridades
					sql.append(" OR (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.nomes_remissivos_assunto_ascii") +" ) ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(assuntosBusca) );
				}
				
			}
			
			if(campoOrdenacao != null)
				sql.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			sql.append( BDUtils.limit( LIMITE_BUSCA_AUTORIDADE ) );
			
			System.out.println("Parametros >>>");
			for (String  string: listaParametos) {
				System.out.println(string);
			}
			System.out.println(sql.toString());
			
			PreparedStatement prepared = con.prepareStatement(sql.toString());
			
			for (int posicaoParametro = 1; posicaoParametro <= listaParametos.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaParametos.get(posicaoParametro-1));
			}
			
			autoridades  =  montaResultadosConsulta(prepared.executeQuery());
		
			System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
			System.out.println("Consulta altoridades demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");
			
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}	
		}
		
		return autoridades;
	}
	
	
	
	/**
	 * Busca uma autoridade cadastrada pelo nome autorizado ou remissivo 
	 *
	 * Chamado a partir da página: /biblioteca/
	 * @param nomeAutorizado
	 * @param nomeRemissivo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<CacheEntidadesMarc> buscaMultiCampoAssuntoAcervoAutoridades(GeraPesquisaTextual geradorPesquisa, CampoOrdenacaoConsultaAcervo campoOrdenacao, String nomeAutorizado, String nomeRemissivo) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		StringBuilder sql = new StringBuilder( " SELECT  ");
		sql.append(PROJECAO_COMUM_BUSCA_ACERVO);
		sql.append(" FROM biblioteca.cache_entidades_marc a ");
		
		Connection con = null;
		List<CacheEntidadesMarc> autoridades = new ArrayList<CacheEntidadesMarc>();
		
		try{
		
			con = Database.getInstance().getSigaaConnection();
			
			List<String> listaParametos = new ArrayList<String>();
			
			if(StringUtils.isEmpty(nomeAutorizado) && StringUtils.isEmpty(nomeRemissivo))
				sql.append(" WHERE 1 = 0 ");  // não traga nenhum resultado
			else
				sql.append(" WHERE 1 = 1 ");
			
			sql.append(" AND a.id_autoridade is not null ");
			
			// 100, 110 ou 111 $a de autoridades
			
			if(StringUtils.notEmpty(nomeAutorizado)){
				
				String[] nomesAutorizadosBusca = BibliotecaUtil.retornaPalavrasBusca(nomeAutorizado); //separa os nomes pelo espaço
				
				
				if(nomesAutorizadosBusca.length == 0)
					sql.append(" AND ( 1 = 0 ) ");   // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					sql.append(" AND (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.entrada_autorizada_assunto_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(nomesAutorizadosBusca) );
				}
				
			}	
				
			// ou 400, 410, 411 $a de autoridades
			if(StringUtils.notEmpty(nomeRemissivo)){
				
				String[] nomesRemissivosBusca = BibliotecaUtil.retornaPalavrasBusca(nomeRemissivo); //separa os nomes pelo espaço
				
				if(nomesRemissivosBusca.length == 0)
					sql.append(" AND ( 1 = 0 ) ");   // Não existe palavras para a busca, usuário digitou por exemplo: "a a a a a a";
				else{
					sql.append(" AND (  "+ geradorPesquisa.gerarMecanismoPesquisaTextual("a.nomes_remissivos_assunto_ascii") +" ) " );
					listaParametos.add( geradorPesquisa.formataTextoPesquisaTextual(nomesRemissivosBusca) );
				}
				
			}
			
			if(campoOrdenacao != null)
				sql.append( "ORDER BY "+campoOrdenacao.getColunaOrdenacao());
			
			sql.append( BDUtils.limit( LIMITE_BUSCA_AUTORIDADE ) );
			
			System.out.println("Parametros >>>");
			for (String  string: listaParametos) {
				System.out.println(string);
			}
			System.out.println(sql.toString());
			
			PreparedStatement prepared = con.prepareStatement(sql.toString());
			
			for (int posicaoParametro = 1; posicaoParametro <= listaParametos.size(); posicaoParametro++) {
				prepared.setString(posicaoParametro, listaParametos.get(posicaoParametro-1));
			}
			
			autoridades  =  montaResultadosConsulta(prepared.executeQuery());
		
			System.out.println(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  ");
			System.out.println("Consulta altoridades demorou: "+ (System.currentTimeMillis() - tempo) +" ms ");
			
		}catch(SQLException sqlEx){
			throw new DAOException(sqlEx.getMessage());
		}finally{
			try{
				if(con != null) con.close();
			}catch(SQLException sqlEx2){
				throw new DAOException(sqlEx2.getMessage());
			}	
		}
		
		return autoridades;
	}
	
	
	
	/** Método que pega os campos  retornados da projeção das busca e monta a lista de objetos cache
	 * que é retornado para a visualização.
	 */
	private List<CacheEntidadesMarc> montaResultadosConsulta(ResultSet resultSet) throws SQLException{
		
		List<CacheEntidadesMarc> autoridadesResultadosTemp = new ArrayList<CacheEntidadesMarc>();

		int cont = 1;
		while (resultSet.next()) { 

			cont = 1;   // lendo um novo objeto cache
			
			CacheEntidadesMarc cache = new CacheEntidadesMarc();
			cache.setId( resultSet.getInt(cont++) );
			cache.setIdAutoridade( resultSet.getInt(cont++) );
			cache.setNumeroDoSistema( resultSet.getInt(cont++) );
			cache.setEntradaAutorizadaAutor( resultSet.getString(cont++) );
			cache.setNomesRemissivosAutor( resultSet.getString(cont++)  );
			cache.setEntradaAutorizadaAssunto( resultSet.getString(cont++)  );
			cache.setNomesRemissivosAssunto( resultSet.getString(cont++)  );
			cache.setCampoEntradaAutorizada( resultSet.getString(cont++)  );
			cache.setCatalogado( resultSet.getBoolean(cont++)  );
			
			autoridadesResultadosTemp.add(cache);
		}

		return autoridadesResultadosTemp;
	}
	
	
	
	/**
	 * Retorna uma lista de Modificações de uma autoridade, geralmente usado no histórico de
	 * alterações ou na visualização da alterações na página de catalogação.
	 * 
	 * @param idTitulo
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> findAlteracoesByAutoridadePeriodo (int idAutoridade, Date dataInicio, Date dataFim, Integer limiteResultados) throws DAOException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String sql = "select p.nome, h.descricao_operacao, h.data_operacao " +
					"from biblioteca.historico_alteracao_autoridade h " +
					"join comum.registro_entrada r on r.id_entrada = h.id_registro_entrada " +
					"join comum.usuario u on u.id_usuario = r.id_usuario " +
					"join comum.pessoa p on p.id_pessoa = u.id_pessoa " +
					"where h.id_autoridade = " + idAutoridade;

		if (dataInicio != null)
			sql += " and data_operacao >= '"+sdf.format(dataInicio)+"' ";
		if (dataFim != null)
			sql += "and data_operacao <= '"+sdf.format(dataFim)+" 23:59:59' ";
					
		sql += "order by data_operacao desc";
		
		if(limiteResultados != null){
			sql += BDUtils.limit(limiteResultados);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql).list();
		return lista;
	}
	
}
