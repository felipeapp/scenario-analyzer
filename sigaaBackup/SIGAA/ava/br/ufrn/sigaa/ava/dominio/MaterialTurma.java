/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/05/2011'
 *
 */
package br.ufrn.sigaa.ava.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe mãe de todos os tipos de material disponibilizados para uma turma.
 * 
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name="material_turma", schema="ava")
public class MaterialTurma implements DominioTurmaVirtual, Comparable<MaterialTurma> {

	/** Identificador único para o material. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
			parameters={ @Parameter(name="sequence_name", value="ava.material_turma_seq") })
	@Column(name = "id_material_turma")
	private int id;
	
	/** 
	 * Tipo de material da turma. 
	 * Ex.: Arquivo, Vídeo, Questionário, etc. 
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_material", nullable = false)
	private TipoMaterialTurma tipoMaterial;
	
	/** Ordem de exibição do material no portal da turma virtual. Permite que o material seja apresentado na sequência escolhida pelo docente. */
	@Column(name = "ordem", nullable = false)
	private Integer ordem = 1;

	/** Nível de exibição do material no portal da turma virtual. Permite que o material seja apresentado de forma identada conforme escolha do docente. */
	@Column(name = "nivel", nullable = false)
	private Integer nivel = 0;

	
	/** Turma a qual o material está vinculado. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;
	
	/** Tópico de aula ao qual o material está vinculado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_topico_aula")
	private TopicoAula topicoAula;
	
	/** Representa id do material especifico. */
	@Column(name = "id_material")	
	private Integer idMaterial;
	
	/** Indica se o material está ativo no sistema. */
	@CampoAtivo
	private boolean ativo =  true;
	

	
	/** Construtor padrão. */
	public MaterialTurma() {
	}

	public MaterialTurma(int id) {
		this.id = id;
	}

	public MaterialTurma(TipoMaterialTurma tipo) {
		tipoMaterial = tipo;
	}

	
	public boolean isTipoArquivo() {
		return tipoMaterial.equals(TipoMaterialTurma.ARQUIVO);
	}
	
	public boolean isTipoIndicacao() {
		return tipoMaterial.equals(TipoMaterialTurma.REFERENCIA);
	}
	
	public boolean isTipoTarefa() {
		return tipoMaterial.equals(TipoMaterialTurma.TAREFA);
	}
	
	public boolean isTipoQuestionario() {
		return tipoMaterial.equals(TipoMaterialTurma.QUESTIONARIO);
	}
	
	public boolean isTipoVideo() {
		return tipoMaterial.equals(TipoMaterialTurma.VIDEO);
	}
	
	public boolean isTipoConteudo() {
		return tipoMaterial.equals(TipoMaterialTurma.CONTEUDO);
	}

	public boolean isTipoRotulo() {
		return tipoMaterial.equals(TipoMaterialTurma.ROTULO);
	}

	public boolean isTipoForum() {
		return tipoMaterial.equals(TipoMaterialTurma.FORUM);
	}

	public boolean isTipoEnquete() {
		return tipoMaterial.equals(TipoMaterialTurma.ENQUETE);
	}

	public boolean isTipoChat() {
		return tipoMaterial.equals(TipoMaterialTurma.CHAT);
	}
	
	public boolean isSite() {
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TipoMaterialTurma getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(TipoMaterialTurma tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public TopicoAula getTopicoAula() {
		return topicoAula;
	}

	public void setTopicoAula(TopicoAula topicoAula) {
		this.topicoAula = topicoAula;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Integer getIdMaterial() {
		return idMaterial;
	}

	public void setIdMaterial(Integer idMaterial) {
		this.idMaterial = idMaterial;
	}
	
	/** Retorna a classe de acordo com o tipo de material. */
	@SuppressWarnings("rawtypes")
	public Class getClasseMaterial() {
		if (isTipoArquivo()) { 
			return new ArquivoTurma().getClass();
		}

		if (isTipoConteudo()) { 
			return new ConteudoTurma().getClass();
		}
		
		if (isTipoIndicacao()) { 
			return new IndicacaoReferencia().getClass();
		}

		if (isTipoQuestionario()) { 
			return new QuestionarioTurma().getClass();
		}

		if (isTipoRotulo()) { 
			return new RotuloTurma().getClass();
		}

		if (isTipoTarefa()) { 
			return new TarefaTurma().getClass();
		}
		
		if (isTipoVideo()) { 
			return new VideoTurma().getClass();
		}

		if (isTipoForum()) { 
			return new ForumTurma().getClass();
		}

		return null;
	}

	@Override
	public String getMensagemAtividade() {
		return "Novo material adicionado.";
	}

	public Integer getNivel() {
		return nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
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
		MaterialTurma other = (MaterialTurma) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * Permite ordenar o material na página principal da turma virtual.
	 */
	@Override
	public int compareTo(MaterialTurma other) {
		if ( getOrdem() != null && other.getOrdem() != null ) {
			return getOrdem() - other.getOrdem();
		}
		return 0;		
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
