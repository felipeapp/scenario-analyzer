/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 03/05/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.negocio.helper.InscricaoAtividadeHelper;

/**
*
* <p> Classe que cria a GRU para o pagamento dos cursos e eventos de extensão.</p>
* 
* @author jadson
*
*/
public class ProcessadorCriaGRUCursosEventosExtensao extends ProcessadorCadastro{

	@Override
	public Object execute(Movimento movimento) throws NegocioException,ArqException, RemoteException {
		
		
		MovimentoCadastro movi = (MovimentoCadastro) movimento;
		
		InscricaoAtividadeParticipante inscricao = (InscricaoAtividadeParticipante) movi.getObjMovimentado();
		
		// Cria a gru e salva no banco comum //
		GuiaRecolhimentoUniao gru = InscricaoAtividadeHelper.criaGRUPagamentoIncricaoCursosEvento(inscricao);
		
		// Atribui a GRU à inscrição passada e atualiza esse campo no banco //
		inscricao.setIdGRUPagamento( gru.getId());
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movimento);
			dao.updateField(InscricaoAtividadeParticipante.class, inscricao.getId(), "idGRUPagamento", inscricao.getIdGRUPagamento());
		}finally{
			if(dao != null)  dao.close();
		}
		
		return inscricao;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		// Não tem validações a fazer
	}
}
