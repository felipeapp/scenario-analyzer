<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form>

<h2>Trancamentos da Avaliação Institucional</h2>

<table class="formulario" width="50%">
	<caption>Informe o Ano/Período da Avaliação</caption>
	<tbody>
		<tr>
			<th width="50%">Ano: </th>
			<td>
				<h:inputText value="#{ motivosTrancamentoMBean.ano }" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
		<tr>
			<th>Período: </th>
			<td>
				<h:inputText value="#{ motivosTrancamentoMBean.periodo }" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton action="#{ motivosTrancamentoMBean.motivos }" value="Gerar Arquivo de Trancamentos"/>
				<h:commandButton action="#{ motivosTrancamentoMBean.cancelar }" value="Cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>

</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
