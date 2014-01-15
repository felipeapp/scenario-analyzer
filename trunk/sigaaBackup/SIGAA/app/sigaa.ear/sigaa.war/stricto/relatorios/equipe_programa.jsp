<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioEquipePrograma.create}" />

<h2> <ufrn:subSistema /> > ${relatorioEquipePrograma.titulo} </h2>

<h:form id="form">
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th>Programa: </th>
		<td>
			<h:selectOneMenu id="programa" value="#{relatorioEquipePrograma.unidade.id}">
				<f:selectItem itemLabel=" Todos " itemValue="-1"/>
				<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<th>Membros: </th>
		<td>
			<h:selectOneMenu id="membros" value="#{relatorioEquipePrograma.tipoMembros}">
				<f:selectItems value="#{relatorioEquipePrograma.tiposMembros}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<th class="required"> Formato do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatorioEquipePrograma.formato}" id="formato">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioEquipePrograma.gerarRelatorio}" value="Emitir Relatório" id="btnGerar"/>
			<h:commandButton action="#{relatorioEquipePrograma.cancelar}" value="Cancelar" id="btnCancelar"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>

	<br>
	<center><h:graphicImage url="/img/required.gif"/> 
	<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>



</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>