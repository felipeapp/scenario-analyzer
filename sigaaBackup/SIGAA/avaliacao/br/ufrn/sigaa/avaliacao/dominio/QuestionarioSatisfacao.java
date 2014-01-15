/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 09/06/2008 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Armazena informações sobre o questionário de satisfação da
 * avaliação institucional.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="satisfacao", schema="avaliacao")
public class QuestionarioSatisfacao implements PersistDB {

	@Id @GeneratedValue
	private int id;
	
	@ManyToOne @JoinColumn(name="id_discente")
	private Discente discente;
	
	@ManyToOne @JoinColumn(name="id_docente")
	private Servidor docente;

	@ManyToOne @JoinColumn(name="id_docente_externo")
	private DocenteExterno docenteExterno;
	
	@Column(name="teve_dificuldade")
	private boolean teveDificuldade;
	
	@Column(name="principal_dificuldade")
	private String principalDificuldade;
	
	@Column(name="criar_questao")
	private boolean criarQuestao;
	
	@Column(name="qual_questao")
	private String qualQuestao;
	
	@ManyToOne
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada criadoPor;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public boolean isTeveDificuldade() {
		return teveDificuldade;
	}

	public void setTeveDificuldade(boolean teveDificuldade) {
		this.teveDificuldade = teveDificuldade;
	}

	public String getPrincipalDificuldade() {
		return principalDificuldade;
	}

	public void setPrincipalDificuldade(String principalDificuldade) {
		this.principalDificuldade = principalDificuldade;
	}

	public boolean isCriarQuestao() {
		return criarQuestao;
	}

	public void setCriarQuestao(boolean criarQuestao) {
		this.criarQuestao = criarQuestao;
	}

	public String getQualQuestao() {
		return qualQuestao;
	}

	public void setQualQuestao(String qualQuestao) {
		this.qualQuestao = qualQuestao;
	}

	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}
	
}
