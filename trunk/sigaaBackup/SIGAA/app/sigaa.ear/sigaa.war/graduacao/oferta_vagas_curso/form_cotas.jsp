<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
.left {
	text-align: left;
	border-spacing: 3px;
}

.center {
	text-align: center;
	border-spacing: 3px;
}
-->
</style>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de oferta de
	cursos para Vestibular</h2>
	<h:messages showDetail="true"></h:messages>
	<div class="descricaoOperacao" style="width: 95%;">Selecione uma
	Unidade para exibir a tabela de oferta de vagas de cursos. Digite o
	número de vagas correspondente ao cursos, por período de entrada, e
	clique em atualizar.</div>
	<h:form id="formulario">
		<table class="formulario" width="99%">
			<caption>Informe o ano base e o tipo de entrada</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Ano de entrada:</th>
					<td><h:selectOneMenu value="#{cadastroOfertaVagasCurso.ano}"
						onchange="submit()"
						valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}"
						id="ano">
						<f:selectItems value="#{cadastroOfertaVagasCurso.anosCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Forma de Ingresso:</th>
					<td>
						<h:selectOneMenu
							value="#{cadastroOfertaVagasCurso.formaIngresso.id}"
							onchange="submit()"
							valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}"
							id="formaIngresso">
							<f:selectItems value="#{formaIngresso.realizamProcessoSeletivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Unidade Responsável do Curso:</th>
					<td><h:selectOneMenu id="unidade"
						value="#{cadastroOfertaVagasCurso.idUnidade}" onchange="submit()"
						valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Matrizes Curriculares:</th>
					<td><h:selectOneMenu id="matriz"
						value="#{cadastroOfertaVagasCurso.apenasMatrizesAtivas}" onchange="submit()"
						valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}">
						<f:selectItem itemLabel=" TODAS " />
						<f:selectItems value="#{cadastroOfertaVagasCurso.matrizCurricularCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Modalidade de Ensino:</th>
					<td><h:selectOneMenu id="modalidadeEducacao"
						value="#{cadastroOfertaVagasCurso.modalidadeEducacao}" onchange="submit()"
						valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}">
						<f:selectItems value="#{cadastroOfertaVagasCurso.modalidadeEducacaoCombo}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="cancelarFiltro" value="Cancelar" onclick="#{confirm}" action="#{cadastroOfertaVagasCurso.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<c:if test="${ not empty cadastroOfertaVagasCurso.listaOfertaVagasCurso}">
			<table class="formulario" width="99%">
				<caption>Informe a Quantidade de Vagas para Cada Oferta</caption>
				<thead>
					<tr>
						<th rowspan="2" style="text-align: left;">Curso / Matriz Curricular</th>
						<c:if test="${ cadastroOfertaVagasCurso.EAD }">
							<th rowspan="2">Pólo</th>
						</c:if>
						<th rowspan="2" style="text-align: left;" width="7%">Período</th>
						<th rowspan="2" style="text-align: center;" width="7%">Vagas<br/>Ofertadas</th>
						<th colspan="${ fn:length(cadastroOfertaVagasCurso.gruposCotas) }" 
							style="text-align: center;">Grupo de Cotas
						</th>
						<th rowspan="2" style="text-align: center;" width="7%">Vagas<br/>Ociosas</th>
					</tr>
					<tr>
						<c:forEach items="#{ cadastroOfertaVagasCurso.gruposCotas }" var="grupo">
							<th style="text-align: center;" width="7%">
								<h:outputText value="#{ grupo.descricao }" title="#{ grupo }" />
							</th>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<c:set var="_municipio" value="" />
					<c:set var="parImpar" value="0" />
					<c:forEach items="#{ cadastroOfertaVagasCurso.listaOfertaVagasCurso }" var="item" varStatus="status">
						<c:if test="${ item.curso.municipio.id != _municipio }">
							<tr>
								<td colspan="${ fn:length(cadastroOfertaVagasCurso.gruposCotas) + (cadastroOfertaVagasCurso.EAD ? 5 : 4)}" class="subFormulario">
							 		${ item.curso.municipio.nome } 
						 		</td>
					 		</tr>
					 		<c:set var="parImpar" value="0" />
							<c:set var="_municipio" value="${ item.curso.municipio.id }" />
						</c:if>
						<tr class="${parImpar % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td rowspan="2">
								${ item.matrizCurricular.curso.nome }
								<h:outputText value=" - #{ item.matrizCurricular.habilitacao.nome}" rendered="#{ not empty item.matrizCurricular.habilitacao.nome }" />
								<h:outputText value=" - #{ item.matrizCurricular.enfase.nome}" rendered="#{ not empty item.matrizCurricular.enfase.nome }" />
								<h:outputText value=" - #{ item.matrizCurricular.grauAcademico.descricao}" />
								<h:outputText value=" - #{ item.matrizCurricular.turno.sigla}" />
								<br/>
								<i><h:outputText value="Matriz Curricular Inativa" rendered="#{not item.matrizCurricular.ativo}" style="color:red" /></i>
							</td>
							<c:if test="${ cadastroOfertaVagasCurso.EAD }">
								<td rowspan="2">
									${ item.polo.descricao }
								</td>
							</c:if>
							<td style="text-align: left;">
								1º Período
							</td>
							<td style="text-align: right;">
								<h:inputText size="3" value="#{item.vagasPeriodo1}"
									onkeyup="return formatarInteiro(this)" maxlength="3"
									converter="#{intConverter}"  style="text-align:right;"
									required="true"
									title="Vagas Ofertadas - 1º Período">
								</h:inputText>
							</td>
							<!-- o forEach aninhado abaixo garante a mesma ordem do cabeçalho do formulário -->
							<c:forEach items="#{ cadastroOfertaVagasCurso.gruposCotas }" var="grupo">
								<c:forEach items="#{ item.cotas }" var="cota">
									<c:if test="${ grupo.id == cota.grupoCota.id }">
										<td style="text-align: right;">
											<h:inputText size="3" value="#{cota.vagasPeriodo1}"
												onkeyup="return formatarInteiro(this)" maxlength="3"
												converter="#{intConverter}"  style="text-align:right;"
												title="#{ grupo.descricao } - 1º Período">
											</h:inputText>
										</td>
									</c:if>
								</c:forEach>
							</c:forEach>
							<td style="text-align: right;">
								<h:inputText size="3" value="#{item.vagasOciosasPeriodo1}"
									onkeyup="return formatarInteiro(this)" maxlength="3"
									converter="#{intConverter}" style="text-align:right;"
									title="Vagas Ociosas - 1º Período">
								</h:inputText>
							</td>
						</tr>
						<tr class="${parImpar % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td style="text-align: left;">
								2º Período
							</td>
							<td style="text-align: right;">
								<h:inputText size="3" value="#{item.vagasPeriodo2}"
									onkeyup="return formatarInteiro(this)" maxlength="3"
									style="text-align:right;"
									converter="#{intConverter}"
									title="Vagas Ofertadas - 2º Período">
								</h:inputText>
							</td>
							<!-- o forEach aninhado abaixo garante a mesma ordem do cabeçalho do formulário -->
							<c:forEach items="#{ cadastroOfertaVagasCurso.gruposCotas }" var="grupo">
								<c:forEach items="#{ item.cotas }" var="cota">
									<c:if test="${ grupo.id == cota.grupoCota.id }">
										<td style="text-align: right;">
											<h:inputText size="3" value="#{cota.vagasPeriodo2}"
												onkeyup="return formatarInteiro(this)" maxlength="3"
												converter="#{intConverter}"  style="text-align:right;"
												title="#{ grupo.descricao } - 2º Período">
											</h:inputText>
										</td>
									</c:if>
								</c:forEach>
							</c:forEach>
							<td style="text-align: right;">
								<h:inputText size="3" value="#{item.vagasOciosasPeriodo2}"
									onkeyup="return formatarInteiro(this)" maxlength="3"
									converter="#{intConverter}" style="text-align:right;"
									title="Vagas Ociosas - 2º Período">
								</h:inputText>
							</td>
						</tr>
						<c:set var="parImpar" value="${ parImpar + 1 }" />
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="${ fn:length(cadastroOfertaVagasCurso.gruposCotas) + (cadastroOfertaVagasCurso.EAD ? 5 : 4)}">
							<h:commandButton id="cadastrar" value="Atualizar" action="#{cadastroOfertaVagasCurso.cadastrar}" />
							<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{cadastroOfertaVagasCurso.cancelar}" />
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
		<c:if test="${empty cadastroOfertaVagasCurso.listaOfertaVagasCurso}">
			<div style="text-align: center">
				Não há cursos de graduação cadastrados para esta unidade.
			</div>
		</c:if>
		<br/>
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>