<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h2 class="tituloPagina">
		<h:outputLink value=""> <ufrn:subSistema /> > <c:out value="Solicita��o de Rean�lise dos Requisitos Formais do Projetos de Ensino"/> </h:outputLink>
	</h2>

	<br/>
			<h:messages/>
			<div class="infoAltRem">
			    <h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Solicitar Rean�lise
			</div>
			<br>
			
		<h:form>
			<table class="listagem">
			    <caption>Solicita��o de Rean�lise dos Requisitos Formais do Projetos de Ensino</caption>

			      <thead>
			      	<tr>
			        	<th>Projeto</th>
			        	<th>Situa��o</th>
			        	<th>&nbsp;</th>
			        </tr>
                  </thead>

			      <tbody>
		
				        <c:forEach items="#{autorizacaoReconsideracao.projetosReconsideraveis}" var="projeto" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

						                    <td> ${projeto.titulo} </td>
						                    <td> ${projeto.situacaoProjeto.descricao} </td>

											<td width="20">
													<h:commandLink  title="Solicitar Rean�lise" action="#{autorizacaoReconsideracao.escolherProjeto}" style="border: 0;" >
													   	<f:param name="id" value="#{projeto.id}"/>				    	
														<h:graphicImage url="/img/seta.gif"  />
													</h:commandLink>
											</td>
					              </tr>

				        </c:forEach>
				        
				        <c:if test="${empty autorizacaoReconsideracao.projetosReconsideraveis}">
		                    <tr> <td colspan="5" align="center"> <font color="red">N�o h� projetos passivos de reconsidera��o <br/> ou o usu�rio atual n�o � coordenador de projetos ativos</font> </td></tr>
						</c:if>
				        
					</tbody>
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>