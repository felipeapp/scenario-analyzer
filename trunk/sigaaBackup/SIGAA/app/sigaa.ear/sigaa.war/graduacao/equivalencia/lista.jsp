<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Equivalências Específicas</h2>
	
	<h:form id="busca" prependId="true">
	
	<table class="formulario" width="90%">
		<caption>Busca por Equivalências Específicas</caption>
		<tbody>
			<tr>
				<td><h:selectBooleanCheckbox value="#{equivalenciaMBean.chkTipoEquivalencia}" styleClass="noborder" id="checkTipoEquiv" /></td>
				<td><label for="busca:checkTipoEquiv">Tipo de Equivalência: </label></td>
				<td>
					<a4j:region>
						<h:selectOneMenu id="tipos" value="#{equivalenciaMBean.obj.tipo}" style="width: 550px;"
							valueChangeListener="#{equivalenciaMBean.carregarCombosByTipo }" onfocus="$('busca:checkTipoEquiv').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --" />
							<f:selectItems value="#{equivalenciaMBean.allTipoCombo}" />
							<a4j:support event="onchange" reRender="cursos, matrizes, curriculo" />
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
				<td><h:selectBooleanCheckbox value="#{equivalenciaMBean.chkCurso}" styleClass="noborder" id="checkCurso" /></td>
				<td><label for="busca:checkCurso">Curso: </label></td>
				<td>
					<a4j:region>
						<h:selectOneMenu id="cursos" value="#{equivalenciaMBean.curso.id}" 
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
				<td><h:selectBooleanCheckbox value="#{equivalenciaMBean.chkMatriz}" styleClass="noborder" id="checkMatriz" /></td>
				<td><label for="busca:checkMatriz">Matriz Curricular:</label></tD>
				<td>
					<a4j:region>
						<h:selectOneMenu id="matrizes" value="#{equivalenciaMBean.matriz.id }"
						 	onfocus="$('busca:checkMatriz').checked = true;">
							<f:selectItems value="#{equivalenciaMBean.matrizesCurriculares}" />
							<a4j:support event="onchange" reRender="curriculo" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start"><h:graphicImage value="/img/ajax-loader.gif"/></f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{equivalenciaMBean.chkCurriculo}" styleClass="noborder" id="checkCurriculo" /></td>
				<td><label for="busca:checkCurriculo">Currículo:</label></tD>
				<td>
					<h:selectOneMenu id="curriculo" value="#{equivalenciaMBean.curriculo.id }"
						onfocus="$('busca:checkCurriculo').checked = true;">
						<f:selectItems value="#{equivalenciaMBean.curriculos}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td><h:selectBooleanCheckbox value="#{equivalenciaMBean.chkComponente}" styleClass="noborder" id="checkComponente" /></td>
				<td><label for="busca:checkComponente">Componente Curricular:</label></td>
				<td>
					<a4j:region>
						<h:inputText value="#{equivalenciaMBean.obj.componente.nome}" id="nomeComponente" 
							style="width: 440px;" onfocus="$('busca:checkComponente').checked = true;" onkeyup="CAPS(this)"/> 
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
								<f:setPropertyActionListener value="#{_componente.id}" target="#{equivalenciaMBean.obj.componente.id}"/>
							</a4j:support>
						</rich:suggestionbox>	
		            </a4j:region>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscarButton" value="Buscar" action="#{equivalenciaMBean.buscar}" />
					<h:commandButton id="cancelarButton" value="Cancelar" onclick="#{confirm}" action="#{equivalenciaMBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	</h:form>
	<c:if test="${ not empty  equivalenciaMBean.resultadoMapPaginado }">
		<div class="infoAltRem" style="width: 100%">
			<img src="/shared/img/alterar.gif" style="overflow: visible;" />: Alterar 
			<h:graphicImage value="/img/check.png" style="overflow: visible; margin-left: 5px;" />: Ativar Equivalência Especifica
			<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />: Inativar Equivalência Especifica
		</div>
		<table class="listagem">
			<caption>Lista de Equivalências Específicas</caption>
			<c:forEach items="#{equivalenciaMBean.resultadoMapPaginado}" var="itemMap" varStatus="status">
				<thead>
					<c:set var="obj" value="#{itemMap.value[0]}"/>
					<tr style="background-color: #C8D5EC;">
						<th colspan="9" style="text-align: center; padding: 10px;">
							<c:out value="EQUIVALÊNCIA ${obj.tipoGlobal ? '' : 'POR '} ${obj.tipoEquivalencia.nome}"/>
						</th>
					</tr>
					<tr>
						<th width="35%"><h:outputText value="Componente Curricular"/></th>
						<c:choose>
							<c:when test="${!obj.tipoGlobal}">
								<th><h:outputText value="Curso" /></th>
								<th><h:outputText value="Matriz" rendered="#{!obj.tipoCurso}"/></th>
								<th><h:outputText value="Currículo" rendered="#{obj.tipoCurriculo}"/></th>
							</c:when>
							<c:otherwise>
								<th colspan="3"><h:outputText value="Expressão" rendered="#{obj.tipoGlobal}"/></th>
							</c:otherwise>
						</c:choose>
						<th align="left">Fim da Vigência</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tr>
					<c:forEach items="#{itemMap.value}" var="item" varStatus="loop">
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${ item.componente }</td>
							<c:choose>
								<c:when test="${!obj.tipoGlobal}">
									<td>${ item.curso }</td>
									<td>${ item.matriz.descricaoMin }</td>
									<td>${ item.curriculo }</td>
								</c:when>
								<c:otherwise>
									<td colspan="3"><h:outputText value="#{item.expressao}" rendered="#{obj.tipoGlobal}"/></td>
								</c:otherwise>
							</c:choose>
							<td align="center">${ item.fimVigenciaToString }</td>
							<td>
								<h:form>
								<h:commandLink action="#{  equivalenciaMBean.atualizar }">
									<f:verbatim><img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" /></f:verbatim>
									<f:param name="id" value="#{ item.id }" />
								</h:commandLink>
								</h:form>
							</td>
							<td>
								<h:form>
								<h:commandLink styleClass="noborder" action="#{equivalenciaMBean.inativarOuAtivar}">
									<f:param name="id" value="#{ item.id }" />
									<c:if test="${!item.ativo }">
										<h:graphicImage url="/img/check.png" alt="Ativar Equivalência Especifica" title="Ativar Equivalência Especifica"  />
									</c:if>	
									<c:if test="${item.ativo }">
										<h:graphicImage url="/img/check_cinza.png" alt="Inativar Equivalência Especifica" title="Inativar Equivalência Especifica"  />
									</c:if>	
								</h:commandLink>
								</h:form>										
							</td>
						</tr>
					</c:forEach>	
				</tr>
			</c:forEach>
		</table>
			<center>
	<h:form id="formPaginacao">
		<br/>
		<div style="text-align: center;"> 
			<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }" style="vertical-align:middle" id="paginacaoVoltar"/>
			<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true" id="mudaPagina">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
			</h:selectOneMenu>
			<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}" style="vertical-align:middle" id="paginacaoAvancar"/>
			<br/><br/>
  				<em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
			</div>
	</h:form>
	</center>
	</c:if>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>