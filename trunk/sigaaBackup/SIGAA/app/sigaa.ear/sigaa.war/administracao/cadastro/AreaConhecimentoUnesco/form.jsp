<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Area de Conhecimento da Unesco</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{areaConhecimentoUnesco.listar}"/>
			</div>
			</h:form>
	</center>


	<div id="panel">
	<div id="cadastrar">

	<table class="formulario" width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Area de Conhecimento da Unesco</caption>
			<h:inputHidden value="#{areaConhecimentoUnesco.confirmButton}" />
			<h:inputHidden value="#{areaConhecimentoUnesco.obj.id}" />
			<tr>
				<th>Código:</th>
				<td><h:inputText size="8" maxlength="20"
					value="#{areaConhecimentoUnesco.obj.codigo}"
					readonly="#{areaConhecimentoUnesco.readOnly}" /></td>
			</tr>
			<tr>
				<th>Descricão:</th>
				<td><h:inputText size="60" maxlength="255"
					value="#{areaConhecimentoUnesco.obj.descricao}"
					readonly="#{areaConhecimentoUnesco.readOnly}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{areaConhecimentoUnesco.confirmButton}"
						action="#{areaConhecimentoUnesco.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{areaConhecimentoUnesco.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>

	</div>

	<div id="listar"></div>
	</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>