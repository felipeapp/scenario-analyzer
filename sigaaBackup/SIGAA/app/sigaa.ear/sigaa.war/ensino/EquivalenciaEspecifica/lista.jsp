<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Equivalências Específicas</h2>
	
	<h:form id="busca">
	
	<table class="formulario" width="90%">
		<caption>Busca por Equivalências Específicas</caption>
		<tbody>
			<tr>
				<td><h:selectBooleanCheckbox value="#{equivalenciaEspecificaMBean.chkCurso}" styleClass="noborder" id="checkCurso" /></td>
				<td><label for="busca:checkCurso">Curso: </label></td>
				<td>
					<a4j:region>
						<h:selectOneMenu id="cursos" value="#{equivalenciaEspecificaMBean.curso.id}" 
							style="width: 550px;" onfocus="$('busca:checkCurso').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{cursoGrad.allCombo}" />
							<a4j:support event="onchange" reRender="matrizes" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start"><h:graphicImage value="/img/ajax-loader.gif"/></f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{equivalenciaEspecificaMBean.chkMatriz}" styleClass="noborder" id="checkMatriz" /></td>
				<td><label for="busca:checkMatriz">Matriz Curricular:</label></tD>
				<td>
					<a4j:region>
						<h:selectOneMenu id="matrizes" value="#{equivalenciaEspecificaMBean.matriz.id }"
						 	onfocus="$('busca:checkMatriz').checked = true;">
							<f:selectItems value="#{equivalenciaEspecificaMBean.matrizesCurriculares}" />
							<a4j:support event="onchange" reRender="curriculo" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start"><h:graphicImage value="/img/ajax-loader.gif"/></f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{equivalenciaEspecificaMBean.chkCurriculo}" styleClass="noborder" id="checkCurriculo" /></td>
				<td><label for="busca:checkCurriculo">Currículo:</label></tD>
				<td>
					<h:selectOneMenu id="curriculo" value="#{equivalenciaEspecificaMBean.obj.curriculo.id }"
						onfocus="$('busca:checkCurriculo').checked = true;">
						<f:selectItems value="#{equivalenciaEspecificaMBean.curriculos}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{equivalenciaEspecificaMBean.chkComponente}" styleClass="noborder" id="checkComponente" /></td>
				<td><label for="busca:checkComponente">Componente Curricular:</label></td>
				<td>
					<a4j:region>
						<h:inputText value="#{equivalenciaEspecificaMBean.obj.componente.nome}" id="nomeComponente" 
							style="width: 440px;" onfocus="$('busca:checkComponente').checked = true;" /> 
						<rich:suggestionbox width="400" height="120" for="nomeComponente" 
							minChars="6" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
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
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscarButton" value="Buscar" action="#{equivalenciaEspecificaMBean.buscar}" />
					<h:commandButton id="cancelarButton" value="Cancelar" onclick="#{confirm}" action="#{equivalenciaEspecificaMBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
		
	
		<c:if test="${ not empty equivalenciaEspecificaMBean.resultadosBusca }">
			<div class="infoAltRem" style="width: 100%">
				<img src="/shared/img/alterar.gif" style="overflow: visible;" />: Alterar 
				<h:graphicImage value="/img/check.png" style="overflow: visible; margin-left: 5px;" />: Ativar Equivalência Especifica
				<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />: Inativar Equivalência Especifica
			</div>
			<table class="listagem">
				<caption>Lista de Equivalências Específicas</caption>
				<thead>
					<tr>
						<th>Curso</th>
						<th>Matriz</th>
						<th>Currículo</th>
						<th>Componente</th>
						<th>Ativo</th>
						<th>Início</th>
						<th>Fim</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{equivalenciaEspecificaMBean.resultadosBusca}" var="item"
					varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${ item.curriculo.matriz.curso }</td>
						<td>${ item.curriculo.matriz.descricaoMin }</td>
						<td>${item.curriculo}</td>
						<td>${item.componente}</td>
						<td><ufrn:format type="SimNao" valor="${item.ativo}"/></td>
						<td><fmt:formatDate value="${item.inicioVigencia}" pattern="dd/MM/yyyy" /></td>
						<td><fmt:formatDate value="${item.fimVigencia}" pattern="dd/MM/yyyy" /></td>
						<td>
							<h:commandLink action="#{ equivalenciaEspecificaMBean.atualizar }">
								<f:verbatim><img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" /></f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink styleClass="noborder" action="#{equivalenciaEspecificaMBean.inativarOuAtivar}">
								<f:param name="id" value="#{ item.id }" />
								<c:if test="${!item.ativo }">
									<h:graphicImage url="/img/check.png" alt="Ativar Equivalência Especifica" title="Ativar Equivalência Especifica"  />
								</c:if>	
								<c:if test="${item.ativo }">
									<h:graphicImage url="/img/check_cinza.png" alt="Inativar Equivalência Especifica" title="Inativar Equivalência Especifica"  />
								</c:if>	
							</h:commandLink>										
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
