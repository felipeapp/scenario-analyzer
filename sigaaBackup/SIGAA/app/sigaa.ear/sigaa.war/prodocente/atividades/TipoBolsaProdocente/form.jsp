<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Bolsa</h2>
	<h:form>
	   <div class="infoAltRem" style="width: 100%">
		<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		<h:commandLink action="#{tipoBolsa.listar}"
			value="Listar Tipos de Bolsas Cadastradas"/>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="50%">
			<caption class="listagem">Cadastro de Tipo de Bolsa</caption>
			<h:inputHidden value="#{tipoBolsa.confirmButton}" />
			<h:inputHidden value="#{tipoBolsa.obj.id}" />
			<h:inputHidden value="#{tipoBolsa.obj.ativo}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{tipoBolsa.obj.descricao}" size="70"
					maxlength="200" readonly="#{tipoBolsaProdocente.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoBolsa.confirmButton}" action="#{tipoBolsa.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{tipoBolsa.cancelar}" 
					onclick="#{confirm}" immediate="true"/></td>
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
