<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> > Habilitar Avaliação do Tutor</h2>

<div class="descricaoOperacao">
Escolha o componente para o qual você deseja habilitar ou desabilitar a avaliação dos tutores
e clique em <strong>Selecionar</strong>. Para voltar à seleção de cursos clique em <strong>Voltar</strong>,
para voltar ao menu de Ensino a Distância clique em <strong>Cancelar</strong>.
</div>

<h:form>

<t:dataTable var="item" value="#{ habilitarAvaliacao.semanasModel }" styleClass="listagem" rowClasses="linhaPar, linhaImpar">

	<f:facet name="caption"><h:outputText value="Componentes Curriculares do curso #{ habilitarAvaliacao.cursoSelecionado.nome }"/></f:facet>

	<t:column>
		<f:facet name="header"><h:outputText value="Componente"/></f:facet>
		<h:outputText value="#{ item.componente.codigoNome }"/>
	</t:column>

	<t:column>
		<h:commandButton value="Habilitar" action="#{ habilitarAvaliacao.habilitarAula }" rendered="#{ !item.habilitada }" style="width: 80px"/>
		<h:commandButton value="Desabilitar" action="#{ habilitarAvaliacao.desabilitarAula }" rendered="#{ item.habilitada }"/>
	</t:column>

</t:dataTable>

<br/>

<p align="center">
<h:commandButton value="<< Voltar" action="#{ habilitarAvaliacao.iniciar }"/>
<h:commandButton value="Cancelar" action="#{ habilitarAvaliacao.cancelar }"/>
</p>


</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>