/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;


/**
 * Conjunto de componentes optativos de um curr�culo
 * @author Andre M Dantas
 * @author David Pereira
 *
 */
@Entity
@Table(name = "grupo_optativas", schema = "graduacao")
public class GrupoOptativas implements Validatable {

	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_grupo_optativas", nullable = false)
	/** Identificador do grupo de optativas */
	private int id;

	/** Descri��o do grupo de optativas conforme deve aparecer no hist�rico do aluno */
	private String descricao;

	/** Carga hor�ria total das disciplinas que comp�es o grupo de optativas */
	private int chTotal;
	
	/** Carga hor�ria m�nima que um aluno precisa cursar para integralizar o grupo de optativas */
	private int chMinima;

	/** Curr�culo ao qual pertence o grupo de optativas */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_curriculo")
	private Curriculo curriculo;
	
	/** Componente curricular associado ao grupo. Este componente vai represent�-lo no hist�rico do aluno. */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_componente_curricular")
	private ComponenteCurricular componente;

	/**
	 * Conjunto de componentes curriculares que fazem parte do grupo de optativas
	 */
	@ManyToMany
    @JoinTable(name = "graduacao.curriculo_optativa", joinColumns = @JoinColumn(name = "id_grupo_optativas"), inverseJoinColumns = @JoinColumn(name = "id_curriculo_componente"))
	private List<CurriculoComponente> componentes;

	/**
	 * Carga hor�ria pendente para integraliza��o do grupo. � a diferen�a entre a carga hor�ria m�nima
	 * e a carga hor�ria integralizada. N�o persistida.
	 */
	@Transient
	private Integer chPendente;

	/**
	 * Calcula carga hor�ria do grupo de acordo com as cargas hor�rias
	 * dos componentes.
	 */
	public void calculaChTotal() {
		chTotal = 0;
		for (CurriculoComponente cc : componentes)
			chTotal += cc.getComponente().getChTotal();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getChTotal() {
		return chTotal;
	}

	public void setChTotal(int chTotal) {
		this.chTotal = chTotal;
	}

	public int getChMinima() {
		return chMinima;
	}

	public void setChMinima(int chMinima) {
		this.chMinima = chMinima;
	}

	public Curriculo getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	public List<CurriculoComponente> getComponentes() {
		return componentes;
	}

	public void setComponentes(List<CurriculoComponente> componentes) {
		this.componentes = componentes;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		if (isEmpty(descricao)) erros.addErro("O campo 'Descri��o' � obrigat�rio.");
		if (chMinima < 15) erros.addErro("Valor inv�lido para a C. H. M�nima. Ela deve ser maior ou igual a 15h (1 cr�dito).");
		
		if (isEmpty(componentes) || componentes.size() < 2) 
			erros.addErro("� necess�rio adicionar pelo menos dois componentes.");
		calculaChTotal();
		if (chMinima > chTotal)
			erros.addErro("A carga hor�ria m�nima n�o pode ser maior que a carga hor�ria total do grupo.");
		
		return erros;
	}

	public void setChPendente(Integer chPendente) {
		this.chPendente = chPendente;
	}

	public Integer getChPendente() {
		return chPendente;
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}
	
}
