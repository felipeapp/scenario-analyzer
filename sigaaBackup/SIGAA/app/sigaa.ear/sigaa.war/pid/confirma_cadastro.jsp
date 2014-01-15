<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<jwr:style src="/css/ensino/pid.css" media="all"/>

<f:view>
	<a4j:keepAlive beanName="cargaHorariaPIDMBean"></a4j:keepAlive>
   	
	<h2><ufrn:subSistema /> > Plano Individual Docente > Submissão para Homologação </h2>
   	
	<h:form id="form">
	
		<c:set var="_pidBean" value="#{cargaHorariaPIDMBean}" />
		<%@include file="/pid/_painel_resumo.jsp"%>   	  
	   	  
		<div class="descricaoOperacao" style="width: 75%">
			<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em;">
				
				<h:selectBooleanCheckbox value="#{cargaHorariaPIDMBean.obj.concordanciaTermoEnvioPID}" id="concordancia"/> 
					
				<h:outputLabel for="concordancia">
				O solicitante declara formalmente que está de acordo com o Termo de
				Adesão e Compromisso da Plataforma SIGAA e que responde pela veracidade de todas as
				informações contidas no seu Plano Individual do Docente que será enviada para análise 
				pela chefia do departamento ou direção da unidade acadêmica especializada.
				</h:outputLabel>
			</p>
			<p style="text-align: right;">
				<em>(Declaração feita em observância aos artigos 297-299 do Código Penal Brasileiro)</em>
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
								<h:commandButton value="Confirmar submissão" action="#{cargaHorariaPIDMBean.enviarPIDParaChefiaDepartamento}" id="cadastrar"/>
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
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
			<br><br>
		</center>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>