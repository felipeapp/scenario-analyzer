/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 03/05/2012
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;

/**
 * Helper utilizado em alguns processamentos que envolvem mensalidades de curso
 * lato tais como, por exemplo, listar as datas de vencimento das GRU a partir
 * de um curso.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class MensalidadeCursoLatoHelper {

	/**
	 * Retorna uma coleção ordenada de datas de vencimento das GRU, calculadas a
	 * partir da data de início de um curso.
	 * 
	 * @param curso
	 * @return
	 */
	public static List<Date> getDatasVencimento(CursoLato curso) {
		if (isEmpty(curso) || isEmpty(curso.getDataInicio()) || isEmpty(curso.getQtdMensalidades()))
			return null;
		List<Date> lista = new LinkedList<Date>();
		Date vencimento = curso.getDataPrimeiraMensalidade() == null ? curso.getDataInicio() : curso.getDataPrimeiraMensalidade();
		int diaVencimento = curso.getDataPrimeiraMensalidade() == null ?
				ParametroHelper.getInstance().getParametroInt(ParametrosLatoSensu.DATA_VENCIMENTO_MENSALIDADE) :
				CalendarUtils.getDiaByData(curso.getDataPrimeiraMensalidade());
		if (CalendarUtils.getDiaByData(vencimento) > diaVencimento)
			vencimento = CalendarUtils.adicionaMeses(vencimento, 1);
		vencimento = CalendarUtils.adicionaDias(vencimento, diaVencimento - CalendarUtils.getDiaByData(vencimento));
		for (int i = 1; i <= curso.getQtdMensalidades(); i++) {
			lista.add(vencimento);
			vencimento = CalendarUtils.adicionaMeses(vencimento, 1);
		}
		return lista;
	}
}
