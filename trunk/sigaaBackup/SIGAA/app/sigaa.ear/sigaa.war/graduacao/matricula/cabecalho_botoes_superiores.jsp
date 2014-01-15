<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<c:if test="${!matriculaGraduacao.discenteLogado}">
	<div class="infoAltRem">
		${matriculaGraduacao.discente.matriculaNome}
		<c:if test="${matriculaGraduacao.matriculaEAD}">
			<br>
			${matriculaGraduacao.discente.polo.descricao}
		</c:if>
		<c:if test="${ matriculaGraduacao.compulsoria }">
			<br/>
			Ano-Período: ${matriculaGraduacao.calendarioParaMatricula.ano}-${matriculaGraduacao.calendarioParaMatricula.periodo}
		</c:if>
	</div>
</c:if>

<div id="wrapper-menu-matricula">

<h:form id="formBotoesSuperiores">
<c:if test="${ matriculaGraduacao.discenteLogado && !matriculaGraduacao.alunoEspecial && !matriculaGraduacao.matriculaTurmasNaoOnline &&
			not empty matriculaGraduacao.discente.curso and !matriculaGraduacao.matriculaFerias and pagina != 'turmas_curriculo' and pagina != 'turmas_programa'}">
	<div class="descricaoOperacao"> 
		<span>
		Caro(a) Aluno(a),
		<p>	
			<b>Para efetivar sua solicitação de matrícula é necessário pressionar o botão CONFIRMAR MATRÍCULAS. Após este procedimento será possível imprimir o comprovante da sua solicitação, que deverá ser armazenado.</b>
		</p>
		</span>	    
	</div> 
