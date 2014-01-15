<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
		<h:messages />
		<h2><ufrn:subSistema /> > Itens de um prorama</h2>

		<table class="listagem">
			<caption class="listagem">Curso e Disciplinas à Distância</caption>
			<tr>
				<td>Curso:</td>
				<td>
					<a4j:region>
						<h:selectOneMenu id="tipo"
							valueChangeListener="#{itemProgramaMBean.changeCursoDistancia}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{itemProgramaMBean.allCursosDistancia}" />
							<a4j:support event="onchange" reRender="disciplinas" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<td>Disciplina:</td>
				<td>
					<a4j:region>
						<h:selectOneMenu id="disciplinas" value="#{itemProgramaMBean.idComponenteCurricular}"
							valueChangeListener="#{itemProgramaMBean.changeDisciplinaPorCurso}">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{itemProgramaMBean.allDisciplinasPorCurso}" />
							<a4j:support event="onchange" reRender="itensProgram" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
		</table>
		<br />
		<div class="infoAltRem">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Programa<br />
		</div>
		<t:dataTable value="#{itemProgramaMBean.dataModel}" var="etapa"
			id="itensProgram" styleClass="listagem" style="width:100%"
			rowClasses="linhaPar,linhaImpar">
			<f:facet name="caption">
				<f:verbatim>Itens de um programa</f:verbatim>
			</f:facet>

			<t:column style="width: 250px; text-align: left;">
				<f:facet name="header">
					<f:verbatim>Aula</f:verbatim>
				</f:facet>
				<h:outputText value="#{etapa.aula}" />
			</t:column>
			<t:column style="width: 250px; text-align: left;">
				<f:facet name="header">
					<f:verbatim>Conteúdo</f:verbatim>
				</f:facet>
				<h:outputText value="#{etapa.conteudo}" />
			</t:column>

			<t:column style="width: 15px;">

				<h:commandLink action="#{ itemProgramaMBean.removerItemPrograma }"
					styleClass="confirm-remover" onclick="#{confirm}">
					<h:graphicImage value="/img/delete.gif" />
				</h:commandLink>

			</t:column>

		</t:dataTable>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
