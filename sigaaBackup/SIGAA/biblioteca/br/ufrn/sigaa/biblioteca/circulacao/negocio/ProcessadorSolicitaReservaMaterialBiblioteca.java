/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;

/**
 *
 * <p>Processador que valida as regras de neg�cio e cria uma reserva de material da biblioteca.</p>
 *
 * <p> 
 * Regras de neg�cio das reservas:
 * <ol>
 *     <li> Usu�rio bloqueado n�o pode reservar material. </li>
 *     <li> Usu�rio suspenso ou com multa n�o paga n�o pode reservar material. </li>
 *     <li> Aqui temos um certo limite para n�mero de reservas, se ultrapassar n�o pode reservar tamb�m </li>
 *     <li> Aqui temos uma restri��o para reservar um livro com apenas um exemplar </li>
 *     <li> O usu�rio n�o pode realizar uma reserva de um livro que j� foi reservado por ele, n�o pode ter duas reservas ativas ao mesmo tempo
 *     do mesmo livro. </li>
 *     <li> O usu�rio n�o pode realizar uma reserva de um livro que j� est� emprestado para ele. </li>
 *     <li> O usu�rio n�o pode realizar uma reserva de um livro que tenha exemplar dispon�vel. (desconsiderando os anexos) </li>
 *     <li> Verificar se o Status do material aceita reserva </li>
 * </ol>
 * </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorSolicitaReservaMaterialBiblioteca extends AbstractProcessador{

	
	/**
	 * <p>Mensagem padr�o mostrada ao usu�rio neste caso de uso, se ele estiver com penalidades. </p>
	 * 
	 * <p>Declarado em uma vari�vel porque � usado aqui e no procesador </p>
	 */
	public static final String MENAGEM_PADRAO_USUARIO_COM_PEDENCIAS_PARA_SOLICITAR_RESERVA = "O usu�rio(a), possui penalidades no sistema de bibliotecas e n�o pode solicitar reservas.";
	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		////////////////////////////////////////////////////////////////////////////
		// Realiza todos as verifica��es das regras de uma reserva
		//
		// Caso sejam atendidas todas, cria a reserva solicitada
		///////////////////////////////////////////////////////////////////////////
		validate(mov);
		
		GenericDAO dao = null;
		
		try{
		
			dao = getGenericDAO(mov);
			
			MovimentoSolicitaReservaMaterialBiblioteca movimento 
				= (MovimentoSolicitaReservaMaterialBiblioteca) mov;
			
			ReservaMaterialBiblioteca reserva = new ReservaMaterialBiblioteca(new TituloCatalografico(movimento.getIdTituloQueVaiSerReservado())
					, movimento.getUsuarioSolicitadorReserva(), ReservaMaterialBiblioteca.StatusReserva.SOLICITADA
					, new Date(), movimento.getDataPrevisaoEmprestimoMaterial() ) ;
			
			dao.create(reserva);
		
		}finally{
			if (dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		
		MovimentoSolicitaReservaMaterialBiblioteca movimento 
			= (MovimentoSolicitaReservaMaterialBiblioteca) mov;
		
		UsuarioBiblioteca usuarioSolicitadorReserva =  movimento.getUsuarioSolicitadorReserva();
		
		if(! ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
			throw new NegocioException("N�o � poss�vel solicitar uma nova reserva, pois o sistema n�o est� configurado para utilizar esse recurso. ");
		}
		
		VerificaSituacaoUsuarioBibliotecaUtil.verificaVinculoUtilizadoAtivo(usuarioSolicitadorReserva);
		
		// Usu�rios bloqueados n�o pode solicitar reservas, n�o precisa testar as demais condi��es
		String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioSolicitadorReserva);
		if(motivoBloqueio != null){
			throw new NegocioException("O usu�rio est� bloqueado para utilizar os servi�os de circula��o da biblioteca, motivo: "+motivoBloqueio);
		}
		
		// Usu�rios suspensos ou  com multas n�o pagas n�o podem solicitar reservas
		List<SituacaoUsuarioBiblioteca> situacoes = VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(
				usuarioSolicitadorReserva.getIdentificadorPessoa()
				, usuarioSolicitadorReserva.getIdentificadorBiblioteca() );
		
		if(situacoes.size() > 0){
			throw new NegocioException(MENAGEM_PADRAO_USUARIO_COM_PEDENCIAS_PARA_SOLICITAR_RESERVA);
		}
		
		ReservaMaterialBibliotecaUtil.verificaQuantidadeMaximaDeReservasDoUsuario(usuarioSolicitadorReserva.getId());
		
		ReservaMaterialBibliotecaUtil.verificaExisteQuantidadeMinimaParaSolicitarReserva(movimento.getIdTituloQueVaiSerReservado());
		
		for (ReservaMaterialBiblioteca reserva : movimento.getReservasJaExistentes()) {
			if(reserva.getUsuarioReserva().getId() == usuarioSolicitadorReserva.getId()){
				throw new NegocioException("O usu�rio j� realizou a reserva desse material.");
			}
		}
		
		ReservaMaterialBibliotecaUtil.verificaUsuarioJaPossuiMaterialEmprestado(usuarioSolicitadorReserva.getId(), movimento.getIdTituloQueVaiSerReservado());
		
		ReservaMaterialBibliotecaUtil.verificaExisteMaterialParaSolicitarReservaNoAcervo(movimento.getIdTituloQueVaiSerReservado());
		
	}

}
