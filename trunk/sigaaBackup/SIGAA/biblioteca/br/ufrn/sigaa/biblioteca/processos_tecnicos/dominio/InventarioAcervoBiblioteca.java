/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Classe que representa um inventário para o acervo de uma biblioteca.</p>
 * 
 * <p>Inventário guarda informações de materiais registrados que depois vão ser conferidos com o 
 * que está no sistema para ver se o que está oficialmente na biblioteca, condiz com o que está nas 
 * estantes das bibliotecas.</p>
 * 
 * @author Felipe
 *
 */
@Entity
@Table(name = "inventario_acervo_biblioteca", schema = "biblioteca")
public class InventarioAcervoBiblioteca implements Validatable {
	
	/**
	 * Id do inventário.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.inventario_acervo_sequence") })
	@Column(name="id_inventario_acervo_biblioteca")
	private int id;
	
	/**
	 * Biblioteca a qual percente o inventário. Apenas materiais dessa biblioteca podem ser registrados e serão recuperados.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", nullable = false)
	private Biblioteca biblioteca;
	
	/**
	 * <p>Caso o inventário possua coleção apenas materiais dessa coleção podem ser registrados e serão recuperados.</p>
	 * 
	 * <p>Caso o inventário não possua coleção, será considerado um inventário geral e todos os materiais da biblioteca 
	 * do inventário poderão ser registrados.</p>
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_colecao", nullable = true)
	private Colecao colecao;
	
	
	/**
	 * Descrição do inventário.
	 */
	@Column(name = "descricao", nullable=true)
	private String descricao;
	
	/**
	 * Ano de validade do inventário.
	 */
	@Column(name = "ano", nullable=true)
	private Integer ano;
	
	/**
	 * Indica se o inventário está aberto (aceita novos registros) ou já foi concluído.
	 */
	@Column(name = "aberto", nullable=true)
	private boolean aberto;
	
	/**
	 * Indica a data em que o inventário foi fechado (concluído).
	 *  Materiais não podem mais ser registrados se o inventário estiver fechado.
	 */
	@Column(name = "data_fechamento", nullable=true)
	private Date dataFechamento;
	
	/**
	 * Lista dos materiais registrados no inventário.
	 */
	@OneToMany(mappedBy="inventario", cascade = { CascadeType.ALL }, fetch= FetchType.LAZY)
	private List<InventarioMaterialRegistrado> inventarioMaterialRegistradoList = new ArrayList<InventarioMaterialRegistrado>();
	
	
	/** Guarda temporariamente a quantidade de materiais registrados para o acervo.*/
	@Transient
	private long quantidadeMateriaisRegistrados;
	
	
	////////////////////////////INFORMAÇÕES DE AUDITORIA  ///////////////////////////////////////


	/**
	 * Informações de quem criou
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
	 * Registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	@AtualizadoEm
	private Date dataUltimaAtualizacao;


	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * construtor padrão
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
		return "Inventário ["+id+"] "+"descriçãoo: "+descricao+"ANO: "+ano
		+"("+biblioteca.getDescricao()+" "+(colecao != null ? colecao.getDescricao(): "")+")"; 
	}

	
	@Override
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();
		
		if (biblioteca == null) {
			mensagens.addErro("O campo 'Biblioteca' é obrigatório.");
		}
		
		if (StringUtils.isEmpty(descricao)) {
			mensagens.addErro("O campo 'Descricao' é obrigatório.");
		}
		
		if (ano == null) {
			mensagens.addErro("O campo 'Ano' é obrigatório.");
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
