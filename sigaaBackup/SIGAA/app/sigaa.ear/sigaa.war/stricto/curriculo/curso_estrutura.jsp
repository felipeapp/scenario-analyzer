<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2><ufrn:subSistema /> > Consulta de Estruturas Curriculares de Pós-Graduação </h2>	
	<h:form id="busca">
	
		<c:if test="${curriculo.mostrarCoordenadorOpcaoCadastrarEstruturaCurricular}">
		<div class="infoAltRem" style="width: 80%">				
			<h:commandLink action="#{curriculo.preCadastrar}">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" /> Cadastrar Nova Estrutura Curricular					
			</h:commandLink>				
		</div>
		<br />
		</c:if>
	
			
		<table class="formulario" width="80%">
			<caption>Busca por Estruturas Curriculares</caption>
			<tbody>
				<tr>
					<td><h:selectBooleanCheckbox value="#{curriculo.filtroPrograma}" id="checkPrograma" /></td>
					<td><label for="checkPrograma" onclick="$('busca:checkPrograma').checked = !$('busca:checkPrograma').checked;">Programa:</label></td>
					<td>
						<h:selectOneMenu id="programa" value="#{ curriculo.programa.id }" onfocus="$('busca:checkPrograma').checked = true;"
							valueChangeListener="#{curriculo.selecionarPrograma}" onchange="submit()" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{ curso.allProgramasAcesso }" id="itemProgramaAtual"/>
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
	
	<c:if test="${empty curriculo.resultadosBusca}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	</c:if>

	<c:if test="${not empty curriculo.resultadosBusca}">
		<h:form id="resultado">
			<br>
	
			<div class="infoAltRem">				
				<c:if test="${curriculo.mostrarCoordenadorOpcaoAlterarEstruturaCurricular}"><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Dados da Estrutura Curricular</c:if>				
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Detalhar Estrutura Curricular				
				<c:if test="${curriculo.mostrarCoordenadorOpcaoDesativarEstruturaCurricular}"><h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" /> : Inativar a Estrutura Curricular</c:if>
				<c:if test="${curriculo.mostrarCoordenadorOpcaoAtivarEstruturaCurricular}"><h:graphicImage value="/img/check.png" style="overflow: visible; margin-left: 5px;" />: Ativar a Estrutura Curricular</c:if>				
			</div>
			<br />
	
			<table class=listagem>
				<caption class="listagem">Lista de Estruturas Curriculares Encontradas (${curriculo.quantidadeResultados })</caption>
				<thead>
					<tr>
						<td width="5%" nowrap="nowrap" style="text-align: right;">Código</td>
						<td width="10%" style="text-align: center;">Ano-Período</td>
						<td>Programa</td>
						<td>Modalidade</td>
						<td>Curso</td>
						<td>Situação</td>
						<c:if test="${curriculo.mostrarCoordenadorOpcaoAlterarEstruturaCurricular}"><td></td></c:if>
						<td></td>
						<c:if test="${curriculo.mostrarCoordenadorOpcaoAtivarEstruturaCurricular or curriculo.mostrarCoordenadorOpcaoDesativarEstruturaCurricular}"><td></td></c:if>						
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
						<c:if test="${curriculo.mostrarCoordenadorOpcaoAlterarEstruturaCurricular}">
							<td width="3%">									 
								<h:commandLink  action="#{curriculo.atualizar}" id="botaoAlterarEstrutura">										
									<h:graphicImage url="/img/alterar.gif"  title="Alterar Dados da Estrutura Curricular"  />
									<f:param name="id" value="#{item.id}"/>	
								</h:commandLink>
							</td>						
						</c:if>
						<td width="3%">
							<h:commandLink  action="#{curriculo.detalharCurriculo}" id="botaoDetalharEstrutura">										
								<h:graphicImage url="/img/view.gif"  title="Detalhar Estrutura Curricular"  />
								<f:param name="id" value="#{item.id}"/>	
							</h:commandLink>
						</td>												
						<c:if test="${curriculo.mostrarCoordenadorOpcaoAtivarEstruturaCurricular or curriculo.mostrarCoordenadorOpcaoDesativarEstruturaCurricular}">
						<td width="3%">									
							<h:commandLink  action="#{curriculo.preInativarOuAtivar}" id="botaoInativarEstrutura">
								<c:if test="${!item.ativo and curriculo.mostrarCoordenadorOpcaoAtivarEstruturaCurricular }">
									<h:graphicImage url="/img/check.png"  title="Ativar a Estrutura Curricular"  />
								</c:if>	
								<c:if test="${item.ativo and curriculo.mostrarCoordenadorOpcaoDesativarEstruturaCurricular}">
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