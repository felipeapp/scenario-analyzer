<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> &gt; Alocação de Turma em Espaço Físico</h2>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Seleção de Turma</caption>
			<tbody>
				<tr>
					<th>Centro: </th>
					<td>
						<a4j:region>
							<h:selectOneMenu id="centros" value="#{alocarTurmaBean.centro.id}" immediate="true"
								valueChangeListener="#{unidade.changeCentro}"	>
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
									<f:selectItems value="#{unidade.allCentroCombo}"/>
									<f:selectItem itemLabel="UFRN" itemValue="605"/>
									<a4j:support reRender="departamentos" event="onchange" />
							</h:selectOneMenu>&nbsp;
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
						</a4j:region>
					</td>
				</tr>
			<a4j:region>
				<tr>
					<th>Departamento: </th>
					<td>
						<h:selectOneMenu id="departamentos" value="#{alocarTurmaBean.departamento.id}" immediate="true"
							valueChangeListener="#{alocarTurmaBean.carregaTurmas}">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
								<f:selectItems value="#{unidade.unidades}"/>
								<a4j:support reRender="turmas" event="onchange" />
						</h:selectOneMenu>&nbsp;
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/ajax-loader.gif"/>
							</f:facet>
						</a4j:status>
					</td>
				</tr>
			</a4j:region>
				<tr>
					<td colspan="2">
						<t:dataTable value="#{alocarTurmaBean.turmas}" var="t" id="turmas" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							<t:column>
								<f:facet name="header">
									<f:verbatim>Ano-Período</f:verbatim>
								</f:facet>
								<h:outputText value="#{t.ano}" />.<h:outputText value="#{t.periodo}" />
							</t:column>
						
							<t:column>
								<f:facet name="header">
									<f:verbatim>Disciplina</f:verbatim>
								</f:facet>
								<h:outputText value="#{t.disciplina.codigo}" /> - <h:outputText value="#{t.disciplina.nome}" />
							</t:column>
						
							<t:column>
								<f:facet name="header">
									<f:verbatim>Cód. Turma</f:verbatim>
								</f:facet>
								<h:outputText value="#{t.codigo}" />
							</t:column>
																					
							<t:column>
								<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
								<h:commandLink action="#{alocarTurmaBean.selecionarTurma}">
									<f:param name="id" value="#{t.id}"/>
									<h:graphicImage url="/img/seta.gif"/>
								</h:commandLink>
							</t:column>
							
						</t:dataTable>
					</td>
				</tr>
			</tbody>
			<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cancelar" action="#{alocarTurmaBean.cancelar}" onclick="#{confirm}" />
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>