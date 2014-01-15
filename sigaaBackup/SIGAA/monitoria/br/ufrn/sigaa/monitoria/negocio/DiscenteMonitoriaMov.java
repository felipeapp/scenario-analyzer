/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * Movimento para cadastro da seleção de monitores
 * e exclusão (DESATIVACAO) de monitor.
 *
 * @author ilueny santos
 * @author Victor Hugo
 *
 */
public class DiscenteMonitoriaMov extends AbstractMovimentoAdapter {
	
	/** Discente de monitoria utilizado na movimentação.*/
	private DiscenteMonitoria discenteMonitoria;
	
	/** Remove cadastros de resultados da prova seletiva.*/
	private Collection<DiscenteMonitoria> selecoesRemovidas;
	
	/** Remove orientações do monitor, utilizado nas alterações do discente no projeto. */
	private Collection<Orientacao> orientacoesRemovidas;
	
	/** Utilizado para varificações na prova seletiva. */
	private ProvaSelecao provaSelecao;
	
	/** Permite verificações no calendário acadêmico vigente. */
	private CalendarioAcademico calendarioAcademico;
	
	/** Informa o novo tipo de monitoria para os casos de alteração do vínculo do discente no projeto. */
	private int novoTipoMonitoria;
	
	/** Permite que operações sejam liberadas da validação no processador. Valor padrão = true. */
	private boolean validar = true;
	
	/** Permite que o discente seja finalizado sem ter enviado os relatórios obrigatórios. */
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
	 * informa o novo tipo vínculo do monitor no projeto
	 * usado em casos de mudança de tipo de vínculo do monitor
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