<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<link rel="stylesheet" href="${ctx}/css/matricula.css" type="text/css" />
<link rel="stylesheet" href="${ctx}/css/stricto/matricula.css" type="text/css"/>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	
	<h2> <ufrn:subSistema /> > Solicitação de Matrícula em Atividade </h2>

	<%-- Instruções para o discente --%>
	<div class="descricaoOperacao">
		<p> Caro(a) Aluno(a), </p> <br />

		<p>
			Selecione a(s) atividade(s) que deseja solicitar sua matrícula, dentre as listadas abaixo. 
			Vale lembrar que as atividades cumpridas ou com solicitação de matrícula efetuada estarão 
			desabilitadas para nova solicitação de matrícula.
		</p>
		<c:choose>
			<c:when test="${matriculaStrictoBean.proficiencia}"> 
				<p> 
					<b>Atenção:</b> Somente as atividades de proficiência definidas em seu currículo são listadas. 
					Em caso de dúvida, consulte	seu orientador ou a coordenação de seu Programa de Pós-Graduação.
				</p>
			</c:when>
			<c:when test="${matriculaStrictoBean.complementar}"> 
				<p> 
					<b>Atenção:</b> Somente as atividades complementares definidas em seu currículo são listadas. 
					Em caso de dúvida, consulte	seu orientador ou a coordenação de seu Programa de Pós-Graduação.
				</p>
			</c:when>
			<c:otherwise>
				<p>
					<b>Atenção:</b> Caso você já esteja matriculado em uma das atividades é possível solicitar uma <i>renovação de matrícula</i>.
					Neste caso, seu orientador (ou o coordenador do programa) poderá estender a duração da atividade, contando para todos os efeitos
					como uma matrícula no período corrente. 
				</p>
			</c:otherwise>
		</c:choose>
	</div>

	<%-- Legenda --%>
	<div class="infoAltRem" style="width: 85%; margin-bottom: 10px;">
		<h4> Legenda </h4>
		<img src="${ctx}/img/graduacao/matriculas/zoom.png">: Ver detalhes da atividade
		<img src="${ctx}/img/graduacao/matriculas/matricula_negada.png">: Matrícula não permitida
		<img src="${ctx}/img/delete.gif">: Cancelar solicitação
	</div>

	<%-- Tabela de atividades disponíveis --%>
	<h:form>
		<h:dataTable value="#{matriculaStrictoBean.sugestoesDataModel}" var="item" 
			styleClass="listagem" rowClasses="linhaPar, linhaImpar" style="width: 85%;">
			
			<f:facet name="caption">
				<h:outputText value="Sugestão de Atividades"/>
			</f:facet>

			<%-- Painel com dados do componente --%>
			<h:column>
				<a href="javascript:void(0);" onclick="PainelComponente.show(<h:outputText value="#{item.atividade.id}"/>)" title="Ver Detalhes da Atividade">
					<img src="${ctx}/img/graduacao/matriculas/zoom.png" alt="" class="noborder" />
				</a>
			</h:column>
			
			<%-- Seleção da atividade --%>
			<h:column>
				<h:selectBooleanCheckbox value="#{item.selected}" id="checkMatricula" 
					rendered="#{(item.atividade.proficiencia || item.atividade.complementarStricto) && item.podeMatricular}"/>
				
				<t:selectOneRadio id="selecaoMatricula" forceId="true" forceIdIndex="false" 
					value="#{matriculaStrictoBean.obj.atividade.id}" 
					rendered="#{!item.atividade.proficiencia &&  !item.atividade.complementarStricto && item.podeMatricular && !matriculaStrictoBean.algumaSolicitacaoCadastrada}">
					<f:selectItem itemValue="#{item.atividade.id}" itemLabel=""/>
				</t:selectOneRadio>

				<f:verbatim rendered="#{!item.podeMatricular}">
					<ufrn:help img="/img/graduacao/matriculas/matricula_negada.png" width="400">
						<li><h:outputText value="#{item.motivoInvalido}"/></li>
					</ufrn:help>
				</f:verbatim>
			</h:column>
			
			<%-- Descrição da atividade --%>
			<h:column>
				<f:facet name="header"><h:outputText value="Atividade"/></f:facet>
				<h:outputText value="#{item.atividade.descricaoResumida}" />
			</h:column>

			<%-- Cancelamento da solicitação --%>
			<h:column>
				<h:commandLink  title="Excluir Solicitação"
					onclick="if (!confirm('Tem certeza que não deseja mais se matricular nesta atividade?')) return false;" 
					action="#{matriculaStrictoBean.removerSolicitacao}"
					rendered="#{item.jaSelecionado}" id="linkParaExclusaoSolicitacao">
					<h:graphicImage url="/img/delete.gif"/>
				</h:commandLink>
			</h:column>
		</h:dataTable>
	
	<br />
	<%-- Botões de operações --%>
	<table id="menu-matricula">
		<tr>
		<td class="botoes confirmacao">
			<h:commandButton title="Adicionar a(s) atividade(s) selecionada(s) à solicitação de matrícula"
				image="/img/graduacao/matriculas/adicionar.gif"
				action="#{matriculaStrictoBean.confirmarSolicitacao}" id="linkAdicionarAtividadeASolicitacao"/><br />
			<h:commandLink value="Confirmar" action="#{matriculaStrictoBean.confirmarSolicitacao}" id="linkConfirmacaoSolicitacao"/>
		</td>
		<td>
			<table class="menuMatricula">
			<tr>
				<td class="operacao">
					<h:graphicImage url="/img/graduacao/matriculas/tela_inicial.png" /><br />
					<h:commandLink value="Voltar à tela inicial" action="#{matriculaStrictoBean.iniciar}" immediate="true" id="linkVoltarParaTelaInicial"/>
				</td>
			</tr>
			</table>
		</td>
		</tr>
	</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>