/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FrequenciaPeriodicos;

/**
 *
 * <p>Processador que remove uma frequência de periódico do sistema </p>
 *
 * <p> <i> Deve migrar todos as assinaturas da frequência antiga para a nova frequência passada</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRemoveFrequenciaPeriodico extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		MovimentoRemoveFrequenciaPeriodicos movimento = (MovimentoRemoveFrequenciaPeriodicos) mov;
		
		GenericDAO dao = null;
		
		try{
			
			dao =  getGenericDAO(mov);
			
			FrequenciaPeriodicos frequenciaASerRemovida = movimento.getFrequenciaASerRemovida();
			FrequenciaPeriodicos frequenciaParaMigraAssinaturas = movimento.getFrequenciaParaMigraAssinaturas();
			dao.updateField(FrequenciaPeriodicos.class, frequenciaASerRemovida.getId(), "ativo", false);
			
			
			// Migra os materias da coleção removida para nova //
			dao.update("UPDATE biblioteca.assinatura SET id_frequencia_periodicos = ? WHERE id_frequencia_periodicos  = ? ", new Object[]{frequenciaParaMigraAssinaturas.getId(), frequenciaASerRemovida.getId()});
			
		}finally{
			if(dao != null) dao.close();
		}
			
		return null;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		ListaMensagens erros = new ListaMensagens();

		MovimentoRemoveFrequenciaPeriodicos movimento = (MovimentoRemoveFrequenciaPeriodicos) mov;

		FrequenciaPeriodicos frequenciaASerRemovida = movimento.getFrequenciaASerRemovida();
		FrequenciaPeriodicos frequenciaParaMigraAssinaturas = movimento.getFrequenciaParaMigraAssinaturas();

		if (frequenciaParaMigraAssinaturas == null || frequenciaParaMigraAssinaturas.getId() == -1)
			erros.addErro("Escolha a frequência para a qual as assinaturas serão migradas");
		else
			if(! frequenciaASerRemovida.isAtivo())
				erros.addErro("A frequência já foi removida.");
		
		
		if (frequenciaParaMigraAssinaturas != null && frequenciaASerRemovida != null && frequenciaASerRemovida.getId() == frequenciaParaMigraAssinaturas.getId())
			erros.addErro("As assinaturas não podem ser migrados para a mesma frequência que está sendo removida.");

		checkValidation(erros);
		
	}

	
}
