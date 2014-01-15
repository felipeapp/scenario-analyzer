<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Tradução de Componente(s) da Estrutura Curricular</h2>

	<h:form id="busca">
		<table class="formulario" width="85%">
			<caption>Busca por Estruturas Curriculares</caption>
			<tbody>
				<a4j:region>
				<tr>
					<td><h:selectBooleanCheckbox value="#{traducaoCurriculoMBean.filtroCurso}" id="checkCurso" /></td>
					<td><label for="checkcurso">Curso:</label></td>
					<td><h:selectOneMenu value="#{traducaoCurriculoMBean.obj.matriz.curso.id}" id="curso" onfocus="$('busca:checkCurso').checked = true;"
							valueChangeListener="#{traducaoCurriculoMBean.carregarMatrizes}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
						<a4j:support event="onchange" reRender="matriz" />
					</h:selectOneMenu></td>
				</tr>
				</a4j:region>
				<tr>
					<td><h:selectBooleanCheckbox value="#{traducaoCurriculoMBean.filtroMatriz}" id="checkMatriz" /></td>
					<td>Matriz Curricular:</td>
					<td><h:selectOneMenu id="matriz" value="#{traducaoCurriculoMBean.obj.matriz.id }" onfocus="$('busca:checkMatriz').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{traducaoCurriculoMBean.possiveisMatrizes}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{traducaoCurriculoMBean.filtroCodigo}" id="checkCodigo" /></td>
					<td><label for="checkCodigo">Código:</label></td>
					<td><h:inputText value="#{traducaoCurriculoMBean.obj.codigo}" size="7" maxlength="7" id="codigo"
						onfocus="$('busca:checkCodigo').checked = true;" onkeyup="CAPS(this)" /></td>
				</tr>
				<c:if test="${acesso.cdp}">
					<td> <h:selectBooleanCheckbox id="somenteAtivas" value="#{traducaoCurriculoMBean.somenteAtivas}" /> </td>
					<td colspan="2"><h:outputLabel for=""></h:outputLabel> Buscar somente estruturas curriculares ativas </td>
				</c:if>	
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{traducaoCurriculoMBean.buscar}" id="btnBuscar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{traducaoCurriculoMBean.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty traducaoCurriculoMBean.resultadosBusca}">
		<h:form id="resultado">
			<br/>
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar<br />
			</div>
	
			<table class="listagem">
				<caption class="listagem">Lista de Estruturas Curriculares Encontradas (${fn:length(traducaoCurriculoMBean.resultadosBusca)})</caption>
				<thead>
					<tr>
						<td>Código</td>
						<td style="text-align: center">Ano-Período</td>
						<td>Matriz Curricular</td>
						<td>Ativo</td>
						<td width="3%"></td>
					</tr>
				</thead>
				<c:forEach items="#{traducaoCurriculoMBean.resultadosBusca}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.codigo}</td>
						<td style="text-align: center">${item.anoPeriodo}</td>
						<td>${item.matriz.descricao}</td>
						<td><ufrn:format type="simnao" valor="${item.ativo}"/></td>
						<td width="3%">
							<h:commandLink id="selecionar" styleClass="noborder" title="Selecionar" action="#{traducaoCurriculoMBean.selecionar}">
							 	<h:graphicImage url="/img/seta.gif" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>