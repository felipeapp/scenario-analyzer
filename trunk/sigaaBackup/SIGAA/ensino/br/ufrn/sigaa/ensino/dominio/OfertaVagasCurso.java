/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 14/07/2008
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Define a oferta de vagas de um curso para um dado ano, período. A oferta pode
 * estar vinculada a um processo seletivo.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity
@Table(name = "oferta_vagas_curso", schema = "ensino")
public class OfertaVagasCurso implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_oferta_vagas_curso", nullable = false)
	private int id;

	/** Matriz curricular ofertada. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_matriz_curricular")
	private MatrizCurricular matrizCurricular;
	
	/** Ano da oferta de vaga. */
	private int ano;
	
	/** Forma de ingresso da oferta de vaga. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_forma_ingresso")
	private FormaIngresso formaIngresso;
	
	/** Número de vagas ofertadas para entrada no primeiro período. */
	@Column(name = "vagas_periodo_1")
	private int vagasPeriodo1;
	
	/** Número de vagas ofertadas para entrada no segundo período. */
	@Column(name = "vagas_periodo_2")
	private int vagasPeriodo2;
	
	/** Número de vagas ociosas para preenchimento no primeiro período. */
	@Column(name = "vagas_ociosas_periodo_1")
	private int vagasOciosasPeriodo1;
	
	/** Número de vagas ociosas para preenchimento no segundo período. */
	@Column(name = "vagas_ociosas_periodo_2")
	private int vagasOciosasPeriodo2;
	
	/** Curso da vaga ofertada. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;
	
	/** Total de vagas ofertadas (primeiro + segundo períodos). */
	@Column(name = "total_vagas", nullable = false)
	private int totalVagas;
	
	/** Total de vagas ofertadas (primeiro + segundo períodos). */
	@Column(name = "total_vagas_ociosas", nullable = false)
	private int totalVagasOciosas;
	
	/** Especifica o Pólo de EaD desta oferta. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_polo", nullable = true)
	private Polo polo;
	
	/** Cotas de reservas de vagas desta oferta. */
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "id_oferta_vagas_curso", nullable = false)
	private Collection<CotaOfertaVagaCurso> cotas;

	/** Construtor padrão. */
	public OfertaVagasCurso() {
		vagasPeriodo1 = 0;
		vagasPeriodo2 = 0;
		vagasOciosasPeriodo1 = 0;
		vagasOciosasPeriodo2 = 0;
		cotas = new LinkedList<CotaOfertaVagaCurso>();
	}
	
	/** Construtor parametrizado. */
	public OfertaVagasCurso(int id) {
		this();
		this.id = id;
	}

	/** Retorna o ano da oferta de vaga. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Retorna o curso da vaga ofertada. 
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Retorna a forma de ingresso da oferta de vaga. 
	 * @return
	 */
	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	/** Retorna a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna a matriz curricular ofertada. 
	 * @return
	 */
	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	/** Retorna o total de vagas ofertadas (primeiro + segundo períodos). 
	 * @return
	 */
	public int getTotalVagas() {
		return totalVagas;
	}
	
	/** Retorna o total de vagas ofertadas (primeiro + segundo períodos). 
	 * @return
	 */
	public int getTotalVagasOciosas() {
		return totalVagasOciosas;
	}

	/** Retorna o número de vagas ofertadas para entrada no primeiro período. 
	 * @return
	 */
	public int getVagasPeriodo1() {
		return vagasPeriodo1;
	}

	/** Retorna o número de vagas ofertadas para entrada no segundo período. 
	 * @return
	 */
	public int getVagasPeriodo2() {
		return vagasPeriodo2;
	}
	
	/** Retorna o número de vagas ociosas para preenchimento no primeiro período. 
	 * @return
	 */
	public int getVagasOciosasPeriodo1() {
		return vagasOciosasPeriodo1;
	}

	/** Retorna o número de vagas ociosas para preenchimento no segundo período. 
	 * @return
	 */
	public int getVagasOciosasPeriodo2() {
		return vagasOciosasPeriodo2;
	}

	/** Seta o Ano da oferta de vaga. 
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Seta o curso da vaga ofertada. 
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Seta a forma de ingresso da oferta de vaga. 
	 * @param formaIngresso
	 */
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta a matriz curricular ofertada. 
	 * @param matrizCurricular
	 */
	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	/** Seta o total de vagas ofertadas (primeiro + segundo períodos). 
	 * @param totalVagas
	 */
	public void setTotalVagas(int totalVagas) {
		this.totalVagas = totalVagas;
	}
	
	/** Seta o total de vagas ofertadas (primeiro + segundo períodos). 
	 * @param totalVagas
	 */
	public void setTotalVagasOciosas(int totalVagasOciosas) {
		this.totalVagasOciosas = totalVagasOciosas;
	}

	/** Seta o número de vagas ociosas para preenchimento no primeiro período. 
	 * @param vagasOciosasPeriodo1
	 */
	public void setVagasOciosasPeriodo1(int vagasOciosasPeriodo1) {
		this.vagasOciosasPeriodo1 = vagasOciosasPeriodo1;
	}

	/** Seta o número de vagas ociosas para preenchimento no segundo período. 
	 * @param vagasOciosasPeriodo2
	 */
	public void setVagasOciosasPeriodo2(int vagasOciosasPeriodo2) {
		this.vagasOciosasPeriodo2 = vagasOciosasPeriodo2;
	}
	
	/** Seta o número de vagas ofertadas para entrada no primeiro período. 
	 * @param vagasPeriodo1
	 */
	public void setVagasPeriodo1(int vagasPeriodo1) {
		this.vagasPeriodo1 = vagasPeriodo1;
	}

	/** Seta o número de vagas ofertadas para entrada no segundo período. 
	 * @param vagasPeriodo2
	 */
	public void setVagasPeriodo2(int vagasPeriodo2) {
		this.vagasPeriodo2 = vagasPeriodo2;
	}

	/** Valida os dados.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		return null;
	}
	
	/**
	 * Retorna uma descrição textual da oferta de vaga, descrevendo a matriz curricular e o total de vagas ofertadas. 
	 * 
	 * @return
	 */
	public String getDescricaoTotalVagas() {
		return getMatrizCurricular().getDescricaoSemEnfase()+ " (" + getTotalVagas()+" vagas)";
	}
	
	/**
	 * Retorna uma descrição textual da oferta de vaga, descrevendo a matriz curricular e o número de vagas ofertadas no período indicado. 
	 * 
	 * @param periodo
	 * @return
	 */
	public String getDescricaoVagas(int periodo) {
		if (periodo == 1)
			return getMatrizCurricular().getDescricaoSemEnfase()+ " (" + getVagasPeriodo1()+" vagas para o 1º período)";
		else if (periodo == 2)
			return getMatrizCurricular().getDescricaoSemEnfase()+ " (" + getVagasPeriodo2()+" vagas para o 2º período)";
		else
			return "";
	}

	/**
	 * Retorna uma representação textual desta oferta de vagas, com os seguintes
	 * atributos: ano, curso, vagas no primeiro e
	 * no segundo período.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ano);
		str.append(" - " + curso.getDescricaoCompleta());
		str.append(", " + vagasPeriodo1);
		str.append("/ " + vagasPeriodo2);
		return str.toString(); 
	}
	
	/** Compara os atributos matrizCurricular e polo para determinar se são iguais. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "matrizCurricular", "polo");
	}
	
	/** Retorna um código hash para o objeto
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, matrizCurricular, polo);
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public Collection<CotaOfertaVagaCurso> getCotas() {
		return cotas;
	}

	public void setCotas(Collection<CotaOfertaVagaCurso> cotas) {
		this.cotas = cotas;
	}

	/** Adiciona um grupo de cotas à coleção de cotas existentes.
	 * @param grupoCota
	 */
	public void addCota(CotaOfertaVagaCurso grupoCota) {
		if (cotas == null) cotas = new LinkedList<CotaOfertaVagaCurso>();
		cotas.add(grupoCota);
	}
}
