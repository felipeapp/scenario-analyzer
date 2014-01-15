/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/05/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 * <p> Processador que contém a lógica de negócio para reorganizar o código de barras dos fascículos de uma assinatura.</p>
 *
 * @author jadson
 *
 */
public class ProcessadorReorganizaCodigoBarrasFasciculos extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov); 
		
		MovimentoReorganizaCodigoBarrasFasciculos movimento = (MovimentoReorganizaCodigoBarrasFasciculos) mov;
		
		AssinaturaDao dao = null;
		
		try{
			dao = getDAO(AssinaturaDao.class, movimento);
			
			// Observação: A informação do código de barras é gerado nos campos transientes informacao informacao2
			// O fascículo permanece com o código de barra antigo até ser atualizado aqui.
		
			// Realiza primeiro a atualização para um valor intermediário para não da conflito
			for (Fasciculo fasciculoAlteracao : movimento.getFasciculosAssinatura()) {
				dao.update("UPDATE biblioteca.material_informacional SET codigo_barras = ? WHERE id_material_informacional = ? "
						, fasciculoAlteracao.getCodigoBarras()+"-intermediario"
						, fasciculoAlteracao.getId() );
			}
			
			// Atualiza o valor intermadiário gerado pelo sistema para o novo //
			for (Fasciculo fasciculoAlteracao : movimento.getFasciculosAssinatura()) {
				dao.update("UPDATE biblioteca.material_informacional SET codigo_barras = ? WHERE id_material_informacional = ? "
						, fasciculoAlteracao.getInformacao()+"-"+fasciculoAlteracao.getInformacao2()
						, fasciculoAlteracao.getId() );
				
				// se é suplemento precisa atualizar o número gerador do fascículo principal dele, que já foi calculado
				if(fasciculoAlteracao.isSuplemento()){
					dao.update("UPDATE biblioteca.material_informacional SET numero_gerador_codigo_barras_anexos = ? WHERE id_material_informacional = ? "
							, fasciculoAlteracao.getFasciculoDeQuemSouSuplemento().getNumeroGeradorCodigoBarrasAnexos()
							, fasciculoAlteracao.getFasciculoDeQuemSouSuplemento().getId() );
				}
			}
		
			// Atualiza o número sequencial do último fascículo gerado //
			dao.updateField(Assinatura.class, movimento.getAssinaturaSelecionada().getId(), "numeroGeradorFasciculo", movimento.getNumeroSequencialFasciculos() );
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * Ver comentários da classe pai
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoReorganizaCodigoBarrasFasciculos movimento = (MovimentoReorganizaCodigoBarrasFasciculos) mov;
		
		SituacaoMaterialInformacionalDao situacaoDao = null;
		
		try{
			
			situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, movimento);
			
			SituacaoMaterialInformacional situacaoEmprestado = situacaoDao.findByExactField(SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			// Verifica se o usuário tem papel de aquisição na biblioteca da assinatura dos fascículos //
			/////////////////////////////////////////////////////////////////////////////////////////////
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				try{
					Biblioteca bibliotecaAssinatura = situacaoDao.findByPrimaryKey(movimento.getAssinaturaSelecionada().getUnidadeDestino().getId(), Biblioteca.class, "unidade.id");
					
					checkRole(bibliotecaAssinatura.getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO);
				}catch (SegurancaException se) {
					throw new NegocioException("O usuário(a): "+ mov.getUsuarioLogado().getNome()
								+ " não tem permissão para alterar fascículos da biblioteca. "
								+movimento.getAssinaturaSelecionada().getUnidadeDestino().getDescricao());
				}
			}
			
			
			
			
			//////////////////////////////////////////////////////////////////////////////////////////////////
			// Verifica se os código de barras não batem  com os códigos existentes em outras assinaturas   //
			/////////////////////////////////////////////////////////////////////////////////////////////////
			for (Fasciculo fasciculoAlteracao : movimento.getFasciculosAssinatura()) {
				
				for (Fasciculo fasciculosOutrasAssinaturas : movimento.getFasciculosEmOutrasAssinaturas()) {
				
					/// Observação: A informação do código de barras é gerado nos campos transientes informacao informacao2
					if(fasciculosOutrasAssinaturas.getCodigoBarras().equals(fasciculoAlteracao.getInformacao()+"-"+fasciculoAlteracao.getInformacao2() ) ){
						throw new NegocioException("O código de barras dos fascículos não pode ser alterados pois já existem outras " +
								" fascículos em outras assinaturas com o mesmo código. Talvez seja preciso primeiro reorganizar os fascículos dessa outra assinatura.");
					}
				}
			}

			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			// Verifica se os fascículos não estão emprestados      //
			/////////////////////////////////////////////////////////////////////////////////////////////
			List<Integer> idsMateriais = new ArrayList<Integer>();
			
			for (Fasciculo fasciculoAlteracao : movimento.getFasciculosAssinatura()) {
				idsMateriais.add(fasciculoAlteracao.getId());
			}
			
			if( situacaoDao.verificaSeExisteAlgumaMaterialEmprestado(idsMateriais)){
				throw new NegocioException(" Os códigos de barras do fascículo não podem ser alterado, pois existe um fascículo que está "+situacaoEmprestado.getDescricao() );
			}
			
		
		}finally{
			if(situacaoDao != null) situacaoDao.close();
		}
		
	}

}
