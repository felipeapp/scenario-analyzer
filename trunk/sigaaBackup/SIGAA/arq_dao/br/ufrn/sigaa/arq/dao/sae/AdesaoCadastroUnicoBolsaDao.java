/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/03/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.sae;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.cadunico.dominio.FormularioCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável pelo acesso a adesão dos alunos ao cadastro único
 * 
 * @author Henrique André
 * 
 */
public class AdesaoCadastroUnicoBolsaDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as adesões feita ao cadastro único em um determinado ano.período
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<AdesaoCadastroUnicoBolsa> findByAnoPeriodo(int ano, int periodo) throws DAOException {
		
		String projecao =  "adesao.discente.matricula, adesao.discente.pessoa.nome, adesao.pontuacao";
		
		Query q = getSession().createQuery("select " + projecao + " from AdesaoCadastroUnicoBolsa adesao where adesao.ano = :ano and adesao.periodo = :periodo order by adesao.pontuacao");
		
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		
		return (List<AdesaoCadastroUnicoBolsa>) HibernateUtils.parseTo(q.list(), projecao, AdesaoCadastroUnicoBolsa.class, "adesao");
	}
	
	/**
	 * Retorna a adesão ao cadastro único do ano e período desejado
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public AdesaoCadastroUnicoBolsa findByDiscente(int idDiscente, int ano, int periodo) throws DAOException {
		Criteria c = getSession().createCriteria(AdesaoCadastroUnicoBolsa.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		
		if (!isEmpty(ano))
			c.add(Restrictions.eq("ano", ano));
		if (!isEmpty(periodo))
			c.add(Restrictions.eq("periodo", periodo));
		
		c.addOrder(Order.desc("ano"));
		c.addOrder(Order.desc("periodo"));
		c.addOrder(Order.desc("pontuacao"));
		c.setMaxResults(1);
		return (AdesaoCadastroUnicoBolsa) c.uniqueResult();
	}

	/**
	 * Retorna todas as adesões do discente
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AdesaoCadastroUnicoBolsa> findAllByDiscente(int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(AdesaoCadastroUnicoBolsa.class);
		c.add(Restrictions.eq("discente.id", idDiscente)).addOrder(Order.desc("ano"));

		c.addOrder(Order.desc("periodo"));
		return c.list();
	}	
	
	/**
	 * Verifica se o discente possui adesão ao cadastro único mais recente
	 * 
	 * @param idDiscente
	 * @param ano
	 * @param periodo 
	 * @return
	 * @throws DAOException
	 */
	public boolean isAdesaoCadastroUnico(int idDiscente, int ano, int periodo) throws DAOException {

		String sql = 
			" SELECT count(id_adesao) FROM sae.adesao_cadastro_unico adesao " + 
			" WHERE adesao.id_discente = ? and " +
				" adesao.ano = ? and " +
				" adesao.periodo = ?";

		int count = getSimpleJdbcTemplate().queryForInt(sql,
				new Object[] { idDiscente, ano, periodo });

		if (count > 0)
			return true;

		return false;
	}

	/**
	 * Busca todas as adesões de discentes Regulares que solicitaram cadastro únic.
	 * PROJEÇÃO: matrícula, nome, pontuação).
	 * 
	 * @param idCadastro
	 * @return
	 * @throws DAOException
	 */
	public List<AdesaoCadastroUnicoBolsa> findByCadastroUnico(int idCadastro)
			throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append("select adesao.discente.matricula, adesao.discente.pessoa.nome, adesao.pontuacao from AdesaoCadastroUnicoBolsa adesao");
		hql.append(" where adesao.cadastroUnico.id = :idCadastro");
		hql.append(" and adesao.discente.tipo = "+Discente.REGULAR);
		hql.append(" and adesao.discente.status in("+StatusDiscente.ATIVO+","+StatusDiscente.FORMANDO+")");
		hql.append(" order by adesao.pontuacao asc");

		Query q = getSession().createQuery(hql.toString());

		q.setInteger("idCadastro", idCadastro);

		List<?> list = q.list();

		List<AdesaoCadastroUnicoBolsa> resultado = null;
		for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {

			if (resultado == null)
				resultado = new ArrayList<AdesaoCadastroUnicoBolsa>();

			int col = 0;
			Object[] object = (Object[]) iterator.next();

			Long matricula = (Long) object[col++];
			String nome = (String) object[col++];
			Integer pontuacao = (Integer) object[col++];

			AdesaoCadastroUnicoBolsa adesao = new AdesaoCadastroUnicoBolsa();
			adesao.setDiscente(new Discente());
			adesao.getDiscente().setPessoa(new Pessoa());

			adesao.getDiscente().getPessoa().setNome(nome);
			adesao.getDiscente().setMatricula(matricula);
			adesao.setPontuacao(pontuacao);

			resultado.add(adesao);
		}

		return resultado;

	}

	/**
	 * Lista os discentes que solicitaram bolsa e que estão em situação de carência.  
	 * @param i 
	 * 
	 * @throws DAOException 
	 * @throws HibernateException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Discente> listarDiscentesSolicitaramBolsaAnoPeriodo(int ano, int periodo, TipoBolsaAuxilio tipoBolsaAuxilio, Integer idCursoDiscente) throws HibernateException, DAOException {
	
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(d.matricula), d.id_discente, p.nome, d.id_curso as id_curso, c.nome as nomeCurso" + 
						" from sae.bolsa_auxilio_periodo bap " +
						" inner join sae.bolsa_auxilio b on b.id_bolsa_auxilio = bap.id_bolsa_auxilio " +
						" inner join discente d on d.id_discente = b.id_discente " +
						" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
						" inner join sae.adesao_cadastro_unico adesao on adesao.id_discente = d.id_discente " +
						" inner join curso c on c.id_curso = d.id_curso " +
						" where adesao.pontuacao in (select adesao.pontuacao from sae.adesao_cadastro_unico adesao where adesao.pontuacao < ?) " +
						" AND bap.ano = ? AND bap.periodo = ? " +
						" AND b.id_tipo_bolsa_auxilio = ? ");
		
		ArrayList<Object> listaParamsSetados = new ArrayList<Object>();
			listaParamsSetados.add(AdesaoCadastroUnicoBolsa.ALUNO_PRIORITARIO);
			listaParamsSetados.add(ano);
			listaParamsSetados.add(periodo);
			listaParamsSetados.add(tipoBolsaAuxilio.getId());
		
		if (idCursoDiscente != 0) {
			sb.append(" AND d.id_curso = ? ");
			listaParamsSetados.add(idCursoDiscente);
		}
		
		Object[] params = listaParamsSetados.toArray();
		sb.append(" order by nome, d.matricula ");
		
		return getJdbcTemplate().query(sb.toString(), params, new RowMapper() {

			public Object mapRow(ResultSet rs, int row) throws SQLException {
				
				Discente discente = new Discente();
				discente.setId(rs.getInt("id_discente"));
				discente.setMatricula(rs.getLong("matricula"));
				discente.setCarente(true);
				
				Pessoa p = new Pessoa();
				p.setNome(rs.getString("nome"));
				
				discente.setPessoa(p);
				
				Curso c = new Curso();
				c.setId(rs.getInt("id_curso"));
				c.setNome(rs.getString("nomeCurso"));
				
				discente.setCurso(c);
				
				return discente;
			}
		});
	}
	
	/**
	 * Verifica se o aluno é prioritário no cadastro único de bolsas
	 * @param d
	 * @param pontos
	 * @return
	 * @throws DAOException
	 */
	public Boolean isDiscentePrioritario(DiscenteAdapter d, int ano, int periodo) throws DAOException {

		StringBuilder hql = new StringBuilder();

		hql.append("select adesao.pontuacao from AdesaoCadastroUnicoBolsa adesao");
		hql.append(" where adesao.discente.id = :idDiscente " +
				" and adesao.ano = :ano and adesao.periodo = :periodo");		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idDiscente", d.getId());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		
		return ((q.uniqueResult() != null) &&  ((Integer)q.uniqueResult() < AdesaoCadastroUnicoBolsa.ALUNO_PRIORITARIO));

	}

	/**
	 * Retorna os discentes com adesão no cadastro único de acordo com os critérios
	 * 
	 * @param unidade 
	 * @param curso 
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<AdesaoCadastroUnicoBolsa>> findAllDiscentesCadastroUnico(Curso curso, Unidade unidade, int ano, int periodo) throws DAOException {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("select adesao.discente.pessoa.nome, adesao.discente.matricula, adesao.pontuacao, adesao.discente.curso.nome " +
				" from AdesaoCadastroUnicoBolsa adesao " +
				" where adesao.cadastroUnico.status = " + FormularioCadastroUnicoBolsa.EM_VIGOR + 
					" and adesao.ano = " + ano + 
					" and adesao.periodo = " + periodo);
				
		 		if (!isEmpty(curso))
					sb.append(" and adesao.discente.curso.id = " + curso.getId());
		 		if (!isEmpty(unidade))
					sb.append(" and adesao.discente.curso.unidade.id = " + unidade.getId());
				
				sb.append(" order by adesao.discente.pessoa.nome");
		
		
				
		
				
		Query query = getSession().createQuery(sb.toString());
		List list = query.list();
		
		Map<String, List<AdesaoCadastroUnicoBolsa>> resultado = null;
		
		for (Iterator it = list.iterator(); it.hasNext();) {
			if (resultado == null) 
				resultado = new HashMap<String, List<AdesaoCadastroUnicoBolsa>>();
			
			Object[] object = (Object[]) it.next();
			int colunas = 0;
			
			List<AdesaoCadastroUnicoBolsa> adesoes = resultado.get(object[3]);
			
			if (adesoes == null)
				adesoes = new ArrayList<AdesaoCadastroUnicoBolsa>();

			
			AdesaoCadastroUnicoBolsa adesao = new AdesaoCadastroUnicoBolsa();
			
			adesao.setDiscente(new Discente());
			adesao.getDiscente().setPessoa(new Pessoa());
			
			adesao.getDiscente().getPessoa().setNome((String) object[colunas++]);
			adesao.getDiscente().setMatricula((Long) object[colunas++]);
			adesao.setPontuacao((Integer) object[colunas++]);
			adesoes.add(adesao);
			
			resultado.put((String) object[colunas++], adesoes);
		}
		
		return resultado;
	}	
	

	/**
	 * Retorna o ID da adesão feita até antes do período passado como argumento.
	 * Por exemplo: anoPeriodoAtual = 20091, vai trazer a última adesão feita até antes de 2009.1
	 * 
	 * @param discente
	 * @param anoPeriodoAtual
	 * @return
	 */
	public int findUltimaAdesao(DiscenteAdapter discente, int anoPeriodoAtual) {
		String sql = " select adesao.id_adesao " +
				" from sae.adesao_cadastro_unico adesao " +
				" inner join sae.cadastro_unico_bolsa cub using( id_cadastro_unico ) " +
				" where cast(ano || '' || '' || periodo as integer) <= ? and id_discente = ? " +
				" and cub.ativo = trueValue() " +
				" order by ano desc, periodo desc " + BDUtils.limit(1);		
		try {
			return getJdbcTemplate().queryForInt(sql, new Object[] { anoPeriodoAtual, discente.getId() });	
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	/**
	 * Método que calcula a quantidade de  discentes
	 * prioritários por faixa etária.
	 * 
	 * @param ano
	 * @param periodo
	 * @param sexo
	 * @param prazoConclusao
	 * @param residente
	 */
	public List<Map<String, Object>> calculaQtdPrioritariosFaixaEtaria(Integer ano, Integer periodo, String sexo, Integer prazoConclusao, Boolean residente) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) <= 18 then 1 else 0 end) as faixa_1, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 19 and extract(year from age(now(), p.data_nascimento)) <= 23 then 1 else 0 end) as faixa_2, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 24 and extract(year from age(now(), p.data_nascimento)) <= 28 then 1 else 0 end) as faixa_3, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 29 and extract(year from age(now(), p.data_nascimento)) <= 33 then 1 else 0 end) as faixa_4, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 34 and extract(year from age(now(), p.data_nascimento)) <= 38 then 1 else 0 end) as faixa_5, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 39 and extract(year from age(now(), p.data_nascimento)) <= 43 then 1 else 0 end) as faixa_6, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 44 and extract(year from age(now(), p.data_nascimento)) <= 48 then 1 else 0 end) as faixa_7, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 49 and extract(year from age(now(), p.data_nascimento)) <= 53 then 1 else 0 end) as faixa_8, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 54 and extract(year from age(now(), p.data_nascimento)) <= 58 then 1 else 0 end) as faixa_9, " +
				"SUM(case when extract(year from age(now(), p.data_nascimento)) >= 59 then 1 else 0 end) as faixa_10 " +
				"from sae.adesao_cadastro_unico adesao " +
				"	join discente d using (id_discente) " +
				"	join comum.pessoa p using (id_pessoa) ");
				
				if (residente) {
					sql.append(
							"	join sae.bolsa_auxilio ba on (ba.id_discente = d.id_discente) " +
							"	join sae.bolsa_auxilio_periodo bap on (bap.id_bolsa_auxilio = ba.id_bolsa_auxilio ) ");
				}
				
				sql.append("where " +
					"	adesao.ano = ? " +
					"	and adesao.periodo = ? " +
					"	and adesao.pontuacao <= ? ");
				
				if (!sexo.equals("0"))
					sql.append("	and p.sexo = ? ");
				if (!isEmpty(prazoConclusao))		
					sql.append("	and d.prazo_conclusao = ?");
				
				if (residente) {
					sql.append(
							"	and bap.ano = ? " +
							"	and bap.periodo = ? " +
							"	and ba.id_tipo_bolsa_auxilio in " + gerarStringIn(new int[] {TipoBolsaAuxilio.RESIDENCIA_GRADUACAO,TipoBolsaAuxilio.RESIDENCIA_POS} ));
				}
				
				
		List<Object> param = new ArrayList<Object>();
		param.add(ano);
		param.add(periodo);
		param.add(AdesaoCadastroUnicoBolsa.ALUNO_PRIORITARIO);
		if (!sexo.equals("0"))
			param.add(sexo);
		if (!isEmpty(prazoConclusao))
			param.add(prazoConclusao);
		if (residente) {
			param.add(ano);
			param.add(periodo);
		}
		
		return getJdbcTemplate().queryForList(sql.toString(), param.toArray());
	}
	
}
