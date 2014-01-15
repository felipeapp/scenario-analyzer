<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="matriculaMedio"/>
<h2> <ufrn:subSistema /> &gt; Matricular Aluno em Série</h2>
<c:set value="#{matriculaMedio.obj.discenteMedio}" var="discente" />
<%@ include file="/medio/discente/info_discente.jsp"%>

<h:form id="form">
	<table class="formulario" style="width: 90%">
		<caption>Matricular discente de ensino médio</caption>
		<h:inputHidden value="#{matriculaMedio.obj.id}" />
		
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td>
				<a4j:region>
					<h:selectOneMenu value="#{matriculaMedio.obj.turmaSerie.serie.cursoMedio.id}" id="selectCurso"
						valueChangeListener="#{matriculaMedio.carregarSeriesByCurso }" style="width: 95%">
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
				<th class="obrigatorio">Série:</th>
				<td>
					<h:selectOneMenu value="#{ matriculaMedio.obj.turmaSerie.serie.id }" style="width: 95%;" id="selectSerie">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ matriculaMedio.seriesByCurso }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano:</th>
				<td>
					<h:inputText value="#{matriculaMedio.ano}" size="4" maxlength="4" 
					onkeyup="return formatarInteiro(this);"/> 
				</td>
			</tr>										
			<tfoot>
		   	<tr>
				<td colspan="2">
					<h:commandButton value="Buscar Turmas" action="#{matriculaMedio.buscarTurmas}" id="buscar" />
					<h:commandButton value="<< Voltar" action="#{matriculaMedio.voltarBuscaDiscente}" id="voltar"/>
					<h:commandButton value="Cancelar" action="#{matriculaMedio.cancelar}" onclick="#{confirm}" id="cancelarBusca" />
				</td>
		   	</tr>
		</tfoot>
		
	</table>
	<br/><br/>
	<c:if test="${fn:length(matriculaMedio.listaTurmaSeries) > 0 }">
	<table id="tableDisciplinas" class="formulario" width="90%">
		<caption>Turmas (${fn:length(matriculaMedio.listaTurmaSeries)})</caption>
		<thead>
			<tr>
				<th width="2%"></th>
				<th>Turma</th>
				<th width="15%" align="left">Mat./Cap.</th>
			</tr>
		</thead>
		<tbody>	
			<c:forEach var="linha" items="#{matriculaMedio.listaTurmaSeries}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>
						<input type="radio" name="id" value="${linha.id}" id="radio${linha.id}"/>
					</td>
						
					<td><label for="radio${linha.id}">${linha.serie.descricaoCompleta} - ${linha.dependencia ?'Dependência':linha.nome}</label></td>
					<td style="text-align: left;" > ${linha.qtdMatriculados}/${linha.capacidadeAluno} alunos</td>
				</tr>		
			</c:forEach>
			<c:if test="${ fn:length(matriculaMedio.listaTurmaSeries) == 0 }">
				<tr><td colspan="3">Nenhuma Turma Localizada.</td></tr>
			</c:if>
		</tbody>		
		<tfoot>
		   	<tr>
				<td colspan="3">
					<h:commandButton value="Matricular" action="#{matriculaMedio.submeterDadosMatriculaMedio}" id="matricular" />
					<h:commandButton value="Cancelar" action="#{matriculaMedio.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   	</tr>
		</tfoot>
	</table>
	</c:if>
	<br/>
	<div class="obrigatorio" style="width: 90%"> Campos de preenchimento obrigatório. </div>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>