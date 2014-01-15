package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Registra a geração de um diário de classe após
 * uma consolidação de turma.
 */

@Entity
@Table(name="diario_classe", schema="ava")
public class DiarioClasse implements PersistDB{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="USER_SEQUENCE_GENERATOR")
	@SequenceGenerator(name="USER_SEQUENCE_GENERATOR", sequenceName="ensino.hibernate_sequence", initialValue=1, allocationSize=1)
	@Column(name="id_diario_classe")
	private int id;
	
	/** O código que identifica o diário. */
	@Column(name="codigo_hash")
	private String codigoHash;
	
	/** O id do arquivo salvo com o pdf gerado. */
	@Column(name="id_arquivo")
	private int idArquivo;
	
	/** A turma do diário. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_turma")
	private Turma turma;
	
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;
	
	@CriadoPor
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada_criacao")
	private RegistroEntrada registroEntradaCriacao;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigoHash() {
		return codigoHash;
	}

	public void setCodigoHash(String codigoHash) {
		this.codigoHash = codigoHash;
	}

	public int getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(int idArquivo) {
		this.idArquivo = idArquivo;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroEntradaCriacao() {
		return registroEntradaCriacao;
	}

	public void setRegistroEntradaCriacao(RegistroEntrada registroEntradaCriacao) {
		this.registroEntradaCriacao = registroEntradaCriacao;
	}
}
