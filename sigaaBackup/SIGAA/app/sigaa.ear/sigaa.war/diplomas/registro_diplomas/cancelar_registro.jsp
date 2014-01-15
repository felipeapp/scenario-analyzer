<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Exclusão de Registro Diplomas </h2>

<h:form>

<table class="visualizacao" >
	<caption>Dados do Registro de Diploma Coletivo</caption>
	<tr>
		<th width="30%"> Curso: </th>
		<td > ${registroDiplomaColetivo.obj.curso.descricao} </td>
	</tr>
	<tr>
		<th> Ano - Período: </th>
		<td>${ registroDiplomaColetivo.obj.ano}.${ registroDiplomaColetivo.obj.periodo}</td>
	</tr>
	<tr>
		<th> Número do Protocolo: </th>
		<td>${registroDiplomaColetivo.obj.processo}</td>
	</tr>
	<tr>
		<th> Data da Colação: </th>
		<td> <ufrn:format name="registroDiplomaColetivo" property="obj.dataColacao" type="data"/></td>
	</tr>
	<tr>
		<th> Data do Registro: </th>
		<td>${registroDiplomaColetivo.obj.dataRegistro}</td>
	</tr>
	<tr>
		<th> Data de Expedição: </th>
		<td>${registroDiplomaColetivo.obj.dataExpedicao}</td>
	</tr>
</table>
<br />

	<center style="font-style: italic; padding-bottom: 5px">
		${fn:length(registroDiplomaColetivo.registrosDiplomas) } Graduando(s) Encontrado(s)
	</center>
	<t:dataTable value="#{registroDiplomaColetivo.registrosDiplomas}" var="registro" rowClasses="linhaImpar,linhaPar" style="width:100%">
		<t:column>
			<f:facet name="header"><f:verbatim>Excluir</f:verbatim></f:facet>
			<h:selectBooleanCheckbox value="#{registro.discente.matricular}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
			<h:outputText value="#{registro.discente.matriculaNome}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><f:verbatim>Matriz Curricular</f:verbatim></f:facet>
			<h:outputText value="#{registro.discente.matrizCurricular.descricaoMin}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><f:verbatim>Nº do Registro</f:verbatim></f:facet>
			<span >&nbsp;</span> <h:outputText value="#{registro.numeroRegistro}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><f:verbatim>Livro</f:verbatim></f:facet>
			<span >&nbsp;</span><h:outputText value="#{registro.livro}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><f:verbatim>Folha</f:verbatim></f:facet>
			<span >&nbsp;</span><h:outputText value="#{registro.folha}"/>
		</t:column>
	</t:dataTable>
	<br>
	
	<center>
	<h:commandButton value="Excluir Registros de Diplomas Selecionados" action="#{registroDiplomaColetivo.excluir}"/>
	<br><br>
	<h:commandButton value="<< Escolher Outro Curso" action="#{registroDiplomaColetivo.formBuscaCurso}"/>
	<h:commandButton value="Cancelar" action="#{registroDiplomaColetivo.cancelar}" onclick="#{confirm}"/>
	</center>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>