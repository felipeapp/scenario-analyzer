/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 09/04/2012
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.RegistroEntradaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.NotaCirculacaoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PunicaoAtrasoEmprestimoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca.StatusReserva;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoDarBaixaExemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoDarBaixaFasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.ProcessadorDarBaixaExemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.ProcessadorDarBaixaFasciculo;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 * <p>Classe que cont�m as regras de neg�cio para a devolu��o de um empr�stimo de um material perdido pelo usu�rio.</p>
 * 
 * <p>Nesse caso diferentemente da devolu��o normal, o material vai ser baixado ao ser devolvido, a pr�xima reserva do material 
 * caso exista n�o vai ser notificada. </p>
 * 
 * @author jadson
 * 
 * @see ProcessadorDevolveEmprestimo
 */
public class ProcessadorDevolveEmprestimoMaterialPerdido extends ProcessadorCadastro {

	/**
	 * Ver coment�rio na classe pai.
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		long tempo = System.currentTimeMillis();
		
		validate(mov);
		
		MovimentoDevolveEmprestimoMaterialPerdido movimento = (MovimentoDevolveEmprestimoMaterialPerdido) mov;
		UsuarioGeral operador = movimento.getUsuarioLogado();
		
		EmprestimoDao dao = null;
		PermissaoDAO pDao = null;
		RegistroEntradaDao rEDao = null;
		MaterialInformacionalDao materialDao = null;
		NotaCirculacaoDao daoNota = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		/**
		 * Objeto que por padr�o deve ser retornado quando � realizado alguma opera��o relacionado a empr�stimos em circula��o.
		 */
		RetornoOperacoesCirculacaoDTO retornoProcessadorCirculacao = new RetornoOperacoesCirculacaoDTO();
		
