<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${acesso.discente}">
	<%@include file="/portais/discente/menu_discente.jsp" %>
</c:if>
<c:if test="${acesso.docente}">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</c:if>
	<h2><ufrn:subSistema/> > Consulta de Estrutura Curricular de Graduação</h2>
	<h:outputText value="#{curriculo.create}" />
	<h:form id="busca">
		<table class="formulario">
			<caption>Buscar Estrutura Curricular</caption>
			<tbody>
				<tr>
					<td width="3%"><h:selectBooleanCheckbox value="#{curriculo.filtroCurso}" id="checkCurso" styleClass="noborder"/></td>
					<td width="15%"> <label for="checkcurso">Curso:</label></td>
					<td><h:selectOneMenu value="#{curriculo.obj.matriz.curso.id}" id="curso" valueChangeListener="#{curriculo.carregarMatrizes}" 
							onchange="$('busca:checkCurso').checked = true; submit();" style="width:95%;">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroMatriz}" id="checkMatriz" styleClass="noborder"/></td>
					<td>Matriz Curricular:</td>
					<td><h:selectOneMenu id="matriz" value="#{curriculo.obj.matriz.id }" style="width:95%;"
						onchange="$('busca:checkMatriz').checked = true;" >
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{curriculo.possiveisMatrizes}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroCodigo}" id="checkCodigo" styleClass="noborder"/></td>
					<td><label for="checkCodigo">Código</label></td>
					<td><h:inputText value="#{curriculo.obj.codigo}" size="7" maxlength="7" id="codigo"
						onchange="$('busca:checkCodigo').checked = true;" onkeyup="CAPS(this)" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{curriculo.buscar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{curriculo.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>

	<c:if test="${not empty curriculo.resultadosBusca}">
		<br>
<!-- 		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/view.gif" style="overflow: visible;" />:
		Visualizar Estrutura Curricular<br />
		</div>
		</center> -->
		<table class=listagem>
			<caption class="listagem">Lista de Estruturas Curriculares Encontradas</caption>
			<thead>
				<tr>
					<td>Código</td>
					<td>Matriz Curricular</td>
					<td>CH. Componentes</td>
					<td>CH. Atividades</td>
					<td>CH. Optativa Min.</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="${curriculo.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>${item.codigo}</td>
					<td>${item.matriz.descricao}</td>
					<td align="right">${item.chNaoAtividadeObrigatoria} </td>
					<td align="right">${item.chAtividadeObrigatoria} </td>
					<td align="right">${item.chOptativasMinima} </td>

					<h:form>
						<td width=25>&nbsp;<input type="hidden" value="${item.id}" name="id" /> <h:commandButton
							image="/img/view.gif" styleClass="noborder" alt="Visualizar" action="#{curriculo.detalharCurriculo}" />
						</td>
						<td>
							<h:commandButton
							image="/img/report.png" styleClass="noborder" alt="Relatório" value="Gerar Relatório"
							action="#{curriculo.gerarRelatorioCurriculo}" />
						</td>

					</h:form>

				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
