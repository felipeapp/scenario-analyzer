package br.ufrn.sigaa.assistencia.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.TipoBolsaDTO;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Dao responsável pelas consultas relativas 
 * ao desempenho academico dos bolsistas.
 * 
 * @author Jean Guerethes
 */
public class DesempenhoAcademicoBolsistaDao extends GenericSigaaDAO {

	/**
	 * Retorna uma coleção de discente dentro dos critérios informados.
	 * 
	 * @param matricula
	 * @param ano
	 * @param periodo
	 * @param verificarSituacao
	 * @param trancadosReprovados
	 * @param situacoes
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Discente> findByDiscentes(Collection<String> matricula, int ano, int periodo, boolean verificarSituacao, boolean trancadosReprovados, SituacaoMatricula... situacoes) throws HibernateException, DAOException {

		StringBuilder sql = new StringBuilder();
		String projecao = "d.id_discente, p.nome, d.nivel";
		
		sql.append(	" select distinct " + projecao +
					" from ensino.matricula_componente mc"+
					" inner join discente d using (id_discente)"+
					" inner join comum.pessoa p using (id_pessoa)"+
					" where mc.ano = :ano and mc.periodo = :periodo");
					
					if (verificarSituacao){
						if (trancadosReprovados) 
							sql.append(" and mc.id_situacao_matricula not in "+ gerarStringIn(situacoes));
						else 
							sql.append(" and mc.id_situacao_matricula in "+ gerarStringIn(situacoes));
					}
					
		sql.append( " and d.matricula in " + gerarStringIn(matricula) +
				    " order by " + projecao);
		
		Query query = getSession().createSQLQuery(sql.toString());
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
	
		List<Object[]> lista = query.list();
		Collection<Discente> discentes = new ArrayList<Discente>();

		if (lista != null) {
			for (Object[] linha : lista) {
				if (!discentes.contains(linha)) {
					Discente discente = new Discente();
					discente.setId((Integer) linha[0]);
					discente.getPessoa().setNome((String) linha[1]);
					discente.setNivel((Character) linha[2]);
					discentes.add(discente);
				}
			}
		}		
		return discentes;
	}
	
	/**
	 * Responsável por percorrer o resultado do tipo de bolsa. 
	 */
	private static final RowMapper BOLSA_DTO_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			TipoBolsaDTO bolsa = new TipoBolsaDTO();
			
			bolsa.setId(rs.getInt("id"));
			bolsa.setDenominacao(rs.getString("denominacao"));

			return bolsa;
		}
	};
	
	/**
	 * Retorna uma coleção com todos os tipos de bolsa encontradas no SIPAC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TipoBolsaDTO> findAllTipoBolsa() {
		return getJdbcTemplate(Sistema.SIPAC).query("select b.id, b.denominacao " +
		 " from bolsas.tipo_bolsa b order by b.denominacao ", BOLSA_DTO_MAPPER);
	}

}