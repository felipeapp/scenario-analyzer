/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 10/12/2012
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.vestibular.dominio.GrupoCotaVagaCurso;

/** Esta classe define as cotas de reserva de vagas para oferta de cursos. 
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(name = "cota_oferta_vaga_curso", schema = "ensino")
public class CotaOfertaVagaCurso implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_cota_oferta_vaga_curso")
	private int id;
	
	/** Oferta de Vagas de Curso ao qual esta cota está associada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_oferta_vagas_curso",nullable=false, insertable=false, updatable=false)
	private OfertaVagasCurso ofertaVagasCurso;
	
	/** Grupo de Cota ao qual esta cota está associada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_grupo_cota_vaga_curso",nullable=false)
	private GrupoCotaVagaCurso grupoCota;
	
	/** Total de vagas para cotas (primeiro + segundo períodos). */
	@Column(name = "total_vagas", nullable = false)
	private int totalVagas;
	
	/** Total de vagas para cotas (primeiro + segundo períodos). */
	@Column(name = "total_vagas_ociosas", nullable = false)
	private int totalVagasOciosas;
	
	/** Número de vagas ociosas para preenchimento no primeiro período. */
	@Column(name = "vagas_ociosas_periodo_1")
	private int vagasOciosasPeriodo1;
	
	/** Número de vagas ociosas para preenchimento no segundo período. */
	@Column(name = "vagas_ociosas_periodo_2")
	private int vagasOciosasPeriodo2;
	
	/** Número de vagas ofertadas para cotas com entrada no primeiro período. */
	@Column(name = "vagas_periodo_1")
	private int vagasPeriodo1;
	
	/** Número de vagas ofertadas para cotas com entrada no segundo período. */
	@Column(name = "vagas_periodo_2")
	private int vagasPeriodo2;
	
	public CotaOfertaVagaCurso() {
	}
	
	public CotaOfertaVagaCurso(int id){
		this();
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public OfertaVagasCurso getOfertaVagasCurso() {
		return ofertaVagasCurso;
	}
	public void setOfertaVagasCurso(OfertaVagasCurso ofertaVagasCurso) {
		this.ofertaVagasCurso = ofertaVagasCurso;
	}
	public GrupoCotaVagaCurso getGrupoCota() {
		return grupoCota;
	}
	public void setGrupoCota(GrupoCotaVagaCurso grupoCota) {
		this.grupoCota = grupoCota;
	}
	public int getTotalVagas() {
		return totalVagas;
	}
	public void setTotalVagas(int totalVagas) {
		this.totalVagas = totalVagas;
	}
	public int getTotalVagasOciosas() {
		return totalVagasOciosas;
	}
	public void setTotalVagasOciosas(int totalVagasOciosas) {
		this.totalVagasOciosas = totalVagasOciosas;
	}
	public int getVagasOciosasPeriodo1() {
		return vagasOciosasPeriodo1;
	}
	public void setVagasOciosasPeriodo1(int vagasOciosasPeriodo1) {
		this.vagasOciosasPeriodo1 = vagasOciosasPeriodo1;
	}
	public int getVagasOciosasPeriodo2() {
		return vagasOciosasPeriodo2;
	}
	public void setVagasOciosasPeriodo2(int vagasOciosasPeriodo2) {
		this.vagasOciosasPeriodo2 = vagasOciosasPeriodo2;
	}
	public int getVagasPeriodo1() {
		return vagasPeriodo1;
	}
	public void setVagasPeriodo1(int vagasPeriodo1) {
		this.vagasPeriodo1 = vagasPeriodo1;
	}
	public int getVagasPeriodo2() {
		return vagasPeriodo2;
	}
	public void setVagasPeriodo2(int vagasPeriodo2) {
		this.vagasPeriodo2 = vagasPeriodo2;
	}

	/** Valida os dados obrigatórios.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(ofertaVagasCurso, "Oferta", lista);
		validateRequired(grupoCota, "Grupo de Cotas", lista);
		return lista;
	}
	
	/** Retorna uma descrição textual desta cota para oferta de vagas de um curso.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(grupoCota.getDescricao()).append(": (")
				.append(vagasPeriodo1).append(", ").append(vagasPeriodo2).append(")");
		return str.toString();
	}

	/** Compara este objeto com outro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CotaOfertaVagaCurso) {
			CotaOfertaVagaCurso other = (CotaOfertaVagaCurso) obj;
			return this.id == other.id;
		} else return false;
	}
	
	/** Retorna um código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, grupoCota);
	}
}