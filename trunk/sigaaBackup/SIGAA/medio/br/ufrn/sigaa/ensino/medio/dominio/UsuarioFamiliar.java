/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 22/09/2011
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que ir� vincular o usu�rio do familiar (pai, m�e..) ao filho do n�vel m�dio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity @Table(name="usuario_familiar", schema="medio")
public class UsuarioFamiliar implements PersistDB {
	
	/** Chave Prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_usuario_familiar", nullable = false)
	private int id;	
	
	/** Usu�rio do familiar */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_usuario", nullable = false)	
	private Usuario usuario;
	
	/** Objeto de relacionamento do discente de m�dio com a classe {@link Discente} utilizada pela implementa��o de {@link DiscenteAdapter}. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_discente_medio", nullable = false)
	private DiscenteMedio discenteMedio;	
	
	/** Data de cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

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

	public DiscenteMedio getDiscenteMedio() {
		return discenteMedio;
	}

	public void setDiscenteMedio(DiscenteMedio discenteMedio) {
		this.discenteMedio = discenteMedio;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}	
}
