/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/08/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isAllEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Calendário de aplicação do ENADE aos discentes ingressantes / concluíntes por curso.
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(schema = "graduacao", name = "calendario_enade")
public class CalendarioEnade implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_calendario_enade", nullable = false)
	private int id;
	
	/** Tipo do ENADE (Ingressante, Concluinte). */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo_enade", nullable = false)
	private TipoENADE tipoEnade;
	
	/** Ano de aplicação do ENADE para os cursos do calendário. */
	private int ano;
	
	/** Cursos cujo os discentes farão obrigatoriamente o ENADE. */
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy="calendarioEnade")
	@JoinColumn(name = "id_calendario_enade", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private Set<CursoGrauAcademicoEnade> cursosGrauAcademico;
	
	/** Data da prova do ENADE. */
	@Column(name="data_prova")
	private Date dataProva;

	/** Construtor padrão. */
	public CalendarioEnade() {
		this.cursosGrauAcademico = new TreeSet<CursoGrauAcademicoEnade>();
		this.tipoEnade = TipoENADE.INGRESSANTE;
	}

	/** Construtor parametrizado. */
	public CalendarioEnade(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TipoENADE getTipoEnade() {
		return tipoEnade;
	}

	public void setTipoEnade(TipoENADE tipoEnade) {
		this.tipoEnade = tipoEnade;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Adiciona um curso à lista de cursos avaliados.
	 * @param curso
	 */
	public boolean addCursoGrauAcademico(CursoGrauAcademicoEnade cursoGrauAcademico) {
		if (this.cursosGrauAcademico == null)
			this.cursosGrauAcademico = new TreeSet<CursoGrauAcademicoEnade>();
		cursoGrauAcademico.setCalendarioEnade(this);
		return cursosGrauAcademico.add(cursoGrauAcademico);
	}

	/** Retorna o ano, tipo do ENADE, e a lista de cursos.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ano).append(" - ").append(tipoEnade.toString()).append(": ");
		for (CursoGrauAcademicoEnade cursoGrau : cursosGrauAcademico)
			str.append(cursoGrau.getCurso().getNome()).append(" - ").append(cursoGrau.getGrauAcademico().getDescricao()).append(", ");
		return str.toString();
	}

	/** Valida os dados obrigatórios do ENADE (ano, tipo do enade, lista de cursos) 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(tipoEnade, "Tipo", lista);
		if ( !isEmpty(tipoEnade) ){
			if( tipoEnade == TipoENADE.INGRESSANTE ){
				validateMinValue(ano, ParticipacaoEnade.ANO_INICIO_ENADE_INGRESSANTE, "Ano", lista);
			}else{
				validateMinValue(ano, ParticipacaoEnade.ANO_INICIO_ENADE_CONCLUINTE, "Ano", lista);
			}	
		}
		if( isAllEmpty(cursosGrauAcademico) )
			lista.addErro("É necessário adicionar pelo menos um curso");
		return lista;
	}

	public Date getDataProva() {
		return dataProva;
	}

	public void setDataProva(Date dataProva) {
		this.dataProva = dataProva;
	}

	public Set<CursoGrauAcademicoEnade> getCursosGrauAcademico() {
		return cursosGrauAcademico;
	}

	public void setCursosGrauAcademico(Set<CursoGrauAcademicoEnade> cursosGrauAcademico) {
		this.cursosGrauAcademico = cursosGrauAcademico;
	}
}
