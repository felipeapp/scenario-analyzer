<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> &gt; Cadastro de matriz com ênfase, reaproveitando dados</h2>

	<div class="descricaoOperacao">
		<p>A operação responsável pelo cadastro de Matriz Curricular com Ênfase, reaproveitando os dados de matrizes 
		existentes consiste em realizar uma cópia de matriz curricular alterando apenas a ênfase, 
		no qual as opções de ênfase são aquelas relacionadas ao curso da <b>matriz de referência</b>, 
		onde todas as demais informações da matriz de origem serão considerados para a nova Matriz.
		Além de criar uma nova matriz uma estrutura curricular é cadastrada e relacionada
		a nova matriz curricular, para isso o sistema realizará uma cópia da estrutura curricular
		de acordo com o currículo selecionado em <b>Currículo de Referência para Cópia</b>.</p>
	</div>
	<div class="infoAltRem" style="width: 100%">
		<img src="/sigaa/img/adicionar.gif" style="overflow: visible;" />: Cadastrar Nova Ênfase
 	</div>
	
	<h:form id="form">
		<a4j:keepAlive beanName="enfase"></a4j:keepAlive>
		<table class="formulario">
			<caption>Cadastro de Ênfases</caption>
			<tr>
				<th width="25%" class="required">Curso:</th>
				<td>
					<a4j:region>
						<h:selectOneMenu value="#{enfase.matrizCurricular.curso.id}" id="curso" style="width: 90%"
						 valueChangeListener="#{enfase.carregaListaMatrizes}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
						<a4j:support event="onchange" reRender="matriz, curriculo, enfase" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th width="30%" class="required">Matriz Curricular de Referência para Cópia:</th>
				<td>
					<a4j:region>
						<h:selectOneMenu value="#{enfase.matrizCurricular.id}" id="matriz"	style="width: 90%"
							valueChangeListener="#{enfase.carregaListaCurriculos}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{enfase.possiveisMatrizes}" />
						<a4j:support event="onchange" reRender="curriculo, enfase" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th width="25%" class="required">Currículo de Referência para Cópia:</th>
				<td>
					<a4j:region>
						<h:selectOneMenu value="#{enfase.curriculo.id}" id="curriculo"	style="width: 90%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{enfase.possiveisCurriculos}" />
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="required" width="25%">Novo Código do Currículo:</th>
				<td>
					<h:inputText value="#{enfase.novoCodigo}" id="novoCodigo" size="6" maxlength="6"/>
				</td>
			</tr>
			<tr>
				<th class="required">Ênfase:</th>
				<td colspan="3">
					<h:selectOneMenu value="#{enfase.matrizCurricular.enfase.id}" id="enfase" >
						<f:selectItems value="#{enfase.enfases}" />
					</h:selectOneMenu>
					<h:commandLink action="#{enfase.preCadastrarSimples}" id="cadastrarLink" title="Cadastrar Nova Ênfase">
						<img src="/sigaa/img/adicionar.gif" style="overflow: visible;" />
					</h:commandLink>
				</td>
			</tr>
			<tr>
				<th>Ativa:</th>
				<td><h:selectBooleanCheckbox id="ativo" value="#{enfase.obj.ativo}"
					readonly="#{enfase.readOnly}" disabled="#{enfase.obj.id == 0}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{enfase.obj.id}" /> 
						<h:commandButton id="btnCadastrar" value="#{enfase.confirmButton}" action="#{enfase.cadastrar}" /> 
						<c:if test="${enfase.obj.id > 0}">
							<h:commandButton value="<< Voltar" action="#{enfase.listar}" id="btnVoltar" immediate="true" />
						</c:if> 
						<h:commandButton value="Cancelar" action="#{enfase.cancelar}" id="btnCancelar" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
