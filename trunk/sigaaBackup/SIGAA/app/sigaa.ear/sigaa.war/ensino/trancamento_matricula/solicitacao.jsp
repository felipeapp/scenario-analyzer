<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">

	var prefix = "tr_justificativa_";
	var prefix_select = "txtJustificativa";
	var prefix_div = "divEspefique";

	function showTRs(resultado){
		var valor = resultado.checked;
		elementName = prefix + resultado.id.substring(3);
		var elemento = getEl(elementName);
		if (valor) {
			elemento.setDisplayed(true);
		} else {
			elemento.setDisplayed(false);
		}
	}

	function changeSelect(resultado){
		var valor = resultado.value;
		elementName = prefix_select + resultado.id.substring(6);
		elementNameDiv = prefix_div + resultado.id.substring(6);
		var elemento = getEl(elementName);
		var elementoDiv = getEl(elementNameDiv);
		if (valor == 6) { //id da opcao "outros"
			elemento.setDisplayed(true);
			elementoDiv.setDisplayed(true);
		} else {
			elemento.setDisplayed(false);
			elementoDiv.setDisplayed(false);
		}
	}

</script>

<style>
	#cepIndicator {
		padding: 0 25px;
		color: #999;
	}
	.desabilitado {
		color: gray;
		font-style: italic;
	}
</style>

