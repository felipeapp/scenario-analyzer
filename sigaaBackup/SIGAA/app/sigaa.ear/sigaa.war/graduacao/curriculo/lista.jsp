<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h:messages showDetail="true"></h:messages>

	<h2><ufrn:subSistema /> > Consulta de Estrutura Curricular de Graduação</h2>

	<h:form id="busca">
		<table class="formulario" width="85%">
			<caption>Busca por Estruturas Curriculares</caption>
			<tbody>
				<a4j:region>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroCurso}" id="checkCurso" /></td>
					<td><label for="checkcurso">Curso:</label></td>
					<td><h:selectOneMenu value="#{curriculo.obj.matriz.curso.id}" id="curso" onfocus="$('busca:checkCurso').checked = true;"
							valueChangeListener="#{curriculo.carregarMatrizes}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
						<a4j:support event="onchange" reRender="matriz" />
					</h:selectOneMenu></td>
				</tr>
				</a4j:region>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroMatriz}" id="checkMatriz" /></td>
					<td>Matriz Curricular:</td>
					<td><h:selectOneMenu id="matriz" value="#{curriculo.obj.matriz.id }" onfocus="$('busca:checkMatriz').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{curriculo.possiveisMatrizes}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroCodigo}" id="checkCodigo" /></td>
					<td><label for="checkCodigo">Código:</label></td>
					<td><h:inputText value="#{curriculo.obj.codigo}" size="7" maxlength="7" id="codigo"
						onfocus="$('busca:checkCodigo').checked = true;" onkeyup="CAPS(this)" /></td>
				</tr>
				<c:if test="${acesso.cdp or acesso.coordenadorCursoGrad}">
					<td> <h:selectBooleanCheckbox id="somenteAtivas" value="#{curriculo.somenteAtivas}" /> </td>
					<td colspan="2"><h:outputLabel for=""></h:outputLabel> Buscar somente estruturas curriculares ativas </td>
				</c:if>	
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton value="Buscar" action="#{curriculo.buscar}" id="btnBuscar"/> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{curriculo.cancelar}" id="btnCancelar"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty curriculo.resultadosBusca}">
		<h:form id="resultado">
			<br>
			<center>
			<div class="infoAltRem">
			<h:graphicImage value="/img/report.png" style="overflow: visible;" />:
			Relatório da Estrutura Curricular
			<h:graphicImage value="/img/requisicoes.png" style="overflow: visible;" />: Declaração de Prazo Máximo
			
			<c:if test="${sessionScope.acesso.cdp or sessionScope.acesso.dae  or acesso.ead or acesso.coordenadorCursoGrad}">
			
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />:
			Detalhar<br />
			
			</c:if>
			
			<c:if test="${sessionScope.acesso.cdp}">
			
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
			Alterar Dados da Estrutura Curricular
			<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />:
			Inativar Estrutura Curricular
			<h:graphicImage value="/img/check.png" style="overflow: visible;" />:
			Ativar Estrutura Curricular
			</c:if>
			</div>
			</center>
	
			<table class="listagem">
				<caption class="listagem">Lista de Estruturas Curriculares Encontradas (${fn:length(curriculo.resultadosBusca)})</caption>
				<thead>
					<tr>
						<td>Cód</td>
						<td style="text-align: center">Ano-Período</td>
						<td>Matriz Curricular</td>
												
						<c:if test="${!sessionScope.acesso.cdp and !sessionScope.acesso.dae and !sessionScope.acesso.ead and !acesso.coordenadorCursoGrad}">
							<td width="3%"></td>
						</c:if>
						
						
						<c:if test="${sessionScope.acesso.cdp or sessionScope.acesso.dae or sessionScope.acesso.ead  or acesso.coordenadorCursoGrad}">
						<td></td>
						<td></td>
						</c:if>
						<td></td>
						<c:if test="${sessionScope.acesso.cdp}">
	
						<td></td>
						<td></td>
						</c:if>
					</tr>
				</thead>
				<c:forEach items="#{curriculo.resultadosBusca}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.codigo}</td>
						<td style="text-align: center">${item.anoPeriodo}</td>
						<td>${item.matriz.descricao}</td>
						<c:if test="${acesso.cdp or acesso.dae or acesso.ead or acesso.coordenadorCursoGrad}">
							<td width="3%">
							<h:commandLink id="detalhar" styleClass="noborder" title="Detalhar" action="#{curriculo.detalharCurriculo}">
							 	<h:graphicImage url="/img/view.gif" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
							</td>
						</c:if>					
						<td width="3%">
							<h:commandLink id="relatorio" styleClass="noborder" title="Relatório da Estrutura Curricular"  action="#{curriculo.gerarRelatorioCurriculo}">
								<h:graphicImage url="/img/report.png" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
						</td>
						<td width="3%">
							<h:commandLink id="declaracaoPrazoMaximo" styleClass="noborder" title="Declaração de Prazo Máximo" action="#{declaracaoPrazoMaximoMBean.iniciar}">
								<h:graphicImage value="/img/requisicoes.png"/>
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>							
						</td>
						<c:if test="${sessionScope.acesso.cdp}">
	
							<td width=20>
								<h:commandLink id="alterar" styleClass="noborder"  title="Alterar Dados da Estrutura Curricular" action="#{curriculo.atualizar}">
									<h:graphicImage url="/img/alterar.gif" />
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>
							</td>
							<td width=25>
								<c:choose>
									<c:when test="${item.ativo == true || item.ativo == null}">
										<h:commandLink id="inativar" styleClass="noborder" title="Inativar Estrutura Curricular" action="#{curriculo.preInativarOuAtivar}">
											<h:graphicImage url="/img/check_cinza.png" />
											<f:param name="id" value="#{item.id}" />
										</h:commandLink>
									</c:when>
									<c:otherwise>
										<h:commandLink id="ativar" styleClass="noborder" title="Ativar Estrutura Curricular" action="#{curriculo.preInativarOuAtivar}">
											<h:graphicImage url="/img/check.png" />
											<f:param name="id" value="#{item.id}" />
										</h:commandLink>
									</c:otherwise>
								</c:choose>
							</td>
						</c:if>
					</tr>
				</c:forEach>
			</table>
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>