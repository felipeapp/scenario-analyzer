/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 14/11/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;

/**
 * Movimento para cadastro da sele��o de monitores
 * e exclus�o (DESATIVACAO) de monitor.
 *
 * @author ilueny santos
 * @author Victor Hugo
 *
 */
public class DiscenteMonitoriaMov extends AbstractMovimentoAdapter {
	
	/** Discente de monitoria utilizado na movimenta��o.*/
	private DiscenteMonitoria discenteMonitoria;
	
	/** Remove cadastros de resultados da prova seletiva.*/
	private Collection<DiscenteMonitoria> selecoesRemovidas;
	
	/** Remove orienta��es do monitor, utilizado nas altera��es do discente no projeto. */
	private Collection<Orientacao> orientacoesRemovidas;
	
	/** Utilizado para varifica��es na prova seletiva. */
	private ProvaSelecao provaSelecao;
	
	/** Permite verifica��es no calend�rio acad�mico vigente. */
	private CalendarioAcademico calendarioAcademico;
	
	/** Informa o novo tipo de monitoria para os casos de altera��o do v�nculo do discente no projeto. */
	private int novoTipoMonitoria;
	
	/** Permite que opera��es sejam liberadas da valida��o no processador. Valor padr�o = true. */
	private boolean validar = true;
	
	/** Permite que o discente seja finalizado sem ter enviado os relat�rios obrigat�rios. */
	private boolean validarRelatorios = true;


	public Collection<DiscenteMonitoria> getSelecoesRemovidas() {
		return selecoesRemovidas;
	}

	public void setSelecoesRemovidas(Collection<DiscenteMonitoria> selecoesRemovidas) {
		this.selecoesRemovidas = selecoesRemovidas;
	}

	public DiscenteMonitoria getDiscenteMonitoria() {
		return discenteMonitoria;
	}

	public void setDiscenteMonitoria(DiscenteMonitoria discenteMonitoria) {
		this.discenteMonitoria = discenteMonitoria;
	}

	public Collection<Orientacao> getOrientacoesRemovidas() {
		return orientacoesRemovidas;
	}

	public void setOrientacoesRemovidas(Collection<Orientacao> orientacoesRemovidas) {
		this.orientacoesRemovidas = orientacoesRemovidas;
	}

	/**
	 * informa o novo tipo v�nculo do monitor no projeto
	 * usado em casos de mudan�a de tipo de v�nculo do monitor
	 * 
	 * @return
	 */
	public int getNovoTipoMonitoria() {
		return novoTipoMonitoria;
	}

	public void setNovoTipoMonitoria(int novoTipoMonitoria) {
		this.novoTipoMonitoria = novoTipoMonitoria;
	}

	public boolean isValidar() {
		return validar;
	}

	public void setValidar(boolean validar) {
		this.validar = validar;
	}

	public ProvaSelecao getProvaSelecao() {
		return provaSelecao;
	}

	public void setProvaSelecao(ProvaSelecao provaSelecao) {
		this.provaSelecao = provaSelecao;
	}

	public CalendarioAcademico getCalendarioAcademico() {
		return calendarioAcademico;
	}

	public void setCalendarioAcademico(CalendarioAcademico calendarioAcademico) {
		this.calendarioAcademico = calendarioAcademico;
	}

	public boolean isValidarRelatorios() {
		return validarRelatorios;
	}

	public void setValidarRelatorios(boolean validarRelatorios) {
		this.validarRelatorios = validarRelatorios;
	}

}