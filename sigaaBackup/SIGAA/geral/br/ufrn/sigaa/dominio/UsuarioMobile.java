/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '20/06/2008'
 *
 */
package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

@Entity 
@Table(name="usuario_mobile", schema="mobile")
public class UsuarioMobile implements PersistDB {

		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		@Column(name="id_usuario_mobile")
		private int id;
		
		@OneToOne
		@JoinColumn(name="id_usuario")
		private Usuario usuario;
		
		@Column(name="senha_mobile")
		private String senhaMobile;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public Usuario getUsuario() {
			return usuario;
		}
		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}
		public String getSenhaMobile() {
			return senhaMobile;
		}
		public void setSenhaMobile(String senhaMobile) {
			this.senhaMobile = senhaMobile;
		}


}
