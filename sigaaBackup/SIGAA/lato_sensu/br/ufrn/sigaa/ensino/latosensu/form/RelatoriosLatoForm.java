/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/04/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.form;

import java.util.ArrayList;

import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.struts.SigaaForm;

/**
 * Form para geração de relatórios do módulo lato sensu
 * 
 * @author Leonardo
 *
 */
@Deprecated
public class RelatoriosLatoForm extends SigaaForm {

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

	public void validarAnos(ArrayList<MensagemAviso> erros) {
		ValidatorUtil.validaInt(getAnoInicio(), "Ano inicial", erros);
		ValidatorUtil.validaInt(getAnoFim(), "Ano final", erros);
	}
}
