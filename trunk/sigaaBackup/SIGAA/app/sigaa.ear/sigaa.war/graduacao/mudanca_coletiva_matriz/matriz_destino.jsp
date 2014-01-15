<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	.exibe{ display: block; }
	.esconde{ display: none; }
</style>

<f:view>
	<h:messages showDetail="true"/>
	<h2> <ufrn:subSistema /> &gt; Mudança Coletiva de Matriz Curricular  &gt; Definir Matriz Curricular de Destino</h2>
	
	<div class="descricaoOperacao">
		<p>Nesta operação o usuário selecionará <b>matriz curricular e estrutura curricular</b> de destino, 
		para aplicar tais mudanças nos discentes selecionados e listados abaixo. 
		Assim como, poderá ser inserida uma observação no histórico dos discentes selecionados.</p>
	</div>
	
	<h:form id="formulario">
		<table class=formulario width="90%">
			<caption>Registrar Mudança Coletiva de Matriz Curricular</caption>
			<tr><td colspan="2" class="subFormulario">Dados da Matriz Curricular de Origem</td></tr>
			<tr>
				<th width="20%"><b>Curso:</b></th>
				<td><h:outputText value="#{mudancaColetivaMatrizCurricular.matrizAtual.curso.descricao}" /></td>
			</tr>
			<tr>
				<th><b>Matriz Curricular de Origem:</b></th>
				<td><h:outputText value="#{mudancaColetivaMatrizCurricular.matrizAtual.descricao}" /></td>
			</tr>
			<tr>
				<th><b>Estrutura Curricular:</b></th>
				<td>${mudancaColetivaMatrizCurricular.curriculoAtual.id ne 0 ? mudancaColetivaMatrizCurricular.curriculoAtual : 'Não Informado'}</td>
			</tr>
	
			<tr>
				<th><b>Ano de Ingresso:</b></th>
				<td>${mudancaColetivaMatrizCurricular.anoIngresso > 0 ? mudancaColetivaMatrizCurricular.anoIngresso : 'Não Informado'}</td>
			</tr>
	
			<tr><td colspan="2" class="subFormulario">Matriz Curricular de Destino</td></tr>
			
			<c:if test="${acesso.administradorDAE}">
				<tr>
					<th class="required">Novo Curso:</th>
					<td>
						<h:selectOneMenu value="#{mudancaColetivaMatrizCurricular.matrizNova.curso.id}" id="novoCurso" 
								valueChangeListener="#{mudancaColetivaMatrizCurricular.carregarMatrizesDestino}" disabled="true">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
							<a4j:support event="onchange" reRender="novaMatrizCurricular, novoCurriculo, tipoMudanca"
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
					<th class="required">Nova Matriz Curricular:</th>
					<td>
						<h:graphicImage value="/img/indicator.gif" id="indicatorMatriz" style="display: none;" />
						<h:selectOneMenu value="#{mudancaColetivaMatrizCurricular.matrizNova.id}" id="novaMatrizCurricular" 
								valueChangeListener="#{ mudancaColetivaMatrizCurricular.carregarCurriculosDestino }" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{mudancaColetivaMatrizCurricular.matrizes}" />
							<a4j:support event="onchange" reRender="novoCurriculo, tipoMudanca" 
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
					<th class="required">Nova Estrutura Curricular:</th>
					<td>
						<h:graphicImage value="/img/indicator.gif" id="indicatorCurriculo" style="display: none;" />
						<h:selectOneMenu value="#{mudancaColetivaMatrizCurricular.curriculoNovo.id}" id="novoCurriculo" 
								valueChangeListener="#{mudancaColetivaMatrizCurricular.determinaTipoMudancaPorCurriculo}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{mudancaColetivaMatrizCurricular.curriculosDestino}" />
							<a4j:support event="onchange" reRender="tipoMudanca"
								onsubmit="$('formulario:indicatorTipo').style.display='inline';
										$('formulario:tipoMudanca').style.display='none';"
								oncomplete="$('formulario:indicatorTipo').style.display='none';
										$('formulario:tipoMudanca').style.display='inline';"/>	
						</h:selectOneMenu> 
					</td>
				</tr>
		
				<tr>
					<th style="font-weight: bold;">Tipo de Mudança:</th>
					<td>
						<h:graphicImage value="/img/indicator.gif" id="indicatorTipo" style="display: none;" />
						<h:outputText value="#{mudancaColetivaMatrizCurricular.descricaoTipoMudanca}" id="tipoMudanca"/>
					</td>
				</tr>
				
				<tr>
					<th width="15%"></th>
					<td>
						<a4j:region>
						<h:selectBooleanCheckbox id="notificar" valueChangeListener="#{mudancaColetivaMatrizCurricular.carregarObservacaoDiscente}" onclick="selecionaObservacao(this);">
							<a4j:support event="onclick" reRender="formulario" 
									onsubmit="$('formulario:indicatorObservacoes').style.display='inline';" 
									oncomplete="$('formulario:indicatorObservacoes').style.display='none';"/>
						</h:selectBooleanCheckbox>
						Registrar Observações no Histórico do Aluno
						<h:graphicImage value="/img/indicator.gif" id="indicatorObservacoes" style="display: none;" />
						</a4j:region>
					</td>
				</tr>
				<tr>
					<th width="15%">
						<h:outputLabel id="panelObservacao" value="Observação:" styleClass="required"  /> 
					</th>
					<td>
						<h:inputTextarea id="observacao" value="#{mudancaColetivaMatrizCurricular.observacaoDiscente.observacao }" rows="5" style="width: 98%;"/>
					</td>
				</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Registrar Mudança"  id="confirmar" action="#{mudancaColetivaMatrizCurricular.cadastrar}" />
						<h:commandButton value="<< Voltar" id="voltar" action="#{mudancaColetivaMatrizCurricular.voltarAlunos}"  />
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{mudancaColetivaMatrizCurricular.cancelar}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
	<br/><br/>
	<table class="formulario" width="90%">
		<caption class="formulario">Discentes selecionados para a mudança de Matriz Curricular (${fn:length(mudancaColetivaMatrizCurricular.discentesSelecionados)})</caption>
		<thead>
			<tr>
				<th width="3%">Matrícula</th>
				<th width="20%">Discente</th>
				<th width="5%">Status</th>
				<th width="20%">Matriz</th>
				<th width="5%">Currículo</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{mudancaColetivaMatrizCurricular.discentesSelecionados}" var="discente" varStatus="status">	
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${discente.matricula}</td>	
					<td>${discente.nome}</td>
					<td>${discente.statusString}</td>
					<td>${discente.matrizCurricular.descricao}</td>
					<td>${discente.curriculo.descricao}</td>
				</tr>
			</c:forEach>	
		</tbody>
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

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>