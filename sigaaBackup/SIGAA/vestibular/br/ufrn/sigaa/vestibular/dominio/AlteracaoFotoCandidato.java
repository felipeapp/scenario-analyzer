package br.ufrn.sigaa.vestibular.dominio;

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
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Dados Pessoais do Candidato ao Vestibular.
 * 
 * @author Jean Guerethes F. Guedes
 */
@Entity
@Table(name = "alteracao_foto_candidato", schema = "vestibular", uniqueConstraints = {})
public class AlteracaoFotoCandidato implements PersistDB {

	/** Chave primária da alteração da foto do candidato. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Usuário do Vestibular que sofreu a mudança. */
	@Column(name = "id_pessoa_vestibular")
	private Integer pessoaVestibular;
	
	/** ID do arquivo da foto 3x4 do candidto. */
	@Column(name = "id_nova_foto")
	private Integer novoIdFoto;
	
	/** ID do arquivo da foto 3x4 do candidto. */
	@Column(name = "id_antiga_foto")
	private Integer idFoto;

	/** Data da alteração da foto do candidato. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Usuário que cadastrou o projeto */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;
	
	public Integer getPessoaVestibular() {
		return pessoaVestibular;
	}

	public void setPessoaVestibular(Integer pessoaVestibular) {
		this.pessoaVestibular = pessoaVestibular;
	}

	public Integer getNovoIdFoto() {
		return novoIdFoto;
	}

	public void setNovoIdFoto(Integer novoIdFoto) {
		this.novoIdFoto = novoIdFoto;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

}