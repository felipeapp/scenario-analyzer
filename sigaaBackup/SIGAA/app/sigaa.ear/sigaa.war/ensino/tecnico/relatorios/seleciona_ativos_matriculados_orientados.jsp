<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatoriosTecnico.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; <h:outputText value="#{relatoriosTecnico.tituloRelatorio}"/> </h2>

<h:form id="form">
<h:inputHidden value="#{relatoriosTecnico.nomeRelatorio}"/>
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relat�rio</caption>
	<tr>
		<th class="obrigatorio">Ano-Per�odo: </th>

		<td>
			<h:inputText id="ano" value="#{relatoriosTecnico.ano}" size="4" maxlength="4"/> -
			<h:inputText id="periodo" value="#{relatoriosTecnico.periodo}" size="1" maxlength="1"/>
		</td>

	</tr>
	<tr>
		<th>Curso:</th>

		<td>
			<h:selectOneMenu id="curso" value="#{relatoriosTecnico.idCurso}">
				<f:selectItems value="#{relatoriosTecnico.cursosCombo}"/>
			</h:selectOneMenu>
		</td>

	</tr>
	<tr>
		<th class="obrigatorio"> Formato do Relat�rio: </th>
		<td>
			<h:selectOneRadio value="#{relatoriosTecnico.formato}">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>
<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{relatoriosTecnico.gerarRelatorio}"/> 
			<h:commandButton value="Cancelar" action="#{relatoriosTecnico.cancelar}" id="cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>