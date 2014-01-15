<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form>

<h2> <ufrn:subSistema/> > Resultado da Avaliação Institucional</h2>

<table class="formulario" style="width: 50%">
	<caption>Informe o Ano/Período da avaliação</caption>
	<tbody>
		<tr>
			<th width="50%" class="required">Ano: </th>
			<td>
				<h:inputText value="#{ resultadoAvaliacaoInstitucionalMBean.ano }" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
		<tr>
			<th class="required">Período: </th>
			<td>
				<h:inputText value="#{ resultadoAvaliacaoInstitucionalMBean.periodo }" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" width="450px">
				<h:commandButton action="#{ resultadoAvaliacaoInstitucionalMBean.comentariosRelativosDocente }" value="Gerar Arquivo de Comentários"/> 
				<h:commandButton action="#{ resultadoAvaliacaoInstitucionalMBean.cancelar }" value="Cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
<br>
<div align="center">
	<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
</div>

</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
