package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * 
 * Entidade que armazena o total de vagas por cada um dos grupos de reservas nas opções/polo/grupo.
 * 
 * @author Gleydson Lima, Rafael Barros
 *
 */

@Entity
@Table(name = "reserva_vaga_grupo", schema = "metropole_digital")
public class ReservaVagaGrupo implements PersistDB, Validatable {

	/**
     * Chave primária
     */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.id_reserva_vaga_grupo") })
	@Column(name = "id_reserva_vaga_grupo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/**
     * Denominação nominal para identificar o grupo de reserva de vagas
     */
	private String denominacao;
	
	/**
     * Descrição nominal mais detalhada para identificar o grupo de reserva de vagas
     */
	private String descricao;
	
	/**
     * Ordem de prioridade em que o grupo irá exercer no processo seletivo
     */
	private int prioridade;
	
	/**
     * Atributo que indica se o grupo é destinado ou não para candidados que estudaram em escola pública
     */
	@Column(name="escola_publica")
	private boolean escolaPublica;
	
	/**
     * Atributo que indica se o grupo é destinado ou não para candidados possuem baixa renda
     */
	@Column(name="baixa_renda")
	private boolean baixaRenda;
	
	/**
     * Atributo que indica se o grupo é destinado ou não para candidados que se enquadram em cota de PNI (pardos, negros e índios)
     */
	private boolean etnia;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

	public boolean isEscolaPublica() {
		return escolaPublica;
	}

	public void setEscolaPublica(boolean escolaPublica) {
		this.escolaPublica = escolaPublica;
	}

	public boolean isBaixaRenda() {
		return baixaRenda;
	}

	public void setBaixaRenda(boolean baixaRenda) {
		this.baixaRenda = baixaRenda;
	}

	public boolean isEtnia() {
		return etnia;
	}

	public void setEtnia(boolean etnia) {
		this.etnia = etnia;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
