/**
 *
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.PessoaGeral;

/**
 * DAO para Consulta de Pessoas
 *
 * @author Ricardo Wendell
 *
 */
public class PessoaDAO extends GenericSharedDBDao {


	/**
	 * Busca Pessoa pelo CPF/CNPJ
	 *
	 * @param cpfCnpj
	 * @return
	 * @throws DAOException
	 */
	public PessoaGeral findByCpfCnpj(long cpfCnpj) throws DAOException {
		Criteria c = getSession().createCriteria(PessoaGeral.class);
		c.add(Expression.eq("cpf_cnpj", cpfCnpj));

		return (PessoaGeral) c.uniqueResult();
	}

	/**
	 * Busca pessoa pelo id do usuario
	 *
	 * @param login
	 * @return
	 */
	public PessoaGeral findByIdUsuario(int idUsuario) {

		String sql = "select p.id_pessoa, p.nome from comum.pessoa p, comum.usuario u where u.id_pessoa = p.id_pessoa and u.id_usuario = ?";

		return (PessoaGeral) getJdbcTemplate().queryForObject(sql, new Object[] { idUsuario } ,new RowMapper(){

			public Object mapRow(ResultSet rs, int numRow) throws SQLException {
				PessoaGeral pessoa = new PessoaGeral();
				pessoa.setId(rs.getInt(1));
				pessoa.setNome(rs.getString(2));
				return pessoa;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<PessoaGeral> findByRazaoSocial(String nome, char tipo) throws DAOException {
		Criteria c = getSession().createCriteria(PessoaGeral.class);
		c.add(Expression.like("nomeAscii", StringUtils.toAscii(nome.toUpperCase()) + "%"));
		c.add(Expression.eq("tipo", String.valueOf(tipo).toUpperCase()));
		c.add(Expression.eq("valido", true));
		c.addOrder(Order.asc("nome"));

		return c.list();
	}

	@SuppressWarnings("unchecked")
	public List<PessoaGeral> findPessoaByNome(String nome) throws DAOException {
		Query q = getSession().createQuery("select p from PessoaGeral p where upper(p.nomeAscii) like upper(?)");
		q.setString(0, StringUtils.toAscii(nome + "%"));
		return q.list();
	}

	/**
	 * Retorna todas as pessoas que são também servidores da
	 * instituição de acordo com os parâmetros de busca especificados.
	 * @param nome
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<PessoaGeral> findPessoaServidorByNomeOrCpf(String nome, Long cpf) throws DAOException {

		String hql = "SELECT DISTINCT p FROM Servidor s INNER JOIN s.pessoa p WHERE p.valido = :valido ";

		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("valido", true);

		if(!isEmpty(nome)){
			hql += " AND p.nomeAscii LIKE :nomeAscii ";
			parametros.put("nomeAscii", StringUtils.toAsciiAndUpperCase(StringUtils.escapeBackSlash(nome)) + "%");
		}

		if(!isEmpty(cpf)){
			hql += " AND p.cpf_cnpj = :cpf ";
			parametros.put("cpf", cpf);
		}

		hql += " ORDER BY p.nome ";

		Query q = getSession().createQuery(hql);
		q.setProperties(parametros);

		return q.list();
	}

}