<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>

<script type="text/javascript">
	function marcarTodos() {
		var elements = document.getElementsByTagName('input');
		for (i = 0; i < elements.length; i++) {
			if (elements[i].type == 'checkbox') {
				elements[i].checked = true;
			}
		}
	}
</script>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Transferência Entre Turmas de Entrada </h2>
<br/>

<f:view>

<h:form id="form">
<table class="formulario" width="100%">
<caption>Informe os Dados para a Matrícula</caption>

	<tbody>
		<tr>
			<th><b> Curso: </b></th>
			<td>
				<h:outputText value="#{ transferenciaTurmaEntradaMBean.curso.nome }" />
			</td>
		</tr>
	
		<tr>
			<th><b> Turma de Entrada Origem: </b></th>
			<td>
				<h:outputText value="#{ transferenciaTurmaEntradaMBean.turmaEntradaOrigem.descricaoResumida }" /> 
			</td>
		</tr>

		<tr>
			<th class="obrigatorio">Turma de Entrada de Destino: </th>
			<td> 
				<h:selectOneMenu value="#{ transferenciaTurmaEntradaMBean.turmaEntradaDestino.id }">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					<f:selectItems value="#{ transferenciaTurmaEntradaMBean.comboTurmasEntrada }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
	</tbody>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="<< Voltar" action="#{ transferenciaTurmaEntradaMBean.telaTurmaEntrada }"/>
				<h:commandButton value="Cancelar" immediate="true" action="#{ transferenciaTurmaEntradaMBean.cancelar }" onclick="#{confirm}"/>
				<h:commandButton value="Avançar >>" action="#{ transferenciaTurmaEntradaMBean.submeterTurmaDestino }"/>
			</td>
	</tfoot>

  </table>
</h:form>

<br />
	<center>	
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
<br/>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>