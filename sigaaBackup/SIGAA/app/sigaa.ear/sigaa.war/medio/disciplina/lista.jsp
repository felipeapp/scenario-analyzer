<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="disciplinaMedioMBean"></a4j:keepAlive>
<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Buscar Disciplina</h2>
	<br/>
	<h:form id="formBusca">
	<table class="formulario" width="70%" border="0">
		<caption>Busca de Disciplinas</caption>
		<tbody>
			<tr>
				<td width="3%">
					<h:selectBooleanCheckbox styleClass="noborder" value="#{disciplinaMedioMBean.filtroCodigo}" id="checkCodigo" /> 
				</td>
				<th style="text-align: left; width: 10%;">
					<label for="checkCodigo" onclick="$('formBusca:checkCodigo').checked=!$('formBusca:checkCodigo').checked">Código:</label>
				</th>
				<td>
					<h:inputText size="10" value="#{disciplinaMedioMBean.codigo}" id="codigoComponente"
					onfocus="$('formBusca:checkCodigo').checked = true;" onkeyup="CAPS(this)"  />
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox styleClass="noborder" value="#{disciplinaMedioMBean.filtroNome}" id="checkNome" /> 
				</td>
				<th style="text-align: left;">
					<label for="checkNome" onclick="$('formBusca:checkNome').checked=!$('formBusca:checkNome').checked">Nome:</label>
				</th>
				<td>
					<h:inputText size="60" value="#{disciplinaMedioMBean.nome }" id="nomeComponente"
					onfocus="$('formBusca:checkNome').checked = true;" />
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{disciplinaMedioMBean.buscar}" value="Buscar" id="busca" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{disciplinaMedioMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>

	<c:if test="${not empty  disciplinaMedioMBean.disciplinas}">
		<br/>
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Disciplina
			<c:if test="${not acesso.pedagogico }">
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Disciplina
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Disciplina
			</c:if>
		</div>
	
		<table class="listagem">
			<caption class="listagem">Disciplinas Encontradas (${fn:length(disciplinaMedioMBean.disciplinas)})</caption>
	
			<thead>
				<tr>
					<td width="10%" style="text-align: center;">Código</td>
					<td>Nome</td>
					<td width="15%" style="text-align: right;">CH</td>
					<td style="text-align: center;" width="5%">Ativo</td>
					<td colspan="3"></td>
				</tr>
			</thead>
	
			<tbody>
				<c:forEach items="#{disciplinaMedioMBean.disciplinas}" var="componente" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: center;">${componente.codigo}</td>
						<td>${componente.detalhes.nome}</td>
						<td style="text-align: right;">${componente.detalhes.chTotal}</td>
						<td style="${ componente.ativo ? '' : 'color:red;'}" align="center"><ufrn:format type="simnao" valor="${componente.ativo}"/></td>
						<td width="5px">
							<h:commandLink action="#{disciplinaMedioMBean.view}" title="Visualizar Disciplina">
								<h:graphicImage value="/img/view.gif"/>
								<f:setPropertyActionListener value="#{componente}" target="#{disciplinaMedioMBean.obj}"/>
							</h:commandLink>							
						</td>
						<c:if test="${not acesso.pedagogico }">
						<td width="5px">
							<h:commandLink action="#{disciplinaMedioMBean.alterar}" title="Alterar Disciplina">
								<h:graphicImage value="/img/alterar.gif"/>
								<f:setPropertyActionListener value="#{componente}" target="#{disciplinaMedioMBean.obj}"/>
							</h:commandLink>						
						</td>
						<td width="5px">
							<h:commandLink action="#{disciplinaMedioMBean.remove}" title="Remover Disciplina"  onclick="#{confirmDelete}">
								<h:graphicImage value="/img/delete.gif"/>
								<f:setPropertyActionListener value="#{componente}" target="#{disciplinaMedioMBean.obj}"/>
							</h:commandLink>						
						</td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
