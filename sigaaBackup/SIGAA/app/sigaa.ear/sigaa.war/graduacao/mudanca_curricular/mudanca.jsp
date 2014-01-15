<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
	function carregando(e) {
		if (e == 'M'){
			if ( document.getElementById('formulario:tipoMudanca') != null){
				document.getElementById('formulario:tipoMudanca').style.display = 'none';
				document.getElementById('formulario:indicatorTipo').style.display = 'block';
			}	
		}else{
			if ( document.getElementById('formulario:Nova_Matriz_Curricular') != null){
				document.getElementById('formulario:Nova_Matriz_Curricular').style.display = 'none';
				document.getElementById('formulario:tipoMudanca').style.display = 'none';
				document.getElementById('formulario:indicatorMatriz').style.display = 'block';
	}}}

	function disable(e) {
		if (e == 'M'){
			if ( document.getElementById('formulario:tipoMudanca') != null){
				document.getElementById('formulario:indicatorTipo').style.display = 'none';
				document.getElementById('formulario:Nova_Matriz_Curricular').click;
				
			}	
	}}
	
</script>

<f:view>

	<c:set var="nomeMudanca" value="${(mudancaCurricular.mudancaCurriculo)?'Estrutura':
								(mudancaCurricular.mudancaEnfase)?'Ênfase':'Matriz'}"/>
									
	<h2 class="title"><ufrn:subSistema /> > Mudança de ${nomeMudanca} Curricular</h2>
	<h:form id="formulario">
	<table class=visualizacao width="95%">
			<caption>Dados da ${nomeMudanca} Curricular</caption>
			<tr>
				<th width="15%">Discente:</th>
				<td><h:outputText value="#{mudancaCurricular.obj.matriculaNome}" /></td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>
					<h:outputText value="#{mudancaCurricular.obj.matrizCurricular.descricao}" />
				</td>
			</tr>
			<c:if test="${mudancaCurricular.mudancaCurriculo and !mudancaCurricular.mudancaCursoMatriz}">
				<tr>
					<th>Estrutura Curricular:</th>
					<td><h:outputText value="#{mudancaCurricular.curriculoAtual.descricao}" /></td>
				</tr>
			</c:if>
			<c:if test="${mudancaCurricular.mudancaEnfase}">
				<tr>
					<th>Ênfase atual:</th>
					<td><h:outputText value="#{mudancaCurricular.matrizAtual.enfase.nome}" /></td>
				</tr>
			</c:if>
			<c:if test="${(not mudancaCurricular.mudancaCurriculo and not mudancaCurricular.mudancaEnfase) or (mudancaCurricular.mudancaCursoMatriz)}">
				<tr>
					<th>Matriz Curricular Atual:</th>
					<td><h:outputText value="#{mudancaCurricular.matrizAtual.descricaoMin}" /></td>
				</tr>
			</c:if>
		</table>
		<table class="formulario" width="99%">
			<tr><td colspan="2" class="subFormulario">Nova ${nomeMudanca} Curricular</td></tr>
			<c:if test="${mudancaCurricular.mudancaCurriculo and !mudancaCurricular.mudancaCursoMatriz}">
				<tr>
					<th width="15%" class="required">Nova Estrutura Curricular:</th>
					<td><h:selectOneMenu value="#{mudancaCurricular.curriculoNovo.id}" id="curriculo">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{mudancaCurricular.possiveisCurriculos}" />
					</h:selectOneMenu> </td>
				</tr>
				<tr>
					<th>Simular Mudança:</th>
					<td>
						<h:selectBooleanCheckbox value="#{mudancaCurricular.simulacao}" id="simulacaoCurriculo">
							<a4j:support event="onclick" reRender="confirmar" />
						</h:selectBooleanCheckbox>
					</td>
				</tr>								
			</c:if>
			<c:if test="${mudancaCurricular.mudancaEnfase}">
				<tr>
					<th width="15%" class="required">Nova Ênfase Curricular:</th>
					<td>
						<h:selectOneMenu value="#{mudancaCurricular.matrizNova.id}" id="curriculo" onchange="submit()" valueChangeListener="#{mudancaCurricular.carregarDadosCurriculo }">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{mudancaCurricular.possiveisEnfases}" />
							<a4j:support event="onselect" reRender="curriculoNovaEnfase" />
						</h:selectOneMenu>
					</td>
				</tr>
				<h:panelGroup id="curriculoNovaEnfase" rendered="#{mudancaCurricular.matrizNova.id != 0 }">
					<tr>
						<th width="15%">Currículo da Nova Ênfase:</th>
						<td>${mudancaCurricular.curriculoNovo.descricao }</td>
					</tr>
				</h:panelGroup>
				<tr>
					<th>Simular Mudança:</th>
					<td>
						<h:selectBooleanCheckbox value="#{mudancaCurricular.simulacao}" id="simulacaoEnfase">
							<a4j:support event="onchange" reRender="confirmar" />
						</h:selectBooleanCheckbox>
					</td>
				</tr>								
			</c:if>
			<c:if test="${(not mudancaCurricular.mudancaCurriculo and not mudancaCurricular.mudancaEnfase) or (mudancaCurricular.mudancaCursoMatriz)}">
				<c:if test="${acesso.administradorDAE}">
					<tr>
						<th width="15%" class="required">Novo Curso:</th>
						<td>
							<h:selectOneMenu value="#{mudancaCurricular.curso.id}" id="curso" style="width: 90%"
							 onchange="carregando('C');submit();" valueChangeListener="#{mudancaCurricular.carregarMatrizes}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				<tr>
					<th width="15%" class="required">Nova Matriz Curricular:</th>
					<td>
						<h:graphicImage value="/img/indicator.gif" id="indicatorMatriz" style="display: none;" />
						
						<c:if test="${not empty mudancaCurricular.possiveisMatrizes }">
							<h:selectOneRadio value="#{mudancaCurricular.matrizNova.id}" id="Nova_Matriz_Curricular" style="width: 90%"
								layout="pageDirection">
								<f:selectItems value="#{mudancaCurricular.possiveisMatrizes}" />
								<a4j:support event="onclick" reRender="tipoMudanca" onsubmit="carregando('M');submit();" 
								  actionListener="#{mudancaCurricular.determinaTipoMudanca}" oncomplete="disable('M');" />
							</h:selectOneRadio>
						</c:if>
						<c:if test="${empty mudancaCurricular.possiveisMatrizes }">
							<i>Nenhuma matriz encontrada para o curso selecionado.</i>
						</c:if>
					</td>
				</tr>
				<c:if test="${mudancaCurricular.mudancaCurriculo}">
						<tr>
							<th width="15%" class="required">Nova Estrutura Curricular:</th>
							<td><h:selectOneMenu value="#{mudancaCurricular.curriculoNovo.id}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{mudancaCurricular.possiveisCurriculosByNovaMatriz }" />
							</h:selectOneMenu> </td>
						</tr>
				</c:if>
				<tr><td colspan="2"> </td></tr>
				<tr>
					<th width="15%" style="font-weight: bold;">Tipo de Mudança:</th>
					<td>
						<h:graphicImage value="/img/indicator.gif" id="indicatorTipo" style="display: none;" />
						<h:outputText value="#{mudancaCurricular.descricaoTipoMudanca}" id="tipoMudanca"/>
					</td>
				</tr>
				<tr>
					<th width="15%"></th>
					<td>
						<h:selectBooleanCheckbox id="notificar" value="#{mudancaCurricular.cadastrarObservacaoDiscente}">
							<a4j:support event="onclick" reRender="formulario" 
							onsubmit="$('formulario:indicatorObservacoes').style.display='inline';" 
							oncomplete="$('formulario:indicatorObservacoes').style.display='none';"/>
						</h:selectBooleanCheckbox>
						Registrar Observações no Histórico do Aluno
						<h:graphicImage value="/img/indicator.gif" id="indicatorObservacoes" style="display: none;" />
					</td>
				</tr>
				<h:panelGroup rendered="#{mudancaCurricular.cadastrarObservacaoDiscente }">
					<tr>
						<th width="15%" class="required">Observações: </th>
						<td>
							<h:inputTextarea rows="5" style="width: 98%;" value="#{mudancaCurricular.observacaoDiscente.observacao }">
							</h:inputTextarea>
						</td>
					</tr>
				</h:panelGroup>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{mudancaCurricular.simulacao? 'Simular Mudança' : 'Registrar Mudança'}"  id="confirmar" action="#{mudancaCurricular.registrarMudanca}" />
						<h:commandButton value="<< Selecionar Outro Discente" id="voltar" action="#{mudancaCurricular.iniciar}"  />
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{mudancaCurricular.cancelar}" immediate="true" />
					</td>
				</tr>
			</tfoot>
	</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
