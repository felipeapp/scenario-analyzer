<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
body { background-color: white; }
table.listagem th {font-weight: bold;}
-->
</style>

<f:view>
<h:outputText value="#{componenteCurricular.create}" />
<h:outputText value="#{componenteCurricular.carregarComponente}" />
		<table class="listagem" style="width:100%">
			<thead>
				<td colspan="2"><h:outputText value="#{componenteCurricular.obj.codigo}" /> -
				<h:outputText value="#{componenteCurricular.obj.detalhes.nome}" />
				</td>
			</thead>
			<c:if test="${componenteCurricular.obj.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva}">
			<tr>
				<th>Cr Teóricos:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.crAula}" /> crs. (${componenteCurricular.obj.detalhes.chAula} hrs.)</td>
			</tr>
			<tr>
				<th>Cr Práticos:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.crLaboratorio}" /> crs. (${componenteCurricular.obj.detalhes.chLaboratorio} hrs.)</td>
			</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.atividade}">
				<tr>
					<th>CH Teórica:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chAula}" /> hrs.</td>
				</tr>
				<tr>
					<th>CH Prática:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chLaboratorio}" /> hrs.</td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.bloco}">
				<tr>
					<th>CH Total:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chAula}" /> hrs.</td>
				</tr>
			</c:if>
			<tr>
				<th>Tipo:</th>
				<td><h:outputText value="#{componenteCurricular.obj.tipoComponente.descricao}" /></td>
			</tr>
			<c:if test="${componenteCurricular.obj.atividade}">
				<tr>
					<th>Tipo de Atividade:</th>
					<td><h:outputText value="#{componenteCurricular.obj.tipoAtividade.descricao}" /></td>
				</tr>
			</c:if>
			<c:if test="${not componenteCurricular.obj.bloco }">
				<tr>
					<th valign="top" colspan="2" style="text-align: center;">Ementa</th>
				</tr>
				<tr>
					<td colspan="2"><h:outputText value="#{componenteCurricular.obj.detalhes.ementa}" /></td>
				</tr>
			</c:if>
			<!-- dados do bloco -->
			<c:if test="${componenteCurricular.obj.bloco }">
				<tr>
					<td colspan="2">
					<table class="subFormulario" width="100%">
						<caption>Sub-unidades do Bloco</caption>
						<c:forEach items="${componenteCurricular.obj.subUnidades}" var="unid">
							<tr>
								<td>${unid.nome}</td>
							</tr>
						</c:forEach>
					</table>
					</td>
				</tr>
			</c:if>
		</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
