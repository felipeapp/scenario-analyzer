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

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Arquivos submetidos pelo usuário em seu porta-arquivos
 *
 * @author Gleydson
 */
@Entity @HumanName(value="Arquivo", genero='M')
@Table(name = "arquivo_turma", schema = "ava")
public class ArquivoTurma extends AbstractMaterialTurma implements Validatable {

	/** Identificador único para o arquivo da turma. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_arquivo_turma")
	private int id;

	/** Tópico de aula ao qual o arquivo está vinculado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_aula")
	private TopicoAula aula;

	/** Arquivo do usuário. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_arquivo")
	private ArquivoUsuario arquivo;

	/** Nome do arquivo. */
	private String nome;

	/** Descrição do arquivo. */
	private String descricao;

	/** Usuário que cadastrou o arquivo. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_usuario")
	private Usuario usuarioCadastro;
	
	/** Data de cadastro do arquivo. */
	private Date data;
	
	/** Material Turma. */
	@ManyToOne(fetch=FetchType.LAZY, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.ARQUIVO);
	
	/** Utilizado durante a notificação dos discentes da inclusão de um novo arquivo na turma. */
	@Transient
	private boolean notificarAlunos;
	
	/** Identifica o tipo de conteúdo do arquivo. */
	@Transient
	private String contentType;
	
	/**Utilizado para Associar um ArquivoTurma ao arquivo com o uplaod do arquivo */
	@Transient
	private UploadedFile arquivoUpload;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ArquivoTurma() {
	}

	public ArquivoUsuario getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoUsuario arquivo) {
		this.arquivo = arquivo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TopicoAula getAula() {
		return aula;
	}

	public void setAula(TopicoAula aula) {
		this.aula = aula;
	}

	public ListaMensagens validate() {
		return null;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public Date getDataCadastro() {
		return data;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public boolean isNotificarAlunos() {
		return notificarAlunos;
	}

	public void setNotificarAlunos(boolean notificarAlunos) {
		this.notificarAlunos = notificarAlunos;
	}

	public boolean isSite() {
		return false;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public MaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(MaterialTurma material) {
		this.material = material;
	}

	
	public UploadedFile getArquivoUpload() {
		return arquivoUpload;
	}

	public void setArquivoUpload(UploadedFile arquivoUpload) {
		this.arquivoUpload = arquivoUpload;
	}

	@Override
	public String getDescricaoGeral() {
		return descricao;
	}	
	
}