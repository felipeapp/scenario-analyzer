<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="relatorioMinistrantes"/>
<h2><ufrn:subSistema /> > Relatório de Docentes Ministrantes de um Componente</h2>

<h:form id="form">
<table align="center" class="formulario" width="80%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th class="required">Período Letivo Inicial: </th>

		<td>
			<h:inputText value="#{ relatorioMinistrantes.anoInicio }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="anoInicio" />
			-
			<h:inputText value="#{ relatorioMinistrantes.periodoInicio }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodoInicio"/>
		</td>

	</tr>
	<tr>
		<th class="required">Período Letivo Final: </th>

		<td>
			<h:inputText value="#{ relatorioMinistrantes.anoFim }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="anoFim" />
			-
			<h:inputText value="#{ relatorioMinistrantes.periodoFim }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodoFim"/>
		</td>

	</tr>
	<tr>
		<th>Componente Curricular: </th>
		<td>
		<h:inputText value="#{relatorioMinistrantes.disciplina.detalhes.nome}" id="nomeComponente" style="width: 440px;"/> 
		<rich:suggestionbox id="suggestion" width="400" height="120" for="nomeComponente" 
			minChars="3" suggestionAction="#{componenteCurricular.autocompleteComponenteCurricular}" var="_componente" fetchValue="#{_componente.nome}">
		
			<h:column>
				<h:outputText value="#{_componente.id} - " />
				<h:outputText value="#{_componente.codigo} - " /> 
				<h:outputText value="#{_componente.nome} - "/>
				(<h:outputText value="#{_componente.detalhes.chTotal} hrs" />)   
			</h:column>

			<f:param name="apenasDepartamento" value="true"/>
			<f:param name="nivelPermitido" value="G"/>
			
			<a4j:support event="onselect">
				<f:param name="apenasDepartamento" value="true"/>
				<f:param name="nivelPermitido" value="G"/>
			
				<f:setPropertyActionListener value="#{ _componente.id }" target="#{ relatorioMinistrantes.disciplina.id }"/>
			</a4j:support>
		</rich:suggestionbox>  
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioMinistrantes.gerarRelatorio}"/> <h:commandButton
						value="Cancelar" action="#{relatorioMinistrantes.cancelar}"  onclick="#{confirm}" id="cancelar" /></td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>