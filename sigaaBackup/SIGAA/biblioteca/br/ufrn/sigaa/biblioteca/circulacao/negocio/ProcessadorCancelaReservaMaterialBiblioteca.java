/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca.StatusReserva;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;

/**
 *
 * <p> Processador que realiza as ações para cancelar uma reserva manualmente. Ou pelo próprio usuário 
 * que a criou ou pelo bibliotecário com algum motivo que a justifique. </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorCancelaReservaMaterialBiblioteca extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCancelaReservaMaterialBiblioteca 
			movimento = (MovimentoCancelaReservaMaterialBiblioteca) mov;
		
		List<ReservaMaterialBiblioteca> reservasASeremCanceladas = movimento.getReservasASeremCanceladas();
		
		ReservaMaterialBibliotecaDao dao = null;
		
		try{
			dao = getDAO(ReservaMaterialBibliotecaDao.class, movimento);
		
			
			for(ReservaMaterialBiblioteca reservaASerCancelada: reservasASeremCanceladas){
				
				// Cancela a reserva
				dao.updateFields(ReservaMaterialBiblioteca.class, reservaASerCancelada.getId()
						, new String[]{"status", "dataCancelamento", "usuarioCancelamento.id", "motivoCancelamento"}
						, new Object[]{StatusReserva.CANCELADA_MANUALMENTE, new Date(), movimento.getUsuarioLogado().getId(), reservaASerCancelada.getMotivoCancelamento()});
			
				
				// Atualiza a data de previsão das reservas que veem depois da reserva cancelada para o mesmo material //
				// E caso a reserva cancelada esteja EM_ESPERA, atualiza o status da reserva imediatamente posterior para EM_ESPERA
				// Comunicando o usuário
				
				List<ReservaMaterialBiblioteca> reservasPosteriores = dao.findAllReservasAtivasDoMesmoTituloPosterioresAReserva(
											reservaASerCancelada.getTituloReservado().getId(), reservaASerCancelada.getDataSolicitacao());
				
				Date dataPrevisaoAnterior = reservaASerCancelada.getDataPrevisaoRetiradaMaterial();
				
				
				// Se está cancelando todas as reservas, não precisa ativas as próximas //
				if(movimento.isCancelamentoTodasReservasdoTitulo()){
				
					// IMPORTANTE :  Se cancelar uma reserva EM_ESPERA, deve ativar a próxima reserva da fila
					boolean ativarAproximaReserva = false;
					
					if (reservaASerCancelada.isReservaEmEspera())
						ativarAproximaReserva = true;
					
					for (ReservaMaterialBiblioteca proximaReserva : reservasPosteriores) {
						dao.updateFields(ReservaMaterialBiblioteca.class, proximaReserva.getId(), new String[]{"dataPrevisaoRetiradaMaterial"}, new Object[]{dataPrevisaoAnterior});
						dataPrevisaoAnterior = proximaReserva.getDataPrevisaoRetiradaMaterial();
						
						
						if(ativarAproximaReserva){
							Date prazoRetirarMaterial = ReservaMaterialBibliotecaUtil.calculaPrazoRetiradaProximaReserva(proximaReserva.getTituloReservado().getId());
							dao.updateFields(ReservaMaterialBiblioteca.class, proximaReserva.getId(), new String[]{"status", "dataEmEspera", "prazoRetiradaMaterial"}, new Object[]{StatusReserva.EM_ESPERA, new Date(), prazoRetirarMaterial, });
							ativarAproximaReserva = false; // Só faz para a próxima reserva
							enviaEmailAvisoRetiradaMaterial(movimento, proximaReserva.getUsuarioReserva().getId(), proximaReserva.getTituloReservado().getId(), proximaReserva.getId(), prazoRetirarMaterial );
						}
					}
				}
				
				if(! movimento.isCancelamentoProprioUsuario()) {
					enviaEmailConfirmacaoCancelamentoReserva(mov, reservaASerCancelada);
				}
			
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * Envia o para o informar ao próximo usuário da fila que sua reserva está disponível 
	 */
	private void enviaEmailAvisoRetiradaMaterial(MovimentoCancelaReservaMaterialBiblioteca 
			movimento, int idUsuarioBiblioteca, int idTitulo, int idReserva, Date prazoRetirarMaterial) throws DAOException{
		EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
		
		UsuarioBibliotecaDao dao = null;
		
		try{
			dao = getDAO(UsuarioBibliotecaDao.class, movimento); 
			Object[] informacoesUsuario = dao.findNomeEmailUsuarioBiblioteca(new UsuarioBiblioteca(idUsuarioBiblioteca));
			ReservaMaterialBibliotecaUtil.enviaEmailReservaDisponivel(sender, (String)informacoesUsuario[0], (String)informacoesUsuario[1], idReserva, idTitulo , prazoRetirarMaterial);
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	/**
	 * Envia e-mail para o usuário do empréstimo.
	 * 
	 * @throws DAOException 
	 */
	private void enviaEmailConfirmacaoCancelamentoReserva(Movimento mov, ReservaMaterialBiblioteca reservaCancelada) throws DAOException {
	
			// informacoesUsuario[0] == nome Usuario
			// informacoesUsuario[1] == email Usuario
			Object[] informacoesUsuario = getDAO( UsuarioBibliotecaDao.class, mov).findNomeEmailUsuarioBiblioteca(reservaCancelada.getUsuarioReserva());
			
			
			String assunto = " Aviso de Cancelamento de Reserva ";
			String titulo = " Cancelamento de reserva ";
			String mensagemUsuario = "A sua reserva para o Título:  <i>"+BibliotecaUtil.obtemDadosResumidosTitulo(reservaCancelada.getTituloReservado().getId())+"</i> foi cancelada.";
			
			String mensagemNivel1Email =  " Devido ao motivo: ";
			String mensagemNivel3Email =  reservaCancelada.getMotivoCancelamento();
			
			new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
					, EnvioEmailBiblioteca.AVISO_RESERVAS_MATERIAL, mensagemUsuario, mensagemNivel1Email, null, mensagemNivel3Email, null
					, null, null,  null, null);
	}

	
	

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCancelaReservaMaterialBiblioteca 
			movimento = (MovimentoCancelaReservaMaterialBiblioteca) mov;
		
		List<ReservaMaterialBiblioteca> reservasASeremCanceladas = movimento.getReservasASeremCanceladas();
		
		ReservaMaterialBibliotecaDao dao = null;
		
		try{
			dao = getDAO(ReservaMaterialBibliotecaDao.class, movimento);
		
			for(ReservaMaterialBiblioteca reservaASerCancelada: reservasASeremCanceladas){
			
				 // Erro do programador, os dados deveriam ser passados //
				if( reservaASerCancelada == null || reservaASerCancelada.getId() == 0 || reservaASerCancelada.getTituloReservado() == null 
						|| reservaASerCancelada.getTituloReservado().getId() == 0
						|| reservaASerCancelada.getDataPrevisaoRetiradaMaterial() == null 
						|| reservaASerCancelada.getDataSolicitacao() == null  ){
					throw new IllegalArgumentException("Erro ao passar os dados da reserva que precisa ser cancelada");
				}
				
				
				if(StringUtils.isEmpty(reservaASerCancelada.getMotivoCancelamento() ) ){
					erros.addErro("O motivo do cancelamento deve ser informado");
				}
				
				if(! movimento.isCancelamentoProprioUsuario()){
					
					// Informação e referência também podem cancelar reservas para o caso de materiais perdisos nos empréstios institucionais //
					if( ! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
							, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
							, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO)){
						erros.addErro("Você não possui permissões para cancelar reservas de materiais");
					}
					
				}
					
					
				/* Caso o usuário volte no navegador e tente cancelar uma reserva já cancelada anteriormente */
				if( ! dao.isReservaAtivaNoBanco(reservaASerCancelada.getId()) ){
					erros.addErro("A reserva selecionada não pode ser cancelada porque ele não se encontra mais ativa");
				}
			
			
			}
		
		}finally{
			if(dao != null) dao.close();		
		}
		
		checkValidation(erros); // dispara execeção caso contenha algum erro
		
	}

}
