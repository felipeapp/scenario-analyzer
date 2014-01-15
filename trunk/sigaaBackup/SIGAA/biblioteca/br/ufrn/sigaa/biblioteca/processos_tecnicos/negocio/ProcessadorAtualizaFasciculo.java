/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 *    <p>Processador que realiza as ações de atualizar fascículos no acervo.</p>
 * 
 *    <p>Utilizando para atualizações de propósito geral, nas quais todas as informações são salvas.</p>
 * 
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 criação da classe
 *
 */
public class ProcessadorAtualizaFasciculo extends AbstractProcessador{

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
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
	 * Ver comentários da classe pai.<br/>
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
				
				//////////////////////// Testa se o fascículo foi baixado ////////////////////////
				Fasciculo fasciculoDoBanco = dao.findByPrimaryKey(fasciculo.getId(), Fasciculo.class);
				
				if ( ! mov.isPermiteAtualizacaoDeBaixados() ) {
					// TEM QUE VERIFICAR SE É NULO, POIS FASCÍCULOS SÃO CRIADOS SEM SITUAÇÃO.
					if( fasciculoDoBanco.getSituacao() != null && fasciculoDoBanco.getSituacao().getId() == situacaoDeBaixa.getId()){
						lista.addMensagem(new MensagemAviso("Fascículo não pôde ser alterado, pois ele foi baixado ", TipoMensagemUFRN.ERROR ));
					}
				}
				
				if( ! fasciculoDoBanco.isAtivo())
					lista.addMensagem(new MensagemAviso("Fascículo não pôde ser alterado, pois ele foi removido ", TipoMensagemUFRN.ERROR ));
				
				
				/* *********************************************************************************************************************************
				 * Se o material está emprestado atualmente e o usuário está tentando alterar para uma situação diferente de empretado 
				 * Pode ocorrer caso o usuário abra a tela de alterar o materail antes de realizar o empréstimos, e clicar em atualizar depois de realizar o empréstimo 
				* **********************************************************************************************************************************/
				
				
				if(! mov.isPermiteAtualizacaoDeEmprestados()){
					
					SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(
							SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
					
					SituacaoMaterialInformacional situacaoAtual =  situacaoDao.findSituacaoAtualMaterial(fasciculo.getId());
					
					if(situacaoAtual.isSituacaoEmprestado() && fasciculo.getSituacao().getId() != situacaoEmprestado.getId()){
						lista.addErro(" A situação do fascículo não pode ser alterada, pois ele está "+situacaoEmprestado.getDescricao() );
					}
				
				}
				
				
				boolean isIncluindoFasciculoAcervo = ! fasciculoDoBanco.isIncluidoAcervo();
				
				dao.detach(fasciculoDoBanco);
				////////////////////////////////////////////////////////////////////////////////////
				
				
				
				if(mov.isVerificaPermissao()){ // usado geralmente na catalogação
				
					fasciculo.setBiblioteca(dao.refresh(fasciculo.getBiblioteca()));
					
					if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
						
						try{
							checkRole(fasciculo.getBiblioteca().getUnidade() , movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
						}catch (SegurancaException se) {
							lista.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()
										+ " não tem permissão para alterar fascículos da biblioteca: "
										+ fasciculo.getBiblioteca().getDescricao());
						}
					
					}
					
				}
				
				
				// BLOQUEIO PARA NÃO INSERIR DOIS MATERIAIS COM MESMO CÓDIGO DE BARRAS.
				
				boolean existeMaterial =  dao.existeMateriaisByCodigosBarras(fasciculo.getCodigoBarras(), fasciculo.getId());
				
				if(existeMaterial){
					lista.addErro(" Já existe um outro material com o mesmo código de barras no sistema: "
							+fasciculo.getCodigoBarras()+", por isso o fascículo não pôde ser atualizado. ");
				}
				
				
				// Bloquea a inclusão de materiais no sistema se o Título estiver sem classificação //
				
				if( isIncluindoFasciculoAcervo ){ // se está incluíndo no acervo agora
					
					ClassificacaoBibliografica classificacaoUtilizada = daoClassificacao.findClassificacaoUtilizadaPelaBiblioteca(fasciculo.getBiblioteca().getId()); 
					
					if(classificacaoUtilizada == null)
						lista.addErro("Para incluir materiais no acervo da biblioteca: "+fasciculo.getBiblioteca().getDescricao()+" é preciso primeiro definir qual a classificação bibliográfica que ela utilizará.");
					else{
						if( ! daoClassificacao.isDadosClassificacaoPreenchidos(fasciculo.getTituloCatalografico().getId(), classificacaoUtilizada.getOrdem())){
							lista.addErro(" Não é possível incluir o fascículo no sistema porque a sua catalogação não possui as informações da classificação "+classificacaoUtilizada.getDescricao()+" preenchidas. ");
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
