/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/06/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ead;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.CoordenacaoTutoria;
import br.ufrn.sigaa.ead.dominio.FichaAvaliacaoEad;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO para buscas de Ensino a Distância
 * 
 * @author David Pereira
 *
 */
public class EADDao extends GenericSigaaDAO {

        /**
         * Busca o último coordenador de tutoria cadastrado ativo no sistema.
         * 
         * @return
         * @throws DAOException
         */
	public CoordenacaoTutoria findUltimaCoordenacao() throws DAOException {
		Criteria c = getSession().createCriteria(CoordenacaoTutoria.class);
		c.add(Expression.eq("ativo", true));
		c.add(Expression.isNull("dataFim"));
		return (CoordenacaoTutoria) c.uniqueResult();
	}

	/**
	 * Lista as metodologias de avaliação cadastradas, mostrando primeiro as ativas e depois as inativas.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<MetodologiaAvaliacao> findMetodologiasOrderByAtivos() throws DAOException {
		@SuppressWarnings("unchecked")
		Collection<MetodologiaAvaliacao> lista = getSession().createQuery("from MetodologiaAvaliacao m order by m.ativa desc, m.curso.nome").list();
		return lista;
	}
	
	/**
	 * Retorna a metodologia de avaliação ativa definida para o curso passado.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public MetodologiaAvaliacao findMetodologiaAvaliacaoPorCurso(Curso curso) throws DAOException {
		try {
			Query q = getSession().createQuery(" select m " +
					" from MetodologiaAvaliacao m" +
					" where m.curso.id = :curso " +
					" and m.ativa = true" +
					" order by m.dataCadastro");
			q.setInteger("curso", curso.getId());
			return (MetodologiaAvaliacao) q.uniqueResult();
		} catch(Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca as notas dos tutores cadastradas para o discente na turma, ano e períodos definidos na {@link MatriculaComponente} passada.
	 * 
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public Double findNotaTutor(MatriculaComponente matricula) throws DAOException {
		String query = "select sum(n.nota)/count(*) from ead.ficha_avaliacao_ead f, ead.avaliacao_discente_ead a, ead.nota_item_avaliacao_ead n "
			+ "where a.id_ficha = f.id and n.id_avaliacao = a.id and f.id_discente = ? and n.id_componente = ? "
			+ "and ano = ? and periodo = ?";
		
		SQLQuery q = getSession().createSQLQuery(query);
		q.setInteger(0, matricula.getDiscente().getId());
		q.setInteger(1, matricula.getTurma().getDisciplina().getId());
		q.setInteger(2, matricula.getTurma().getAno());
		q.setInteger(3, matricula.getTurma().getPeriodo());
		
		Number result = (Number) q.uniqueResult();
		if (result != null)
			return result.doubleValue();
		else
			return 0.0;
	}
	
	/**
	 * Busca as notas dos tutores cadastradas para as turmas de um ano e períodos definidos
	 * 
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @param metodologiaAvaliacao
	 * @param aulaInicio
	 * @param aulaFim
	 * @return mapa idMatricula/Nota
	 * @throws DAOException
	 */
	public Map<Integer, Double> findNotasTutorByIntervaloAulas(int idDiscente, int ano, int periodo, MetodologiaAvaliacao metodologiaAvaliacao, int aulaInicio, int aulaFim) throws DAOException {
		String query =  " select id_componente, sum(n.nota)/((select count(*) from ead.item_avaliacao_ead where id_metodologia=? and ativo = trueValue()) * (?)) " +
						" from ead.ficha_avaliacao_ead f, ead.avaliacao_discente_ead a, " +
						" ead.nota_item_avaliacao_ead n " +
						" where a.id_ficha = f.id and n.id_avaliacao = a.id and f.id_discente = ? " +
						" and ano = ? and periodo = ? and a.semana >= ? and a.semana <= ? " +
						" group by id_componente";
		
		SQLQuery q = getSession().createSQLQuery(query);
		q.setInteger(0, metodologiaAvaliacao.getId());
		q.setInteger(1, (aulaFim-aulaInicio)+1);
		
		q.setInteger(2, idDiscente);
		q.setInteger(3, ano);
		q.setInteger(4, periodo);
		q.setInteger(5, aulaInicio);
		q.setInteger(6, aulaFim);
		
		Map<Integer, Double> result = new TreeMap<Integer, Double>();
		@SuppressWarnings("unchecked")
		Collection<Object[]>lista = q.list();
		for (Object[] obj : lista) {
			result.put((Integer) obj[0], ((Number) obj[1]).doubleValue());
		}
		return result;
	}

