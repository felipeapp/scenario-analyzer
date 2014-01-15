/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 22/05/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.pessoa.dominio;

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
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Objeto respons�vel por armazenar o mes�rio volunt�rio, que ser� usado para captar 
 * aqueles alunos que queiram participar das elei��es como mes�rio.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "mesario_voluntario", schema = "comum")
public class MesarioVoluntario implements Validatable{


	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_mesario_voluntario", nullable = false)
	private int id;

	/** Discente requerente da inscri��o para mes�rio volunt�rio.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_inscricao")
	private Discente discenteInscricao = new Discente();

	/** N�mero do T�tulo de Eleitor do discente.*/
	@Column(name = "titulo_eleitor")
	private String tituloEleitor;
	
	/** N�mero da zona eleitoral do discente.*/
	@Column(name = "zona")
	private String zona;
	
	/** N�mero da se��o eleitoral do discente.*/
	@Column(name = "secao")
	private String secao;
	
	/** N�mero da se��o eleitoral do discente.*/
	@Column(name = "ano")
	private Integer ano;

	/** registro entrada do usu�rio que cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** data que foi cadastrado */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoEm
	private Date dataCadastro;
	
	/**
	 * Constructor default
	 */
	public MesarioVoluntario() {
		super();
	}

	/**
	 * Constructor minimal
	 * @param ano
	 */
	public MesarioVoluntario(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Discente getDiscenteInscricao() {
		return discenteInscricao;
	}

	public void setDiscenteInscricao(Discente discenteInscricao) {
		this.discenteInscricao = discenteInscricao;
	}

	public String getTituloEleitor() {
		return tituloEleitor;
	}

	public void setTituloEleitor(String tituloEleitor) {
		this.tituloEleitor = tituloEleitor;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public String getSecao() {
		return secao;
	}

	public void setSecao(String secao) {
		this.secao = secao;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(discenteInscricao, "Discente", erros);
		ValidatorUtil.validateRequired(tituloEleitor, "T�tulo de Eleitor", erros);
		ValidatorUtil.validateRequired(zona, "Zona Eleitoral", erros);
		ValidatorUtil.validateRequired(secao, "Se��o Eleitoral", erros);
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		return erros;
	}


	
	
	
	
}
