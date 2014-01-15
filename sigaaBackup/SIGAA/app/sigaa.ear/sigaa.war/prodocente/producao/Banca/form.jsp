<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<h2><a href="${ctx}/prodocente/nova_producao.jsf">
			 <h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" 
			 		style="overflow: visible;" /></a>
  		<c:if test="${banca.curso}"> Cadastro de Trabalhos de Conclusão </c:if>
   		<c:if test="${not banca.curso}"> Cadastro de Comissões Julgadoras </c:if>
	</h2>

    <h:form id="banca">
		<div class="infoAltRem" style="width: 100%">
			<h:graphicImage value="/img/listar.gif"style="overflow: visible;" />
	    	
			<h:commandLink action="#{ banca.listar }" >
				<f:param name="curso" value="#{banca.curso}"/>
           		<c:if test="${ banca.curso }"> Listar Trabalhos de Conclusão Cadastrados </c:if>
           		<c:if test="${ not banca.curso }"> Listar Comissões Julgadoras Cadastradas</c:if>
			</h:commandLink>
		</div>
    </h:form>

	<h:form id="form">
	<table class="formulario" width="100%">
	<caption class="listagem">
      <c:if test="${banca.curso}"> Cadastro de Trabalhos de Conclusão </c:if>
      <c:if test="${not banca.curso}"> Cadastro de Comissões Julgadoras </c:if>
	</caption>
	 <h:inputHidden value="#{banca.confirmButton}" id="hdnConfirmButton"/>
	 <h:inputHidden value="#{banca.obj.id}" id="idObj" />
	 <h:inputHidden value="#{banca.obj.validado}" id="validado" />
	 <h:inputHidden value="#{banca.obj.tipoBanca.id}" id="idTipoBanca" />
	 <h:inputHidden value="#{banca.obj.tipoBanca.descricao}" id="descTipoBanca" />

	<tr>
	 <td>
	  <table>
		<tr>
			<th class="required" width="10%">Título:</th>
			<td colspan="3">
				<h:inputText size="60" value="#{banca.obj.titulo}" readonly="#{banca.readOnly}" id="titulo" />
					<ufrn:help img="/img/ajuda.gif">Título do Trabalho ou Nome do Curso</ufrn:help>
			</td>
		</tr>
		<tr>
			<th class="required">Data da Banca:</th>
			<td colspan="3">
				<table width="100%">
					<tr>
					<td>
						<t:inputCalendar value="#{banca.obj.data}" title="Data da Banca"
					size="10" maxlength="10" readonly="#{banca.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataBanca">
					<f:converter converterId="convertData"/> </t:inputCalendar>
					</td>
					<th class="required" nowrap="nowrap" >Ano de Referência:</th>
					<td>
						 <h:selectOneMenu id="anoReferencia" value="#{banca.obj.anoReferencia}">
						  <f:selectItem itemValue="" itemLabel="--SELECIONE--" />
						  <f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   						 </h:selectOneMenu>
					</td>
					<th class="required" nowrap="nowrap">Data da Publicação:</th>
					<td>
						<t:inputCalendar value="#{banca.obj.dataProducao}" size="10" maxlength="10" readonly="#{banca.readOnly}" 
								popupDateFormat="dd/MM/yyyy" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" title="Data da Publicação">
							<f:converter converterId="convertData"/> 
						</t:inputCalendar>
					</td>								
					</tr>
				</table>
			 </td>
		</tr>
		<tr>
			<th valign="top">Examinado(s):</th>
			<td colspan="3">
				<h:inputTextarea value="#{banca.obj.autor}" id="autor" cols="85" rows="3"/>
			</td>
		</tr>
		<tr>
		<c:if test="${banca.curso }">
			<th class="required">Natureza do Exame:</th>
			<td><h:selectOneMenu  style="width: 180px" value="#{banca.obj.naturezaExame.id}" id="naturezaExame"
						disabled="#{banca.readOnly}" disabledClass="#{banca.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{naturezaExame.allCombo}" />
			</h:selectOneMenu></td>
		</c:if>
		<c:if test="${!banca.curso }">
			<th class="required">Categoria Funcional:</th>
			<td><h:selectOneMenu  style="width: 180px" value="#{banca.obj.categoriaFuncional.id}" id="categoriaFuncional"
						disabled="#{banca.readOnly}" disabledClass="#{banca.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{categoriaFuncional.allCombo}" />
			</h:selectOneMenu></td>
		</c:if>
			<th class="required">País:</th>
			<td><h:selectOneMenu value="#{banca.obj.pais.id}" id="pais"
						disabled="#{banca.readOnly}" disabledClass="#{banca.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{pais.allCombo}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Área:</th>
			<td><h:selectOneMenu style="width: 180px" value="#{banca.obj.area.id}" disabled="#{banca.readOnly}" id="area" 
						disabledClass="#{banca.disableClass}" valueChangeListener="#{banca.changeArea}" onchange="submit();">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{area.allCombo}" />
			</h:selectOneMenu></td>
			<th class="required">Instituição:</th>
			<td>
				<h:inputHidden id="instituicaoId" value="#{banca.obj.instituicao.id}" />
				<h:inputText id="instituicaoNome" value="#{banca.obj.instituicao.nome}" size="43" />
				<ajax:autocomplete source="form:instituicaoNome" target="form:instituicaoId" baseUrl="/sigaa/ajaxInstituicao" 
						className="autocomplete" indicator="indicator" minimumCharacters="3" parser="new ResponseXmlToHtmlListParser()" />
				<span id="indicator" style="display:none;">
					<img src="/sigaa/img/indicator.gif" />
				</span>
			</td>
		</tr>

		<tr>
			<th class="required">Sub-Área:</th>
			<td><h:selectOneMenu style="width: 180px" value="#{banca.obj.subArea.id}" id="subarea" disabled="#{banca.readOnly}"
						disabledClass="#{banca.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{banca.subArea}" />
				</h:selectOneMenu></td>
			<th class="required">Município:</th>
			<td>
				<h:inputHidden id="municipioId" value="#{banca.obj.municipio.id}" />
				<h:inputText id="municipioNome" value="#{banca.obj.municipio.nome}" size="43" />
				<ajax:autocomplete source="form:municipioNome" target="form:municipioId"
					baseUrl="/sigaa/ajaxMunicipios" className="autocomplete"
					indicator="indicatorMunicipio" minimumCharacters="3" parameters="buscaTipo=findByNome"
					parser="new ResponseXmlToHtmlListParser()" />

				<span id="indicatorMunicipio" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
			</td>
		</tr>

		<tr>
			<th class="required">Departamento:</th>
			<td colspan="3"><h:selectOneMenu value="#{banca.obj.departamento.id}" id="departamento"
				disabled="#{banca.readOnly}" disabledClass="#{banca.disableClass}">
				<f:selectItems value="#{unidade.allDepartamentoUnidAcademicaCombo}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>

		</tr>
		<tr>
			<th valign="top">Informações complementares:</th>
			<td colspan="3">
				<h:inputTextarea value="#{banca.obj.informacao}" id="informacao" cols="85" rows="2" />
			</td>
		</tr>
	</table>
	</td>

         <!-- Botoes -->
	<c:if test="${banca.validar}">
		<tr>
			<th>Validar:</th>
	  		<td colspan="3">
				<h:selectOneRadio rendered="#{bean.validar}" value="#{banca.obj.validado}" id="rdbtnValidado">
					<f:selectItem itemValue="false" itemLabel="Inválido" />
					<f:selectItem itemValue="true" itemLabel="Válido" />
		    	</h:selectOneRadio>
			</td>
	 	</tr>
	</c:if>
	<tfoot>
		<tr>
	    	<td colspan="4">
	   			<h:commandButton value="Validar" action="#{banca.validar}" immediate="true" rendered="#{banca.validar}" id="btnValidar" />
	   			<h:commandButton value="#{banca.confirmButton}"	action="#{banca.cadastrar}" id="btnCadastrar" /> 
	   			<h:commandButton value="Cancelar" immediate="true" action="#{banca.cancelar}" onclick="#{confirm}" id="btnCancelar" />
	   		</td>
		</tr>
	</tfoot>
	<!-- Fim botoes -->

	 </table>
	 </h:form>
	 <br />
	 <center>
	 	<h:graphicImage url="/img/required.gif" style="vertical-align: top;" /> 
	 		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>