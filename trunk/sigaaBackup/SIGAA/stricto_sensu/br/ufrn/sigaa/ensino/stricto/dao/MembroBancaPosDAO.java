package br.ufrn.sigaa.ensino.stricto.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Consultas relacionada aos membros da banca de pós graduação. 
 * @author Mário Rizzi
 *
 */
public class MembroBancaPosDAO  extends GenericSigaaDAO  {

	/**
	 * Retorna todas as pessoas associadas a docentes ativos e membros de banca.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> findPessoaByNome(String nome) throws DAOException{
			
		StringBuilder projecao = new StringBuilder("p.id_pessoa, p.nome, p.cpf_cnpj, p.email ");
		
		StringBuilder sql = new StringBuilder(
				" SELECT DISTINCT " + projecao + " FROM ( "+ 
				" ( SELECT DISTINCT " + projecao +
				" 	FROM ensino.docente_externo " +
				"	INNER JOIN comum.pessoa p USING(id_pessoa)  " +
				" ) UNION ( SELECT DISTINCT " + projecao +
				" 	FROM stricto_sensu.membro_banca_pos " +
				"	INNER JOIN comum.pessoa p ON(p.id_pessoa = id_pessoa_membro_externo) " +
				" )  ) AS p WHERE LOWER(p.nome) like LOWER( ? ) ORDER BY p.nome ");
		
		Query q = getSession().createSQLQuery(sql.toString());	
		
		return getJdbcTemplate().query(sql.toString(), new Object[]{"%" + nome + "%"}, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Pessoa p = new Pessoa();
				p.setId( rs.getInt("id_pessoa") );
				p.setNome( rs.getString("nome") );
				p.setCpf_cnpj( rs.getLong("cpf_cnpj") );
				p.setEmail( rs.getString("email") );
				return p;
			}
		});

	}
	
}
