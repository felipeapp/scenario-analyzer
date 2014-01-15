/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 24/04/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 *    <p>Processador que realiza as a��es de atualizar fasc�culos no acervo.</p>
 * 
 *    <p>Utilizando para atualiza��es de prop�sito geral, nas quais todas as informa��es s�o salvas.</p>
 * 
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 cria��o da classe
 *
 */
public class ProcessadorAtualizaFasciculo extends AbstractProcessador{

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoAtualizaFasciculo mov = (MovimentoAtualizaFasciculo) movimento;
		
		validate(mov);
		
		MaterialInformacionalDao dao = null;
		
		try{
			
			dao = getDAO(MaterialInformacionalDao.class, mov);
			
			for (Fasciculo fasciculo : mov.getFasciculos()) {
			
				dao.update(fasciculo);
				dao.registraAlteracaoMaterial(fasciculo, mov.getDescricaoOperacao(), true);
			}
			
		}finally{
			if(dao != null ) dao.close();
		}
		
		
		return null;
	}

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		MovimentoAtualizaFasciculo mov = (MovimentoAtualizaFasciculo) movimento;
		
		
		MaterialInformacionalDao dao = null;
		SituacaoMaterialInformacionalDao situacaoDao = null;
		ClassificacaoBibliograficaDao daoClassificacao = null;
		
		ListaMensagens lista = new ListaMensagens();
		
		try {
		
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, movimento);
			
			daoClassificacao = getDAO(ClassificacaoBibliograficaDao.class, movimento);
			
			SituacaoMaterialInformacional situacaoDeBaixa = dao.findByExactField(
					SituacaoMaterialInformacional.class, "situacaoDeBaixa", true, true);
			
			
			for (Fasciculo fasciculo : mov.getFasciculos()) {
				
				//////////////////////// Testa se o fasc�culo foi baixado ////////////////////////
				Fasciculo fasciculoDoBanco = dao.findByPrimaryKey(fasciculo.getId(), Fasciculo.class);
				
				if ( ! mov.isPermiteAtualizacaoDeBaixados() ) {
					// TEM QUE VERIFICAR SE � NULO, POIS FASC�CULOS S�O CRIADOS SEM SITUA��O.
					if( fasciculoDoBanco.getSituacao() != null && fasciculoDoBanco.getSituacao().getId() == situacaoDeBaixa.getId()){
						lista.addMensagem(new MensagemAviso("Fasc�culo n�o p�de ser alterado, pois ele foi baixado ", TipoMensagemUFRN.ERROR ));
					}
				}
				
				if( ! fasciculoDoBanco.isAtivo())
					lista.addMensagem(new MensagemAviso("Fasc�culo n�o p�de ser alterado, pois ele foi removido ", TipoMensagemUFRN.ERROR ));
				
				
				/* *********************************************************************************************************************************
				 * Se o material est� emprestado atualmente e o usu�rio est� tentando alterar para uma situa��o diferente de empretado 
				 * Pode ocorrer caso o usu�rio abra a tela de alterar o materail antes de realizar o empr�stimos, e clicar em atualizar depois de realizar o empr�stimo 
				* **********************************************************************************************************************************/
				
				
				if(! mov.isPermiteAtualizacaoDeEmprestados()){
					
					SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(
							SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
					
					SituacaoMaterialInformacional situacaoAtual =  situacaoDao.findSituacaoAtualMaterial(fasciculo.getId());
					
					if(situacaoAtual.isSituacaoEmprestado() && fasciculo.getSituacao().getId() != situacaoEmprestado.getId()){
						lista.addErro(" A situa��o do fasc�culo n�o pode ser alterada, pois ele est� "+situacaoEmprestado.getDescricao() );
					}
				
				}
				
				
				boolean isIncluindoFasciculoAcervo = ! fasciculoDoBanco.isIncluidoAcervo();
				
				dao.detach(fasciculoDoBanco);
				////////////////////////////////////////////////////////////////////////////////////
				
				
				
				if(mov.isVerificaPermissao()){ // usado geralmente na cataloga��o
				
					fasciculo.setBiblioteca(dao.refresh(fasciculo.getBiblioteca()));
					
					if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
						
						try{
							checkRole(fasciculo.getBiblioteca().getUnidade() , movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
						}catch (SegurancaException se) {
							lista.addErro("O usu�rio(a): "+ mov.getUsuarioLogado().getNome()
										+ " n�o tem permiss�o para alterar fasc�culos da biblioteca: "
										+ fasciculo.getBiblioteca().getDescricao());
						}
					
					}
					
				}
				
				
				// BLOQUEIO PARA N�O INSERIR DOIS MATERIAIS COM MESMO C�DIGO DE BARRAS.
				
				boolean existeMaterial =  dao.existeMateriaisByCodigosBarras(fasciculo.getCodigoBarras(), fasciculo.getId());
				
				if(existeMaterial){
					lista.addErro(" J� existe um outro material com o mesmo c�digo de barras no sistema: "
							+fasciculo.getCodigoBarras()+", por isso o fasc�culo n�o p�de ser atualizado. ");
				}
				
				
				// Bloquea a inclus�o de materiais no sistema se o T�tulo estiver sem classifica��o //
				
				if( isIncluindoFasciculoAcervo ){ // se est� inclu�ndo no acervo agora
					
					ClassificacaoBibliografica classificacaoUtilizada = daoClassificacao.findClassificacaoUtilizadaPelaBiblioteca(fasciculo.getBiblioteca().getId()); 
					
					if(classificacaoUtilizada == null)
						lista.addErro("Para incluir materiais no acervo da biblioteca: "+fasciculo.getBiblioteca().getDescricao()+" � preciso primeiro definir qual a classifica��o bibliogr�fica que ela utilizar�.");
					else{
						if( ! daoClassificacao.isDadosClassificacaoPreenchidos(fasciculo.getTituloCatalografico().getId(), classificacaoUtilizada.getOrdem())){
							lista.addErro(" N�o � poss�vel incluir o fasc�culo no sistema porque a sua cataloga��o n�o possui as informa��es da classifica��o "+classificacaoUtilizada.getDescricao()+" preenchidas. ");
						}
					}
				}
				
				
				
			}
		
		} finally {
			
			if (dao != null) dao.close();
			if(situacaoDao != null) situacaoDao.close();
			if(daoClassificacao != null) daoClassificacao.close();
			
			checkValidation(lista);
			
		}
	}
	
}
