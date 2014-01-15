/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 *
 * <p>Passa os dados para o processador</p>
 * 
 * @author jadson
 *
 */
public class MovimentoSolicitaReservaMaterialBiblioteca extends AbstractMovimentoAdapter{

	/**
	 * O t�tulo que ser� reservado pelo Usu�rio
	 */
	private int idTituloQueVaiSerReservado;
	
	/**
	 * O usu�rio que est� solicitando a reserva
	 */
	private UsuarioBiblioteca usuarioSolicitadorReserva;
	
	/**
	 * A lista de reservas j� existentes para o t�tulo selecionado
	 */
	private List<ReservaMaterialBiblioteca> reservasJaExistentes;
	
	
	/**
	 * A data prov�vel que o usu�rio vai conseguir concretizar a reserva.
	 */
	private Date dataPrevisaoEmprestimoMaterial;

	
	/**
	 * Construtor padr�o
	 * 
	 * @param idTituloQueVaiSerReservado
	 * @param usuarioSolicitadorReserva
	 * @param reservasJaExistentes
	 * @param dataPrevisaoEmprestimoMaterial
	 */
	public MovimentoSolicitaReservaMaterialBiblioteca(int idTituloQueVaiSerReservado, UsuarioBiblioteca usuarioSolicitadorReserva,
			List<ReservaMaterialBiblioteca> reservasJaExistentes, Date dataPrevisaoEmprestimoMaterial) {
		super();
		super.setCodMovimento(SigaaListaComando.SOLICITA_RESERVA_MATERIAL_BIBLIOTECA);
		this.idTituloQueVaiSerReservado = idTituloQueVaiSerReservado;
		this.usuarioSolicitadorReserva = usuarioSolicitadorReserva;
		this.reservasJaExistentes = reservasJaExistentes;
		this.dataPrevisaoEmprestimoMaterial = dataPrevisaoEmprestimoMaterial;
	}

	public int getIdTituloQueVaiSerReservado() {
		return idTituloQueVaiSerReservado;
	}

	public UsuarioBiblioteca getUsuarioSolicitadorReserva() {
		return usuarioSolicitadorReserva;
	}

	public List<ReservaMaterialBiblioteca> getReservasJaExistentes() {
		return reservasJaExistentes;
	}

	public Date getDataPrevisaoEmprestimoMaterial() {
		return dataPrevisaoEmprestimoMaterial;
	}
	
	
	
}
