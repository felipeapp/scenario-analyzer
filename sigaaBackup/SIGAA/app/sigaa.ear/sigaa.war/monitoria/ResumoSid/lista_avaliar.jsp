<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>
<f:view>

<h:outputText value="#{resumoSid.create}" />

<h2 class="tituloPagina">
	<h:outputLink value=""> <ufrn:subSistema /> > <c:out value="Lista de Resumos do Semin�rio de Inicia��o � Doc�ncia (SID)"/> </h:outputLink>		
</h2>

<h:messages  showDetail="true" showSummary="true"/>

<div class="infoAltRem">
    <h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar Resumo
    <h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Avaliar Resumo
</div>


<h:form>
<table class="listagem">
	<caption>Avalia��es de Resumos Cadastradas</caption>
			
	<thead>
	<tr>
		<th>Ano - Projeto de Ensino</th>
		<th>Data do Envio</th>			        	
		<th>Data Avalia��o</th>
		<th>Situa��o</th>
		<th colspan="4">&nbsp;</th>
	</tr>
			        
	<c:set var="lista" value="#{resumoSid.resumosParaAvaliar}" />
				
	<c:if test="${empty lista}">
        <tbody>
			<tr> <td colspan="4" ><center> <font color="red">N�o h� Resumos dispon�veis para avalia��o!</font></center> </td></tr>
		</tbody>		
	</c:if>

	<c:if test="${not empty lista}">
		<tbody>
			
				<c:forEach items="#{lista}" var="aval" varStatus="status">				
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${aval.projetoEnsino.anoTitulo} </td>				                    					    
					    <td><fmt:formatDate value='${aval.resumoSid.dataEnvio}' pattern='dd/MM/yyyy HH:mm' /></td>
					    <td><fmt:formatDate value='${aval.dataAvaliacao}' pattern='dd/MM/yyyy' /></td>
						<td>${aval.statusAvaliacao.descricao}</td>
						<td>
								<h:commandLink  title="Visualizar Resumo" action="#{resumoSid.viewSid}" >
								   	<f:param name="id" value="#{aval.resumoSid.id}"/>				    	
									<h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>
																
						<td>
								<h:commandLink  title="Avaliar Resumo" action="#{resumoSid.selecionarAvaliacao}" rendered="#{aval.avaliacaoEmAberto}">
								   	<f:param name="id" value="#{aval.id}"/>				    	
									<h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
						</td>
					</tr>					
				</c:forEach>
		</tbody>
					
	</c:if>

</table>
</h:form>
<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>