</c:if>
<table id="menu-matricula">
	<tr>
	</tr>
	<tr>
	<td>
		<table class="menuMatricula">
			<tr>
				<c:if test="${matriculaGraduacao.solicitacaoMatricula and matriculaGraduacao.discente.graduacao and pagina != 'instrucoes'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/instrucoes.png" /><br />
						<c:if test="${pagina == 'instrucoes'}">
							<span class="destaque">Instruções para Matrícula On-line</span>
						</c:if>
						<c:if test="${pagina != 'instrucoes'}">
							<a href="javascript:void(0);" onclick="PainelAjudaMatricula.show()">Ajuda para Matrícula On-line</a>
						</c:if>
					</td>
				</c:if>

				<c:if test="${matriculaGraduacao.discente.stricto && matriculaGraduacao.solicitacaoMatricula}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/tela_inicial.png" /><br />
						<h:commandLink value="Voltar à tela inicial" action="#{matriculaStrictoBean.iniciar}" immediate="true" id="btaoVoltarTelaInicial"/>
					</td>
				</c:if>
				<c:if test="${matriculaGraduacao.discente.stricto && matriculaGraduacao.solicitacaoMatricula && pagina != 'turmas_programa'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/turmas_curriculo.png" /><br />
						<c:if test="${pagina == 'turmas_programa'}">
							<span class="destaque">Ver as turmas do Programa</span>
						</c:if>
						<c:if test="${pagina != 'turmas_programa'}">
							<h:commandLink value="Ver as turmas do programa" action="#{matriculaStrictoBean.listarSugestoesMatricula}" id="btaoVerTurmasPrograma"/>
						</c:if>
					</td>
				</c:if>
				<c:if test="${!matriculaGraduacao.discenteLogado and !matriculaGraduacao.alunoEspecial}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/busca_alunos.png" /><br />
						<h:commandLink value="Selecionar Outro Discente" action="#{matriculaGraduacao.buscarDiscente}" id="selecionarOutroDiscente"/>
					</td>
				</c:if>

				<c:if test="${!matriculaGraduacao.solicitacaoMatricula and matriculaGraduacao.alunoEspecial}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/busca_alunos.png" /><br/>
						<h:commandLink value="Selecionar Outro Discente" action="#{matriculaGraduacao.buscarDiscenteEspecial}" id="buscaEspecial"/>
					</td>
				</c:if>

				<c:if test="${matriculaGraduacao.compulsoria and pagina != 'escolha_restricoes'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/escolha_restricoes.png" /><br />
						<h:commandLink value="Escolher Restrições" action="#{matriculaGraduacao.escolherRestricoes}" id="escolheRestricoes"/>
					</td>
				</c:if>

				<c:if test="${ matriculaGraduacao.foraPrazo and pagina != 'escolha_status'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/escolha_status.png" /><br />
						<c:if test="${pagina == 'escolha_status'}">
							<span class="destaque">Escolher Status</span>
						</c:if>
						<c:if test="${pagina != 'escolha_status'}">
							<h:commandLink value="Escolher Status" action="#{matriculaGraduacao.escolherStatus}" id="escolheStatus"/>
						</c:if>
					</td>
				</c:if>

				<c:if test="${!matriculaGraduacao.discente.stricto && !matriculaGraduacao.discente.residencia && !matriculaGraduacao.alunoEspecial && !matriculaGraduacao.matriculaTurmasNaoOnline &&
						not empty matriculaGraduacao.discente.curso and !matriculaGraduacao.matriculaFerias and pagina != 'turmas_curriculo'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/turmas_curriculo.png" /><br />
						<c:if test="${pagina == 'turmas_curriculo'}">
							<span class="destaque">Ver as turmas da Estr. Curricular</span>
						</c:if>
						<c:if test="${pagina != 'turmas_curriculo'}">
							<h:commandLink value="Ver as turmas da Estr. Curricular" action="#{matriculaGraduacao.listarSugestoesMatricula}" id="verTurmasEstCurricular"/>
						</c:if>
					</td>
				</c:if>

				<c:if test="${!matriculaGraduacao.discente.tecnico && !matriculaGraduacao.discente.stricto && !matriculaGraduacao.discente.residencia && !matriculaGraduacao.alunoEspecial && !matriculaGraduacao.matriculaTurmasNaoOnline &&
						not empty matriculaGraduacao.discente.curso and !matriculaGraduacao.matriculaFerias and pagina != 'turmas_equivalentes_curriculo'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/turmas_equivalentes_curriculo.png" /><br />
						<c:if test="${pagina == 'turmas_equivalentes_curriculo'}">
							<span class="destaque">Ver equivalentes a Estr. Curricular</span>
						</c:if>
						<c:if test="${pagina != 'turmas_equivalentes_curriculo'}">
							<h:commandLink value="Ver equivalentes a Est. Curricular" action="#{matriculaGraduacao.listarSugestoesMatriculaEquivalentes}" id="verEquivEstCurricualar"/>
						</c:if>
					</td>
				</c:if>

				<c:if test="${matriculaGraduacao.buscarTurmasAbertas and pagina != 'outras_turmas'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/outras_turmas.png" /><br />
						<c:if test="${pagina == 'outras_turmas'}">
							<span class="destaque">Buscar Turmas</span>
						</c:if>
						<c:if test="${pagina != 'outras_turmas'}">
							<h:commandLink value="Buscar Turmas Abertas" action="#{matriculaGraduacao.telaOutrasTurmas}" id="btnverTurmasAbertas"/>
						</c:if>
					</td>
				</c:if>

				<c:if test="${matriculaGraduacao.matriculaFerias}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/outras_turmas.png" /><br />
						<h:commandLink value="Turmas de Férias Abertas" action="#{matriculaGraduacao.telaOutrasTurmas}" id="turmasFeriasAberta"/>
					</td>
				</c:if>

				<c:if test="${matriculaGraduacao.exibirOrientacoes and pagina != 'orientacoes'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/outras_turmas.png" /><br />
						<c:if test="${pagina == 'orientacoes'}">
							<span class="destaque">Ver orientações da coordenação</span>
						</c:if>
						<c:if test="${pagina != 'orientacoes'}">
						<h:commandLink value="Ver orientações da coordenação" action="#{matriculaGraduacao.telaSolicitacoes}" id="verorientacoesCoordenacao"/>
						</c:if>
					</td>
				</c:if>

				<c:if test="${matriculaGraduacao.exibirTurmasSelecionadas and pagina != 'turmas_selecionadas'}">
					<td class="operacao">
						<h:graphicImage url="/img/graduacao/matriculas/turmas_selecionadas.png" /><br />
						<c:if test="${pagina == 'turmas_selecionadas'}">
							<span class="destaque">Ver as turmas selecionadas</span>
						</c:if>
						<c:if test="${pagina != 'turmas_selecionadas'}">
						<h:commandLink value="Ver as turmas selecionadas"  action="#{matriculaGraduacao.telaSelecaoTurmas}"  id="verTurmasSelecionadass"/>
						</c:if>
					</td>
				</c:if>
			</tr>
		</table>
	</td>

	<c:if test="${pagina == 'turmas_selecionadas'}">
		<c:if test="${!matriculaGraduacao.solicitacaoMatricula && !matriculaGraduacao.alunoRecemCadastrado}">
			<td class="botoes">
				<h:commandButton title="Matricular Discente Nessas Turmas" image="/img/graduacao/matriculas/salvar.gif"
					 action="#{matriculaGraduacao.telaResumo}" id="matricularNessasTurmas"/><br />
				<h:commandLink title="Matricular Discente Nessas Turmas" 
					 action="#{matriculaGraduacao.telaResumo}" value="Matricular Discente" id="telaResumo"/>
			</td>
		</c:if>
		<c:if test="${matriculaGraduacao.solicitacaoMatricula || matriculaGraduacao.alunoRecemCadastrado}">
			<td class="botoes confirmacao" id="botoes_confirmacao">
				<h:commandButton title="Confirmar Matrículas" image="/img/graduacao/matriculas/salvar.gif" id="botaoSubmissao"
					 action="#{matriculaGraduacao.submeterSolicitacoes}" alt="Confirmar Matrículas"/><br />
				<h:commandLink title="Confirmar Matrículas" value="Confirmar Matrículas" id="linkSubmissao"
					 action="#{matriculaGraduacao.submeterSolicitacoes}" onclick="bloquearSubmissao();"/> 
			</td>
		</c:if>
		<td class="botoes nao_salvar">
			<h:commandButton id="cancelOp" title="Cancelar" image="/img/graduacao/matriculas/cancelar.gif"
				onclick="#{confirm}" action="#{matriculaGraduacao.cancelarMatricula}" alt="Cancelar"/><br />
			<h:commandLink id="sairSemSalvar" title="Sair sem salvar" value="Sair sem salvar" onclick="#{confirm}"
					 action="#{matriculaGraduacao.cancelarMatricula}" />
		</td>
	</c:if>
	</tr>
</table>
</h:form>
</div>