	/**
	 * Busca as notas dos tutores cadastradas para o discente na turma, ano e períodos definidos na {@link MatriculaComponente} 
	 * passada restringido a busca às semanas passadas.
	 * 
	 * @param matricula
	 * @param metodologiaAvaliacao
	 * @param aulaMin
	 * @param aulaMax
	 * @return
	 * @throws DAOException
	 */
	public Double findNotaTutorByIntervaloAulas(MatriculaComponente matricula, MetodologiaAvaliacao metodologiaAvaliacao, int aulaMin, int aulaMax) throws DAOException {
//		String query = "select sum(n.nota)/count(*) from ead.ficha_avaliacao_ead f, ead.avaliacao_discente_ead a, ead.nota_item_avaliacao_ead n "
//			+ "where a.id_ficha = f.id and n.id_avaliacao = a.id and f.id_discente = ? and n.id_componente = ? "
//			+ "and ano = ? and periodo = ? and a.semana >= ? and a.semana <= ?";
		
		String query =  " select sum(n.nota)/((select count(*) from ead.item_avaliacao_ead where id_metodologia=? and ativo = trueValue()) * (?)) " +
						" from ead.ficha_avaliacao_ead f, ead.avaliacao_discente_ead a, " +
						" ead.nota_item_avaliacao_ead n " +
						" where a.id_ficha = f.id and n.id_avaliacao = a.id and f.id_discente = ? and n.id_componente = ? " +
						" and ano = ? and periodo = ? and a.semana >= ? and a.semana <= ? ";
		
		SQLQuery q = getSession().createSQLQuery(query);
		q.setInteger(0, metodologiaAvaliacao.getId());
		q.setInteger(1, (aulaMax-aulaMin)+1);
		
		q.setInteger(2, matricula.getDiscente().getId());
		q.setInteger(3, matricula.getTurma().getDisciplina().getId());
		q.setInteger(4, matricula.getTurma().getAno());
		q.setInteger(5, matricula.getTurma().getPeriodo());
		q.setInteger(6, aulaMin);
		q.setInteger(7, aulaMax);
		
		Number result = (Number) q.uniqueResult();
		if (result != null)
			return result.doubleValue();
		else
			return null;
	}
	
