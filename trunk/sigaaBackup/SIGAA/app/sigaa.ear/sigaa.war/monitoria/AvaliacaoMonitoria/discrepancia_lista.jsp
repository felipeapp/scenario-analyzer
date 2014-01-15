<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="title"><ufrn:subSistema /> > Avaliação de Projetos com Discrepância</h2>

<f:view>

<h:outputText value="#{ avalProjetoMonitoria.create }"/>


	<div class="descricaoOperacao">
		<b>Atenção:</b><br />
		Os projetos listados abaixo foram avaliados com notas muito distintas
		entre os avaliadores. Estes projetos devem ser reavaliados.
	</div>

	<br>
    <c:if test="${not empty avalProjetoMonitoria.projetosDiscrepancia }">
		<div class="infoAltRem">
	   	    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Projeto
		    <h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Avaliar Projeto
		</div>
	</c:if>

	<h:form>
	<table class="listagem">
		<caption>Projeto de Ensino com notas discrepantes</caption>
		<thead>
			<tr>
				<td>Projeto</td>
				<td>Edital</td>
				<td style="text-align: right;">Avaliador 1</td>
                <td style="text-align: right;">Avaliador 2</td>			
				<td style="text-align: right;">Média</td>
				<td></td>
				<td></td>				
			</tr>
		</thead>
		<c:forEach items="#{ avalProjetoMonitoria.projetosDiscrepancia }" var="projeto">
			<tr>
				<td>${ projeto.anoTitulo }</td>
				<td>${ projeto.editalMonitoria.descricao }</td>
				<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${projeto.notaPrimeiraAvaliacao}"/>   </td>
                <td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${projeto.notaSegundaAvaliacao}"/>   </td>
				<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${ projeto.mediaAnalise }"/></td>

				<td width="2%">		
						<h:commandLink title="Visualizar Projeto" action="#{ projetoMonitoria.view }">
						       <f:param name="id" value="#{projeto.id}"/>
						       <h:graphicImage url="/img/view.gif" />
						</h:commandLink>
				</td>

				<td width="2%">
						<h:commandLink action="#{avalProjetoMonitoria.escolheProjetoDiscrepancia}" title="Avaliar Projeto">
						       <f:param name="id" value="#{projeto.id}"/>
						       <h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		<c:if test="${empty avalProjetoMonitoria.projetosDiscrepancia }">
			<tr>
				<td colspan="5" align="center"> <font color="red">Não há projetos de monitoria com avaliações discrepantes para serem analisados.</font></td>
			</tr>
		</c:if>
		<tfoot>
				<tr>
					<td colspan="8" align="center">
		 				<h:commandButton value="<< Voltar" action="#{publicarResultado.iniciarBuscarAvaliacoesEdital}" immediate="true" />
		 				<h:commandButton action="#{ avalProjetoMonitoria.cancelar }" value="Cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>