<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:outputText value="#{discenteMonitoria.create}" />
	<h:outputText value="#{prvaSelecao.create}" />	
	<h:outputText value="#{projetoMonitoria.create}" />

	<h2><ufrn:subSistema /> > Lista de Projetos com Processo Seletivo Cadastrado</h2>
	<br/>

		<h:form>
			<center>
			  <table>
			  <tr>
				<th> Filtrar por Centro:</th>
				<td>
					<h:selectOneMenu id="centro" value="#{projetoMonitoria.obj.unidade.id}" 
						valueChangeListener="#{discenteMonitoria.changeCentroListaTodos}" onchange="submit()" style="width: 500px">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CENTRO --"  />
						<f:selectItem itemValue="-1" itemLabel="TODOS"  />						
						<f:selectItems value="#{unidade.centrosEspecificasEscolas}"/>
					</h:selectOneMenu>
				</td>
				</tr>
			 </table>
			</center>
		</h:form>

<br/>
<br/>

			<div class="infoAltRem">
			    <h:graphicImage value="/img/monitoria/form_green.png"style="overflow: visible;"/>: Consultar lista de inscritos para prova de seleção
			    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Ver detalhes do projeto			    
			</div>

			<br/>
			
	<h:form>
			<c:set var="lista" value="#{discenteMonitoria.provas}" />
			
			<table class="listagem">
			    <caption>Escolha um projeto para consultar lista de candidatos à vaga de monitor (${ fn:length(lista) })</caption>

			      <thead>
			      	<tr>
			        	<th>Projeto de Ensino</th>
			        	<th>Centro</th>
			        	<th>Inscrições até</th>			        				        	
			        	<th>&nbsp;</th>			        	
			        </tr>
			      </thead>



				<c:if test="${empty lista}">
			        <tbody>
	                    <tr> <td colspan="5" align="center"> <font color="red">Não há projetos de monitoria com provas de seleção cadastradas.</font> </td></tr>
					</tbody>
				</c:if>

				<c:if test="${not empty lista}">

			        <tbody>
				          	<c:forEach items="#{lista}" var="prova" varStatus="status">
					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

					                    <td> ${prova.projetoEnsino.titulo} </td>
					                    
					                    <td> ${prova.projetoEnsino.unidade.sigla} </td>					                    
					                                        
					                    <td width="15%">
					                     	<fmt:formatDate pattern="dd/MM/yyyy" value="${prova.dataLimiteIncricao}" />
					                    </td>

										<td width="6%">
												<h:commandLink  title="Ver Lista de Candidatos" action="#{provaSelecao.visualizarCandidatos}" style="border: 0;">
												      <f:param name="id" value="#{prova.id}"/>
												      <h:graphicImage url="/img/monitoria/form_green.png" />
												</h:commandLink>

												<h:commandLink  title="Ver Detalhes do Projeto" action="#{projetoMonitoria.view}" style="border: 0;">
												      <f:param name="id" value="#{prova.projetoEnsino.id}"/>
												      <h:graphicImage url="/img/view.gif" />
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