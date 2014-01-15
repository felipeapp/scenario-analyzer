<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2><ufrn:subSistema /> > Consulta de Estruturas Curriculares de Residência Médica/Multi-profissional </h2>
	<h:outputText value="#{curriculo.create}" />
	<c:if test="${curriculo.exibirFiltrosBusca}">
	<h:form id="busca">
		<h:outputText value="#{curriculo.carregarCursosResidenciaCombo}"></h:outputText>
		<table class="formulario" width="50%">
			<caption>Busca por Estruturas Curriculares</caption>
			<tbody>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroPrograma}" id="checkPrograma" /></td>
					<td><label for="checkPrograma" onclick="$('busca:checkPrograma').checked = !$('busca:checkPrograma').checked;">Programa:</label></td>
					<td>
					
						<c:if test="${ !acesso.gestorResidenciaMedica and !curriculo.portalComplexoHospitalar }">
							<h:selectOneMenu id="programa" value="#{ curriculo.programa.id }" onfocus="$('busca:checkPrograma').checked = true;"
								valueChangeListener="#{curriculo.selecionarPrograma}" onchange="submit()" >
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
								<f:selectItems value="#{ unidade.allProgramaPosCombo }" id="itensProgramas"/>
							</h:selectOneMenu>
						</c:if>
						
						<c:if test="${ curriculo.portalComplexoHospitalar }">
							<h:selectOneMenu id="programa" value="#{ curriculo.programa.id }" onfocus="$('busca:checkPrograma').checked = true;"
								valueChangeListener="#{curriculo.selecionarPrograma}" onchange="submit()" >
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
								<f:selectItems value="#{ unidade.allProgramaResidenciaCombo }" id="itensUnidades"/>
							</h:selectOneMenu>
						</c:if>
						
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroCurso}" id="checkCurso" /></td>
					<td><label for="checkcurso" onclick="$('busca:checkCurso').checked = !$('busca:checkCurso').checked;">Curso:</label></td>
					<td><h:selectOneMenu value="#{curriculo.obj.curso.id}" id="curso" onfocus="$('busca:checkCurso').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{curriculo.cursosCombo}" id="itensCursos"/>
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{curriculo.buscar}" id="botaoBuscar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{curriculo.cancelar}" id="botaoCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	</c:if>

	<c:if test="${not empty curriculo.resultadosBusca}">
		<br>

		<div class="infoAltRem">
			<c:if test="${acesso.ppg or acesso.secretariaPosGraduacao || (curriculo.portalComplexoHospitalar) }">
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar dados da Estrutura Curricular
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Detalhar Estrutura Curricular
			</c:if>
			<c:if test="${acesso.ppg || (curriculo.portalComplexoHospitalar) }">
				<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" /> : Inativar Currículo
				<h:graphicImage value="/img/check.png" style="overflow: visible; margin-left: 5px;" />: Ativar Currículo	
			</c:if>
		</div>
		<br />

		<table class=listagem>
			<caption class="listagem">Lista de Estruturas Curriculares Encontradas</caption>
			<thead>
				<tr>
					<td width="7%">Cód.</td>
					<td width="10%" style="text-align: center;">Ano-Período</td>
					<td>Programa</td>
					<td>Curso</td>
					<td>Situação</td>
					<c:if test="${acesso.algumUsuarioStricto || (curriculo.portalComplexoHospitalar) }">
						<td></td>
						<td></td>
					</c:if>
					<c:if test="${acesso.ppg || (curriculo.portalComplexoHospitalar) }">
						<td></td>
					</c:if>
				</tr>
			</thead>
			<c:forEach items="${curriculo.resultadosBusca}" var="item" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${item.codigo}</td>
					<td style="text-align: center;">${item.anoPeriodo}</td>
					<td>${item.curso.unidade.nome}</td>
					<td>${item.curso.descricao}</td>
					<td>
						<c:if test="${item.ativo }">ATIVO</c:if>
						<c:if test="${!item.ativo }">INATIVO</c:if>
					</td>

					<c:if test="${ curriculo.portalComplexoHospitalar }">
						<td width="3%">
							 <h:form>
								<input type="hidden" value="${item.id}" name="id" /> 
								<h:commandButton image="/img/alterar.gif" styleClass="noborder" value="Alterar"	action="#{curriculo.atualizar}" title="Alterar dados da Estrutura Curricular" />
							</h:form>
						</td>
						<td width="3%">
							 <h:form>
								<input type="hidden" value="${item.id}" name="id" />
								<h:commandButton image="/img/view.gif" styleClass="noborder" value="Detalhar" action="#{curriculo.detalharCurriculo}" title="Detalhar Estrutura Curricular" />
							</h:form>
						</td>
						<td width="3%">
							 <h:form>
								<input type="hidden" value="${item.id}" name="id" /> 
								<h:commandLink styleClass="noborder" action="#{curriculo.preInativarOuAtivar}">
									<c:if test="${!item.ativo }">
										<h:graphicImage url="/img/check.png"  title="Ativar a Estrutura Curricular"  />
									</c:if>	
									<c:if test="${item.ativo }">
										<h:graphicImage url="/img/check_cinza.png"  title="Inativar a Estrutura Curricular"  />
									</c:if>	
								</h:commandLink>										
							</h:form>
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
