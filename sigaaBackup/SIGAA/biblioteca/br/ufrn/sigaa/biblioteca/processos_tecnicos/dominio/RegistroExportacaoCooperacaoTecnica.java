/*
 * RegistroExportacaoCooperacaoTecnica.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
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
 *   Classe que regista as exportações de títulos e autoridade do setor de cooperação técnica
 * para gerar relatórios.
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
	 * Se o que foi exportado foi um título 
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
	 * A data da exportação
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_exportacao", nullable=false)
	private java.util.Date dataExportacao;
	
	
	/**
	 *  Guarda o a biblioteca de quem exportou o título. Como um título não está em uma 
	 * biblioteca específica é preciso guardar de onde o título foi exportado  
	 *
	 */
	@OneToOne
	@JoinColumn(name="id_biblioteca", referencedColumnName="id_biblioteca")
	private Biblioteca bibloteca;
	
	
	
	/** Guarda o número de controle da FGV utilizado na geração do arquivo, caso ocorra algum erro 
	 * quando a FGV tentar ler, temos como saber qual exportação foi, porque o número de controle não se repete, 
	 * é sequênical.*/
	@Column(name="numero_controle_fgv", nullable=true)
	private Integer numeroControleFGV;
	
	
	//////////////////////////// INFORMAÇÕES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * Guarda usuário que fez a exportação
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;
	
	
	
	public RegistroExportacaoCooperacaoTecnica(){
		
	}
	
	
	/**
	 * Construtor para registro de exportação de títulos 
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
	 * Construtor para registro de exportação de títulos para a FGV
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
	 * Construtor para o registro de exportação de autoridades
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
	 * Construtor para registro de exportação de autoridades para a FGV
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
