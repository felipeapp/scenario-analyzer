<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<%@include file="/stricto/menu_coordenador.jsp" %>


	<h:outputText value="#{teseOrientada.create}"/>

	<c:if test="${teseOrientada.residencia}">
		<h2>Monografia Residência Médica</h2>
	</c:if>
	<c:if test="${teseOrientada.lato}">
		<h2>Monografia - Especialização</h2>
	</c:if>
	<c:if test="${teseOrientada.stricto}">
		<h2>Orientação Mestrado/Doutorado</h2>
	</c:if>
	<h:form>
		<div style="text-align:right">
		<c:if test="${teseOrientada.stricto}">
				 <div class="infoAltRem" style="width: 100%">
				  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
				  <h:commandLink action="#{teseOrientada.listarStricto}" value="Listar Orientações Cadastradas"/>
				 </div>
		</c:if>
		<c:if test="${teseOrientada.residencia}">

			<div class="infoAltRem" style="width: 100%">
			  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
			  <h:commandLink action="#{teseOrientada.listarResidencia}"
				value="Listar Monografias Cadastradas"/>

			 </div>

		</c:if>
		<c:if test="${teseOrientada.lato}">

			<div class="infoAltRem" style="width: 100%">
			  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
			  <h:commandLink action="#{teseOrientada.listarLato}"
				value="Listar Monografias Cadastradas"/>

			 </div>

		</c:if>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="95%">
			<caption class="listagem">Cadastro de Orientação <c:if test="${teseOrientada.residencia }">- Residência Médica</c:if> </caption>
			<h:inputHidden value="#{teseOrientada.confirmButton}" />
			<h:inputHidden value="#{teseOrientada.obj.id}" />

			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{teseOrientada.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{teseOrientada.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn,inativos=true"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" />
				</span></td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{teseOrientada.obj.titulo}" size="60"
					maxlength="255" readonly="#{teseOrientada.readOnly}" id="titulo" /></td>
			</tr>
			<%-- Discente Externo --%>
			<tr>
			<c:if test="${(not teseOrientada.atualizar) or (not teseOrientada.discenteMigrado) }">
				<th>Orientando Externo:<ufrn:help img="/img/prodocente/help.png">Discente cuja Pós-graduação não foi cursada na ${ configSistema['siglaInstituicao'] }</ufrn:help></th>
				<td>
				 <h:selectBooleanCheckbox
						value="#{teseOrientada.obj.discenteExterno}" id="discenteExterno"
						readonly="#{teseOrientada.readOnly}" onchange="submit()" />
				</td>
			</c:if>
			</tr>
			<tr>
				<th class="required">Orientando:</th>
				<td>
					<%-- Se NÃO for discente externo --%>
					<c:if test="${ not teseOrientada.obj.discenteExterno }">
						<c:if test="${(not teseOrientada.atualizar) or (not teseOrientada.discenteMigrado) }">
							<h:inputHidden id="idDiscente" value="#{ teseOrientada.obj.orientandoDiscente.id }"/>
			 				<h:inputText id="nomeDiscente"
							value="#{teseOrientada.obj.orientandoDiscente.pessoa.nome}" size="80" />
							<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
							baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
							indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=S,status=todos"
							parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
						</c:if>
					</c:if>
					<%-- Discente Externo --%>
					<c:if test="${teseOrientada.obj.discenteExterno}">
						<h:inputText id="orientando" maxlength="100"
						value="#{teseOrientada.obj.orientando}" size="60" readonly="#{teseOrientada.readOnly}" />
					</c:if>
					<%--Discente Migrado --%>
					<c:if test="${teseOrientada.discenteMigrado}">
						<h:inputText id="orientando" maxlength="100"
						value="#{teseOrientada.obj.orientando}" size="60" readonly="#{teseOrientada.readOnly}" />
					</c:if>


				</td>
			</tr>
			<tr>
				<th class="required">Tipo de Orientação:</th>

				<td><h:selectOneMenu value="#{teseOrientada.obj.tipoOrientacao.id}"
					disabled="#{teseOrientada.readOnly}"
					disabledClass="#{teseOrientada.disableClass}" id="tipoOrientacao">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{teseOrientada.tipoOrientacao}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Orientador/Co-Orientador:</th>

				<td><h:selectOneMenu value="#{teseOrientada.obj.orientacao}"
					style="width: 300px" disabled="#{teseOrientada.readOnly}"
					disabledClass="#{teseOrientada.disableClass}" id="orientacao">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{teseOrientada.tipoOrientacaoDocente}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Instituição de Ensino:</th>
				<td><h:selectOneMenu value="#{teseOrientada.obj.instituicaoEnsino.id}"
					disabled="#{teseOrientada.readOnly}"
					disabledClass="#{teseOrientada.disableClass}" id="instituicaoEnsino">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{instituicoesEnsino.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Agência Financiadora:</th>

				<td><h:selectOneMenu value="#{teseOrientada.obj.entidadeFinanciadora.id}"
					disabled="#{teseOrientada.readOnly}" disabledClass="#{teseOrientada.disableClass}" style="width: 300px"
					id="entidadeFinanciadora">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
				<tr>
				<th class="required">Área:</th>
				<td><h:selectOneMenu value="#{teseOrientada.obj.area.id}"
					disabled="#{teseOrientada.readOnly}" disabledClass="#{teseOrientada.disableClass}"
					id="area" valueChangeListener="#{teseOrientada.changeArea}" style="width: 300px"
					onchange="submit()" immediate="true">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{area.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{teseOrientada.obj.subArea.id}" style="width: 300px"
					disabled="#{teseOrientada.readOnly}" disabledClass="#{teseOrientada.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{teseOrientada.subArea}"/>
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar value="#{teseOrientada.obj.periodoInicio}"
					size="10" maxlength="10" readonly="#{teseOrientada.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="periodoInicio" />

					Período de Fim: <t:inputCalendar value="#{teseOrientada.obj.periodoFim}" size="10"
					maxlength="10" readonly="#{teseOrientada.readOnly}" renderAsPopup="true"
					renderPopupButtonAsImage="true" id="periodoFim" />

					</td>
			</tr>
			<tr>
				<th>Páginas:</th>
				<td><h:inputText value="#{teseOrientada.obj.paginas}" size="10"
					maxlength="10" readonly="#{teseOrientada.readOnly}" id="paginas" /></td>
			</tr>
			<tr>
				<th class="required">Programa de Pós:</th>
				<td><h:selectOneMenu value="#{teseOrientada.obj.programaPos.id}"
					style="width: 400px" disabled="#{teseOrientada.readOnly}"
					disabledClass="#{teseOrientada.disableClass}" id="programaPos">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{unidade.allProgramaPosCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>Outro Programa fora da ${ configSistema['siglaInstituicao'] }:
				<ufrn:help img="/img/prodocente/help.png">Informe o nome do programa caso não seja
				um programa da ${ configSistema['siglaInstituicao'] } </ufrn:help>
				</th>
				<td><h:inputText value="#{teseOrientada.obj.programa}" size="70"
					maxlength="255" readonly="#{teseOrientada.readOnly}" id="programa" /></td>
			</tr>

			<tr>
				<th>Data de Defesa:</th>
				<td><t:inputCalendar value="#{teseOrientada.obj.dataPublicacao}"
					size="10" maxlength="10" readonly="#{teseOrientada.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true"
					id="dataPublicacao" /></td>
			</tr>
			<tr>
				<th>Informações complementares:</th>
				<td><h:inputText value="#{teseOrientada.obj.informacao}" size="80"
					maxlength="255" readonly="#{teseOrientada.readOnly}" id="informacao" /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{teseOrientada.confirmButton}"
						action="#{teseOrientada.cadastrar}" /> <h:commandButton value="Cancelar"
						action="#{teseOrientada.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
