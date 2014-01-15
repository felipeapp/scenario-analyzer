/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 09/11/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.ensino.stricto.dominio.MembroBancaPos;
import br.ufrn.sigaa.ensino.stricto.jsf.ConsultarDefesaMBean;

/** DAO responsável por consultas específicas à BancaPos
 * @author Édipo Elder F. Melo
 *
 */
public class BancaPosDao extends GenericSigaaDAO {
	

	/**
	 * Consulta todas as defesas pertencentes a um tipo de Banca de Pós-Graduação e Programa de Pós
	 * 
	 * @param tipoBanca
	 * @param programa
	 * @param dataInicio
	 * @param dataFim
	 * @param tituloTrabalho
	 * @param tipoOrdenacao caso 1 - ordena por discente, caso 2 - ordena por data da banca
	 * @return
	 * @throws DAOException
	 */	
	public List<BancaPos> consultarDefesas(Integer tipoBanca, Integer programa, Date dataInicio, Date dataFim, String tituloTrabalho, String docente, String discente, int tipoOrdenacao, Character nivelEnsino) throws DAOException {
		return consultarDefesas(tipoBanca, programa, dataInicio, dataFim, tituloTrabalho, docente, discente, tipoOrdenacao, nivelEnsino, null);		
	}
	
	/**
	 * Consulta todas as defesas de um programa e status informados
	 * @param programa
	 * @param status
	 * @throws DAOException
	 */	
	public List<BancaPos> findDefesasByProgramaAndStatus(Integer programa, Integer status) throws DAOException {
		return consultarDefesas(null, programa, null, null, null, null, null, 0, null, status);		
	}	

