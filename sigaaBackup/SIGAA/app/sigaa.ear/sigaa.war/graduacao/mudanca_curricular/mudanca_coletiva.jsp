<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{mudancaCurricular.create}" />
	<h2><ufrn:subSistema /> &gt; Mudan�a Coletiva de Curr�culo</h2>
	<h:messages showDetail="true"></h:messages>
	
	<div class="descricaoOperacao">
		<p>Esta opera��o efetua a mudan�a de curr�culo de <strong>todos</strong> os alunos segundo os crit�rios especificados.</p>
		<p>Os alunos <strong>Ativos</strong> e <strong>Trancados</strong> ser�o afetados pela opera��o.</p>
		<p>Caso seja especificado o ano-per�odo de ingresso, apenas os alunos ativos pertencentes �quele ano-per�odo de ingresso ser�o afetados.</p>
	</div>
	
	<h:form id="formulario">
	<table class="formulario" width="100%">
			<caption class="listagem">
			Dados da Mudan�a de Curr�culo
			</caption>
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td>
					<a4j:region>
					<h:selectOneMenu id="cursos" value="#{mudancaCurricular.curso.id}" 
						valueChangeListener="#{mudancaCurricular.carregarMatrizes}" style="width: 80%">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
						<a4j:support event="onchange" reRender="matrizes" />
					</h:selectOneMenu>&nbsp;
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/ajax-loader.gif"/>
						</f:facet>
					</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Matriz Curricular:</th>
				<td>
					<a4j:region>
					<h:selectOneMenu id="matrizes" value="#{mudancaCurricular.matrizAtual.id}" valueChangeListener="#{mudancaCurricular.carregarCurriculosOrigem}">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{mudancaCurricular.matrizes}" />
						<a4j:support event="onchange" reRender="curriculosOrigem"></a4j:support>
					</h:selectOneMenu>&nbsp;
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/ajax-loader.gif"/>
						</f:facet>
					</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" width="25%">Curr�culo de origem:</th>
				<td>
					<a4j:region>
					<h:selectOneMenu value="#{mudancaCurricular.curriculoAtual.id}" id="curriculosOrigem" valueChangeListener="#{mudancaCurricular.carregarCurriculosDestino}">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{mudancaCurricular.curriculosOrigem}" />
						<a4j:support event="onchange" reRender="curriculosDestino"></a4j:support>
					</h:selectOneMenu>&nbsp;
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/ajax-loader.gif"/>
						</f:facet>
					</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Curr�culo de destino:</th>
				<td><h:selectOneMenu value="#{mudancaCurricular.curriculoNovo.id}" id="curriculosDestino">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{mudancaCurricular.curriculosDestino}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>Ano-per�odo de ingresso:</th>
				<td>
					<h:inputText id="anoIngresso" value="#{mudancaCurricular.anoIngresso}" 
					size="4" maxlength="4" onkeyup="formatarInteiro(this)" />-<h:inputText id="periodoIngresso" 
					value="#{mudancaCurricular.periodoIngresso}" size="1" maxlength="1"	onkeyup="formatarInteiro(this)" />
					<ufrn:help>Se for informado um valor, apenas alunos ativos do ano-per�odo especificado ser�o mudados de curr�culo.</ufrn:help>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4"><h:commandButton value="Mudar Alunos de Curr�culo"  id="confirmar"
						action="#{mudancaCurricular.registrarMudancaColetiva}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar"
						action="#{mudancaCurricular.cancelar}" immediate="true" /></td>
				</tr>
			</tfoot>
	</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
