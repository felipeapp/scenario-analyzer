<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Categoria de Projeto de Pesquisa</h2>

	<center>
		<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar Categorias Cadastradas" action="#{categoriaProjetoPesquisaBean.listar}"/>
			</div>
		</h:form>
	</center>

	<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Categoria de Projeto de Pesquisa</caption>
			<h:inputHidden value="#{categoriaProjetoPesquisaBean.confirmButton}" />
			<h:inputHidden value="#{categoriaProjetoPesquisaBean.obj.id}" />
			<tr>
				<th class="obrigatorio">Denominação:</th>
				<td><h:inputText size="80" maxlength="80"
					readonly="#{categoriaProjetoPesquisaBean.readOnly}"  value="#{categoriaProjetoPesquisaBean.obj.denominacao}" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Ordem:</th>
				<td><h:inputText size="2" maxlength="2" onkeyup="formatarInteiro(this);"
					readonly="#{categoriaProjetoPesquisaBean.readOnly}"  value="#{categoriaProjetoPesquisaBean.obj.ordem}" /></td>
			</tr>
			<c:if test="${not categoriaProjetoPesquisaBean.obj.ativo and categoriaProjetoPesquisaBean.obj.id > 0}">
			<tr>
				<th>Ativo:</th>
				<td>
					<h:selectBooleanCheckbox value="#{categoriaProjetoPesquisaBean.obj.ativo}" disabled="#{categoriaProjetoPesquisaBean.readOnly}"/>
				</td>
			</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{categoriaProjetoPesquisaBean.confirmButton}"
						action="#{categoriaProjetoPesquisaBean.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{categoriaProjetoPesquisaBean.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
