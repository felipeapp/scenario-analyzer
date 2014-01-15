/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Resposta a uma tarefa enviada pelo aluno ao professor pelo portal da turma.
 * 
 * @author David Ricardo
 *
 */
@Entity
@Table(name="resposta_tarefa_turma", schema="ava")
public class RespostaTarefaTurma implements Validatable {

	/**Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id", nullable = false)
	private int id;
	
	/** Tarefa que está sendo respondida. */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="id_tarefa")
	private TarefaTurma tarefa;
	
	/** Se a resposta está ativa ou não */
	@Column(name = "ativo")
	private Boolean ativo;
	
	/** Nome do arquivo enviado na resposta. */
	@Column(name="nome_arquivo")
	private String nomeArquivo;
	
	/** Comentários da resposta*/
	private String comentarios;
	
	/** Preenchido se a tarefa for de texto online. */ 
	@Column(name="texto_resposta")
	private String textoResposta;
	
	/** Data em que a tarefa foi enviada. */ 
	@Column(name="data_envio")
	private Date dataEnvio;
	
	/** Grupo de alunos que enviou a tarefa. */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="id_grupo")
	private GrupoTarefaTurma grupo;
	
	/** Grupo de alunos que enviou a tarefa. */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="id_grupo_discentes")
	private GrupoDiscentes grupoDiscentes;
	
	/** Usuário que enviou o arquivo. */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="id_usuario_envio")
	private Usuario usuarioEnvio;
	
	/** Id do arquivo enviado. */
	@Column(name="id_arquivo")
	private int idArquivo;
	
	/** Nota recebida na tarefa. */
	@Column(name="nota_recebida")
	private Double notaRecebida;

	/** Se o professor já leu a tarefa. */
	@Column(name="lida")
	private boolean lida;
	
	/** Texto de resposta do professor. */
	@Column(name="texto_correcao")
	private String textoCorrecao;
	
	/** Id do arquivo de resposta do professor. */
	@Column(name="id_arquivo_correcao")
	private Integer idArquivoCorrecao;
	
	/** Nome do arquivo de resposta do professor. */
	@Column(name="nome_arquivo_correcao")
	private String nomeArquivoCorrecao;
	
	/** Data em que o professor corrigiu a resposta. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_correcao")
	private Date dataCorrecao;

	/** Número do comprovante da resposta. */
	@Column(name="numero_comprovante")
	private Integer numeroComprovante;
	
	@Transient
	/** Indica a nota da tarefa **/
	private Double nota;
	
	@Transient
	/** Indica se a resposta possui um grupo. **/
	private boolean existeGrupo;
	
	/**
	 * @return the tarefa
	 */
	public TarefaTurma getTarefa() {
		return tarefa;
	}

	/**
	 * @param tarefa the tarefa to set
	 */
	public void setTarefa(TarefaTurma tarefa) {
		this.tarefa = tarefa;
	}

	/**
	 * @return the comentários
	 */
	public String getComentarios() {
		return comentarios;
	}

	/**
	 * @param comentários the comentários to set
	 */
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	/**
	 * @return the dataEnvio
	 */
	public Date getDataEnvio() {
		return dataEnvio;
	}

	/**
	 * @param dataEnvio the dataEnvio to set
	 */
	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the idArquivo
	 */
	public int getIdArquivo() {
		return idArquivo;
	}

	/**
	 * @param idArquivo the idArquivo to set
	 */
	public void setIdArquivo(int idArquivo) {
		this.idArquivo = idArquivo;
	}

	/**
	 * @return the notaRecebida
	 */
	public Double getNotaRecebida() {
		return notaRecebida;
	}

	/**
	 * @param notaRecebida the notaRecebida to set
	 */
	public void setNotaRecebida(Double notaRecebida) {
		this.notaRecebida = notaRecebida;
	}

	/**
	 * @return the usuarioEnvio
	 */
	public Usuario getUsuarioEnvio() {
		return usuarioEnvio;
	}

	/**
	 * @param usuarioEnvio the usuarioEnvio to set
	 */
	public void setUsuarioEnvio(Usuario usuarioEnvio) {
		this.usuarioEnvio = usuarioEnvio;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		//if (!DateUtils.isSameDay(dataEnvio, tarefa.getDataEntrega()) && dataEnvio.after(tarefa.getDataEntrega()))
		//	lista.addErro("Não é possível enviar a tarefa pois o prazo de envio se esgotou");
		
		return lista;
	}

	/**
	 * @return the grupo
	 */
	public GrupoTarefaTurma getGrupo() {
		return grupo;
	}

	/**
	 * @param grupo the grupo to set
	 */
	public void setGrupo(GrupoTarefaTurma grupo) {
		this.grupo = grupo;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public boolean isLida() {
		return lida;
	}

	public void setLida(boolean lida) {
		this.lida = lida;
	}

	public String getTextoResposta() {
		return textoResposta;
	}

	public void setTextoResposta(String textoResposta) {
		this.textoResposta = textoResposta;
	}

	public String getTextoCorrecao() {
		return textoCorrecao;
	}

	public void setTextoCorrecao(String textoCorrecao) {
		this.textoCorrecao = textoCorrecao;
	}

	public Integer getIdArquivoCorrecao() {
		return idArquivoCorrecao;
	}

	public void setIdArquivoCorrecao(Integer idArquivoCorrecao) {
		this.idArquivoCorrecao = idArquivoCorrecao;
	}

	public Date getDataCorrecao() {
		return dataCorrecao;
	}

	public void setDataCorrecao(Date dataCorrecao) {
		this.dataCorrecao = dataCorrecao;
	}

	public String getNomeArquivoCorrecao() {
		return nomeArquivoCorrecao;
	}

	public void setNomeArquivoCorrecao(String nomeArquivoCorrecao) {
		this.nomeArquivoCorrecao = nomeArquivoCorrecao;
	}

	public Integer getNumeroComprovante() {
		return numeroComprovante;
	}

	public void setNumeroComprovante(Integer numeroComprovante) {
		this.numeroComprovante = numeroComprovante;
	}
	
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public void setGrupoDiscentes(GrupoDiscentes grupoDiscentes) {
		this.grupoDiscentes = grupoDiscentes;
	}

	public GrupoDiscentes getGrupoDiscentes() {
		return grupoDiscentes;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}

	public Double getNota() {
		return nota;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj != null) {
			RespostaTarefaTurma other = (RespostaTarefaTurma) obj;
			if (other.getId() == this.id)
				return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public void setExisteGrupo(boolean existeGrupo) {
		this.existeGrupo = existeGrupo;
	}

	public boolean isExisteGrupo() {
		return existeGrupo;
	}
	
}