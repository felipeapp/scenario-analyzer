/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/08/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ead;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ead.dominio.AvaliacaoDiscenteEad;
import br.ufrn.sigaa.ead.dominio.FichaAvaliacaoEad;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.dominio.SemanaAvaliacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MediaItem;
import br.ufrn.sigaa.ensino.dominio.MediaItemDiscente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pesquisa.dominio.ItemAvaliacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO com métodos para a realização da avaliação semanal
 * de discentes pelos seus tutores.
 * 
 * @author David Pereira
 *
 */
public class FichaAvaliacaoEadDao extends GenericSigaaDAO {

	/**
	 * Retorna a ficha de avaliação do discente de acordo com o ano, período e componente curricular passados.
	 * 
	 * @param discente
	 * @param periodo
	 * @param ano
	 * @param componenteCurricular 
	 * @return
	 * @throws DAOException
	 */
	public FichaAvaliacaoEad findFichaAvaliacaoByDiscente(DiscenteAdapter discente, int periodo, int ano, ComponenteCurricular componenteCurricular) throws DAOException {
		try {
			Criteria c = getCriteria(FichaAvaliacaoEad.class);
			c.add(Expression.eq("discente.id", discente.getId()));
			c.add(Expression.eq("periodo", periodo));
			c.add(Expression.eq("ano", ano));
			if (componenteCurricular != null)
				c.add(Expression.eq("componente.id", componenteCurricular.getId()));
			
			@SuppressWarnings("unchecked")
			List<FichaAvaliacaoEad> fichas = c.list();
			if (!isEmpty(fichas))
				return fichas.get(0);
			else
				return null;
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	

	/**
	 * Retorna a {@link AvaliacaoDiscenteEad} da semana, de acordo com a ficha passada.
	 * 
	 * @param ficha
	 * @param semana
	 * @return
	 * @throws DAOException
	 */
	public AvaliacaoDiscenteEad findAvaliacaoBySemana(FichaAvaliacaoEad ficha, int semana) throws DAOException {
		try {
			Criteria c = getCriteria(AvaliacaoDiscenteEad.class);
			c.add(Expression.eq("ficha.id", ficha.getId()));
			c.add(Expression.eq("semana", semana));
			return (AvaliacaoDiscenteEad) c.uniqueResult();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna as fichas de avaliação relacionadas ao discente.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<FichaAvaliacaoEad> findFichasAvaliacaoByDiscente(Discente discente) throws DAOException {
		try {
			Criteria c = getCriteria(FichaAvaliacaoEad.class);
			c.add(Expression.eq("discente.id", discente.getId()));
			
			@SuppressWarnings("unchecked")
			List<FichaAvaliacaoEad> lista = c.list();
			return lista;
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Verifica se o componente faz parte da ficha de avaliação e se o discente está matriculado, aprovado ou reprovado na turma.
	 * 
	 * @param ficha
	 * @param componente
	 * @return
	 */
	public boolean isComponenteValido(FichaAvaliacaoEad ficha, ComponenteCurricular componente) {
		return getJdbcTemplate().queryForInt("select count(*) from ensino.matricula_componente mc, ensino.turma t, ensino.componente_curricular c "
				+ "where mc.id_discente=? and mc.ano = ? and mc.periodo = ? and t.id_disciplina = c.id_disciplina and c.id_tipo_componente != 1  "
				+ "and mc.id_turma = t.id_turma and t.id_disciplina = ? and mc.id_situacao_matricula in (2, 4, 6, 7) ",
				new Object[] { ficha.getDiscente().getId(), ficha.getAno(), ficha.getPeriodo(), componente.getId() }) > 0;
	}

	/**
	 * Retorna a lista de {@link MediaItemDiscente} existentes para a turma passada.
	 * 
	 * @param turma
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List <MediaItemDiscente> findByNotasUnidadesPorAluno(Turma turma, MetodologiaAvaliacao metodologia) throws HibernateException, DAOException {
		String sql = "(select p.nome, d.id_discente, n.id_item, d.matricula, max(a.semana) as semanaMaxima, sum(n.nota) as media_item_total, 1 as periodo_avaliacao " +
							"from discente d " +
							"inner join comum.pessoa p using (id_pessoa) " +
							"inner join ensino.matricula_componente mc using (id_discente) " +
							"inner join ensino.turma t using (id_turma) " +
							"left join ead.ficha_avaliacao_ead f on (f.id_discente = mc.id_discente and f.periodo = t.periodo and t.ano = f.ano) " +
							"left join ead.avaliacao_discente_ead a on (a.id_ficha = f.id ";
		
							if(!metodologia.isUmaProva())
								sql +=  "and semana <= " + metodologia.getNumeroAulasPrimeiraUnidade();
							
							sql += ") " +
							"left join ead.nota_item_avaliacao_ead n on (n.id_avaliacao = a.id and t.id_disciplina = n.id_componente) " +
						"where mc.id_turma = ? " +
						" group by p.nome, d.id_discente, n.id_item, d.matricula, p.nome " +
						"order by p.nome, d.id_discente, n.id_item, d.matricula, p.nome " +
						") ";

		if(!metodologia.isUmaProva())
			sql+=		"UNION " +
						"( " +
						"select p.nome, d.id_discente, n.id_item, d.matricula, max(a.semana) as semanaMaxima, sum(n.nota) as media_item_total, 2 as periodo_avaliacao " +
							"from discente d " +
							"inner join comum.pessoa p using (id_pessoa) " +
							"inner join ensino.matricula_componente mc using (id_discente) " +
							"inner join ensino.turma t using (id_turma) " +
							"left join ead.ficha_avaliacao_ead f on (f.id_discente = mc.id_discente and f.periodo = t.periodo and t.ano = f.ano) " +
							"left join ead.avaliacao_discente_ead a on (a.id_ficha = f.id and semana > " + metodologia.getNumeroAulasPrimeiraUnidade() + ") " +
							"left join ead.nota_item_avaliacao_ead n on (n.id_avaliacao = a.id and t.id_disciplina = n.id_componente) " +
						"where mc.id_turma = ?" +
						" group by p.nome, d.id_discente, n.id_item, d.matricula, p.nome " +
						"order by p.nome, d.id_discente, n.id_item, d.matricula, p.nome " +
						") order by nome, id_discente, id_item , periodo_avaliacao";
		
		List <Map <String, Object>> rs = null;
		if(metodologia.isUmaProva())
			rs = getJdbcTemplate().queryForList(sql, new Object[] { turma.getId() });
		else
			rs = getJdbcTemplate().queryForList(sql, new Object[] { turma.getId(), turma.getId() });
		
		List<MediaItemDiscente> medias = new ArrayList<MediaItemDiscente>();
		Discente d = null;
		boolean adicionado = false;
		
		for (Map<String, Object> map : rs) {
			adicionado = false;
			
			Integer idDiscente = (Integer) map.get("id_discente");
			Long matricula = (Long) map.get("matricula");
			String nome = (String) map.get("nome");
			d = new Discente(idDiscente);
			d.setMatricula(matricula);
			d.setPessoa(new Pessoa());
			d.getPessoa().setNome(nome);
			
			Integer semanaMaximaAvaliacao = (Integer) map.get("semanaMaxima");
			MediaItemDiscente mediaItemDiscente = new MediaItemDiscente();
			mediaItemDiscente.setDiscente(d);
			mediaItemDiscente.setMetodologia(metodologia);
			
			if(medias.contains(mediaItemDiscente)) {
				mediaItemDiscente = medias.get(medias.indexOf(mediaItemDiscente));
				adicionado = true;
			}
			if(!isEmpty(semanaMaximaAvaliacao))
				mediaItemDiscente.setSemanaMaximaAvaliacao(semanaMaximaAvaliacao);
			
			Integer idItemAvaliacao = (Integer) map.get("id_item");
			Integer periodoAvaliacao = (Integer) map.get("periodo_avaliacao");
			BigDecimal mediaTotal = (BigDecimal) map.get("media_item_total");
			MediaItem mediaItem = new MediaItem();
			if(!isEmpty(idItemAvaliacao)) {
				mediaItem.setItem(new ItemAvaliacao(idItemAvaliacao));
				mediaItem.setPeriodoAvaliacao(periodoAvaliacao);
				mediaItemDiscente.addMediaItem(mediaItem);
				
				if(!isEmpty(mediaTotal)) {
				    if(metodologia.isUmaProva())
					mediaTotal = mediaTotal.divide(new BigDecimal((turma.getDisciplina().getChTotalAula() > 100 ? 8 : 4)), 1, RoundingMode.HALF_UP);
				    else
					mediaTotal = mediaTotal.divide(new BigDecimal(metodologia.getNumeroAulasByUnidade(periodoAvaliacao)), 1, RoundingMode.HALF_UP);
				}
				
				mediaItem.setMedia(mediaTotal);
			}
			
			if(!adicionado)
				medias.add(mediaItemDiscente);
		}
		
		return medias;
	}

	/**
	 * Retorna o conjunto de ano e período de matrícula do discente na turma do componente presentes na ficha de avaliação.
	 * 
	 * @param obj
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>>  anoPeriodoComponenteCurricularByDiscente(FichaAvaliacaoEad obj) throws HibernateException, DAOException  {
		return anoPeriodoComponenteCurricularByDiscente(obj.getDiscente().getId(), obj.getComponente().getId());
	}

	/**
	 * Retorna o conjunto de ano e período de matrícula do discente na turma do componente passado.
	 * 
	 * @param discenteEscolhido
	 * @param componenteEscolhido
	 * @return
	 */
	public List<Map<String, Object>>  anoPeriodoComponenteCurricularByDiscente(Integer discenteEscolhido, Integer componenteEscolhido) {
		String sql = " select ano, periodo from discente d " +
		 " inner join ensino.matricula_componente mc on mc.id_discente = d.id_discente " +
		 " inner join ensino.componente_curricular cc on cc.id_disciplina = mc.id_componente_curricular " +
		 " where d.id_discente = " + discenteEscolhido  + " and cc.id_disciplina = " + componenteEscolhido + " and mc.id_situacao_matricula not in " + UFRNUtils.gerarStringIn(new int[] {SituacaoMatricula.EXCLUIDA.getId(), SituacaoMatricula.DESISTENCIA.getId(), SituacaoMatricula.CANCELADO.getId()}) +
		 " order by ano, periodo DESC";

		List<Map<String, Object>>  lista = getJdbcTemplate().queryForList(sql);
		return lista;
	}

	/**
	 * Retorna uma lista de ids de componentes que possuem avaliações não consolidadas.
	 * 
	 * @param listSemanas
	 * @return
	 */
	public List<Integer> findSemanasAvaliacaoNaoConsolidadas(List<SemanaAvaliacao> listSemanas) {

		if (listSemanas.size() == 0) { // caso não tenha semanas de avaliação ativas no momento
			listSemanas.add(new SemanaAvaliacao());
		}
			
		List<Integer> listaIDS = new ArrayList<Integer>();
		for (SemanaAvaliacao integer : listSemanas) {
			listaIDS.add(integer.getSemana());
		}

		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id_disciplina",listaIDS);
		
		String sql = "select cc.id_disciplina as id_disciplina from ensino.componente_curricular cc " +
					 "inner join ensino.turma t on t.id_disciplina = cc.id_disciplina " +
					 "where cc.id_disciplina in (:id_disciplina) " +
					 "and id_situacao_turma = 1"; // NAO consolidadas
		
		ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
			public Integer mapRow(ResultSet rs, int arg1) throws SQLException {
				return rs.getInt("id_disciplina");
			}
		};
		return this.getSimpleJdbcTemplate().query(sql, mapper, params);
	}
	
	/**
	 * Busca uma matrícula de uma turma de forma aleatória.
	 *
	 * @param turma
	 * @param excluindoEssa
	 * @return
	 * @throws DAOException
	 */
	public Integer findMatriculaAleatoriaByTurma(Turma turma) throws DAOException {
		Query q = getSession().createQuery("select mc.discente.id from MatriculaComponente mc where mc.turma.id = ? order by random()");
		q.setInteger(0, turma.getId());
		q.setMaxResults(1);
		return (Integer) q.uniqueResult();
	}
	
	/**
	 * Busca uma matrícula de uma turma de forma aleatória.
	 *
	 * @param turma
	 * @param excluindoEssa
	 * @return
	 * @throws DAOException
	 */
	public Integer findMatriculaAleatoriaByTurmaAgrupadora(Turma turma) throws DAOException {
		Query q = getSession().createQuery("select mc.discente.id from MatriculaComponente mc " +
											" where mc.turma.turmaAgrupadora.id = ? order by random()");
		q.setInteger(0, turma.getId());
		q.setMaxResults(1);
		return (Integer) q.uniqueResult();
	}
}
