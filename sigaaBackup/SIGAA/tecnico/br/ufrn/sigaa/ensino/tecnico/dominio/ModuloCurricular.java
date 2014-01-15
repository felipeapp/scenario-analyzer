package br.ufrn.sigaa.ensino.tecnico.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Relacionamento entre MODULO e ESTRUTURA CURRICULAR.
 * DisciplinaComplementar generated by hbm2java
 */
@Entity
@Table(name = "modulo_curricular", schema = "tecnico", uniqueConstraints = {})
public class ModuloCurricular implements PersistDB {

	/** Chave prim�ria. */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_modulo_curricular", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Indica o M�dulo associado. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_modulo", unique = false, nullable = true, insertable = true, updatable = true)
	private Modulo modulo;

	/** Indica a Estrutura Curricular associada. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_estrutura_curricular", unique = false, nullable = true, insertable = true, updatable = true)
	private EstruturaCurricularTecnica estruturaCurricularTecnica;

	/** Indica o per�odo em que o M�dulo � ofertado na Estrutura Curricular. */
	@Column(name = "periodo_oferta", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer periodoOferta;
	
	/** Data de cadastro do M�dulo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usu�rio que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data da �ltima atualiza��o do M�dulo. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Registro entrada do usu�rio que realizou a �ltima atualiza��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	// Constructors

	/** default constructor */
	public ModuloCurricular() {
	}

	/** default minimal constructor */
	public ModuloCurricular(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public ModuloCurricular(int idDisciplinaComplementar,
			ComponenteCurricular disciplina) {
		this.id = idDisciplinaComplementar;
	}

	/** full constructor */
	public ModuloCurricular(int idDisciplinaComplementar, Modulo modulo,
			ComponenteCurricular disciplina,
			EstruturaCurricularTecnica estruturaCurricularTecnica,
			boolean obrigatoria, Integer periodoOferta) {
		this.id = idDisciplinaComplementar;
		this.modulo = modulo;
		this.estruturaCurricularTecnica = estruturaCurricularTecnica;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Modulo getModulo() {
		return this.modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public EstruturaCurricularTecnica getEstruturaCurricularTecnica() {
		return this.estruturaCurricularTecnica;
	}

	public void setEstruturaCurricularTecnica(EstruturaCurricularTecnica estruturaCurricularTecnica) {
		this.estruturaCurricularTecnica = estruturaCurricularTecnica;
	}

	public Integer getPeriodoOferta() {
		return periodoOferta;
	}

	public void setPeriodoOferta(Integer periodoOferta) {
		this.periodoOferta = periodoOferta;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "modulo", "estruturaCurricularTecnica");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll( id, modulo, estruturaCurricularTecnica);
	}

}
