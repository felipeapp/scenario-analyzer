<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Mini Curso</h2>


	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/> 
	 <a href="${ctx}/prodocente/atividades/MiniCurso/lista.jsf" >Listar Mini Cursos Cadastrados</a>
	 </div>
    </h:form>
	<h:messages showDetail="true"></h:messages>	
	<table class=formulario width="100%">
		<h:form id="form">
			<caption class="listagem">Cadastro de Mini Curso</caption>
			<h:inputHidden value="#{miniCurso.confirmButton}" />
			<h:inputHidden value="#{miniCurso.obj.id}" />
			<tr>
				<th class="required">Título:</th>
				<td><h:inputText value="#{miniCurso.obj.titulo}" size="60"
					maxlength="255" readonly="#{miniCurso.readOnly}" id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Congresso/Evento:</th>
				<td><h:inputText value="#{miniCurso.obj.nomeCongresso}"
					size="60" maxlength="255" readonly="#{miniCurso.readOnly}"
					id="nomeCongresso" /></td>
			</tr>
			<tr>
				<th class="required">Docente:</th>

				<td><h:inputHidden id="id" value="#{miniCurso.obj.servidor.id}"></h:inputHidden>
				<h:inputText id="nomeServidor"
					value="#{miniCurso.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
					source="form:nomeServidor" target="form:id"
					baseUrl="/sigaa/ajaxDocente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
			</tr>
			<tr>
				<th class="required">Início</th>
				<td><t:inputCalendar id="dataInicio"
					value="#{miniCurso.obj.dataInicio}" size="10" maxlength="10"
					readonly="#{miniCurso.readOnly}" renderAsPopup="true"
					renderPopupButtonAsImage="true" />
				</td>
			</tr>
			<tr>
			<th>Fim</th>
			<td>		 
					 <t:inputCalendar id="dataFim"
					value="#{miniCurso.obj.dataFim}" size="10" maxlength="10"
					readonly="#{miniCurso.readOnly}" renderAsPopup="true"
					renderPopupButtonAsImage="true" />
			</td>
			</tr>

			<tr>
				<th class="required">Carga Horária:</th>
				<td><h:inputText value="#{miniCurso.obj.cargaHoraria}"
					size="6" maxlength="255" readonly="#{miniCurso.readOnly}"
					id="cargaHoraria" /></td>
			</tr>
			<tr>
				<th class="required">Tipo de Carga Horária:</th>
				<td><h:inputText value="#{miniCurso.obj.tipoChHoraria}"
					size="30" maxlength="255" readonly="#{miniCurso.readOnly}"
					id="tipoChHoraria" /></td>
			</tr>

			<tr>
				<th class="required">Numero de Docentes:</th>
				<td><h:inputText value="#{miniCurso.obj.numeroDocentes}"
					size="6" maxlength="255" readonly="#{miniCurso.readOnly}"
					id="numeroDocentes" /></td>
			</tr>

			<tr>
				<th class="required">Departamento:</th>
				<td><h:inputHidden value="#{miniCurso.habilitaDepartamento}" id="habilitaDepartamento" />
				<h:selectOneMenu value="#{miniCurso.obj.departamento.id}"
					disabled="#{miniCurso.readOnly}" style="width: 220px"
					disabledClass="#{miniCurso.disableClass}" id="departamento" >
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu> 
				</td>
			</tr>
			<tr>
			<th>Departamento Externo:</th>
			<td>
				<input type="checkbox" id="form:habilita"  onchange="desabilitarDepartamento()"/>
			 </td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{miniCurso.confirmButton}" action="#{miniCurso.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{miniCurso.cancelar}" /></td>
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
<script type="text/javascript">
<!--
	function desabilitarDepartamento(){
		if(document.getElementById('form:habilita').checked){
			document.getElementById('form:departamento').disabled = true;
			document.getElementById('form:habilitaDepartamento').value = false;
		}else{
			document.getElementById('form:departamento').disabled = false;
			document.getElementById('form:habilitaDepartamento').value = true;
		}
	}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
