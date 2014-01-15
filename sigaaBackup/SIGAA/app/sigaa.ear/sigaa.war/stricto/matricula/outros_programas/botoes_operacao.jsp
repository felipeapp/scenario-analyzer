<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<c:if test="${!matriculaGraduacao.discenteLogado}">
	<div class="infoAltRem">
		${matriculaGraduacao.discente.matriculaNome}
	</div>
</c:if>

<div id="wrapper-menu-matricula">

<h:form id="formBotoesSuperiores">
	<c:if test="${pagina == 'turmas_selecionadas' && (matriculaGraduacao.solicitacaoMatricula || matriculaGraduacao.alunoRecemCadastrado)}">
		<div class="descricaoOperacao"> 
			<span>
			Caro(a) Aluno(a),
			<p>	
				<b>Para efetivar sua solicita��o de matr�cula � necess�rio pressionar o bot�o CONFIRMAR MATR�CULAS. Ap�s este procedimento ser� poss�vel imprimir o comprovante da sua solicita��o, que dever� ser armazenado.</b>
			</p>
			</span>	    
		</div> 
	</c:if>
	<br/>
<table id="menu-matricula">
	<tr>
	</tr>
	<tr>
	<td>
		<table class="menuMatricula">
			<tr>
				<c:if test="${matriculaGraduacao.discente.stricto && matriculaGraduacao.solicitacaoMatricula}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/tela_inicial.png" /><br />
						<h:commandLink value="Voltar � tela inicial" action="#{matriculaStrictoBean.iniciar}" immediate="true" id="btaoVoltarTelaInicial"/>
					</td>
				</c:if>

				<c:if test="${matriculaGraduacao.exibirTurmasSelecionadas and pagina != 'turmas_selecionadas'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/turmas_selecionadas.png" /><br />
						<c:if test="${pagina == 'turmas_selecionadas'}">
							<span class="destaque">Ver as turmas selecionadas</span>
						</c:if>
						<c:if test="${pagina != 'turmas_selecionadas'}">
						<h:commandLink value="Ver as turmas selecionadas"  action="#{matriculaOutroProgramaStrictoBean.telaSelecaoTurmas}"  id="verTurmasSelecionadass"/>
						</c:if>
					</td>
				</c:if>
			</tr>
		</table>
	</td>

	<c:if test="${pagina == 'turmas_selecionadas'}">
		<c:if test="${matriculaGraduacao.solicitacaoMatricula || matriculaGraduacao.alunoRecemCadastrado}">
			<td class="botoes confirmacao" id="botoes_confirmacao">
				<h:commandButton title="Confirmar Matr�culas" image="/img/graduacao/matriculas/salvar.gif" id="botaoSubmissao" action="#{matriculaGraduacao.submeterSolicitacoes}" alt="Confirmar Matr�culas"/>
				<br />
				<h:commandLink title="Confirmar Matr�culas" value="Confirmar Matr�culas" id="linkSubmissao" action="#{matriculaGraduacao.submeterSolicitacoes}" onclick="bloquearSubmissao();"/> 
			</td>
		</c:if>
		<td class="botoes nao_salvar">
			<h:commandButton id="cancelOp" title="Cancelar" image="/img/graduacao/matriculas/cancelar.gif" onclick="#{confirm}" action="#{matriculaGraduacao.cancelarMatricula}" alt="Cancelar"/>
			<br />
			<h:commandLink id="sairSemSalvar" title="Sair sem salvar" value="Sair sem salvar" onclick="#{confirm}" action="#{matriculaGraduacao.cancelarMatricula}" />
		</td>
	</c:if>
	</tr>
</table>
</h:form>
</div>