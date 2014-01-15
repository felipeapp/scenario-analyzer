<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.SituacaoMatricula"%>
<style>
<!--
table.subFormulario th{
	font-weight: bold;
}
-->
</style>
<f:view>
<%@include file="/stricto/menu_coordenador.jsp" %>
<h:form id="formulario">
	<h:outputText value="#{retificacaoMatricula.create}" />
	<h:outputText value="#{consolidacaoIndividual.create}"/>
	<h2 class="title"> <ufrn:subSistema /> &gt; Retificação de Aproveitamento e Consolidação de Turmas > retificação</h2>

	<h:messages showDetail="true"></h:messages>
	<c:set value="#{retificacaoMatricula.discenteEscolhido}" var="discente" />
	
	<%@ include file="/graduacao/info_discente.jsp"%>
	
	<table class="formulario" width="90%" border="1">
			<caption class="listagem">Retificação de Matrícula</caption>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Dados Consolidados (${retificacaoMatricula.matriculaOriginal.situacaoMatricula.descricao})</caption>

					<tr>
						<th>Disciplina: </th>
						<td colspan="3">${retificacaoMatricula.matriculaOriginal.componente}</td>
					</tr>
					<tr>
						<th>Ano.Período: </th>
						<td colspan="3">${retificacaoMatricula.matriculaOriginal.anoPeriodo}</td>
					</tr>

						<c:if test="${retificacaoMatricula.aptidao}">
							<tr>
								<th width="25%">Aptidão Final: </th>
								<td><ufrn:format type="bool_sn" valor="${retificacaoMatricula.matriculaModificada.apto}" /></td>
							</tr>
						</c:if>
						<c:if test="${retificacaoMatricula.conceito}">
							<tr>
								<th width="25%">Conceito Final: </th>
								<td><h:outputText value="#{retificacaoMatricula.matriculaOriginal.conceitoChar}" id="conceito" /></td>
							</tr>
						</c:if>
						<c:if test="${retificacaoMatricula.nota}">
							<tr>
								<th width="25%">Média Final: </th>
								<td><h:outputText value="#{retificacaoMatricula.matriculaOriginal.mediaFinal}" id="mediafinal"/></td>
							</tr>
						</c:if>
							<tr>
								<th width="25%">Faltas: </th>
								<td><h:outputText value="#{retificacaoMatricula.matriculaOriginal.numeroFaltas}" id="faltas" /></td>
							</tr>

				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Retificação da Consolidação da Turma</caption>
					<tr>
						<c:if test="${retificacaoMatricula.matriculaOriginal.aproveitado}">
							<th width="45%" class="required">Tipo de Aproveitamento: </th>
							<td width="15%">
							<h:selectOneMenu value="#{retificacaoMatricula.matriculaModificada.situacaoMatricula.id}" onchange="mudarTipos()" id="situacao">
								<f:selectItems value="#{retificacaoMatricula.situacoesRetificacaoAproveitamentos}"/>
							</h:selectOneMenu>
							</td>
						
							<th width="20%" class="required">Ano-Período: </th>
							<td width="20%">
							<h:inputText value="#{retificacaoMatricula.matriculaModificada.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this)" id="ano"/>
							-<h:inputText value="#{retificacaoMatricula.matriculaModificada.periodo}" size="1" maxlength="1" onkeyup="formatarInteiro(this)" id="periodo"/>
							</td>
						</c:if>
					</tr>
					
					<tr id="linhaMediaFreq">
						<c:if test="${retificacaoMatricula.aptidao}">
							<th width="25%" class="required">Aptidão Final: </th>
							<td width="15%">
								<h:selectOneRadio id="aptidao" value="#{retificacaoMatricula.matriculaModificada.apto}">
									<f:selectItem itemValue="true" itemLabel="Sim"/>
									<f:selectItem itemValue="false" itemLabel="Não"/>
								</h:selectOneRadio>
							</td>
						</c:if>
						<c:if test="${retificacaoMatricula.conceito}">
							<th width="25%" class="required">Conceito Final: </th>
							<td width="15%">
							<select name="conceitoAtualizado">
								<option value="-1">-</option>
								<c:forEach var="conceito" items="${ consolidacaoIndividual.conceitos }">
									<option value="${ conceito.valor }" ${ (retificacaoMatricula.matriculaOriginal.conceito == conceito.valor) ? 'selected="selected"' : '' }>${ conceito.conceito }</option>
								</c:forEach>
							</select>
							</td>
						</c:if>
						<c:if test="${retificacaoMatricula.nota}">
							<th width="25%" class="required">Média Final: </th>
							<td width="15%">
							<h:inputText value="#{retificacaoMatricula.matriculaModificada.mediaFinal}" size="5" maxlength="5" onkeydown="return(formataValor(this, event, 1))" id="mediaFinal">
							<f:converter converterId="convertNota"/>
							</h:inputText>
							</td>
						</c:if>
						<th width="10%" class="required">Faltas: </th>
						<td>
						<h:inputText value="#{retificacaoMatricula.matriculaModificada.numeroFaltas}" size="5" maxlength="5" onkeyup="formatarInteiro(this)" id="numfaltas"/>
						</td>
					</tr>

				</table>
				</td>
			</tr>
	</table>
	
