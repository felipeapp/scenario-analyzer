<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem .descricao { width: 90% }
</style>

<a4j:keepAlive beanName="formaAtuacaoDocenciaAssistidaBean"/>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Formas de Atua��o de Bolsistas</h2>
	
	<div class="descricaoOperacao">
		<p> <b>	Caro usu�rio, </b> </p>
		<p>
			Abaixo poder�o ser cadastradas as formas de atua��o que poder�o ser especificadas em seu plano de trabalho.
		</p> 	
	</div>
	
	<h:form id="formularioFormaAtuacao">
		<h:inputHidden value="#{formaAtuacaoDocenciaAssistidaBean.obj.id}"/>
	
		<table class="formulario" style="width:75%;">
			<caption>Formul�rio de Cadastro de Formas de Atua��o</caption>
			<tr>
				<th class="required" width="20%"> Descri��o: </th>
				<td>  <h:inputText value="#{formaAtuacaoDocenciaAssistidaBean.obj.descricao}" maxlength="200" style="width: 95%" id="descricao"/> </td>
			</tr>
	
			<tfoot>
			<tr>
				<td colspan="2"> 
					<h:commandButton value="#{formaAtuacaoDocenciaAssistidaBean.confirmButton}" action="#{formaAtuacaoDocenciaAssistidaBean.cadastrar}" id="cadastrar"/> 
					<h:commandButton value="Cancelar" action="#{formaAtuacaoDocenciaAssistidaBean.cancelarAlteracao}" immediate="true" onclick="#{confirm}" id="cancelar"/>
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

	<h:form prependId="false">	
	<a4j:outputPanel id="formasAtuacao" rendered="#{not empty formaAtuacaoDocenciaAssistidaBean.allAtivos}">
	<div class="infoAltRem" style="width:75%;">
        <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar forma de atua��o
        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover forma de atua��o
	</div>
	
	<t:dataTable value="#{formaAtuacaoDocenciaAssistidaBean.allAtivos}" var="_forma" style="width:75%;" styleClass="listagem"  id="dataTable"
		rowClasses="linhaPar, linhaImpar" columnClasses="descricao, opcao, opcao">
		<f:facet name="caption"> <h:outputText value="Formas de atua��o cadastradas"></h:outputText> </f:facet>
		<t:column>
			<f:facet name="header"><f:verbatim>Descri��o</f:verbatim></f:facet>
			<h:outputText value="#{_forma.descricao}"/>
		</t:column>
		<t:column>
			<h:commandLink title="Alterar forma de atua��o" action="#{formaAtuacaoDocenciaAssistidaBean.atualizar}" id="alterar">
				<h:graphicImage url="/img/alterar.gif"/>
				<f:param name="id" value="#{_forma.id}"/>
			</h:commandLink>
		</t:column>
		<t:column>
			<h:commandLink title="Remover forma de atua��o" action="#{formaAtuacaoDocenciaAssistidaBean.inativar}" onclick="#{confirmDelete}" id="excluir">
				<h:graphicImage url="/img/delete.gif"/>
				<f:param name="id" value="#{_forma.id}"/>
			</h:commandLink>
		</t:column>					
	</t:dataTable>

	</a4j:outputPanel>	
	</h:form>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	