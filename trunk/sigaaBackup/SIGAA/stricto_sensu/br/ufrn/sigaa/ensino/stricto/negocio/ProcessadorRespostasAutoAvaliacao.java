/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 24/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ensino.stricto.dominio.RespostasAutoAvaliacao;
import br.ufrn.sigaa.questionario.negocio.ProcessadorQuestionarioRespostas;

/**
 * Processado responsável por persistir as respostas da Auto Avaliação da Pós Graduação.
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorRespostasAutoAvaliacao extends AbstractProcessador{

	/** Persite as respostas da Auto Avaliação da Pós Graduação.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		ProcessadorQuestionarioRespostas procRespostas = new ProcessadorQuestionarioRespostas();
		RespostasAutoAvaliacao respostasAutoAvaliacao = ((MovimentoCadastro)mov).getObjMovimentado();
		procRespostas.cadastrarRespostas(mov, respostasAutoAvaliacao.getRespostas(), false);
		GenericDAO dao = getGenericDAO(mov);
		try {
			dao.createOrUpdate(respostasAutoAvaliacao);
		} finally {
			dao.close();
		}
		return respostasAutoAvaliacao;
	}

	/** Valida os dados antes de persistir
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