<%-- 	<c:if test="${not empty retificacaoMatricula.historicoRetificacoes}"> --%>
	<br>
	<table class="formulario" width="90%" border="1">
		<caption>Histórico de Retificações Dessa Matrícula</caption>
		<thead>
			<td style="text-align: center;">
				<c:if test="${retificacaoMatricula.aptidao}">
					Aptidão
				</c:if>
				<c:if test="${retificacaoMatricula.conceito}">
					Conceito
				</c:if>
				<c:if test="${retificacaoMatricula.nota}">
					Média
				</c:if>
				 Anterior
			</td>
			<td style="text-align: right;">Faltas Anteriores</td>
			<td style="text-align: center;">Situação Anterior</td>
			<td>Usuário</td>
			<td style="text-align: center;">Data</td>
		</thead>
		<body>
			<c:forEach items="${retificacaoMatricula.historicoRetificacoes}" var="ret" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<c:if test="${retificacaoMatricula.aptidao}">
						<td><ufrn:format type="bool_sn" valor="${ret.aptoAntigo}" /></td>
					</c:if>
					<c:if test="${retificacaoMatricula.conceito}">
						<td style="text-align: center;">${ret.conceitoAntigoChar}</td>
					</c:if>
					<c:if test="${retificacaoMatricula.nota}">
						<td>${ret.mediaFinalAntiga}</td>
					</c:if>
				</td>
				<td style="text-align: right;">${ret.numeroFaltasAntigo}</td>
				<td style="text-align: center;">${ret.situacaoAntiga.descricao}</td>
				<td>${ret.registroEntrada.usuario.login}</td>
				<td style="text-align: center;"><ufrn:format type="data" valor="${ret.data}" /></td>
			</tr>
			</c:forEach>
		</body>
	</table>
	<%-- </c:if> --%>

	<br>
	<br>
	<center>
	 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{retificacaoMatricula.cancelar}" id="cancelar" /> 
		<h:commandButton value="Próximo Passo >>" action="#{retificacaoMatricula.confirmar}" id="confirmar"/>
		<br><br>
		<h:commandButton value="<< Escolher outro discente" action="#{retificacaoMatricula.iniciar}" id="iniciar"/>
		<h:commandButton value="<< Escolher outra Matrícula" action="#{retificacaoMatricula.voltarSelecaoMatricula}" id="voltar" />
	</center>
	<br>

	<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>

</h:form>
</f:view>
<script type="text/javascript">
mudarTipos();
<!--
function mudarTipos() {
	var sel = $('formulario:situacao');
	var val = sel.options[sel.selectedIndex];
	if (val.value == '<%=SituacaoMatricula.APROVEITADO_DISPENSADO.getId()%>'
		|| val.value == '<%=SituacaoMatricula.EXCLUIDA.getId()%>'
		|| val.value == '<%=SituacaoMatricula.CANCELADO.getId()%>'
		) {
		$('linhaMediaFreq').style.visibility = 'hidden';
	} else {
		$('linhaMediaFreq').style.visibility = 'visible';
	}
}
mudarTipos();
//-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
