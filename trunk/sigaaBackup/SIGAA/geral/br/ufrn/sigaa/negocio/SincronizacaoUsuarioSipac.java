/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '14/02/2007'
 *
 */
package br.ufrn.sigaa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import br.ufrn.arq.dao.Database;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe para sincronização dos usuários dos alunos do Sigaa
 * com o Sipac 
 * @author David Pereira
 *
 */
public class SincronizacaoUsuarioSipac {

	/**
	 * Verifica se uma pessoa existe no sipac e retorna o seu id
	 * @param cpf Cpf da pessoa a ser buscada
	 * @param passaporte passaporte da pessoa a ser buscada
	 * @return id da pessoa no banco do sipac, se existir; -1 se não existir.
	 * @throws SQLException
	 */
	public int buscaIdPessoaSipac(Long cpf, String passaporte, Connection con) throws SQLException {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select id_pessoa from comum.pessoa where ");
			if (isNotEmpty(cpf))
				sql.append(" cpf_cnpj=? ");
			else
				sql.append(" passaporte=? ");
			
			st = con.prepareStatement(sql.toString());
			if (isNotEmpty(cpf))
				st.setLong(1, cpf);
			else
				st.setString(1, passaporte);
			
			rs = st.executeQuery();
			if (rs.next()) {
				return rs.getInt("id_pessoa");
			} else {
				return -1;
			}
			
		} finally {
			Database.getInstance().close(rs);
			Database.getInstance().close(st);
		}
		
	}

	/**
	 * Cadastra a pessoa do aluno cadastrado no Sipac
	 * @param p Pessoa do aluno
	 * @return id da pessoa cadastrada
	 * @throws SQLException
	 */
	public int cadastroPessoa(PessoaGeral p, Connection con, boolean isSipac) throws SQLException {
		PreparedStatement st = con.prepareStatement("insert into comum.pessoa "
				+ "(id_pessoa, nome, data_nascimento, sexo, passaporte, cpf_cnpj, "
				+ "email, endereco, tipo, valido, funcionario, internacional) values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");			
		
		
		st.setInt(1, p.getId());
		st.setString(2, p.getNome());
		
		if (p.getDataNascimento() == null)
			st.setNull(3, Types.DATE);
		else
			st.setDate(3, new java.sql.Date(p.getDataNascimento().getTime()));
		
		st.setString(4, String.valueOf(p.getSexo()));
		st.setString(5, p.getPassaporte());
		
		if (isNotEmpty(p.getCpf_cnpj()))
			st.setLong(6, p.getCpf_cnpj());
		else if (isSipac && isEmpty(p.getCpf_cnpj()))
			st.setLong(6, -p.getId());
		else
			st.setNull(6, Types.BIGINT);
		
		st.setString(7, p.getEmail());
		st.setString(8, p.getEndereco());
		st.setString(9, String.valueOf(p.getTipo()));
		st.setBoolean(10, p.isValido());
		
		if (p.isFuncionario() == null)
			st.setNull(11, Types.BOOLEAN);
		else
			st.setBoolean(11, p.isFuncionario());
			
		st.setBoolean(12, p.isInternacional());
		
		st.executeUpdate();
		
		return p.getId();
	}
	
	/**
	 * Cadastra usuário no sipac
	 * @param u Usuário a ser cadastrado
	 * @throws SQLException
	 */
	public void cadastroUsuario(UsuarioGeral u, Connection con) throws SQLException {
		
		PreparedStatement st = con.prepareStatement("insert into comum.usuario "
				+ "(id_usuario, login, email, inativo, id_aluno, "
				+ "id_servidor, autorizado, expira_senha, id_unidade, funcionario, id_setor, "
				+ "id_pessoa, tipo, ramal, objetivo_cadastro, justificativa_negado, data_cadastro) values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ "?, ?, ?, ?)");			

		st.setInt(1, u.getId());
		st.setString(2, u.getLogin());
		st.setString(3, u.getEmail());
		st.setBoolean(4, false); 
		st.setNull(5, Types.INTEGER);
		
		if (u.getIdServidor() == null || u.getIdServidor() == 0)
			st.setNull(6, Types.INTEGER);
		else
			st.setInt(6, u.getIdServidor());
		st.setBoolean(7, u.isAutorizado());
		if (u.getExpiraSenha() == null)
			st.setNull(8, Types.DATE);
		else
			st.setDate(8, new java.sql.Date(u.getExpiraSenha().getTime()));
		st.setInt(9, u.getUnidade().getId());
		st.setBoolean(10, u.isFuncionario());
		st.setNull(11, Types.INTEGER);

		st.setInt(12, u.getPessoa().getId());
		st.setInt(13, u.getTipo().getId());
		st.setString(14, u.getRamal());
		st.setString(15, u.getObjetivoCadastro());
		st.setString(16, u.getJustificativaCadastroNegado());
		if (u.getDataCadastro() == null)
			st.setNull(17, Types.DATE);
		else
			st.setDate(17, new java.sql.Date(u.getDataCadastro().getTime()));
		
		st.executeUpdate();
		
		st.close();
	}
	
	public void alteraDadosPessoais(Usuario usr, Connection con) throws SQLException {
		PreparedStatement st = con.prepareStatement("update comum.usuario set email=?, where id_usuario=?");
		
		st.setString(1, usr.getEmail());
		st.setInt(2, usr.getId());
		
		st.executeUpdate();
	}
	
}
