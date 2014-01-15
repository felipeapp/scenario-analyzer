/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/05/2009
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;

/**
 * DAO para buscar e salvar mensagens
 * de aviso no banco de dados.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class MensagemAvisoDao extends GenericSharedDBDao {

	private static final String SQL = "select m.*, s.descricao as sistema, ss.nome as subsistema from comum.mensagem_aviso m left join comum.sistema s on (m.id_sistema = s.id) left join comum.subsistema ss on (m.id_subsistema = ss.id) ";
	
	private static final RowMapper MENSAGEM_AVISO_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			MensagemAviso ma = new MensagemAviso();
			ma.setCodigo(rs.getString("codigo"));
			ma.setClasseConstantes(rs.getString("classe_constantes"));
			ma.setMensagem(rs.getString("mensagem"));
			ma.setSistema(new Sistema(rs.getInt("id_sistema")));
			ma.getSistema().setNome(rs.getString("sistema"));
			ma.setSubSistema(new SubSistema(rs.getInt("id_subsistema")));
			ma.getSubSistema().setNome(rs.getString("subsistema"));
			ma.setTipo(TipoMensagemUFRN.valueOf(rs.getInt("tipo")));
			return ma;
		}
	};
	
	/**
	 * Salva uma mensagem. Se o id for 0, insere uma nova no banco,
	 * se for maior que zero atualiza.
	 * @param msg
	 */
	public void salvar(MensagemAviso msg) {
		if (getJdbcTemplate().queryForInt("select count(*) from comum.mensagem_aviso where codigo = ?", new Object[] { msg.getCodigo() }) == 0) {
			update("insert into comum.mensagem_aviso (codigo, classe_constantes, mensagem, id_sistema, id_subsistema, tipo) values "
					+ "(?, ?, ?, ?, ?, ?)", new Object[] { msg.getCodigo(), msg.getClasseConstantes(), msg.getMensagem(), msg.getSistema().getId(), (msg.getSubSistema() == null ? null : msg.getSubSistema().getId()), msg.getTipo().ordinal() });
		} else {
			update("update comum.mensagem_aviso set classe_constantes=?, mensagem=?, id_sistema = ?, id_subsistema = ?, tipo = ? where codigo = ?",
					new Object[] { msg.getClasseConstantes(), msg.getMensagem(), msg.getSistema().getId(), (msg.getSubSistema() == null ? null : msg.getSubSistema().getId()), msg.getTipo().ordinal(), msg.getCodigo() });
		}
	}
	
	/**
	 * Busca uma mensagem de aviso pelo seu código.
	 * @param codigo
	 * @return
	 */
	public MensagemAviso findByCodigo(String codigo) {
		try {
			return (MensagemAviso) getJdbcTemplate().queryForObject(SQL + "where codigo = ?", 
				new Object[] { codigo }, MENSAGEM_AVISO_MAPPER);
		} catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Busca uma mensagem de aviso pelo sistema ou subsistema. Se esses parâmetros
	 * forem null, não são considerados na busca.
	 * @param sistema
	 * @param subSistema
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<MensagemAviso> findBySistemaSubSistema(Sistema sistema, SubSistema subSistema) {
		StringBuilder sql = new StringBuilder(SQL + "where 1 = 1 ");
		List<Object> params = new ArrayList<Object>();
		
		if (!isEmpty(sistema)) {
			sql.append("and m.id_sistema = ? ");
			params.add(sistema.getId());
		}
		
		if (!isEmpty(subSistema)) {
			sql.append("and m.id_subsistema = ? ");
			params.add(subSistema.getId());
		}
		
		return getJdbcTemplate().query(sql.toString(), params.toArray(), MENSAGEM_AVISO_MAPPER);
	}

	/**
	 * Busca uma mensagem de aviso pela chave primária.
	 * @param id
	 * @return
	 */
	public MensagemAviso findByPrimaryKey(int id) {
		return (MensagemAviso) getJdbcTemplate().queryForObject(SQL + "where m.id = ?", 
				new Object[] { id }, MENSAGEM_AVISO_MAPPER);
	}

	/**
	 * Remove uma mensagem do banco de dados.
	 * @param msg
	 */
	public void remover(MensagemAviso msg) {
		update("delete from comum.mensagem_aviso where codigo = ?", new Object[] { msg.getCodigo() });
	}

}

