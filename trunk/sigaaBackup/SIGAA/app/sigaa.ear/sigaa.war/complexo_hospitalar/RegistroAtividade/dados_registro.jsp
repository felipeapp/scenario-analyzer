<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.formulario tbody tr th { font-weight: bold; }

	table.formulario tr.matriculaCompulsoria td {
		background: #EEE;
		text-align: center;
		font-style: italic;
	}
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema /> > ${registroAtividade.descricaoOperacao} &gt; Registro da Atividade </h2>

	<c:if test="${ registroAtividade.consolidacao && registroAtividade.migrado }">
		<div class="descricaoOperacao">
			<p><strong>Atenção!!</strong></p>
			<p>
				Esta atividade foi cadastrada originalmente no PontoA e não contém todas as informações necessárias para sua consolidação.
				Por favor, informe os dados solicitados para regularizar o cadastro.
			</p>
		</div>
	</c:if>

	<c:set var="discente" value="#{registroAtividade.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form id="form">
	<table class="formulario" style="width: 90%">
		<caption> Informe os detalhes do registro da atividade </caption>
		<tbody>
		<tr>
			<th> Atividade: </th>
			<td> ${registroAtividade.obj.componente.codigoNome} </td>
		</tr>
		<tr>
			<th> Tipo da Atividade: </th>
			<td> ${registroAtividade.obj.componente.tipoAtividade.descricao} </td>
		</tr>

		<c:if test="${ registroAtividade.matricula || registroAtividade.validacao }">

			<tr>
				<th class="obrigatorio" style="font-weight: normal;"> Ano-Período: </th>
				<td>
					<h:inputText size="4" maxlength="4" id="ano" value="#{registroAtividade.obj.ano}" /> . 
					<h:inputText size="1" maxlength="1" value="#{registroAtividade.obj.periodo}" onkeyup="return(formatarInteiro(this))"/>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" style="font-weight: normal;"> Data de Início: </th>
				<td>
					<h:selectOneMenu value="#{registroAtividade.obj.mes}">
						<f:selectItems value="#{registroAtividade.meses}"/>
					</h:selectOneMenu>&nbsp;/&nbsp;
					<h:selectOneMenu value="#{registroAtividade.obj.anoInicio}"
					onchange="$('form:ano').value = this.value;" >
						<f:selectItems value="#{registroAtividade.anos}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>

		<c:if test="${ registroAtividade.consolidacao }">
			<tr>
				<th> Ano Período: </th>
				<td> ${registroAtividade.obj.ano} . ${registroAtividade.obj.periodo} </td>
			</tr>
			<tr>
				<th> Data de Início: </th>
				<td>
				<ufrn:format type="mes" valor="${registroAtividade.obj.mes - 1}" /> / ${registroAtividade.obj.ano}
				</td>
			</tr>
		</c:if>


		<c:if test="${ registroAtividade.consolidacao || registroAtividade.validacao }">
			<tr>
				<td colspan="2" class="subFormulario" style="text-align: center;"> Resultado </td>
			</tr>

				<tr>
					<th class="obrigatorio" style="font-weight: normal;"> Data Final: </th>
					<td>
						<h:selectOneMenu value="#{registroAtividade.obj.mesFim}">
							<f:selectItems value="#{registroAtividade.meses}"/>
						</h:selectOneMenu>&nbsp;/&nbsp;
						<h:selectOneMenu value="#{registroAtividade.obj.anoFim}">
							<f:selectItems value="#{registroAtividade.anos}"/>
						</h:selectOneMenu>
					</td>
				</tr>

			<c:if test="${ registroAtividade.obj.registroAtividade.atividade.necessitaMediaFinal }">

				<tr>
					<th class="obrigatorio" style="font-weight: normal;"> Nota Final: </th>
					<td>
						<h:inputText value="#{registroAtividade.obj.mediaFinal}" size="5" maxlength="5" id="campoNota" onkeydown="return(formataValor(this, event, 1))">
							<f:converter converterId="convertNota"/>
						</h:inputText>

						<c:if test="${not registroAtividade.informarDocentesEnvolvidos}">
							<span style="font-variant: small-caps;">
								<h:selectBooleanCheckbox value="#{ registroAtividade.dispensa }" id="checkDispensa" onchange="marcarDispensa(this)"/>
								<label for="form:checkDispensa">Registrar como DISPENSADO</label>
							</span>
								<script>
									var marcarDispensa = function(dispensa) {
										if (dispensa.checked) {
											$('form:campoNota').disable();
										} else {
											$('form:campoNota').enable();
										}
									}
									marcarDispensa($('form:checkDispensa'));
								</script>
						</c:if>
					</td>
				</tr>

			</c:if>
		</c:if>

		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Selecionar Outra Atividade" action="#{registroAtividade.telaAtividades}"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{registroAtividade.cancelar}"/>
					<h:commandButton value="Próximo Passo >>" action="#{registroAtividade.verConfirmacao}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br/></center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>