<f:view>
	<f:subview id="menu">
		<ufrn:subSistema teste="tutor">
			<%@include file="/portais/tutor/menu_tutor.jsp" %>
		</ufrn:subSistema>
		<ufrn:subSistema teste="not tutor">
			<%@include file="/portais/discente/menu_discente.jsp" %>
		</ufrn:subSistema>
	</f:subview>

	<h:outputText value="#{trancamentoMatricula.create}"/>
	<h:outputText value="#{motivoTrancamento.create}"/>

	<h2> Solicitação de Trancamento de Matrícula </h2>
	
	<c:if test="${ not trancamentoMatricula.tutorEad }">
		<div class="descricaoOperacao">
			<c:if test="${trancamentoMatricula.discente.graduacao}">
			<%@include file="/ensino/trancamento_matricula/instrucoes/discente_graduacao.jsp" %>
			</c:if>
			<c:if test="${trancamentoMatricula.discente.stricto}">
			<%@include file="/ensino/trancamento_matricula/instrucoes/discente_stricto.jsp" %>
			</c:if>
		</div>
	</c:if>
	
	<c:set var="discente" value="#{trancamentoMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	
	<h:form>
	<table class="listagem">
		<caption>Selecione as matrículas que deseja trancar</caption>
		<thead>
		<tr>
			<th width="2%"> </th>

			<th style="text-align: center"> Ano-Período </th>
			<th colspan="2"> Componente Curricular</th>
			<th style="text-align: right;"> Turma</th>
			<th style="text-align: center"> Status</th>
			<th style="text-align: center"> Período Letivo</th>
			<th style="text-align: center"> Data Limite de Trancamento</th>
		</tr>
		</thead>
		<tbody>
			<c:forEach var="matricula" items="${trancamentoMatricula.matriculas}" varStatus="status">

			<c:if test="${matricula.solicitacaoTrancamento != null}">
				<c:set var="bgcolor" value="background-color:  #FEEAC5"/>
			</c:if>
			<c:if test="${matricula.solicitacaoTrancamento == null}">
				<c:set var="bgcolor" value=""/>
			</c:if>
				
				<c:if test="${not matricula.dentroPrazoLimiteTrancamento && matricula.solicitacaoTrancamento == null}">
					<tr class="desabilitado" title="Não é permitido trancar este componente. Prazo expirado.">
				</c:if>
				<c:if test="${matricula.dentroPrazoLimiteTrancamento or (not matricula.dentroPrazoLimiteTrancamento && matricula.solicitacaoTrancamento != null)}">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="${bgcolor}">
				</c:if>
				
					<td>
						<c:if test="${matricula.dentroPrazoLimiteTrancamento && matricula.matriculado && matricula.solicitacaoTrancamento == null}">
							<input type="checkbox" name="matriculas" value="${matricula.id}" id="mat${matricula.id}" onclick="showTRs(this)" class="noborder">
						</c:if>
					</td>
					<td style="text-align: center"> ${matricula.anoPeriodo } </td>
					<td width="20"> ${matricula.componente.codigo} </td>
					<td> <label for="mat${matricula.id}"> ${matricula.componente.nome}</label>
						<c:if test="${matricula.solicitacaoTrancamento != null}">
						<br>
						<i>
						Trancamento realizado em <fmt:formatDate value="${matricula.solicitacaoTrancamento.dataCadastro}" pattern="dd/MM/yyy 'às' HH:mm"/>
						</i>
						</c:if>
					</td>
					<td style="text-align: right;">${matricula.turma.codigo}</td>
					<td style="text-align: center">${matricula.situacaoMatricula.descricao}</td>
					<td style="text-align: center"><ufrn:format type="data" valor="${matricula.turma.dataInicio}" /> - <ufrn:format type="data" valor="${matricula.turma.dataFim}"/></td>
					<td style="text-align: center"> <fmt:formatDate value="${matricula.dataLimiteTrancamento}" pattern="dd/MM/yyyy"/> </td>
				</tr>
				<tr class="esconder ${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" id="tr_justificativa_${matricula.id}">
					<td colspan="7">
						<table width="100%">
							<tr>
								<td colspan="2" align="center" style="background: #F5F5F5;">
									<i><b>Informe o motivo do seu trancamento para fins de avaliação da instituição:</b></i>
								</td>
							</tr>
							<tr>
								<td>
									<c:forEach var="opcao" items="${motivoTrancamento.allExibir}">
										<c:if test="${opcao.id != 6}">
											<input type="radio" id="motivo${matricula.id}_${opcao.id}" name="motivo${matricula.id}" value="${opcao.id}" onclick="javascript: getEl('observacao${matricula.id}').setDisplayed(false)"/> 
											<label for="motivo${matricula.id}_${opcao.id}"> ${opcao.descricao} </label>
										</c:if>

										<c:if test="${opcao.id == 6}">
											<input type="radio" id="motivo${matricula.id}_${opcao.id}" name="motivo${matricula.id}" value="${opcao.id}" onclick="javascript: getEl('observacao${matricula.id}').setDisplayed(true); getEl('txtMotivo${matricula.id}').focus()"/> 
											<label for="motivo${matricula.id}_${opcao.id}"> ${opcao.descricao} </label>
											<div id="observacao${matricula.id}" style="display: none;"><br>
											Especifique o motivo:
											<input type="text" size="80" name="txtMotivo${matricula.id}" id="txtMotivo${matricula.id}"/>
											</div>
										</c:if>
										 <br>
									</c:forEach>
								</td>
							</tr>
						</table>
					</td>
					<td></td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="8" align="center">
					<h:commandButton value="Solicitar Trancamento >>" action="#{trancamentoMatricula.resumirSolicitacao}"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{trancamentoMatricula.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<c:forEach items="${trancamentoMatricula.disciplinasMarcadas}" var="marcado">
	<script type="text/javascript">
	marcaCheckBox('mat${marcado}');
	</script>
</c:forEach>

<script type="text/javascript">
	var lista = getEl(document).getChildrenByClassName('esconder');
	for (i = 0; i < lista.size(); i++) {
		lista[i].setDisplayed(false);
	}

	var justs = getEl(document).getChildrenByTagName('textarea');
	for (i = 0; i < justs.size(); i++) {
		justs[i].setDisplayed(false);
	}

	var lista = getEl(document).getChildrenByClassName('divEspecifique');
	for (i = 0; i < lista.size(); i++) {
		lista[i].setDisplayed(false);
	}

	var checks = getEl(document).getChildrenByTagName('input');
	for (i = 0; i < checks.size(); i++) {
		if( checks[i].dom.type == 'checkbox' && checks[i].dom.checked ){
			//checks[i].dom.disable();
			showTRs(checks[i].dom);
		}
	}
</script>