<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Dados da Presta��o de Servi�os</h2>
<br>

<table class=formulario width="95%">
<h:form> <caption class="listagem">Dados da Equipe da Presta��o de Servi�os</caption>

<h:inputHidden value="#{prestacaoServico.obj.id}"/>

<tfoot>
<tr> <td colspan=2>
	<h:commandButton value="<< Voltar" action="#{prestacaoServico.irTelaDescricao}" />	
	<h:commandButton value="Cancelar" action="#{prestacaoServico.cancelar}" />
	<h:commandButton value="Cadastrar" action="#{prestacaoServico.cadastrar}" />
</td> </tr>
</tfoot>

</h:form>
</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>