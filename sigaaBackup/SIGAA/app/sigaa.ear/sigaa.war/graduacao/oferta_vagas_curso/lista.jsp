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
.right {
	text-align: right;
	border-spacing: 3px;
}
-->
</style>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Lista de Oferta de Cursos</h2>
	<h:messages showDetail="true"></h:messages>
	<div class="descricaoOperacao" style="width: 95%;">
	<p>Caro Usuário,</p>
	<p>Informe os parâmetros abaixo
	para exibir a tabela de oferta de vagas de cursos. Serão listado apenas os cursos com vagas ofertadas.</p>
	</div>
	<h:form id="formulario">
		<table class="formulario" width="100%">
			<caption>Informe os Parâmetros da Busca</caption>
			<tbody>
				<tr>
					<th width="30%" class="obrigatorio">Ano de entrada:</th>
					<td><h:selectOneMenu value="#{cadastroOfertaVagasCurso.ano}"
						id="ano">
						<f:selectItems value="#{cadastroOfertaVagasCurso.anosCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Forma de Ingresso:</th>
					<td>
						<h:selectOneMenu
							value="#{cadastroOfertaVagasCurso.formaIngresso.id}"
							id="formaIngresso">
							<f:selectItems value="#{formaIngresso.realizamProcessoSeletivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Unidade Responsável do Curso:</th>
					<td><h:selectOneMenu id="unidade"
						value="#{cadastroOfertaVagasCurso.idUnidade}" >
						<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
						<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Matrizes Curriculares:</th>
					<td><h:selectOneMenu id="matriz"
						value="#{cadastroOfertaVagasCurso.apenasMatrizesAtivas}" >
						<f:selectItem itemLabel=" TODAS " />
						<f:selectItems value="#{cadastroOfertaVagasCurso.matrizCurricularCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Modalidade de Ensino:</th>
					<td><h:selectOneMenu id="modalidadeEducacao"
						value="#{cadastroOfertaVagasCurso.modalidadeEducacao}" >
						<f:selectItems value="#{cadastroOfertaVagasCurso.modalidadeEducacaoCombo}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center;">
						<h:commandButton value="Buscar" action="#{cadastroOfertaVagasCurso.listarOfertaVagasCursoGraduacao}" id="btnBusca" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{cadastroOfertaVagasCurso.cancelar}" id="btnCancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>
		<br/>
		<c:set var="colSpan" value="6" />
		<c:if test="${ not empty cadastroOfertaVagasCurso.listaOfertaVagasCurso}">
		<table class="listagem" width="100%">
			<caption>Resultado da Busca</caption>
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
								${ item.polo.cidade.nome }
							</td>
						</c:if>
						<td style="text-align: left;">
							1º Período
						</td>
						<td style="text-align: right;">
							<h:outputText value="#{item.vagasPeriodo1}"/>
							<c:set var="totalVagasPeriodo1" value="${ totalVagasPeriodo1 + item.vagasPeriodo1 }" />
						</td>
						<!-- o forEach aninhado abaixo garante a mesma ordem do cabeçalho do formulário -->
						<c:forEach items="#{ cadastroOfertaVagasCurso.gruposCotas }" var="grupo">
							<td style="text-align: right;">
								<c:set var="temCota" value="${ false }" />
								<c:forEach items="#{ item.cotas }" var="cota">
									<c:if test="${ grupo.id == cota.grupoCota.id }">
										<h:outputText value="#{cota.vagasPeriodo1}"/>
										<c:set var="temCota" value="${ true }" />
									</c:if>
								</c:forEach>
								<c:if test="${ !temCota }">0</c:if>
							</td>
						</c:forEach>
						<td style="text-align: right;">
							<h:outputText value="#{item.vagasOciosasPeriodo1}"/>
							<c:set var="totalVagasOciosasPeriodo1" value="${ totalVagasOciosasPeriodo1 + item.vagasOciosasPeriodo1 }" />
						</td>
					</tr>
					<tr class="${parImpar % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: left;">
							2º Período
						</td>
						<td style="text-align: right;">
							<h:outputText value="#{item.vagasPeriodo2}"/>
							<c:set var="totalVagasPeriodo2" value="${ totalVagasPeriodo2 + item.vagasPeriodo2 }" />
						</td>
						<!-- o forEach aninhado abaixo garante a mesma ordem do cabeçalho do formulário -->
						<c:forEach items="#{ cadastroOfertaVagasCurso.gruposCotas }" var="grupo">
							<td style="text-align: right;">
								<c:set var="temCota" value="${ false }" />
								<c:forEach items="#{ item.cotas }" var="cota">
									<c:if test="${ grupo.id == cota.grupoCota.id }">
										<h:outputText value="#{cota.vagasPeriodo2}"/>
										<c:set var="temCota" value="${ true }" />
									</c:if>
								</c:forEach>
								<c:if test="${ !temCota }">0</c:if>
							</td>
						</c:forEach>
						<td style="text-align: right;">
							<h:outputText value="#{item.vagasOciosasPeriodo2}"/>
							<c:set var="totalVagasOciosasPeriodo2" value="${ totalVagasOciosasPeriodo2 + item.vagasOciosasPeriodo2 }" />
						</td>
					</tr>
					<c:set var="parImpar" value="${ parImpar + 1 }" />
				</c:forEach>
				<tr class="caixaCinza">
					<td style="text-align:right;" colspan="${(cadastroOfertaVagasCurso.EAD ? 3 : 2)}">
						<b>Total:</b>
					</td>
					<td class="rotulo" style="text-align:right;">
						<b>${totalVagasPeriodo1 + totalVagasPeriodo2}</b>
					</td>
					<c:forEach items="#{ cadastroOfertaVagasCurso.gruposCotas }" var="grupo">
						<td style="text-align: right;">
							<c:set var="totalCota" value="0" />
							<c:forEach items="#{ cadastroOfertaVagasCurso.listaOfertaVagasCurso }" var="item" varStatus="status">
								<c:forEach items="#{ item.cotas }" var="cota">
									<c:if test="${ grupo.id == cota.grupoCota.id }">
										<c:set var="totalCota" value="${totalCota + cota.vagasPeriodo1 + cota.vagasPeriodo2}"/>
									</c:if>
								</c:forEach>
							</c:forEach>
							${ totalCota }
						</td>
					</c:forEach>
					<td class="rotulo" style="text-align:right;">
						<b>${totalVagasOciosasPeriodo1 + totalVagasOciosasPeriodo2}</b>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td style="text-align: center;" colspan="${ fn:length(cadastroOfertaVagasCurso.gruposCotas) + (cadastroOfertaVagasCurso.EAD ? 5 : 4)}">
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{cadastroOfertaVagasCurso.cancelar}" id="btnCancelar2" />
					</td>
				</tr>
			</tfoot>
		</table>
		</c:if>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>