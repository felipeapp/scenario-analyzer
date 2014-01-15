/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/04/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;

/**
*
* <p>Processador que remove uma situação do sistema </p>
*
* <p> <i> Deve migrar todos os materiais para a nova situação passada</i> </p>
* 
* @author jadson
*
*/
public class ProcessadorRemoveSituacao extends AbstractProcessador{
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		MovimentoRemoveSituacao movimento = (MovimentoRemoveSituacao) mov;
		
		SituacaoMaterialInformacionalDao dao = null;
		
		try{
			
			dao =  getDAO(SituacaoMaterialInformacionalDao.class, mov);
			
			SituacaoMaterialInformacional situacao = movimento.getSituacao();
			SituacaoMaterialInformacional novaSituacao = movimento.getNovaSituacao();
			dao.updateField(SituacaoMaterialInformacional.class, situacao.getId(), "ativo", false);
			
			
			// Migra os materias da situção removida para nova //
			dao.update("UPDATE biblioteca.material_informacional SET id_situacao_material_informacional = ? WHERE id_situacao_material_informacional  = ? ", new Object[]{novaSituacao.getId(), situacao.getId()});
			
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

		MovimentoRemoveSituacao movimento = (MovimentoRemoveSituacao) mov;

		SituacaoMaterialInformacional situacaoASerRemovida = movimento.getSituacao();
		SituacaoMaterialInformacional novaSituacao = movimento.getNovaSituacao();

		if (novaSituacao == null || novaSituacao.getId() == -1)
			erros.addErro("Escolha a situação para a qual os materiais serão migrados");
		else{
			
			GenericDAO dao = null;
			
			try{
				dao = getGenericDAO(movimento);
				
				SituacaoMaterialInformacional situacaoASerRemovidaBanco 
					= dao.findByPrimaryKey(situacaoASerRemovida.getId(), SituacaoMaterialInformacional.class);
				
				SituacaoMaterialInformacional novaSituacaoBanco 
					= dao.findByPrimaryKey(novaSituacao.getId(), SituacaoMaterialInformacional.class);
				
				if(! situacaoASerRemovidaBanco.isAtivo())
					erros.addErro("A situação já foi removida.");
				
				if( ! situacaoASerRemovidaBanco.isEditavel())
					erros.addErro("Essa situação não pode ser removida, pois ela faz parte das regras do sistema.");
				
				if(novaSituacaoBanco.isSituacaoEmprestado()){
					erros.addErro("Os materiais não podem ser migrados para a situação "+novaSituacaoBanco.getDescricao()+", isso só é possível realizando um empréstimo.");
				}
				
				if(novaSituacaoBanco.isSituacaoDeBaixa()){
					erros.addErro("Os materiais não podem ser migrados para a situação "+novaSituacaoBanco.getDescricao()+", deve-se realizar a baixa desses materiais.");
				}
				
				dao.detach(situacaoASerRemovidaBanco);
				dao.detach(novaSituacaoBanco);
				
			}finally{
				if(dao != null) dao.close();
			}
			
		}

		try{
			checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, movimento);
		}catch(SegurancaException se){
			erros.addErro("Usuário não tem permissão para remover uma situação de material informacional.");
		}
		
		if (novaSituacao != null && situacaoASerRemovida != null && situacaoASerRemovida.getId() == novaSituacao.getId())
			erros.addErro("Os materiais não podem ser migrados para a mesma situção que está sendo removida.");

		checkValidation(erros);
		
	}
}
