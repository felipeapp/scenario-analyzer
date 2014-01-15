/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *  <p>Processador para atualizar as informa��es dos exemplares do sistema. </p>
 *
 *  <p>Utilizando para atualiza��es de prop�sito geral, nas quais todas as informa��es s�o salvas.</p>
 *
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 cria��o da classe
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
				 * Se alterou o c�digo de barras do exemplar principal, tem que alterar o c�digo de *
				 * todos os seus anexos para seguirem o c�digo do principal, apenas mudando a letra *
				 * no final e seguir o padr�o da cria��o.                                           *
				 * **********************************************************************************/
				
				Exemplar exemplarDoBanco = dao.findByPrimaryKey(exemplar.getId(), Exemplar.class);
				
				if(! exemplar.getCodigoBarras().equalsIgnoreCase(exemplarDoBanco.getCodigoBarras())){ // usu�rio mudou o c�digo de barras
					
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
		// provavelmente porque n�o est� sendo feito proje��o, mas como isso j� t� a muito tempo assim, n�o quero alterar para n�o gerar novos erros. 
		
		MaterialInformacionalDao dao2 = null;
		
		try{
			
			dao2 = getDAO(MaterialInformacionalDao.class, mov);
			
			for (Exemplar anexo : anexos) {
				dao2.update(anexo);
				dao2.registraAlteracaoMaterial(anexo, "Alterando o C�digo Barras do anexo para "+anexo.getCodigoBarras()+" devido a altera��o do C�digo de Barras do exemplar principal", true);
				//dao.detach(anexo);
			}
		}finally{
			if(dao2 != null) dao.close();
		}
		return null;
	}

	
		
		
	/**
	 *   Altera o c�digo de barras do anexo para manter o padr�o de cria��o do sistema.<br/>
	 *   Se o exemplar principal era L000005 e o anexo L000005A e mudou para L00000500000 o anexo
	 *   deve mudar para L00000500000A<br/>
	 *
	 * @param anexos
	 * @param antigoCodigoBarrasPrincipal
	 */
	private void alteraCodigoBarrasAnexos(List<Exemplar> anexos, String novoCodigoBarrasPrincipal){
		
		for (Exemplar anexo : anexos) {
		
			 // O c�digo de barras do anexo deve ter pelo menos 2 caracteres, um do c�digo de   //
			// barras do principal e o outro que � a letra do anexo                             //
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
						lista.addMensagem(new MensagemAviso("Exemplar n�o p�de ser alterado, pois ele foi baixado ", TipoMensagemUFRN.ERROR ));
					}
				}
				
				if( ! exemplarDoBanco.isAtivo())
					lista.addMensagem(new MensagemAviso("Exemplar n�o p�de ser alterado, pois ele foi removido ", TipoMensagemUFRN.ERROR ));
				
			    /* *********************************************************************************************************************************
				* Se o material est� emprestado atualmente e o usu�rio est� tentando alterar para uma situa��o diferente de empretado //
				*  Pode ocorrer caso o usu�rio abra a tela de alterar o materail antes de realizar o empr�stimos, e clicar em atualizar depois de realizar o empr�stimo //
				* **********************************************************************************************************************************/
				
				if(! mov.isPermiteAtualizacaoDeEmprestados()){
					
					SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(
							SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
					
					SituacaoMaterialInformacional situacaoAtual =  situacaoDao.findSituacaoAtualMaterial(exemplar.getId());
					
					if(situacaoAtual.isSituacaoEmprestado() && exemplar.getSituacao().getId() != situacaoEmprestado.getId()){
						lista.addErro(" A situa��o do exemplar n�o pode ser alterada, pois ele est� "+situacaoEmprestado.getDescricao() );
					}
				}
				
				dao.detach(exemplarDoBanco);
				////////////////////////////////////////////////////////////////////////////////////
				
				
				
				if(mov.isVerificaPermissao()){ // usado geralmente na cataloga��o
					
					if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
						
						
						try{
							checkRole(exemplar.getBiblioteca().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
						}catch (SegurancaException se) {
							lista.addErro("O usu�rio(a): "+ mov.getUsuarioLogado().getNome()
										+ " n�o tem permiss�o para alterar exemplares da biblioteca. "
										+exemplar.getBiblioteca().getDescricao());
						}
					
					}
					
				}
				
				// BLOQUEIO PARA N�O INSERIR DOIS MATERIAIS COM MESMO C�DIGO DE BARRAS.
				
				boolean existeMaterial =  dao.existeMateriaisByCodigosBarras(exemplar.getCodigoBarras(), exemplar.getId());
				
				if(existeMaterial){
					lista.addErro(" J� existe um outro material com o mesmo c�digo de barras no sistema: "
							+exemplar.getCodigoBarras()+", por isso o exemplar n�o p�de ser atualizado. ");
				}
				
			} // fecha o for
			
		}finally{
			
			if(dao != null) dao.close();
			if(situacaoDao != null) situacaoDao.close();
			
			checkValidation(lista);
		}
		
	}

}
