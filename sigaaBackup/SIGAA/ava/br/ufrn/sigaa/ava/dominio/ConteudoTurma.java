/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe que representa um conteúdo submetido pelo professor para o uso dos
 * alunos na turma virtual.
 *
 * @author David Pereira
 *
 */
@Entity @HumanName(value="Conteúdo", genero='M')
@Table(name = "conteudo", schema = "ava")
public class ConteudoTurma extends AbstractMaterialTurma implements DominioTurmaVirtual {

	
	/**
	 * Chave primaria.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_conteudo_turma", nullable = false)
	private int id;

	/**
	 * Atributo
	 */
	@Column(name = "ativo")
	private Boolean ativo;
	
	/**
	 * Titulo do conteudo  inserido.
	 */
	private String titulo;

	/**
	 * Texto do conteudo.
	 */
	private String conteudo;

	/**
	 * Usuario que cadastrou.
	 */
	@CriadoPor
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro")
	private Usuario usuarioCadastro;

	/**
	 * Data de cadastro.
	 */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/**
	 * Topico de aula correspondente.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_topico_aula")
	private TopicoAula aula = new TopicoAula();

	/** Material Turma. */
	@ManyToOne(fetch=FetchType.LAZY, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.CONTEUDO);
	
	/**
	 * Turma do conteudo. 
	 */
	@Transient
	Turma turma = null;
	
	/**
	 * retorna o valor do conteudo.
	 * @return conteudo
	 */
	public String getConteudo() {
		return conteudo;
	}

	/**
	 * Seta o valor do conteudo.
	 * @param conteudo
	 */
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	/**
	 * Retorna o valor da data de cadastro.
	 * @return the dataCadastro
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * Define a data de cadastro do conteúdo.
	 * @param dataCadastro            
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * Retorna a chave primaria. 
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a chave primaria.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	public Boolean getAtivo()
	{
		return this.ativo;
	}

	public void setAtivo(Boolean ativo)
	{
		this.ativo = ativo;
	}

	
	/**
	 * retorna o titulo.
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Seta o titulo.
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Pega o usuario que cadastrou.
	 * @return the usuarioCadastro
	 */
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	/**
	 * Seta o usuario que cadastrou.
	 * @param usuarioCadastro
	 */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public TopicoAula getAula() {
		return aula;
	}

	public void setAula(TopicoAula aula) {
		this.aula = aula;
	}

	@Override
	public String getNome() {
		return titulo;
	}

	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();

		if (StringUtils.isEmpty(titulo)) 
			lista.addErro("Digite um título para o conteúdo.");
		
		if (StringUtils.isEmpty(conteudo)) 
			lista.addErro("O conteúdo é obrigatório.");
		
		if (getAula() == null || getAula().getId() == 0) 
			lista.addErro("Escolha um tópico de aula.");
		
		return lista;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public String getMensagemAtividade() {
		return "Novo Conteúdo: " + titulo;
	}
	
	public MaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(MaterialTurma material) {
		this.material = material;
	}

	@Override
	public String getDescricaoGeral() {
		return conteudo;
	}

	
}
