<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:outputText value="#{resumoSid.create}" />
	<h:outputText value="#{projetoMonitoria.create}" />
		

	<h2><ufrn:subSistema /> > Enviar Resumos do Seminário de Iniciação à Docência (SID)</h2>

			<table width="100%" class="subFormulario">
				<tr>
				<td width="40"><html:img page="/img/help.png"/> </td>
				<td style="text-align: justify">
					Lista com todos os Projetos de Ensino onde o usuário atual é Coordenador(a).<br/>
					Os resumos do SID só poderão ser enviados durante o período definido pela Pró-Reitoria de Graduação.
				</td>
				</tr>
			</table>
			<br>


			<div class="infoAltRem">
			    <h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Selecionar Projeto para Enviar Resumo SID			    
			</div>


	<h:form>
			<table class="listagem">
			    <caption>Lista de Projetos para enviar Resumos do Seminário de Iniciação à Docência (SID)</caption>

			      <thead>
			      	<tr>
			        	<th>Projeto</th>
			        	<th>&nbsp;</th>
			        </tr>


				<c:set var="lista" value="#{projetoMonitoria.projetosAtivosCoordenadosUsuarioLogado}" />


				<c:if test="${empty lista}">
			        <tbody>
	                    <tr> <td colspan="5" align="center"> <font color="red">Não há projetos com Resumos do SID Pendentes de envio<br/> ou o usuário atual não é Coordenador de projetos ativos</font> </td></tr>
					</tbody>
				</c:if>

				<c:if test="${not empty lista}">

			        <tbody>
				        <c:forEach items="#{lista}" var="projeto" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					                    <td> ${projeto.anoTitulo}</td>
										<td>
													
													<h:commandLink  title="Enviar Resumo" action="#{resumoSid.listarResumosProjeto}" style="border: 0;">
													   	<f:param name="id" value="#{projeto.id}"/>				    	
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