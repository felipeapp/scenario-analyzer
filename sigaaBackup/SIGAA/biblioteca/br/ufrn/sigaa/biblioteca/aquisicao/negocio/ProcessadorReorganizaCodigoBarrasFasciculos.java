/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Processador que cont�m a l�gica de neg�cio para reorganizar o c�digo de barras dos fasc�culos de uma assinatura.</p>
 *
 * @author jadson
 *
 */
public class ProcessadorReorganizaCodigoBarrasFasciculos extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov); 
		
		MovimentoReorganizaCodigoBarrasFasciculos movimento = (MovimentoReorganizaCodigoBarrasFasciculos) mov;
		
		AssinaturaDao dao = null;
		
		try{
			dao = getDAO(AssinaturaDao.class, movimento);
			
			// Observa��o: A informa��o do c�digo de barras � gerado nos campos transientes informacao informacao2
			// O fasc�culo permanece com o c�digo de barra antigo at� ser atualizado aqui.
		
			// Realiza primeiro a atualiza��o para um valor intermedi�rio para n�o da conflito
			for (Fasciculo fasciculoAlteracao : movimento.getFasciculosAssinatura()) {
				dao.update("UPDATE biblioteca.material_informacional SET codigo_barras = ? WHERE id_material_informacional = ? "
						, fasciculoAlteracao.getCodigoBarras()+"-intermediario"
						, fasciculoAlteracao.getId() );
			}
			
			// Atualiza o valor intermadi�rio gerado pelo sistema para o novo //
			for (Fasciculo fasciculoAlteracao : movimento.getFasciculosAssinatura()) {
				dao.update("UPDATE biblioteca.material_informacional SET codigo_barras = ? WHERE id_material_informacional = ? "
						, fasciculoAlteracao.getInformacao()+"-"+fasciculoAlteracao.getInformacao2()
						, fasciculoAlteracao.getId() );
				
				// se � suplemento precisa atualizar o n�mero gerador do fasc�culo principal dele, que j� foi calculado
				if(fasciculoAlteracao.isSuplemento()){
					dao.update("UPDATE biblioteca.material_informacional SET numero_gerador_codigo_barras_anexos = ? WHERE id_material_informacional = ? "
							, fasciculoAlteracao.getFasciculoDeQuemSouSuplemento().getNumeroGeradorCodigoBarrasAnexos()
							, fasciculoAlteracao.getFasciculoDeQuemSouSuplemento().getId() );
				}
			}
		
			// Atualiza o n�mero sequencial do �ltimo fasc�culo gerado //
			dao.updateField(Assinatura.class, movimento.getAssinaturaSelecionada().getId(), "numeroGeradorFasciculo", movimento.getNumeroSequencialFasciculos() );
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * Ver coment�rios da classe pai
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoReorganizaCodigoBarrasFasciculos movimento = (MovimentoReorganizaCodigoBarrasFasciculos) mov;
		
		SituacaoMaterialInformacionalDao situacaoDao = null;
		
		try{
			
			situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, movimento);
			
			SituacaoMaterialInformacional situacaoEmprestado = situacaoDao.findByExactField(SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			// Verifica se o usu�rio tem papel de aquisi��o na biblioteca da assinatura dos fasc�culos //
			/////////////////////////////////////////////////////////////////////////////////////////////
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
				try{
					Biblioteca bibliotecaAssinatura = situacaoDao.findByPrimaryKey(movimento.getAssinaturaSelecionada().getUnidadeDestino().getId(), Biblioteca.class, "unidade.id");
					
					checkRole(bibliotecaAssinatura.getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO);
				}catch (SegurancaException se) {
					throw new NegocioException("O usu�rio(a): "+ mov.getUsuarioLogado().getNome()
								+ " n�o tem permiss�o para alterar fasc�culos da biblioteca. "
								+movimento.getAssinaturaSelecionada().getUnidadeDestino().getDescricao());
				}
			}
			
			
			
			
			//////////////////////////////////////////////////////////////////////////////////////////////////
			// Verifica se os c�digo de barras n�o batem  com os c�digos existentes em outras assinaturas   //
			/////////////////////////////////////////////////////////////////////////////////////////////////
			for (Fasciculo fasciculoAlteracao : movimento.getFasciculosAssinatura()) {
				
				for (Fasciculo fasciculosOutrasAssinaturas : movimento.getFasciculosEmOutrasAssinaturas()) {
				
					/// Observa��o: A informa��o do c�digo de barras � gerado nos campos transientes informacao informacao2
					if(fasciculosOutrasAssinaturas.getCodigoBarras().equals(fasciculoAlteracao.getInformacao()+"-"+fasciculoAlteracao.getInformacao2() ) ){
						throw new NegocioException("O c�digo de barras dos fasc�culos n�o pode ser alterados pois j� existem outras " +
								" fasc�culos em outras assinaturas com o mesmo c�digo. Talvez seja preciso primeiro reorganizar os fasc�culos dessa outra assinatura.");
					}
				}
			}

			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			// Verifica se os fasc�culos n�o est�o emprestados      //
			/////////////////////////////////////////////////////////////////////////////////////////////
			List<Integer> idsMateriais = new ArrayList<Integer>();
			
			for (Fasciculo fasciculoAlteracao : movimento.getFasciculosAssinatura()) {
				idsMateriais.add(fasciculoAlteracao.getId());
			}
			
			if( situacaoDao.verificaSeExisteAlgumaMaterialEmprestado(idsMateriais)){
				throw new NegocioException(" Os c�digos de barras do fasc�culo n�o podem ser alterado, pois existe um fasc�culo que est� "+situacaoEmprestado.getDescricao() );
			}
			
		
		}finally{
			if(situacaoDao != null) situacaoDao.close();
		}
		
	}

}
