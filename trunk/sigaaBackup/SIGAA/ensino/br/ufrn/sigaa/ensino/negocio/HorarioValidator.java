package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.form.HorarioForm;

/**
 * Classe que realiza a validação padrão de objetos Horario.
 * 
 * @author amdantas
 *
 */
public class HorarioValidator {

	public static void validarHorario(Horario h, HorarioForm form, ListaMensagens lista) {
		Horario horario = h;

		horario.setInicio(ValidatorUtil.validaHora(form.getInicio(), "Hora Inicio", lista));
		horario.setFim(ValidatorUtil.validaHora(form.getFim(), "Hora Fim", lista));

	}

}
