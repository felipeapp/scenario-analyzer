<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Transferência Entre Turmas de Entrada </h2>
<br/>

<f:view>

<h:form id="form">
<table class="formulario" width="100%">
<caption>Informe os Dados para a Matrícula</caption>

	<tbody>
		<tr>
			<th width="30%"><b> Curso: </b></th>
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
			<th><b> Turma de Entrada Destino: </b></th>
			<td>
				<h:outputText value="#{ transferenciaTurmaEntradaMBean.turmaEntradaDestino.descricaoResumida }" /> 
			</td>
		</tr>
	</tbody>
  </table>
	
  <table class="subFormulario" width="100%">
  	<caption>Turmas Encontradas</caption>

		<thead>
			<tr>
				<th>Discentes / Disciplina Matriculado</th>
				<th>Disciplina de Destino</th>
			</tr>
		</thead>
		
		<c:forEach var="linha" items="#{ transferenciaTurmaEntradaMBean.turmas }"> 
			<tr>
				<td colspan="2" class="subFormulario"> ${ linha.key } </td>
			</tr>
					
			<c:forEach var="turma" items="#{ linha.value }">
				<tr>
					<td> ${ turma.turma.nomeResumido } </td>
					<td>
						<c:choose>
							<c:when test="${ turma.exibeCombo  }">
								<h:selectOneMenu id="idTurmaDestino" value="#{ turma.turmaDestino.id }">
									<f:selectItem itemValue="0" itemLabel="SELECIONE" />
									<f:selectItems value="#{ turma.turmaCombo }" />
		   						</h:selectOneMenu>
							</c:when>
							<c:otherwise>
								${ turma.turmaDestino.nomeResumido }		
							</c:otherwise>
						</c:choose>
					</td>	
				</tr>
			</c:forEach>
			
		</c:forEach>
	
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Efetuar Transferência" action="#{ transferenciaTurmaEntradaMBean.efeturarTransferencia }"/>
				<h:commandButton value="<< Voltar" action="#{ transferenciaTurmaEntradaMBean.telaTurmaDestino }"/>
				<h:commandButton value="Cancelar" immediate="true" action="#{ transferenciaTurmaEntradaMBean.cancelar }" onclick="#{confirm}"/>
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