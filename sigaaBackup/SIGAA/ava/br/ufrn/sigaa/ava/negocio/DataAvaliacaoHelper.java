package br.ufrn.sigaa.ava.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;

/**
 * Classe auxiliar para Datas de Avalia��o.
 * 
 * @author Diego J�come
 *
 */
public class DataAvaliacaoHelper {

	/**
	 * Cria uma Data de Avalia��o de acordo com os dados da tarefa passado como par�metro.
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
	 * Cria uma Data de Avalia��o de acordo com os dados do question�rio passado como par�metro.
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
