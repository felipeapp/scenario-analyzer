/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Entidade que atribui permissão para uma pessoa na turma
 *
 * @author Gleydson
 *
 */
@Entity @HumanName(value="Permissão", genero='F')
@Table(name="permissao_ava", schema="ava")
public class PermissaoAva implements DominioTurmaVirtual {
	
	public static final int DOCENTE = 1;
	public static final int FORUM = 2;
	public static final int ENQUETE = 3;
	public static final int TAREFA = 4;
	public static final int CORRIGIR_TAREFA = 5;
	public static final int INSERIR_ARQUIVO = 6;

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;

	@ManyToOne
	@JoinColumn(name="id_pessoa")
	private Pessoa pessoa = new Pessoa(); // discente ou outro professor

	@ManyToOne
	@JoinColumn(name="id_turma")
	private Turma turma;

	// Permissão para a pessoa agir como docente na turma
	private boolean docente;

	// Permissão para gerenciar fóruns
	private boolean forum;

	// permissão para gerenciar enquete
	private boolean enquete;

	// permissão para gerenciar tarefas
	private boolean tarefa;

	// permissão para corrigir tarefas
	@Column(name="corrigir_tarefa")
	private boolean corrigirTarefa;

	// permissão para inserir arquivo
	@Column(name="inserir_arquivo")
	private boolean inserirArquivo;
	
	@CriadoPor @ManyToOne
	@JoinColumn(name="id_usuario_cadastro")
	private Usuario usuarioCadastro;
	
	@CriadoEm @Column(name="data_cadastro")
	private Date dataCadastro;

	public boolean isCorrigirTarefa() {
		return corrigirTarefa;
	}

	public void setCorrigirTarefa(boolean corrigirTarefa) {
		this.corrigirTarefa = corrigirTarefa;
	}

	public boolean isDocente() {
		return docente;
	}

	public void setDocente(boolean docente) {
		this.docente = docente;
	}

	public boolean isEnquete() {
		return enquete;
	}

	public void setEnquete(boolean enquete) {
		this.enquete = enquete;
	}

	public boolean isForum() {
		return forum;
	}

	public void setForum(boolean forum) {
		this.forum = forum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isInserirArquivo() {
		return inserirArquivo;
	}

	public void setInserirArquivo(boolean inserirArquivo) {
		this.inserirArquivo = inserirArquivo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public boolean isTarefa() {
		return tarefa;
	}

	public void setTarefa(boolean tarefa) {
		this.tarefa = tarefa;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getMensagemAtividade() {
		return null;
	}

}