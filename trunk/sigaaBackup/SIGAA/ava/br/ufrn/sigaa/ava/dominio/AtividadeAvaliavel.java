/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 30/07/2010
 *
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Atividade Avaliável representa as atividades que podem receber notas.
 * 
 * @author Diego Jácome
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="atividade_avaliavel", schema="ava")
public abstract class AtividadeAvaliavel extends AbstractMaterialTurma{

	/** Atividade e do tipo tarefa. */
	public static final int TIPO_TAREFA = 1;
	
	/** Atividade e do tipo questionário. */
	public static final int TIPO_QUESTIONARIO = 2;
	
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_atividade_avaliavel")
	protected int id;
	
	/** Título da atividade. */
	protected String titulo;
	
	/** Abreviação. */
	protected String abreviacao;
	
	/** Unidade. */
	protected int unidade;
	
	/** Nota máxima. */
	@Column(name = "nota_maxima")
	protected Double notaMaxima;
	
	/** Peso da atividade no cálculo da média da unidade */
	protected int peso = 1;

	
	/** Indica se a tarefa possui nota. */
	@Column(name = "possui_nota")
	protected boolean possuiNota;
	
	/** Referencia o tópico de aula ao qual este questionário está associado */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_topico_aula", nullable = true )
	private TopicoAula aula = new TopicoAula ();
	
	/** Material Turma. */
	@ManyToOne(fetch=FetchType.LAZY, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	protected MaterialTurma material = new MaterialTurma(TipoMaterialTurma.TAREFA);
	
	/** Indica se o tipo da Atividade. */
	@Column(name = "tipo_atividade")
	protected int tipoAtividade;
	
	//////////////////////////// informações auditoria //////////////////////////////
	
	/**
	 * Registro de entrada do usuário que cadastrou
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

	public int getTipoAtividade() {
		return tipoAtividade;
	}

	public void setTipoAtividade(int tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}

	/**
	 * Registro entrada do usuário que realizou a última atualização
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
	
	/**
	 * Ao remover as atividades as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	
	private boolean ativo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAbreviacao() {
		return abreviacao;
	}

	public void setAbreviacao(String abreviacao) {
		this.abreviacao = abreviacao;
	}

	public boolean isPossuiNota() {
		return possuiNota;
	}

	public void setPossuiNota(boolean possuiNota) {
		this.possuiNota = possuiNota;
	}

	public int getUnidade() {
		return unidade;
	}

	public void setUnidade(int unidade) {
		this.unidade = unidade;
	}

	public Double getNotaMaxima() {
		return notaMaxima;
	}

	public void setNotaMaxima(Double notaMaxima) {
		this.notaMaxima = notaMaxima;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}

	public TopicoAula getAula() {
		return aula;
	}

	public void setAula(TopicoAula aula) {
		this.aula = aula;
	}
	
	
	public MaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(MaterialTurma material) {
		this.material = material;
	}
	
	public boolean isTarefa () {
		return this instanceof TarefaTurma;
	}
	
	public boolean isQuestionario () {
		return this instanceof QuestionarioTurma;
	}
	
	/**
	 * Identifica o tipo de atividade
	 *	<ul>
	 * 		<li>TAREFA = 1;</li>
	 * 		<li>QUESTIONÁRIO = 2;</li>
	 *	</ul> 
	 */
	public abstract int getAtividade ();

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isAtivo() {
		return ativo;
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

	@Override
	public Usuario getUsuarioCadastro() {
		Usuario u = null;
		
		if (getRegistroCadastro() != null)
			u = (Usuario) getRegistroCadastro().getUsuario();
		
		return u;
	}
}