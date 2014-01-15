package br.ufrn.sigaa.ead.dominio;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Item do programa. Diz o conteúdo das aulas de uma disciplina de ensino a
 * distância.
 * 
 * @author Agostinho Campos
 * @author David Pereira
 * 
 */
@Entity
@Table(name = "item_programa", schema = "ensino")
public class ItemPrograma implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_componente_curricular")
	/** Disciplina que possui os itens */
	private ComponenteCurricular disciplina;

	/** Aula que possui o conteúdo deste item */
	private Integer aula;

	/** Conteúdo da aula */
	private String conteudo;

	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@CriadoEm
	private Date criadoEm;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	public Integer getAula() {
		return aula;
	}

	public void setAula(Integer aula) {
		this.aula = aula;
	}

}
