package br.ufrn.shared.wireless.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Implementação de WirelessDAO
 * 
 * @author David Pereira
 * 
 */
@SuppressWarnings("unchecked")
public class WirelessDAO extends GenericSharedDBDao {

	public List<Object[]> findConexoes(UsuarioGeral usuario, Date dataInicio, Date dataFim)
			throws DAOException {
		Connection con = null;
		StringBuffer sql = new StringBuffer(
				"select p.nome, l.ip, u.tipo, l.mac, l.data from comum.logon_wireless l, comum.usuario u, comum.pessoa p "
						+ "where l.id_usuario = u.id_usuario and u.id_pessoa = p.id_pessoa ");

		if (usuario != null)
			sql.append("and l.id_usuario = ?");
		else
			sql.append("and l.data between ? and ?");

		List resultado = new ArrayList();

		try {
			con = Database.getInstance().getComumConnection();
			PreparedStatement st = con.prepareStatement(sql.toString());

			if (usuario != null) {
				st.setInt(1, usuario.getId());
			} else {
				st.setDate(1, new java.sql.Date(dataInicio.getTime()));
				st.setDate(2, new java.sql.Date(dataFim.getTime()));
			}

			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Object[] linha = new Object[5];
				linha[0] = rs.getString("nome");
				linha[1] = rs.getString("ip");
				UsuarioGeral user = new UsuarioGeral();
				user.setTipo(new TipoUsuario(rs.getInt("tipo")));
				linha[2] = user.getTipoUsuarioDesc();
				linha[3] = rs.getString("mac");
				linha[4] = rs.getTimestamp("data");

				resultado.add(linha);
			}

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			Database.getInstance().close(con);
		}

		return resultado;
	}

}
