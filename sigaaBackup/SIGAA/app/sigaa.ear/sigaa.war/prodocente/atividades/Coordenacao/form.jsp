<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Coordenação de Curso</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{coordenacao.listar}" value="Listar Coordenações de Cursos Cadastradas"/>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Coordenação de Curso</caption>
			<h:inputHidden value="#{coordenacao.confirmButton}" />
			<h:inputHidden value="#{coordenacao.obj.id}" />
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{coordenacao.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{coordenacao.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th>Remunerado:</th>
				<td>
					<h:selectBooleanCheckbox id="pago" value="#{coordenacao.obj.pago }"/>
				</td>
			</tr>
			<tr>
				<th class="required">Nome do Curso:</th>
				<td><h:inputText value="#{coordenacao.obj.nome}"
					size="60" maxlength="255" readonly="#{coordenacao.readOnly}"
					id="nomeCurso" /></td>
			</tr>
			<tr>
				<th class="required">Período Início:</th>
				<td><t:inputCalendar
							value="#{coordenacao.obj.periodoInicio}" size="10" onkeypress="return(formataData(this, event))"
							maxlength="10" readonly="#{coordenacao.readOnly}" id="periodoInicio"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar
							value="#{coordenacao.obj.periodoFim}" size="10" onkeypress="return(formataData(this, event))"
							maxlength="10" readonly="#{coordenacao.readOnly}" id="periodoFim"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{coordenacao.confirmButton}"	action="#{coordenacao.cadastrarCurso}" />
						<h:commandButton value="Cancelar" action="#{coordenacao.cancelar}" id="cancelar"  
							onclick="return confirm('Deseja realmente cancelar esta operação?');"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
