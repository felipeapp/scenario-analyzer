package br.ufrn.comum.dominio;

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
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * 
 * Esta classe serve como um log sobre as alterações que serão feitas sobre as unidades do sistema.
 * 
 * @author Mário Melo
 *
 */
@Entity
@Table(name="alteracao_unidade", schema = "comum")
public class AlteracaoUnidade implements PersistDB{
	
	
	public static final char TIPO_ALTERACAO = 'U';
	
	public static final char TIPO_REMOCAO = 'R';
	
	/** Identificador */
	@Id
	@Column(name="id_alteracao_unidade")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="comum.alteracao_unidade_seq") })
	private int id;
	
	/**
	 * A unidade que foi alterada pelo usuario
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_unidade")
	private Unidade unidade;
	
	/**
	 * Contém a descrição da unidade nova 
	 */
	@Column(name="descricao_nova")
	private String descricaoNova;
	
	/**
	 * Contém a descrição da unidade antiga  
	 */
	@Column(name="descricao_antiga")
	private String descricaoAntiga;
	
	/** Esta data contém a data que a unidade foi alterada */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_alteracao")
	@CriadoEm
	private Date dataAlteracao;
	
	/** Tipo da alteração: (R) Remoção, (U) Atualização */
	@Column(name="tipo")
	private char tipo;
	
	/** Identificador do usuário que realiza a alteração */
	@ManyToOne
	@JoinColumn(name = "id_usuario_alteracao")
	@CriadoPor
	private UsuarioGeral usuarioAlteracao;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}


	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public String getDescricaoNova() {
		return descricaoNova;
	}

	public void setDescricaoNova(String descricaoNova) {
		this.descricaoNova = descricaoNova;
	}

	public String getDescricaoAntiga() {
		return descricaoAntiga;
	}

	public void setDescricaoAntiga(String descricaoAntiga) {
		this.descricaoAntiga = descricaoAntiga;
	}

	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	public UsuarioGeral getUsuarioAlteracao() {
		return usuarioAlteracao;
	}

	public void setUsuarioAlteracao(UsuarioGeral usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}
	
}
