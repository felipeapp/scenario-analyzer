<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem .descricao { width: 90% }
</style>


<f:view>
	<h2> <ufrn:subSistema /> &gt; �reas de Conhecimento em Ci�ncias e Tecnologia </h2>
	
	<div class="descricaoOperacao">
		<p> <b>	Caro usu�rio, </b> </p>
		<p>
			Abaixo poder�o ser cadastradas as �reas de atua��o no Bacharelado em Ci�ncias e Tecnologia 
			que ficar�o dispon�veis para vincula��o aos planos de trabalho dos bolsistas REUNI.
		</p> 	
	</div>
	
	<h:form id="formularioFormaAtuacao">
		<h:inputHidden value="#{areaConhecimentoCienciasTecnologiaBean.obj.id}"/>
	
		<table class="formulario" style="width:75%;">
			<caption>Formul�rio de Cadastro de �reas de Conhecimetno</caption>
			<tr>
				<th class="required" width="20%"> Denomina��o: </th>
				<td>  <h:inputText value="#{areaConhecimentoCienciasTecnologiaBean.obj.denominacao}" style="width: 95%"/> </td>
			</tr>
	
			<tfoot>
			<tr>
				<td colspan="2"> 
					<h:commandButton value="#{areaConhecimentoCienciasTecnologiaBean.confirmButton}" action="#{areaConhecimentoCienciasTecnologiaBean.cadastrar}"/> 
					<h:commandButton value="Cancelar" action="#{areaConhecimentoCienciasTecnologiaBean.cancelar}" immediate="true" onclick="#{confirm}" rendered="#{ areaConhecimentoCienciasTecnologiaBean.obj.id == 0 }"/>
					<h:commandButton value="Cancelar Altera��o" action="#{areaConhecimentoCienciasTecnologiaBean.cancelarAlteracao}" immediate="true" onclick="#{confirm}" rendered="#{ areaConhecimentoCienciasTecnologiaBean.obj.id != 0 }"/>
				</td>
			</tr>
			</tfoot>		
		</table>
		<br/>
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
			<br><br>
		</center>
	
	</h:form>

	<h:form>	
	<a4j:outputPanel id="areasConhecimento" rendered="#{not empty areaConhecimentoCienciasTecnologiaBean.allAtivos}">
	<div class="infoAltRem" style="width:75%;">
        <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar �rea
        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover �rea
	</div>
	
	<t:dataTable value="#{areaConhecimentoCienciasTecnologiaBean.allAtivos}" var="_area" style="width:75%;" styleClass="listagem" 
		rowClasses="linhaPar, linhaImpar" columnClasses="descricao, opcao, opcao">
		<f:facet name="caption"> <h:outputText value="�reas cadastradas"></h:outputText> </f:facet>
		<t:column>
			<f:facet name="header"><f:verbatim>Denomina��o</f:verbatim></f:facet>
			<h:outputText value="#{_area.denominacao}"/>
		</t:column>
		<t:column>
			<h:commandLink title="Alterar �rea" action="#{areaConhecimentoCienciasTecnologiaBean.atualizar}">
				<h:graphicImage url="/img/alterar.gif"/>
				<f:param name="id" value="#{_area.id}"/>
			</h:commandLink>
		</t:column>
		<t:column>
			<h:commandLink title="Excluir �rea" action="#{areaConhecimentoCienciasTecnologiaBean.inativar}" onclick="#{confirmDelete}">
				<h:graphicImage url="/img/delete.gif"/>
				<f:param name="id" value="#{_area.id}"/>
			</h:commandLink>
		</t:column>					
	</t:dataTable>

	</a4j:outputPanel>	
	</h:form>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	