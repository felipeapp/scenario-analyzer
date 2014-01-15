<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<%@include file="/portais/discente/menu_discente.jsp" %>

	<h:outputText value="#{projetoMonitoria.create}"/>	
	<h:outputText value="#{provaSelecao.create}"/>		
	
	<h2><ufrn:subSistema /> > Visualizar Resultados de Seleção</h2>


	<div class="infoAltRem">
	    <h:graphicImage value="/img/monitoria/document_chart.png"style="overflow: visible;"/>: Visualizar Resultado da Seleção
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Dados da Prova Seletiva
	    <br/>
	</div>

	<br/>

	<c:set var="provas" value="#{discenteMonitoria.provas}"/>

	<c:if test="${empty provas}">
	<center><i> Você não participou da seleção de nenhum projeto de monitoria. </i></center>
	</c:if>


	<c:if test="${not empty provas}">
	
	<h:form>
	 <table class="listagem">
	    <caption>Projetos de Monitoria Encontrados (${ fn:length(provas) })</caption>

	      <thead>
	      	<tr>
	        	<th style="text-align: center">Data da Prova</th>	      	
	        	<th>Título da prova</th>
	        	<th>Projeto</th>
	        	<th>Unidade</th>
	        	<th>&nbsp;</th>
	        	<th>&nbsp;</th>
	        </tr>
	 	</thead>
	 	<tbody>
       	<c:forEach items="#{provas}" var="prova" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

                    <td align="center"> <fmt:formatDate value="${prova.dataProva}" pattern="dd/MM/yyyy"/> </td>
                    <td> ${prova.titulo} </td>
                    <td> ${prova.projetoEnsino.anoTitulo} </td>
                    <td> ${prova.projetoEnsino.unidade.sigla} </td>
					<td width="3%">
							<h:commandLink title="Visualizar Resultado da Seleção" action="#{ provaSelecao.visualizarResultados }" immediate="true">							
							      <f:param name="id" value="#{prova.id}"/>
							      <h:graphicImage url="/img/monitoria/document_chart.png" />
							</h:commandLink>
					</td>
					
					<td width="3%">
							<h:commandLink title="Visualizar Dados da Prova" action="#{ provaSelecao.view }" immediate="true">							
							      <f:param name="id" value="#{prova.id}"/>
							      <h:graphicImage url="/img/view.gif" />
							</h:commandLink>
					</td>
              </tr>
          </c:forEach>
	 	</tbody>
	 </table>
	</h:form>
	
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>