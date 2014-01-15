package br.ufrn.sigaa.arq.tags;


import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibeAlterarProcessoSeletivo;
import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibeConfirmarPagamento;
import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibeGerenciarAgendamento;
import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibeGerenciarInscricoes;
import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibeImprimirQuestionario;
import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibeListarPresenca;
import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibePublicar;
import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibeRemoverProcessoSeletivo;
import static br.ufrn.sigaa.negocio.OperacaoProcessoSeletivoValidator.isExibeSolicitarAlteracao;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;

/** Tag handler que verifica as possíveis operações sobre um processo seletivo.<br>
 * Exemplo de uso:<br>
 * <code>
    <sigaa:permissaoOperarProcesso operacao="..." processoSeletivo="${...}" acesso="${...}" subSistema="${...}">
		Aqui  fica o conteúdoque deseja disponibilizar.
	</sigaa:permissaoOperarProcesso >
 * </code> 
 * @author Mário Rizzi
 *
 */
public class PermissaoOperarProcessoSeletivo extends TagSupport {

	
	private ProcessoSeletivo processoSeletivo;
	private String operacao;
	
	/** Verifica se o usuário tem permissão para realizar uma operação com a turma.
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		
		boolean processaBody = false;
		
		if ("publicar".equalsIgnoreCase(operacao))
			processaBody = isExibePublicar(processoSeletivo);
		else if ("solicitarAlteracao".equalsIgnoreCase(operacao))
			processaBody = isExibeSolicitarAlteracao(processoSeletivo);
		else if ("confirmarPagamento".equalsIgnoreCase(operacao))
			processaBody = isExibeConfirmarPagamento( processoSeletivo);
		else if ("alterarProcesso".equalsIgnoreCase(operacao))
			processaBody = isExibeAlterarProcessoSeletivo(processoSeletivo);
		else if ("removerProcesso".equalsIgnoreCase(operacao))
			processaBody = isExibeRemoverProcessoSeletivo(processoSeletivo);
		else if ("gerenciarInscricoes".equalsIgnoreCase(operacao))
			processaBody = isExibeGerenciarInscricoes(processoSeletivo);
		else if ("listarPresenca".equalsIgnoreCase(operacao))
			processaBody = isExibeListarPresenca(processoSeletivo);
		else if ("imprimirQuestionario".equalsIgnoreCase(operacao))
			processaBody = isExibeImprimirQuestionario(processoSeletivo);
		else if ("gerenciarAgendamento".equalsIgnoreCase(operacao))
			processaBody = isExibeGerenciarAgendamento(processoSeletivo);
		else
			throw new JspTagException("Operação não reconhecida.");

		if (processaBody)
			return EVAL_BODY_INCLUDE;
		else
				return SKIP_BODY;

		
	}
	
	/** Seta o processo seletivo a ser avaliada na tag.
	 * @param turma
	 */
	public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao; 
	}
	
}
