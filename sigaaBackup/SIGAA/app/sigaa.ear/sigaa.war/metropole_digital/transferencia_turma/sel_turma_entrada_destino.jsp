<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Transferência Entre Turmas </h2>
<br/>

<f:view>
<div class="descricaoOperacao">
	<p>Escolha a turma para o qual o aluno será transferido e, em seguida, selecione o botão avançar.</p>
</div>

<h:form id="form">
<table class="formulario" width="100%">
<caption>Transferencia entre Turmas</caption>

	<tbody>
		<tr>
			<th><b> Curso: </b></th>
			<td>
				<h:outputText value="#{ transferenciaTurmaIMD.curso.nome }" />
			</td>
		</tr>
	
		<tr>
			<th><b> Turma de Origem: </b></th>
			<td>
				<h:outputText value="#{ transferenciaTurmaIMD.turmaEntradaOrigem.descricaoResumida }" /> 
			</td>
		</tr>

		<tr>
			<th class="obrigatorio">Turma de Destino: </th>
			<td> 
				<h:selectOneMenu value="#{ transferenciaTurmaIMD.turmaEntradaDestino.id }">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					<f:selectItems value="#{ transferenciaTurmaIMD.comboTurmasEntradaDestino }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
	</tbody>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="<< Voltar" action="#{ transferenciaTurmaIMD.voltarTelaTurmaEntrada }"/>
				<h:commandButton value="Cancelar" immediate="true" action="#{ transferenciaTurmaIMD.cancelar }" onclick="#{confirm}"/>
				<h:commandButton value="Avançar >>" action="#{ transferenciaTurmaIMD.submeterTurmaDestino }"/>
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