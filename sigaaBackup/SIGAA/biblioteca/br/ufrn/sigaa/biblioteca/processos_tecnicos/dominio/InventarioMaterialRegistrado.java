/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 07/03/2012
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * <p>Classe que representa um registro de um material em um invent�rio.</p>
 * 
 * @author Felipe
 *
 */
@Entity
@Table(name = "inventario_material_registrado", schema = "biblioteca")
public class InventarioMaterialRegistrado implements Validatable {
	
	/**
	 * Id do registro.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.inventario_acervo_sequence") })
	@Column(name="id_inventario_material_registrado")
	private int id;
	
	/**
	 * Data em que o material foi registrado.
	 */
	@Column(name = "data_registro", nullable=true)
	private Date dataRegistro;
	
	/**
	 * Usu�rio que registrou o material.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuarioRegistro;
	
	/**
	 * Indica se o registrado est� ativo ou n�o (removido).
	 */
	@Column(name = "ativo", nullable=true)
	private boolean ativo;
	
	/**
	 * Invent�rio ao qual pertence o registro.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_inventario_acervo_biblioteca", nullable = false)
	private InventarioAcervoBiblioteca inventario;
	
	/**
	 * Material registrado.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_material_informacional", nullable = false)
	private MaterialInformacional material;
	
	public InventarioMaterialRegistrado() {
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventarioMaterialRegistrado other = (InventarioMaterialRegistrado) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		
		if (dataRegistro == null) {
			mensagens.addErro("O campo 'Data de registro' � obrigat�rio.");
		}
		
		if (usuarioRegistro == null) {
			mensagens.addErro("O campo 'Usu�rio de registro' � obrigat�rio.");
		}
		
		if (inventario == null) {
			mensagens.addErro("O campo 'Invent�rio' � obrigat�rio.");
		}
		
		if (material == null) {
			mensagens.addErro("O campo 'Material' � obrigat�rio.");
		}
		
		return mensagens;
	}
	
	// Gets e sets

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public Usuario getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(Usuario usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public InventarioAcervoBiblioteca getInventario() {
		return inventario;
	}

	public void setInventario(InventarioAcervoBiblioteca inventario) {
		this.inventario = inventario;
	}

	public MaterialInformacional getMaterial() {
		return material;
	}

	public void setMaterial(MaterialInformacional material) {
		this.material = material;
	}
}
