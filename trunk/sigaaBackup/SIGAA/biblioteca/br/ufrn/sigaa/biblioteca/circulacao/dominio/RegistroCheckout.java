package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 *  <p>Guarda os registros de check out feitos no sistema de bibliotecas.</p>
 *
 *  <p>Checkout são as verificações que são feitas nas saídas das bibliotecas para verificar se 
 *     o material que o usuário está tirando da biblioteca realmente foi emprestado. Ou o usuário esqueceu que tinha que empresta-lo.
 *  </p>
 *
 * @author jadson
 * @since 27/11/2009
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table (name="registro_checkout", schema="biblioteca")
public class RegistroCheckout implements PersistDB{

	/** Situações que podem ocorrer na saída do material da biblioteca*/
	public static final int MATERIAL_EMPRESTADO = 1;
	public static final int MATERIAL_NAO_EMPRESTADO = 2;
	public static final int MATERIAL_EMPRESTADO_MAS_ATRASADO = 3;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.registro_checkout_sequence") })
	@Column(name = "id_registro_checkout", nullable = false)
	private int id;

	/** 
	 *  Usuário que fez o checkout. Ou seja o operador do sistema que fica na saída da biblioteca.
	 *  Não é o usuário que estava saindo com o material. 
	 */
	@ManyToOne
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	/** O material em quem foi realizado o checkout */
	@Column(name = "id_material_informacional", nullable = false)
	private Integer idMaterialInformacional;
	
	/** Quando foi feito o checkout . */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	/** O resultado do checkout. Se o material realmente estava emprestado, se não estava 
	 * emprestado e não poderia sair da biblioteca ou se estava emprestado mais já estava atrasado, 
	 * neste último caso depende da política da biblioteca se deixar o usuário sair com o material ou não. */
	private int resultado;
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdMaterialInformacional() {
		return idMaterialInformacional;
	}
	
	public void setIdMaterialInformacional(Integer idMaterialInformacional) {
		this.idMaterialInformacional = idMaterialInformacional;
	}
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public int getResultado() {
		return resultado;
	}
	
	public void setResultado(int resultado) {
		this.resultado = resultado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}