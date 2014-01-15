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

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;

/**
 *
 * <p>Passa os dados para o processador que vai cancelar a reserva </p>
 * 
 * @author jadson
 *
 */
public class MovimentoCancelaReservaMaterialBiblioteca extends AbstractMovimentoAdapter{

	/**
	 * A lista de reservas que vão ser canceladas, normalmente quando o próprio usuário cancelar sua reserva, essa lista só tem 1 reserva.
	 */
	private List<ReservaMaterialBiblioteca> reservasASeremCanceladas;
	
	/**
	 * Indica que está ocorrendo um cancelamento pelo próprio usuário que soliciou a reserva ou não.
	 * Caso seja pro um bibliotecário, tem-se que realizar algumas verificações extras para saber 
	 * se o bibliotecário tem autorização para cancelar reservas.
	 */
	private boolean cancelamentoProprioUsuario;

	/** Indica se está cancelando todas as reservas de um Título, nesse caso não precisa dar-se o 
	 * trabalho de ativar as próximas reservas, já que todas serão canceladas.
	 * 
	 * IMPORTANTE: Ou cancela 1 ou cancela todas, senão vai ficar difícil saber para quais reservas enviar o email de reserva disponível caso a cancelada esteja EM_ESPERA. 
	 * Teria que verificar se a próxima está entre as canceladas, se sim, ativar a próxima da próxima, e assim sucessivamente.
	 */
	private boolean cancelamentoTodasReservasdoTitulo;
	
	/**
	 * Construtor padrão.
	 * @param idReserva
	 * @param motivoCancelamento
	 * @param usuarioCancelamento
	 * @param cancelamentoProprioUsuario
	 */
	public MovimentoCancelaReservaMaterialBiblioteca(List<ReservaMaterialBiblioteca> reservasASeremCanceladas, boolean cancelamentoProprioUsuario, boolean cancelamentoTodasReservasdoTitulo) {
		this.reservasASeremCanceladas = reservasASeremCanceladas;
		this.cancelamentoProprioUsuario = cancelamentoProprioUsuario;
		this.cancelamentoTodasReservasdoTitulo = cancelamentoTodasReservasdoTitulo;
		setCodMovimento(SigaaListaComando.CANCELA_RESERVA_MATERIAL_BIBLIOTECA);
		
	}
	

	public List<ReservaMaterialBiblioteca> getReservasASeremCanceladas() {
		return reservasASeremCanceladas;
	}

	public boolean isCancelamentoProprioUsuario() {
		return cancelamentoProprioUsuario;
	}

	public boolean isCancelamentoTodasReservasdoTitulo() {
		return cancelamentoTodasReservasdoTitulo;
	}
	
	
	
}
