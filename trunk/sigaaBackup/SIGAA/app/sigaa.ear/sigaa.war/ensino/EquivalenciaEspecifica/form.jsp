<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Equivalências Específicas</h2>

	<h:form id="form">
		<table class="formulario">
			<caption>Cadastro de Equivalências Específicas</caption>
			<tr>
				<th>Curso: <span class="required">&nbsp;</span></th>
				<td>
					<a4j:region>
						<h:selectOneMenu id="cursos" value="#{equivalenciaEspecificaMBean.curso.id}" style="width: 550px;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CURSO --" />
							<f:selectItems value="#{cursoGrad.allCombo}" />
							<a4j:support event="onchange" reRender="matrizes" />
						</h:selectOneMenu> &nbsp;
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/ajax-loader.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">Matriz Curricular: <span class="required">&nbsp;</span></th>
				<td>
					<a4j:region>
						<h:selectOneMenu id="matrizes" value="#{equivalenciaEspecificaMBean.matriz.id }">
							<f:selectItems value="#{equivalenciaEspecificaMBean.matrizesCurriculares}" />
							<a4j:support event="onchange" reRender="curriculo" />
						</h:selectOneMenu>&nbsp;
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/ajax-loader.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th>Currículo: <span class="required">&nbsp;</span></th>
				<td>
					<h:selectOneMenu id="curriculo" value="#{equivalenciaEspecificaMBean.obj.curriculo.id }">
						<f:selectItems value="#{equivalenciaEspecificaMBean.curriculos}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Componente Curricular: <span class="required">&nbsp;</span></th>
				<td>
					<a4j:region>
						<h:inputText value="#{equivalenciaEspecificaMBean.obj.componente.nome}" id="nomeComponente" style="width: 440px;"/> 
						<rich:suggestionbox width="400" height="120" for="nomeComponente" 
							minChars="6" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="5" 
							suggestionAction="#{componenteCurricular.autocompleteGraduacao}" var="_componente" fetchValue="#{_componente.nome}">
							<h:column>
								<h:outputText value="#{_componente.codigo}"/>
							</h:column>
							<h:column>
								<h:outputText value="#{_componente.nome}"/>
							</h:column>
							<h:column>
								<h:outputText value="#{_componente.unidade.sigla}"/>
							</h:column>
							<a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_componente.id}" target="#{equivalenciaEspecificaMBean.obj.componente.id}"/>
							</a4j:support>
						</rich:suggestionbox>	
		            </a4j:region>
		            <ufrn:help>É necessário selecionar o nome do componente curricular.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Expressão de Equivalência: <span class="required">&nbsp;</span></th>
				<td><h:inputText id="expressao" value="#{ equivalenciaEspecificaMBean.obj.expressao }" size="50"/></td>
			</tr>
			<tr>
				<th>Início da Vigência: <span class="required">&nbsp;</span></th>
				<td>
					<t:inputCalendar id="inicio"
						value="#{ equivalenciaEspecificaMBean.obj.inicioVigencia }"
						renderAsPopup="true"
						renderPopupButtonAsImage="true"
						onkeypress="return(formataData(this,event))" size="10"
						maxlength="10">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th>Fim da Vigência: </th>
				<td>
					<t:inputCalendar id="fim"
						value="#{ equivalenciaEspecificaMBean.obj.fimVigencia }"
						renderAsPopup="true"
						renderPopupButtonAsImage="true"
						onkeypress="return(formataData(this,event))" size="10"
						maxlength="10">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</t:inputCalendar>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden id="id" value="#{equivalenciaEspecificaMBean.obj.id}" /> 
						<h:commandButton value="#{equivalenciaEspecificaMBean.confirmButton}" action="#{equivalenciaEspecificaMBean.cadastrar}" id="btnConfirmar"/> 
						<c:choose>
							<c:when test="${equivalenciaEspecificaMBean.obj.id > 0}">
								<h:commandButton value="Cancelar" action="#{equivalenciaEspecificaMBean.listar}" immediate="true" id="btnListar"/>
							</c:when>
							<c:otherwise>
								<h:commandButton value="Cancelar" action="#{equivalenciaEspecificaMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancel"/>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
