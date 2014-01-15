	<table class="formulario" width="100%">
	<caption class="listagem"> ${nomeOperacaoVisualizacao} </caption>

	<tr>
		<th width="20%;" style="font-weight: bold;">Curso:</th>
		<td>
			<h:outputText value="#{metodologiaAvaliacaoEad.obj.curso.nome}" />
		</td>
	</tr>
	<tr>
		<th nowrap="nowrap" style="font-weight: bold;">Método de Avaliação:</th>
		<td>
			<h:outputText value="#{metodologiaAvaliacaoEad.obj.descricaoMetodoAvaliacao}" />
		</td>
	</tr>
	<c:if test="${ metodologiaAvaliacaoEad.obj.permiteTutor }">
	<tr>
		<th nowrap="nowrap" style="font-weight: bold;"> Peso do Tutor: </th>
		<td> <h:outputText value="#{metodologiaAvaliacaoEad.obj.porcentagemTutor}" />%</td>
	</tr>
	</c:if>
	<tr>
		<th nowrap="nowrap" style="font-weight: bold;"> Peso do Professor: </th>
		<td> <h:outputText value="#{metodologiaAvaliacaoEad.obj.porcentagemProfessor}" />%</td> 
	</tr>
	<c:if test="${ metodologiaAvaliacaoEad.mostrarNumeroAulas }">
	<tr>
		<th nowrap="nowrap" style="font-weight: bold;"> Número de Aulas: </th>
		<td> 
			<h:outputText value="#{metodologiaAvaliacaoEad.obj.numeroAulas}" />
		</td>
	</tr>
	</c:if>
		
	<tr>
		<th style="font-weight: bold;"> Ativo: </th>
		<td> 
			<h:outputText value="#{metodologiaAvaliacaoEad.obj.ativa ? 'Sim' : 'Não'}" />
		</td>
	</tr>
	
	<tr>
		<th nowrap="nowrap" style="font-weight: bold;">Ano-Período de início:</th>
		<td><h:outputText value="#{metodologiaAvaliacaoEad.obj.anoInicio}"/>.<h:outputText value="#{metodologiaAvaliacaoEad.obj.periodoInicio}"/></td>
	</tr>
	<tr>
		<th nowrap="nowrap" style="font-weight: bold;">Ano-Período final:</th>
		<td><h:outputText value="#{metodologiaAvaliacaoEad.obj.anoFim}"/>.<h:outputText value="#{metodologiaAvaliacaoEad.obj.periodoFim}" /></td>
	</tr>		
	<c:if test="${ metodologiaAvaliacaoEad.obj.permiteTutor }">
	<tr>
		<td colspan="2">
		<table width="100%" class="subFormulario">
		<caption>Cabeçalho da Ficha de Avaliação</caption>
			<tr>
				<td>
					<c:if test="${ not empty metodologiaAvaliacaoEad.obj.cabecalhoFicha}"> 
						${ metodologiaAvaliacaoEad.obj.cabecalhoFicha }
					</c:if>
					<c:if test="${ empty metodologiaAvaliacaoEad.obj.cabecalhoFicha}"> 
						<i>Não definido.</i>
					</c:if>
					
				</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
			  <caption>Itens da Ficha de Avaliação</caption>
			   <tr><td colspan="2">
				<t:dataTable var="item" value="#{ metodologiaAvaliacaoEad.obj.itens }" width="100%" rowClasses="linhaPar,linhaImpar" rowIndexVar="row">
					<t:column>
						<f:facet name="header"><f:verbatim>&nbsp;</f:verbatim></f:facet>
						<h:outputText value="#{ row + 1 }."/>
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Item</f:verbatim></f:facet>
						<h:outputText value="#{ item.nome }" />
					</t:column>
					<t:column>
						<f:facet name="header"><f:verbatim>Ativo</f:verbatim></f:facet>
						<h:outputText value="#{item.ativo ? 'Ativo' : 'Inativo'}"/>
					</t:column>
					
					<t:column>
						<f:facet name="header">
							<f:verbatim></f:verbatim>
						</f:facet>
					</t:column>
				</t:dataTable>
			   </td></tr>
			</table>
		</td>
	</tr>
	</c:if>
	</table>