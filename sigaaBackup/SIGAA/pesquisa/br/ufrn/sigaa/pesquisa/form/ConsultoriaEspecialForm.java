/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/05/2007
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.pesquisa.dominio.ConsultoriaEspecial;

/**
 * Form para cadastro de controle de consultoria especial
 *
 * @author Ricardo Wendell
 *
 */
public class ConsultoriaEspecialForm extends SigaaForm<ConsultoriaEspecial> {

	private String dataInicio;

	private String dataFim;

	public ConsultoriaEspecialForm() {
		this.obj = new ConsultoriaEspecial();
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {
		super.validate(req);

		if ( getObj().getConsultor().getId() <= 0 ) {
			addMensagemErro(" � necess�rio informar o consultor ", req);
		}

		validaData(dataInicio, "Data de in�cio", req);
		validaData(dataFim, "Data final", req);

		if ( getObj().getDataInicio() != null && getObj().getDataFim() != null && getObj().getDataInicio().getTime() > getObj().getDataFim().getTime() ) {
			addMensagemErro("O per�odo da final da Consultoria deve ser posterior ao per�odo inicial da mesma.", req);
		}

	}

	public String getDataFim() {
		if (dataFim == null && getObj() != null && getObj().getDataFim() != null ) {
			dataFim = Formatador.getInstance().formatarData(getObj().getDataFim());
		}
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
		this.obj.setDataFim( Formatador.getInstance().parseDate( dataFim ) );
	}

	public String getDataInicio() {
		if (dataInicio == null && getObj() != null && getObj().getDataInicio() != null ) {
			dataInicio = Formatador.getInstance().formatarData(getObj().getDataInicio());
		}
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
		this.obj.setDataInicio( Formatador.getInstance().parseDate( dataInicio ) );
	}

}
