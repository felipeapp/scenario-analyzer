<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Necessidade Especial</h2>

	<center>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoNecessidadeEspecialMBean.listar}"/>
			</div>
			</h:form>
	</center>
	

	<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">${tipoNecessidadeEspecialMBean.confirmButton} Tipo de Necessidade Especial</caption>
			<h:inputHidden value="#{tipoNecessidadeEspecialMBean.confirmButton}" />
			<h:inputHidden value="#{tipoNecessidadeEspecialMBean.obj.id}" />
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoNecessidadeEspecialMBean.readOnly}"value="#{tipoNecessidadeEspecialMBean.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoNecessidadeEspecialMBean.confirmButton}"
						action="#{tipoNecessidadeEspecialMBean.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoNecessidadeEspecialMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br/><%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%><br/>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>