<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Lista de inscritos para fiscal</h2>

	<h:form id="formBusca">
		<table class="formulario" width="90%">
			<caption>Lista de Inscritos para Fiscal</caption>
			<tbody>
				<tr>
					<th width="20%" class="obrigatorio"><b>Processo Seletivo:</b></th>
					<td><h:selectOneMenu id="processoSeletivo" immediate="true"
						value="#{relatoriosVestibular.idProcessoSeletivo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th><b>Tipo:</b></th>
					<td><h:selectOneMenu value="#{relatoriosVestibular.perfil}"
						id="tipo" onchange="submit()">
						<f:selectItem itemLabel="Todos" itemValue="0" id="todosPerfis" />
						<f:selectItem itemLabel="Discente" itemValue="1" />
						<f:selectItem itemLabel="Servidor" itemValue="2" />

					</h:selectOneMenu></td>
				</tr>
				<c:if test="${relatoriosVestibular.perfil == 1}">
					<tr>
						<th><b><h:outputText value="Curso:" /></b></th>
						<td><h:selectOneMenu value="#{relatoriosVestibular.idCurso}"
							id="curso" style="width:95%;">
							<f:selectItem id="todosCursos" itemValue="0"
								itemLabel="TODOS CURSOS" />
							<f:selectItems value="#{cursoGrad.allCombo}" />
						</h:selectOneMenu></td>
					</tr>
				</c:if>
				<c:if test="${relatoriosVestibular.perfil == 2}">
					<tr>
						<th><b><h:outputText value="Unidade:" /></b></th>
						<td><h:selectOneMenu
							value="#{relatoriosVestibular.idUnidade}" id="unidade"
							style="width:95%;">
							<f:selectItem itemValue="0" itemLabel="TODAS UNIDADES"
								id="todasUnidades" />
							<f:selectItems value="#{unidade.allCombo}" />
						</h:selectOneMenu></td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton id="gerarRelatorio"
						value="Gerar Relatório"
						action="#{relatoriosVestibular.gerarRelatorioListaInscritos}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}"
						action="#{relatoriosVestibular.cancelar}" id="cancelar" /></td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		</center>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>