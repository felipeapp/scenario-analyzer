/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 30/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.PerfilInteresseUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * <p>Dao para as consultas exclusivas para a parte de disseminação da informação do módulo de biblioteca. </p>
 *
 * 
 * @author jadson
 *
 */
public class DisseminacaoDaInformacaoDao extends GenericSigaaDAO{

	
	/**
	 * <p>Retorna o perfil de interesse de um usuário na biblioteca.</p>
	 *
	 * <p>É a partir desse perfil de interesse que são obtidas as informações que o usuário deseja receber pelo sistema.</p>
	 *
	 * @return
	 * @throws DAOException
	 */
	public PerfilInteresseUsuarioBiblioteca findPerfilUsuarioBiblioteca(UsuarioBiblioteca ub) throws DAOException{
		
		String projecao = " perfil.id, perfil.receberInformativoNovasAquisicoes, perfil.areaDoInformativo.id, "
			+" assuntos.id, assuntos.numeroDoSistema, "
			+" autores.id, autores.numeroDoSistema, " 
			+" bibliotecas.id ";
		
		String hql = " SELECT "+projecao
			+" FROM PerfilInteresseUsuarioBiblioteca perfil "
			+" LEFT JOIN perfil.assuntosDeInteresse assuntos "
			+" LEFT JOIN perfil.autoresDeInteresse autores "
			+" LEFT JOIN perfil.bibliotecasDeInteresse bibliotecas "
			+" WHERE perfil.usuario.id = :idUsuarioBiblioteca ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuarioBiblioteca", ub.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista =  q.list();
		
		if(lista != null && lista.size() > 0){
			
			PerfilInteresseUsuarioBiblioteca retorno = new PerfilInteresseUsuarioBiblioteca();
			
			retorno.setId( (Integer) lista.get(0)[0]);
			retorno.setReceberInformativoNovasAquisicoes( (Boolean) lista.get(0)[1]);
			
			Integer idAreasInformativo = (Integer) lista.get(0)[2];
			
			if(idAreasInformativo != null)
				retorno.setAreaDoInformativo( new AreaConhecimentoCnpq(idAreasInformativo));
			else
				retorno.setAreaDoInformativo( new AreaConhecimentoCnpq(-1));
			
			// Retorna 1 array para cada autor ou assunto que o usuário marcou interesse //
			for (Object[] object : lista) {
				if(object[3] != null){
					Autoridade autoridade = new Autoridade( (Integer) object[3]);
					autoridade.setNumeroDoSistema(  (Integer) object[4]  );
					retorno.adicionaAssuntosDeInteresse(autoridade);
				}
				if(object[5] != null){
					Autoridade autoridade = new Autoridade( (Integer) object[5]);
					autoridade.setNumeroDoSistema(  (Integer) object[6]  );
					retorno.adicionaAutoresDeInteresse(autoridade);
				}
				if(object[7] != null)
					retorno.adicionaBibliotecaInteresse(new Biblioteca( (Integer) object[7]));
			}
			
			retorno.setUsuario(ub);
			return retorno;
		}else{
			PerfilInteresseUsuarioBiblioteca retorno = new PerfilInteresseUsuarioBiblioteca();
			retorno.setAreaDoInformativo( new AreaConhecimentoCnpq(-1));
			retorno.setUsuario(ub);
			return retorno; // Caso não tenha, retorna um perfil vazio !
		}	
	}
	
