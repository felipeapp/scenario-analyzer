package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.Horario;

/**
 * Processador que realiza a validação e persistência de objetos Horario
 * 
 * @author amdantas
 *
 */
public class ProcessadorHorario extends ProcessadorCadastro {

	/**
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		Horario horario = (Horario) mov;
		validate(horario);

		// Dizer que o horário eh ativo
		horario.setAtivo(true);

		GenericDAO dao = getGenericDAO(mov);
		try {
			if (horario.getCodMovimento().equals(SigaaListaComando.CADASTRAR_HORARIO)) {
				dao.create(horario);
			} else if (horario.getCodMovimento().equals(SigaaListaComando.ALTERAR_HORARIO)) {
				dao.update(horario);
			}
		} finally {
			dao.close();
		}
		return null;
	}

	/**
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		Horario horario = (Horario) mov;
		// verificar se esse horário choca com algum já cadastrado pra essa
		// unidade
		HorarioDao dao = getDAO(HorarioDao.class, mov);
		ListaMensagens erros = new ListaMensagens();
		try {
			
			Horario horarioNoBanco =  dao.findByUnidade( horario.getUnidade().getId(), horario.getNivel(), horario.getTurnoChar(), String.valueOf(horario.getOrdem()).charAt(0));
			if(horarioNoBanco!= null && horario.getCodMovimento().equals(SigaaListaComando.CADASTRAR_HORARIO)) {
				erros.addErro("Não é possível cadastrar dois horários com a mesma ordem para um mesmo turno, unidade e nível.");
			}
			
			if (horario.getId() > 0 && !dao.permiteAlteracao(horario)) {
				erros.addErro("Esse horário não pode ser alterado pois existem turmas que o utilizam.");
			}
			if (erros.isEmpty() && dao.verificaChoqueHorario(horario)) {
				erros.addErro("Esse horário não pode ser cadastrado. Ocorreu um conflito com os horários já existentes.");
			}
		} finally {
			dao.close();
		}

		checkValidation(erros);

	}

}
