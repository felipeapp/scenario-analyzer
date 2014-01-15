package br.ufrn.sigaa.ava.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;

/**
 * Classe auxiliar para Datas de Avaliação.
 * 
 * @author Diego Jácome
 *
 */
public class DataAvaliacaoHelper {

	/**
	 * Cria uma Data de Avaliação de acordo com os dados da tarefa passado como parâmetro.
	 * 
	 * @param parecer
	 * @return
	 * @throws DAOException 
	 */
	public static DataAvaliacao criar(TarefaTurma tt, List<Integer> tarefasRespondidas) {
		DataAvaliacao da = new DataAvaliacao ();
		
		da.setData(tt.getDataEntrega());
		da.setTurma(tt.getAula().getTurma());
		da.setIdTarefa(tt.getId());
		da.setDescricao(tt.getNome());
		da.setHoraEntrega(tt.getHoraEntrega());
		da.setMinutoEntrega(tt.getMinutoEntrega());
		
		if (!isEmpty(tarefasRespondidas) && tarefasRespondidas.contains(tt.getId()))
			da.setConcluida(true);
		
		return da;
	}
	
	/**
	 * Cria uma Data de Avaliação de acordo com os dados do questionário passado como parâmetro.
	 * 
	 * @param parecer
	 * @return
	 * @throws DAOException 
	 */
	public static DataAvaliacao criar(QuestionarioTurma qq) {
		DataAvaliacao da = new DataAvaliacao ();
		
		da.setData(qq.getFim());
		da.setTurma(qq.getAula().getTurma());
		da.setIdQuestionario(qq.getId());
		da.setDescricao(qq.getNome());
		da.setHoraEntrega(qq.getHoraFim());
		da.setMinutoEntrega(qq.getMinutoFim());
		
		if (!qq.getRespostas().isEmpty())
			da.setConcluida(true);
		
		return da;
	}
	
}
