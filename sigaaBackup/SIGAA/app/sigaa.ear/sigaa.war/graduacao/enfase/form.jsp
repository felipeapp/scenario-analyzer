<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> &gt; Cadastro de matriz com �nfase, reaproveitando dados</h2>

	<div class="descricaoOperacao">
		<p>A opera��o respons�vel pelo cadastro de Matriz Curricular com �nfase, reaproveitando os dados de matrizes 
		existentes consiste em realizar uma c�pia de matriz curricular alterando apenas a �nfase, 
		no qual as op��es de �nfase s�o aquelas relacionadas ao curso da <b>matriz de refer�ncia</b>, 
		onde todas as demais informa��es da matriz de origem ser�o considerados para a nova Matriz.
		Al�m de criar uma nova matriz uma estrutura curricular � cadastrada e relacionada
		a nova matriz curricular, para isso o sistema realizar� uma c�pia da estrutura curricular
		de acordo com o curr�culo selecionado em <b>Curr�culo de Refer�ncia para C�pia</b>.</p>
	</div>
	<div class="infoAltRem" style="width: 100%">
		<img src="/sigaa/img/adicionar.gif" style="overflow: visible;" />: Cadastrar Nova �nfase
 	</div>
	
	<h:form id="form">
		<a4j:keepAlive beanName="enfase"></a4j:keepAlive>
		<table class="formulario">
			<caption>Cadastro de �nfases</caption>
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
				<th width="30%" class="required">Matriz Curricular de Refer�ncia para C�pia:</th>
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
				<th width="25%" class="required">Curr�culo de Refer�ncia para C�pia:</th>
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
				<th class="required" width="25%">Novo C�digo do Curr�culo:</th>
				<td>
					<h:inputText value="#{enfase.novoCodigo}" id="novoCodigo" size="6" maxlength="6"/>
				</td>
			</tr>
			<tr>
				<th class="required">�nfase:</th>
				<td colspan="3">
					<h:selectOneMenu value="#{enfase.matrizCurricular.enfase.id}" id="enfase" >
						<f:selectItems value="#{enfase.enfases}" />
					</h:selectOneMenu>
					<h:commandLink action="#{enfase.preCadastrarSimples}" id="cadastrarLink" title="Cadastrar Nova �nfase">
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
