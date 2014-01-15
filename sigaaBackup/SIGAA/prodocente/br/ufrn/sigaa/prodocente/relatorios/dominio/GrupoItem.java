/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AtividadeMapper;

/**
 * O grupo é uma composição de itens de avaliação. Por exemplo:
 *
 * 1 - Produção Intelectual 2 - Atividades de Ensino
 *
 * Ele possui um conjunto de itens.
 *
 * @author Gleydson, Eric
 */
@Entity
@Table(name = "grupo_item", schema = "prodocente")
public class GrupoItem implements Validatable {

	public static final int QUANTITATIVO = 1;

	public static final int QUALITATIVO = 2;

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_grupo_item", nullable = false)
	private int id;

	@Column(name = "pontuacao")
	private float pontuacao = 0;

	@Column(name = "validade")
	private int validade = 0;

	@Column(name = "limite_pontuacao")
	private float limitePontuacao = 0;

	@Column(name = "indice_topico")
	private int indiceTopico = 0;

	@Column(name= "tipo_pontuacao")
	private int tipoPontuacao = QUANTITATIVO;

	@JoinColumn(name = "id_grupo_relatorio_produtividade", referencedColumnName = "id_grupo_relatorio_produtividade")
	@ManyToOne
	private GrupoRelatorioProdutividade grupoRelatorioProdutividade;

	@JoinColumn(name = "id_item_relatorio_produtividade", referencedColumnName = "id_item_relatorio_produtividade")
	@ManyToOne
	private ItemRelatorioProdutividade itemRelatorioProdutividade;

	@Transient
	private Collection<ProducaoIntelectual> producoes;

	@Transient
	// do tipo ViewAtividadeBuilder
	private Collection<ViewAtividadeBuilder> atividades;

	/**
	 * Indica se a produção é uma atividade, caso afirmativo, a visualização
	 * deve renderizar de maneira diferente.
	 */
	@Transient
	private boolean atividade;

	@Transient
	private ViewAtividadeBuilder atividadeCorrente; // setado pela view pela
													// atividade corrente que
													// está sendo exibida (em
													// rel_producao.jsp)

	@Transient
	private float acumuladorPontos; // acumula os pontos do grupo

	/**
	 * Calcula o total de pontos do grupo analisando os aspectos de pontuação
	 * máxima
	 *
	 * @return
	 */
	public float getTotalPontos() {

		float totalPontos = getTotalPontosSemLimite();

		if (totalPontos > limitePontuacao && limitePontuacao > 0) {
			return limitePontuacao;
		} else {
			return totalPontos;
		}
	}
	
	/**
	 * Calcula o total de pontos do grupo sem considerar a pontuação máxima
	 *
	 * @return
	 */
	public float getTotalPontosSemLimite() {

		float totalPontos = 0;
		float totalPontosMapper = 0;
		if (atividade) {

			if (atividades == null)
				return 0;
			else {
				for (ViewAtividadeBuilder atividade: atividades){
					if(tipoPontuacao == QUALITATIVO) {
						AtividadeMapper mapper = (AtividadeMapper) ReflectionUtils.newInstance( itemRelatorioProdutividade.getProducaoMapper() );
						if(mapper.calculaPontuacao(atividade) != 0)
							totalPontosMapper += mapper.calculaPontuacao(atividade);
						else
							totalPontos+=atividade.getQtdBase();
					} else
						totalPontos+=1;
				}
			}

		} else {

			if (producoes == null)
				return 0;
			else {
				for (ProducaoIntelectual producao : producoes) {
					totalPontos += (producao.isValidado() ? 1 : 0);
				}
			}


		}

		totalPontos = (totalPontos * pontuacao) + totalPontosMapper;

		return totalPontos;
		
	}


	/**
	 * Verificar se o grupo de itens está vazio
	 *
	 * @return
	 */
	public boolean isVazio() {
		return (producoes == null || producoes.isEmpty())
				&& (atividades == null || atividades.isEmpty());
	}

	/** Creates a new instance of GrupoItem */
	public GrupoItem() {
	}

	/**
	 * Creates a new instance of GrupoItem with the specified values.
	 *
	 * @param idGrupoItem
	 *            the idGrupoItem of the GrupoItem
	 */
	public GrupoItem(int idGrupoItem) {
		this.id = idGrupoItem;
	}

	/**
	 * Gets the idGrupoItem of this GrupoItem.
	 *
	 * @return the idGrupoItem
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the idGrupoItem of this GrupoItem to the specified value.
	 *
	 * @param idGrupoItem
	 *            the new idGrupoItem
	 */
	public void setId(int idGrupoItem) {
		this.id = idGrupoItem;
	}

	/**
	 * Gets the pontuacao of this GrupoItem.
	 *
	 * @return the pontuacao
	 */
	public float getPontuacao() {
		return pontuacao;
	}

	/**
	 * Gets the pontuacao of this GrupoItem.
	 *
	 * @return the pontuacao
	 */
	public float getPontosItem() {
		if(atividade){
			if(tipoPontuacao == QUALITATIVO) {
				AtividadeMapper mapper = (AtividadeMapper) ReflectionUtils.newInstance( itemRelatorioProdutividade.getProducaoMapper() );
				if(mapper.calculaPontuacao(atividadeCorrente) != 0)
					return mapper.calculaPontuacao(atividadeCorrente);
				return atividadeCorrente.getQtdBase()*pontuacao;
			} else
				return pontuacao;
		} else
			return pontuacao;
	}

