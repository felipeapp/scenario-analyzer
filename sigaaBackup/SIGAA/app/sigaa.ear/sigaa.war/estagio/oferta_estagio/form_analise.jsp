<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="ofertaEstagioMBean" />
	<h2> <ufrn:subSistema /> &gt; An�lise da Oferta de Est�gio</h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usu�rio, </b>
		</p> 	
		<p>
			Voc� dever� informar a nova situa��o referente ao parecer da Oferta de Est�gio selecionada.
		</p>
	</div>
	
	<c:set var="oferta" value="#{ofertaEstagioMBean.obj}"/>
	<%@include file="include/_oferta.jsp"%>
	
	<br/>	
	
	<h:form>
		<table class="formulario" width="70%">
			<caption>Informe a Situa��o da An�lise</caption>		
			<tr>
				<th class="obrigatorio">Situa��o:</th>
				<td>
					<h:selectOneMenu id="situacao" value="#{ofertaEstagioMBean.obj.status.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ofertaEstagioMBean.statusCombo}"/>
					</h:selectOneMenu>				
				</td>			
			</tr>	
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btConfirmar" value="Confirmar" action="#{ofertaEstagioMBean.cadastrar}"/>
						<h:commandButton value="<< Voltar" action="#{ofertaEstagioMBean.iniciarConsulta}" id="btVoltar"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{ofertaEstagioMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>							
		</table>		
	</h:form>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	