<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Orientação</h2>
	<h:form>
		 <div class="infoAltRem" style="width: 100%">
	     <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		 <h:commandLink action="#{tipoOrientacao.listar}"
			value="Listar Tipo de Orientação Cadastrados"/>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="70%">
			<caption class="listagem">Cadastro de Tipo de Orientação</caption>
			<h:inputHidden value="#{tipoOrientacao.confirmButton}" />
			<h:inputHidden value="#{tipoOrientacao.obj.id}" />
			<tr>
				<th  class="required">Descrição:</th>
				<td><h:inputText value="#{tipoOrientacao.obj.descricao}"
					size="60" maxlength="255" readonly="#{tipoOrientacao.readOnly}"
					id="descricao" /></td>
			</tr>
			<tr>
				<th  class="required">Nível Ensino:</th>
				<td><h:inputText value="#{tipoOrientacao.obj.nivelEnsino}"
					size="60" maxlength="1" readonly="#{tipoOrientacao.readOnly}"
					id="contexto" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoOrientacao.confirmButton}"
						action="#{tipoOrientacao.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{tipoOrientacao.cancelar}" 
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