	/**
	 * Sets the pontuacao of this GrupoItem to the specified value.
	 *
	 * @param pontuacao
	 *            the new pontuacao
	 */
	public void setPontuacao(float pontuacao) {
		this.pontuacao = pontuacao;
	}

	/**
	 * Gets the limitePontuacao of this GrupoItem.
	 *
	 * @return the limitePontuacao
	 */
	public float getLimitePontuacao() {
		return this.limitePontuacao;
	}

	/**
	 * Sets the limitePontuacao of this GrupoItem to the specified value.
	 *
	 * @param limitePontuacao
	 *            the new limitePontuacao
	 */
	public void setLimitePontuacao(float limitePontuacao) {
		this.limitePontuacao = limitePontuacao;
	}

	/**
	 * Gets the grupoRelatorioProdutividade of this GrupoItem.
	 *
	 * @return the grupoRelatorioProdutividade
	 */
	public GrupoRelatorioProdutividade getGrupoRelatorioProdutividade() {
		return this.grupoRelatorioProdutividade;
	}

	/**
	 * Sets the grupoRelatorioProdutividade of this GrupoItem to the specified
	 * value.
	 *
	 * @param grupoRelatorioProdutividade
	 *            the new grupoRelatorioProdutividade
	 */
	public void setGrupoRelatorioProdutividade(
			GrupoRelatorioProdutividade idGrupoRelatorioProdutividade) {
		this.grupoRelatorioProdutividade = idGrupoRelatorioProdutividade;
	}

	/**
	 * Gets the itemRelatorioProdutividade of this GrupoItem.
	 *
	 * @return the itemRelatorioProdutividade
	 */
	public ItemRelatorioProdutividade getItemRelatorioProdutividade() {
		return this.itemRelatorioProdutividade;
	}

	/**
	 * Sets the itemRelatorioProdutividade of this GrupoItem to the specified
	 * value.
	 *
	 * @param itemRelatorioProdutividade
	 *            the new itemRelatorioProdutividade
	 */
	public void setItemRelatorioProdutividade(
			ItemRelatorioProdutividade idItemRelatorioProdutividade) {
		this.itemRelatorioProdutividade = idItemRelatorioProdutividade;
	}

	/**
	 * Determines whether another object is equal to this GrupoItem. The result
	 * is <code>true</code> if and only if the argument is not null and is a
	 * GrupoItem object that has the same id field values as this object.
	 *
	 * @param object
	 *            the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the argument;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		// not set
		if (!(object instanceof GrupoItem)) {
			return false;
		}
		GrupoItem other = (GrupoItem) object;
		if (this.id != other.id
				&& this.itemRelatorioProdutividade.getId() != other.itemRelatorioProdutividade
						.getId())
			return false;
		return true;
	}

	/**
	 * Returns a string representation of the object. This implementation
	 * constructs that representation based on the id fields.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return "br.ufrn.sigaa.prodocente.relatorio.dominio.GrupoItem[idGrupoItem="
				+ id + "]";
	}

	public ListaMensagens validate() {
		return null;
	}

	public Collection<ProducaoIntelectual> getProducoes() {
		return producoes;
	}

	public void setProducoes(Collection<ProducaoIntelectual> producoes) {
		this.producoes = producoes;
	}

	public int getValidade() {
		return validade;
	}

	public void setValidade(int validade) {
		this.validade = validade;
	}

	public boolean isAtividade() {
		return atividade;
	}

	public void setAtividade(boolean atividade) {
		this.atividade = atividade;
	}

	public Collection<ViewAtividadeBuilder> getAtividades() {
		return atividades;
	}

	@SuppressWarnings("unchecked")
	public void setAtividades(Collection atividades) {
		this.atividades = atividades;
	}

	/**
	 * @return the indiceTopico
	 */
	public int getIndiceTopico() {
		return indiceTopico;
	}

	/**
	 * @param indiceTopico
	 *            the indiceTopico to set
	 */
	public void setIndiceTopico(int indiceTopico) {
		this.indiceTopico = indiceTopico;
	}

	public float getAcumuladorPontos() {
		return acumuladorPontos;
	}

	public void setAcumuladorPontos(float acumuladorPontos) {
		this.acumuladorPontos = acumuladorPontos;
	}

	public ViewAtividadeBuilder getAtividadeCorrente() {
		return atividadeCorrente;
	}

	public void setAtividadeCorrente(ViewAtividadeBuilder atividadeCorrente) {
		this.atividadeCorrente = atividadeCorrente;
	}

	/**
	 * @return the tipoPontuacao
	 */
	public int getTipoPontuacao() {
		return tipoPontuacao;
	}

	/**
	 * @param tipoPontuacao the tipoPontuacao to set
	 */
	public void setTipoPontuacao(int tipoPontuacao) {
		this.tipoPontuacao = tipoPontuacao;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