	/**
	 * Consulta todas as defesas conforme os parâmetros informados
	 * 
	 * @param tipoBanca
	 * @param programa
	 * @param dataInicio
	 * @param dataFim
	 * @param tituloTrabalho
	 * @param tipoOrdenacao caso 1 - ordena por discente, caso 2 - ordena por data da banca
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public List<BancaPos> consultarDefesas(Integer tipoBanca, Integer programa, Date dataInicio, Date dataFim, String tituloTrabalho, String docente, String discente, int tipoOrdenacao, Character nivelEnsino, Integer status) throws DAOException {
		
		String projecao = " banca.id, banca.dadosDefesa.discente.discente.gestoraAcademica.id, banca.dadosDefesa.discente.id, " +
				"banca.dadosDefesa.discente.discente.gestoraAcademica.nome, banca.dadosDefesa.discente.discente.matricula," +
				"banca.dadosDefesa.discente.discente.pessoa.nome, banca.dadosDefesa.discente.discente.nivel, banca.dadosDefesa.titulo, " +
				"banca.data, banca.tipoBanca, banca.status,banca.dadosDefesa.discente.discente.anoIngresso, banca.dadosDefesa.discente.discente.periodoIngresso ";
		
		StringBuffer sb = new StringBuffer();
		
		String sqlMembro = "";
		if (!StringUtils.isEmpty(docente)){
			sqlMembro = " left join banca.membrosBanca membro " +
				        " left join membro.pessoaMembroExterno membroExterno "+
			            " left join membro.docentePrograma.pessoa docentePrograma "+
			            " left join membro.docenteExternoPrograma.pessoa docenteExternoPrograma "+
			            " left join membro.docenteExternoInstituicao.pessoa externoInstituicao ";
		}

		sb.append(" select "+projecao+" from BancaPos banca " +
				  " inner join banca.dadosDefesa " +
				  " inner join banca.dadosDefesa.discente.discente discente " +
				  " inner join banca.dadosDefesa.discente.discente.pessoa pessoa " +
				  " inner join banca.dadosDefesa.discente.discente.gestoraAcademica unidade " +
				  sqlMembro+				  
				  " where 1 = 1");
		
		if (tipoBanca != null && tipoBanca != 0) {
			sb.append(" and banca.tipoBanca = :tipoBanca ");
		}
			
		if (programa != null && programa != 0) {
			sb.append(" and unidade.id = :idUnidade ");
		}
					
		if (dataInicio != null) {
			sb.append(" and banca.data >= :dataInicio ");
		}
		
		if (dataFim != null) {
			sb.append(" and banca.data <= :dataFim ");
		}
		
		if (!StringUtils.isEmpty(tituloTrabalho)) {
			sb.append(" and ");
			sb.append(UFRNUtils.convertUtf8UpperLatin9("banca.dadosDefesa.titulo") 
			+ " like " + UFRNUtils.toAsciiUpperUTF8(" '%' || :titulo || '%' "));
		}
		
		if (!StringUtils.isEmpty(docente)){
			sb.append(" and (");
			sb.append(" membroExterno.nomeAscii like :docente");
			sb.append(" or docentePrograma.nomeAscii like "+UFRNUtils.toAsciiUpperUTF8(":docente||'%'"));
			sb.append(" or docenteExternoPrograma.nomeAscii like "+UFRNUtils.toAsciiUpperUTF8(":docente||'%'"));
			sb.append(" or externoInstituicao.nomeAscii like "+UFRNUtils.toAsciiUpperUTF8(":docente||'%'"));
			sb.append(" )");			
		}		
		
		if (!StringUtils.isEmpty(discente)){
			sb.append(" and pessoa.nomeAscii like "+UFRNUtils.toAsciiUpperUTF8(":discente||'%' "));
		}
		
		if (nivelEnsino != null) {
			sb.append(" and discente.nivel = :nivelEnsino ");
		}
		
		if (status != null)
			sb.append(" and banca.status = "+status);			
		
		switch (tipoOrdenacao) {
			case ConsultarDefesaMBean.ORDENAR_BANCAS_POR_DISCENTE:
				sb.append(" order by unidade.nome, discente.pessoa.nome, banca.data desc, banca.dadosDefesa.titulo");
				break;
			case ConsultarDefesaMBean.ORDENAR_BANCAS_POR_DATA:
				sb.append(" order by unidade.nome, banca.data desc, discente.pessoa.nome, banca.dadosDefesa.titulo"); 
				break;
			case ConsultarDefesaMBean.ORDENAR_BANCAS_ANO_PERIODO_INGRESSO:
				sb.append(" order by unidade.nome, discente.anoIngresso, discente.periodoIngresso, discente.pessoa.nome, banca.dadosDefesa.titulo"); 
				break;
			default:
				sb.append(" order by unidade.nome, discente.pessoa.nome, banca.data desc, banca.dadosDefesa.titulo");
				break;
		}
		
		Query q = getSession().createQuery(sb.toString());
		
		if (tipoBanca != null && tipoBanca != 0) {
			q.setInteger("tipoBanca", tipoBanca);
		}
		
		if (programa != null && programa != 0) {
			q.setInteger("idUnidade", programa);

		}
		
		// pesquisa apenas dataInicio
		if (dataInicio != null && !dataInicio.equals("") ) {
			q.setTimestamp("dataInicio", dataInicio); 
		}
		
		// pesquisa apenas dataFinal
		if (dataFim != null&& !dataFim.equals("")) {
			q.setTimestamp("dataFim", dataFim); 
		}

		if (tituloTrabalho != null && !tituloTrabalho.trim().isEmpty()) {
			q.setParameter("titulo",  tituloTrabalho.trim());
		}
		
		if (!StringUtils.isEmpty(docente)){
			q.setParameter("docente", docente.trim());
			q.setParameter("docente", StringUtils.toAscii(docente.toUpperCase()));
			
		}		
		
		if (!StringUtils.isEmpty(discente)){
			q.setParameter("discente", discente.trim());
			q.setParameter("discente", StringUtils.toAscii(discente.toUpperCase()));
		}
		
		if (nivelEnsino != null)
			q.setCharacter("nivelEnsino", nivelEnsino);
		
		@SuppressWarnings("unchecked")
		List<BancaPos> lista = (List<BancaPos>) HibernateUtils.parseTo(q.list(), projecao, BancaPos.class, "banca");
		return lista;

	}
	
	/**
	 * Consulta todas as defesas pendentes de aprovação do orientador informado
	 * 
	 * @param docente
	 * @return
	 * @throws DAOException
	 */
	public List<BancaPos> findBancasPendentesByOrientador(int idOrientador, boolean docenteExterno) throws DAOException {
		
		String projecao = " banca.id, banca.dadosDefesa.discente.discente.gestoraAcademica.id, banca.dadosDefesa.discente.id, " +
				"banca.dadosDefesa.discente.discente.gestoraAcademica.nome, banca.dadosDefesa.discente.discente.matricula," +
				"banca.dadosDefesa.discente.discente.pessoa.nome, banca.dadosDefesa.discente.discente.nivel, banca.dadosDefesa.titulo, " +
				"banca.data, banca.tipoBanca, banca.status ";
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(" select "+projecao+" from BancaPos banca " +
				  " inner join banca.dadosDefesa " +
				  " inner join banca.dadosDefesa.discente.discente discente " +
				  " inner join banca.dadosDefesa.discente.discente.pessoa pessoa " +
				  " inner join banca.dadosDefesa.discente.discente.gestoraAcademica unidade ");
		
		sb.append(" where banca.status = "+BancaPos.PENDENTE_APROVACAO);
		
		sb.append(" and discente.id in ( ");
		
			sb.append(" select discente.id from OrientacaoAcademica orientacao ");
			sb.append(" where orientacao.fim is null ");
			sb.append(" and orientacao.cancelado = falseValue() ");					
			sb.append(" and discente.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto()) );
		
			if (docenteExterno)
				sb.append(" and orientacao.docenteExterno.id = :idOrientador ");
			else 
				sb.append(" and orientacao.servidor.id = :idOrientador ");
		
		sb.append(" ) ");
			
		Query q = getSession().createQuery(sb.toString());
		
		q.setInteger("idOrientador", idOrientador);
		
		@SuppressWarnings("unchecked")
		List<BancaPos> lista = (List<BancaPos>) HibernateUtils.parseTo(q.list(), projecao, BancaPos.class, "banca");
		return lista;

	}	
	
	/**
	 * Consulta todas as Bancas de Pós-Graduação de acordo com o discente
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<BancaPos> findByDiscente(DiscenteStricto discente)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(BancaPos.class);
			c.createCriteria("dadosDefesa").add(
					Restrictions.eq("discente", discente)).addOrder(
					Order.desc("titulo"));
			
			@SuppressWarnings("unchecked")
			Collection<BancaPos> lista = c.list();			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Consulta todas as Bancas de Pós-Graduação de acordo com o discente e tipo
	 * da banca (qualificação, defesa)
	 * 
	 * @param discente
	 * @param tipoBanca
	 * @return
	 * @throws DAOException
	 */
	public Collection<BancaPos> findByDiscente(DiscenteStricto discente, int tipoBanca)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(BancaPos.class);
			c.add(Restrictions.eq("tipoBanca", tipoBanca))
				.createCriteria("dadosDefesa").add(
					Restrictions.eq("discente", discente)).addOrder(
					Order.desc("titulo"));
			@SuppressWarnings("unchecked")
			Collection<BancaPos> lista = c.list();		
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Consulta todas as Bancas de Pós-Graduação de acordo com o Período informado
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public Collection<BancaPos> findByPeriodo(Date dataInicio, Date dataFim) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(BancaPos.class);
			c.add(Restrictions.between("data", CalendarUtils.descartarHoras(dataInicio), CalendarUtils.descartarHoras(dataFim)));
			@SuppressWarnings("unchecked")
			Collection<BancaPos> lista = c.list();		
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	
	/** Retorna a banca de pós referente à matrícula componente.
	 * @param matriculaComponente
	 * @return
	 * @throws DAOException
	 */
	public BancaPos findByMatriculaComponente(MatriculaComponente matriculaComponente) throws DAOException {
			Criteria c = getSession().createCriteria(BancaPos.class);
			c.add(Restrictions.ne("status", BancaPos.CANCELADA))	
				.createCriteria("matriculaComponente").add(
					Restrictions.eq("id", matriculaComponente.getId()));
				
			return (BancaPos) c.setMaxResults(1).uniqueResult();
		}

	/**
	 * Consulta todas as Bancas de Pós-Graduação de acordo com o discente e 
	 *  o tipo de banca examinadora 
	 * 
	 * @param discente
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public BancaPos findMaisRecenteByTipo(DiscenteStricto discente, int tipo)
			throws DAOException {
		try {
			Query q = getSession()
					.createQuery(
					"FROM BancaPos"
							+ " WHERE dadosDefesa.discente.id=:discente"
							+ " and tipoBanca=:tipo"
							+ " and data is not null"
							+ " and (status not in ("+BancaPos.CANCELADA+") or status is null)"
							+ " ORDER BY data desc");
			q.setInteger("discente", discente.getId());
			q.setInteger("tipo", tipo);
			q.setMaxResults(1);
			return (BancaPos) q.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Consulta todos os membros de uma Banca de Pós-Graduação
	 * 
	 * @param banca
	 * @return
	 * @throws DAOException
	 */
	public List<MembroBancaPos> findMembrosByBanca(BancaPos banca)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MembroBancaPos.class);
			c.add(Restrictions.eq("banca", banca));
			@SuppressWarnings("unchecked")
			List<MembroBancaPos> lista = c.list();			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Consulta os trabalhos homologados de acordo com o Discente 
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public HomologacaoTrabalhoFinal findHomologacaoByDiscente(DiscenteAdapter discente) throws DAOException {
		return (HomologacaoTrabalhoFinal) getSession().createQuery("from HomologacaoTrabalhoFinal h where h.banca.dadosDefesa.discente.id = ?").setInteger(0, discente.getId()).setMaxResults(1).uniqueResult();
	}

