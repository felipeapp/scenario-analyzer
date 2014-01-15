/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/10/2011
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
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.dominio.Unidade;

/**
 *
 * <p>Processador quem contém as regras para alteração da situação de vários materiais ativos no acervo.</p>
 * 
 * @author felipe
 *
 */
public class ProcessadorTransferirMateriaisEntreSetores extends AbstractProcessador{

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		MovimentoTransferirMateriaisEntreSetores movi = (MovimentoTransferirMateriaisEntreSetores) mov;
		MaterialInformacionalDao dao = getDAO(MaterialInformacionalDao.class, movi);
		
		try{

			for(MaterialInformacional mat : movi.getMateriais()) {
				dao.updateFields(MaterialInformacional.class, mat.getId(), new String[]{ "situacao" }, new Object[]{ movi.getSituacao() });
			}
		
		}finally{
			if(dao != null)  dao.close();
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
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MaterialInformacionalDao dao = null;
		SituacaoMaterialInformacionalDao situacaoDao = null;
		
		try {
			dao = getDAO(MaterialInformacionalDao.class, mov);
			situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, mov);
			
			MovimentoTransferirMateriaisEntreSetores movi = (MovimentoTransferirMateriaisEntreSetores) mov;
			
			ListaMensagens lista = new ListaMensagens();
			
			SituacaoMaterialInformacional situacao = null;
			
			if (movi.getMateriais().size() == 0) {
				lista.addErro("A lista de materiais está vazia. Adicione pelo menos um material.");
			}
			
			for (MaterialInformacional material : movi.getMateriais()) {
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)) {
					material.getBiblioteca().setUnidade( new Unidade( dao.findByPrimaryKey(material.getBiblioteca().getId(), Biblioteca.class, "unidade.id").getUnidade().getId() ) ); 
					
					try {
						checkRole(material.getBiblioteca().getUnidade(), mov, 
								SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, 
								SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, 
								SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, 
								SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, 
								SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO);
					} catch (SegurancaException se) {
						lista.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()+ " não tem permissão para alterar exemplares do(a) "+material.getBiblioteca().getDescricao());
					}
				}
				
				situacao = situacaoDao.findSituacaoAtualMaterial(material.getId());

				if (situacao.getId() == movi.getSituacao().getId()) {
					lista.addErro("Material com o código de barras "+material.getCodigoBarras()+" já se encontra na situação informada.");		
				} else if (situacao.isSituacaoEmprestado()) {
					lista.addErro("Material com o código de barras "+material.getCodigoBarras()+" se encontra emprestado.");					
				} else if(situacao.isSituacaoDeBaixa()) {
					lista.addErro("Material com o código de barras "+material.getCodigoBarras()+" se encontra baixado.");
				}
			}	
					
			checkValidation(lista);		
		} finally {
			if(dao != null)  dao.close();
			if(situacaoDao != null)  situacaoDao.close();	
		}	
	}
}
