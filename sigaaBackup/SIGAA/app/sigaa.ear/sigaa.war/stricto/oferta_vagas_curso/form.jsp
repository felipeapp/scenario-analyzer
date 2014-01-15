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
	cursos</h2>
	<h:messages showDetail="true"></h:messages>
	<div class="descricaoOperacao" style="width: 80%;">Selecione um
	ano para exibir a tabela de oferta de vagas de cursos. Digite o número
	de vagas correspondente ao cursos, por período de entrada, e clique em
	atualizar.</div>
	<h:form id="formulario">
		<table class="formulario" width="80%">
			<caption>Informe o ano base e o período</caption>
			<tbody>
				<tr>
					<th>Programa:</th>
					<td><h:outputText
						value="#{cadastroOfertaVagasCurso.programaStricto}"></h:outputText></td>
				</tr>
				<tr>
					<th>Ano:</th>
					<td><a4j:region>
						<h:selectOneMenu value="#{cadastroOfertaVagasCurso.ano}" id="anoOferta">
							<f:selectItems value="#{cadastroOfertaVagasCurso.anosCombo}" />
							<a4j:support event="onchange" reRender="table"
								action="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoStricto}" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif">Atualizando a tabela</h:graphicImage>
							</f:facet>
						</a4j:status>
					</a4j:region></td>
				</tr>
		</table>
		<div align="center"><h:panelGroup id="table">
			<t:dataTable value="#{cadastroOfertaVagasCurso.listaOfertaVagasCurso}"
				columnClasses="left,left,center,center"
				headerClass="left,left,center,center"
				rowClasses="linhaPar,linhaImpar" var="item" style="width:80%;">
				<h:column>
					<f:facet name="header">
						<h:outputText value="Municipio" />
					</f:facet>
					<h:outputText value="#{item.curso.municipio.nome}"></h:outputText>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="Curso" />
					</f:facet>
					<h:outputText value="#{item.curso.descricao}"></h:outputText>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="1º Período" />
					</f:facet>
					<h:inputText size="3" value="#{item.vagasPeriodo1}"	onkeyup="return formatarInteiro(this)" maxlength="3" converter="#{intConverter}" id="vagasperiodo1"></h:inputText>
					<h:outputText styleClass="required"></h:outputText>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText value="2º Período" />
					</f:facet>
					<h:inputText size="3" value="#{item.vagasPeriodo2}"	onkeyup="return formatarInteiro(this)" maxlength="3" converter="#{intConverter}" id="vagasperiodo2"></h:inputText>
					<h:outputText styleClass="required"></h:outputText>
				</h:column>
			</t:dataTable>
		</h:panelGroup></div>
		<table class="formulario" width="80%">
			<tfoot>
				<tr>
					<td>
						<h:commandButton id="cadastrar" value="Atualizar" action="#{cadastroOfertaVagasCurso.cadastrar}" />
						<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{cadastroOfertaVagasCurso.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>