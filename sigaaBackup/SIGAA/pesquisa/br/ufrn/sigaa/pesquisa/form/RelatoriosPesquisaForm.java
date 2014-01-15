/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/04/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.struts.SigaaForm;

/**
 * Form utilizado para geração de relatórios do modulo de pesquisa
 *
 * @author ricardo
 *
 */
@SuppressWarnings("unchecked")
public class RelatoriosPesquisaForm extends SigaaForm {

	private int anoInicio;

	private int anoFim;

	public int getAnoFim() {
		return anoFim;
	}

	public int getAnoInicio() {
		return anoInicio;
	}

	public void setAnoFim(int anoFim) {
		this.anoFim = anoFim;
	}

	public void setAnoInicio(int anoInicio) {
		this.anoInicio = anoInicio;
	}

	public void validarAnos(ListaMensagens erros) {
		ValidatorUtil.validaInt(getAnoInicio(), "Ano inicial", erros);
		ValidatorUtil.validaInt(getAnoFim(), "Ano final", erros);
	}

}
