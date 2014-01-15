/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 07/11/2006
 *
 */
package br.ufrn.comum.sincronizacao;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.arq.dao.Database;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.UsuarioUnidade;

/**
 * Classe para sincronizar o cadastro de usuários nos três bancos
 * (Comum, Administrativo e Acadêmico).
 *
 * @author David Ricardo
 *
 */
public class SincronizaCadastroUsuariosUnidades {

	private JdbcTemplate jt;

	private SincronizaCadastroUsuariosUnidades(DataSource ds) {
		this.jt = new JdbcTemplate(ds);
	}

	public void removerUsuarioUnidade(UsuarioUnidade usuarioUnidade) {
		jt.update("delete from comum.usuario_unidade where id_usuario=? and id_unidade=?", new Object[] { usuarioUnidade.getUsuario().getId(), usuarioUnidade.getUnidade().getId() });
	}

	public void sincronizarUsuarioUnidade(UsuarioUnidade usuarioUnidade) {
			if (!hasUsuarioUnidade(usuarioUnidade)) {
				jt.update("insert into comum.usuario_unidade "
						+ "(id_usuario, id_unidade, id_usuario_cadastro, data, id_responsabilidade_unidade) values "
						+ "(?, ?, ?, ?, ?)", new Object[] { usuarioUnidade.getUsuario().getId(), usuarioUnidade.getUnidade().getId(), usuarioUnidade.getUsuarioCadastro().getId(), usuarioUnidade.getDataCadastro(), usuarioUnidade.getResponsabilidadeUnidade() });
			}
	}

	private boolean hasUsuarioUnidade(UsuarioUnidade usuarioUnidade) {
		int count = jt.queryForInt("select count(*) from comum.usuario_unidade where id_usuario=? and id_unidade = ?", new Object[] { usuarioUnidade.getUsuario().getId(), usuarioUnidade.getUnidade().getId() });
		return count > 0;
	}

	public void sincronizarUsuarioUnidade(ArrayList<UsuarioUnidade> usuariosUnidades, UsuarioGeral usrLogado) {
		for (UsuarioUnidade u: usuariosUnidades){
			u.setUsuarioCadastro(usrLogado);
			u.setDataCadastro(new java.util.Date());
			sincronizarUsuarioUnidade(u);
		}
	}

	public void removerUsuarioUnidade(ArrayList<UsuarioUnidade> usuariosUnidades) {
		for (UsuarioUnidade u: usuariosUnidades){
			removerUsuarioUnidade(u);
		}
	}

	public static SincronizaCadastroUsuariosUnidades usandoSistema(int sistema) {
		
		 DataSource ds = null;
		 
		 if (Sistema.isSistemaAtivo(sistema)) {
			 ds = Database.getInstance().getDataSource(sistema);
		 }
		 
		 return usandoDataSource(ds);
	 }
	
	public static SincronizaCadastroUsuariosUnidades usandoDataSource(DataSource ds) {
		if (ds == null) {
			return new SincronizaCadastroUsuariosUnidades(ds) {
				public void removerUsuarioUnidade(ArrayList<UsuarioUnidade> usuariosUnidades) { }
				public void sincronizarUsuarioUnidade(ArrayList<UsuarioUnidade> usuariosUnidades, UsuarioGeral usrLogado) { }
				public void sincronizarUsuarioUnidade(UsuarioUnidade usuarioUnidade) { }
				public void removerUsuarioUnidade(UsuarioUnidade usuarioUnidade) { }
			};
		} else {
			return new SincronizaCadastroUsuariosUnidades(ds);
		}
	}


}