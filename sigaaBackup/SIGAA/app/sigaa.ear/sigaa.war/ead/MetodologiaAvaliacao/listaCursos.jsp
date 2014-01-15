<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> > Habilitar Avalia��o do Tutor</h2>

<div class="descricaoOperacao">
Escolha o curso para o qual voc� deseja habilitar ou desabilitar a avalia��o dos tutores
e clique em <strong>Selecionar</strong>. Para voltar ao menu de Ensino a Dist�ncia
clique em <strong>Cancelar</strong>.
</div>

<h:form>

<t:dataTable var="item" value="#{ habilitarAvaliacao.cursosModel }" styleClass="listagem" rowClasses="linhaPar, linhaImpar">

	<f:facet name="caption"><h:outputText value="Cursos de Ensino a Dist�ncia"/></f:facet>

	<t:column>
		<f:facet name="header"><h:outputText value="Curso"/></f:facet>
		<h:outputText value="#{ item.nome }"/>
	</t:column>

	<t:column>
		<h:commandButton value="Selecionar >>" action="#{ habilitarAvaliacao.selecionaCurso }"/>
	</t:column>

</t:dataTable>

<br/>

<p align="center">
<h:commandButton value="Cancelar" action="#{ habilitarAvaliacao.cancelar }"/>
</p>


</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>