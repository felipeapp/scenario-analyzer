package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.metropoledigital.dao.LancamentoNotasDisciplinaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.NotaIMD;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Classe responsável relatórios para os discentes do Curso Técnico do IMD.
 * 
 * @author Rafael Silva
 *
 */

@Component("relatoriosDiscentesIMD") @Scope("request")
public class RelatoriosDiscentesIMDMbean extends SigaaAbstractController{
	/**Redireciona para a pággina que irá exibir as notas de PT, AE e PE do discente na disciplina selecionadas.*/
	public static final String JSP_NOTAS_DISCIPLINA = "/metropole_digital/relatorios/portal_discente/view_notas_disciplina.jsp";
	
	/**Notas do discente na disciplina selecionada*/
	public NotaIMD notasDisciplina;
	
	/**Disciplina ao qual o aluno está vinculado*/
	public Turma turmaDisciplina;
	
	
	/**
	 * Gera o relatório de notas do aluno para uma determinada disciplina;
	 * 
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String initRelatorioNotasDisciplina(Turma  turma) throws DAOException{
		LancamentoNotasDisciplinaDao dao = getDAO(LancamentoNotasDisciplinaDao.class);		
		Turma disciplina = getDAO(TurmaDao.class).findByPrimaryKey(turma.getId());
		TurmaEntradaTecnico t = getDAO(TurmaEntradaTecnicoDao.class).findTurmaEntradaByEspecializacao(disciplina.getEspecializacao().getId());
		turmaDisciplina = turma;
		
		//recupera as notas dos discentes na disciplina selecionada. Rotina não otimizada;
		List<NotaIMD> listaNotasDiscentes = dao.findNotasAlunos(t.getId(), disciplina);
		
		int idDiscente = getUsuarioLogado().getDiscente().getDiscente().getId();
		for (NotaIMD l : listaNotasDiscentes) {
			if (idDiscente==l.getDiscente().getId()) {
				notasDisciplina = l;
				break;
			}			
		}		
		return forward(JSP_NOTAS_DISCIPLINA);
	}

	//GETTERS AND SETTERS
	public NotaIMD getNotasDisciplina() {
		return notasDisciplina;
	}

	public void setNotasDisciplina(NotaIMD notasDisciplina) {
		this.notasDisciplina = notasDisciplina;
	}

	public Turma getTurmaDisciplina() {
		return turmaDisciplina;
	}

	public void setTurmaDisciplina(Turma turmaDisciplina) {
		this.turmaDisciplina = turmaDisciplina;
	}		
}
