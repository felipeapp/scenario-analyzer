<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
	 <h:graphicImage title="Voltar para Tela de Novas Produ��es" value="/img/prodocente/voltarproducoes.gif"style="overflow: visible;"/>
	</a>
	Cadastro de Apresenta��o Em Evento
	</h2>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/ApresentacaoEmEvento/lista.jsf" >Listar Apresenta��es em Eventos Cadastradas</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>


<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Apresenta��o em Eventos</caption>
	 <h:inputHidden value="#{apresentacaoEmEvento.confirmButton}" />
	 <h:inputHidden value="#{apresentacaoEmEvento.obj.id}" />
	<h:inputHidden value="#{apresentacaoEmEvento.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

		<tr>
		 <th class="required">T�tulo:</th>
		 <td>
		  <h:inputText value="#{apresentacaoEmEvento.obj.titulo}" size="50" readonly="#{apresentacaoEmEvento.readOnly}" />
		 </td>
		</tr>
		<tr>
			<th class="required">Evento:</th>
			<td><h:inputText value="#{apresentacaoEmEvento.obj.evento}"
				size="50" maxlength="255"
				readonly="#{apresentacaoEmEvento.readOnly}" /></td>
		</tr>
		<tr>
				<th>Local:</th>
				<td><h:inputText value="#{apresentacaoEmEvento.obj.local}" size="50"
					maxlength="255" readonly="#{apresentacaoEmEvento.readOnly}" /></td>
		</tr>
		<tr>
			<th>Informa��es Complementares:</th>
			<td>
			<textarea value="#{apresentacaoEmEvento.obj.informacao}" cols="49" rows="3"></textarea>
			</td>
		</tr>

          </table>
	  </td>
	 <!-- Fim Coluna 1 -->

	 <!-- Coluna 2 -->
	  <td width="50%">
	   <table id="coluna2">

		<tr>
			<th class="required">Ano de Refer�ncia:</th>
			<td>
			<%--
			<t:inputCalendar onkeypress="return(formataData(this, event))" value="#{apresentacaoEmEvento.obj.data}"
				size="10" maxlength="10"
				readonly="#{apresentacaoEmEvento.readOnly}" renderAsPopup="true"
				renderPopupButtonAsImage="true" />
			--%>
			 <h:selectOneMenu id="anoReferencia" value="#{apresentacaoEmEvento.obj.anoReferencia}">
				<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   			</h:selectOneMenu>
			</td>
		</tr>
		<tr>
				<th class="required">Tipo de Evento:</th>

				<td><h:selectOneMenu style="width: 180px;"
					value="#{apresentacaoEmEvento.obj.tipoEvento.id}"
					disabled="#{apresentacaoEmEvento.readOnly}"
					disabledClass="#{apresentacaoEmEvento.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoEvento.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">�mbito:</th>

				<td><h:selectOneMenu style="width: 180px;"
					value="#{apresentacaoEmEvento.obj.tipoRegiao.id}"
					disabled="#{apresentacaoEmEvento.readOnly}"
					disabledClass="#{apresentacaoEmEvento.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">�rea:</th>

				<td><h:selectOneMenu style="width: 180px;" value="#{apresentacaoEmEvento.obj.area.id}"
					disabled="#{apresentacaoEmEvento.readOnly}"
					disabledClass="#{apresentacaoEmEvento.disableClass}" id="area" valueChangeListener="#{apresentacaoEmEvento.changeArea}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{area.allCombo}" />
					<a4j:support event="onchange" reRender="subarea" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-�rea:</th>

				<td><h:selectOneMenu style="width: 180px;" value="#{apresentacaoEmEvento.obj.subArea.id}"
					disabled="#{apresentacaoEmEvento.readOnly}"
					disabledClass="#{apresentacaoEmEvento.disableClass}" id="subarea">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{apresentacaoEmEvento.subArea}" />
				</h:selectOneMenu></td>
			</tr>


       </table>
	  </td>
	 <!-- Fim Coluna 2  -->
	 </tr>


         <!-- Botoes -->
	 <c:if test="${apresentacaoEmEvento.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{apresentacaoEmEvento.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inv�lido" />
			<f:selectItem itemValue="true" itemLabel="V�lido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{apresentacaoEmEvento.validar}" immediate="true" rendered="#{apresentacaoEmEvento.validar}"/>
	   <h:commandButton value="#{apresentacaoEmEvento.confirmButton}"
		action="#{apresentacaoEmEvento.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{apresentacaoEmEvento.cancelar}" /></td>
	  </tr>
	 </tfoot>
	 <!-- Fim botoes -->


	 </table>
	 </h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	 <br />
	 <center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	 </center>
	 <br />


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
