<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.exibe{ display: block; }
	.esconde{ display: none; }
</style>

<f:view>

	<h2 class="title"><ufrn:subSistema /> > Mudança de Matriz Curricular</h2>
	<h:form id="formulario">
		<table class=visualizacao width="95%">
			<caption>Dados da Matriz Curricular</caption>
			<tr>
				<th width="15%">Discente:</th>
				<td><h:outputText value="#{mudancaCurricular.obj.matriculaNome}" /></td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>
					<h:outputText value="#{mudancaCurricular.obj.curso.descricao}" />
				</td>
			</tr>
			<tr>
				<th>Matriz Curricular Atual:</th>
				<td><h:outputText value="#{mudancaCurricular.matrizAtual.descricao}" /></td>
			</tr>
			<tr>
				<th>Estrutura Curricular:</th>
				<td><h:outputText value="#{mudancaCurricular.curriculoAtual.descricao}" /></td>
			</tr>
		</table>
	
		<table class="formulario" width="99%">
			<tr><td colspan="2" class="subFormulario">Nova Matriz Curricular</td></tr>
			
			<c:if test="${acesso.administradorDAE}">
				<tr>
					<th>Curso:</th>
					<td>
						<h:selectOneMenu value="#{mudancaCurricular.curso.id}" id="novoCurso" 
								valueChangeListener="#{mudancaCurricular.carregarMatrizes}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
							<a4j:support event="onchange" reRender="novaMatrizCurricular, tipoMudanca"
								onsubmit="$('formulario:indicatorMatriz').style.display='inline'; 
										$('formulario:indicatorCurriculo').style.display='inline';
										$('formulario:indicatorTipo').style.display='inline';
										$('formulario:novaMatrizCurricular').style.display='none';
										$('formulario:novoCurriculo').style.display='none';
										$('formulario:tipoMudanca').style.display='none';" 
								oncomplete="$('formulario:indicatorMatriz').style.display='none'; 
										$('formulario:indicatorCurriculo').style.display='none';
										$('formulario:indicatorTipo').style.display='none';
										$('formulario:novaMatrizCurricular').style.display='inline';
										$('formulario:novoCurriculo').style.display='inline';
										$('formulario:tipoMudanca').style.display='inline';"/>
						</h:selectOneMenu>
						<h:graphicImage value="/img/indicator.gif" id="indicatorCurso" style="margin-left:5px; display: none;" />
					</td>
				</tr>
			</c:if>
			
				<tr>
					<th>Matriz Curricular:</th>
					<td>
						<h:graphicImage value="/img/indicator.gif" id="indicatorMatriz" style="display: none;" />
						<h:selectOneMenu value="#{mudancaCurricular.matrizNova.id}" id="novaMatrizCurricular" 
								valueChangeListener="#{ mudancaCurricular.carregarDadosCurriculo }" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{mudancaCurricular.possiveisMatrizes}" />
							<a4j:support event="onchange" reRender="label_curriculo, novoCurriculo, tipoMudanca" 
								onsubmit="$('formulario:indicatorCurriculo').style.display='inline';
										$('formulario:indicatorTipo').style.display='inline';
										$('formulario:novoCurriculo').style.display='none';
										$('formulario:tipoMudanca').style.display='none';"
								oncomplete="$('formulario:indicatorCurriculo').style.display='none';
										$('formulario:indicatorTipo').style.display='none';
										$('formulario:novoCurriculo').style.display='inline';
										$('formulario:tipoMudanca').style.display='inline';"/>
						</h:selectOneMenu>
					</td>
				</tr>

				<tr>
					<th> 
					 	<h:outputLabel value="Estrutura Curricular:" id="label_curriculo" styleClass="#{mudancaCurricular.mudancaCurriculo ? 'required' : ''}" />
					</th>
					<td>
						<h:graphicImage value="/img/indicator.gif" id="indicatorCurriculo" style="display: none;" />
						<h:selectOneMenu value="#{mudancaCurricular.curriculoNovo.id}" id="novoCurriculo" 
								valueChangeListener="#{mudancaCurricular.determinaTipoMudancaPorCurriculo}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{mudancaCurricular.curriculosDestino}" />
							<a4j:support event="onchange" reRender="tipoMudanca"
								onsubmit="$('formulario:indicatorTipo').style.display='inline';
										$('formulario:tipoMudanca').style.display='none';"
								oncomplete="$('formulario:indicatorTipo').style.display='none';
										$('formulario:tipoMudanca').style.display='inline';"/>	
						</h:selectOneMenu> 
					</td>
				</tr>
		
				<tr><td colspan="2"> </td></tr>
				<tr>
					<th style="font-weight: bold;">Tipo de Mudança:</th>
					<td>
						<h:graphicImage value="/img/indicator.gif" id="indicatorTipo" style="display: none;" />
						<h:outputText value="#{mudancaCurricular.descricaoTipoMudanca}" id="tipoMudanca"/>
					</td>
				</tr>
				<br/><br/>		
				
				<tr>
					<th width="15%"></th>
					<td>
						<h:selectBooleanCheckbox id="notificar" value="#{mudancaCurricular.cadastrarObservacaoDiscente}" onclick="selecionaObservacao(this);" >
							<a4j:support event="onclick" reRender="formulario" 
									onsubmit="$('formulario:indicatorObservacoes').style.display='inline';" 
									oncomplete="$('formulario:indicatorObservacoes').style.display='none';"/>
						</h:selectBooleanCheckbox>
						Registrar Observações no Histórico do Aluno
						<h:graphicImage value="/img/indicator.gif" id="indicatorObservacoes" style="display: none;" />
					</td>
				</tr>
				<tr>
					<th width="15%">
						<h:outputLabel id="panelObservacao" value="Observação:" styleClass="required"  /> 
					</th>
					<td>
						<h:inputTextarea id="observacao" value="#{mudancaCurricular.observacaoDiscente.observacao }" rows="5" style="width: 98%;"/>
					</td>
				</tr>
				
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Simular Mudança"  id="simularMudanca" action="#{mudancaCurricular.simularMudanca}" />
						<h:commandButton value="Registrar Mudança"  id="confirmar" action="#{mudancaCurricular.registrarMudanca}" />
						<h:commandButton value="<< Selecionar Outro Discente" id="voltar" action="#{mudancaCurricular.iniciar}"  />
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{mudancaCurricular.cancelar}" immediate="true" />
					</td>
				</tr>
			</tfoot>
	</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>	<br>
	</center>

</f:view>

<script>
	function selecionaObservacao(obs) {
		if (obs.checked) {
			$('formulario:panelObservacao').show();
			$('formulario:observacao').show();
			
		} else {
			$('formulario:panelObservacao').hide();
			$('formulario:observacao').hide();
		}
		$('formulario:notificar').focus();
	}
	
	selecionaObservacao($('formulario:notificar'));
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>