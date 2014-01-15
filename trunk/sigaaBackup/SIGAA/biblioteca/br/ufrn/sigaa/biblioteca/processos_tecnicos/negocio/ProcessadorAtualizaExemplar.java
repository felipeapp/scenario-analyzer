/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/03/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 *  <p>Processador para atualizar as informações dos exemplares do sistema. </p>
 *
 *  <p>Utilizando para atualizações de propósito geral, nas quais todas as informações são salvas.</p>
 *
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 criação da classe
 *
 */
public class ProcessadorAtualizaExemplar extends AbstractProcessador{
	
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoAtualizaExemplar mov = (MovimentoAtualizaExemplar) movimento;
		
		
		validate(mov);
		
		
		List<Exemplar> anexos = new ArrayList<Exemplar>();
		
		MaterialInformacionalDao dao = null;
		ExemplarDao exemplarDao = null;
		
		try{
			
			dao = getDAO(MaterialInformacionalDao.class, mov);
			exemplarDao = getDAO(ExemplarDao.class, mov);
			
			for (Exemplar exemplar : mov.getExemplares()) {
			
			
				exemplar.setCodigoBarras(exemplar.getCodigoBarras().toUpperCase());
				/* **********************************************************************************
				 * Se alterou o código de barras do exemplar principal, tem que alterar o código de *
				 * todos os seus anexos para seguirem o código do principal, apenas mudando a letra *
				 * no final e seguir o padrão da criação.                                           *
				 * **********************************************************************************/
				
				Exemplar exemplarDoBanco = dao.findByPrimaryKey(exemplar.getId(), Exemplar.class);
				
				if(! exemplar.getCodigoBarras().equalsIgnoreCase(exemplarDoBanco.getCodigoBarras())){ // usuário mudou o código de barras
					
					anexos = exemplarDao.findAnexosAtivosDoExemplar(exemplar.getId());
				
					if(anexos.size() > 0){
					
						alteraCodigoBarrasAnexos(anexos, exemplar.getCodigoBarras());
					}
				}
				
				dao.detach(exemplarDoBanco);
				
				dao.update(exemplar);
				
				dao.registraAlteracaoMaterial(exemplar, mov.getDescricaoOperacao(), true);
				
				dao.detach(exemplar);
				
				// retirei daqui
			
			}
			
		}finally{
			if(dao != null) dao.close();
			if(exemplarDao != null) exemplarDao.close();
		}
		
		// abre um novo dao para atualizar os anexo porque tava dando erro de  Illegal attempt to associate a collection with two open sessions
		// provavelmente porque não está sendo feito projeção, mas como isso já tá a muito tempo assim, não quero alterar para não gerar novos erros. 
		
		MaterialInformacionalDao dao2 = null;
		
		try{
			
			dao2 = getDAO(MaterialInformacionalDao.class, mov);
			
			for (Exemplar anexo : anexos) {
				dao2.update(anexo);
				dao2.registraAlteracaoMaterial(anexo, "Alterando o Código Barras do anexo para "+anexo.getCodigoBarras()+" devido a alteração do Código de Barras do exemplar principal", true);
				//dao.detach(anexo);
			}
		}finally{
			if(dao2 != null) dao.close();
		}
		return null;
	}

	
		
		
	/**
	 *   Altera o código de barras do anexo para manter o padrão de criação do sistema.<br/>
	 *   Se o exemplar principal era L000005 e o anexo L000005A e mudou para L00000500000 o anexo
	 *   deve mudar para L00000500000A<br/>
	 *
	 * @param anexos
	 * @param antigoCodigoBarrasPrincipal
	 */
	private void alteraCodigoBarrasAnexos(List<Exemplar> anexos, String novoCodigoBarrasPrincipal){
		
		for (Exemplar anexo : anexos) {
		
			 // O código de barras do anexo deve ter pelo menos 2 caracteres, um do código de   //
			// barras do principal e o outro que é a letra do anexo                             //
			if ( anexo.getCodigoBarras().length() > 1 ){
				
				char c = anexo.getCodigoBarras().charAt(  anexo.getCodigoBarras().length()-1 );
				
				if(Character.isLetter(c)){
					anexo.setCodigoBarras( novoCodigoBarrasPrincipal + c  );
				}
				
			}
		}
	}

	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		MovimentoAtualizaExemplar mov = (MovimentoAtualizaExemplar) movimento;
		
		MaterialInformacionalDao dao = null;
		SituacaoMaterialInformacionalDao situacaoDao = null;
		
		ListaMensagens lista = new ListaMensagens();

		try{
			
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, movimento);
			
			SituacaoMaterialInformacional situacaoDeBaixa = dao.findByExactField(
					SituacaoMaterialInformacional.class, "situacaoDeBaixa", true, true);
			
		
			
			for (Exemplar exemplar : mov.getExemplares()) {
				
				//////////////////////// Testa se o exemplar foi baixado ////////////////////////
				Exemplar exemplarDoBanco = dao.findByPrimaryKey(exemplar.getId(), Exemplar.class);
				
				if ( ! mov.isPermiteAtualizacaoDeBaixados() ) {
					if ( exemplarDoBanco.getSituacao().getId() == situacaoDeBaixa.getId() ){
						lista.addMensagem(new MensagemAviso("Exemplar não pôde ser alterado, pois ele foi baixado ", TipoMensagemUFRN.ERROR ));
					}
				}
				
				if( ! exemplarDoBanco.isAtivo())
					lista.addMensagem(new MensagemAviso("Exemplar não pôde ser alterado, pois ele foi removido ", TipoMensagemUFRN.ERROR ));
				
			    /* *********************************************************************************************************************************
				* Se o material está emprestado atualmente e o usuário está tentando alterar para uma situação diferente de empretado //
				*  Pode ocorrer caso o usuário abra a tela de alterar o materail antes de realizar o empréstimos, e clicar em atualizar depois de realizar o empréstimo //
				* **********************************************************************************************************************************/
				
				if(! mov.isPermiteAtualizacaoDeEmprestados()){
					
					SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(
							SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
					
					SituacaoMaterialInformacional situacaoAtual =  situacaoDao.findSituacaoAtualMaterial(exemplar.getId());
					
					if(situacaoAtual.isSituacaoEmprestado() && exemplar.getSituacao().getId() != situacaoEmprestado.getId()){
						lista.addErro(" A situação do exemplar não pode ser alterada, pois ele está "+situacaoEmprestado.getDescricao() );
					}
				}
				
				dao.detach(exemplarDoBanco);
				////////////////////////////////////////////////////////////////////////////////////
				
				
				
				if(mov.isVerificaPermissao()){ // usado geralmente na catalogação
					
					if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
						
						
						try{
							checkRole(exemplar.getBiblioteca().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
						}catch (SegurancaException se) {
							lista.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()
										+ " não tem permissão para alterar exemplares da biblioteca. "
										+exemplar.getBiblioteca().getDescricao());
						}
					
					}
					
				}
				
				// BLOQUEIO PARA NÃO INSERIR DOIS MATERIAIS COM MESMO CÓDIGO DE BARRAS.
				
				boolean existeMaterial =  dao.existeMateriaisByCodigosBarras(exemplar.getCodigoBarras(), exemplar.getId());
				
				if(existeMaterial){
					lista.addErro(" Já existe um outro material com o mesmo código de barras no sistema: "
							+exemplar.getCodigoBarras()+", por isso o exemplar não pôde ser atualizado. ");
				}
				
			} // fecha o for
			
		}finally{
			
			if(dao != null) dao.close();
			if(situacaoDao != null) situacaoDao.close();
			
			checkValidation(lista);
		}
		
	}

}
