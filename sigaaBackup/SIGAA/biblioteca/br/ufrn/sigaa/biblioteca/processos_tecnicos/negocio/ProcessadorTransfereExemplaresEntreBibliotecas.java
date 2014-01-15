/*
 * ProcessadorTransfereExemplaresEntreBibliotecas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.remoting.RemoteAccessException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.integracao.dto.RegistroEntradaDTO;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.dto.biblioteca.DadosTransferenciaMaterialDTO;
import br.ufrn.integracao.interfaces.PatrimonioRemoteService;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RegistroMovimentacaoMaterialInformacional;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *    <p>Processador que realiza as regras de negócio para a transferência de exemplares entre bibliotecas.</p>
 *    <p><i>OBS.: Tem que chamar o SIPAC para gerar um chamado patrimonial se o exemplar for tombados.</i></p>
 *
 * @author jadson
 * @since 23/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorTransfereExemplaresEntreBibliotecas extends AbstractProcessador{

	
	/**
	 * Método que executa a operação referente ao caso-de-uso Transferir Exemplares Entre Bibliotecas.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		MovimentoTransfereExemplaresEntreBibliotecas movimento = (MovimentoTransfereExemplaresEntreBibliotecas) mov;
		
		
		validate(mov);
		
		
		ExemplarDao dao = null;
	
		// aguarda os exemplares, que foi necessário abrir um chamado patrimonial para mostrar ao usuário
		List<String> codigoDeBarraDosExemplaresAbertoChamado =  new ArrayList<String>();
		
		String numeroChamadoPatrimonial = null; // para mostrar ao usuário o número do chamado patrimonial gerado
		
		try{
			dao = getDAO(ExemplarDao.class, movimento);
		
			List<Exemplar> exemplaresTransferencia =  movimento.getExemplares();
			Biblioteca bibliotecaDestino = movimento.getBibliotecaDestino();
		
		    // Guarda o número de patrimônio e a descrição da Unidade destino do exemplar //
			// Essa informação é usada para gerar o chamado patrimonial.                  //
			List<DadosTransferenciaMaterialDTO> dadosTransferencia = new ArrayList<DadosTransferenciaMaterialDTO>();
			
			
			/* **********************************************************************************
			 *  para cada exemplar que vai ser transferido gera um registro de movimentação e muda
			 *  a biblioteca do exemplar
			 *************************************************************************************/
			
			bibliotecaDestino = dao.refresh(bibliotecaDestino);
			
			List<RegistroMovimentacaoMaterialInformacional> registros = new ArrayList<RegistroMovimentacaoMaterialInformacional>();
			
			for (Exemplar exemplar : exemplaresTransferencia) {
				
				exemplar = dao.refresh(new Exemplar(exemplar.getId()));
				
				RegistroMovimentacaoMaterialInformacional registroCriado = new RegistroMovimentacaoMaterialInformacional
						(exemplar, (Usuario) movimento.getUsuarioLogado(), new Biblioteca(exemplar.getBiblioteca().getId()), bibliotecaDestino );
				
				if(exemplar.getNumeroPatrimonio() != null &&  movimento.isGerarChamadoPatrimonial()){ // SOMENTE EXEMPLARES TOMBADOS NO PATRIMÔNIO GERA O CHAMADO PATRIMONIAL
					
					dadosTransferencia.add( new DadosTransferenciaMaterialDTO(exemplar.getNumeroPatrimonio()
							, exemplar.getBiblioteca().getUnidade().getCodigo()
							, exemplar.getBiblioteca().getUnidade().getNome()
							, bibliotecaDestino.getUnidade().getCodigo()
							, bibliotecaDestino.getUnidade().getNome()));
					
					registroCriado.setGerouChamadoPatrimonial(true);
				}else{
					registroCriado.setGerouChamadoPatrimonial(false);
				}
				
				registros.add(registroCriado);
				
				dao.update(" UPDATE biblioteca.material_informacional set id_biblioteca = ? where id_material_informacional = ? ", new Object[]{ bibliotecaDestino.getId(), exemplar.getId()}  );
				
				if(exemplar.getNumeroPatrimonio() != null &&  movimento.isGerarChamadoPatrimonial()){
					codigoDeBarraDosExemplaresAbertoChamado.add(exemplar.getCodigoBarras());
				}
				
			}
			
			// GERA O CHAMADO PATRIMONIA NO SIPAC
			if(dadosTransferencia.size() > 0 &&  movimento.isGerarChamadoPatrimonial()){
				try {
					//PatrimonioRemoteService remoto = BibliotecaDelegate.getInstance().getPatrimonioRemoteService();
					
					PatrimonioRemoteService patrimonioInvoker = getBean("patrimonioRemoteServiceInvoker", movimento);
					
					// Não pode passar um usuário nem pessoa do SIGAA para o SIPAC
					Usuario usuario = (Usuario) movimento.getUsuarioLogado();
					
					UsuarioDTO usuarioDTO = new UsuarioDTO();
					usuarioDTO.setId(usuario.getId());
					usuarioDTO.setRamal(usuario.getRamal());
					usuarioDTO.setEmail(usuario.getEmail());
					usuarioDTO.setLogin(usuario.getLogin());
					usuarioDTO.setIdPessoa(usuario.getPessoa().getId());
					usuarioDTO.setNome(usuario.getPessoa().getNome());
					
					numeroChamadoPatrimonial = patrimonioInvoker.geraChamadoPatrimonial(usuarioDTO, dadosTransferencia, deRegistroParaDTO(movimento.getUsuarioLogado().getRegistroEntrada()));
				} catch (RemoteAccessException uee) {
					uee.printStackTrace();
					throw new NegocioException(" Não foi possível gerar o chamado patrimonial para essa transferência. Se preferir, desmarque a opção de gerar o chamado patrimonial, e gere-o manualmente após a transferência.");
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new NegocioException(" Não foi possível gerar o chamado patrimonial para essa transferência. Se preferir, desmarque a opção de gerar o chamado patrimonial, e gere-o manualmente após a transferência.");
				}
			}
			
			
			for (RegistroMovimentacaoMaterialInformacional registraMovimentacao : registros) {
				dao.createNoFlush(registraMovimentacao);
			}
			
		}finally{
			if( dao != null) dao.close();
		}
		
		return new Object[]{numeroChamadoPatrimonial, codigoDeBarraDosExemplaresAbertoChamado};
	}
	
	/**
	 * Método que coverte um objeto do tipo RegistroEntrada para sua respectiva representação DTO.
	 * 
	 * @param registro RegistroEntrada que se deseja converter para DTO.
	 * @return O objeto RegistroEntrada convertido para seu DTO.
	 */
	private RegistroEntradaDTO deRegistroParaDTO(RegistroEntrada registro) {
		RegistroEntradaDTO dto = new RegistroEntradaDTO();
		dto.setId(registro.getId());
		dto.setCanal(registro.getCanal());
		dto.setData(registro.getData());
		dto.setIdUsuario(registro.getUsuario().getId());
		dto.setIP(registro.getIP());
		dto.setIpInternoNat(registro.getIpInternoNat());
		dto.setResolucao(registro.getResolucao());
		dto.setServer(registro.getServer());
		dto.setUserAgent(registro.getUserAgent());
		return dto;
	}
	
	
	/**
	 * Método que valida as regras de negócio referentes ao caso-de-uso Transferir Exemplares Entre Bibliotecas.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoTransfereExemplaresEntreBibliotecas movimento = (MovimentoTransfereExemplaresEntreBibliotecas) mov;
		
		ExemplarDao dao = null;
		
		try{
			
			dao = getDAO(ExemplarDao.class, movimento);
			
			Biblioteca bibliotecaDestino = movimento.getBibliotecaDestino();
			
			if (movimento.getExemplares() == null || movimento.getExemplares().size() == 0) {
				lista.addErro("Não há exemplares na lista ou não foram encontrados.");
				checkValidation(lista);
			}
		
			if(bibliotecaDestino == null || bibliotecaDestino.getId() == -1){
				lista.addErro("Selecione a biblioteca destino dos exemplares.");
				checkValidation(lista);
			}
		
			for (Exemplar e :  movimento.getExemplares()) {
				
				e = dao.refresh(new Exemplar(e.getId()));
				
				if(e.isEmprestado()){
					lista.addErro("O Exemplar: "+e.getCodigoBarras()+" não pode ser transferido, pois está "+e.getSituacao().getDescricao());
					movimento.adicionaExemplaresComErroTransferencia(e);
				}
				
				if( bibliotecaDestino != null  &&  e.getBiblioteca().getId() == bibliotecaDestino.getId()){
					lista.addErro("O Exemplar: "+e.getCodigoBarras()+" não pode ser transferido para a mesma biblioteca");
					movimento.adicionaExemplaresComErroTransferencia(e);
				}
			
				if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
					try{
						
						checkRole(e.getBiblioteca().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
						
					}catch (SegurancaException se) {
						lista.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()
									+ " não tem permissão para transferir o exemplar "+e.getCodigoBarras()+" pois ele pertence a biblioteca: "
									+ e.getBiblioteca().getDescricao());
						movimento.adicionaExemplaresComErroTransferencia(e);
					}
				
				}
				
			}
			
		}finally{
			if( dao != null) dao.close();
			checkValidation(lista);
		}
		
	}

}
