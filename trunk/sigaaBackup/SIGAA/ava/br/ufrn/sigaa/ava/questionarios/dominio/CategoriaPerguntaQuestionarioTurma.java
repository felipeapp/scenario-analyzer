/* 
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Criado em: 28/11/2010
 */
package br.ufrn.sigaa.ava.questionarios.dominio;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Categoria que agrupa as perguntas de questionário de um docente.
 * 
 * @author Fred_Castro
 *
 */
@Entity @Table(name="categoria_pergunta_questionario_turma", schema = "ava")
public class CategoriaPerguntaQuestionarioTurma implements Validatable {
	
	/** Identificador do objeto */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_categoria_pergunta_questionario_turma", nullable = false)
	private int id;
	
	/**
	 * O dono da categoria.
	 */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_dono")
	private Usuario dono;
	
	/**
	 * O nome da categoria
	 */
	private String nome;
	
	/**
	 * A listagem com as questões desta categoria.
	 */
	@OneToMany(fetch = FetchType.LAZY) @JoinColumn(name="id_categoria")
	private List <PerguntaQuestionarioTurma> perguntas = new ArrayList <PerguntaQuestionarioTurma> ();
	
	/** Usuários com quem esta categoria está compartilhada. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema="ava", name="categoria_questao_compartilhamento",
			joinColumns=@JoinColumn(name="id_categoria_pergunta_questionario_turma"),
			inverseJoinColumns=@JoinColumn(name="id_pessoa"))
	private List<Pessoa> pessoasComCompartilhamento;
	
	/**
	 * Indica se o objeto foi removido.
	 */
	private boolean ativo = true;
	
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
	
	/** Indica se é para exibir as perguntas da categoria na listagem de categorias. */
	@Transient
	private boolean exibirPerguntas = false;

	/**
	 * Construtor padrão necessário para o hibernate.
	 */
	public CategoriaPerguntaQuestionarioTurma () {}
	
	/**
	 * Constroi uma categoria de questões com o id passado.
	 * @param id
	 */
	public CategoriaPerguntaQuestionarioTurma (int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getDono() {
		return dono;
	}

	public void setDono(Usuario dono) {
		this.dono = dono;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public List<PerguntaQuestionarioTurma> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<PerguntaQuestionarioTurma> perguntas) {
		this.perguntas = perguntas;
	}

	public boolean isExibirPerguntas() {
		return exibirPerguntas;
	}

	public void setExibirPerguntas(boolean exibirPerguntas) {
		this.exibirPerguntas = exibirPerguntas;
	}

	@Override
	public ListaMensagens validate() {
		
		ListaMensagens lm = new ListaMensagens();
		
		if (StringUtils.isEmpty(nome))
			lm.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
		
		return lm;
	}

	public List<Pessoa> getPessoasComCompartilhamento() {
		return pessoasComCompartilhamento;
	}

	public void setPessoasComCompartilhamento(
			List<Pessoa> pessoasComCompartilhamento) {
		this.pessoasComCompartilhamento = pessoasComCompartilhamento;
	}
}