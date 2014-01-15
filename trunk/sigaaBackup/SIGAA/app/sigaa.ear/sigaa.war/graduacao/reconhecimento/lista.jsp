<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Consulta de Reconhecimentos de Cursos de Graduação</h2>
	<h:form id="busca">
		<table class="formulario" width="90%">
			<caption>Busca por Reconhecimentos</caption>
			<tbody>
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{reconhecimento.chkPortaria}"
							styleClass="noborder" id="checkPortaria" />
					</td>
					<td>
						<label for="busca:checkPortaria">Portaria/Decreto:</label>
					</td>
					<td>
						<h:inputText value="#{reconhecimento.obj.portariaDecreto}" size="80" id="portaria" onfocus="$('busca:checkPortaria').checked = true;" />
					</td>
				</tr>
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{reconhecimento.chkCurso}"
							styleClass="noborder" id="checkCurso" />
					</td>
					<td><label for="busca:checkCurso">Curso:</label></td>
					<td>
						<a4j:region>
							<h:selectOneMenu id="curso" value="#{reconhecimento.obj.matriz.curso.id }" 
								valueChangeListener="#{reconhecimento.carregarMatrizes }" onfocus="$('busca:checkCurso').checked = true;"
								style="max-width: 750px" >
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{cursoGrad.allCombo}" />
								<a4j:support event="onchange" reRender="matriz" />
							</h:selectOneMenu>
							<a4j:status>
								<f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
							</a4j:status>
						</a4j:region>
					</td>
				</tr>
				<tr>
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{reconhecimento.chkMatriz}" styleClass="noborder" id="checkMatriz" />
					</td>
					<td>
						<label for="busca:checkMatriz">Matriz Curricular:</label>
					</td>
					<td>
						<h:selectOneMenu value="#{reconhecimento.obj.matriz.id}" id="matriz"
							onfocus="$('busca:checkMatriz').checked = true;" style="max-width: 750px">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{reconhecimento.possiveisMatrizes}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="buscarButton" value="Buscar" action="#{reconhecimento.buscar}" />
						<h:commandButton id="cancelarButton" value="Cancelar" onclick="#{confirm}" action="#{reconhecimento.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>

	<c:if test="${empty reconhecimento.resultadosBusca}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	</c:if>
	
	<c:if test="${not empty reconhecimento.resultadosBusca}">
		<br>
	
		<c:if test="${sessionScope.acesso.cdp}">
			<center>
				<div class="infoAltRem">
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Reconhecimento
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Reconhecimento<br />
				</div>
			</center>
		</c:if>
	

				<table class=listagem>
					<caption class="listagem">Lista de Reconhecimentos Encontrados (${fn:length(reconhecimento.resultadosBusca) })</caption>
					<thead>
						<tr>
							<th>Matriz Curricular</th>
							<th width="250px">Portaria/Decreto</th>
							<th align="center" width="125px">Data de Validade</th>
							<th colspan="2"></th>
						</tr>
					</thead>
					<c:forEach items="#{reconhecimento.resultadosBusca}" var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>
								<h:outputText value="#{item.matriz.descricao}"/>
							</td>
							<td >
								<h:outputText value="NÃO INFORMADO" rendered="#{empty item.portariaDecreto}"/>
								<h:outputText value="#{item.portariaDecreto}" rendered="#{not empty item.portariaDecreto}"/>
							</td>
							<td align="center">
								<h:outputText value="INDEFINIDO" rendered="#{empty item.validade}"/>
								<h:outputText value="#{item.validade}" rendered="#{not empty item.validade}"/>
							</td>
							<c:if test="${sessionScope.acesso.cdp}">
								<td>
									<h:commandLink title="Alterar Reconhecimento" id="alterarReconhecimento"
										action="#{reconhecimento.atualizar}" >
										<h:graphicImage value="/img/alterar.gif"/>
										<f:param value="#{item.id}" name="id" /> 
									</h:commandLink>	
								</td>
								<td>
									<h:commandLink  title="Remover Reconhecimento" id="removerConhecimento"
										action="#{reconhecimento.preRemover}">
										<h:graphicImage value="/img/delete.gif"/>
										<f:param value="#{item.id}" name="id" />
									</h:commandLink>
								</td>
							</c:if>
						</tr>
					</c:forEach>
				</table>
		
	</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
