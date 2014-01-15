/*
 * RegistroExportacaoCooperacaoTecnica.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 *
 *   Classe que regista as exporta��es de t�tulos e autoridade do setor de coopera��o t�cnica
 * para gerar relat�rios.
 *
 * @author jadson
 * @since 21/05/2009
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name="registro_exportacao_cooperacao_tecnica", schema="biblioteca")
public class RegistroExportacaoCooperacaoTecnica implements PersistDB{

	/**
	 * Id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_registro_exportacao_cooperacao_tecnica")
	private int id;
	
	
	/**
	 * Se o que foi exportado foi um t�tulo 
	 */
	@OneToOne
	@JoinColumn(name="id_titulo_catalografico", referencedColumnName="id_titulo_catalografico")
	private TituloCatalografico tituloCatalografico;
	
	
	/**
	 * Se o que foi exportado foi uma autoridade
	 */
	@OneToOne
	@JoinColumn(name="id_autoridade", referencedColumnName="id_autoridade")
	private Autoridade autoridade;
	
	
	/**
	 * A data da exporta��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_exportacao", nullable=false)
	private java.util.Date dataExportacao;
	
	
	/**
	 *  Guarda o a biblioteca de quem exportou o t�tulo. Como um t�tulo n�o est� em uma 
	 * biblioteca espec�fica � preciso guardar de onde o t�tulo foi exportado  
	 *
	 */
	@OneToOne
	@JoinColumn(name="id_biblioteca", referencedColumnName="id_biblioteca")
	private Biblioteca bibloteca;
	
	
	
	/** Guarda o n�mero de controle da FGV utilizado na gera��o do arquivo, caso ocorra algum erro 
	 * quando a FGV tentar ler, temos como saber qual exporta��o foi, porque o n�mero de controle n�o se repete, 
	 * � sequ�nical.*/
	@Column(name="numero_controle_fgv", nullable=true)
	private Integer numeroControleFGV;
	
	
	//////////////////////////// INFORMA��ES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * Guarda usu�rio que fez a exporta��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;
	
	
	
	public RegistroExportacaoCooperacaoTecnica(){
		
	}
	
	
	/**
	 * Construtor para registro de exporta��o de t�tulos 
	 * 
	 * @param tituloCatalografico
	 * @param dataExportacao
	 * @param bibloteca
	 */
	public RegistroExportacaoCooperacaoTecnica(TituloCatalografico tituloCatalografico, Date dataExportacao, Biblioteca bibloteca) {
		
		this.tituloCatalografico = tituloCatalografico;
		this.dataExportacao = dataExportacao;
		this.bibloteca = bibloteca;
	}

	/**
	 * Construtor para registro de exporta��o de t�tulos para a FGV
	 * 
	 * @param tituloCatalografico
	 * @param dataExportacao
	 * @param bibloteca
	 */
	public RegistroExportacaoCooperacaoTecnica(TituloCatalografico tituloCatalografico, Date dataExportacao, Biblioteca bibloteca, Integer numeroControleFGV) {
		
		this(tituloCatalografico, dataExportacao, bibloteca);
		this.numeroControleFGV = numeroControleFGV;
	}
	
	
	/**
	 * Construtor para o registro de exporta��o de autoridades
	 * 
	 * @param autoridade
	 * @param dataExportacao
	 * @param bibloteca
	 */
	public RegistroExportacaoCooperacaoTecnica(Autoridade autoridade, Date dataExportacao, Biblioteca bibloteca) {
		
		this.autoridade = autoridade;
		this.dataExportacao = dataExportacao;
		this.bibloteca = bibloteca;
	}
	
	/**
	 * Construtor para registro de exporta��o de autoridades para a FGV
	 * 
	 * @param tituloCatalografico
	 * @param dataExportacao
	 * @param bibloteca
	 */
	public RegistroExportacaoCooperacaoTecnica(Autoridade autoridade, Date dataExportacao, Biblioteca bibloteca, Integer numeroControleFGV) {
		
		this(autoridade, dataExportacao, bibloteca);
		this.numeroControleFGV = numeroControleFGV;
	}
	
	
	
	// gets
	
	public TituloCatalografico getTituloCatalografico() {
		return tituloCatalografico;
	}

	public Autoridade getAutoridade() {
		return autoridade;
	}

	public java.util.Date getDataExportacao() {
		return dataExportacao;
	}

	public Biblioteca getBibloteca() {
		return bibloteca;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getNumeroControleFGV() {
		return numeroControleFGV;
	}


	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}


	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	
	
}
