/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/11/2006'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.Calendar;
import java.util.Date;

import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * Esta classe representa um item de Produção Intelectual do Relatório do
 * Prodocente
 *
 * @author Gleydson, Eric
 *
 */
public class ProducaoIntelectual extends AbstractAvaliacaoDocente {

	private String nomeAtividade;

	private Date data;

	/** objeto de produção intelectual */
	private Producao producao;

	public Date getData() {
		return data;
	}

	public Integer getAno() {
		if ( getAnoVigencia() == 0 && getData() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(getData());
			return c.get(Calendar.YEAR);
		}
		return getAnoVigencia();
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getNomeAtividade() {
		return nomeAtividade;
	}

	public void setNomeAtividade(String nomeAtividade) {
		this.nomeAtividade = nomeAtividade;
	}

	public Producao getProducao() {
		return producao;
	}

	public void setProducao(Producao producao) {
		this.producao = producao;
	}

	public boolean isValidado() {
		return producao.getValidado() != null && producao.getValidado();
	}
}
