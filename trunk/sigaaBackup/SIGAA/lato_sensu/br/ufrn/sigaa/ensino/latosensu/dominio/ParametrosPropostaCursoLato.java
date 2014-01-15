package br.ufrn.sigaa.ensino.latosensu.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Entidade responsável pelo gerenciamento os parâmetros dos curso Lato Sensu.  
 * 
 * @author Jean Guerethes
 */
@Entity
@Table(name = "parametros_proposta_curso_lato", schema = "lato_sensu")
public class ParametrosPropostaCursoLato implements Validatable {

	
	/** Chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_parametro_curso_lato", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Indica se está em uso */
	private boolean ativo;

	/** Porcentagem mínima de docentes que devem ser da Instituição */
	@Column(name = "porcentagem_servidores")
	private Double porcentagemServidores;
	
	/** Porcentagem da carga horária máxima total dos docentes */
	@Column(name = "porcentagem_total_docente_curso")
	private Double chTotalDocenteCurso;

	/** Porcentagem Mínima de Docentes Internos */
	@Column(name = "porcentagem_minima_docentes_interno")
	private Double porcentagemMinimaDocentesInternos;

	/** Porcentagem Máxima de Docentes Externos */
	@Column(name = "porcentagem_maxima_docentes_externos")
	private Double porcentagemMaximaDocentesExternos;
	
	/** Porcetagem das vagas destinadas aos servidores */
	@Column(name = "porcentagem_vagas_servidores")
	private Double porcentagemVagasServidores;

	/** Necessário o docente ter cadastro o Curriculo Lattes */
	@Column(name = "curriculo_lattes_obrigatorio")
	private Boolean curriculoLattesObrigatorio;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public Double getPorcentagemServidores() {
		return porcentagemServidores;
	}

	public void setPorcentagemServidores(Double porcentagemServidores) {
		this.porcentagemServidores = porcentagemServidores;
	}

	public Double getChTotalDocenteCurso() {
		return chTotalDocenteCurso;
	}

	public void setChTotalDocenteCurso(Double chTotalDocenteCurso) {
		this.chTotalDocenteCurso = chTotalDocenteCurso;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Double getPorcentagemMinimaDocentesInternos() {
		return porcentagemMinimaDocentesInternos;
	}

	public void setPorcentagemMinimaDocentesInternos(
			Double porcentagemMinimaDocentesInternos) {
		this.porcentagemMinimaDocentesInternos = porcentagemMinimaDocentesInternos;
	}

	public Double getPorcentagemMaximaDocentesExternos() {
		return porcentagemMaximaDocentesExternos;
	}

	public void setPorcentagemMaximaDocentesExternos(
			Double porcentagemMaximaDocentesExternos) {
		this.porcentagemMaximaDocentesExternos = porcentagemMaximaDocentesExternos;
	}

	public Double getPorcentagemVagasServidores() {
		return porcentagemVagasServidores;
	}

	public void setPorcentagemVagasServidores(Double porcentagemVagasServidores) {
		this.porcentagemVagasServidores = porcentagemVagasServidores;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}

	public Boolean getCurriculoLattesObrigatorio() {
		return curriculoLattesObrigatorio;
	}

	public void setCurriculoLattesObrigatorio(Boolean curriculoLattesObrigatorio) {
		this.curriculoLattesObrigatorio = curriculoLattesObrigatorio;
	}
	
}