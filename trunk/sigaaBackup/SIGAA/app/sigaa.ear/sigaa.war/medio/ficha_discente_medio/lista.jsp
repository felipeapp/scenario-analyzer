<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="fichaDiscenteMedio"/>
	<a4j:keepAlive beanName="turmaSerie"/>
	<h2> <ufrn:subSistema /> &gt; Fichas Dos Alunos</h2>
	<h:form>
		<table class="formulario" style="width: 80%">
		<caption>Dados das Turmas</caption>
		<h:inputHidden value="#{turmaSerie.obj.id}" />
		<tbody>
			<tr>
				<th class="obrigatorio" width="40%">Ano:</th>
				<td><h:inputText value="#{fichaDiscenteMedio.obj.ano}" size="5" maxlength="4" onkeyup="return formatarInteiro(this);" /></td>
			</tr>
			<tr>
				<th class="required">Curso:</th>
				<td>
				<a4j:region>
					<h:selectOneMenu value="#{fichaDiscenteMedio.obj.serie.cursoMedio.id}" id="selectCurso"
						valueChangeListener="#{turmaSerie.carregarSeriesByCurso }" style="width: 75%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
				 		<a4j:support event="onchange" reRender="selectSerie" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="required">Série:</th>
				<td>
					<h:selectOneMenu value="#{ fichaDiscenteMedio.obj.serie.id }" style="width: 75%;" id="selectSerie">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ turmaSerie.seriesByCurso }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>
					<h:selectBooleanCheckbox id="checkAtivo" value="#{fichaDiscenteMedio.turmasInativas}" styleClass="noborder" /> 
				</th>
				<td> Listar Turmas Inativas.</td>
			</tr>
		</tbody>
		<tfoot>
		   	<tr>
				<td colspan="2">
					<h:commandButton value="Listar Turmas" action="#{fichaDiscenteMedio.buscar}" id="listarTurmas" />
					<h:commandButton value="Cancelar" action="#{fichaDiscenteMedio.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   	</tr>
		</tfoot>	
	</table>
	<br />
	
	<c:if test="${not empty fichaDiscenteMedio.listaTurmaSeries}">
		<div class="infoAltRem" align="center">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible; margin-right: 0px;"/>: Selecionar Turma
		</div>
		<table class="listagem" style="width: 80%">
			<caption>Turmas Encontradas</caption>
			<thead>
				<tr>
					<td>Curso</td>
					<td>Turma</td>
					<td>Ano</td>
					<td></td>
				</tr>
			</thead>
			<c:forEach var="turma" items="#{fichaDiscenteMedio.listaTurmaSeries}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>
						<h:outputText value="#{turma.serie.descricao}" />
					</td>
					<td>
						<h:outputText value="#{turma.nome}" />
					</td>
					<td>
						<h:outputText value="#{turma.ano}" />
					</td>
					<td>
						<h:commandLink action="#{fichaDiscenteMedio.selecionarTurma}">
							<h:graphicImage value="/img/seta.gif" style="float: right; margin: 0 2px 5px 0;" title="Selecionar Turma" />
							<f:param name="id" value="#{turma.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>