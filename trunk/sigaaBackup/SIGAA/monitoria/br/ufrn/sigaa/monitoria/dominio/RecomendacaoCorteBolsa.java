package br.ufrn.sigaa.monitoria.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.EqualsUtil;

/*******************************************************************************
 * <p>
 * Classe que representa uma recomendação para um corte de bolsa, feita por um
 * membro da comissão de monitoria no momento da avaliação de um relatório de
 * projeto.
 * </p>
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "recomendacao_corte_bolsa", schema = "monitoria")
public class RecomendacaoCorteBolsa implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_recomendacao_corte_bolsa")
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada = new RegistroEntrada();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_monitoria")
	private DiscenteMonitoria discenteMonitoria = new DiscenteMonitoria();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_avaliacao_relatorio_projeto")
	private AvaliacaoRelatorioProjeto avaliacaoRelatorioProjeto = new AvaliacaoRelatorioProjeto();

	@Column(name = "justificativa")
	private String justificativa;

	public AvaliacaoRelatorioProjeto getAvaliacaoRelatorioProjeto() {
		return avaliacaoRelatorioProjeto;
	}

	public void setAvaliacaoRelatorioProjeto(
			AvaliacaoRelatorioProjeto avaliacaoRelatorioProjeto) {
		this.avaliacaoRelatorioProjeto = avaliacaoRelatorioProjeto;
	}

	public DiscenteMonitoria getDiscenteMonitoria() {
		return discenteMonitoria;
	}

	public void setDiscenteMonitoria(DiscenteMonitoria discenteMonitoria) {
		this.discenteMonitoria = discenteMonitoria;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return EqualsUtil.testEquals(this, obj, "discenteMonitoria");
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	
	
}
