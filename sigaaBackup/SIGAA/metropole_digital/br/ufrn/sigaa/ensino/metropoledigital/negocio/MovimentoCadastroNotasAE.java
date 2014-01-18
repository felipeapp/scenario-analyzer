package br.ufrn.sigaa.ensino.metropoledigital.negocio;


import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.RetornoNotaAEIntegracaoMoodle;

/**
 * Movimento utilizado para realizar o cadastro das 
 * notas das Atividades Executadas(AE) sincronizadas com o moodle.
 * 
 * @author Rafael Silva
 *
 */
public class MovimentoCadastroNotasAE extends AbstractMovimentoAdapter{
	/** Lista dos alunos vinculados a disciplina no SIGAA.*/
	List<NotaIMD> listaNotasIMD = new ArrayList<NotaIMD>();
	/** Lista dos alunos vinculados a disciplina no Moodle e suas respectivas notas de AE*/
	List<RetornoNotaAEIntegracaoMoodle> listaRetornoMoodle = new ArrayList<RetornoNotaAEIntegracaoMoodle>();
	
	//GETTERS AND SETTERS
	public List<NotaIMD> getListaNotasIMD() {
		return listaNotasIMD;
	}
	public void setListaNotasIMD(List<NotaIMD> listaNotasIMD) {
		this.listaNotasIMD = listaNotasIMD;
	}
	public List<RetornoNotaAEIntegracaoMoodle> getListaRetornoMoodle() {
		return listaRetornoMoodle;
	}
	public void setListaRetornoMoodle(
			List<RetornoNotaAEIntegracaoMoodle> listaRetornoMoodle) {
		this.listaRetornoMoodle = listaRetornoMoodle;
	}
}
