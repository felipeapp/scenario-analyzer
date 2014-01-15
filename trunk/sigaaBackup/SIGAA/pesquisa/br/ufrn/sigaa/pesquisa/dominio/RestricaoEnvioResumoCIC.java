package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;

@Entity
@Table(name = "restricao_envio_cic", schema = "pesquisa", uniqueConstraints = {})
public class RestricaoEnvioResumoCIC implements PersistDB {

	@Id
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_congresso")
	
	private CongressoIniciacaoCientifica congresso;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cota_bolsa", unique = false, nullable = true, insertable = true, updatable = true)
	private CotaBolsas cotaBolsa;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_bolsa", unique = false, nullable = true, insertable = true, updatable = true)
	private TipoBolsaPesquisa tipoBolsa;
	
	@Column(name = "data_inicial", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataInicial;

	@Column(name = "data_final", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataFinal;

	@Transient
	private int tipoRestricao = 1;
	
	public RestricaoEnvioResumoCIC() {
		congresso = new CongressoIniciacaoCientifica();
		cotaBolsa = new CotaBolsas();
		tipoBolsa = new TipoBolsaPesquisa();
		tipoRestricao = 1;
	}
	
	public boolean isCota(){
		return tipoRestricao == 1;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CotaBolsas getCotaBolsa() {
		return cotaBolsa;
	}

	public void setCotaBolsa(CotaBolsas cotaBolsa) {
		this.cotaBolsa = cotaBolsa;
	}

	public TipoBolsaPesquisa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaPesquisa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public CongressoIniciacaoCientifica getCongresso() {
		return congresso;
	}

	public void setCongresso(CongressoIniciacaoCientifica congresso) {
		this.congresso = congresso;
	}

	public int getTipoRestricao() {
		return tipoRestricao;
	}

	public void setTipoRestricao(int tipoRestricao) {
		this.tipoRestricao = tipoRestricao;
	}

	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public static boolean compareTo(List<RestricaoEnvioResumoCIC> restricoes, RestricaoEnvioResumoCIC restricao) {
		for (RestricaoEnvioResumoCIC rest : restricoes) {
			if ( ( rest.getId() == restricao.getId() && rest.getId() != 0 && restricao.getId() != 0 ) 
					|| ( rest.getTipoBolsa() != null && restricao.getTipoBolsa() != null && rest.getTipoBolsa().getId() == restricao.getTipoBolsa().getId() )
					|| ( rest.getCotaBolsa() != null && restricao.getCotaBolsa() != null && rest.getCotaBolsa().getId() == restricao.getCotaBolsa().getId() ) )
				return true;
		}
		return false;	
	}

	public String getDescricao() {
		if(cotaBolsa != null)
			return "O plano está vinculado à cota " + cotaBolsa.getDescricaoCompleta();
		if(tipoBolsa != null) {
			Formatador f = Formatador.getInstance();
			return "O plano é da modalidade " + tipoBolsa.getDescricaoResumida() + " com vigência entre " +
					f.formatarData(dataInicial) + " e " +  f.formatarData(dataFinal) + ", inclusive.";
		}
		return "";
	}
}