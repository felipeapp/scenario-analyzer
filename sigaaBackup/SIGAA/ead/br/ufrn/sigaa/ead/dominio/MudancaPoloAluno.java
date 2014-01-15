/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Oct 15, 2007
 *
 */
package br.ufrn.sigaa.ead.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Classe que registra a mudança do polo de um discente de EAD ou de curso PROBASICA
 *
 * @author Victor Hugo
 *
 */
@Entity
@Table(name="mudanca_polo_aluno", schema="ead")
public class MudancaPoloAluno implements PersistDB {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;

	@ManyToOne
	@JoinColumn(name="id_discente")
	/** discente que teve o polo alterado */
	private DiscenteGraduacao discenteGraduacao;

	/** registra o polo antigo e novo caso seja alteração de polo de aluno de curso distancia */
	@ManyToOne
	@JoinColumn(name="id_polo_antigo")
	private Polo poloAntigo;

	@ManyToOne
	@JoinColumn(name="id_polo_novo")
	/** polo que o discente passou a possuir depois da alteração.
	 *  perceba que este campo só será populado se for alteração de
	 *  polo de discente EAD e o discente possuir um polo antes da operação
	 *  Ao alterar pólos de discente de cursos pro-básica o registro fica em id_curso_novo */
	private Polo poloNovo;

	/** registra o curso antigo e novo caso seja alteração de polo de aluno de curso pro-básica	 */
	@ManyToOne
	@JoinColumn(name="id_curso_antigo")
	private Curso cursoAntigo;

	@ManyToOne
	@JoinColumn(name="id_curso_novo")
	/** curso que o discente passou a possuir depois da alteração.
	 * perceba que este campo só será populado se for alteração de polo de discente de curso PROBASICA.
	 * Ao alterar pólos de discente de cursos EAD o registro fica em id_polo_novo */
	private Curso cursoNovo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteGraduacao getDiscenteGraduacao() {
		return discenteGraduacao;
	}

	public void setDiscenteGraduacao(DiscenteGraduacao discenteGraduacao) {
		this.discenteGraduacao = discenteGraduacao;
	}

	public Polo getPoloAntigo() {
		return poloAntigo;
	}

	public void setPoloAntigo(Polo poloAntigo) {
		this.poloAntigo = poloAntigo;
	}

	public Polo getPoloNovo() {
		return poloNovo;
	}

	public void setPoloNovo(Polo poloNovo) {
		this.poloNovo = poloNovo;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Curso getCursoAntigo() {
		return cursoAntigo;
	}

	public void setCursoAntigo(Curso cursoAntigo) {
		this.cursoAntigo = cursoAntigo;
	}

	public Curso getCursoNovo() {
		return cursoNovo;
	}

	public void setCursoNovo(Curso cursoNovo) {
		this.cursoNovo = cursoNovo;
	}


}
