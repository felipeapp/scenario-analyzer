<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Equivalências Específicas</h2>

	<h:form id="form">
		<table class="formulario">
			<caption>Cadastro de Equivalências Específicas</caption>
			<tr>
				<th><h:outputLabel id="label_tipo" value="Tipo de Equivalência:" styleClass="required"/></th>
				<td>
					<a4j:region>
						<h:selectOneMenu id="tipos" value="#{equivalenciaMBean.obj.tipo}" style="width: 550px;"
						valueChangeListener="#{equivalenciaMBean.carregarCombosByTipo }">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --" />
							<f:selectItems value="#{equivalenciaMBean.allTipoCombo}" />
							<a4j:support event="onchange" reRender="label_curso, cursos, label_matriz, matrizes, curriculo, label_curriculo" />
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
				<th> <h:outputLabel id="label_curso" value="Curso:" styleClass="#{equivalenciaMBean.selectCurso ? '' : 'required'}" /></th>
				<td>
					<a4j:region>
						<h:selectOneMenu id="cursos" value="#{equivalenciaMBean.curso.id}" disabled="#{equivalenciaMBean.selectCurso}">
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
				<th nowrap="nowrap"><h:outputLabel id="label_matriz" value="Matriz Curricular:" styleClass="#{equivalenciaMBean.selectMatriz ? '' : 'required'}"/></th>
				<td>
					<a4j:region>
						<h:selectOneMenu id="matrizes" value="#{equivalenciaMBean.matriz.id }" disabled="#{equivalenciaMBean.selectMatriz}">
							<f:selectItems value="#{equivalenciaMBean.matrizesCurriculares}" />
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
				<th><h:outputLabel id="label_curriculo" value="Currículo:" styleClass="#{equivalenciaMBean.selectCurriculo ? '' : 'required'}"/></th>
				<td>
					<h:selectOneMenu id="curriculo" value="#{equivalenciaMBean.curriculo.id }" disabled="#{equivalenciaMBean.selectCurriculo}">
						<f:selectItems value="#{equivalenciaMBean.curriculos}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th><h:outputLabel id="label_comp" value="Componente Curricular:" styleClass="required"/></th>
				<td>
					<a4j:region>
						<h:inputText value="#{equivalenciaMBean.obj.componente.nome}" id="nomeComponente" style="width: 440px;"/> 
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
								<f:setPropertyActionListener value="#{_componente.id}" target="#{equivalenciaMBean.obj.componente.id}"/>
							</a4j:support>
						</rich:suggestionbox>	
		            </a4j:region>
				<ufrn:help>É necessário selecionar o nome do componente curricular.</ufrn:help>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<div class="descricaoOperacao">
					<p style="text-align: center; font-style: italic">
					 	<b>Atenção!</b> A expressão equivalência deve ser cercada por parênteses.
						<b>Exemplo: ( ( DIM0052 ) E ( DIM0301 OU DIM0053 ) )</b>
					</p>
				</div>
				</td>
			</tr>
			<tr>
				<th><h:outputLabel id="label_exp" value="Expressão de Equivalência:" styleClass="required"/></th>
				<td><h:inputText id="expressao" value="#{ equivalenciaMBean.obj.expressao }" size="50" onkeyup="CAPS(this)"/></td>
			</tr>
			
			<tr>
				<th><h:outputLabel id="label_periodo" value="Fim da Vigência:"/></th>
				<td>
					<h:inputText id="anoFim" value="#{ equivalenciaMBean.obj.anoFimVigencia }" style="text-align: center;" onkeyup="return formatarInteiro(this);" size="4" maxlength="4"/>.
					<h:inputText id="periodoFim" value="#{ equivalenciaMBean.obj.periodoFimVigencia }" style="text-align: center;" onkeyup="return formatarInteiro(this);" size="1" maxlength="1"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden id="id" value="#{equivalenciaMBean.obj.id}" /> 
						<h:commandButton value="#{equivalenciaMBean.confirmButton}" action="#{equivalenciaMBean.cadastrar}" id="btnConfirmar"/> 
						<c:choose>
							<c:when test="${equivalenciaMBean.obj.id > 0}">
								<h:commandButton value="Cancelar" action="#{equivalenciaMBean.backList}" immediate="true" id="btnListar"/>
							</c:when>
							<c:otherwise>
								<h:commandButton value="Cancelar" action="#{equivalenciaMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancel"/>
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
