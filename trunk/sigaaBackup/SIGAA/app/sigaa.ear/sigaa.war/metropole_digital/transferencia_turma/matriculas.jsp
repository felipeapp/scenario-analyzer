<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema></ufrn:subSistema>
	&gt; Transferência Entre Turmas
</h2>
<br />

<f:view>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Dados da Transfência</caption>

			<tbody>
				<tr>
					<th width="30%" class="rotulo">Curso:</th>
					<td><h:outputText
							value="#{ transferenciaTurmaIMD.curso.nome }" /></td>
				</tr>

				<tr>
					<th class="rotulo">Turma de Origem:</th>
					<td><h:outputText
							value="#{ transferenciaTurmaIMD.turmaEntradaOrigem.descricaoResumida }" />
					</td>
				</tr>

				<tr>
					<th class="rotulo">Turma de Destino: </b></th>
					<td><h:outputText
							value="#{ transferenciaTurmaIMD.turmaEntradaDestino.descricaoResumida }" />
					</td>
				</tr>
				<tr style="width: 100%;">
					<td colspan="2">
						<table class="subFormulario" width="100%">
							<caption style="text-align: center;">Disciplinas
								Encontradas</caption>

							<thead>
								<tr>
									<th colspan="2">Discente(s) / Disciplina(s) Matriculada(s)</th>
								</tr>
							</thead>

							<c:forEach var="linha" items="#{ transferenciaTurmaIMD.turmas }">
								<tr>
									<td colspan="2" class="subFormulario">${ linha.key }</td>
								</tr>

								<c:forEach var="turma" items="#{ linha.value }">
									<tr>
										<td>${ turma.turma.nomeResumido }</td>
									</tr>
								</c:forEach>
							</c:forEach>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Efetuar Transferência"
							action="#{ transferenciaTurmaIMD.efeturarTransferencia }" /> <h:commandButton
							value="<< Voltar"
							action="#{ transferenciaTurmaIMD.telaTurmaDestino }" /> <h:commandButton
							value="Cancelar" immediate="true"
							action="#{ transferenciaTurmaIMD.cancelar }" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento
			obrigatório. </span>
	</center>
	<br />

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>