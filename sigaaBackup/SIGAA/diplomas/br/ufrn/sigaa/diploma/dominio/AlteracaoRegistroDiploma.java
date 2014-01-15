/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 23/02/2012
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Registra as alterações em um registro de diplomas.
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(schema = "diploma", name = "alteracao_registro_diploma")
public class AlteracaoRegistroDiploma implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_alteracao_registro_diploma")
	private int id;
	
	/** Registro de diploma que foi alterado. */
	@OneToOne
	@JoinColumn(name = "id_registro_diploma")
	private RegistroDiploma registroDiploma;
	
	/** Número do protocolo do diploma. */
	private String processo;
	
	/** Data de registro do diploma. */
	@Column(name = "data_registro")
	private Date dataRegistro;
	
	/** Data de expedição do diploma. */
	@Column(name = "data_expedicao")
	private Date dataExpedicao;
	
	/** Data de colação. */
	@Column(name = "data_colacao")
	private Date dataColacao;
	
	/** Registro de Entrada do usuário responsável pelo registro. */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data/hora de criação do registro de diploma. */
	@CriadoEm
	@Column(name = "criado_em")
	private Date criadoEm;
	
	/**
	 * Construtor padrão.
	 */
	public AlteracaoRegistroDiploma() {
	}

	/**
	 * Cria um registro da altereção de dados dos diplomas baseado nas
	 * diferenças entre o registro anterior e o novo registro.
	 * 
	 * @param registroDiplomaAnterior
	 * @param registroDiplomaAtual
	 */
	public AlteracaoRegistroDiploma(RegistroDiploma registroDiplomaAnterior, RegistroDiploma registroDiplomaAtual) {
		this();
		this.registroDiploma = registroDiplomaAnterior;
		if (registroDiplomaAnterior.getDataColacao() != registroDiplomaAtual.getDataColacao())
			this.dataColacao = registroDiplomaAnterior.getDataColacao();
		if (registroDiplomaAnterior.getDataExpedicao() != registroDiplomaAtual.getDataExpedicao())
			this.dataExpedicao = registroDiplomaAnterior.getDataExpedicao();
		if (registroDiplomaAnterior.getDataRegistro() != registroDiplomaAtual.getDataRegistro())
			this.dataRegistro = registroDiplomaAnterior.getDataRegistro();
		if (registroDiplomaAnterior.getProcesso() != registroDiplomaAtual.getProcesso())
			this.processo = registroDiplomaAnterior.getProcesso();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroDiploma getRegistroDiploma() {
		return registroDiploma;
	}

	public void setRegistroDiploma(RegistroDiploma registroDiploma) {
		this.registroDiploma = registroDiploma;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public Date getDataRegistro() {
		return dataRegistro;
	}

	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	public Date getDataExpedicao() {
		return dataExpedicao;
	}

	public void setDataExpedicao(Date dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	public Date getDataColacao() {
		return dataColacao;
	}

	public void setDataColacao(Date dataColacao) {
		this.dataColacao = dataColacao;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/** Retorna o ID do registro.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ID = " + id;
	}
}
