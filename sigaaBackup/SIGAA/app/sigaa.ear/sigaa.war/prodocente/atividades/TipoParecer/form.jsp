<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Parecer</h2>
	<h:form>
		 <div class="infoAltRem" style="width: 100%">
	     <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
  		 <h:commandLink action="#{tipoParecer.listar}"
			value="Listar Tipos de Parecer Cadastrados"/>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="50%">
			<caption class="listagem">Cadastro de Tipo de Parecer</caption>
			<h:inputHidden value="#{tipoParecer.confirmButton}" />
			<h:inputHidden value="#{tipoParecer.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{tipoParecer.obj.descricao}" size="70"
					maxlength="200" readonly="#{tipoParecer.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoParecer.confirmButton}"
						action="#{tipoParecer.cadastrar}"/> <h:commandButton
						value="Cancelar" action="#{tipoParecer.cancelar}"  
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
