<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.jsf.CursoMBean"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>
<style>
table.formulario th { font-weight: bold; !important}
table.formulario td { text-align: left; }
table.listagem tr.curso td{
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}
</style>

<f:view>
<a4j:keepAlive beanName="validacaoVinculo" />
	<h:form id="form">
	<h2 class="title"><ufrn:subSistema /> > Validação de Vínculos de Ingressante</h2>

	<table class="formulario"  width="80%">
		<caption>Informe os Critérios de Busca</caption>
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.DAE } %>">
		<tr>
			<td width="1px;"><h:selectBooleanCheckbox value="#{validacaoVinculo.filtroMatricula}" id="checkMatricula" styleClass="noborder" /></td>
			<td>
				&nbsp;<span style="text-align:right;font-weight:normal">Matrícula:</span>&nbsp;
			</td>
			<td>
				<h:inputText value="#{validacaoVinculo.discente.matricula}" size="14" id="matriculaDiscente" maxlength="12" onkeyup="return formatarInteiro(this);" onfocus="$('form:checkMatricula').checked = true;"/>
			</td>	
		</tr>
		<tr>
			<td width="1px;"><h:selectBooleanCheckbox value="#{validacaoVinculo.filtroCpf}" id="checkCpf" styleClass="noborder" /></td>
			<td>	
				&nbsp;<span style="text-align:right;font-weight:normal">CPF:</span>&nbsp;
			</td>	
			<td>
				<h:inputText id="cpf" value="#{validacaoVinculo.discente.pessoa.cpf_cnpj}" size="14" maxlength="14" onblur="formataCPF(this, event, null)" onkeypress="return formataCPF(this, event, null)" onfocus="$('form:checkCpf').checked = true;" >
					<f:converter converterId="convertCpf"/>
      				<f:param name="type" value="cpf" />						
				</h:inputText>
			</td>
		</tr>
		<tr>
			<td width="1px;"><h:selectBooleanCheckbox value="#{validacaoVinculo.filtroNomeDiscente}" id="checkNomeDiscente" styleClass="noborder" /></td>
			<td>	
				&nbsp;<span style="text-align:right;font-weight:normal">Nome do Discente:</span>&nbsp;
			</td>	
			<td>
				<h:inputText id="nomeDiscente" value="#{validacaoVinculo.discente.pessoa.nome }" size="60" onfocus="$('form:checkNomeDiscente').checked = true;"/>
			</td>
		</tr>

		<tr>
			<td width="1px;"><h:selectBooleanCheckbox value="#{validacaoVinculo.filtroNomeCurso}" id="checkNomeCurso" styleClass="noborder" /></td>
			<td>
				&nbsp;<span style="text-align:right;font-weight:normal;">Curso:</span>&nbsp;
			</td>
			<td>
				<a4j:region id="curso">
				<h:inputHidden id="idCurso" value="#{validacaoVinculo.discente.curso.id}" />
				<h:inputText id="nomeCurso" value="#{validacaoVinculo.discente.curso.nome}" size="60" onfocus="$('form:checkNomeCurso').checked = true;">
				</h:inputText>
				<rich:suggestionbox
						id="suggestionNomeCurso"
						for="form:nomeCurso"
						var="_curso"
						fetchValue="#{_curso.descricao}"
						suggestionAction="#{ curso.autocompleteNomeCurso }"
						nothingLabel="Nenhum curso encontrado"
						frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200"
						width="600" height="400" minChars="3" >
					
					<h:column>
						<h:outputText value="#{ _curso.descricao }"/>
					</h:column>
					
					<a4j:support event="onselect" action="#{ validacaoVinculo.carregarMatrizes }" reRender="form:idCurso,form:matrizes" >
						<f:setPropertyActionListener value="#{ _curso.id }" target="#{ validacaoVinculo.discente.curso.id }"/>
					</a4j:support>
					
				</rich:suggestionbox>
				</a4j:region>
			</td>	
		</tr>	
		</ufrn:checkRole>
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO,  SigaaPapeis.SECRETARIA_COORDENACAO } %>">
		<tr>
			<td width="1px;"><h:selectBooleanCheckbox value="true" readonly="true" disabled="true" styleClass="check" /></td>
			<td>
				&nbsp;<span style="text-align:right;font-weight:bold;">Curso:</span>&nbsp;
			</td>
			<td>
				<h:outputText value="#{validacaoVinculo.curso.descricao}"></h:outputText>
			</td>	
		</tr>	
		</ufrn:checkRole>
		<tr>
			<td width="1px;"><h:selectBooleanCheckbox value="#{validacaoVinculo.filtroMatriz}" id="checkMatriz" styleClass="noborder" /></td>
			<td>	
				&nbsp;<span style="text-align:right;font-weight:normal">Matriz Curricular:</span>&nbsp;
			</td>	
			<td>
				<a4j:outputPanel id="matrizes">
					<h:selectOneMenu  value="#{validacaoVinculo.discente.matrizCurricular.id}" onclick="$('form:checkMatriz').checked = true;"
					 id="idMatrizCurricular" >
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					<f:selectItems value="#{validacaoVinculo.matrizesCurriculares}"/>
					</h:selectOneMenu>
				</a4j:outputPanel>
			</td>
		</tr>
		<tfoot><tr><td colspan="3" style="text-align:center;">
			<h:commandButton action="#{validacaoVinculo.filtrarDiscente}"  value=" Filtrar "/>
			<h:commandButton value="Cancelar" onclick="#{confirm}"  action="#{validacaoVinculo.cancelar}" />
			</td></tr></tfoot>
	</table><br/>

	<c:if test="${ not empty validacaoVinculo.discentes }">
	
		<div class="infoAltRem" style="text-align: center; width: 100%">
			<img src="/sigaa/ava/img/user_ok.png">: Confirmar Vínculo
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.DAE } %>">
			<img src="/sigaa/img/monitoria/user1_delete.png">: Desconfirmar Vínculo
			</ufrn:checkRole>
		</div>
	
		<table class="listagem" style="width:90%;">
				<caption> Selecione abaixo o discente (${fn:length(validacaoVinculo.discentes)}) </caption>
				<thead>
					<tr>
						<th style="text-align: center;"> Matrícula </th>
						<th> Aluno </th>
						<th style="text-align:center"> Data de Validação </th>
						<th> Status </th>
						<th> </th>
						<th> </th>
					</tr>
				</thead>
				<tbody>
					<c:set var="idFiltro" value="-1" />
					<c:forEach items="#{validacaoVinculo.discentes}" var="discente" varStatus="status">
					
						<c:set var="idLoop" value="${discente.curso.id}" />
					
						<c:if test="${discente.graduacao}">
							<c:set var="idLoop" value="${discente.matrizCurricular.id}" />
						</c:if>
					
						<c:if test="${ idFiltro != idLoop}">
							
							<c:set var="idFiltro" value="${discente.curso.id}" />
							<c:if test="${discente.graduacao}">
								<c:set var="idFiltro" value="${discente.matrizCurricular.id}" />
							</c:if>
							
							<tr class="curso">
								<td colspan="7">
									<c:if test="${discente.graduacao}">
										<h:outputText value="#{discente.matrizCurricular.descricao}"/>
									</c:if>
									<c:if test="${!discente.graduacao}">
										<h:outputText value="#{discente.curso.descricao}"/>
									</c:if>
								</td>
							</tr>
						</c:if>
						
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td style="text-align: center;">
								<h:outputText value="#{discente.matricula}"/>
							</td>
							<td>
								<h:outputText value="#{discente.nome}"/>
							</td>
							<td align="center">
								<c:if test="${!discente.discente.marcado}">						
									<fmt:formatDate value="${discente.discente.validacaoVinculo.dataValidacao}" pattern="dd/MM/yyyy HH:mm"/>
								</c:if>
							</td>
							<td>
								<h:outputText value="#{discente.statusString}"/>
							</td>
							<td>
								<c:if test="${discente.discente.marcado}">
									<h:commandLink action="#{validacaoVinculo.preConfirmarVinculo}" title="Confirmar Vínculo">
										<f:param name="idDiscente" value="#{discente.id}" />
										<h:graphicImage value="/ava/img/user_ok.png" />
									</h:commandLink>
								</c:if>
							</td>
							<td>
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.DAE } %>">
									<c:if test="${!discente.discente.marcado}">
										<h:commandLink action="#{validacaoVinculo.preDesconfirmarVinculo}" title="Desconfirmar Vínculo">
											<f:param name="idDiscente" value="#{discente.id}" />
											<h:graphicImage value="/img/monitoria/user1_delete.png" />
										</h:commandLink>
									</c:if>
								</ufrn:checkRole>
							</td>
						</tr>
					</c:forEach>
				</tbody>
		</table>
	</c:if>

</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
