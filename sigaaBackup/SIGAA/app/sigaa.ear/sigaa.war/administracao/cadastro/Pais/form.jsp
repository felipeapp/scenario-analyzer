<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > País</h2>

	<center>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{pais.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="60%">
		<h:form>
			<caption class="listagem">Cadastro de País</caption>
			<h:inputHidden value="#{pais.confirmButton}" />
			<h:inputHidden value="#{pais.obj.id}" />
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{pais.readOnly}" value="#{pais.obj.nome}" /></td>
			</tr>
			<tr>
				<th class="required">Nacionalidade:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{pais.readOnly}" value="#{pais.obj.nacionalidade}"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{pais.confirmButton}"
						action="#{pais.cadastrar}" /> <h:commandButton value="Cancelar" onclick="#{confirm}"
						action="#{pais.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	
	<br />
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>