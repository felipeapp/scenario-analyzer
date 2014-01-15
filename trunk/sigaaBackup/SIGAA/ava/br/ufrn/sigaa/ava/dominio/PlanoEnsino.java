/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 18/01/2008
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

import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Registra o plano de ensino de uma turma.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="plano_ensino", schema="ava")
public class PlanoEnsino implements DominioTurmaVirtual {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	@ManyToOne @JoinColumn(name="id_turma")
	private Turma turma;
	
	private String objetivos;
	
	private String metodologia;
	
	private String bibliografia;
	
	@Column(name="criado_em")
	private Date criadoEm;
	
	@ManyToOne @JoinColumn(name="criado_por")
	private Usuario criadoPor;

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

	public String getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	public String getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	public String getBibliografia() {
		return bibliografia;
	}

	public void setBibliografia(String bibliografia) {
		this.bibliografia = bibliografia;
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
		return null;
	}
	
}
