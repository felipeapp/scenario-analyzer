<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<jwr:style src="/css/ensino/pid.css" media="all"/>

<f:view>
	<a4j:keepAlive beanName="cargaHorariaPIDMBean"></a4j:keepAlive>
   	
	<h2><ufrn:subSistema /> > Plano Individual Docente > Submiss�o para Homologa��o </h2>
   	
	<h:form id="form">
	
		<c:set var="_pidBean" value="#{cargaHorariaPIDMBean}" />
		<%@include file="/pid/_painel_resumo.jsp"%>   	  
	   	  
		<div class="descricaoOperacao" style="width: 75%">
			<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em;">
				
				<h:selectBooleanCheckbox value="#{cargaHorariaPIDMBean.obj.concordanciaTermoEnvioPID}" id="concordancia"/> 
					
				<h:outputLabel for="concordancia">
				O solicitante declara formalmente que est� de acordo com o Termo de
				Ades�o e Compromisso da Plataforma SIGAA e que responde pela veracidade de todas as
				informa��es contidas no seu Plano Individual do Docente que ser� enviada para an�lise 
				pela chefia do departamento ou dire��o da unidade acad�mica especializada.
				</h:outputLabel>
			</p>
			<p style="text-align: right;">
				<em>(Declara��o feita em observ�ncia aos artigos 297-299 do C�digo Penal Brasileiro)</em>
			</p>
		</div>
		
		<br>
		<c:set var="exibirApenasSenha" value="true" scope="request" />
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp" %>
			
		<a4j:outputPanel id="concordanciaTermo"> 
			<table class="formulario" width="100%" id="tabela">
				<tfoot>
					<tr>
							<td colspan="2" align="center">
								<h:commandButton value="Confirmar submiss�o" action="#{cargaHorariaPIDMBean.enviarPIDParaChefiaDepartamento}" id="cadastrar"/>
								<h:commandButton value="<< Voltar" action="#{cargaHorariaPIDMBean.voltarConfirmacao}" id="voltar" />
								<h:commandButton value="Cancelar" action="#{cargaHorariaPIDMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
							</td>
					</tr>
				</tfoot>
			</table>
		</a4j:outputPanel>
		
		<br />
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
			<br><br>
		</center>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>