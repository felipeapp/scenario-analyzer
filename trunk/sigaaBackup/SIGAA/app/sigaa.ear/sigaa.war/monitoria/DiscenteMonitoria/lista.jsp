<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:outputText value="#{discenteMonitoria.create}" />

	<h2><ufrn:subSistema /> > Lista de Provas de seleção cadastradas</h2>

			<div class="infoAltRem">
			    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Cadastrar Resultado da Seleção dos Monitores
			</div>

<h:form>
			<table class="listagem">
			    <caption>Escolha uma prova para cadastrar o resultado da seleção</caption>

			      <thead>
			      	<tr>
			        	<th>Prova</th>
			        	<th>Projeto</th>
			        	<th><span title="Bolsas Remuneradas">BR</span></th>
			        	<th><span title="Bolsas Não Remuneradas">BNR</span></th>
			        	<th>&nbsp;</th>
			        </tr>
			      </thead>


				<c:set var="lista" value="#{discenteMonitoria.projetos}" />


				<c:if test="${empty lista}">
			        <tbody>
	                    <tr> <td colspan="5" align="center"> <font color="red">Não há Projetos disponíveis</font> </td></tr>
					</tbody>
				</c:if>

				<c:if test="${not empty lista}">

			        <tbody>
				          	<c:forEach items="#{lista}" var="projeto">
					          	<c:forEach items="#{projeto.provasSelecao}" var="prova" varStatus="status">				          	
					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

					                    <td> <fmt:formatDate value="${prova.dataProva}" pattern="dd/MM/yyyy"/> </td>
					                    <td> ${projeto.anoTitulo} </td>
					                    <td> ${prova.vagasRemuneradas} </td>
					                    <td> ${prova.vagasNaoRemuneradas} </td>

										<td>
											<h:commandLink title="Cadastrar Resultado da Seleção" action="#{discenteMonitoria.iniciarCadatroResultadoProva}" 
												style="border: 0;" rendered="#{prova.permitidoAlterar}">
											      <f:param name="idProva" value="#{prova.id}"/>
											      <h:graphicImage url="/img/seta.gif" />
											</h:commandLink>
										</td>
					              </tr>
					           </c:forEach>
					        </c:forEach>
					</tbody>

				</c:if>

		</table>
</h:form>		
		[<b>BR</b> - Bolsas Remuneradas, <b>BNR</b> - Bolsas Não Remuneradas]

<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>