<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="curriculoMedio"/>
<h2> <ufrn:subSistema /> > Listagem dos Currículos de Ensino Médio</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Informe os critérios da busca</caption>
 		<tbody>
			<tr>
				<td width="3%">
					<h:selectBooleanCheckbox value="#{curriculoMedio.filtroCodigo}" id="checkCodigo" styleClass="noborder"/> 
				</td>
				<td width="20%">
					<label for="checkCodigo" onclick="$('form:checkCodigo').checked = !$('form:checkCodigo').checked;">Código:</label>
				</td>
				<td>
					<h:inputText size="10" value="#{curriculoMedio.obj.codigo}" onfocus="$('form:checkCodigo').checked = true;" />
				</td>
			</tr>
			<tr>
				<td width="3%">
					<h:selectBooleanCheckbox value="#{curriculoMedio.filtroCurso}" id="checkCurso" styleClass="noborder"/> 
				</td>
				<td width="20%">
					<label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">Curso:</label>
				</td>
				<td>
				<a4j:region>
					<h:selectOneMenu value="#{curriculoMedio.obj.cursoMedio.id}" id="selectCurso" onfocus="$('form:checkCurso').checked = true;"
						valueChangeListener="#{curriculoMedio.carregarSeriesByCurso }" style="width: 95%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
				 		<a4j:support event="onchange" reRender="selectSerie" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			<tr>
				<td width="3%">
					<h:selectBooleanCheckbox value="#{curriculoMedio.filtroSerie}" id="checkSerie" styleClass="noborder"/> 
				</td>
				<td width="20%">
					<label for="checkSerie" onclick="$('form:checkSerie').checked = !$('form:checkSerie').checked;">Série:</label>
				</td>
				<td>
					<h:selectOneMenu value="#{ curriculoMedio.obj.serie.id }" style="width: 95%;" id="selectSerie" onfocus="$('form:checkSerie').checked = true;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ curriculoMedio.seriesByCurso }" />
					</h:selectOneMenu>
				</td>
			</tr>
	    </tbody>
	
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{curriculoMedio.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{curriculoMedio.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />

<c:if test="${not empty curriculoMedio.listaCurriculos}">

<div class="infoAltRem">
	<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Estrutura Curricular
	<c:if test="${not acesso.pedagogico }">
	<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Estrutura Curricular
	<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />: Inativar Estrutura Curricular
	<h:graphicImage value="/img/check.png" style="overflow: visible;" />: Ativar Estrutura Curricular
	</c:if>
</div>
	
	<table class="listagem">
	  <caption>Estruturas Curriculares Encontradas (${ fn:length(curriculoMedio.listaCurriculos) }) </caption>
		<thead>
			<tr>
				<th width="10%" style="text-align: left;">Código</th>
				<th style="text-align: left;">Descrição</th>
				<th style="text-align: center;"> Ano de Entrada em Vigor</th>
				<th style="text-align: right;">C.H.</th>
				<th style="text-align: center">Ativo</th>
				<th colspan="3"></th>
			</tr>
		</thead>
		<tbody>
		
		   <c:forEach var="linha" items="#{curriculoMedio.listaCurriculos}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: left;">${linha.codigo}</td>
					<td style="text-align: left;">${linha.descricao}</td>
					<td style="text-align: center;">${linha.anoEntradaVigor}</td>
					<td style="text-align: right;">${linha.cargaHoraria}h</td>
					<td style="text-align: center"><ufrn:format type="bool_sn" valor="${ linha.ativo }" /></td>
					<td width="2%" align="right">
						<h:commandLink action="#{curriculoMedio.view}" >
							<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Visualizar Estrutura Curricular" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
					<c:if test="${not acesso.pedagogico }">
					<td width="2%" align="right">
						<h:commandLink action="#{curriculoMedio.atualizar}" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Estrutura Curricular" />  
							<f:param name="id" value="#{linha.id}"/> 
						</h:commandLink>
					</td>
					<td width="2%" align="right">
						<c:choose>
							<c:when test="${linha.ativo == true || linha.ativo == null}">
								<h:commandLink id="inativar" styleClass="noborder" title="Inativar Estrutura Curricular" action="#{curriculoMedio.preInativarOuAtivar}">
									<h:graphicImage url="/img/check_cinza.png" />
									<f:param name="id" value="#{linha.id}" />
								</h:commandLink>
							</c:when>
							<c:otherwise>
								<h:commandLink id="ativar" styleClass="noborder" title="Ativar Estrutura Curricular" action="#{curriculoMedio.preInativarOuAtivar}">
									<h:graphicImage url="/img/check.png" />
									<f:param name="id" value="#{linha.id}" />
								</h:commandLink>
							</c:otherwise>
						</c:choose>
					</td>
					</c:if>
				</tr>
		   </c:forEach>
		</tbody>
	</table>
</c:if>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>