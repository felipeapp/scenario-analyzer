<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Fator Gerador</h2>

<table class=formulario>
<h:form> <caption class="listagem">Cadastro de Fator Gerador</caption>	
<h:inputHidden value="#{fatorGerador.confirmButton}"/> 
<h:inputHidden value="#{fatorGerador.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td>
	<h:inputText value="#{fatorGerador.obj.descricao}" size="60" maxlength="255" readonly="#{fatorGerador.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{fatorGerador.confirmButton}" action="#{fatorGerador.cadastrar}" /> <h:commandButton value="Cancelar" action="#{fatorGerador.cancelar}" /></td>
</tr></tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
