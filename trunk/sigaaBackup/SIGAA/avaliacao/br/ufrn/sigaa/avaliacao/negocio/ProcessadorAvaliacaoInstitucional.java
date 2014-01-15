/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 30/04/2008
 */
package br.ufrn.sigaa.avaliacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta;

/**
 * Processador para gravar respostas dos discentes e docentes 
 * da avaliação institucional.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorAvaliacaoInstitucional extends AbstractProcessador {

	/** Persiste a Avaliação Institucional.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		AvaliacaoInstitucional aval = ((MovimentoCadastro) mov).getObjMovimentado();
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class, mov);
		GenericDAO genericDAO = getGenericDAO(mov);
				
		try {
			if (aval.getId() == 0) {
				dao.create(aval);
			} else {
				AvaliacaoInstitucional avalDb = genericDAO.findByPrimaryKey(aval.getId(), AvaliacaoInstitucional.class);
				if (avalDb != null) {
					// Deletar todas as respostas
					dao.removerRespostas(aval);
					// Deletar todos os trancamentos
					dao.removerTrancamentos(aval);
					// Deletar todas as observações
					dao.removerObservacoes(aval);
		
					dao.update(aval);
				}
			}
		} finally {
			dao.close();
			genericDAO.close();
		}
		
		return null;
	}

	/** Valida a Avaliação Institucional.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		AvaliacaoInstitucional aval = ((MovimentoCadastro) mov).getObjMovimentado();
		
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class, mov);
		int ano = aval.getAno();
		int periodo = aval.getPeriodo();
		if (isEmpty(aval.getFormulario()))
			throw new NegocioException("A Avaliação não possui a indicação do formulário que foi preenchido.");
		try {
		
			AvaliacaoInstitucional obj = null;
			if (aval.getDiscente() != null)
				obj = dao.findByDiscente(aval.getDiscente(), ano, periodo, aval.getFormulario());
			else if (aval.getServidor() != null)
				obj = dao.findByDocente(aval.getServidor(), ano, periodo, aval.getFormulario());
			else
				obj = dao.findByDocenteExterno(aval.getDocenteExterno(), ano, periodo, aval.getFormulario());
			
			if (obj != null && obj.isFinalizada()) throw new NegocioException("Avaliação já realizada!");
			
			if (aval.isFinalizada()) {
				for (RespostaPergunta resposta : aval.getRespostas()) {
					if ( !isEmpty(resposta.getDocenteTurma()) && resposta.getResposta() == null)
						throw new NegocioException("Nem todas as perguntas foram respondidas. É necessário responder todas as perguntas para finalizar a avaliação.");
				}
			}
		
		} finally {
			dao.close();
		}
		
	}

}