	/**
	 * Busca as notas dos tutores cadastradas para o discente na turma, ano e períodos definidos na {@link MatriculaComponente} passada.
	 * 
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Integer,Double> findNotaTutorTurma(Turma turma, MetodologiaAvaliacao metodologiaAvaliacao) throws DAOException {
		
		int semanas;
		if ( turma.getDisciplina().getChTotalAula() < 100 )
			semanas = FichaAvaliacaoEad.QTD_SEMANAS_COMPONENTE_CURTO;
		else
			semanas = FichaAvaliacaoEad.QTD_SEMANAS_COMPONENTE_LONGO;
			
		String query = "select m.id_matricula_componente , (sum(n.nota)/((select count(*) from ead.item_avaliacao_ead where id_metodologia=? and ativo = trueValue()) * (?))) " 
			+ " from ensino.matricula_componente m, ead.ficha_avaliacao_ead f, ead.avaliacao_discente_ead a, ead.nota_item_avaliacao_ead n "
			+ " where a.id_ficha = f.id and n.id_avaliacao = a.id and f.id_discente = m.id_discente and m.id_turma = ? and n.id_componente = ? "
			+ " and f.ano = ? and f.periodo = ? and m.ano = ? and m.periodo = ? "
			+ " group by m.id_matricula_componente ";
		
		SQLQuery q = getSession().createSQLQuery(query);
		q.setInteger(0, metodologiaAvaliacao.getId());
		q.setInteger(1, semanas);
		q.setInteger(2, turma.getId());
		q.setInteger(3, turma.getDisciplina().getId());
		q.setInteger(4, turma.getAno());
		q.setInteger(5, turma.getPeriodo());
		q.setInteger(6, turma.getAno());
		q.setInteger(7, turma.getPeriodo());
		
		List<Object[]> result = q.list();
		
		HashMap<Integer,Double> notasMatricula = new HashMap<Integer,Double>();
		if (result != null) {
			
			for ( Object[] linha : result ) {
				 Integer i = 0;	
				 
				 Integer idMatricula = ( Integer ) linha[i++];
				 Double nota = (( Number ) linha[i++]).doubleValue();
				 
				 notasMatricula.put(idMatricula, nota);	 
			}
			return notasMatricula;
		}	
		
		return notasMatricula;
	}
	
	/**
	 * Busca as notas dos tutores cadastradas para os discentes da turma, dentro de um intervalo de aulas.
	 * 
	 * @param Turma
	 * @param metodologiaAvaliacao
	 * @param aulaMin
	 * @param aulaMax
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Integer,Double> findNotaTutorByIntervaloAulasTurma(Turma turma, MetodologiaAvaliacao metodologiaAvaliacao, int aulaMin, int aulaMax) throws DAOException {

		
		String query =  " select m.id_matricula_componente, (sum(n.nota)/((select count(*) from ead.item_avaliacao_ead where id_metodologia=? and ativo = trueValue()) * (?))) " +
						" from ensino.matricula_componente m , ead.ficha_avaliacao_ead f, ead.avaliacao_discente_ead a,  ead.nota_item_avaliacao_ead n  " +
						" where a.id_ficha = f.id and n.id_avaliacao = a.id and f.id_discente = m.id_discente and m.id_turma = ? " +
						" and n.id_componente = ?  and m.ano = ? and m.periodo = ? and f.ano = ? and f.periodo = ? and a.semana >= ? and a.semana <= ? " +
						" group by m.id_matricula_componente ";
		
		SQLQuery q = getSession().createSQLQuery(query);
		q.setInteger(0, metodologiaAvaliacao.getId());
		q.setInteger(1, (aulaMax-aulaMin)+1);
		
		q.setInteger(2, turma.getId());
		q.setInteger(3, turma.getDisciplina().getId());
		q.setInteger(4, turma.getAno());
		q.setInteger(5, turma.getPeriodo());
		q.setInteger(6, turma.getAno());
		q.setInteger(7, turma.getPeriodo());
		q.setInteger(8, aulaMin);
		q.setInteger(9, aulaMax);
		
		List<Object[]> result = q.list();
		
		HashMap<Integer,Double> notasMatricula = new HashMap<Integer,Double>();
		if (result != null) {
			for ( Object[] linha : result ) {
				 Integer i = 0;	
				 
				 Integer idMatricula = ( Integer ) linha[i++];
				 Double nota = (( Number ) linha[i++]).doubleValue();
				 
				 notasMatricula.put(idMatricula, nota);	 
			}
			return notasMatricula;
		}	

		return notasMatricula;
	}
	
	/**
	 * Retorna uma coleção contendo todos os docentes ativos de turmas ead.
	 * 
	 * @return
	 */
	public Collection<Servidor> findAllDocentesEad() {
    	    String sql = "select distinct id_servidor, siape, id_pessoa, nome, lotacao, id_formacao, regime_trabalho, dedicacao_exclusiva " +
    	    		"from rh.servidor s " +
    	    			"inner join comum.pessoa p using (id_pessoa) " +
                            	"inner join ensino.docente_turma dt on (s.id_servidor = dt.id_docente) " +
                            	"inner join ensino.turma t on (t.id_turma = dt.id_turma) " +
                            "where t.distancia = trueValue() and id_categoria = ? and id_ativo = ?" +
                        "order by nome";
	    
	    @SuppressWarnings("unchecked")
	    Collection<Servidor> lista = getJdbcTemplate().query( sql.toString(), new Object[] { Categoria.DOCENTE, Ativo.SERVIDOR_ATIVO },  new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Servidor servidor = new Servidor();
				servidor.setId(rs.getInt("id_servidor"));
				servidor.setSiape(rs.getInt("siape"));
				servidor.getPessoa().setId(rs.getInt("id_pessoa"));
				servidor.getPessoa().setNome(rs.getString("nome"));
				servidor.setLotacao(rs.getString("lotacao"));
				servidor.setFormacao(new Formacao());
				servidor.getFormacao().setId(rs.getInt("id_formacao"));
				servidor.setRegimeTrabalho(rs.getInt("regime_trabalho"));
				servidor.setDedicacaoExclusiva(rs.getBoolean("dedicacao_exclusiva"));

				return servidor;
			
			}
	    });

	    return lista;
	}
	
	/**
	 * Retorna a metodologia de avaliação EAD de uma turma
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Integer findMetodologiaAvaliacaoByTurma(Integer idTurma) throws HibernateException, DAOException {
	
		String sql = "select ma.metodo_avaliacao from ensino.turma t " +
				"join ensino.matricula_componente m on m.id_turma = t.id_turma " +
				"join discente d on d.id_discente = m.id_discente " +
				"join curso c on c.id_curso = d.id_curso " +
				"join ead.metodologia_avaliacao ma on ma.id_curso = c.id_curso " +
				"where t.id_turma = " + idTurma +
				"limit 1";
	
		Integer res = (Integer) getSession().createSQLQuery(sql).uniqueResult();
		return res;
	}
}
