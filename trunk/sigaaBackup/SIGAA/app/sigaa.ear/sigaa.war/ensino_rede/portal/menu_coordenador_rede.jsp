<%@page import="br.ufrn.sigaa.pessoa.dominio.Discente"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<h:form>
<input type="hidden" id="jscook_action" name="jscook_action" />
<div id="menu-dropdown">
<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">

	<t:navigationMenuItem itemLabel="Cadastros" icon="/img/graduacao/coordenador/cadastro.gif">
	
		<t:navigationMenuItem itemLabel="Cadastro de Docentes">
			<t:navigationMenuItem itemLabel="Solicitar Cadastro de Docente" action="#{solicitacaoDocenteRedeMBean.iniciarCadastrar}" id="PreCadastroDocentRede"/>
			<t:navigationMenuItem itemLabel="Solicitar Alteração da Situação do Docente" action="#{solicitacaoDocenteRedeMBean.listarDocentes}" id="iniciarAltDocenteRede"/>				
			<t:navigationMenuItem itemLabel="Atualizar Dados Pessoais" action="#{docenteRedeMBean.iniciarAtualizarDadosPessoaisCoordenador}" id="atualizarDadosPessoaisDocente"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Coordenação Unidade">
			<t:navigationMenuItem itemLabel="Atualizar Dados da Coordenação" action="#{coordenadorUnidadeMBean.atualizarCoordenacao }" id="PreAlterarCoordenacao" />
			<t:navigationMenuItem itemLabel="Atualizar Dados da Vice-Coordenação" action="#{coordenadorUnidadeMBean.atualizarViceCoordenacao }" id="PreAlterarviceCoordenacao" />
			<t:navigationMenuItem itemLabel="Cadastrar Secretário(a)" action="#{coordenadorUnidadeMBean.iniciarCadastrarSecretaria }" id="PreCadastrarSecretarioUnidade" />
			<t:navigationMenuItem itemLabel="Atualizar Dados da Secretaria" action="#{coordenadorUnidadeMBean.alterarSecretaria }" id="PreAlterarSecretarioUnidade"/>
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Aluno" icon="/img/graduacao/coordenador/aluno.png">
		<t:navigationMenuItem itemLabel="Consultar Discente" action="#{consultarDiscenteMBean.iniciarConsultar}" id="consultarDiscente"/>
		<t:navigationMenuItem itemLabel="Alterar Dados Pessoais" action="#{cadastroDiscenteRedeMBean.iniciarAtualizarDadosPessoais}" id="alterarDadosPessoaisAlunoe"/>
		<t:navigationMenuItem itemLabel="Vínculo" id="vinculoPrograma">
			<t:navigationMenuItem itemLabel="Confirmar Vínculo" action="#{confirmacaoVinculoMBean.iniciarCoordenadorCampus}" id="confirmarViculo"/>
			<t:navigationMenuItem itemLabel="Trancar" action="#{trancamentoProgramaEnsinoRedeMBean.iniciar}" id="trancarPrograma" split="true" />
			<t:navigationMenuItem itemLabel="Retornar Discente de Trancamento" action="#{retornarTrancamentoProgramaRedeMBean.iniciar}" id="retornarTrancarPrograma" />
			<t:navigationMenuItem itemLabel="Estornar Operação" action="#{estornaOperacaoRedeMBean.iniciar}" id="estornarOperacao" />
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Turmas" icon="/img/graduacao/coordenador/turma.png">
			<t:navigationMenuItem itemLabel="Criar Turma" action="#{ turmaRedeMBean.iniciarCadastrarPortal }"  icon="/img/graduacao/coordenador/turma_sol.png" id="criarTurmas"/>
			<t:navigationMenuItem itemLabel="Listar/Alterar Turma" action="#{turmaRedeMBean.iniciarAlterarCoordenacao}" itemDisabled="false" id="consultaDeturmas"/>
			<t:navigationMenuItem itemLabel="Solicitar Turma de Outro Programa" action="#{ solicitacaoTurma.iniciar }" icon="/img/graduacao/coordenador/turma_sol.png" rendered="false" id="solicitacaoTurmaOutroPeriodo"/>
			<t:navigationMenuItem itemLabel="Visualizar Solicitações Enviadas" action="#{ solicitacaoTurma.listar }" icon="/img/graduacao/coordenador/listar.png" rendered="false" id="visualizacaoSolicitacEnviadas"/>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Relatórios" icon="/img/graduacao/coordenador/relatorios.png" itemDisabled="#{acesso.secretarioCentro}" >
		<t:navigationMenuItem id="turmas" itemLabel="Turmas">
			<t:navigationMenuItem itemLabel="Relatório de Docentes por Turmas" action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioTurmas }" id="relatorioDocenteTurma"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="alunos" itemLabel="Alunos">
			<t:navigationMenuItem itemLabel="Discente por Status" action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioDiscentePorUnidade }" id="relatorioStatus"/>
			<t:navigationMenuItem itemLabel="Contato dos Discentes" action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioContatoDiscente }" id="relatorioContatoDiscente"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="docentes" itemLabel="Docentes">
			<t:navigationMenuItem itemLabel="Docentes da Unidade" action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioDocentes }" id="relatorioDocentesDaUnidade"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="outros" itemLabel="Outros">
			<t:navigationMenuItem itemLabel="Desempenho por Disciplina" action="#{ relatoriosEnsinoRedeMBean.iniciarRelatorioDesempenho }" id="relatorioDesempenhoDisciplina"/>
		</t:navigationMenuItem>				
	</t:navigationMenuItem>

</t:jscookMenu>

	</div>
	</h:form>
<script>
function redirectManual(){
	return window.open("${linkPublico.urlDownload}/manual_nee_solicitacao_apoio.pdf","_blank");
}
</script>