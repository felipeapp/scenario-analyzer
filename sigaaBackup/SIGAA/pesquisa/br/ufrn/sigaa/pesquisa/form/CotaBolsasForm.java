/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;

/**
 * Form utilizado para o cadastro de manuten��o dos per�odos de cotas de bolsas,
 * que determinam a dura��o dos planos de trabalho
 *
 * @author Ricardo Wendell
 *
 */
public class CotaBolsasForm extends SigaaForm<CotaBolsas> {

	String dataInicio;

	String dataFim;

	public CotaBolsasForm() {
		obj = new CotaBolsas();
	}

	public String getDataFim() {
		CotaBolsas cota = obj;
		if(cota.getFim() != null)
			dataFim = Formatador.getInstance().formatarData( cota.getFim() );
		return dataFim;
	}

	public String getDataInicio() {
		CotaBolsas cota = obj;
		if(cota.getInicio() != null)
			dataInicio = Formatador.getInstance().formatarData( cota.getInicio() );
		return dataInicio;
	}

	public void setDataFim(String dataFim) {
		CotaBolsas cota = obj;
		cota.setFim(parseDate(dataFim));
		this.dataFim = dataFim;
	}

	public void setDataInicio(String dataInicio) {
		CotaBolsas cota = obj;
		cota.setInicio(parseDate(dataInicio));
		this.dataInicio = dataInicio;
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {

		Formatador f = Formatador.getInstance();

		// Validar descri��o
		if (obj.getDescricao() == null || "".equals(obj.getDescricao().trim())) {
			addMensagemErro("� necess�rio informar uma descri��o para o per�odo", req);
		} else {
			obj.setDescricao(obj.getDescricao().trim().toUpperCase());
		}

		obj.setInicio(f.parseDate(dataInicio));
		if (obj.getInicio() == null) {
			addMensagemErro("A data de in�cio do per�odo � inv�lida", req);
		}
		obj.setFim(f.parseDate(dataFim));
		if (obj.getFim() == null) {
			addMensagemErro("A data de fim do per�odo � inv�lida", req);
		}
		if (obj.getInicio() != null && obj.getFim() != null && obj.getInicio().after(obj.getFim())) {
			addMensagemErro("O per�odo informado � inv�lido", req);
		}
	}

}
