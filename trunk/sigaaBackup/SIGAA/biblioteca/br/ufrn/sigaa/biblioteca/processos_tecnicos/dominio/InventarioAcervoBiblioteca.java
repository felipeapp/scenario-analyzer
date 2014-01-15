/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 07/03/2012
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 * <p>Classe que representa um invent�rio para o acervo de uma biblioteca.</p>
 * 
 * <p>Invent�rio guarda informa��es de materiais registrados que depois v�o ser conferidos com o 
 * que est� no sistema para ver se o que est� oficialmente na biblioteca, condiz com o que est� nas 
 * estantes das bibliotecas.</p>
 * 
 * @author Felipe
 *
 */
@Entity
@Table(name = "inventario_acervo_biblioteca", schema = "biblioteca")
public class InventarioAcervoBiblioteca implements Validatable {
	
	/**
	 * Id do invent�rio.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.inventario_acervo_sequence") })
	@Column(name="id_inventario_acervo_biblioteca")
	private int id;
	
	/**
	 * Biblioteca a qual percente o invent�rio. Apenas materiais dessa biblioteca podem ser registrados e ser�o recuperados.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", nullable = false)
	private Biblioteca biblioteca;
	
	/**
	 * <p>Caso o invent�rio possua cole��o apenas materiais dessa cole��o podem ser registrados e ser�o recuperados.</p>
	 * 
	 * <p>Caso o invent�rio n�o possua cole��o, ser� considerado um invent�rio geral e todos os materiais da biblioteca 
	 * do invent�rio poder�o ser registrados.</p>
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_colecao", nullable = true)
	private Colecao colecao;
	
	
	/**
	 * Descri��o do invent�rio.
	 */
	@Column(name = "descricao", nullable=true)
	private String descricao;
	
	/**
	 * Ano de validade do invent�rio.
	 */
	@Column(name = "ano", nullable=true)
	private Integer ano;
	
	/**
	 * Indica se o invent�rio est� aberto (aceita novos registros) ou j� foi conclu�do.
	 */
	@Column(name = "aberto", nullable=true)
	private boolean aberto;
	
	/**
	 * Indica a data em que o invent�rio foi fechado (conclu�do).
	 *  Materiais n�o podem mais ser registrados se o invent�rio estiver fechado.
	 */
	@Column(name = "data_fechamento", nullable=true)
	private Date dataFechamento;
	
	/**
	 * Lista dos materiais registrados no invent�rio.
	 */
	@OneToMany(mappedBy="inventario", cascade = { CascadeType.ALL }, fetch= FetchType.LAZY)
	private List<InventarioMaterialRegistrado> inventarioMaterialRegistradoList = new ArrayList<InventarioMaterialRegistrado>();
	
	
	/** Guarda temporariamente a quantidade de materiais registrados para o acervo.*/
	@Transient
	private long quantidadeMateriaisRegistrados;
	
	
	////////////////////////////INFORMA��ES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * Informa��es de quem criou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * Data de cadastro
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm
	@Column(name="data_criacao")
	private Date dataCriacao;

	/**
	 * Registro entrada do usu�rio que realizou a �ltima atualiza��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * Data da �ltima atualiza��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;


	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * construtor padr�o
	 */
	public InventarioAcervoBiblioteca() {
		
	}

	/**
	 * Construtor de um objeto persistido mas sem dados.
	 * @param id
	 */
	public InventarioAcervoBiblioteca(int id) {
		this.id = id;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventarioAcervoBiblioteca other = (InventarioAcervoBiblioteca) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	
	@Override
	public String toString() {
		return "Invent�rio ["+id+"] "+"descri��oo: "+descricao+"ANO: "+ano
		+"("+biblioteca.getDescricao()+" "+(colecao != null ? colecao.getDescricao(): "")+")"; 
	}

	
	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		
		if (biblioteca == null) {
			mensagens.addErro("O campo 'Biblioteca' � obrigat�rio.");
		}
		
		if (StringUtils.isEmpty(descricao)) {
			mensagens.addErro("O campo 'Descricao' � obrigat�rio.");
		}
		
		if (ano == null) {
			mensagens.addErro("O campo 'Ano' � obrigat�rio.");
		}
		
		return mensagens;
	}
	
	
	// Gets e sets

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getDescricaoCompleta() {
		return descricao+" - "+ano;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public boolean isAberto() {
		return aberto;
	}

	public void setAberto(boolean aberto) {
		this.aberto = aberto;
	}

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public List<InventarioMaterialRegistrado> getInventarioMaterialRegistradoList() {
		return inventarioMaterialRegistradoList;
	}

	public void setInventarioMaterialRegistradoList(List<InventarioMaterialRegistrado> inventarioMaterialRegistradoList) {
		this.inventarioMaterialRegistradoList = inventarioMaterialRegistradoList;
	}

	public Colecao getColecao() {
		return colecao;
	}

	public void setColecao(Colecao colecao) {
		this.colecao = colecao;
	}

	public long getQuantidadeMateriaisRegistrados() {
		return quantidadeMateriaisRegistrados;
	}

	public void setQuantidadeMateriaisRegistrados(long quantidadeMateriaisRegistrados) {
		this.quantidadeMateriaisRegistrados = quantidadeMateriaisRegistrados;
	}

	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(
			RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}
	
	
	
}
