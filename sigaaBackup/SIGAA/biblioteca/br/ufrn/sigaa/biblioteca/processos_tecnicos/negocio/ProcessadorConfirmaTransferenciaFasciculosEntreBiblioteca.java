/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/11/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.ProcessadorMensagemBean;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.OperacaoAutoridazacaoTransferenciaFasciculos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RegistroMovimentacaoMaterialInformacional;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *   Processador que realiza definitivamente a transfer�ncia dos fasc�culos, depois que um bibliotec�rio 
 * da biblioteca de destino verifica e confirmar a transfer�ncia.
 *
 * @author jadson
 * @since 26/11/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorConfirmaTransferenciaFasciculosEntreBiblioteca extends AbstractProcessador{

	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		validate(mov);
		
		MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas movimento = (MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas) mov;
		
		AssinaturaDao dao = null;
		
		List<UsuarioGeral> destinatarios = new ArrayList<UsuarioGeral>();
		Map<Integer, String> dadosMensagem = new HashMap<Integer, String>();
		
		List<String> codigoBarrasFasciculosTransferidos = new ArrayList<String>();
		
		try{
			dao = getDAO(AssinaturaDao.class, movimento);
		
			Integer proximoNumeroFasciculo = null;
			
			// Para cada fasc�culo
			for (Fasciculo f : movimento.getFasciculos()) {
			
				RegistroMovimentacaoMaterialInformacional registro 
				= dao.refresh( new RegistroMovimentacaoMaterialInformacional(f.getIdentificador()));
				
				// Usado caso o usu�rio escolhar mudar o c�digo dos fasc�culos //
				if(proximoNumeroFasciculo == null)
					proximoNumeroFasciculo = registro.getAssinturaDestino().getNumeroGeradorFasciculo();
				
				
				if(f.getOpcaoSelecao().equals(OperacaoAutoridazacaoTransferenciaFasciculos.SIM.ordinal())){    // A transfer�ncia foi confirmada 
					
					registro.setUsuarioAutorizouMovimentacaoMaterial((Usuario) movimento.getUsuarioLogado());
					registro.setDataAutorizacao(new Date());
					registro.setPendente(false);
					
					Fasciculo temp = ( Fasciculo) dao.refresh(registro.getMaterial());
					
					// Realiza a transfer�ncia //
					temp.setAssinatura(registro.getAssinturaDestino());
					temp.setBiblioteca(registro.getAssinturaDestino().getUnidadeDestino());
					
					// Altera o registro de transfer�ncia para n�o ficar mais pendente.
					dao.update(registro);
					
					
					
					if(movimento.isCodigoDeBarrasAcompanhaCodigoNovaAssinatura()){
						proximoNumeroFasciculo = atualizaCodigoBarrasFasciculo(temp, proximoNumeroFasciculo, movimento);
					}
					
					
					// Realiza a transfer�ncia (altera a assinatura e biblioteca do fasc�culo)//
					dao.mudarFasciculoDeAssinatura(temp.getId(), temp.getAssinatura().getId(),  temp.getBiblioteca().getId());
					
					codigoBarrasFasciculosTransferidos.add(temp.getCodigoBarras());
					
				}else{                
					
					if(f.getOpcaoSelecao().equals(OperacaoAutoridazacaoTransferenciaFasciculos.NAO.ordinal())){   // A transfer�ncia N�O foi confirmada
					
						/////  Transforma usu�rio em usu�rio geral /////
						UsuarioGeral destinatarioTemp = new UsuarioGeral(registro.getUsuarioMovimentouMaterial().getId());
						
						if(! destinatarios.contains(destinatarioTemp)){
							destinatarios.add(destinatarioTemp);
							dadosMensagem.put(destinatarioTemp.getId(), " * A transfer�ncia do fasc�culo: "+f.getCodigoBarras()
									+" n�o foi autorizada, motivo : "+f.getInformacao()+". \n");
						}else{
							dadosMensagem.put(destinatarioTemp.getId(), 
									dadosMensagem.get(destinatarioTemp.getId())+" * A transfer�ncia do fasc�culo: "+f.getCodigoBarras()
									+" n�o foi autorizada, motivo : "+f.getInformacao()+". \n");
						}
						
						dao.remove(registro); // N�o precisa guardar a registro da transfer�ncia se n�o houve
					}
					
					/* **************************************************************************************
					 *  Se o usuario escolheu AutorizaTransferenciaFasciculosEntreAssinaturasMBean.AGUARDAR *
					 *  n�o precisa executar nenhuma a��o.
					 * **************************************************************************************/
					
				}
				
				// No final precisa atualizar o n�mero gerador do �ltimo fasc�culo transferido //
				if(movimento.isCodigoDeBarrasAcompanhaCodigoNovaAssinatura()){
					dao.updateField(Assinatura.class, registro.getAssinturaDestino().getId(), "numeroGeradorFasciculo", proximoNumeroFasciculo );
				}
			
				
			}
			
			
			try {
				enviaMensagemTransferenciasNaoAutorizadas(mov, destinatarios, dadosMensagem);
		
			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
				throw new NegocioException(" N�o foi poss�vel enviar a mensagem ao usu�rio que solicitou a transfer�ncia.");
			}
			
		}finally{
			if( dao != null)  dao.close();
		}
		
		return codigoBarrasFasciculosTransferidos;
	}



	/**
	 * <p>Cont�m a l�gica de atualiza��o dos c�digos de barras de fasc�culos transferidos</p>
	 *
	 * <p>Caso n�o seja suplemento tem que atualizar o c�digo de barras de todos os suplementos dele, mas que os suplementos permanecam na assinatura antiga.</p>
	 *
	 * <p>Caso o fasc�culo seja um suplemento, n�o ser� atualizado na transfer�ncia, a atualiza��o vai correr se o fasc�culo principal dele mudar. 
	 * Sen�o mesmo mudando de assinatura permance com o mesmo c�digo.</p>
	 * @throws DAOException 
	 *
	 * @Integer
	 */
	private Integer atualizaCodigoBarrasFasciculo(Fasciculo temp, Integer proximoNumeroFasciculo, MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas movimento) throws DAOException {
		
		FasciculoDao dao = null;
		
		try{
			
			dao = getDAO(FasciculoDao.class, movimento);
		
			if( ! temp.isSuplemento()){
				
				temp.geraCodigoBarrasFasciculo(temp.getAssinatura().getCodigo(), ++proximoNumeroFasciculo);
				
				dao.update("UPDATE biblioteca.material_informacional SET codigo_barras = ? WHERE id_material_informacional = ? ", temp.getCodigoBarras(), temp.getId() );
			
				List<Fasciculo> suplementos = dao.findSuplementosDoFasciculo(temp.getId());
				
				//////// Atualiza o c�digo dos suplementos //////////
				for (Fasciculo suplemento : suplementos) {
					Character caracterSuplemento = suplemento.getCodigoBarras().charAt(suplemento.getCodigoBarras().length()-1);
					dao.update("UPDATE biblioteca.material_informacional SET codigo_barras = ? WHERE id_material_informacional = ? ", temp.getCodigoBarras()+caracterSuplemento, suplemento.getId() );
				}
				
				
			}else{
				
				// Se for suplemento n�o atualiza o c�digo de barras porque vai ter que ficar como o mesmo c�digo de barras do principal
				
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return proximoNumeroFasciculo;
	}

	
	
	
	/**
	 *    M�todo que envia uma mensagem ao bibliotec�rio que tinha solicitado a transfer�ncia de 
	 *  fasc�culos caso a transfer�ncia de algum fasc�culos tenha sido recusada, informando 
	 *  na mensagem o motivo do bibliotec�rio que recusou.
	 */
	private void enviaMensagemTransferenciasNaoAutorizadas(Movimento mov, List<UsuarioGeral> destinatarios, Map<Integer, String> dadosMensagem) 
					throws UnsupportedEncodingException, NegocioException, ArqException, RemoteException{
		
		
		// manda uma mensagem para cada bibliotec�rio cuja solicita��o de transfer�ncia foi rejeitada.
		for (UsuarioGeral destinatario : destinatarios) {
			
			br.ufrn.arq.caixa_postal.Mensagem msg = new br.ufrn.arq.caixa_postal.Mensagem();	
			
			msg.setTitulo(" TRANSFER�NCIA DE FASC�CULOS N�O AUTORIZADA");
			msg.setTipo(br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM);
			msg.setAutomatica(true);
			
			StringBuilder mensagemChamado = new StringBuilder();
		
			mensagemChamado.append(dadosMensagem.get(destinatario.getId()));
			
			mensagemChamado.append("\n\n\n");
			mensagemChamado.append("---\n");
			mensagemChamado.append("Confer�ncia da Transfer�ncia realizada por: \n");
			mensagemChamado.append(mov.getUsuarioLogado().getNome()+" em "+ new SimpleDateFormat("dd/MM/yyyy HH:mm ").format( new Date())+"\n");
			mensagemChamado.append("Ramal: "+(mov.getUsuarioLogado().getRamal() != null ? mov.getUsuarioLogado().getRamal(): "N�O INFORMADO")+"\n");
			mensagemChamado.append("Email: "+(mov.getUsuarioLogado().getEmail() != null ? mov.getUsuarioLogado().getEmail(): "N�O INFORMADO")+"\n");
		
			mensagemChamado.append("\n");
			
			msg.setMensagem(mensagemChamado.toString());
			
			List<UsuarioGeral> destinatariosMensagem = new ArrayList<UsuarioGeral>();
			
			destinatariosMensagem.add(destinatario);
			
			msg.setDestinatarios(destinatariosMensagem);
			
			try {
				msg.setTitulo(URLDecoder.decode(msg.getTitulo().replaceAll("\\+", "&#43;"), "ISO_8859-1"));
				msg.setMensagem(URLDecoder.decode(msg.getMensagem().replaceAll("\\+", "&#43;"), "ISO_8859-1"));
			} catch (IllegalArgumentException e) {
				msg.setTitulo(new String(msg.getTitulo().getBytes(), "ISO_8859-1"));
				msg.setMensagem(new String(msg.getMensagem().getBytes(), "ISO_8859-1"));
			}
			
			msg.setCodMovimento(ArqListaComando.MENSAGEM_ENVIAR);
			
			ProcessadorMensagemBean processadorMensagem = new ProcessadorMensagemBean();
			msg.setUsuarioLogado(mov.getUsuarioLogado());
			msg.setSistema(mov.getSistema());
			processadorMensagem.execute(msg);
		}
	}
	
	
	
	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas m = (MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas) mov;
	
		// Verifica se todos os motivos foram informados pelo usu�rio //
		for (Fasciculo f : m.getFasciculos()) {
			if(f.getOpcaoSelecao().equals(OperacaoAutoridazacaoTransferenciaFasciculos.NAO.ordinal()) && StringUtils.isEmpty(f.getInformacao())){
				erros.addErro(" O motivo do cancelamento da transfer�ncia do fasc�culo "+f.getCodigoBarras()+" n�o foi informado");
			}
		}
		
		checkValidation(erros);
	}

}
