/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
	 * A lista de reservas que v�o ser canceladas, normalmente quando o pr�prio usu�rio cancelar sua reserva, essa lista s� tem 1 reserva.
	 */
	private List<ReservaMaterialBiblioteca> reservasASeremCanceladas;
	
	/**
	 * Indica que est� ocorrendo um cancelamento pelo pr�prio usu�rio que soliciou a reserva ou n�o.
	 * Caso seja pro um bibliotec�rio, tem-se que realizar algumas verifica��es extras para saber 
	 * se o bibliotec�rio tem autoriza��o para cancelar reservas.
	 */
	private boolean cancelamentoProprioUsuario;

	/** Indica se est� cancelando todas as reservas de um T�tulo, nesse caso n�o precisa dar-se o 
	 * trabalho de ativar as pr�ximas reservas, j� que todas ser�o canceladas.
	 * 
	 * IMPORTANTE: Ou cancela 1 ou cancela todas, sen�o vai ficar dif�cil saber para quais reservas enviar o email de reserva dispon�vel caso a cancelada esteja EM_ESPERA. 
	 * Teria que verificar se a pr�xima est� entre as canceladas, se sim, ativar a pr�xima da pr�xima, e assim sucessivamente.
	 */
	private boolean cancelamentoTodasReservasdoTitulo;
	
	/**
	 * Construtor padr�o.
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
