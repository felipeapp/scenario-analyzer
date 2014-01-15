<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem .descricao { width: 90% }
</style>


<f:view>
	<h2> <ufrn:subSistema /> &gt; Áreas de Conhecimento em Ciências e Tecnologia </h2>
	
	<div class="descricaoOperacao">
		<p> <b>	Caro usuário, </b> </p>
		<p>
			Abaixo poderão ser cadastradas as áreas de atuação no Bacharelado em Ciências e Tecnologia 
			que ficarão disponíveis para vinculação aos planos de trabalho dos bolsistas REUNI.
		</p> 	
	</div>
	
	<h:form id="formularioFormaAtuacao">
		<h:inputHidden value="#{areaConhecimentoCienciasTecnologiaBean.obj.id}"/>
	
		<table class="formulario" style="width:75%;">
			<caption>Formulário de Cadastro de Áreas de Conhecimetno</caption>
			<tr>
				<th class="required" width="20%"> Denominação: </th>
				<td>  <h:inputText value="#{areaConhecimentoCienciasTecnologiaBean.obj.denominacao}" style="width: 95%"/> </td>
			</tr>
	
			<tfoot>
			<tr>
				<td colspan="2"> 
					<h:commandButton value="#{areaConhecimentoCienciasTecnologiaBean.confirmButton}" action="#{areaConhecimentoCienciasTecnologiaBean.cadastrar}"/> 
					<h:commandButton value="Cancelar" action="#{areaConhecimentoCienciasTecnologiaBean.cancelar}" immediate="true" onclick="#{confirm}" rendered="#{ areaConhecimentoCienciasTecnologiaBean.obj.id == 0 }"/>
					<h:commandButton value="Cancelar Alteração" action="#{areaConhecimentoCienciasTecnologiaBean.cancelarAlteracao}" immediate="true" onclick="#{confirm}" rendered="#{ areaConhecimentoCienciasTecnologiaBean.obj.id != 0 }"/>
				</td>
			</tr>
			</tfoot>		
		</table>
		<br/>
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
			<br><br>
		</center>
	
	</h:form>

	<h:form>	
	<a4j:outputPanel id="areasConhecimento" rendered="#{not empty areaConhecimentoCienciasTecnologiaBean.allAtivos}">
	<div class="infoAltRem" style="width:75%;">
        <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar área
        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover área
	</div>
	
	<t:dataTable value="#{areaConhecimentoCienciasTecnologiaBean.allAtivos}" var="_area" style="width:75%;" styleClass="listagem" 
		rowClasses="linhaPar, linhaImpar" columnClasses="descricao, opcao, opcao">
		<f:facet name="caption"> <h:outputText value="Áreas cadastradas"></h:outputText> </f:facet>
		<t:column>
			<f:facet name="header"><f:verbatim>Denominação</f:verbatim></f:facet>
			<h:outputText value="#{_area.denominacao}"/>
		</t:column>
		<t:column>
			<h:commandLink title="Alterar área" action="#{areaConhecimentoCienciasTecnologiaBean.atualizar}">
				<h:graphicImage url="/img/alterar.gif"/>
				<f:param name="id" value="#{_area.id}"/>
			</h:commandLink>
		</t:column>
		<t:column>
			<h:commandLink title="Excluir área" action="#{areaConhecimentoCienciasTecnologiaBean.inativar}" onclick="#{confirmDelete}">
				<h:graphicImage url="/img/delete.gif"/>
				<f:param name="id" value="#{_area.id}"/>
			</h:commandLink>
		</t:column>					
	</t:dataTable>

	</a4j:outputPanel>	
	</h:form>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	