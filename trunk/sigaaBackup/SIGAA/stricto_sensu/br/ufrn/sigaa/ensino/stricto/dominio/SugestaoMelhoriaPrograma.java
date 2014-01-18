/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 08/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Ações que o docente indica para a melhoria da qualidade de formação de
 * Recursos Humanos e que contribua para o incremento do conceito do programa,
 * distribuídas nos seguintes níveis:
 * <ol>
 * <li>Docente</li>
 * <li>Coordenação do Programa</li>
 * <li>Direção do Centro</li>
 * <li>Pró-reitoria de Pós-Graduação</li>
 * <li>Reitoria</li>
 * </ol>
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(schema="stricto_sensu", name="sugestao_melhoria_programa")
public class SugestaoMelhoriaPrograma implements PersistDB {
	
	/** Chave primaria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_sugestao_melhoria_programa", nullable = false)
	private int id;
	
	/** Ações que o Programa indica para a melhoria da qualidade de docentes. */
	@Column(name="docente")
	private String docente;
	
	/** Ações que o Programa indica para a melhoria da qualidade da Coordenação do Programa. */
	@Column(name="coordenacao_programa")
	private String coordenacaoPrograma;
	
	/** Ações que o Programa indica para a melhoria da qualidade do Centro. */
	@Column(name="direcao_centro")
	private String direcaoCentro;
	
	/** Ações que o Programa indica para a melhoria da qualidade da Pró Reitoria de Pós Graduação. */
	@Column(name="pro_reitoria_pos_graduacao")
	private String proReitoriaPosGraduacao;
	
	/** Ações que o Programa indica para a melhoria da qualidade da Reitoria. */
	@Column(name="reitoria")
	private String reitoria;
	
	/**
	 * Construtor padrão.
	 */
	public SugestaoMelhoriaPrograma() {
	}
	 
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDocente() {
		return docente;
	}
	public void setDocente(String docente) {
		this.docente = docente;
	}
	public String getCoordenacaoPrograma() {
		return coordenacaoPrograma;
	}
	public void setCoordenacaoPrograma(String coordenacaoPrograma) {
		this.coordenacaoPrograma = coordenacaoPrograma;
	}
	public String getDirecaoCentro() {
		return direcaoCentro;
	}
	public void setDirecaoCentro(String direcaoCentro) {
		this.direcaoCentro = direcaoCentro;
	}
	public String getProReitoriaPosGraduacao() {
		return proReitoriaPosGraduacao;
	}
	public void setProReitoriaPosGraduacao(String proReitoriaPosGraduacao) {
		this.proReitoriaPosGraduacao = proReitoriaPosGraduacao;
	}
	public String getReitoria() {
		return reitoria;
	}
	public void setReitoria(String reitoria) {
		this.reitoria = reitoria;
	}

	/** Retorna se pelo menos um dos campos foi preenchido.  
	 * @return
	 */
	public boolean isPreenchido() {
		return !isEmpty(coordenacaoPrograma) || !isEmpty(direcaoCentro)
				|| !isEmpty(docente) || !isEmpty(proReitoriaPosGraduacao)
				|| !isEmpty(reitoria);
	}
}
