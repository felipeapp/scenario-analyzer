<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Estágio de Desenvolvimento de Invenção</h2>

	<center>
		<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{estagioInvencaoBean.listar}"/>
			</div>
		</h:form>
	</center>

	<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Estágio de Desenvolvimento de Invenção</caption>
			<h:inputHidden value="#{estagioInvencaoBean.confirmButton}" />
			<h:inputHidden value="#{estagioInvencaoBean.obj.id}" />
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText size="30" maxlength="30"
					readonly="#{estagioInvencaoBean.readOnly}"  value="#{estagioInvencaoBean.obj.descricao}" /></td>
			</tr>
			<c:if test="${not estagioInvencaoBean.obj.ativo}">
			<tr>
				<th>Ativo:</th>
				<td>
					<h:selectBooleanCheckbox value="#{estagioInvencaoBean.obj.ativo}" disabled="#{estagioInvencaoBean.readOnly}"/>
				</td>
			</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{estagioInvencaoBean.confirmButton}"
						action="#{estagioInvencaoBean.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{estagioInvencaoBean.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
