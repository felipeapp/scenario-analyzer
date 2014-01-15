/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Processador que remove uma frequ�ncia de peri�dico do sistema </p>
 *
 * <p> <i> Deve migrar todos as assinaturas da frequ�ncia antiga para a nova frequ�ncia passada</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRemoveFrequenciaPeriodico extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
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
			
			
			// Migra os materias da cole��o removida para nova //
			dao.update("UPDATE biblioteca.assinatura SET id_frequencia_periodicos = ? WHERE id_frequencia_periodicos  = ? ", new Object[]{frequenciaParaMigraAssinaturas.getId(), frequenciaASerRemovida.getId()});
			
		}finally{
			if(dao != null) dao.close();
		}
			
		return null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
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
			erros.addErro("Escolha a frequ�ncia para a qual as assinaturas ser�o migradas");
		else
			if(! frequenciaASerRemovida.isAtivo())
				erros.addErro("A frequ�ncia j� foi removida.");
		
		
		if (frequenciaParaMigraAssinaturas != null && frequenciaASerRemovida != null && frequenciaASerRemovida.getId() == frequenciaParaMigraAssinaturas.getId())
			erros.addErro("As assinaturas n�o podem ser migrados para a mesma frequ�ncia que est� sendo removida.");

		checkValidation(erros);
		
	}

	
}
