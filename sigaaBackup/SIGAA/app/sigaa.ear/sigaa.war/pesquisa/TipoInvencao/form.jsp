<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Tipo de Invenção</h2>

	<center>
		<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoInvencao.listar}"/>
			</div>
		</h:form>
	</center>

	<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Invenção</caption>
			<h:inputHidden value="#{tipoInvencao.confirmButton}" />
			<h:inputHidden value="#{tipoInvencao.obj.id}" />
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText size="30" maxlength="30"
					readonly="#{tipoInvencao.readOnly}"  value="#{tipoInvencao.obj.descricao}" /></td>
			</tr>
			<c:if test="${not tipoInvencao.obj.ativo}">
			<tr>
				<th>Ativo:</th>
				<td>
					<h:selectBooleanCheckbox value="#{tipoInvencao.obj.ativo}" disabled="#{tipoInvencao.readOnly}"/>
				</td>
			</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{tipoInvencao.confirmButton}"
						action="#{tipoInvencao.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoInvencao.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
