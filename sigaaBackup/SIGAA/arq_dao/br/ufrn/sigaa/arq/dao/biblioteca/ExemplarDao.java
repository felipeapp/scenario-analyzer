/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/08/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;

/**
 *
 * DAO para as pesquisas de exemplares.
 *
 * @author Jadson
 * @since 26/08/2008
 * @version 1.0 criação da classe
 *
 */
public class ExemplarDao extends GenericSigaaDAO{

	/** Limite máximo de resultados de uma busca. */
	public static final int LIMITE_RESULTADOS = 300;
	
	/**
	 *   Encontra todos os anexos ativos do exemplar passado.
	 */
	public Long countAnexosAtivosDoExemplar(int idExemplar) throws DAOException{
		
		String hql = new String( " SELECT count(e) FROM Exemplar e "
				+" WHERE e.exemplarDeQuemSouAnexo.id = :idExemplar AND e.situacao.situacaoDeBaixa = falseValue() AND e.ativo = trueValue() ");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idExemplar", idExemplar);
		
		return (Long) q.uniqueResult();
	}
	
	
	
	/**
	 *   Encontra todos os anexos ativos do exemplar passado.
	 */
	public List<Exemplar> findAnexosAtivosDoExemplar(int idExemplar) throws DAOException{
		
		String hql = new String( " SELECT e FROM Exemplar e "
				+" WHERE e.exemplarDeQuemSouAnexo.id  = :idExemplar AND e.situacao.situacaoDeBaixa = falseValue() AND e.ativo = trueValue() ");
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idExemplar", idExemplar);
		
		@SuppressWarnings("unchecked")
		List<Exemplar> lista = q.list();
		return lista;
	}
	
	
	
	
	/**
	 *      Método que retorna a descrição da biblioteca do Exemplar passado.
	 */
	public String findDescricaoBibliotecaDoExemplar(int idExemplar) throws DAOException {
		
		String hql = " SELECT e.biblioteca.descricao FROM Exemplar e WHERE e.id = :idExemplar ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idExemplar", idExemplar);
		return (String) q.uniqueResult();
	}


	/**
	 *    Pega o id do Título do Exemplar passado.
	 *
	 * @param id o id do fascículo
	 */
	public Integer obtemIdDoTituloDoExemplar(int idExemplar) throws DAOException{
	
		String hql =" SELECT e.tituloCatalografico.id FROM Exemplar e "
			+" WHERE e.situacao.situacaoDeBaixa = falseValue() AND e.ativo = trueValue() AND e.id = :idExemplar";
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idExemplar", idExemplar);
		
		return (Integer) q.uniqueResult();
	}

	
	/**
	 * 
	 *    Método que DEVE SER SEMPRE CHAMADO se desejar atualizar as informações do exemplar. 
	 *    Tem que trazer os relacionamentos inicializados (biblioteca, coleção, etc...) senão o 
	 *    hibernate não vai atualizar.
	 *
	 * @param id o id do fascículo
	 */
	public Exemplar findExemplarAtivosByIDInicializandoRelacionamentos(int id) throws DAOException{
	
		StringBuilder hql = new StringBuilder(" FROM Exemplar e ");
		hql.append(" INNER JOIN FETCH e.colecao ");
		hql.append(" INNER JOIN FETCH e.biblioteca ");
		hql.append(" INNER JOIN FETCH e.situacao ");
		hql.append(" INNER JOIN FETCH e.status ");
		hql.append(" INNER JOIN FETCH e.tipoMaterial ");
		
		hql.append(" WHERE e.id = :idExemplar ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idExemplar", id);
		
		return (Exemplar) q.uniqueResult();
	}
	
	/**
	 * Busca todos os exemplares ativos do título passado
	 */
	public List<Exemplar> findExemplaresAtivosDeUmTitulo(int idTitulo)throws DAOException{

		String hql =" FROM Exemplar exemplar "
			+" WHERE exemplar.situacao.situacaoDeBaixa = falseValue() AND exemplar.ativo = trueValue() AND exemplar.tituloCatalografico.id = :idTitulo";
		
		Query q = getSession().createQuery(hql);	
		q.setInteger("idTitulo", idTitulo);
		
		@SuppressWarnings("unchecked")
		List<Exemplar> lista = q.list();
		return lista;
	}
	
	
	/**
	 * Método que encontra os exemplares ativos cujos valores coincidam com os valores do Exemplar passado.
	 */
	public List<Exemplar> findAllExemplaresAtivosByExemplo(Exemplar exemplar, Date dataCriacaoInicio, Date dataCriacaoFinal) throws DAOException{
		
		boolean possuiCriterioBusca = false;
		
		String hql =" SELECT DISTINCT exemplar FROM Exemplar exemplar LEFT JOIN " 
			+" exemplar.formasDocumento formaDocumento "
			+" WHERE exemplar.ativo = trueValue() ";
		
		// TODOS os filtros de busca são opcionais //	
	
		if( StringUtils.notEmpty(exemplar.getCodigoBarras())){
			hql += " AND exemplar.codigoBarras like :codigoBarras ";
			possuiCriterioBusca = true;
		}
		
		if(exemplar.getStatus().getId() > 0){
			hql += " AND exemplar.status.id = :idStatus";
			possuiCriterioBusca = true;
		}
		
		if(exemplar.getSituacao().getId() > 0){
			hql += " AND exemplar.situacao.id = :idSituacao";
			possuiCriterioBusca = true;
		}
		
		if(exemplar.getBiblioteca().getId() > 0){
			hql += " AND  exemplar.biblioteca.id = :idBiblioteca";
			possuiCriterioBusca = true;
		}
		
		if(exemplar.getColecao().getId() > 0){
			hql += " AND  exemplar.colecao.id = :idColecao";
			possuiCriterioBusca = true;
		}
		
		if(isPeriodoBuscaValido(dataCriacaoInicio, dataCriacaoFinal) ){
			hql += " AND  exemplar.dataCriacao between :dataCriacaoInicio AND :dataCriacaoFinal ";
			possuiCriterioBusca = true;
		}
		
		if(exemplar.getTipoMaterial().getId() > 0){
			hql += " AND  exemplar.tipoMaterial.id = :idTipoMaterial";
			possuiCriterioBusca = true;
		}
		
		if( !isEmpty(exemplar.getFormasDocumento()) ){
			hql += " AND  formaDocumento.id IN " + UFRNUtils.gerarStringIn(exemplar.getFormasDocumento());
			possuiCriterioBusca = true;
		}
		
		hql += " ORDER BY exemplar.biblioteca.id, exemplar.codigoBarras  ";
		
		if(possuiCriterioBusca){
			
			Query q = getSession().createQuery(hql);
			
			if( StringUtils.notEmpty(exemplar.getCodigoBarras())){
				q.setString("codigoBarras", exemplar.getCodigoBarras()+"%");
			}
			
			if(exemplar.getStatus().getId() > 0){
				q.setInteger("idStatus", exemplar.getStatus().getId());
			}
			
			if(exemplar.getSituacao().getId() > 0){
				q.setInteger("idSituacao", exemplar.getSituacao().getId());
			}
			
			if(exemplar.getBiblioteca().getId() > 0){
				q.setInteger("idBiblioteca", exemplar.getBiblioteca().getId());
			}
			
			if(exemplar.getColecao().getId() > 0){
				q.setInteger("idColecao", exemplar.getColecao().getId());
			}
			
			if(exemplar.getTipoMaterial().getId() > 0){
				q.setInteger("idTipoMaterial", exemplar.getTipoMaterial().getId());
			}
			
			if(isPeriodoBuscaValido(dataCriacaoInicio, dataCriacaoFinal)){
				q.setDate("dataCriacaoInicio", dataCriacaoInicio);
				q.setDate("dataCriacaoFinal", dataCriacaoFinal);
			}
			
			q.setMaxResults(LIMITE_RESULTADOS);
			
			@SuppressWarnings("unchecked")
			List<Exemplar> lista = q.list();
			return lista;
			
		}else{
			return new ArrayList<Exemplar>();
		}
	}
	
	
	/**
	 * Método que compara se o intervalo de tempo da pesquisa é valido
	 */
	private boolean isPeriodoBuscaValido(Date dataInicio, Date dataFinal){
		
		if (dataInicio != null && dataFinal != null && dataInicio.getTime() < dataFinal.getTime()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Conta os exemplares ativos por faixa de código de barras.
	 */
	public Integer countExemplaresAtivosByCodigosBarras(String codigosBarrasInicial, String CodigoBarrasFinal) throws DAOException {
		
		Integer quantidade = 0;
		
		Criteria c = getSession().createCriteria(Exemplar.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.between("codigoBarras", codigosBarrasInicial, CodigoBarrasFinal));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", false));
		quantidade = (Integer) c.list().get(0);
		
		return quantidade;
	}
	
	/**
	 * Conta os exemplares ativos por faixa de código de barras. 
	 */
	public Integer countExemplaresAtivosNoAcervoByDoTitulo(int idTituloCatalografico) throws DAOException {
		
		Integer quantidade = 0;
		
		Criteria c = getSession().createCriteria(Exemplar.class);
		c.setProjection(Projections.distinct(Projections.countDistinct("id")));
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("tituloCatalografico.id", idTituloCatalografico));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", false));
		quantidade = (Integer) c.uniqueResult();
		
		return quantidade;
	}
	
	/**
	 *    Busca o exemplar ativo pelo seu código de barras
	 *
	 * @return o exemplar ativo que possui o código de barras
	 */
	public Exemplar findExemplarAtivoByCodigoBarras(String codigoBarras) throws DAOException{
		
		String hql = "SELECT i FROM Exemplar i where i.situacao.situacaoDeBaixa = falseValue() AND i.ativo = trueValue() AND i.codigoBarras = ?";
		
		Query q = getSession().createQuery(hql);
		q.setString(0, codigoBarras);
		q.setMaxResults(1);
		
		return (Exemplar) q.uniqueResult(); 
	}
	
	
	/**
	 *     Busca os exemplar ativos por faixa de código de barras. 
	 */
	public List<Exemplar> findExemplaresAtivosByCodigosBarras(String codigosBarrasInicial, String CodigoBarrasFinal) throws DAOException {
		
		List <Exemplar> ms = new ArrayList <Exemplar>();
		
		Criteria c = getSession().createCriteria (Exemplar.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.between("codigoBarras", codigosBarrasInicial, CodigoBarrasFinal));
		c.createCriteria("situacao").add(Restrictions.eq("situacaoDeBaixa", false));
		@SuppressWarnings("unchecked")
		List<Exemplar> lista = c.list();
		ms.addAll(lista);
		
		return ms;
	}
	
	
	/** 
	 * Encontra todos os exemplares ativo no acervo do título passado
	 */
	public List<Exemplar> findAllExemplarAtivosDoTitulo(int idTitulo) throws DAOException{
		
		String hql = "SELECT e FROM Exemplar e where e.situacao.situacaoDeBaixa = falseValue() AND e.ativo = trueValue() AND e.tituloCatalografico.id = :idTitulo";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idTitulo", idTitulo);
		
		@SuppressWarnings("unchecked")
		List<Exemplar> lista = q.list();
		return lista;
	}
	
	
	/**
	 * <p> Esse método é utilizado na visualização dos materiais da busca do acervo. </p>
	 * 
	 * <p> Conta a quantidade de exemplares para realizar a paginação </p>
	 *
	 * @see this{@link #findAllExemplarAtivosDoTituloComDadosByBiblioteca(int, List, boolean, int, int)}
	 *
	 * @param idAssinatura
	 * @param anoCronologico o fascículos de um determinado ano
	 * @return os fascículos ativos da assinatura com ou sem artigos.
	 */
	public Integer countTodosExemplaresAtivosDoTituloComDadosByBiblioteca(int idTitulo, List<Integer> idsBibliotecasAcervoPublico, boolean apenasSituacaoVisivelUsuarioFinal, Integer idBibliotecaEspecificaMateriais) throws DAOException{
		
		StringBuilder hql = new StringBuilder("SELECT ");
		
		hql.append( " count(DISTINCT e.id) " );
		
		hql.append(getHQLPadraoFindTodosExemplares());
		
		if(apenasSituacaoVisivelUsuarioFinal)
			hql.append(" AND e.situacao.visivelPeloUsuario = trueValue() ");
		
		if(idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0)
			hql.append(" AND e.biblioteca.id in ( :idBibliotecas )");
		
		if(idBibliotecaEspecificaMateriais != null && idBibliotecaEspecificaMateriais > 0 )
			hql.append(" AND e.biblioteca.id =  :idBibliotecaEspecificaMateriais ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idTitulo", idTitulo);
		
		if(idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0)
			q.setParameterList("idBibliotecas", idsBibliotecasAcervoPublico);
		
		// Se o usuário está buscando os exemplares de uma biblioteca específica 
		if(idBibliotecaEspecificaMateriais != null && idBibliotecaEspecificaMateriais > 0 )
			q.setInteger("idBibliotecaEspecificaMateriais", idBibliotecaEspecificaMateriais);
		
		return  ((Long) q.uniqueResult()).intValue();	
		
	}
	
	
	
	
	/** 
	 *   <p>Esse método é usado para recuperar os materiais de um Título nas buscas do Acervo</p>
	 * 
	 *   <p>Encontra todos os exemplares ativo no acervo do título passado já trazendo os dados dos relacionamentos. </p>
	 *   <p>Evitando que o hibernate tenha que abrir várias conexão para trazer essas informações.</p>
	 *   
	 *   @see this{@link #countTodosExemplaresAtivosDoTituloComDadosByBiblioteca(int, List, boolean)
	 */
	public List<Exemplar> findAllExemplarAtivosDoTituloComDadosByBiblioteca(int idTitulo, List<Integer> idsBibliotecasAcervoPublico, boolean apenasSituacaoVisivelUsuarioFinal, Integer idBibliotecaMateriais, int pagina, int limite) throws DAOException{

		long tempo = System.currentTimeMillis();

		String hqlProjecaoAdicional = "biblioteca.unidade.id";
		String hqlInnerJoinAdicional = "INNER JOIN biblioteca.unidade unidade";
		String hqlWhere = " e.situacao.situacaoDeBaixa = falseValue() "+
				"AND e.ativo = trueValue() " +
				"AND e.tituloCatalografico.id = :idTitulo ";

		
		if(apenasSituacaoVisivelUsuarioFinal)
			hqlWhere += " AND e.situacao.visivelPeloUsuario = trueValue() ";
		
		if(idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0)
			hqlWhere += " AND e.biblioteca.id in (:idBibliotecas)";
		
		if(idBibliotecaMateriais != null && idBibliotecaMateriais > 0)
			hqlWhere += " AND e.biblioteca.id = :idBibliotecaMateriais";
		
		StringBuilder hql = new StringBuilder(MaterialInformacionalDao.getHQLPadraoExemplar(hqlProjecaoAdicional, hqlInnerJoinAdicional, hqlWhere));
		
		hql.append(" ORDER BY biblioteca.id, e.codigoBarras ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idTitulo", idTitulo);
		
		if(idsBibliotecasAcervoPublico != null && idsBibliotecasAcervoPublico.size() > 0)
			q.setParameterList("idBibliotecas", idsBibliotecasAcervoPublico);
		// Se o usuário selecionou uma biblioteca específica //
		if(idBibliotecaMateriais != null && idBibliotecaMateriais > 0)
			q.setParameter("idBibliotecaMateriais", idBibliotecaMateriais);
		
		q.setFirstResult((pagina-1) * limite);
		q.setMaxResults(limite);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<Exemplar> exemplares = new ArrayList<Exemplar>();

		if (lista != null) {
			exemplares.addAll(HibernateUtils.parseTo(lista, MaterialInformacionalDao.getProjecaoHQLPadraoExemplar(hqlProjecaoAdicional), Exemplar.class, "e"));
		}
		
		
		System.out.println(">>>>>>>>>>> Consultar todas informações dos exemplares demorou: "+ (System.currentTimeMillis()-tempo)+" ms");
		
		return exemplares;
	}
	
	
	
	private static String getHQLPadraoFindTodosExemplares(){
		return " FROM Exemplar e "+
		" INNER JOIN e.colecao c "+
		" INNER JOIN e.biblioteca b "+
		" INNER JOIN e.situacao s "+
		" INNER JOIN e.status st "+
		" INNER JOIN e.tipoMaterial t"+
		" WHERE e.situacao.situacaoDeBaixa = falseValue() AND e.ativo = trueValue() AND e.tituloCatalografico.id = :idTitulo";
	}
	
	
	/** 
	 * 	<p>Método padrão para recuperar todas as informações de um determinado exemplar, pode retornar os exemplares baixados</p>
	 * 
	 *  <p>Utilizado quando o usuário deseja visualizar os detalhes do exemplar, utilizando tanto nas buscar internas quando na pública</p>
	 * 
	 *   Encontra todos os exemplares ativo no acervo do título passado já trazendo os dados dos relacionamentos. 
	 *   Evitando que o hibernate tenha que abrir várias conexão para trazer essas informações
	 */
	public Exemplar findTodosDadosExemplar(int idExemplar) throws DAOException{
		
		
		StringBuilder hql = new StringBuilder("SELECT e.id, e.codigoBarras, e.numeroChamada, e.segundaLocalizacao, e.notaGeral, e.notaUsuario, "
				+" e.numeroPatrimonio, e.numeroVolume, e.tomo, e.anexo, e.notaTeseDissertacao, e.notaConteudo, e.ativo, "
				+" c.id, c.descricao, b.id, b.descricao, s.id, s.descricao, s.situacaoDisponivel, s.situacaoEmprestado, s.situacaoDeBaixa, st.id, st.descricao, t.id, t.descricao, "
				+" f.denominacao ");
		hql.append(" FROM Exemplar e ");
		hql.append(" INNER JOIN e.colecao c ");
		hql.append(" INNER JOIN e.biblioteca b ");
		hql.append(" INNER JOIN e.situacao s ");
		hql.append(" INNER JOIN e.status st ");
		hql.append(" INNER JOIN e.tipoMaterial t");
		hql.append(" LEFT JOIN e.formasDocumento f");
		hql.append(" WHERE e.ativo = trueValue() AND e.id = :idExemplar");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idExemplar", idExemplar);
		
		/**
		 * Pode retorna mais de um resultado se o material tiver mais de formato do documento
		 */
		@SuppressWarnings("unchecked")
		List<Object[]> listaDadosExemplar = q.list();
	
		int contadorIndex = 0;

		if(listaDadosExemplar != null && listaDadosExemplar.size() > 0){
		
			Object[] dadosExemplar	= listaDadosExemplar.get(0);
			
			Exemplar e = new Exemplar( (Integer) dadosExemplar[contadorIndex++]); 
			e.setCodigoBarras( (String) dadosExemplar[contadorIndex++]);
			e.setNumeroChamada( (String) dadosExemplar[contadorIndex++]);
			e.setSegundaLocalizacao( (String) dadosExemplar[contadorIndex++]);
			e.setNotaGeral( (String) dadosExemplar[contadorIndex++]);
			e.setNotaUsuario( (String) dadosExemplar[contadorIndex++]);
			e.setNumeroPatrimonio( (Long) dadosExemplar[contadorIndex++]);
			e.setNumeroVolume( (Integer) dadosExemplar[contadorIndex++]);
			e.setTomo( (String) dadosExemplar[contadorIndex++]);
			e.setAnexo( (Boolean) dadosExemplar[contadorIndex++]);
			e.setNotaTeseDissertacao( (String) dadosExemplar[contadorIndex++]);
			e.setNotaConteudo( (String) dadosExemplar[contadorIndex++]);
			e.setAtivo( (Boolean) dadosExemplar[contadorIndex++]);
			e.setColecao( new Colecao((Integer) dadosExemplar[contadorIndex++], (String) dadosExemplar[contadorIndex++]));
			e.setBiblioteca( new Biblioteca( (Integer) dadosExemplar[contadorIndex++], (String) dadosExemplar[contadorIndex++]));
			SituacaoMaterialInformacional s = new SituacaoMaterialInformacional((Integer) dadosExemplar[contadorIndex++], (String) dadosExemplar[contadorIndex++], (Boolean) dadosExemplar[contadorIndex++], (Boolean) dadosExemplar[contadorIndex++], (Boolean) dadosExemplar[contadorIndex++]);
			e.setSituacao( s );
			e.setStatus( new StatusMaterialInformacional((Integer) dadosExemplar[contadorIndex++], (String) dadosExemplar[contadorIndex++]) );
			e.setTipoMaterial( new TipoMaterial((Integer) dadosExemplar[contadorIndex++], (String) dadosExemplar[contadorIndex++]) );
			
			//e.setAtivo(true); // Só busca os ativos, então pode setar ativo no material 
		
			for (Object[] dadosExemplar2 : listaDadosExemplar) {
				if(StringUtils.notEmpty( (String) dadosExemplar2[26]) ){
					e.adicionaFormasDocumento( new FormaDocumento((String) dadosExemplar2[26]));
				}
			}
	
			return e;
		}
	
		return null;
		
	}
	
	
	/** 
	 * Encontra todos os exemplares e suas informações do título passado, usado na exibição de uma indicação de referência na turma virtual.
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public List<Exemplar> findAllExemplarAtivosByTitulo ( int idTitulo ) throws HibernateException, DAOException{
			
		StringBuilder hql = new StringBuilder("SELECT e.id, e.codigoBarras, "
				+" b.descricao, t.descricao, c.descricao, st.descricao, s.descricao, s.situacaoDisponivel, s.situacaoEmprestado, s.situacaoDeBaixa");
		hql.append(" FROM Exemplar e ");
		hql.append(" INNER JOIN e.colecao c ");
		hql.append(" INNER JOIN e.biblioteca b ");
		hql.append(" INNER JOIN e.situacao s ");
		hql.append(" INNER JOIN e.status st ");
		hql.append(" INNER JOIN e.tipoMaterial t");
		hql.append(" WHERE e.ativo = trueValue() AND e.tituloCatalografico.id = :idTitulo");
				
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idTitulo", idTitulo);
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		if ( result !=  null ) {
			
			List<Exemplar> exemplares = new ArrayList<Exemplar>();
			
			for (Object[] linha : result) {
				Integer i = 0;
			
				Exemplar e = new Exemplar();
				e.setId( (Integer)  linha[i++] );
				e.setCodigoBarras( (String) linha[i++] );
				
				Biblioteca b = new Biblioteca();
				b.setDescricao( (String) linha[i++] );
				e.setBiblioteca(b);
				
				TipoMaterial tm = new TipoMaterial();
				tm.setDescricao( (String) linha[i++] );
				e.setTipoMaterial(tm);
				
				Colecao c = new Colecao();
				c.setDescricao( (String) linha[i++] );
				e.setColecao(c);
				
				StatusMaterialInformacional s = new StatusMaterialInformacional();
				s.setDescricao( (String) linha[i++] );
				e.setStatus(s);
				
				SituacaoMaterialInformacional st = new SituacaoMaterialInformacional();
				st.setDescricao( (String) linha[i++] );
				st.setSituacaoDisponivel( (Boolean) linha[i++] );
				st.setSituacaoEmprestado( (Boolean) linha[i++] );
				st.setSituacaoDeBaixa( (Boolean) linha[i++] );
				e.setSituacao(st);
				
				exemplares.add(e);
			} 
			return exemplares;
		}
		
		return null;		
	}
	
	/** 
	 * Encontra todos os exemplares e suas informações do título passado, usado na exibição de uma indicação de referência na turma virtual.
	 * @param idTitulo
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public List<Exemplar> findAllExemplarAtivosComPrazoByTitulo ( int idTitulo ) throws HibernateException, DAOException{
			
		Query  q = getSession().createSQLQuery(
				"select ex.id_exemplar , mi.codigo_barras ,  b.descricao as descBibl, tm.descricao as descTipo, c.descricao as descCol, s.descricao as descSta, " +
				"sm.descricao as descSit, sm.situacao_disponivel, sm.situacao_emprestado, sm.situacao_de_baixa,  em.prazo "+
				"from biblioteca.titulo_catalografico as tc "+
				"inner join biblioteca.exemplar as ex on ex.id_titulo_catalografico = tc.id_titulo_catalografico "+
				"inner join biblioteca.material_informacional as mi on ex.id_exemplar = mi.id_material_informacional "+
				"inner join biblioteca.biblioteca as b on mi.id_biblioteca = b.id_biblioteca "+
				"inner join biblioteca.tipo_material as tm on tm.id_tipo_material = mi.id_tipo_material "+
				"inner join biblioteca.colecao as c on mi.id_colecao = c.id_colecao "+
				"inner join biblioteca.status_material_informacional as s on s.id_status_material_informacional = mi.id_status_material_informacional "+
				"inner join biblioteca.situacao_material_informacional as sm on sm.id_situacao_material_informacional = mi.id_situacao_material_informacional "+
				"left join biblioteca.emprestimo as em on em.id_material = mi.id_material_informacional and em.data_devolucao is null and em.data_estorno is null "+
				"where tc.id_titulo_catalografico = " +idTitulo+
				"order by sm.descricao , ex.id_exemplar");

		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		
		if ( result !=  null ) {
			
			List<Exemplar> exemplares = new ArrayList<Exemplar>();
			
			for (Object[] linha : result) {
				Integer i = 0;
			
				Exemplar e = new Exemplar();
				e.setId( (Integer)  linha[i++] );
				e.setCodigoBarras( (String) linha[i++] );
				
				Biblioteca b = new Biblioteca();
				b.setDescricao( (String) linha[i++] );
				e.setBiblioteca(b);
				
				TipoMaterial tm = new TipoMaterial();
				tm.setDescricao( (String) linha[i++] );
				e.setTipoMaterial(tm);
				
				Colecao c = new Colecao();
				c.setDescricao( (String) linha[i++] );
				e.setColecao(c);
				
				StatusMaterialInformacional s = new StatusMaterialInformacional();
				s.setDescricao( (String) linha[i++] );
				e.setStatus(s);
				
				SituacaoMaterialInformacional st = new SituacaoMaterialInformacional();
				st.setDescricao( (String) linha[i++] );
				st.setSituacaoDisponivel( (Boolean) linha[i++] );
				st.setSituacaoEmprestado( (Boolean) linha[i++] );
				st.setSituacaoDeBaixa( (Boolean) linha[i++] );
				e.setSituacao(st);
				if ( linha[i] != null )
					e.setPrazoEmprestimo( (Date) linha[i++] );
				exemplares.add(e);
			} 
			return exemplares;
		}
		
		return null;		
	}
	
}
