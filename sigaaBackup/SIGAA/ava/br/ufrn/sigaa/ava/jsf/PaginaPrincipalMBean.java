/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.TopicoAula;

/**
 * Controlador da p�gina principal da turma virtual
 */
@Component("principalTurma") @Scope("request")
public class PaginaPrincipalMBean extends ControllerTurmaVirtual {

	private List<TopicoAula> topicos;

	TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);

	/**
	 * Carrega os dados da turma virtual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /portais/turma/turma.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaDados() throws DAOException {

		setSubSistemaAtual(SigaaSubsistemas.PORTAL_TURMA);

		topicos = getAulas();

		// carrega conte�dos
		List<ConteudoTurma> conteudos = dao.findConteudoTurma(turma());
		for (ConteudoTurma t : conteudos) {
			TopicoAula aulaCorrespondente = topicos.get(topicos.indexOf(t.getAula()));
			aulaCorrespondente.getMateriais().add(t);
		}

		// carrega links
		Collection<IndicacaoReferencia> referencias = dao.findLinksByTurma(turma());
		for (IndicacaoReferencia r : referencias) {
			if (r.getAula() != null) {
				TopicoAula aulaCorrespondente = topicos.get(topicos.indexOf(r.getAula()));
				aulaCorrespondente.getMateriais().add(r);
			}
		}

		// carrega arquivos
		Collection<ArquivoTurma> arquivos = dao.findArquivosByTurma(turma().getId());
		for (ArquivoTurma a : arquivos) {
			TopicoAula aulaCorrespondente = topicos.get(topicos.indexOf(a.getAula()));
			aulaCorrespondente.getMateriais().add(a);
		}

		// carrega tarefas
		//Collection<TarefaTurma> tarefas = dao.findByExactField(TarefaTurma.class, "aula.turma.id", turma().getId());
		//TurmaDao turmaDao = (TurmaDao) getDAO(TurmaDao.class); 
		/*Collection<TarefaTurma> tarefas = turmaDao.findByTurma(turma());
		for (TarefaTurma a : tarefas) {
			TopicoAula aulaCorrespondente = topicos.get(topicos.indexOf(a.getAula()));
			aulaCorrespondente.getMateriais().add(a);
		}*/

		return "";

	}

	private List<TopicoAula> getAulas() throws DAOException {

		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class);
		Collection<TopicoAula> aulas = dao.findAulasByTurma(turma());

		List<TopicoAula> result = new ArrayList<TopicoAula>();
		// id e refer�ncia da cole��o
		Map<Integer, Collection<TopicoAula>> referenciaColecao = new HashMap<Integer, Collection<TopicoAula>>();
		// Constr�i a �rvore de aulas
		for (TopicoAula aula : aulas) {
			referenciaColecao.put(aula.getId(), aula.getTopicosFilhos());
			if (aula.getTopicoPai() == null) {
				result.add(aula);
			} else {
				if (referenciaColecao.get(aula.getTopicoPai().getId()) == null)
					referenciaColecao.put(aula.getTopicoPai().getId(), new ArrayList<TopicoAula>());
				if (!referenciaColecao.get(aula.getTopicoPai().getId()).contains(aula))
					referenciaColecao.get(aula.getTopicoPai().getId()).add(aula);
			}
		}
		// Coloca o n�vel de profundidade de cada t�pico para poder mostrar na
		// view
		List<TopicoAula> resultComNivel = new ArrayList<TopicoAula>();
		percorreNivel(result, resultComNivel, 0);

		return resultComNivel;
	}

	/**
	 * M�todo respons�vel por percorrer uma cole��o de t�picos de aula setando os seus nive�s 
	 * de acordo com o n�vel passado por par�metro.
	 * @param filhas
	 * @param resultComNivel
	 * @param nivel
	 */
	private void percorreNivel(List<TopicoAula> filhas,
			Collection<TopicoAula> resultComNivel, int nivel) {
		for (TopicoAula aula : filhas) {
			aula.setNivel(nivel);
			resultComNivel.add(aula);
			if (aula.getTopicosFilhos().size() > 0) {
				percorreNivel(aula.getTopicosFilhos(), resultComNivel, ++nivel);
				nivel--;
			}
		}
	}

}
