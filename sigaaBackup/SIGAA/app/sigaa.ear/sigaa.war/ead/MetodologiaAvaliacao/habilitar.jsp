<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">
.coluna1 { width: 60px; text-align: right; }
.coluna2 { width: 120px; text-align: center; }
.coluna3 { width: 100px; text-align: center; }
table.listagem thead tr th { text-align: center; }
</style>

<f:view>

<h2><ufrn:subSistema /> > Habilitar Avaliação do curso <h:outputText value="#{ habilitarAvaliacao.cursoSelecionado.nome }"/></h2>

<div class="descricaoOperacao">
Selecione as semanas de aulas para as quais você deseja habilitar ou desabilitar a avaliação
pelo tutor. Para escolher um outro curso, clique em <strong>Voltar</strong>. Para voltar ao menu de Ensino a Distância
clique em <strong>Cancelar</strong>.
</div>

<h:form>

<t:dataTable var="semana" value="#{ habilitarAvaliacao.semanasModel }" styleClass="listagem" rowClasses="linhaPar, linhaImpar" columnClasses="coluna1, coluna2, coluna3" style="width: 290px">

	<f:facet name="caption"><h:outputText value="Semanas de Aulas"/></f:facet>

	<t:column>
		<f:facet name="header"><h:outputText value="Semana"/></f:facet>	
		<h:outputText value="#{ semana.semana }"/>
	</t:column>

	<t:column>
		<f:facet name="header"><h:outputText value="Estado Atual"/></f:facet>	
		<h:outputText value="#{ semana.estadoAtual }"/>
	</t:column>

	<t:column>
		<h:commandButton value="Habilitar" action="#{ habilitarAvaliacao.habilitarAula }" rendered="#{ !semana.habilitada }" style="width: 80px"/>
		<h:commandButton value="Desabilitar" action="#{ habilitarAvaliacao.desabilitarAula }" rendered="#{ semana.habilitada }"/>
	</t:column>

</t:dataTable>

<br/>

<p align="center">
<h:commandButton value="<< Voltar" action="#{ habilitarAvaliacao.iniciar }" rendered="#{ !habilitarAvaliacao.obj.umaProva }"/> 
<h:commandButton value="<< Voltar" action="#{ habilitarAvaliacao.telaComponente }" rendered="#{ habilitarAvaliacao.obj.umaProva }"/>
<h:commandButton value="Cancelar" action="#{ habilitarAvaliacao.cancelar }"/>
</p>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>