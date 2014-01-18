package br.ufrn.sigaa.ensino.metropoledigital.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;

/**
 * Movimento utilizado no cadastro das notas dos alunos em uma determinada disciplina.
 * 
 * @author Rafael Silva
 *
 */
public class MovimentoCadastroNotasDisciplina extends AbstractMovimentoAdapter{
	/**Notas dos alunos na disciplina informada*/
	private List<NotaIMD> notasIMD;

	//GETTERS AND SETTERS
	public List<NotaIMD> getNotasIMD() {
		return notasIMD;
	}

	public void setNotasIMD(List<NotaIMD> notasIMD) {
		this.notasIMD = notasIMD;
	}
}
