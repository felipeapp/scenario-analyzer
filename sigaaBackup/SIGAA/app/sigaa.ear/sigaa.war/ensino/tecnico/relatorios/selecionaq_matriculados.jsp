<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatoriosTecnico.create}"></h:outputText>
<h2>
	<ufrn:subSistema /> &gt; <h:outputText id="titulo" value="#{relatoriosTecnico.tituloRelatorio}"/>
</h2>

<h:form id="form">
<h:inputHidden value="#{relatoriosTecnico.nomeRelatorio}"/>
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Unidade:</th>
		<td>
			<h:inputHidden id="id_unidade" value="#{relatoriosTecnico.unidade.id}"/>
			<h:outputText id="nome_unidade" value="#{relatoriosTecnico.unidade.nome}" />
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Ano:</th>
		<td>
			<h:inputText id="ano" value="#{relatoriosTecnico.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio"> Formato do Relatório: </th>
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
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatoriosTecnico.gerarRelatorio}"/> 
				<h:commandButton value="Cancelar" action="#{relatoriosTecnico.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>