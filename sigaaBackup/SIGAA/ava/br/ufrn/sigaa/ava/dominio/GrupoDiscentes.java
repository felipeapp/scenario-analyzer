/* 
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Criado em: 01/09/2010
 */

package br.ufrn.sigaa.ava.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que organiza um grupo de discentes para uma turma.
 * 
 * @author Fred_Castro
 *
 */
@Entity
@Table (schema="ava", name="grupo_discentes")
public class GrupoDiscentes implements DominioTurmaVirtual {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_grupo_discentes", nullable = false)
	private int id;
	
	/** Os discentes que participam deste grupo. */
	@ManyToMany
	@JoinTable(
		name = "grupo_discentes_discente", schema = "ava",
		joinColumns = @JoinColumn (name="id_grupo_discentes"),
		inverseJoinColumns = @JoinColumn(name="id_discente")
	)
	private List <Discente> discentes = new ArrayList <Discente> ();
	
	/** A turma a qual este grupo pertence. */
	@ManyToOne()
	@JoinColumn(name="id_turma")
	private Turma turma;
	
	/** O nome do grupo. */
	private String nome;
	
	/**
	 * Grupo pai. Os grupos são organizados em estrutura de árvore.
	 */
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_grupo_pai")
	private GrupoDiscentes grupoPai;
	
	@Transient
	private GrupoDiscentes grupoFilho;
	
	private boolean ativo = true;
	
	@Transient
	private boolean alterado;
	
	@Transient
	private Integer numCriacao;
	
	//////////////////////  dados para auditoria  ////////////////////
	
	/**
	 * Registro entrada do usuário que cadastrou
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
	 * Registro de entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	//////////////////////////////////////////////////////////////////
	
	@Override
	public String getMensagemAtividade() {
		return "Grupo cadastrado";
	}

	@Override
	public Turma getTurma() {
		return turma;
	}

	@Override
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId (int id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj != null) {
			GrupoDiscentes other = (GrupoDiscentes) obj;
			if (other.getId() == this.id)
				return true;
		}

		return false;
	}
	
	public List<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<Discente> discentes) {
		this.discentes = discentes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroCadastro() {
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

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public boolean isAlterado() {
		return alterado;
	}

	public void setAlterado(boolean alterado) {
		this.alterado = alterado;
	}
	
	public GrupoDiscentes getGrupoPai() {
		return grupoPai;
	}

	public void setGrupoPai(GrupoDiscentes grupoPai) {
		this.grupoPai = grupoPai;
	}

	public GrupoDiscentes getGrupoFilho() {
		return grupoFilho;
	}

	public void setGrupoFilho(GrupoDiscentes grupoFilho) {
		this.grupoFilho = grupoFilho;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public void setNumCriacao(Integer numCriacao) {
		this.numCriacao = numCriacao;
	}

	public Integer getNumCriacao() {
		return numCriacao;
	}
}