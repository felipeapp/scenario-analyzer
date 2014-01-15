<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.DAE,  SigaaPapeis.ADMINISTRADOR_DAE} ); %>

<f:view>
	<a4j:keepAlive beanName="relatoriosPlanejamento"/>
	<h:outputText value="#{relatoriosPlanejamento.create}" />

<h:form id="form">
<h2> <ufrn:subSistema /> > ${relatoriosPlanejamento.tituloRelatorio} </h2>
<table class="formulario" style="width:70%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th class="obrigatorio"> Unidade: </th>
		<td>
			<h:selectOneMenu id="centro" value="#{relatoriosPlanejamento.unidade.id}">
				<f:selectItem itemValue="-1" itemLabel="UFRN"/>
				<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="obrigatorio"> Ano: </th>
		<td>
			<h:inputText id="ano" value="#{relatoriosPlanejamento.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" converter="#{ intConverter }"/>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatoriosPlanejamento.gerarRelatorioJSP}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatoriosPlanejamento.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
<div class="obrigatorio">Campo de preenchimento obrigatório.</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>