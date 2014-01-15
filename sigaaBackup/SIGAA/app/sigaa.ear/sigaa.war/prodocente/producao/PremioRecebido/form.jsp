<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h:messages showDetail="true"/>
<h2>
<a href="${ctx}/prodocente/nova_producao.jsf">
	<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
</a>
Cadastro de Prêmio Recebido
</h2>


   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/PremioRecebido/lista.jsf" >Listar Prêmios Recebidos Cadastrados</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>


<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Prêmio Recebido</caption>
	 <h:inputHidden value="#{premioRecebido.confirmButton}" />
	 <h:inputHidden value="#{premioRecebido.validar}" />
	 <h:inputHidden value="#{premioRecebido.readOnly}" />
	 <h:inputHidden value="#{premioRecebido.obj.id}" />
	 <h:inputHidden value="#{premioRecebido.obj.validado}" />

	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

		<tr>
			<th class="required">Prêmio:</th>
			<td><h:inputText value="#{premioRecebido.obj.premio}" size="60" id="premio"
				maxlength="255" readonly="#{premioRecebido.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Instituição:</th>
				<td><h:selectOneMenu style="width: 340px;" value="#{premioRecebido.obj.instituicao.idObject}"
				disabled="#{premioRecebido.readOnly}"
				disabledClass="#{premioRecebido.disableClass}" id="instituicao" >
				<%--<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />--%>
				<f:selectItems value="#{instituicoesEnsino.allCombo}" />
			</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Abrangência:</th>
				<td><h:selectOneMenu style="width: 340px;" value="#{premioRecebido.obj.tipoRegiao.idObject}" id="abrangencia"
				disabled="#{premioRecebido.readOnly}"
				disabledClass="#{premioRecebido.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{tipoRegiao.allCombo}" />
			</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<th class="required">Data de Produção:</th>
			<td>
				<t:inputCalendar value="#{premioRecebido.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{premioRecebido.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
			</td>
		</tr>

		<tr>
			<th class="required">Ano de Referência:</th>
			<td>
			<h:selectOneMenu id="anoReferencia" value="#{premioRecebido.obj.anoReferencia}">
				<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{premioRecebido.anosCadastrarAnoReferencia}" />
   			</h:selectOneMenu>

			</td>
		</tr>

		<tr>
			<th>Informações Complementares:</th>
			<td>
			<h:inputTextarea value="#{premioRecebido.obj.informacao}" cols="49" rows="3" />
			</td>
		</tr>


		<tr> <td>&nbsp;</td></tr>

         </table>
	  </td>
	 <!-- Fim Coluna 1 -->

	 <!-- Coluna 2 -->
	  <td width="30%">
	   <table id="coluna2">


		<tr><td colspan="2"><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /></td></tr>

       </table>
	  </td>
	 <!-- Fim Coluna 2  -->
	 </tr>


         <!-- Botões -->
	 <c:if test="${premioRecebido.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{premioRecebido.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan="2">
	   <h:commandButton value="Validar" action="#{premioRecebido.validar}" immediate="true" rendered="#{premioRecebido.validar}"/>
	   <h:commandButton value="#{premioRecebido.confirmButton}"
		action="#{premioRecebido.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{premioRecebido.cancelar}" onclick="#{confirm}"/></td>
	  </tr>
	 </tfoot>
	 <!-- Fim botões -->


	 </table>
	 </h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	 <br />
	 <center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	 <br />


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
