package br.ufrn.sigaa.ensino.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.NotificacaoAcademica;
import br.ufrn.sigaa.ensino.dominio.NotificacaoAcademicaDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para notificações de discentes.
 * 
 * @author Diego Jácome
 *
 */
public class NotificacaoAcademicaDao extends GenericSigaaDAO {

	/**
	 * Retorna se o sql é válido
	 * @param sql
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 */
	public String isConsultaSQLValido(String sql, boolean anoPeriodoReferencia, Integer anoReferencia, Integer periodoReferencia) throws  DAOException  {   
  
		  String erro = null;  
		  try{   
			  			  
			  Query  q = getSession().createSQLQuery(sql);
			  setAnoPeriodoReferenciaInQuery(anoPeriodoReferencia, anoReferencia,periodoReferencia, q);
			  
			  Integer totalDiscentes = q.list().size();
			  if ( !anoPeriodoReferencia && totalDiscentes == 0 )
				  return "Nenhum resultado encontrado";
			  findDiscentesByFiltroNotificacao(sql,anoPeriodoReferencia,anoReferencia,periodoReferencia);

		  } catch (Exception e) {
			  return MensagensArquitetura.CONTEUDO_INVALIDO;
		  } 
		  return erro;
	}
	
	/**
	 * Retorna a lista de discentes da notificacao
	 * @param sql
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Discente> findDiscentesByFiltroNotificacao(String filtro, boolean anoPeriodoReferencia, Integer anoReferencia, Integer periodoReferencia) throws  DAOException  {   
  
		String sql = " select d.id_discente , p.nome , p.email " +
					 " from discente d " +
					 " join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
					 " where d.id_discente in ( " +filtro+ " ) " +
					 " order by p.nome ";	
		
		Query  q = getSession().createSQLQuery(sql);
		
		setAnoPeriodoReferenciaInQuery(anoPeriodoReferencia, anoReferencia,	periodoReferencia, q);
		  
		List<Object[]> result = q.list();
		ArrayList<Discente> discentes = new ArrayList<Discente>();
		if ( result != null ) {
			for ( Object[] linha : result ){
				Integer i = 0;
				Discente d = new Discente();
				d.setId((Integer)linha[i++]);
				Pessoa p = new Pessoa();
				p.setNome((String)linha[i++]);
				p.setEmail((String)linha[i++]);
				d.setPessoa(p);
				discentes.add(d);
			}
		}
				
		return discentes;
	}
	
	/**
	 * Retorna a lista de discentes notificados pela notificação
	 * @param notificacao
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public ArrayList<NotificacaoAcademicaDiscente> findNotificacoesDiscentes (Integer idNotificacaoAcademica, Integer idRegistro , PagingInformation paging) throws  DAOException  {   
  
		String sqlCount = "select nd.id_notificacao_academica_discente , nd.pendente , nd.data_confirmacao , n.id_notificacao_academica , " +
							" n.descricao , n.exige_confirmacao , d.id_discente , d.matricula , d.status , p.nome , " +
							" c.id_curso , c.nome as cnome , m.id_municipio , m.nome as mnome " +
							" from ensino.notificacao_academica_discente nd " +
							" inner join discente d on d.id_discente = nd.id_discente" +
							" inner join curso c on c.id_curso = d.id_curso " +
							" inner join comum.municipio m on m.id_municipio = c.id_municipio " +
							" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa" +
							" inner join ensino.notificacao_academica n on n.id_notificacao_academica = nd.id_notificacao_academica" +
							" where n.id_notificacao_academica = " +idNotificacaoAcademica+ " and nd.id_registro_entrada_notificacao = " +idRegistro+
							" order by cnome , mnome , p.nome asc";
		
		String sql = "select nd.id_notificacao_academica_discente , nd.pendente , nd.data_confirmacao , max(r.data_visualizacao) , n.id_notificacao_academica , " +
						" n.descricao , n.exige_confirmacao , d.id_discente , d.matricula , d.status , p.nome , " +
						" c.id_curso , c.nome as cnome , m.id_municipio , m.nome as mnome " +
						" from ensino.notificacao_academica_discente nd " +
						" inner join discente d on d.id_discente = nd.id_discente" +
						" inner join curso c on c.id_curso = d.id_curso " + 
						" inner join comum.municipio m on m.id_municipio = c.id_municipio " +
						" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa" +
						" inner join ensino.notificacao_academica n on n.id_notificacao_academica = nd.id_notificacao_academica" +
						" left join ensino.registro_notificacao_academica r on r.id_notificacao_academica_discente = nd.id_notificacao_academica_discente" +
						" where n.id_notificacao_academica = " +idNotificacaoAcademica+ " and nd.id_registro_entrada_notificacao = " +idRegistro+
						" group by nd.id_notificacao_academica_discente , nd.pendente , nd.data_confirmacao , n.id_notificacao_academica ,  " +
						" n.descricao , n.exige_confirmacao , d.id_discente , d.matricula , d.status , p.nome ,  c.id_curso , c.nome , m.id_municipio , m.nome " +
						" order by cnome , mnome , p.nome asc";
		
		Query q = getSession().createSQLQuery(sql);
		
		if(paging != null && q != null){
			paging.setTotalRegistros(count(sqlCount));
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
		
		List<Object[]> result = q.list();
		
		ArrayList<NotificacaoAcademicaDiscente> notificacoesDiscentes = new ArrayList<NotificacaoAcademicaDiscente>();
		if ( result != null ) {
			for ( Object[] linha : result ){
				Integer i = 0;
				
				NotificacaoAcademicaDiscente nd = new NotificacaoAcademicaDiscente();
				nd.setId( (Integer) linha[i++] );
				nd.setPendente( (Boolean) linha[i++] );
				nd.setDataConfirmacao( (Date) linha[i++] );
				
				Date ultimaVisualizacao = (Date) linha[i++];
				if (ultimaVisualizacao!=null){
					nd.setVisualizada(true);
					nd.setUltimaVisualizacao(ultimaVisualizacao);
				}else{
					nd.setVisualizada(false);
				}
				
				NotificacaoAcademica n = new NotificacaoAcademica();
				n.setId( (Integer) linha[i++] );
				n.setDescricao( (String) linha[i++]);
				n.setExigeConfirmacao( (Boolean) linha[i++]);
				
				Discente d = new Discente();
				d.setId( (Integer) linha[i++] );
				Number matricula = (Number)linha[i++];
				d.setMatricula(matricula != null ? matricula.longValue() : null);
				d.setStatus( (Short) linha[i++] );
				
				Pessoa p = new Pessoa();
				p.setNome( (String) linha[i++]);

				Curso c = new Curso();
				c.setId((Integer) linha[i++]);
				c.setNome((String) linha[i++]);
				c.setMunicipio(new Municipio());
				c.getMunicipio().setId((Integer) linha[i++]);
				c.getMunicipio().setNome((String) linha[i++]);

				d.setPessoa(p);
				d.setCurso(c);
				nd.setDiscente(d);
				nd.setNotificacaoAcademica(n);
				
				notificacoesDiscentes.add(nd);
			}
		}
		return notificacoesDiscentes;
		
	}
	
	/**
	 * Retorna a lista de discentes notificados pela notificação
	 * @param notificacao
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 94575802
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<NotificacaoAcademicaDiscente> findNotificacoesByDiscente (Integer idNotificacaoAcademica, Integer idRegistro, Long matricula, String nome, String nomeCurso ) throws  DAOException  {   
  
		String sql = "select nd.id_notificacao_academica_discente , nd.pendente , nd.data_confirmacao , n.id_notificacao_academica , " +
						" n.descricao , n.exige_confirmacao , d.id_discente , d.matricula , d.status , p.nome , c.id_curso , c.nome as cnome , m.id_municipio , m.nome as mnome " +
						" from ensino.notificacao_academica_discente nd " +
						" inner join discente d on d.id_discente = nd.id_discente" +
						" inner join curso c on c.id_curso = d.id_curso " +
						" inner join comum.municipio m on m.id_municipio = c.id_municipio " +
						" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa" +
						" inner join ensino.notificacao_academica n on n.id_notificacao_academica = nd.id_notificacao_academica" +
						" where n.id_notificacao_academica = " + idNotificacaoAcademica + " and nd.id_registro_entrada_notificacao = " +idRegistro;
		
		if (matricula != null) 
			sql += " and d.matricula = " + matricula;
		if (nome != null && !nome.isEmpty())
			sql += " and " + UFRNUtils.convertUtf8UpperLatin9("p.nome_ascii")
						+ " like '" + UFRNUtils.trataAspasSimples(StringUtils.toAscii(nome.toUpperCase()))
						+ "%'";
		if (nomeCurso != null && !nomeCurso.isEmpty())
			sql += " and " + UFRNUtils.convertUtf8UpperLatin9("c.nome_ascii")
						+ " like '" + UFRNUtils.trataAspasSimples(StringUtils.toAscii(nomeCurso.toUpperCase()))
						+ "%'";	
		
		sql +=	" order by c.nome , m.nome , p.nome asc";
		
		Query q = getSession().createSQLQuery(sql);
				
		List<Object[]> result = q.list();
		
		ArrayList<NotificacaoAcademicaDiscente> notificacoesDiscentes = new ArrayList<NotificacaoAcademicaDiscente>();
		if ( result != null ) {
			for ( Object[] linha : result ){
				Integer i = 0;
				
				NotificacaoAcademicaDiscente nd = new NotificacaoAcademicaDiscente();
				nd.setId( (Integer) linha[i++] );
				nd.setPendente( (Boolean) linha[i++] );
				nd.setDataConfirmacao( (Date) linha[i++] );
				
				NotificacaoAcademica n = new NotificacaoAcademica();
				n.setId( (Integer) linha[i++] );
				n.setDescricao( (String) linha[i++]);
				n.setExigeConfirmacao( (Boolean) linha[i++]);
				
				Discente d = new Discente();
				d.setId( (Integer) linha[i++] );
				Number matriculaLinha = (Number)linha[i++];
				d.setMatricula(matriculaLinha != null ? matriculaLinha.longValue() : null);
				d.setStatus( (Short) linha[i++]);

				Pessoa p = new Pessoa();
				p.setNome( (String) linha[i++]);

				Curso c = new Curso();
				c.setId((Integer) linha[i++]);
				c.setNome((String) linha[i++]);
				c.setMunicipio(new Municipio());
				c.getMunicipio().setId((Integer) linha[i++]);
				c.getMunicipio().setNome((String) linha[i++]);

				d.setPessoa(p);
				d.setCurso(c);
				nd.setDiscente(d);
				nd.setNotificacaoAcademica(n);
				
				notificacoesDiscentes.add(nd);
			}
		}
		return notificacoesDiscentes;
		
	}
	
	/**
	 * Retorna o todos de discentes de várias notificacoes
	 * @param sql
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<Discente> findDiscentesByNotificacao(NotificacaoAcademica notificacao) throws  DAOException  {   
  
		String sql = " select d.id_discente , d.matricula , p.nome , p.email , c.nome as cnome , m.nome as mnome " +
					 " from discente d " +
					 " join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
					 " join curso c on c.id_curso = d.id_curso " +
					 "join comum.municipio m on c.id_municipio = m.id_municipio " +
					 " where d.id_discente in ( " +notificacao.getSqlFiltrosDiscentes()+ " ) " +
					 " order by p.nome ";
		
		Query  q = getSession().createSQLQuery(sql);
		
		setAnoPeriodoReferenciaInQuery(notificacao, q);
		
		List<Object[]> result = q.list();

		if ( result != null ) {

			ArrayList<Discente> discentes = new ArrayList<Discente>();
						
			for ( Object[] linha : result ){
				Integer i = 0;
								
				Discente d = new Discente();
				d.setId((Integer)linha[i++]);
				Number matricula = (Number)linha[i++];
				d.setMatricula(matricula != null ? matricula.longValue() : null);
				
				Pessoa p = new Pessoa();
				p.setNome((String)linha[i++]);
				p.setEmail((String)linha[i++]);
				
				Curso c = new Curso();
				c.setMunicipio(new Municipio());
				c.setNome((String)linha[i++]);
				c.getMunicipio().setNome((String)linha[i++]);
				
				d.setPessoa(p);	
				d.setCurso(c);	
				
				discentes.add(d);

			}

			return discentes;
		}
		
		return null;
	}
	
	/**
	 * Retorna o todos de discentes de várias notificacoes
	 * @param sql
	 * @throws DAOException 
	 * @throws HibernateException 
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Integer,List<Discente>> findDiscentesByNotificacoes(List<NotificacaoAcademica> notificacoes) throws  DAOException  {   
  
		String sql = "";
		
		for ( NotificacaoAcademica n : notificacoes ){
			
			String filtro = n.getSqlFiltrosDiscentes();
			filtro = setAnoPeriodoReferenciaInSql(n, filtro);
			
			sql += sql == "" ? "" : " union ";
			sql += "(" +
					" select "+ n.getId() + " as id , d.id_discente , d.matricula , p.nome , p.email , c.nome as cnome , m.nome as mnome " +
					 " from discente d " +
					 " join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
					 " join curso c on c.id_curso = d.id_curso " +
					 "join comum.municipio m on c.id_municipio = m.id_municipio " +
					 " where d.id_discente in ( " +filtro+ " ) " +
					 " order by p.nome "+
					") ";
		}
		
		if (notificacoes.size() > 1 )
			sql += " order by id , nome";
		
		Query  q = getSession().createSQLQuery(sql);
		List<Object[]> result = q.list();

		if ( result != null ) {

			HashMap<Integer,List<Discente>> map = new HashMap<Integer,List<Discente>>();
			ArrayList<Discente> discentes = new ArrayList<Discente>();
			
			Integer oldId = null;
			Integer newId = null;
			
			for ( Object[] linha : result ){
				Integer i = 0;
				
				newId = (Integer)linha[i++];
				
				if ( oldId != null && newId.intValue() != oldId.intValue() ){
					ArrayList<Discente> discentesNotificacao = new ArrayList<Discente>();
					discentesNotificacao.addAll(discentes);
					map.put(oldId,discentesNotificacao);
					discentes = new ArrayList<Discente>();
				}
				
				Discente d = new Discente();
				d.setId((Integer)linha[i++]);
				Number matricula = (Number)linha[i++];
				d.setMatricula(matricula != null ? matricula.longValue() : null);
				
				Pessoa p = new Pessoa();
				p.setNome((String)linha[i++]);
				p.setEmail((String)linha[i++]);
				
				Curso c = new Curso();
				c.setMunicipio(new Municipio());
				c.setNome((String)linha[i++]);
				c.getMunicipio().setNome((String)linha[i++]);
				
				d.setPessoa(p);	
				d.setCurso(c);	
				
				discentes.add(d);
				
				oldId = newId;
			}
			map.put(oldId,discentes);
			return map;
		}
		
		return null;
	}
	
	/**
	 * Verifica se um discente possui notificação acadêmica pendente
	 * @param id
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public boolean isPendenteNotificacao(int id , Boolean exigeConfirmacao ) throws HibernateException, DAOException {
		
		String confirmacao = "";
		if ( exigeConfirmacao == null )
			confirmacao = "";
		else if ( exigeConfirmacao == true )
			confirmacao = " and na.exige_confirmacao = trueValue() ";
		else 
			confirmacao = " and na.exige_confirmacao = falseValue() ";
		
		String sql = " select count(*) " +
		 " from ensino.notificacao_academica_discente n " +
		 " inner join ensino.notificacao_academica na using (id_notificacao_academica) " +
		 " where n.pendente = trueValue() " +
		 confirmacao + "and n.id_discente = "+id;
		
		Query  q = getSession().createSQLQuery(sql);
		Integer count = ((Number) q.uniqueResult()).intValue();
		
		if ( count > 0 )
			return true;
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Busca os discentes pendentes de notificação acadêmica
	 * @param id
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public NotificacaoAcademicaDiscente findNotificacaoDiscentePendenteByDiscente ( Discente d , Boolean exigeConfirmacao ) throws HibernateException, DAOException {
		
		String confirmacao = "";
		if ( exigeConfirmacao == null )
			confirmacao = "";
		else if ( exigeConfirmacao == true )
			confirmacao = " AND nd.notificacaoAcademica.exigeConfirmacao = trueValue() ";
		else 
			confirmacao = " AND nd.notificacaoAcademica.exigeConfirmacao = falseValue() ";
		
		Query q = getSession().createQuery(
				"SELECT nd FROM NotificacaoAcademicaDiscente nd " +
						" WHERE nd.pendente = trueValue() " +
						confirmacao +
						" AND nd.discente.id = :idDiscente " +
						" ORDER BY nd.dataCadastro ASC");
		
		q.setInteger("idDiscente", d.getId());
		q.setMaxResults(1);
		return (NotificacaoAcademicaDiscente) q.uniqueResult();
	}
	
	/**
	 * Busca todas notificações enviadas para os discentes.
	 * @param id
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<NotificacaoAcademica> findNotificacaoEnviadas (Integer ano, Integer periodo) throws HibernateException, DAOException {
		
		int mesInicio = periodo == 1 ? 0 : 5;
		int mesFim = periodo == 1 ? 6 : 0;
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.MONTH, mesInicio);
		cal.set(Calendar.YEAR, ano);

		Date inicio = cal.getTime();	
		
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.MONTH, mesFim);
		cal.add(Calendar.DAY_OF_MONTH,-1);
		cal.set(Calendar.YEAR, ano);
		
		Date fim = cal.getTime();
		
		Query q = getSession().createSQLQuery(
				"select n.id_notificacao_academica, n.descricao, n.exige_confirmacao, nd.data_cadastro, nd.id_registro_entrada_notificacao, " +
				"count(nd.id_notificacao_academica_discente) as total_enviados, " +
				"(" +
					" select count(ndd.id_notificacao_academica_discente) from ensino.notificacao_academica_discente ndd " +
					" where ndd.id_notificacao_academica = n.id_notificacao_academica " +
					" and ndd.data_cadastro = nd.data_cadastro" +
					" and ndd.pendente = falseValue()" + 
					") as total_confirmados " +
				"from ensino.notificacao_academica n " +
				"inner join ensino.notificacao_academica_discente nd using (id_notificacao_academica) " +
				"where nd.data_cadastro >= '"+inicio+"' and nd.data_cadastro <= '"+fim+"' "+
				"group by n.id_notificacao_academica, n.descricao, n.exige_confirmacao, nd.data_cadastro , nd.id_registro_entrada_notificacao " +
				"order by nd.data_cadastro desc , n.exige_confirmacao desc , n.descricao asc");
		
		List<Object[]> result = q.list();
		ArrayList<NotificacaoAcademica> notificacoes = new ArrayList<NotificacaoAcademica>();
		if ( result != null ) {
			for ( Object[] linha : result ){
				Integer i = 0;
				NotificacaoAcademica n = new NotificacaoAcademica();
				n.setId( (Integer) linha[i++] );
				n.setDescricao( (String) linha[i++]);
				n.setExigeConfirmacao( (Boolean) linha[i++]);
				n.setDataEnvio((Date) linha[i++]);
				n.setIdRegistroNotificacao((Integer) linha[i++]);
				n.setQtdEnviadas(((Number) linha [i++]).intValue());
				n.setQtdConfirmadas(((Number) linha [i++]).intValue());
				notificacoes.add(n);
			}
		}
				
		return notificacoes;
	}
	
	
	/**
	 * Inseri as notificações de discentes em lote.
	 * @param id
	 * @throws ArqException 
	 */
	public void inserirNotificacoes (List<NotificacaoAcademicaDiscente> notificacoes) throws ArqException {
		
		Connection con = null;
		try {
			if (notificacoes != null && !notificacoes.isEmpty()){
				con = Database.getInstance().getSigaaConnection();
				Statement st = con.createStatement();
				
				for ( NotificacaoAcademicaDiscente n :notificacoes ){
					
					String aux = n.getMensagemNotificacao().replaceAll("'","''");
					n.setMensagemNotificacao(aux);
					
					aux = n.getMensagemEmail().replaceAll("'", "''");
					n.setMensagemEmail(aux);
					
					st.addBatch( 
						"insert into ensino.notificacao_academica_discente (" +
						"   id_notificacao_academica_discente," +
						"	id_notificacao_academica," +
						"   id_registro_entrada_notificacao," +
						"   id_registro_entrada_confirmacao," +
						"	data_cadastro," +
						"	data_confirmacao," +
						"	pendente," +
						"	id_discente," +
						"	mensagem_notificacao," +
						"	mensagem_email," +
						"	exige_confirmacao" +
						")" +
						"values (" +
						" (select nextVal('public.hibernate_sequence')), " +
						n.getNotificacaoAcademica().getId() + ", " +
						n.getRegistroNotificacao().getId() + ", " +
						"null, " +
						"'" + n.getDataCadastro() + "', " +
						"null, " +
						n.isPendente() + ", " +
						n.getDiscente().getId() + ", " +
						"'" + n.getMensagemNotificacao() + "', " +
						"'" + n.getMensagemEmail() + "', " +
						n.getExigeConfirmacao() + 
						")"
					);
				}
				st.executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ArqException("Erro na notificação acadêmica!");

		} finally {
			try {
				if(con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * Seta o ano e o período de referência em uma notificação
	 * @param notificacao
	 * @param q
	 * @throws ArqException 
	 */
	private void setAnoPeriodoReferenciaInQuery(
			NotificacaoAcademica notificacao, Query q) {
		if (notificacao.isAnoPeriodoReferencia())
			setAnoPeriodoReferenciaInQuery(notificacao.isAnoPeriodoReferencia(),notificacao.getAnoReferencia(), notificacao.getPeriodoReferencia(), q);
	}
	
	/**
	 * Seta o ano e o período de referência em uma notificação
	 * @param anoPeriodoReferencia
	 * @param anoReferencia
	 * @param periodoReferencia
	 * @param q
	 * @throws ArqException 
	 */
	private void setAnoPeriodoReferenciaInQuery(boolean anoPeriodoReferencia,
			Integer anoReferencia, Integer periodoReferencia, Query q) {
		if (anoPeriodoReferencia){
			if ( anoReferencia != null )
				q.setInteger("anoReferencia", anoReferencia);
			else {
				int anoAtual = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
				q.setInteger("anoReferencia", anoAtual);
			}  
			if ( periodoReferencia != null )
				q.setInteger("periodoReferencia", periodoReferencia);
			else {
				int mes = br.ufrn.arq.util.CalendarUtils.getMesAtual();
				int periodoAtual = (( mes < 6 ) ? 1 :  2);
				q.setInteger("periodoReferencia", periodoAtual);
			}	
		}
	}
	
	/**
	 * Seta o ano e o período de referência em uma notificação
	 * @param notificacao
	 * @param sql
	 * @throws ArqException 
	 */
	private String setAnoPeriodoReferenciaInSql(
			NotificacaoAcademica notificacao, String sql) {
		if (notificacao.isAnoPeriodoReferencia())
			sql = setAnoPeriodoReferenciaInSql(notificacao.isAnoPeriodoReferencia(),notificacao.getAnoReferencia(), notificacao.getPeriodoReferencia(), sql);
		return sql;
	}
	
	/**
	 * Seta o ano e o período de referência em uma notificação
	 * @param anoPeriodoReferencia
	 * @param anoReferencia
	 * @param periodoReferencia
	 * @param sql
	 * @throws ArqException 
	 */
	private String setAnoPeriodoReferenciaInSql(boolean anoPeriodoReferencia,
			Integer anoReferencia, Integer periodoReferencia, String sql) {
		if (anoPeriodoReferencia){
			if ( anoReferencia != null )
				sql = sql.replaceAll(":anoReferencia", anoReferencia.toString());
			else {
				Integer anoAtual = br.ufrn.arq.util.CalendarUtils.getAnoAtual();
				sql = sql.replaceAll(":anoReferencia", anoAtual.toString());
			}  
			if ( periodoReferencia != null )
				sql = sql.replaceAll(":periodoReferencia", periodoReferencia.toString());
			else {
				Integer mes = br.ufrn.arq.util.CalendarUtils.getMesAtual();
				Integer periodoAtual = (( mes < 6 ) ? 1 :  2);
				sql = sql.replaceAll(":periodoReferencia", periodoAtual.toString());
			}	
		}
		return sql;
	}
	
	/**
	 * Retorna o total de notificações confirmadas
	 * @param id
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Integer countTotalConfirmadas(int id ) throws HibernateException, DAOException {
			
		String sql = " select count(nd.id_notificacao_academica_discente) from ensino.notificacao_academica_discente nd "+
					 " where nd.id_notificacao_academica = "+id+" and nd.pendente = falseValue()";
		
		Query  q = getSession().createSQLQuery(sql);
		Integer count = ((Number) q.uniqueResult()).intValue();
		
		return count;
	}
	
	/**
	 * Retorna o total de notificações visualizadas
	 * @param id
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Integer countTotalVisualizadas(int id ) throws HibernateException, DAOException {
			
		String sql = " select count(distinct r.id_discente) from ensino.registro_notificacao_academica r "+
						" join ensino.notificacao_academica_discente nd on nd.id_notificacao_academica_discente = r.id_notificacao_academica_discente "+
						" where  nd.id_notificacao_academica = "+id;
		
		Query  q = getSession().createSQLQuery(sql);
		Integer count = ((Number) q.uniqueResult()).intValue();
		
		return count;
	}
}
