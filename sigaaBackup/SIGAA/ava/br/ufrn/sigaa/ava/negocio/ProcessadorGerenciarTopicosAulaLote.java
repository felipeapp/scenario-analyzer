/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 30/10/2009
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dominio.TopicoAula;

/**
 * Processador que cadastra todos os novos t�picos de aula e edita todos os t�picos de aula 
 * de turma virtual.
 * 
 * @author Fred de Castro
 *
 */
public class ProcessadorGerenciarTopicosAulaLote extends AbstractProcessador{

	/**
	 * Salva todos os t�picos de aula passados.
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoGerenciaTopicosAula personalMov = (MovimentoGerenciaTopicosAula) mov;
		
		validate(personalMov);
		
		List <TopicoAula> topicos = personalMov.getTopicosAula();
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO(personalMov);
			for (TopicoAula t : topicos)
				dao.createOrUpdate(t);
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	/**
	 * Verifica se h� pelo menos um t�pico de aula a salvar ou alterar.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoGerenciaTopicosAula personalMov = (MovimentoGerenciaTopicosAula) mov;
		
		if (personalMov.getTopicosAula().isEmpty())
			throw new NegocioException ("N�o h� t�picos a cadastrar.");
	}
}