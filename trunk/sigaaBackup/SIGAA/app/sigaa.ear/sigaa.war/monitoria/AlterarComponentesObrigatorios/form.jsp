<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Alterar Componentes Obrigatórios do Projeto</h2>
	<br>
	<h:form>

	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:inputHidden value="#{projetoMonitoria.confirmButton}"/>
	<h:inputHidden value="#{projetoMonitoria.obj.id}"/>
	
	<table class="formulario" width="100%">
	<caption class="listagem"> Alterar Componentes Obrigatórios do Projeto </caption>

	<tr>
		<th width="18%">Projeto de Ensino:</th> <td><b><h:outputText value="#{projetoMonitoria.obj.titulo}"/></b></td>
	</tr>
	<tr>
		<th>Ano: </th> <td><b><h:outputText value="#{projetoMonitoria.obj.ano}"/></b></td>
	</tr>

	<tr>
		<td colspan="2">		
		
		
		<table class="subFormulario" width="100%">
		<caption>Lista de Componentes Curriculares do Projeto</caption>
			<c:if test="${ not empty projetoMonitoria.obj.componentesCurriculares }">
				<tr><td>
					<t:dataTable value="#{projetoMonitoria.obj.componentesCurriculares}" var="compCurricular" rowClasses="linhaPar,linhaImpar" width="100%" id="comp">

						<t:column>
							<f:facet name="header"><f:verbatim>Componente Curricular</f:verbatim></f:facet>
							<h:outputText value="#{compCurricular.disciplina.codigoNome}" id="nomeComponenteCurricular"/>
						</t:column>

						<t:column>
							<f:facet name="header"><f:verbatim>Oferta</f:verbatim></f:facet>
							<h:outputText value="#{compCurricular.semestre1 ? '(1º SEM) ': ''}" id="semestre1" />
							<h:outputText value="#{compCurricular.semestre2 ? '(2º SEM)': ''}" id="semestre2" />
						</t:column>

						<t:column>
							<f:facet name="header"><f:verbatim>Obrigatório<ufrn:help img="/img/ajuda.gif">Exige que o monitor tenha obtido média maior ou igual a 7.0 neste componente.</ufrn:help></f:verbatim></f:facet>
							<h:selectBooleanCheckbox value="#{compCurricular.obrigatorioSelecao}" id="checkObrigatorioSelecao" />
						</t:column>

					</t:dataTable>
				</td></tr>
			</c:if>
			<c:if test="${ empty projetoMonitoria.obj.componentesCurriculares }">
				<tr>
					<td align="center"><font color="red">Não há Componentes Curriculares neste projeto!</font></td>
				</tr>
			</c:if>
			</table>
		
		</td>

	</tr>
	

	<tfoot>
		<tr>
			<td colspan="2">
				<c:if test="${not empty projetoMonitoria.obj.componentesCurriculares }">
					<h:commandButton value="#{projetoMonitoria.confirmButton}" action="#{projetoMonitoria.alterarComponentesObrigatorios}" rendered="#{acesso.monitoria}"/>
				</c:if>
				<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>