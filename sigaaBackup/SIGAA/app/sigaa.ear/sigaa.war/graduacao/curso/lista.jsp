<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2>
	<ufrn:subSistema /> > Consulta de Cursos
	</h2>
	<h:outputText value="#{cursoGrad.create}" />
	<h:form id="busca">
		<a4j:keepAlive beanName="cursoGrad" />
		<table class="formulario" width="50%">
			<caption>Busca por Cursos</caption>
			<tbody>
				<tr>
					<td><input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder"  ${ cursoGrad.param == 'nome' ? "checked='checked'" : "" }></td>
					<td><label for="checkNome">Nome:</label></td>
					<td><h:inputText value="#{cursoGrad.obj.nome}" size="40" id="param1"
						onfocus="marcaCheckBox('checkNome')" onkeyup="CAPS(this)" /></td>
				</tr>
				<tr>
					<td><input type="radio" id="checkCentro" name="paramBusca" value="centro" class="noborder" ${ cursoGrad.param == 'centro' ? "checked='checked'" : "" }></td>
					<td>
						<label for="checkCentro">
							<c:choose>
								<c:when test="${nivel eq 'G'}">
									Centro:
								</c:when>
								<c:when test="${nivel eq 'S'}">
									Programa:
								</c:when>
								<c:otherwise>
									Unidade:
								</c:otherwise>
							</c:choose>
						</label>
					</td>
					<td>
						<c:choose>
							<c:when test="${nivel eq 'G'}">
								<h:selectOneMenu value="#{cursoGrad.obj.unidade.id}" onclick="marcaCheckBox('checkCentro')" id="centroG">
									<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{unidade.allCentroCombo}" />
								</h:selectOneMenu>
							</c:when>
							<c:when test="${nivel eq 'S'}">
								<h:selectOneMenu value="#{cursoGrad.obj.unidade.id}" onclick="marcaCheckBox('checkCentro')" id="centroS">
									<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{unidade.allProgramaPosCombo}" />
								</h:selectOneMenu>
							</c:when>
							<c:otherwise>
								<h:selectOneMenu value="#{cursoGrad.obj.unidade.id}" onclick="marcaCheckBox('checkCentro')" id="centro">
									<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
									<f:selectItems value="#{unidade.allDeptosEscolasCombo}" />
								</h:selectOneMenu>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>				
				<tr>
					<td><input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder"  ${ cursoGrad.param == 'todos' ? "checked='checked'" : "" }></td>
					<td><label for="checkTodos">Todos</label></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton id="buscar" value="Buscar" action="#{cursoGrad.buscar}" /> <h:commandButton id="cancelar"
						value="Cancelar" onclick="#{confirm}" action="#{cursoGrad.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<c:if test="${not empty cursoGrad.resultadosBusca}">
		<br>
		<c:if test="${sessionScope.acesso.cdp or sessionScope.acesso.ppg}">
			<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Dados do Curso
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Curso
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Curso
				<br />
			</div>
			</center>
		</c:if>
		<c:if test="${not (sessionScope.acesso.cdp or sessionScope.acesso.ppg)}">
			<center>
				<div class="infoAltRem">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Curso
				</div>
			</center>
		</c:if>
			<table class="listagem">
			<caption class="listagem">Lista de Cursos Encontrados (${fn:length(cursoGrad.resultadosBusca)})</caption>
			<thead>
				<tr>
					<td width="8%">Unidade</td>
					<td>Cidade</td>
					<td>Curso</td>
					<td width="10%">Modalidade</td>
					<td width="10%">Convênio</td>
					<ufrn:subSistema teste="stricto">
						<td width="10%">Tipo</td>
					</ufrn:subSistema>
					<td width="10%">Ativo</td>
					<c:if test="${sessionScope.acesso.cdp or sessionScope.acesso.ppg or sessionScope.acesso.moduloDiploma}">
					<td width="2%"></td>
					<td width="2%"></td>
					</c:if>
					<td width="2%"></td>
				</tr>
			</thead>

			<h:form prependId="false">
			<c:forEach items="#{cursoGrad.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
					<td>${item.unidade.sigla}</td>
					<td>${item.municipio.nome}</td>
					<td>${item.nome}</td>
					<td>${item.modalidadeEducacao.descricao}</td>
					<td>${item.convenio.descricao}</td>
					<ufrn:subSistema teste="stricto">
					<td>${item.tipoCursoStricto.descricao }</td>
					</ufrn:subSistema>
					<td><h:outputText value="#{item.ativo}" converter="convertSimNao"/></td>
					<c:if test="${sessionScope.acesso.cdp or sessionScope.acesso.ppg or sessionScope.acesso.moduloDiploma}">
						<td width="2%">
							<h:commandLink action="#{cursoGrad.atualizar}" id="alterar">
									<h:graphicImage url="/img/alterar.gif" alt="Alterar Dados do Curso" title="Alterar Dados do Curso"/>
									<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
						<td width="2%">
							<h:commandLink action="#{cursoGrad.preRemover}" id="remover">
									<h:graphicImage url="/img/delete.gif" alt="Remover Curso" title="Remover Curso"/>
									<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
					</c:if>
					<td width="2%">
						<h:commandLink action="#{cursoGrad.visualizar}" id="visualizar">
								<h:graphicImage url="/img/view.gif" alt="Visualizar Curso" title="Visualizar Curso"/>
								<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</h:form>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
