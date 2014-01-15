/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/07/2009
 *
 */
package br.ufrn.sigaa.avaliacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;

/**
 * Movimento que encapsula os dados necessários para o processamento das notas
 * da Avaliação Institucional.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class MovimentoCalculoAvaliacaoInstitucional extends AbstractMovimento{

	/** ID do objeto movimentado. */
	private int id;
	
	/** Indica se o processador deve salvar os resultados. */
	private boolean salvarResultado = false;
	
	/** DocenteTurma a ser processado. */
	private DocenteTurma docenteTurma;
	
	/** TurmaDocenciaAssistida a ser processada. */
	private TurmaDocenciaAssistida turmaDocenciaAssistida;
	
	/** Informações sobre o processamento realizado. */ 
	private ParametroProcessamentoAvaliacaoInstitucional parametroProcessamento;
	
	/** Indica que o o processamento da avaliação é referente à docência assistida. */
	private boolean docenciaAssistida;
	
	/** Retorna o ID do objeto movimentado. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return this.id;
	}

	/** Seta o ID do objeto movimentado. 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Indica se o processador deve salvar os resultados.
	 * @return
	 */
	public boolean isSalvarResultado() {
		return salvarResultado;
	}

	/** Seta se o processador deve salvar os resultados.
	 * @param salvarResultado
	 */
	public void setSalvarResultado(boolean salvarResultado) {
		this.salvarResultado = salvarResultado;
	}

	/** Retorna o DocenteTurma a ser processado.
	 * @return
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	/** Seta o DocenteTurma a ser processado.
	 * @param docenteTurma
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	/** Retorna as informações sobre o processamento realizado.
	 * @return
	 */
	public ParametroProcessamentoAvaliacaoInstitucional getParametroProcessamento() {
		return parametroProcessamento;
	}

	/** Seta as informações sobre o processamento realizado.
	 * @param parametroProcessamento
	 */
	public void setParametroProcessamento(ParametroProcessamentoAvaliacaoInstitucional parametroProcessamento) {
		this.parametroProcessamento = parametroProcessamento;
	}

	public TurmaDocenciaAssistida getTurmaDocenciaAssistida() {
		return turmaDocenciaAssistida;
	}

	public void setTurmaDocenciaAssistida(
			TurmaDocenciaAssistida turmaDocenciaAssistida) {
		this.turmaDocenciaAssistida = turmaDocenciaAssistida;
	}

	public boolean isDocenciaAssistida() {
		return docenciaAssistida;
	}

	public void setDocenciaAssistida(boolean docenciaAssistida) {
		this.docenciaAssistida = docenciaAssistida;
	}

}