	/**
	 * Retorna as informações necessárias para exibição no caso de uso de DSI no sistema.
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<CacheEntidadesMarc> polulaInformacoesAutoridadeAssunto(List<Autoridade> autoridadesAssunto) throws DAOException {
		
		String hql = getSqlComumBuscaAssuntosAutorizados(false);
		hql += " AND c.idAutoridade IN "+UFRNUtils.gerarStringIn(autoridadesAssunto);
		Query q = getSession().createQuery(hql);
		
		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(
				HibernateUtils.parseTo(q.list(), getProjecaoComumBuscaAssuntosAutorizados(false), CacheEntidadesMarc.class, "c"));
		return lista;
	}
	
	
	/**
	 * Retorna as informações necessárias para exibição no caso de uso de DSI no sistema.
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<CacheEntidadesMarc> polulaInformacoesAutoridadeAutor(List<Autoridade> autoridadesAssunto) throws DAOException {
		
		String hql = getSqlComumBuscaAutoresAutorizados(false);
		hql += " AND c.idAutoridade IN "+UFRNUtils.gerarStringIn(autoridadesAssunto);
		Query q = getSession().createQuery(hql);
		
		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(
				HibernateUtils.parseTo(q.list(), getProjecaoComumBuscaAutoresAutorizados(false), CacheEntidadesMarc.class, "c"));
		return lista;
	}
	
	
	
	
	/* *******************************************************************************************
	 * **************************  Para a busca de assuntos  ************************** 
	 * *******************************************************************************************/
	
	
	/**
	 * Retorna um assunto autorizada a partir do texto passado. 
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<CacheEntidadesMarc> findAssuntosAutorizadosByDescricao(String descricao) throws DAOException {
		
		String hql = getSqlComumBuscaAssuntosAutorizados(false);
		
		String[] palavrasParaBusca = BibliotecaUtil.retornaPalavrasBusca(StringUtils.toAsciiAndUpperCase(descricao));
		
		for (int ptr = 0; ptr < palavrasParaBusca.length; ptr++) {
			hql += " AND c.entradaAutorizadaAssuntoAscii like :descricao"+ptr;
		}
		
		hql += " ORDER BY c.entradaAutorizadaAssunto ";
		
		Query q = getSession().createQuery(hql);
		
		for (int ptr = 0; ptr < palavrasParaBusca.length; ptr++) {
			q.setString("descricao"+ptr, "%"+palavrasParaBusca[ptr]+"%");
		}
		
		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(
				HibernateUtils.parseTo(q.list(), getProjecaoComumBuscaAssuntosAutorizados(false), CacheEntidadesMarc.class, "c"));
		return lista;
	}
	
	
	
	
	
//	/**
//	 * Retorna a quantidade dos assuntos autorizado para o usuário registrar interesse.
//	 *
//	 * @return
//	 * @throws DAOException
//	 */
//	public Integer countAllAssuntosAutorizados(Character letraInicialAssunto) throws DAOException{
//		
//		String hql = getSqlComumBuscaAssuntosAutorizados(true);
//		
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			hql += " AND c.entradaAutorizadaAssunto like :letraInicialAssunto ";
//		}
//		
//		Query q = getSession().createQuery(hql);
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			q.setString("letraInicialAssunto", letraInicialAssunto+"%");
//		}
//		
//		return ((Long) q.uniqueResult()).intValue();
//		
//	}
	
	
	
//	/**
//	 * Retorna todas as entradas autorizadas de assunto da base de autoridades para o usuário indicar 
//	 * o interesse em acompanhar esse assunto.
//	 *
//	 * @return
//	 * @throws DAOException
//	 */
//	public List<CacheEntidadesMarc> findAllAssuntosAutorizados(Character letraInicialAssunto, int pagina, int limite) throws DAOException{
//		
//		String hql = getSqlComumBuscaAssuntosAutorizados(false);
//		
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			hql += " AND c.entradaAutorizadaAssunto like :letraInicialAssunto ";
//		}
//		
//		hql += " ORDER BY c.entradaAutorizadaAssunto ";
//		
//		Query q = getSession().createQuery(hql);
//		
//		
//		
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			q.setString("letraInicialAssunto", letraInicialAssunto+"%");
//		}
//		
//		q.setFirstResult((pagina-1) * limite);
//		q.setMaxResults(limite);
//		
//		@SuppressWarnings("unchecked")
//		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(
//				HibernateUtils.parseTo(q.list(), getProjecaoComumBuscaAssuntosAutorizados(false), CacheEntidadesMarc.class, "c"));
//		return lista;
//	}
	
	
	
//	/**
//	 * <p>Retorna a posição do assunto do assunto dentro do total de assuntos cadastrados para interesse. </p>
//	 * 
//	 * <p>Utilizado para fazer a páginação quando o usuário escolhe apenas assuntos iniciados com um letra específica.
//	 * É preciso saber onde aqueles assuntos estarão localizados dentro do total de assuntos.</p>
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public Integer findPosicaoAssuntoDentroDoTotalAssuntos(Character letraInicialAssunto) throws DAOException{
//		
//		String hql = getSqlComumBuscaAssuntosAutorizados(true);
//		
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			hql += " AND c.entradaAutorizadaAssunto < :letraInicialAssunto ";
//		}
//		
//		Query q = getSession().createQuery(hql);
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			q.setString("letraInicialAssunto", letraInicialAssunto+"%");
//		}
//		
//		return ((Long) q.uniqueResult()).intValue();
//		
//	}
	
	
	
	
	/**
	 * Retorna a parte comum da consulta utilizada nas duas buscas de resultados dos assuntos autorizados.
	 *
	 * @param contarResultados
	 * @return
	 */
	private String getSqlComumBuscaAssuntosAutorizados(boolean contarResultados){
		
		String projecao = getProjecaoComumBuscaAssuntosAutorizados(contarResultados);
		
		String hql = " SELECT "+projecao+" FROM CacheEntidadesMarc c "+  
					 " WHERE c.idAutoridade IS NOT NULL "+
					 " AND ( " +
					 "    c.campoEntradaAutorizada = '"+Etiqueta.CABECALHO_TOPICOS.getTag()+"'"+
					 " OR c.campoEntradaAutorizada = '"+Etiqueta.CABECALHO_NOME_GEOGRAFICO.getTag()+"'"+
					 " OR c.campoEntradaAutorizada = '"+Etiqueta.CABECALHO_GERAL_SUBDIVISAO.getTag()+"'"+
					 " )";
		
		return hql;
	}
	
	
	
	
	
	/** Retorna a projeção comum utilizada nas duas buscas de resultados dos assuntos autorizados. */
	private String getProjecaoComumBuscaAssuntosAutorizados(boolean contarResultados){
		if(contarResultados)
			return " COUNT (DISTINCT c.idAutoridade ) ";
		else
			return " c.id, c.idAutoridade, c.numeroDoSistema, c.entradaAutorizadaAssunto ";
	}

	
	
	
	
	
	
	/* *******************************************************************************************
	 * **************************  Para a busca de autores  ************************** 
	 * *******************************************************************************************/
	

	/**
	 * Retorna um assunto autorizada a partir do texto passado. 
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<CacheEntidadesMarc> findAutoresAutorizadosByDescricao(String descricao) throws DAOException {
		
		String hql = getSqlComumBuscaAutoresAutorizados(false);
		
		String[] palavrasParaBusca = BibliotecaUtil.retornaPalavrasBusca(StringUtils.toAsciiAndUpperCase(descricao) );
		
		for (int ptr = 0; ptr < palavrasParaBusca.length; ptr++) {
			hql += " AND c.entradaAutorizadaAutorAscii like :descricao"+ptr;
		}
		
		hql += " ORDER BY c.entradaAutorizadaAutor ";
		
		Query q = getSession().createQuery(hql);
		
		for (int ptr = 0; ptr < palavrasParaBusca.length; ptr++) {
			q.setString("descricao"+ptr, "%"+palavrasParaBusca[ptr]+"%");
		}
		
		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(
				HibernateUtils.parseTo(q.list(), getProjecaoComumBuscaAutoresAutorizados(false), CacheEntidadesMarc.class, "c"));
		return lista;
		
	}
	
	
	
	
//	/**
//	 * Retorna a quantidade dos autores autorizado para o usuário registrar interesse.
//	 *
//	 * @return
//	 * @throws DAOException
//	 */
//	public Integer countAllAutoresAutorizados(Character letraInicialAssunto) throws DAOException{
//		
//		String hql = getSqlComumBuscaAutoresAutorizados(true);
//		
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			hql += " AND c.entradaAutorizadaAutor like :letraInicialAssunto ";
//		}
//		
//		Query q = getSession().createQuery(hql);
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			q.setString("letraInicialAssunto", letraInicialAssunto+"%");
//		}
//		
//		return ((Long) q.uniqueResult()).intValue();
//		
//	}
	
	
//	/**
//	 * Retorna todas as entradas autorizadas de assunto da base de autoridades para o usuário indicar 
//	 * o interesse em acompanhar esse assunto.
//	 *
//	 * @return
//	 * @throws DAOException
//	 */
//	public List<CacheEntidadesMarc> findAllAutoresAutorizados(Character letraInicialAssunto, int pagina, int limite) throws DAOException{
//		
//		String hql = getSqlComumBuscaAutoresAutorizados(false);
//		
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			hql += " AND c.entradaAutorizadaAutor like :letraInicialAssunto ";
//		}
//		
//		hql += " ORDER BY c.entradaAutorizadaAutor ";
//		
//		Query q = getSession().createQuery(hql);
//		
//		
//		
//		if(letraInicialAssunto != null && ! letraInicialAssunto.equals('?')){
//			q.setString("letraInicialAssunto", letraInicialAssunto+"%");
//		}
//		
//		q.setFirstResult((pagina-1) * limite);
//		q.setMaxResults(limite);
//		
//		@SuppressWarnings("unchecked")
//		List<CacheEntidadesMarc> lista = new ArrayList<CacheEntidadesMarc>(
//				HibernateUtils.parseTo(q.list(), getProjecaoComumBuscaAutoresAutorizados(false), CacheEntidadesMarc.class, "c"));
//		return lista;
//	}
	
	
	
	
//	/**
//	 * <p>Retorna a posição do autor  dentro do total de assuntos cadastrados para interesse. </p>
//	 * 
//	 * <p>Utilizado para fazer a páginação quando o usuário escolhe apenas assuntos iniciados com um letra específica.
//	 * É preciso saber onde aqueles assuntos estarão localizados dentro do total de assuntos.</p>
//	 * 
//	 * @return
//	 * @throws DAOException
//	 */
//	public Integer findPosicaoAutorDentroDoTotalAutores(Character letraInicialAutor) throws DAOException{
//		
//		String hql = getSqlComumBuscaAutoresAutorizados(true);
//		
//		if(letraInicialAutor != null && ! letraInicialAutor.equals('?')){
//			hql += " AND c.entradaAutorizadaAutor < :letraInicialAutor ";
//		}
//		
//		Query q = getSession().createQuery(hql);
//		if(letraInicialAutor != null && ! letraInicialAutor.equals('?')){
//			q.setString("letraInicialAutor", letraInicialAutor+"%");
//		}
//		
//		return ((Long) q.uniqueResult()).intValue();
//		
//	}
	
	
	/**
	 * Retorna a parte comum da consulta utilizada nas duas buscas de resultados dos assuntos autorizados.
	 *
	 * @param contarResultados
	 * @return
	 */
	private String getSqlComumBuscaAutoresAutorizados(boolean contarResultados){
		
		String projecao = getProjecaoComumBuscaAutoresAutorizados(contarResultados);
		
		String hql = " SELECT "+projecao+" FROM CacheEntidadesMarc c "+  
					 " WHERE c.idAutoridade IS NOT NULL "+
					 " AND ( " +
					 "    c.campoEntradaAutorizada = '"+Etiqueta.NOME_PESSOAL.getTag()+"'"+
					 " OR c.campoEntradaAutorizada = '"+Etiqueta.NOME_CORPORATIVO.getTag()+"'"+
					 " OR c.campoEntradaAutorizada = '"+Etiqueta.NOME_EVENTO.getTag()+"'"+
					 " )";
		
		return hql;
	}
	
	
	/** Retorna a projeção comum utilizada nas duas buscas de resultados dos assuntos autorizados. */
	private String getProjecaoComumBuscaAutoresAutorizados(boolean contarResultados){
		if(contarResultados)
			return " COUNT (DISTINCT c.idAutoridade ) ";
		else
			return " c.id, c.idAutoridade, c.numeroDoSistema, c.entradaAutorizadaAutor ";
	}
	
	
	
	
	
	/* *******************************************************************************************
	 * Consultas usados para recuperar os usuários com interesse em determinado assunto ou autor 
	 * *******************************************************************************************/
	
	
	/**
	 * <p> Método que retorna todos os usuários <strong>ATIVOS NÃO AQUITADOS</strong> que indicaram interesse em receber o informativo de novas aquisições. </p>
	 * 
	 * <p> Obs 1.: Quando o usuário quita seu vínculo na biblioteca, automaticamente ele deixar de receber os informativos do seu perfil. </p>
	 * <p> Obs 2.: Retorna o nome e o email para poder enviar realizando apenas uma consulta. </p> 
	 *
	 * @return <ul> 
	 *         <li> Object[0] = nome do usuário </li>
	 *         <li> Object[1] = email do usuário </li>
	 *         <li> Object[2] = email da pessoa </li> 
	 *         <li> Object[3] = a área de conhecimento sobre a qual o usuário deseja receber o informativo, pode ser nulo então receber de todoas as áreas.</li> 
	 *         </ul>
	 * @throws DAOException 
	 * @throws  
	 */
	
	public List<Object[]> findUsuariosAtivosInteresseReceberInformativo() throws DAOException {
		
		// Obs.:  Utiliza sql porque não tem como fazer "inner joins" em HQL entre UsuariosBiblioteca -> Pessoa -> Usuario
		String sql =" SELECT DISTINCT pessoa.nome, usuario.email AS emailUsuario, pessoa.email AS emailPessoa, perfil.id_area_conhecimento_informativo " +  
					" FROM biblioteca.perfil_interesse_usuario_biblioteca perfil "+
					" INNER JOIN biblioteca.usuario_biblioteca usuarioBiblioteca ON perfil.id_usuario_biblioteca = usuarioBiblioteca.id_usuario_biblioteca "+
					" INNER JOIN comum.pessoa pessoa                             ON (usuarioBiblioteca.id_pessoa = pessoa.id_pessoa)  " +   // Apenas pessoas tem perfil de interesse por enquanto, bibliotecas não
					" INNER JOIN comum.usuario usuario                           ON (pessoa.id_pessoa = usuario.id_pessoa) "+
					" WHERE perfil.receber_informativo_novas_aquisicoes = :true "+
					" AND usuarioBiblioteca.ativo = :true AND usuarioBiblioteca.quitado = :false ";   // Quando o usuário tem seu vínculo quitado deixa de receber os informativos da biblioteca
			
		Query q = getSession().createSQLQuery(sql);
		q.setBoolean("true", true);
		q.setBoolean("false", false);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}


	
	/**
	 * <p>Retorna os ids dos materiais foram incluídos no acervo entre as datas passadas.</p>
	 * 
	 * <p>Utilizado para encontrar os materiais que foram incluídos no acervo nessa data, e a partir disso encontrar os usuários que 
	 * cadastraram interesse em assuntos ou autores dos seus Títulos.</p>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<Integer> findIdsMateriaisIncluidosAcervoEntreDatas(Date dataIncial, Date dataFinal) throws DAOException{
		
		String hqlExemplar = 
			         " SELECT exemplar.id as id FROM Exemplar exemplar " +
					 " INNER JOIN exemplar.situacao situacao"+
					 " WHERE exemplar.dataCriacao between :dataCriacaoInicio AND :dataCriacaoFinal " +
					 " AND situacao.situacaoDeBaixa = :false AND exemplar.ativo = :true ";
		
		// No caso especial do fascículo tem que ter sido incluído no acervo, não apenas registrado
		String hqlFasciculo = 
		 	" SELECT fasciculo.id as id FROM Fasciculo fasciculo " +
		 	" INNER JOIN fasciculo.situacao situacao"+
		 	" WHERE fasciculo.dataCriacao between :dataCriacaoInicio AND :dataCriacaoFinal " +
		 	" AND fasciculo.incluidoAcervo = :true AND fasciculo.ativo = :true AND situacao.situacaoDeBaixa = :false ";
		
		Query qExemplar = getSession().createQuery(hqlExemplar);
		qExemplar.setTimestamp("dataCriacaoInicio", CalendarUtils.configuraTempoDaData(dataIncial, 0, 0, 0, 0)      );
		qExemplar.setTimestamp("dataCriacaoFinal" , CalendarUtils.configuraTempoDaData(dataFinal, 23, 59, 59, 999)  );
		qExemplar.setBoolean("true", true);
		qExemplar.setBoolean("false", false);
		
		@SuppressWarnings("unchecked")
		List<Integer> idsExemplares = (List<Integer>) qExemplar.list();
		
		Query qFasciculo = getSession().createQuery(hqlFasciculo);
		qFasciculo.setTimestamp("dataCriacaoInicio", CalendarUtils.configuraTempoDaData(dataIncial, 0, 0, 0, 0)      );
		qFasciculo.setTimestamp("dataCriacaoFinal" , CalendarUtils.configuraTempoDaData(dataFinal, 23, 59, 59, 999)  );
		qFasciculo.setBoolean("true", true);
		qFasciculo.setBoolean("false", false);
		
		@SuppressWarnings("unchecked")
		List<Integer> idsFasciculos = (List<Integer>) qFasciculo.list();
		
		idsExemplares.addAll(idsFasciculos);
		
		return idsExemplares;
	}


	/**
	 * <p>Encontra todos os usuários que indicaram interesse em autoridades refereciadas pelo Título 
	 * passado e escolheram a biblioteca do material passada ou receber informação de Todas as bibliotecas.</p>
	 *  
	 * <p><strong>Observação:</strong>  A referência ocorre quando no momento da catalogação o bibliotecário preenche os dados do Título
	 * com os assuntos ou autores autorizados da base de autoridades.</p> 
	 *  
	 * @param idTitulo o Título do material incluído no acervo.
	 * @param idBiblioteca  a biblioteca do material incluído no acervo
	 * @return um array de objetos contendo: <br/>
	 *  					   Object[0] : o id do usuário biblioteca                   <br/>  
	 *                         Object[1] : o nome do usuário                            <br/>  
	 *                         Object[2] : o email do Usuário                           <br/>  
	 *                         Object[3] : o email da pessoa                            <br/>  
	 *                         Object[4] : se é informação de assunto                   <br/> 
	 *                         Object[5] : o assunto ou autor de interesse   <br/>  
	 *                         Object[6] : a biblioteca de interesse do perfil do usuário <br/>       
	 * @throws DAOException 
	 */
	public List<Object[]> findUsuariosInteresseTituloEBibliotecaByReferencia(Integer idTitulo, Integer idBiblioteca) throws DAOException {
		
		// Os dados retornados
		List<Object[]> retorno  = new ArrayList<Object[]>();
		
		
		
		/////////// Encontra as autoridades relacionadas ao Título passado  /////////////////////
		
		String sqlFindAutoridadesRelacionadasTitulo = 
		" SELECT DISTINCT autoridade.id_autoridade "+
		" FROM biblioteca.titulo_catalografico titulo "+
		" INNER JOIN biblioteca.campo_dados cd           ON cd.id_titulo_catalografico  = titulo.id_titulo_catalografico "+
		" INNER JOIN biblioteca.sub_campo sub            ON sub.id_campo_dados = cd.id_campo_dados "+
		" INNER JOIN biblioteca.sub_campo subAutoridade  ON subAutoridade.id_sub_campo  = sub.id_sub_campo_autoridade "+
		" INNER JOIN biblioteca.campo_dados cdAutoridade ON cdAutoridade.id_campo_dados = subAutoridade.id_campo_dados "+
		" INNER JOIN biblioteca.autoridade autoridade    ON autoridade.id_autoridade = cdAutoridade.id_autoridade "+
		" WHERE titulo.id_titulo_catalografico = :idTitulo ";
		
		Query qInterna = getSession().createSQLQuery(sqlFindAutoridadesRelacionadasTitulo);
		qInterna.setInteger("idTitulo", idTitulo);
		
		
		@SuppressWarnings("unchecked")
		List<Integer> idAutoridadesByReferenciaAoTitulo = qInterna.list();
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		
		
		
		
		
		
		//////////// Encontra os usuários que cadastraram interesse nas autoridades relacionadas ao Título para a biblioteca passada //////////
		
		if(idAutoridadesByReferenciaAoTitulo.size() > 0 ){
		
			retorno.addAll(retornaInfoUsuarioRegistrouInteresseAssunto(idAutoridadesByReferenciaAoTitulo, idBiblioteca));
			
			retorno.addAll(retornaInfoUsuarioRegistrouInteresseAutor(idAutoridadesByReferenciaAoTitulo, idBiblioteca));
			
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
		}
		
		return retorno;
	}
	
	
	
	
	
	/**
	 * <p>Encontra todos os usuários que indicaram interesse em autoridades refereciadas pelo Título 
	 * passado e escolheram a biblioteca do material passada ou receber informação de Todas as bibliotecas.</p>
	 *  
	 * <p><strong>Observação:</strong>  Utilizando quando no momento da catalogação o bibliotecário NÃO preenche os dados do Título
	 * com os assuntos ou autores autorizados da base de autoridades. Neste caso busca-se as autoridades a partir do 
	 * texto contido nos campos do Título que podem contém alguma entrada autorizada da base de autoridades.</p> 
	 *  
	 * @param idTitulo o Título do material incluído no acervo.
	 * @param idBiblioteca  a biblioteca do material incluído no acervo
	 * @param idsAutoridadesJaEncontradas as autoridades já encontradas na outras busca, para não mandar email repetido para o usuário.
	 * @return um array de objetos contendo: <br/>
	 *                         Object[0] : o id do usuário biblioteca                   <br/>  
	 *                         Object[1] : o nome do usuário                            <br/>  
	 *                         Object[2] : o email do Usuário                           <br/>  
	 *                         Object[3] : o email da pessoa                            <br/>  
	 *                         Object[4] : se é informação de assunto                   <br/> 
	 *                         Object[5] : o assunto ou autor de interesse   <br/>  
	 *                         Object[6] : a biblioteca de interesse do perfil do usuário <br/>    
	 * @throws DAOException 
	 */
	public List<Object[]> findUsuariosInteresseTituloEBibliotecaByTexto(Integer idTitulo, Integer idBiblioteca) throws DAOException {
		
		// Os dados retornados
		List<Object[]> retorno  = new ArrayList<Object[]>();
		
		/** Guarda as autoridades de Assuntos encontradas a partir da busca textual nas informações do Título */ 
		List<Integer> idsAutoridadesAssuntos = new ArrayList<Integer>();
		
		/** Guarda as autoridades de Autores encontradas a partir da busca textual nas informações do Título */
		List<Integer> idsAutoridadesAutores = new ArrayList<Integer>();
		
		
		
		/* ***************************  Encontra as autoridade de assunto ************************************ */
		
		String sqlBuscaAssuntosTitulo = 
		" SELECT sub.dado "+
		" FROM biblioteca.titulo_catalografico titulo "+
		" INNER JOIN biblioteca.campo_dados cd           ON cd.id_titulo_catalografico  = titulo.id_titulo_catalografico "+
		" INNER JOIN biblioteca.etiqueta e               ON e.id_etiqueta = cd.id_etiqueta "+
		" INNER JOIN biblioteca.sub_campo sub            ON sub.id_campo_dados = cd.id_campo_dados "+
		" WHERE titulo.id_titulo_catalografico = :idTitulo "+
		" AND ( " +
		"        ( e.tag in ('"+Etiqueta.ASSUNTO_PESSOAL.getTag()+"', '"+Etiqueta.ASSUNTO_ENTIDADE.getTag()+"', '"+Etiqueta.ASSUNTO_EVENTOS.getTag()+"', '"+Etiqueta.ASSUNTO.getTag()+"', '"+Etiqueta.ASSUNTO_GEOGRAFICO.getTag()+"', '"+Etiqueta.ASSUNTO_SEM_CONTROLE.getTag()+"') AND sub.codigo = '"+SubCampo.SUB_CAMPO_A+"') "+
		"        OR (e.tag in ('"+Etiqueta.ASSUNTO.getTag()+"') AND sub.codigo = '"+SubCampo.SUB_CAMPO_X+"') " +
		" ) ";
		
		
		Query qBuscaAssuntos = getSession().createSQLQuery(sqlBuscaAssuntosTitulo);
		qBuscaAssuntos.setInteger("idTitulo", idTitulo);
		
		@SuppressWarnings("unchecked")
		List<String> assuntosTitulo =  qBuscaAssuntos.list();

		// Encontra as autoridades cuja entrada autorizada for igual ao assunto contido no Título */
		if(assuntosTitulo.size() > 0 ){
			
			//  A partir dos assuntos acha as autoridades  //
			StringBuilder sqlBuscaAutoridadesAssuntos = new  StringBuilder(
			" SELECT id_autoridade " +
			" FROM biblioteca.cache_entidades_marc c " +
			" WHERE c.id_autoridade IS NOT NULL  " + 
			" AND ( c.campo_entrada_autorizada  = '"+Etiqueta.CABECALHO_TOPICOS.getTag()+"'  " +
			"       OR c.campo_entrada_autorizada = '"+Etiqueta.CABECALHO_NOME_GEOGRAFICO.getTag()+"'  " +
			"       OR c.campo_entrada_autorizada = '"+Etiqueta.CABECALHO_GERAL_SUBDIVISAO.getTag()+"'  " +
			"       )  " +
			" AND ( 1 = 0 ");
			
			for (int posicaoParamentro = 0; posicaoParamentro < assuntosTitulo.size(); posicaoParamentro++) {
				sqlBuscaAutoridadesAssuntos.append(" OR entrada_autorizada_assunto like :assunto"+posicaoParamentro+" ");
			}
			
			sqlBuscaAutoridadesAssuntos.append(" ) ");
			
			Query qBuscaAutoridadesAssuntos = getSession().createSQLQuery(sqlBuscaAutoridadesAssuntos.toString());
			for (int posicaoParamentro = 0; posicaoParamentro < assuntosTitulo.size(); posicaoParamentro++) {
				qBuscaAutoridadesAssuntos.setString("assunto"+posicaoParamentro, "%"+assuntosTitulo.get(posicaoParamentro)+"%");
			}
			
			@SuppressWarnings("unchecked")
			List<Integer> list = qBuscaAutoridadesAssuntos.list();
			idsAutoridadesAssuntos =  list;
		}
		
		/* ******************************************************************************************* */
		
		
		
		
		
		
		/* ***************************  Encontra as autoridade de autores  ************************************ */
		String sqlBuscaAutoresTitulo = 
			" SELECT sub.dado "+
			" FROM biblioteca.titulo_catalografico titulo "+
			" INNER JOIN biblioteca.campo_dados cd           ON cd.id_titulo_catalografico  = titulo.id_titulo_catalografico "+
			" INNER JOIN biblioteca.etiqueta e               ON e.id_etiqueta = cd.id_etiqueta "+
			" INNER JOIN biblioteca.sub_campo sub            ON sub.id_campo_dados = cd.id_campo_dados "+
			" WHERE titulo.id_titulo_catalografico = :idTitulo "+
			" AND ( " +
			"        ( e.tag in ('"+Etiqueta.AUTOR.getTag()+"', '"+Etiqueta.AUTOR_COOPORATIVO.getTag()+"', '"+Etiqueta.AUTOR_EVENTO.getTag()+"', '"+Etiqueta.AUTOR_SECUNDARIO.getTag()+"', '"+Etiqueta.AUTOR_COOPORATIVO_SECUNDARIO.getTag()+"', '"+Etiqueta.AUTOR_EVENTO_SECUNDARIO.getTag()+"') AND sub.codigo = '"+SubCampo.SUB_CAMPO_A+"') "+
			" ) ";
			
			
		Query qBuscaAutores = getSession().createSQLQuery(sqlBuscaAutoresTitulo);
		qBuscaAutores.setInteger("idTitulo", idTitulo);
		
		@SuppressWarnings("unchecked")
		List<String>  lista = qBuscaAutores.list();
		List<String> autoresTitulo =  lista;
		
		
		// Encontra as autoridades cuja entrada autorizada for igual ao assunto contido no Título */
		if(autoresTitulo.size() > 0 ){
			
			//  A partir dos assuntos acha as autoridades  //
			StringBuilder sqlBuscaAutoridadesAutores = new  StringBuilder(
			" SELECT id_autoridade " +
			" FROM biblioteca.cache_entidades_marc c " +
			" WHERE id_autoridade IS NOT NULL  " + 
			" AND ( c.campo_entrada_autorizada  = '"+Etiqueta.NOME_PESSOAL.getTag()+"'  " +
			"       OR c.campo_entrada_autorizada = '"+Etiqueta.NOME_CORPORATIVO.getTag()+"'  " +
			"       OR c.campo_entrada_autorizada = '"+Etiqueta.NOME_EVENTO.getTag()+"'  " +
			"       )  " +
			" AND ( 1 = 0 ");
			
			for (int posicaoParamentro = 0; posicaoParamentro < autoresTitulo.size(); posicaoParamentro++) {
				sqlBuscaAutoridadesAutores.append(" OR entrada_autorizada_autor like :autor"+posicaoParamentro+" ");
			}
			
			sqlBuscaAutoridadesAutores.append(" ) ");
			
			
			Query qBuscaAutoridadesAutores = getSession().createSQLQuery(sqlBuscaAutoridadesAutores.toString());
			for (int posicaoParamentro = 0; posicaoParamentro < autoresTitulo.size(); posicaoParamentro++) {
				qBuscaAutoridadesAutores.setString("autor"+posicaoParamentro, "%"+autoresTitulo.get(posicaoParamentro)+"%");
			}
			
			@SuppressWarnings("unchecked")
			List<Integer> list = qBuscaAutoridadesAutores.list();
			idsAutoridadesAutores =  list;
		}
		
		
		
		
		/* ******************************************************************************************* */
		
		
		retorno.addAll(retornaInfoUsuarioRegistrouInteresseAssunto(idsAutoridadesAssuntos, idBiblioteca));
		
		retorno.addAll(retornaInfoUsuarioRegistrouInteresseAutor(idsAutoridadesAutores, idBiblioteca));
		
		
		/* ******************************************************************************************* */
			
		return retorno;
	}
	
	
	
	
	/** Retorna informações dos usuáriso que cadatraram interesse da autoridades de assuntos retornadas */
	private List<Object[]> retornaInfoUsuarioRegistrouInteresseAssunto(List<Integer> idsAutoridadesAssuntos, int idBiblioteca) throws HibernateException, DAOException{
		
		/* *******  Encontra os usuários que cadastraram interesse nas autoridades obtidas a partir da busca textual das informações do Títulos  *********/
		
		// Obs.:  Utiliza sql porque não tem como fazer "inner joins" em HQL entre UsuariosBiblioteca -> Pessoa -> Usuario
		StringBuilder sqlAssuntos = new StringBuilder(" SELECT DISTINCT usuarioBiblioteca.id_usuario_biblioteca, pessoa.nome, usuario.email AS emailUsuario, pessoa.email AS emailPessoa, true, cacheAssunto.entrada_autorizada_assunto as assuntosInteresse,  COALESCE(biblioteca.descricao, 'Todas')  as bibliotecaInteresse " +  
					" FROM biblioteca.perfil_interesse_usuario_biblioteca perfil "+
					" INNER JOIN biblioteca.usuario_biblioteca usuarioBiblioteca ON perfil.id_usuario_biblioteca = usuarioBiblioteca.id_usuario_biblioteca "+
					" INNER JOIN comum.pessoa pessoa                             ON (usuarioBiblioteca.id_pessoa = pessoa.id_pessoa)  " +   // Apenas pessoas tem perfil de interesse por enquanto, bibliotecas não
					" INNER JOIN comum.usuario usuario                           ON (pessoa.id_pessoa = usuario.id_pessoa) "+
					" LEFT JOIN biblioteca.perfil_interesse_x_autoridade_assunto assuntosInteresse ON perfil.id_perfil_interesse_usuario_biblioteca = assuntosInteresse.id_perfil_interesse_usuario_biblioteca "+
					" LEFT JOIN biblioteca.perfil_interesse_x_biblioteca bibliotecaInteresse ON perfil.id_perfil_interesse_usuario_biblioteca = bibliotecaInteresse.id_perfil_interesse_usuario_biblioteca"+
					
					" LEFT JOIN biblioteca.cache_entidades_marc cacheAssunto ON cacheAssunto.id_autoridade = assuntosInteresse.id_autoridade"+
					" LEFT JOIN biblioteca.biblioteca ON bibliotecaInteresse.id_biblioteca = biblioteca.id_biblioteca "+
					" WHERE usuarioBiblioteca.ativo = :true AND usuarioBiblioteca.quitado = :false "+                                       // Quando o usuário tem seu vínculo quitado deixa de receber os informativos da biblioteca
					" AND ( bibliotecaInteresse.id_biblioteca = :idBiblioteca OR bibliotecaInteresse IS NULL ) "+                                   // Os que cadastraram interesse em uma biblioteca específica ou não escolheram nenhuma biblioteca
					" AND ( 1 = 0 ");
		if(idsAutoridadesAssuntos.size() > 0)
			sqlAssuntos.append("	OR assuntosInteresse.id_autoridade in  "+UFRNUtils.gerarStringIn(idsAutoridadesAssuntos)+" ");
		
		sqlAssuntos.append(" ) ");			
		
		Query q = getSession().createSQLQuery(sqlAssuntos.toString());
		q.setInteger("idBiblioteca", idBiblioteca);
		q.setBoolean("true", true);
		q.setBoolean("false", false);
		
		@SuppressWarnings("unchecked")
		List<Object[]> listaTemp = q.list();
		return listaTemp;
	}
	
	
	
	
	/** Retorna informações dos usuáriso que cadatraram interesse da autoridades de autores retornadas */
	private List<Object[]> retornaInfoUsuarioRegistrouInteresseAutor(List<Integer> idsAutoridadesAutores, int idBiblioteca) throws HibernateException, DAOException{
		
		// Obs.:  Utiliza sql porque não tem como fazer "inner joins" em HQL entre UsuariosBiblioteca -> Pessoa -> Usuario
		StringBuilder sqlAutores = new StringBuilder(" SELECT DISTINCT usuarioBiblioteca.id_usuario_biblioteca, pessoa.nome, usuario.email AS emailUsuario, pessoa.email AS emailPessoa, false, cacheAutor.entrada_autorizada_autor as autorInteresse, COALESCE(biblioteca.descricao, 'Todas')  as bibliotecaInteresse " +  
					" FROM biblioteca.perfil_interesse_usuario_biblioteca perfil "+
					" INNER JOIN biblioteca.usuario_biblioteca usuarioBiblioteca ON perfil.id_usuario_biblioteca = usuarioBiblioteca.id_usuario_biblioteca "+
					" INNER JOIN comum.pessoa pessoa                             ON (usuarioBiblioteca.id_pessoa = pessoa.id_pessoa)  " +   // Apenas pessoas tem perfil de interesse por enquanto, bibliotecas não
					" INNER JOIN comum.usuario usuario                           ON (pessoa.id_pessoa = usuario.id_pessoa) "+
					" LEFT JOIN biblioteca.perfil_interesse_x_autoridade_autor autorInteresse ON perfil.id_perfil_interesse_usuario_biblioteca = autorInteresse.id_perfil_interesse_usuario_biblioteca "+
					" LEFT JOIN biblioteca.perfil_interesse_x_biblioteca bibliotecaInteresse ON perfil.id_perfil_interesse_usuario_biblioteca = bibliotecaInteresse.id_perfil_interesse_usuario_biblioteca"+
					
					" LEFT JOIN biblioteca.cache_entidades_marc cacheAutor ON cacheAutor.id_autoridade = autorInteresse.id_autoridade"+
					" LEFT JOIN biblioteca.biblioteca ON bibliotecaInteresse.id_biblioteca = biblioteca.id_biblioteca "+
					" WHERE usuarioBiblioteca.ativo = :true AND usuarioBiblioteca.quitado = :false "+                                       // Quando o usuário tem seu vínculo quitado deixa de receber os informativos da biblioteca
					" AND ( bibliotecaInteresse.id_biblioteca = :idBiblioteca OR bibliotecaInteresse IS NULL ) "+                                   // Os que cadastraram interesse em uma biblioteca específica ou não escolheram nenhuma biblioteca
					" AND ( 1 = 0 ");
		
		if(idsAutoridadesAutores.size() > 0)
			sqlAutores.append(" 	OR autorInteresse.id_autoridade in  "+UFRNUtils.gerarStringIn(idsAutoridadesAutores)+" ");
		
		sqlAutores.append(" ) ");			
		
		Query qAutores = getSession().createSQLQuery(sqlAutores.toString());
		qAutores.setInteger("idBiblioteca", idBiblioteca);
		qAutores.setBoolean("true", true);
		qAutores.setBoolean("false", false);
		
		@SuppressWarnings("unchecked")
		List<Object[]> listaTempAutores = qAutores.list();
		return listaTempAutores;
	}
	
	
	
	
}