		try {
			
			dao = getDAO(EmprestimoDao.class, movimento);
			pDao = getDAO(PermissaoDAO.class, movimento, Sistema.COMUM);
			rEDao = getDAO(RegistroEntradaDao.class, movimento);
			materialDao = getDAO(MaterialInformacionalDao.class, movimento);
			daoNota = getDAO(NotaCirculacaoDao.class, movimento);
		
			// Se atrasou o empr�stimo, deve-se criar uma suspens�o para o mesmo, impedindo o usu�rio
			// de realizar empr�stimos ou renova��es por um determinado tempo.
			List<PunicaoAtrasoEmprestimoStrategy> punicoesStrategy = null;
			
			List<String> mensagensComprovanteDevolucao = new ArrayList<String>();
			List<PunicaoAtrasoEmprestimoBiblioteca> punicoesSofridas = new ArrayList<PunicaoAtrasoEmprestimoBiblioteca>();
			
			operador.setPermissoes(pDao.findPermissoesByUsuario(operador));
			
			operador.setPapeis(new ArrayList <Papel>());
			
			for (Permissao p : operador.getPermissoes())
				operador.getPapeis().add(p.getPapel());
			
			
			// Pega o �ltimo empr�stimo ativo do material, livrando os empr�stimos estornados
			Emprestimo e = dao.findEmprestimoCirculacao(movimento.getIdMaterial(), true, true);		
			
			if (e != null){
			
				verificaPermissaoEBibliotecaEmprestimo(operador, e.getMaterial(), movimento);
				
				ProcessadorDevolveEmprestimo.verificaNotasDeCirculacao(daoNota, e.getMaterial(), retornoProcessadorCirculacao);
				
				e.setDataDevolucao(new Date());
				
				// Se atrasou a entrega
				boolean atrasou = false;
				
				PoliticaEmprestimo politicaEmprestimo = e.getPoliticaEmprestimo();
				
				if (!politicaEmprestimo.isPrazoContadoEmHoras())
					atrasou = CalendarUtils.estorouPrazo(e.getPrazo(), new Date());
				else
					atrasou = CalendarUtils.estorouPrazoConsiderandoHoras(e.getPrazo(), new Date());
				
				if (atrasou){
					
					punicoesStrategy = new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiasPunicao();
					
					for (PunicaoAtrasoEmprestimoStrategy punicaoStrategy : punicoesStrategy) {
						PunicaoAtrasoEmprestimoBiblioteca punicao = punicaoStrategy.criarPunicaoAutomatica(e, e.getPrazo(), new Date());
						mensagensComprovanteDevolucao.add(punicao.getMensagemComprovante());
						punicoesSofridas.add(punicao);
						dao.create(punicao);
					}
				}
			
				e.setUsuarioRealizouDevolucao( (Usuario) operador );
			
				e.setSituacao(Emprestimo.DEVOLVIDO);
			
				// Atualiza o empr�stimo, salvando a data da devolu��o, o usu�rio que realizou a devolu��o e a nova situa��o do empr�stimo.
				dao.updateFields(Emprestimo.class, e.getId(), new String []{"dataDevolucao", "usuarioRealizouDevolucao", "situacao"}, new Object [] {e.getDataDevolucao(), e.getUsuarioRealizouDevolucao(), e.getSituacao()});
			
				
				DevolucaoMaterialPerdido devolucaoAnterior = dao.findByExactField(DevolucaoMaterialPerdido.class, "emprestimo.id", movimento.getDevolucaoMaterialPerdido().getEmprestimo().getId(), true);
				
				if(devolucaoAnterior != null) // se j� havia uma devolu��o para o mesmo empr�timo, apaga a anterior, isso ocorre quando o empr�stimo � estornado.
					dao.remove(devolucaoAnterior);
				
				// Cria o objeto com as informa��es de como o empr�stimo foi devolvido //
				dao.create(movimento.getDevolucaoMaterialPerdido());
				
				
				if(movimento.isRealizarBaixaMaterial()){
					
					/* ************************************************************************************************
					 * Realiza a baixa do material no acervo                                                          *
					 * *************************************************************************************************/
					if(e.getMaterial() instanceof Exemplar){
					
						Exemplar queVaiSerBaixado = (Exemplar) e.getMaterial();
						
						queVaiSerBaixado = dao.refresh(queVaiSerBaixado); // para carregar todas as informa��es antes de atualizar sen�o vai apagar o que n�o t� preenchido
						
						dao.detach(queVaiSerBaixado); // sen�o d� erro nos processadores chamados
						
						queVaiSerBaixado.setMotivoBaixa("Material baixado do acervo em raz�o da perda pelo usu�rio.");
						
						ProcessadorDarBaixaExemplar processadorDarBaixaExemplar = new ProcessadorDarBaixaExemplar();
						MovimentoDarBaixaExemplar movAuxiliar1 = new MovimentoDarBaixaExemplar(queVaiSerBaixado, true, false);
						movAuxiliar1.setCodMovimento(SigaaListaComando.DAR_BAIXA_EXEMPLAR);
						movAuxiliar1.setUsuarioLogado(movimento.getUsuarioLogado());
						movAuxiliar1.setSistema(movimento.getSistema());
						movAuxiliar1.setApplicationContext(mov.getApplicationContext()); // Para poder chamar o MBean remoto do SIPAC
						ListaMensagens mensagemRetornoBaixa = (ListaMensagens) processadorDarBaixaExemplar.execute(movAuxiliar1);
						
						if(mensagemRetornoBaixa != null){
							for(MensagemAviso mensagem: mensagemRetornoBaixa.getMensagens())
								retornoProcessadorCirculacao.addMensagemAosUsuarios(mensagem.getMensagem());
						}
						
					}else{
					
						Fasciculo queVaiSerBaixado =  (Fasciculo) e.getMaterial();
						
						queVaiSerBaixado = dao.refresh(queVaiSerBaixado); // para carregar todas as informa��es antes de atualizar sen�o vai apagar o que n�o t� preenchido
						
						dao.detach(queVaiSerBaixado); // sen�o d� erro nos processadores chamados
						
						queVaiSerBaixado.setMotivoBaixa("Material baixado do acervo em raz�o da perda pelo usu�rio.");
						
						ProcessadorDarBaixaFasciculo processadorDarBaixaFasciculo = new ProcessadorDarBaixaFasciculo();
						MovimentoDarBaixaFasciculo movAuxiliar2 = new MovimentoDarBaixaFasciculo(queVaiSerBaixado, true);
						movAuxiliar2.setCodMovimento(SigaaListaComando.DAR_BAIXA_FASCICULO);
						movAuxiliar2.setUsuarioLogado(movimento.getUsuarioLogado());
						movAuxiliar2.setSistema(movimento.getSistema());
						movAuxiliar2.setApplicationContext(mov.getApplicationContext()); // Para poder chamar o MBean remoto do SIPAC
						processadorDarBaixaFasciculo.execute(movAuxiliar2);
						
					}
					/* ************************************************************************************************/
				
				}else{
					
					SituacaoMaterialInformacional situacaoDisponivel = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoDisponivel", true, true);
					
					////// Atualiza a situa��o do material para DISPON�VEL //////
					List<Integer> idMaterialDevolucao = new ArrayList<Integer>();
					idMaterialDevolucao.add(  e.getMaterial().getId());
					materialDao.atualizaSituacaoDeMateriais(idMaterialDevolucao, situacaoDisponivel.getId());
					
					
					/* ***********************************************************************************
					 *  Caso o sistema trabalhe com reservas eCaso exista reserva para o t�tulo do material
					 *  
					 *  - Muda a pr�xima reserva para o status EM_ESPERA
					 *  - Avisa aos usu�rio que sua reserva se encontra dispon�vel
					 *  
					 * ************************************************************************************/
					
					if(ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
					
						reservaDao = getDAO(ReservaMaterialBibliotecaDao.class, mov);
						ReservaMaterialBiblioteca proximaReserva = reservaDao.findProximaReservaSolicitadaDoTituloDoMaterial(e.getMaterial().getId());
						
						if(proximaReserva != null){
							Date prazoRetirarMaterial  = CalendarUtils.adicionaDias(new Date(), ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_EM_DIAS_USUARIO_TEM_PARA_EFETIVAR_RESERVA));
							
							prazoRetirarMaterial = CirculacaoUtil.prorrogaPrazoConsiderandoFimDeSemanaEInterrupcoesDaBiblioteca( prazoRetirarMaterial, e.getMaterial().getBiblioteca());
							
							reservaDao.updateFields(ReservaMaterialBiblioteca.class, proximaReserva.getId(), new String []{"status", "dataEmEspera", "prazoRetiradaMaterial"}, new Object [] { StatusReserva.EM_ESPERA, new Date(), prazoRetirarMaterial } );
							ProcessadorDevolveEmprestimo.enviaEmailAvisoReservaDisponivel(movimento, proximaReserva, prazoRetirarMaterial);
							
							retornoProcessadorCirculacao.addMensagemAosUsuarios("ATEN��O o material "+e.getMaterial().getCodigoBarras()+" est� reservado !  "
									+"( pr�ximo usu�rio: "+proximaReserva.getUsuarioReserva().getNome()+","
									+" prazo de retirada do material: "+new SimpleDateFormat("dd/MM/yyyy").format(prazoRetirarMaterial)+" )");
							
						}
					}
				}
				
				
				
				/* ************************************************************************************************/
				
				
				
				
				
				retornoProcessadorCirculacao.emprestimoRetornado =  CirculacaoUtil.montaInformacoesEmprestimoDevolvido(e, punicoesSofridas );
				
				retornoProcessadorCirculacao.addOperacaoRealizada( new OperacaoBibliotecaDto (OperacaoBibliotecaDto.OPERACAO_DEVOLUCAO, e.getMaterial().getId(), e.getId() , e.getMaterial().getCodigoBarras()
						,  BibliotecaUtil.obtemDadosMaterialInformacional( e.getMaterial().getId() ) , e.getDataDevolucao(),  e.getPrazo()  )    );
				
				retornoProcessadorCirculacao.mensagensComprovanteDevolucao = mensagensComprovanteDevolucao;
				
				ProcessadorDevolveEmprestimo.enviaEmailConfirmacaoDevolucao(movimento,  retornoProcessadorCirculacao.getOperacoesRealizadas(), retornoProcessadorCirculacao.emprestimoRetornado, retornoProcessadorCirculacao.mensagensComprovanteDevolucao);
				
				return retornoProcessadorCirculacao;
			
			}
			
