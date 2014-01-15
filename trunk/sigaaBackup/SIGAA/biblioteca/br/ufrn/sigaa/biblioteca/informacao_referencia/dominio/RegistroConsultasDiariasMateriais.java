/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 31/10/08
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**   
 *   <p>Registrado feito pelos bibliotecários com os livros que são consultados nas estantes da biblioteca.<br/> </p>
 *   
 *   <p> <strong>Observação: Este registro guarda as quantidades dos materiais consultados
 *   , ou seja, serve para fazer consultas QUANTITATIVAS. </strong>
 *   </p>
 *   <p>Existe outro registro <code>br.ufrn.sigaa.biblioteca.circulacao.dominio.RegistroConsultaMaterialInformacional</code> que guarda as consultas 
 *   realizadas descritivamente, com os materiais que foram consultados.</strong>
 *   </p>
 *   
 * @see br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroConsultaMaterialInformacional 
 */

@Entity
@Table(name="registro_consultas_diarias_materiais", schema = "biblioteca")
public class RegistroConsultasDiariasMateriais implements Validatable {
	
	
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
	@Column (name="id_registro_consultas_diarias_materiais")
	private int id;
	
	
	/** O tipo dos materiais consultados. Livros, CD, etc.... */
	@ManyToOne
	@JoinColumn (name="id_tipo_material")
	private TipoMaterial tipoMaterial = new TipoMaterial();
	
	/**  A coleção dos materiais consultados  */
	@ManyToOne
	@JoinColumn (name="id_colecao")
	private Colecao colecao = new Colecao();
	
	/**  A biblioteca para qual feito o registro da consulta de material */
	@ManyToOne
	@JoinColumn (name="id_biblioteca")
	private Biblioteca biblioteca = new Biblioteca();
	
	/**  O turno que em foi feito o registro da consulta de material */
	private int turno = TURNO_MATUTINO;
	
	/** A data em que a consulta foi realizada, não necessáriamente é a data que ele foi registrada no sistema. */
	@Temporal(TemporalType.DATE)
	@Column(name="data_consulta")
	private Date dataConsulta;
	
	/** Cada registro contém um lista de classes CDU/Black e quantidades registradas. */
	@OneToMany(mappedBy="registroConsultaMaterial", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<ClasseMaterialConsultado> classesConsultadas = new ArrayList<ClasseMaterialConsultado>();
	

	/**
	 * Construtor vazio.
	 */
	public RegistroConsultasDiariasMateriais() {
		super();
	}
	
	
	/**
	 * Construtor padrão.
	 */
	public RegistroConsultasDiariasMateriais(TipoMaterial tipoMaterial,
			Colecao colecao, Biblioteca biblioteca, int turno, Date data) {
		super();
		this.dataConsulta = CalendarUtils.descartarHoras(data);
		this.tipoMaterial = tipoMaterial;
		this.colecao = colecao;
		this.biblioteca = biblioteca;
		this.turno = turno;
	}

	
	
	
	/////////////////////  Informações de  Auditoria  ////////////////////////// 
	
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_criacao")
	private Date dataCriacao;
	
	@CriadoPor
	@ManyToOne
	@JoinColumn (name="id_registro_entrada_criacao")
	private RegistroEntrada registroEntradaCriacao;

	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="atualizado_em")
	private Date atualizadoEm;
	
	@AtualizadoEm
	@ManyToOne
	@JoinColumn (name="id_registro_entrada_atualizacao")
	private RegistroEntrada registroEntradaAtualizacao;
	
	//////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	/**
	 * Uma classe de registro de consultas é igual a outra se tiver os mesmos dados.
	 */
	@Override
	public boolean equals(Object obj){
		return EqualsUtil.testEquals(this, obj, "tipoMaterial.id", "biblioteca.id", "colecao.id", "turno", "dataConsulta");
	}
	
	/**
	 * Uma classe de registro de consultas é igual a outra se tiver os mesmos dados.
	 */
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(tipoMaterial.getId(), biblioteca.getId(), colecao.getId(), turno, dataConsulta);
	}
	
	
	
	
	
	/*
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
		
		if(colecao == null || colecao.getId() <= 0)
			erros.addErro("Selecione a coleção do material consultado");
		
		if(tipoMaterial == null || tipoMaterial.getId() <= 0)
			erros.addErro("Selecione o tipo do material consultado");
		
		if(biblioteca == null || biblioteca.getId() <= 0)
			erros.addErro("Selecione a Biblioteca do material consultado");
		
		
		if(turno != RegistroConsultasDiariasMateriais.TURNO_MATUTINO 
				&& turno != RegistroConsultasDiariasMateriais.TURNO_VERPERTINO
				&& turno != RegistroConsultasDiariasMateriais.TURNO_NOTURNO)
			erros.addErro("Turno informado inválido");
		
		return erros;
		
	}
	
	
	
	// sets e gets
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroEntradaCriacao() {
		return registroEntradaCriacao;
	}

	public TipoMaterial getTipoMaterial() {
		return tipoMaterial;
	}


	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	public RegistroEntrada getRegistroEntradaAtualizacao() {
		return registroEntradaAtualizacao;
	}

	public int getTurno() {
		return turno;
	}

	public Colecao getColecao() {
		return colecao;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public List<ClasseMaterialConsultado> getClassesConsultadas() {
		return classesConsultadas;
	}

	public void setClassesConsultadas(List<ClasseMaterialConsultado> classesConsultadas) {
		this.classesConsultadas = classesConsultadas;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}


	public void setTipoMaterial(TipoMaterial tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}


	public void setColecao(Colecao colecao) {
		this.colecao = colecao;
	}


	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}


	public void setTurno(int turno) {
		this.turno = turno;
	}


	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}


	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}


	public void setRegistroEntradaCriacao(RegistroEntrada registroEntradaCriacao) {
		this.registroEntradaCriacao = registroEntradaCriacao;
	}


	public void setAtualizadoEm(Date atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}


	public void setRegistroEntradaAtualizacao(RegistroEntrada registroEntradaAtualizacao) {
		this.registroEntradaAtualizacao = registroEntradaAtualizacao;
	}
	
}