	/**
	 * Remove todos os Membros pertencentes a uma Banca de Pós-Graduação
	 * 
	 * @param b
	 */
	public void removerMembrosBanca(BancaPos b) {
		update("delete from stricto_sensu.membro_banca_pos where id_banca = ?", new Object[] { b.getId() });
	}
	
	/** Retorna a quantidade de dissertações e teses defendidas por centro. 
	 * @param idCentro Limita a consulta ao ID do centro
	 * @param idDepartamento Limita a consulta ao ID do departamento
	 * @param ano Ano de referência
	 * @param periodo Período de referência
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQtdDefesasTeses(Integer idCentro, Integer idDepartamento, Integer ano, Integer periodo) throws HibernateException, DAOException {
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		StringBuilder sql = new StringBuilder("select"
				+ " centro.id_unidade,"
				+ " centro.codigo_unidade as codigo,"
				+ " centro.nome,"
				+ " centro.sigla,"
				+ " sum (case when discente.nivel = :mestrado then 1 else 0 end) as dissertacoes,"
				+ " sum (case when discente.nivel = :doutorado then 1 else 0 end) as teses"
				+ " from stricto_sensu.homologacao_trabalho_final"
				+ " inner join stricto_sensu.banca_pos on (id_banca_pos = id_banca)"
				+ " inner join stricto_sensu.dados_defesa using (id_dados_defesa)"
				+ " inner join discente using (id_discente)"
				+ " inner join curso using (id_curso)"
				+ " inner join comum.unidade programa on (curso.id_unidade = programa.id_unidade)"
				+ " inner join comum.unidade centro on (programa.unidade_responsavel = centro.id_unidade)"
				+ " where banca_pos.tipobanca = :tipoBanca");
		if (ano != null && periodo != null) {
			sql.append(" and extract(year from banca_pos.data) = :ano");
			sql.append(" and ceil(extract(month from banca_pos.data) / 6) = :periodo");
		}
		if (idCentro != null) 
			sql.append(" and centro.id_unidade = :idCentro");
		if (idDepartamento != null)
			sql.append(" and departamento.id_unidade = :idDepartamento");
		sql.append(" group by centro.id_unidade, centro.codigo_unidade, centro.nome, centro.sigla");
		sql.append(" order by nome");
		
		Query q = getSession().createSQLQuery(sql.toString());
		if (idCentro != null) 
			q.setInteger("idCentro", idCentro);
		if (idDepartamento != null)
			q.setInteger("idDepartamento", idDepartamento);
		if (ano != null && periodo != null) {
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}
		q.setCharacter("mestrado", NivelEnsino.MESTRADO);
		q.setCharacter("doutorado", NivelEnsino.DOUTORADO);
		q.setInteger("tipoBanca", BancaPos.BANCA_DEFESA);

		@SuppressWarnings("unchecked")		
		List<Object[]> lista = q.list();
		
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("id_unidade", obj[0]);
			linha.put("codigo", obj[1]);
			linha.put("nome", obj[2]);
			linha.put("sigla", obj[3]);
			linha.put("dissertacoes", obj[4]);
			linha.put("teses", obj[5]);
			resultado.add(linha);
		}
		return resultado;
	}
	
	/**
	 * Retorna a quantidade de defesas no ano inicial e final informados, agrupados por ano.
	 * @param anoInicio
	 * @param anoFim
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoDefesasAno(int anoInicio, int anoFim) throws HibernateException, DAOException{
		String sql = 
		" 	select cast( extract(year from b.data) as int) as ano, "+
		"       count(*) as total "+ 
		" from stricto_sensu.banca_pos b "+
		" where b.tipobanca = "+BancaPos.BANCA_DEFESA+
		" and (b.status not in ("+BancaPos.CANCELADA+") or b.status is null)" +
		" and extract(year from b.data) between "+anoInicio+" and "+ anoFim +
		" group by 1 "+
		" order by 1 ";
			
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		try {
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.list();
			resultado = lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return resultado;
	}
	
	/**
	 * Retorna a quantidade de defesas no ano inicial e final informados, agrupados por curso e nível de ensino.
	 * @param anoInicio
	 * @param anoFim
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoDefesasAnoDetalhado(int ano) throws HibernateException, DAOException{
		String sql = "select curso.nome as curso, discente.nivel as nivel, count(*) as total " +
		" from stricto_sensu.banca_pos b " +
		" inner join stricto_sensu.dados_defesa using (id_dados_defesa)" +
		" inner join discente using (id_discente)" +
		" inner join curso using (id_curso)" +
		" where b.tipobanca = "+BancaPos.BANCA_DEFESA+
		" and (b.status not in ("+BancaPos.CANCELADA+") or b.status is null)" +
		" and extract(year from b.data) = " + ano +
		" group by 1, 2" +
		" order by 1, 2";
			
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		try {
			SQLQuery q = getSession().createSQLQuery(sql);
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.list();
			resultado = lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return resultado;
	}
	
	
	/**
	 *     Retorna uma lista de bancas de um certo programa de pós-graduação,
	 * de um certo ano e período.
	 * @param programa
	 * @param ano
	 * @param periodo
	 * @return Um mapeamento entre o nome de cada programa e as bancas desse programa
	 * no período passado como parâmetro.
	 */
	public Map<String, List<BancaPos> > findByPrograma( Unidade programa, int ano,
			int periodo )
			throws DAOException {
		
		Map<String, List<BancaPos> > r = new HashMap<String, List<BancaPos> >();
		
		CalendarioAcademico ca =  CalendarioAcademicoHelper.getCalendario( ano, periodo, programa, NivelEnsino.STRICTO, null, null, null);
		
		// retorna vazio para um período inválido
		if (ca == null)
			return r;
		
		String hql =
				"FROM BancaPos AS bp " +
				"    JOIN bp.dadosDefesa AS dd " +
				"    JOIN dd.discente AS stricto " +
				"    JOIN stricto.discente AS d " +
				"    JOIN d.curso AS c " +
				"    JOIN c.unidade AS u " +
				"WHERE " +
				"    ( bp.data BETWEEN :inicioPeriodo AND :fimPeriodo ) AND " +
				"    u.id = :id_programa " +
				"ORDER BY bp.data ASC ";
		
		Query q = getSession().createQuery( hql );
		
		q.setInteger( "id_programa", programa.getId() );
		q.setDate( "inicioPeriodo", ca.getInicioPeriodoLetivo() );
		q.setDate( "fimPeriodo",    ca.getFimPeriodoLetivo() );
		
		@SuppressWarnings("unchecked")
		List<Object[]> l = q.list();
		
		for ( Object[] obj : l ) {
			BancaPos banca = (BancaPos) obj[0];
			// descartar as bancas sem membros
			if ( banca.getMembrosBanca().isEmpty() )
				continue;
			String nomePrograma = banca.getDadosDefesa().getDiscente().getCurso().getUnidade().getNome();
			if ( ! r.containsKey( nomePrograma ) ) {
				r.put( nomePrograma, new ArrayList<BancaPos>() );
			}
			r.get( nomePrograma ).add( banca );
		}
		
		return r;
	}

	/***
	 * Serve para recuperar endereço lattes dos discentes das bancas;
	 * @param servidor
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<PerfilPessoa> getPerfisPessoas(List<Integer> idsDiscente) throws HibernateException, DAOException {
		String sql = "select p.id_discente , p.endereco_lattes from comum.perfil_pessoa p " +
			" where p.id_discente in "+UFRNUtils.gerarStringIn(idsDiscente);
				
		DataSource ds = Database.getInstance().getComumDs();

		return (List<PerfilPessoa>) getJdbcTemplate( ds ).query(sql, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

				List<PerfilPessoa> resultado = new ArrayList<PerfilPessoa>();

				while (rs.next()) {
					PerfilPessoa perfil = new PerfilPessoa();
					perfil.setIdDiscente(rs.getInt("id_discente"));
					perfil.setEnderecoLattes(rs.getString("endereco_lattes"));
					resultado.add(perfil);
				}
				
				return resultado;
			}
		});
		
	}
	
	
}
