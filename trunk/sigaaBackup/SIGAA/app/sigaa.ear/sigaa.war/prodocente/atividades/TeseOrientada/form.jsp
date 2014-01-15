<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<%@include file="/stricto/menu_coordenador.jsp" %>

	<h:outputText value="#{teseOrientada.create}"/>

	<c:if test="${teseOrientada.residencia}">
		<h2><ufrn:subSistema /> > Monografia Residência Médica</h2>
	</c:if>
	<c:if test="${teseOrientada.stricto}">
		<h2><ufrn:subSistema /> > Orientação Pós-Graduação</h2>
	</c:if>
	<h:form id="form">
		<div style="text-align:right">
		<c:if test="${teseOrientada.stricto}">
				 <div class="infoAltRem" style="width: 100%">
				  <h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
				  <h:commandLink action="#{teseOrientada.listarStricto}" value="Listar Orientações Cadastradas"/>
				 </div>
		</c:if>
		<c:if test="${teseOrientada.residencia}">
			<div class="infoAltRem" style="width: 100%">
			  <h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
			  <h:commandLink action="#{teseOrientada.listarResidencia}"
				value="Listar Monografias Cadastradas"/>
			</div>
		</c:if>
		</div>
		<table class=formulario width="95%">
			<caption class="listagem">${teseOrientada.confirmButton } Orientação <c:if test="${teseOrientada.residencia }"> - Residência Médica</c:if> </caption>
			<h:inputHidden value="#{teseOrientada.confirmButton}" />
			<h:inputHidden value="#{teseOrientada.obj.id}" />
			<tr>
				<th class="required" width="28%">Docente:</th>
				<td>
					<ufrn:subSistema teste="portalDocente">
						${usuario.servidor.nome} 
					</ufrn:subSistema>
				
					<ufrn:subSistema teste="not portalDocente">
							<h:inputHidden id="id" value="#{teseOrientada.obj.servidor.id}"/>
							<h:inputText id="nomeServidor" readonly="#{teseOrientada.readOnly}"
								value="#{teseOrientada.obj.servidor.pessoa.nome}" size="60"/> 
							<ajax:autocomplete
								source="form:nomeServidor" target="form:id" 
								baseUrl="/sigaa/ajaxDocente" className="autocomplete"
								indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,inativos=true"
								parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicator" style="display:none; ">
								<img src="/sigaa/img/indicator.gif" />
							</span>
					</ufrn:subSistema>
				</td>
			</tr>
			<tr>
				<th class="required">Orientação/Co-Orientação:</th>
				<td><h:selectOneMenu value="#{teseOrientada.obj.orientacao}"
					style="width: 40%" disabled="#{teseOrientada.readOnly}"
					disabledClass="#{teseOrientada.disableClass}" id="orientacao">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{teseOrientada.tipoOrientacaoDocente}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Tipo de Orientação:</th>
				<td><h:selectOneMenu value="#{teseOrientada.obj.tipoOrientacao.id}"
					disabled="#{teseOrientada.readOnly}" style="width: 40%"
					disabledClass="#{teseOrientada.disableClass}" id="tipoOrientacao"
					valueChangeListener="#{teseOrientada.tipoOrientacaoListener}"
					onchange="submit();" >
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{teseOrientada.tipoOrientacao}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td>
					<h:inputText value="#{teseOrientada.obj.titulo}" style="width: 95%;"
						maxlength="255" readonly="#{teseOrientada.readOnly}" id="titulo" />
				</td>
			</tr>
			<%-- Discente Externo --%>
			<tr>
			<c:if test="${(not teseOrientada.atualizar) or (not teseOrientada.discenteMigrado) }">
				<th>Orientando Externo:</th>
				<td>
					<h:selectBooleanCheckbox
						value="#{teseOrientada.obj.discenteExterno}" id="discenteExterno"
						readonly="#{teseOrientada.readOnly}" onchange="submit()" />
					<ufrn:help img="/img/prodocente/help.png">Discente cuja Pós-graduação não foi cursada na ${ configSistema['siglaInstituicao'] }</ufrn:help>						
				</td>
			</c:if>
			</tr>
			<tr>
				<th class="required">Orientando:</th>
				<td>
					<%-- Se NÃO for discente externo --%>
					<c:if test="${not teseOrientada.obj.discenteExterno }">
						<c:if test="${(not teseOrientada.atualizar) or (not teseOrientada.discenteMigrado) }">
							<h:inputHidden id="idDiscente" value="#{ teseOrientada.obj.orientandoDiscente.id }"/>
			 				<h:inputText id="nomeDiscente"
								value="#{teseOrientada.obj.orientandoDiscente.pessoa.nome}" style="width: 95%;"/>
							<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
							baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
							indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=${teseOrientada.nivelEnsinoAutoComplete},status=todos"
							parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</c:if>
					</c:if>
					<%-- Discente Externo --%>
					<c:if test="${teseOrientada.obj.discenteExterno}">
						<h:inputText id="orientando" maxlength="100"
						value="#{teseOrientada.obj.orientando}" style="width: 95%;" readonly="#{teseOrientada.readOnly}" />
					</c:if>
					<%--Discente Migrado 
					<c:if test="${not empty teseOrientada.discenteMigrado and empty teseOrientada.discenteMigrado}">
						<h:inputText id="migrado" maxlength="100"
						value="#{teseOrientada.obj.orientando}" style="width: 95%;" readonly="#{teseOrientada.readOnly}" />
					</c:if>--%>
				</td>
			</tr>
			<tr>
				<th class="required">Instituição de Ensino:</th>
				<td><h:selectOneMenu value="#{teseOrientada.obj.instituicaoEnsino.id}"
					disabled="#{teseOrientada.readOnly}"
					disabledClass="#{teseOrientada.disableClass}" id="instituicaoEnsino">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- " />
					<f:selectItems value="#{instituicoesEnsino.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Agência Financiadora:</th>

				<td><h:selectOneMenu value="#{teseOrientada.obj.entidadeFinanciadora.id}"
					disabled="#{teseOrientada.readOnly}" disabledClass="#{teseOrientada.disableClass}" style="width: 70%"
					id="entidadeFinanciadora">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- " />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
				<tr>
				<th class="required">Área:</th>
				<td>
					<a4j:region>
						<h:selectOneMenu value="#{teseOrientada.obj.area.id}"
							disabled="#{teseOrientada.readOnly}" disabledClass="#{teseOrientada.disableClass}"
							id="area" valueChangeListener="#{teseOrientada.changeArea}" style="width: 70%"
							immediate="true">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{area.allCombo}" />
							<a4j:support event="onchange" reRender="subArea"/>
						</h:selectOneMenu>
			            <a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{teseOrientada.obj.subArea.id}" style="width: 70%"
					disabled="#{teseOrientada.readOnly}" disabledClass="#{teseOrientada.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{teseOrientada.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Período da Orientação:</th>
				<td><t:inputCalendar value="#{teseOrientada.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{teseOrientada.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoInicio">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				a
				<t:inputCalendar value="#{teseOrientada.obj.periodoFim}"
					size="10" maxlength="10" readonly="#{teseOrientada.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoFim">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th>Número de Páginas:</th>
				<td><h:inputText value="#{teseOrientada.obj.paginas}" size="10"
					maxlength="10" readonly="#{teseOrientada.readOnly}" 
					id="paginas" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<th class="required">Programa de Pós:</th>
				<td><h:selectOneMenu value="#{teseOrientada.obj.programaPos.id}"
					style="width: 95%" disabled="#{teseOrientada.readOnly}"
					disabledClass="#{teseOrientada.disableClass}" id="programaPos">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{teseOrientada.possiveisProgramasPos}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>
					<ufrn:help img="/img/prodocente/help.png">
						Informe o nome do programa caso não esteja listado acima
					</ufrn:help>				
					Outro Programa:
				</th>
				<td>
					<h:inputText value="#{teseOrientada.obj.programa}" style="width: 95%"
					maxlength="255" readonly="#{teseOrientada.readOnly}" id="programa" />
				</td>
			</tr>

			<tr>
				<th>Data de Defesa:</th>
				<td>
				<t:inputCalendar value="#{teseOrientada.obj.dataPublicacao}"
					size="10" maxlength="10" readonly="#{teseOrientada.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataPublicacao">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th></th>
				<td>
					<h:selectBooleanCheckbox value="#{teseOrientada.obj.desligado}" id="desligado" readonly="#{teseOrientada.readOnly}" />
					<h:outputLabel for="desligado">O discente não concluiu o trabalho e foi desligado do programa</h:outputLabel>
				</td>
			</tr>
			<tr>
				<th>Informações complementares:</th>
				<td><h:inputText value="#{teseOrientada.obj.informacao}" style="width: 95%"
					maxlength="255" readonly="#{teseOrientada.readOnly}" id="informacao" /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{teseOrientada.confirmButton}"	action="#{teseOrientada.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{teseOrientada.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">Campos de preenchimento obrigatório. </span>
		</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
