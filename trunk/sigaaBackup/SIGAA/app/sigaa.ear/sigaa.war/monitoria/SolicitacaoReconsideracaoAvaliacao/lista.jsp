<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:outputText value="#{reconsideracaoAvaliacao.create}" />

	<h2 class="tituloPagina">
		<h:outputLink value=""> <ufrn:subSistema /> > <c:out value="Solicitação de Reconsideração da Avaliação do Projetos de Ensino"/> </h:outputLink>
	</h2>

	<br/>

			<div class="infoAltRem">
			    <h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Solicitar Reconsideração
			</div>
			<br>
			
			<h:form>

			<table class="listagem">
			    <caption>Solicitação de Reconsideração de Projetos de Ensino</caption>

			      <thead>
			      	<tr>
			        	<th>Projeto</th>
			        	<th>Situação</th>
			        	<th>&nbsp;</th>
			        </tr>


				<c:set var="lista" value="#{reconsideracaoAvaliacao.projetosReconsideraveisUsuarioLogado}" />


				<c:if test="${empty lista}">
			        <tbody>
	                    <tr> <td colspan="5" align="center"> <font color="red">Não há projetos passivos de reconsideração <br/> ou o usuário atual não é coordenador de projetos ativos</font> </td></tr>
					</tbody>
				</c:if>

				<c:if test="${not empty lista}">

			        <tbody>
				        <c:forEach items="#{lista}" var="projeto" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

						                    <td> ${projeto.titulo} </td>
						                    <td> ${projeto.situacaoProjeto.descricao} </td>

											<td width="20">
													
													<h:commandLink   title="Solicitar Reconsideração" action="#{reconsideracaoAvaliacao.solicitarReconsideracaoExtensao}" style="border: 0;" >
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