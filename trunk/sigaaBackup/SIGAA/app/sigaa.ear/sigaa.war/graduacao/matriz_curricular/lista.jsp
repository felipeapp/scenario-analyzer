<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Consulta de Matrizes Curriculares de Cursos de Graduação</h2>
	<h:outputText value="#{matrizCurricular.create}" />
	<h:form id="busca">
		<table class="formulario" width="90%">
			<caption>Busca por Matrizes</caption>
			<tbody>
				<tr>
					<td><input type="radio" id="checkCurso" name="paramBusca" value="curso" class="noborder" ${matrizCurricular.filtroCurso ? "checked='checked'" : ""}></td>
					<td><label for="checkCurso">Curso</label></td>
					<td><h:selectOneMenu value="#{matrizCurricular.obj.curso.id}" id="curso"
						onfocus="marcaCheckBox('checkCurso')">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td><input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder"  ${!matrizCurricular.filtroCurso ? "checked='checked'" : ""}></td>
					<td><label for="checkTodos">Todos</label></td>
				</tr>
				<c:if test="${acesso.cdp}">
					<td> <h:selectBooleanCheckbox id="somenteAtivas" value="#{matrizCurricular.somenteAtivas}" /> </td>
					<td colspan="2"><h:outputLabel for=""></h:outputLabel> Buscar somente matrizes ativas </td>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{matrizCurricular.buscar}" id="btnBusca" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{matrizCurricular.cancelar}" id="btnCancelar" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	
	<c:if test="${not empty matrizCurricular.resultadosBusca}">
		<br>

		<c:if test="${acesso.cdp}">
		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
		Alterar dados da Matriz Curricular<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
		Remover Matriz Curricular<br />
		</div>
		</center>
		</c:if>

		<table class=listagem>
			<caption class="listagem">Matrizes curriculares Encontradas</caption>
			<thead>
				<tr>
					<td>Descrição</td>
					<td>Município</td>
					<c:if test="${acesso.cdp}">
						<td>Situação da Atividade</td>
						<td>Situação</td>
						<td></td>
						<td></td>
					</c:if>
				</tr>
			</thead>
			<c:forEach items="${matrizCurricular.resultadosBusca}" var="item" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${item.descricao}</td>
					<td>${item.curso.municipio}</td>
					<c:if test="${acesso.cdp}">
						<td>
							${ item.situacao.descricao }
						</td>
						<td>
							${ item.ativo ? "Ativa" : "Inativa" }
						</td>
						<td width="20">
							<h:form>
								<input type="hidden" value="${item.id}" name="id" /> 
								<h:commandButton image="/img/alterar.gif" styleClass="noborder" alt="Alterar dados da Matriz Curricular" title="Alterar dados da Matriz Curricular" id="btnAlteracao"
								action="#{matrizCurricular.atualizar}" />
							</h:form>
						</td>
						<td width="25">
							<h:form>
								<input type="hidden" value="${item.id}" name="id" /> 
								<h:commandButton image="/img/delete.gif" styleClass="noborder" alt="Remover" title="Remover Matriz Curricular"
								action="#{matrizCurricular.preRemover}" id="btaoDelete"/>
							</h:form>
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
