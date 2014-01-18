<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2><ufrn:subSistema /> > Consulta de Estruturas Curriculares de P�s-Gradua��o </h2>
	<h:outputText value="#{curriculo.create}" />
	<c:if test="${curriculo.exibirFiltrosBusca}">
	<h:form id="busca">
		<h:outputText value="#{curriculo.carregarCursosStrictoCombo}"></h:outputText>
		<table class="formulario" width="50%">
			<caption>Busca por Estruturas Curriculares</caption>
			<tbody>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroPrograma}" id="checkPrograma" /></td>
					<td><label for="checkPrograma" onclick="$('busca:checkPrograma').checked = !$('busca:checkPrograma').checked;">Programa:</label></td>
					<td>
						<h:selectOneMenu id="programa" value="#{ curriculo.programa.id }" onfocus="$('busca:checkPrograma').checked = true;"
							valueChangeListener="#{curriculo.selecionarPrograma}" onchange="submit()" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{ unidade.allProgramaPosCombo }" id="itensProgramas"/>
						</h:selectOneMenu>
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
	<c:if test="${empty curriculo.resultadosBusca}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os crit�rios de busca informados.</div>
	</c:if>

	<c:if test="${not empty curriculo.resultadosBusca}">
		<h:form id="resultado">
			<br>
	
			<div class="infoAltRem">
				<c:if test="${acesso.ppg or acesso.secretariaPosGraduacao }">
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Dados da Estrutura Curricular
				</c:if>
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Detalhar Estrutura Curricular
				<c:if test="${acesso.ppg }">
					<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" /> : Inativar a Estrutura Curricular
					<h:graphicImage value="/img/check.png" style="overflow: visible; margin-left: 5px;" />: Ativar a Estrutura Curricular	
				</c:if>
			</div>
			<br />
	
			<table class=listagem>
				<caption class="listagem">Lista de Estruturas Curriculares Encontradas (${curriculo.quantidadeResultados })</caption>
				<thead>
					<tr>
						<td width="5%" nowrap="nowrap" style="text-align: right;">C�digo</td>
						<td width="10%" style="text-align: center;">Ano-Per�odo</td>
						<td>Programa</td>
						<td>Modalidade</td>
						<td>Curso</td>
						<td>Situa��o</td>
						<c:if test="${acesso.algumUsuarioStricto }">
							<td></td>
							<td></td>
						</c:if>
						<c:if test="${acesso.ppg }">
							<td></td>
						</c:if>
					</tr>
				</thead>
				<c:forEach items="#{curriculo.resultadosBusca}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: right;">${item.codigo}</td>
						<td style="text-align: center;">${item.anoPeriodo}</td>
						<td>${item.curso.descricao}</td>
						<td>${item.curso.modalidadeEducacao.descricao}</td>
						<td>${item.curso.nivelDescricao}</td>
						<td>
							<c:if test="${item.ativo }">ATIVO</c:if>
							<c:if test="${!item.ativo }">INATIVO</c:if>
						</td>
						<c:if test="${acesso.ppg}">
							<td width="3%">									 
								<h:commandLink styleClass="noborder" action="#{curriculo.atualizar}" id="botaoAlterarEstrutura">										
									<h:graphicImage url="/img/alterar.gif"  title="Alterar Dados da Estrutura Curricular"  />
									<f:param name="id" value="#{item.id}"/>	
								</h:commandLink>
							</td>
						</c:if>
						<c:if test="${acesso.algumUsuarioStricto}">
							<td width="3%">
								<h:commandLink styleClass="noborder" action="#{curriculo.detalharCurriculo}" id="botaoDetalharEstrutura">										
									<h:graphicImage url="/img/view.gif"  title="Detalhar Estrutura Curricular"  />
									<f:param name="id" value="#{item.id}"/>	
								</h:commandLink>
							</td>
						</c:if>
						<c:if test="${acesso.ppg}">
							<td width="3%">									
									<h:commandLink styleClass="noborder" action="#{curriculo.preInativarOuAtivar}" id="botaoInativarEstrutura">
										<c:if test="${!item.ativo }">
											<h:graphicImage url="/img/check.png"  title="Ativar a Estrutura Curricular"  />
										</c:if>	
										<c:if test="${item.ativo }">
											<h:graphicImage url="/img/check_cinza.png"  title="Inativar a Estrutura Curricular"  />
										</c:if>
										<f:param name="id" value="#{item.id}"/>	
									</h:commandLink>										
							</td>
						</c:if>						
					</tr>
				</c:forEach>
			</table>
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>