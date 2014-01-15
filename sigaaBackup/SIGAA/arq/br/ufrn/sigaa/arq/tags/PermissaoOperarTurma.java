package br.ufrn.sigaa.arq.tags;

import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isExibeProcessamentoMatricula;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isExibeProcessamentoRematricula;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteAdicionarReservaSemSolicitacao;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteAjustarTurma;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteAlterar;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteAlterarStatusMatriculaTurma;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteCadastrarNoticia;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteConsolidar;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteDuplicarTurma;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteEnviarEmail;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteFecharTurma;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteGerarDiarioClasse;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteGerarDiarioTurma;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteGerarListaFrequencia;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteGerarNotasTutores;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteGerarPlanilhaNotas;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteListarAlunos;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteListarAlunosImpressao;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteListarNotasAlunos;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteReabrirTurma;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteRemoverTurma;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteVerAgenda;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteVisualizarTurma;
import static br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator.isPermiteVisualizarTurmaVirtual;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ensino.dominio.Turma;

/** Tag handler que verifica as possíveis operações sobre uma turma.<br>
 * Exemplo de uso:<br>
 * <code>
    <sigaa:permissaoOperarTurma operacao="consolidar" turma="${t}">
		<li  id="btnConsolidar">
			<h:commandLink styleClass="noborder" title="Consolidar Turma" action="#{buscaTurmaBean.consolidarTurma}" >
				<f:param name="id" value="#{t.id}" />
				<f:param name="turmasEAD" value="#{ buscaTurmaBean.turmasEAD }" />
				Consolidar Turma
			</h:commandLink>
		</li>
	</sigaa:permissaoOperarTurma>
 * </code> 
 * @author Édipo Elder F. Melo
 *
 */
public class PermissaoOperarTurma extends TagSupport {

	// Atributos da Tag
	/** Turma a ser avaliada. */
	private Turma turma;
	/** String da operação a ser verificada. */
	private String operacao;
	
	/** Verifica se o usuário tem permissão para realizar uma operação com a turma.
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		
		boolean processaBody = false;
		
		try{
			if ("adicionarReservaSemSolicitacao".equalsIgnoreCase(operacao))
				processaBody = isPermiteAdicionarReservaSemSolicitacao(turma);
			else if ("alterar".equalsIgnoreCase(operacao))
				processaBody = isPermiteAlterar(turma);
			else if ("cadastrarNoticia".equalsIgnoreCase(operacao))
				processaBody = isPermiteCadastrarNoticia(turma);
			else if ("consolidar".equalsIgnoreCase(operacao))
				processaBody = isPermiteConsolidar(turma);
			else if ("gerarPlanilhaNotas".equalsIgnoreCase(operacao))
				processaBody = isPermiteGerarPlanilhaNotas(turma);
			else if ("gerarDiarioTurma".equalsIgnoreCase(operacao))
				processaBody = isPermiteGerarDiarioTurma(turma);
			else if ("gerarDiarioClasse".equalsIgnoreCase(operacao))
				processaBody = isPermiteGerarDiarioClasse(turma);
			else if ("gerarListaFrequencia".equalsIgnoreCase(operacao))
				processaBody = isPermiteGerarListaFrequencia(turma);
			else if ("fecharTurma".equalsIgnoreCase(operacao))
				processaBody = isPermiteFecharTurma(turma);
			else if ("listarAlunos".equalsIgnoreCase(operacao))
				processaBody = isPermiteListarAlunos(turma);
			else if ("listarAlunosImpressao".equalsIgnoreCase(operacao))
				processaBody = isPermiteListarAlunosImpressao(turma);
			else if ("gerarNotasTutores".equalsIgnoreCase(operacao))
				processaBody = isPermiteGerarNotasTutores(turma);
			else if ("exibeProcessamentoMatricula".equalsIgnoreCase(operacao))
				processaBody = isExibeProcessamentoMatricula(turma);
			else if ("exibeProcessamentoRematricula".equalsIgnoreCase(operacao))
				processaBody = isExibeProcessamentoRematricula(turma);
			else if ("reabrirTurma".equalsIgnoreCase(operacao))
				processaBody = isPermiteReabrirTurma(turma);
			else if ("removerTurma".equalsIgnoreCase(operacao))
				processaBody = isPermiteRemoverTurma(turma);
			else if ("visualizarTurma".equalsIgnoreCase(operacao))
				processaBody = isPermiteVisualizarTurma(turma);
			else if ("visualizarTurmaVirtual".equalsIgnoreCase(operacao))
				processaBody = isPermiteVisualizarTurmaVirtual(turma);
			else if ("listarNotasAlunos".equalsIgnoreCase(operacao))
				processaBody = isPermiteListarNotasAlunos(turma);
			else if ("alterarStatusMatriculaTurma".equalsIgnoreCase(operacao))
				processaBody = isPermiteAlterarStatusMatriculaTurma(turma);
			else if ("enviarEmail".equalsIgnoreCase(operacao))
				processaBody = isPermiteEnviarEmail(turma);
			else if ("verAgenda".equalsIgnoreCase(operacao))
				processaBody = isPermiteVerAgenda(turma);
			else if ("ajustarTurma".equalsIgnoreCase(operacao))
				processaBody = isPermiteAjustarTurma(turma);
			else if ("duplicarTurma".equalsIgnoreCase(operacao))
				processaBody = isPermiteDuplicarTurma(turma);
			else
				throw new JspTagException("Operação não reconhecida.");
			if (processaBody)
				return EVAL_BODY_INCLUDE;
			else
				return SKIP_BODY;
		
		}catch (DAOException e) {
			throw new JspException(e);
		}
		
	}
	
	/** Seta a turma a ser avaliada na tag.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Seta a operação a ser verificada se o usuário tem permissão para executar.
	 * @param operacao
	 */
	public void setOperacao(String operacao) {
		this.operacao = operacao; 
	}
	
}
