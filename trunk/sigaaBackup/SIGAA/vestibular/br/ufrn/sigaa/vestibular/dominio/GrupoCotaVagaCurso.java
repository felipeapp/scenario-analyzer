/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 10/12/2012
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que define os grupos de cotas aplicados � oferta de vagas em processos
 * seletivos.</br> Cada grupo � determinado pela combina��o dos atributos que
 * indicam se o candidato � egresso de escola p�blica (
 * {@link #egressoEscolaPublica}), se a renda familiar per capita � inferior �
 * 1,5 sal�rio-minimo ({@link #baixaRendaFamiliar}), e se autodeclarou pertencer
 * � grupos �tnicos ({@link #pertenceGrupoEtnico}).<br/>
 * Tamb�m � definida uma ordem de preenchimento das vagas remanescentes do grupo
 * da cota, indicando de quais outros grupos de cotas os candidatos dever�o ser
 * convocados para completar o preenchimento da vaga.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "grupo_cota_vaga_curso", schema = "ensino")
public class GrupoCotaVagaCurso implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_grupo_cota_vaga_curso", nullable = false)
	private int id;
	
	/** Descri��o do grupo de cota. */
	@Column(nullable = false)
	private String descricao;
	
	/** Descri��o detalhada do grupo de cota. Geralmente contem o trecho da lei espec�fica que define o grupo. */
	@Column(name="descricao_detalhada", nullable = false)
	private String descricaoDetalhada;
	
	/** Indica se este grupo de cotas � ativo ou n�o. */
	@CampoAtivo
	private boolean ativo;
	
	/** Indica se a cota se aplica aos candidatos egressos de escolas p�blicas. */
	@Column(name = "egresso_escola_publica")
	private boolean egressoEscolaPublica;
	
	/** Indica se a cota se aplica aos candidatos que possuem renda familiar per capita abaixo de 1,5 sal�rio-m�nimo. */
	@Column(name = "baixa_renda_familiar")
	private boolean baixaRendaFamiliar;
	
	/** Indica se a cota se aplica aos candidatos pertencentes � grupos �tnicos (negros, pardos, ind�genas). */
	@Column(name = "pertence_grupo_etnico")
	private boolean pertenceGrupoEtnico;
	
	/** N�o preenchendo todas as vagas de cotas do grupo, o preenchimento dar-se-� pela ordem do(s) grupo(s) definido(s) nesta cole��o. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(name="ordem_chamada_cotas_remanescentes", schema="ensino",
		joinColumns=@JoinColumn(name="id_grupo_cota_vaga_curso"),  
		inverseJoinColumns=@JoinColumn(name="id_grupo_cota_rechamada"))
	@IndexColumn(name = "ordem")
	private List<GrupoCotaVagaCurso> ordemChamadaVagasRemanescentes;
	
	/** N�vel de ensino deste grupo de cota. */
	private char nivel;
	
	/** Construtor padr�o. */
	public GrupoCotaVagaCurso() {
		super();
		ordemChamadaVagasRemanescentes = new LinkedList<GrupoCotaVagaCurso>();
	}
	
	/** Construtor parametrizado. */
	public GrupoCotaVagaCurso(int id) {
		this();
		this.id = id;
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
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	public boolean isEgressoEscolaPublica() {
		return egressoEscolaPublica;
	}
	public void setEgressoEscolaPublica(boolean egressoEscolaPublica) {
		this.egressoEscolaPublica = egressoEscolaPublica;
	}
	public boolean isBaixaRendaFamiliar() {
		return baixaRendaFamiliar;
	}
	public void setBaixaRendaFamiliar(boolean baixaRendaFamiliar) {
		this.baixaRendaFamiliar = baixaRendaFamiliar;
	}
	public boolean isPertenceGrupoEtnico() {
		return pertenceGrupoEtnico;
	}
	public void setPertenceGrupoEtnico(boolean pertenceGrupoEtnico) {
		this.pertenceGrupoEtnico = pertenceGrupoEtnico;
	}
	public List<GrupoCotaVagaCurso> getOrdemChamadaVagasRemanescentes() {
		return ordemChamadaVagasRemanescentes;
	}
	public void setOrdemChamadaVagasRemanescentes(
			List<GrupoCotaVagaCurso> ordemChamadaVagasRemanescentes) {
		this.ordemChamadaVagasRemanescentes = ordemChamadaVagasRemanescentes;
	}
	public char getNivel() {
		return nivel;
	}
	
	public void setNivel(char nivel) {
		this.nivel = nivel;
	}
	
	/** Valida os dados obrigat�rios.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(descricao, "Descri��o", lista);
		return lista;
	}
	
	/** Retorna uma representa��o textual deste grupo de cotas.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(descricao).append(": ").append(descricaoDetalhada);
		return str.toString();
	}

	/** Compara este objeto com outro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GrupoCotaVagaCurso) {
			GrupoCotaVagaCurso other = (GrupoCotaVagaCurso) obj;
			return other.id == this.id;
		} else return false;
	}
	
	/** Retorna um c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, descricao);
	}

	public String getDescricaoDetalhada() {
		return descricaoDetalhada;
	}

	public void setDescricaoDetalhada(String descricaoDetalhada) {
		this.descricaoDetalhada = descricaoDetalhada;
	}
}
