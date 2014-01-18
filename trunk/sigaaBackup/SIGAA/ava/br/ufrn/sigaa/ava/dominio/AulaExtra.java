/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 18/01/2008
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Registro de aulas que não estão previstas no calendário
 * da turma.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="aula_extra", schema="ava") @HumanName(value="Aula Extra", genero='F')
public class AulaExtra implements DominioTurmaVirtual {

	/** Constantes referentes ao tipo da aula extra. */
	/** Caso a aula extra seja uma aula de reposição. */
	public static final int REPOSICAO = 1;
	/** Caso a aula extra seja uma aula adicional. */
	public static final int ADICIONAL = 2;
	/** Caso a aula extra seja uma aula de ensino individual. */
	public static final int ENSINO_INDIVIDUAL = 3;
	
	/**Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id", nullable = false)
	private int id;
	
	/**
	 * A turma que possui esta aula extra.
	 */
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name="id_turma")
	private Turma turma;
	
	/** Número de aulas de 50 minutos que a aula extra terá */
	@Column(name="numero_aulas")
	private int numeroAulas;
	
	/** Tipo de Aula (1 - Reposicao, 2 - Adicional, 3 - Ensino Individual) */
	private Integer tipo;
	
	/**
	 * Título da aula extra.
	 */
	private String descricao;
	
	/**
	 * Observações digitadas pelo professor sobre esta aula extra.
	 */
	private String observacoes;
	
	/**
	 * O dia em que esta aula extra ocorrerá.
	 */
	@Column(name="data_aula")
	private Date dataAula;
	
	/**
	 * A dia da aula que a aula extra está repondo.
	 */
	@Column(name="aula_reposta")
	private Date dataAulaReposta;
	
	/**
	 * Data em que a aula foi criada.
	 */	
	@Column(name="criado_em")
	private Date criadoEm;
	
	/**
	 * Usuáro que criou a aula.
	 */	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name="criado_por")
	private Usuario criadoPor;
	
	/** Identifica se a aula está ativa no sistema. */
	@CampoAtivo
	private boolean ativo = true;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Date getDataAula() {
		return dataAula;
	}

	public void setDataAula(Date dataAula) {
		this.dataAula = dataAula;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Usuario getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(Usuario criadoPor) {
		this.criadoPor = criadoPor;
	}

	public String getMensagemAtividade() {
		return "Registrada uma aula extra em " + Formatador.getInstance().formatarData(dataAula);
	}

	public int getNumeroAulas() {
		return numeroAulas;
	}

	public void setNumeroAulas(int numeroAulas) {
		this.numeroAulas = numeroAulas;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	/** Retorna a descrição textual do tipo da aula extra tratando o caso da aula não possuir tipo.
	 *  Ex: Adicional, Reposição.
	 * @return 
	 */
	public String getTipoDesc() {
		if (tipo == null)
			return "Não Definido";
		else
			return nomeTipo(tipo); 
	}
	
	/** Retorna a descrição textual do tipo da aula extra. Ex: Adicional, Reposição.
	 * @return 
	 */
	public static String nomeTipo(int tipo) {
		return tipo == REPOSICAO ? "Reposição" : tipo == ADICIONAL ? "Adicional" : "Ensino Individual"; 
	}
	
	@Override
	public boolean equals (Object o){
		return EqualsUtil.testEquals(this, o, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDataAulaReposta(Date dataAulaReposta) {
		this.dataAulaReposta = dataAulaReposta;
	}

	public Date getDataAulaReposta() {
		return dataAulaReposta;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isAtivo() {
		return ativo;
	}
	
}