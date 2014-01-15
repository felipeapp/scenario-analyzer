<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${sessionScope.acesso.administracao}">

</c:if>
	<h2><ufrn:subSistema /> > Tipo de Entrada</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoEntrada.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Entrada</caption>
			<h:inputHidden value="#{tipoEntrada.confirmButton}" />
			<h:inputHidden value="#{tipoEntrada.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoEntrada.readOnly}"  value="#{tipoEntrada.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{tipoEntrada.confirmButton}"
						action="#{tipoEntrada.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoEntrada.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	
	<br>
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>