			throw new NegocioException("Empr�stimo n�o encontrado.");
			
		} catch ( NegocioException re){           // Erros nas regras de neg�cio.
			throw re;
		} catch (DAOException de){                // Erros de acesso ao banco
			de.printStackTrace();
			throw de;
		} catch (Exception e){                   // Erros n�o imaginados
			e.printStackTrace();
			throw new ArqException(e.getMessage());	
		} finally {
			if (dao != null )  dao.close();
			if (rEDao != null) rEDao.close();
			if (pDao != null)  pDao.close();
			if (materialDao != null)  materialDao.close();
			if (daoNota != null) daoNota.close();
			if (reservaDao != null) reservaDao.close();
			
			System.out.println("    ***    Devolver empr�stimo de material perdido demorou: "+ (System.currentTimeMillis()-tempo) +" ms       ***     ");  // tempo <= 1s
		}
		
	}

	
	/** 
	 * <p> Verifica se o material pertence � biblioteca do operador para permitir empr�stimo </p> 
	 * <p> <i> ( setor de informa��o e refer�ncia pode fazer empr�stimos institucionais de materiais ) </i> </p>
	 *
	 * @throws SegurancaException
	 */
	private void verificaPermissaoEBibliotecaEmprestimo(UsuarioGeral operador,  MaterialInformacional material, MovimentoDevolveEmprestimoMaterialPerdido movimento)throws NegocioException{
		
			
		if(! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){ // Administradores gerais podem operar em qualquer biblioteca
		
			// Caso o usu�rio n�o tenha entre o material para substituir o perdido apenas os usu�rios com papel de    //
			// bibliotec�rio podem autorizar essa devolu��o                                                           //
			if(movimento.getDevolucaoMaterialPerdido().getTipo() == DevolucaoMaterialPerdido.TipoDevolucaoMaterialPerdido.USUARIO_NAO_ENTREGOU_MATERIAL_SUBSTITUTO ){
				try {
				
					UFRNUtils.checkRole(operador, material.getBiblioteca().getUnidade(),
							SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
							SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
					);
				
				} catch (SegurancaException ex){
					throw new NegocioException ("O senhor(a) n�o tem permiss�o para devolver empr�stimos de materiais perdidos nos quais o usu�rio n�o substituiu o material, ou o material de c�digo de barras: "+ material.getCodigoBarras() +" n�o pertence a uma biblioteca onde o senhor(a) possua permiss�o para operar.");
				}
			}else{
				
				// Caso o usu�rio tenha entregue o material, qual um de circula��o ou informa��o e refer�ncia pode devolver //
				try {
					
					UFRNUtils.checkRole(operador, material.getBiblioteca().getUnidade(),
							SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
							SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
							SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
							SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF
					);
					
				} catch (SegurancaException ex){
					throw new NegocioException ("O material de c�digo de barras: "+ material.getCodigoBarras() +" n�o pertence a uma biblioteca onde o senhor(a) possua permiss�o para operar.");
				}
			}
		}
		
	}
	
	
	
	/**
	 * Ver coment�rio na classe pai.
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoDevolveEmprestimoMaterialPerdido movimento = (MovimentoDevolveEmprestimoMaterialPerdido) mov;
		
		
		if(movimento.getDevolucaoMaterialPerdido().isUsuarioNaoEntregouMaterial()){
			if( StringUtils.isEmpty(movimento.getDevolucaoMaterialPerdido().getMotivoNaoEntregaMaterial() ) ){
				throw new NegocioException("Informe o motivo do usu�rio n�o ter entre um material substituto.");
			}
		}
		
		if(movimento.getDevolucaoMaterialPerdido().isUsuarioEntregouMaterialSimilar() && movimento.isRealizarBaixaMaterial()){
			throw new NegocioException("A baixa do material s� pode ser realizada se o usu�rio entregou um material equivalente ou n�o repos o material perdido. ");
		}
		
		
	}

}
