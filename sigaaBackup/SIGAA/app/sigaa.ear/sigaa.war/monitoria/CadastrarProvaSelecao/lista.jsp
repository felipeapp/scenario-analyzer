<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{provaSelecao.create}"/>


	<h2><ufrn:subSistema /> > Lista de Provas Seletivas do Projeto</h2>


	<div class="infoAltRem">
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;" />: Alterar Prova de Seleção
		    <h:graphicImage value="/img/delete.gif"style="overflow: visible;" />: Apagar Prova de Seleção<br/>	
		    <h:graphicImage value="/img/monitoria/form_green.png"style="overflow: visible;"/>: Consultar lista de inscritos		    
		    <h:graphicImage value="/img/monitoria/document_chart.png"style="overflow: visible;"/>: Visualizar Resultado da Seleção							
		    <h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Cadastrar Resultado da Seleção
	</div>

	<h:form>
	<table class="listagem">
		<caption class="listagem">Lista de Provas Seletivas do Projeto:<br/> "${provaSelecao.projetoEnsino.anoTitulo}"</caption>
		<thead>
			<tr>
				<th>Data Prova</th>
				<th>Inscrições até</th>				
				<th>Bolsas Remuneradas</th>
				<th>Bolsas Não Remuneradas</th>
				<th></th>				
				<th></th>
				<th></th>
				<th></th>				
				<th></th>				
			</tr>
		</thead>

		<c:forEach items="#{provaSelecao.provasSelecao}" var="prova" varStatus="status">
            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
            
	            <td><fmt:formatDate pattern="dd/MM/yyyy" value="${prova.dataProva}"/></td>
   	            <td><fmt:formatDate pattern="dd/MM/yyyy" value="${prova.dataLimiteIncricao}"/></td>
                <td> ${prova.vagasRemuneradas} </td>
                <td> ${prova.vagasNaoRemuneradas} </td>

				<td width="2%">
							
							<h:commandLink  title="Alterar" action="#{provaSelecao.atualizar}" style="border: 0;" rendered="#{prova.permitidoAlterar}">
								      <f:param name="id" value="#{prova.id}"/>
								      <h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
				</td>
				<td  width="2%">			
							<h:commandLink  title="Ver Candidatos" action="#{provaSelecao.visualizarCandidatos}" style="border: 0;">
								      <f:param name="id" value="#{prova.id}"/>
								      <h:graphicImage url="/img/monitoria/form_green.png" />
							</h:commandLink>
				</td>
				<td width="2%">			
							<h:commandLink title="Visualizar Resultado Seleção"  action="#{ provaSelecao.visualizarResultados }">
								      <f:param name="id" value="#{prova.id}"/>
								      <h:graphicImage url="/img/monitoria/document_chart.png" />
							</h:commandLink>
				</td>
				<td width="2%">			
							<c:if test="${empty prova.discentesInscritos}">
								<h:commandLink action="#{provaSelecao.preRemover}" style="border: 0;" onclick="return confirm('Atenção! Deseja realmente remover esta prova da lista?');">
								      <f:param name="id" value="#{prova.id}"/>
								      <h:graphicImage url="/img/delete.gif" />
								</h:commandLink>
							</c:if>
				</td>
				
				<td>
							<h:commandLink title="Cadastrar Resultado da Seleção" action="#{discenteMonitoria.iniciarCadatroResultadoProva}" style="border: 0;">
							      <f:param name="idProva" value="#{prova.id}"/>
							      <h:graphicImage url="/img/seta.gif" />
							</h:commandLink>
				</td>
				
				
			</tr>
		</c:forEach>

		<c:if test="${empty provaSelecao.provasSelecao}">
			<tr>
				<td colspan="10"><center><font color="red">Não há Provas de Seleção cadastradas<br/></font> </center></td>
			</tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="10" align="center">
					<h:commandLink value="Cadastrar Nova Prova..." action="#{provaSelecao.novaProvaSeletiva}">
					      <f:param name="idProjeto" value="#{provaSelecao.projetoEnsino.id}"/>
					</h:commandLink>
				</td>
			</tr>
		</tfoot>

	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>