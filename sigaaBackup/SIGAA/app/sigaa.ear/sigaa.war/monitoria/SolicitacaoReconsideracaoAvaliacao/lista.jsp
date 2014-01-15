<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:outputText value="#{reconsideracaoAvaliacao.create}" />

	<h2 class="tituloPagina">
		<h:outputLink value=""> <ufrn:subSistema /> > <c:out value="Solicita��o de Reconsidera��o da Avalia��o do Projetos de Ensino"/> </h:outputLink>
	</h2>

	<br/>

			<div class="infoAltRem">
			    <h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Solicitar Reconsidera��o
			</div>
			<br>
			
			<h:form>

			<table class="listagem">
			    <caption>Solicita��o de Reconsidera��o de Projetos de Ensino</caption>

			      <thead>
			      	<tr>
			        	<th>Projeto</th>
			        	<th>Situa��o</th>
			        	<th>&nbsp;</th>
			        </tr>


				<c:set var="lista" value="#{reconsideracaoAvaliacao.projetosReconsideraveisUsuarioLogado}" />


				<c:if test="${empty lista}">
			        <tbody>
	                    <tr> <td colspan="5" align="center"> <font color="red">N�o h� projetos passivos de reconsidera��o <br/> ou o usu�rio atual n�o � coordenador de projetos ativos</font> </td></tr>
					</tbody>
				</c:if>

				<c:if test="${not empty lista}">

			        <tbody>
				        <c:forEach items="#{lista}" var="projeto" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

						                    <td> ${projeto.titulo} </td>
						                    <td> ${projeto.situacaoProjeto.descricao} </td>

											<td width="20">
													
													<h:commandLink   title="Solicitar Reconsidera��o" action="#{reconsideracaoAvaliacao.solicitarReconsideracaoExtensao}" style="border: 0;" >
													   	<f:param name="id" value="#{projeto.id}"/>				    	
														<h:graphicImage url="/img/seta.gif"  />
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