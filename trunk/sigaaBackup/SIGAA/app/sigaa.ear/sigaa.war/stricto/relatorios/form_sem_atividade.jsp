<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2> <ufrn:subSistema /> > Relatório de Docentes Doutores sem Atividades na Pós Graduação </h2>

<h:form id="form">

<div class="descricaoOperacao">Este relatório lista os docentes com
		titulação de Doutor que não tem orientandos acadêmicos e não estão
		cadastrados como docentes de turmas de pós graduação, de acordo com os
		critérios informados.</div>

<table class="formulario" style="width: 55%">
<caption> Informe os Critérios para a Emissão do Relatório </caption>
	<tbody>
	<tr>
		<th class="required">Departamento: </th>
		<td>
			<h:selectOneMenu value="#{ relatorioAtividadesDocente.idUnidade }" id="selecaoDepartamento">
				<f:selectItem itemValue="0" itemLabel="TODOS"/>
				<f:selectItems value="#{ unidade.allDeptosEscolasCombo }"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="required">Ano-Período: </th>
		<td>
			<h:inputText value="#{relatorioAtividadesDocente.ano}" onkeyup="return formatarInteiro(this);" 
				id="ano" size="4" maxlength="4" converter="#{ intConverter }"/>-
			<h:inputText value="#{relatorioAtividadesDocente.periodo}" onkeyup="return formatarInteiro(this);"
				id="periodo" size="1" maxlength="1" converter="#{ intConverter }"/>			
		</td>
	</tr>
</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioAtividadesDocente.gerarRelatorioSemAtividades}" value="Emitir Relatório" id="btnGerarRelatorio"/>
			<h:commandButton action="#{relatorioAtividadesDocente.cancelar}" value="Cancelar" id="btnCancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>