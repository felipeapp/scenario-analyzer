<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>
	<ufrn:subSistema /> > Mini-Curso
	</h2>


	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	 <h:commandLink action="#{miniCurso.listar}" value="Listar Mini Cursos Cadastrados"/>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="100%">
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
				<th class="required">Data de Início:</th>
				<td><t:inputCalendar id="dataInicio"
					value="#{miniCurso.obj.periodoInicio}" size="10" maxlength="10"
					readonly="#{miniCurso.readOnly}" renderAsPopup="true"
					renderPopupButtonAsImage="true" onkeypress="return(formataData(this, event))" />
				</td>
			</tr>
			<tr>
			<th>Data de Fim:</th>
			<td>
					 <t:inputCalendar id="dataFim"
					value="#{miniCurso.obj.dataFim}" size="10" maxlength="10"
					readonly="#{miniCurso.readOnly}" renderAsPopup="true"
					renderPopupButtonAsImage="true" onkeypress="return(formataData(this, event))"/>
			</td>
			</tr>

			<tr>
				<th class="required">Carga Horária Ministrada/Tipo de Carga Horária:</th>
				<td><h:inputText value="#{miniCurso.obj.cargaHoraria}"
					size="6" maxlength="255" readonly="#{miniCurso.readOnly}"
					id="cargaHoraria" />
					<h:selectOneMenu value="#{miniCurso.obj.tipoChHoraria}"
					disabled="#{miniCurso.readOnly}" style="width: 90px"
					disabledClass="#{miniCurso.disableClass}" id="chHoraria" >
					<f:selectItems value="#{miniCurso.tipoCH}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Número de Outros Docentes no Curso:</th>
				<td><h:inputText value="#{miniCurso.obj.numeroDocentes}"
					size="6" maxlength="255" readonly="#{miniCurso.readOnly}"
					id="numeroDocentes" /></td>

			</tr>

			<%--
			<tr>
				<th class="required">Departamento:</th>
				<td><h:inputHidden value="#{miniCurso.habilitaDepartamento}" id="habilitaDepartamento" />
				<h:selectOneMenu value="#{miniCurso.obj.departamento.id}"
					disabled="#{miniCurso.readOnly}" style="width: 375px"
					disabledClass="#{miniCurso.disableClass}" id="departamento" >
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
				</h:selectOneMenu>
				</td>
			</tr>
			 --%>

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
