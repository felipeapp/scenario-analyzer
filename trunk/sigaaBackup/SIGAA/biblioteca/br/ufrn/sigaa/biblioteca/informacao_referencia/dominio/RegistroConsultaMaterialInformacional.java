/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 30/04/2009
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *   <p>Classe que registra as consultas a materiais informacionais nas estantes da biblioteca.<br/>
 *   <p>Este registro é feito pelos bibliotecários quando o usuário consulta um material na biblioteca 
 *   e deixa esse material sobre a mesa. Antes de devolve-lo à estante, o bibliotecário deve registrar 
 *   no sistema a sua consulta</p>.
 *   
 *   <p> <strong>Observação: Este registro guarda um referência para o material que foi registrado
 *   , ou seja, serve para fazer consultas QUALITATIVAS. </strong>
 *   </p>
 *   
 * @author Fred_Castro
 * @since 30/04/2009
 * @version 1.0
 *
 */

@Entity
@Table(name="registro_consulta_material_informacional", schema="biblioteca")
public class RegistroConsultaMaterialInformacional implements Validatable {

	/** Indica que o registro da consulta de material foi feita para o turno matutino */
	public static final int TURNO_MATUTINO = 1;
	
	/** Indica que o registro da consulta de material foi feita para o turno vespertino */
	public static final int TURNO_VERPERTINO = 2;
	
	/** Indica que o registro da consulta de material foi feita para o turno noturno */
	public static final int TURNO_NOTURNO = 3;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name = "id_registro_consulta_material_informacional", nullable = false)
	private int id;
	
	/** O Turno no qual a consulta foi realizada */
	@Column(name = "turno", nullable = false)
	private int turno;

	/** O Material consultado */
	@ManyToOne
	@JoinColumn(name = "id_material_informacional", referencedColumnName = "id_material_informacional")
	private MaterialInformacional materialInformacional;

	/** A data que a consulta foi realizada */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_consulta", nullable = false)
	private Date dataConsulta;
	
	/**
	 * RegistroEntrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	
	
	
	/**
	 * Construtor vazio para hibernate e JSF
	 */
	public RegistroConsultaMaterialInformacional() {
		super();
	}


	/**
	 * Construtor que cria um novo registro de consulta de materiais.
	 * 
	 * @param turno
	 * @param materialInformacional
	 * @param data
	 */
	public RegistroConsultaMaterialInformacional(int turno,
			MaterialInformacional materialInformacional, Date data) {
		super();
		this.turno = turno;
		this.materialInformacional = materialInformacional;
		this.dataConsulta = data;
	}


	
	
	/**
	 * Verifica se os dados requeridos pelo Registro não são nulos
	 */
	@Override
	public ListaMensagens validate (){

		ListaMensagens mensagens = new ListaMensagens();

		if (materialInformacional == null || materialInformacional.getId() <= 0)
			mensagens.addErro("O Material deve ser informado.");

		if (dataConsulta == null)
			mensagens.addErro("A data deve ser informada.");
		
		if ( turno <= 0)
			mensagens.addErro("O turno deve ser informado.");
		
		return mensagens;
	}

	
	////////////////////////  sets e gets //////////////////////
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date data) {
		this.dataConsulta = data;
	}
	
	public MaterialInformacional getMaterialInformacional() {
		return materialInformacional;
	}

	public void setMaterialInformacional(MaterialInformacional materialInformacional) {
		this.materialInformacional = materialInformacional;
	}

	public int getTurno() {
		return turno;
	}

	public void setTurno(int turno) {
		this.turno = turno;
	}
	
	public RegistroEntrada getRegistroCadastro(){
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj);
	}
}