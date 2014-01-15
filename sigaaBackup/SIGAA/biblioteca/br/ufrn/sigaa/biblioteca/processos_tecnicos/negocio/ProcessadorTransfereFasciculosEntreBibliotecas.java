/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 23/10/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RegistroMovimentacaoMaterialInformacional;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *     Realiza a transferência de fascículos entre bibliotecas.<br/>
 *     Como fascículos não são tombados não precisa gerar um guia de movimentação do Sipac, mas
 *  para cada fascículo Transferido o sistema cria um registro de Movimentação.
 *
 *
 * @author jadson
 * @since 23/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorTransfereFasciculosEntreBibliotecas extends AbstractProcessador{


	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoTransfereFasciculosEntreBibliotecas movimento = (MovimentoTransfereFasciculosEntreBibliotecas) mov;
		
		AssinaturaDao dao = null;
		BibliotecaDao daoBiblioteca = null;
		
		validate(mov);
		
		Assinatura origem = movimento.getAssinaturaOrigem();
		Assinatura destino = movimento.getAssinaturaDestino();
		
		boolean transferenciaMesmaBiblioteca = false;
		
		String descricaoBibliotecaDestino = "";
		
		try{
			dao = getDAO(AssinaturaDao.class, movimento);
		
			daoBiblioteca = getDAO(BibliotecaDao.class, movimento);
			
			List<Fasciculo> fasciculos = movimento.getFasciculos();
			
			List<RegistroMovimentacaoMaterialInformacional> registros = new ArrayList<RegistroMovimentacaoMaterialInformacional>();
			
			for (Fasciculo fasciculo : fasciculos) {
				
				/* *********************************************************************************
				 * SOMENTE CRIA UM REGISTRO DE MOVIMENTAÇÃO PENDENTE, A TRANSFERÊNCIA MESMO OCORRE
				 * APENAS DEPOIS QUE ALGUM BIBLIOTECÁRIO DA BIBLIOTECA DESTINO AUTORIZA
				 ***********************************************************************************/
				
				RegistroMovimentacaoMaterialInformacional registro
					= new RegistroMovimentacaoMaterialInformacional(fasciculo, (Usuario) movimento.getUsuarioLogado()
						, origem.getUnidadeDestino(), movimento.getBibliotecaDestino(), origem, destino );
				
				/* ****************************************************************************************
				 * SE A TRANSFERÊNCIA OCORRE PARA A MESMA BIBLIOTECA NÃO PRECISA O BIBLIOTECÁRIO AUTORIZAR
				 ******************************************************************************************/
				if(origem.getUnidadeDestino().getId() == movimento.getBibliotecaDestino().getId()){
					
					transferenciaMesmaBiblioteca = true;
					
					registro.setUsuarioAutorizouMovimentacaoMaterial((Usuario) movimento.getUsuarioLogado());
					registro.setDataAutorizacao(new Date());
					registro.setPendente(false);
					
					/// Se é diferente de null é porque o usuário escolheu outra assinatura da mesma biblioteca.
					if(destino != null){
						fasciculo.setAssinatura(destino);
						fasciculo.setBiblioteca(destino.getUnidadeDestino());
						dao.updateNoFlush(fasciculo);
					}
					
				}else
					descricaoBibliotecaDestino = daoBiblioteca.findDescricaoBibliotecaInternaAtiva(movimento.getBibliotecaDestino().getId());
				
				registros.add(registro);
				
			}
			
			for (RegistroMovimentacaoMaterialInformacional registraMovimentacao : registros) {
				dao.createNoFlush(registraMovimentacao);
			}
			
		}finally{
			if( dao != null)  dao.close();
			if( daoBiblioteca != null ) daoBiblioteca.close();
		}
		
		if(transferenciaMesmaBiblioteca)
			return "Transferência realizada com sucesso.";
		else
			return "Uma Solicitação de Transferência foi criada com sucesso. Para que a transferência seja concluída é necessário" +
			" que um bibliotecário da biblioteca: \""+descricaoBibliotecaDestino+"\" autorize-a. ";
		
	}


	
	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoTransfereFasciculosEntreBibliotecas movimento
					= (MovimentoTransfereFasciculosEntreBibliotecas) mov;
		
		AssinaturaDao dao = null;
		
		try{
			dao = getDAO(AssinaturaDao.class, movimento);
		
			Assinatura origem = movimento.getAssinaturaOrigem();
			Assinatura destino = movimento.getAssinaturaDestino();
		
			if(destino == null && ! movimento.isSolicitarCriacaoAssinatura()){
				lista.addErro("Selecione a assinatura destino dos fascículos.");
				checkValidation(lista);
			}
		
			List<Fasciculo> fasciculos = movimento.getFasciculos();
			
			if(fasciculos == null || fasciculos.size() == 0)
				lista.addErro("Selecione algum fascículo para realizar a tranferência.");
			else{
				for (Fasciculo f :  fasciculos) {
					
					Fasciculo temp = dao.refresh(new Fasciculo(f.getId()));
					
					if(temp.isEmprestado()){
						lista.addErro("Fascículo: "+temp.getCodigoBarras()+" não pode ser transferido, pois está "+temp.getSituacao().getDescricao());
					}
				}
				
				if(destino != null && origem.getId() == destino.getId())
					lista.addErro("Fascículos não podem ser transferidos para a mesma assinatura");
			
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
					try{
						
						checkRole(origem.getUnidadeDestino().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
						
					}catch (SegurancaException se) {
						lista.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()
									+ " não tem permissão para transferir materiais da biblioteca: "
									+origem.getUnidadeDestino().getDescricao());
					}
				
				}
			
			}
			
		}finally{
			if( dao != null) dao.close();
		}
			
		checkValidation(lista);
	}

	
}
